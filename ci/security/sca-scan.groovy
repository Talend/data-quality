#!/usr/bin/groovy

def AWS_POD_LABEL = "TDQ-VERACODE-${UUID.randomUUID().toString()}"
def scanResultPath = 'scan-result.json'
def slackChannel = 'tdq_ci'


pipeline {
    agent {
        kubernetes {
            label AWS_POD_LABEL
            yamlFile 'ci/podTemplate.yml'
        }
    }

    triggers {
        cron(BRANCH_NAME == "master" ? "0 13 * * 0" : "")
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
        timeout(time: 90, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        stage('Build project') {
            steps {
                container('talend-jdk8-builder-base') {
                    configFileProvider([configFile(fileId: 'maven-settings-nexus-zl', variable: 'MAVEN_SETTINGS')]) {
                        sh 'java -version'
                        sh 'mvn --version'
                        sh 'mvn -U clean package -DskipTests -B --fail-at-end -s $MAVEN_SETTINGS'
                    }
                }
            }
        }

        stage('Scan 3rd parties vulnerabilities') {
            steps {
                container('talend-jdk8-builder-base') {
                    withCredentials([string(credentialsId: 'veracode-token', variable: 'SRCCLR_API_TOKEN')]) {
                        sh """#!/bin/bash

                    # In case of trouble use DEBUG=1 sh -s -- scan...
                      
                    curl -sSL https://download.sourceclear.com/ci.sh | sh -s -- scan --json  > ${scanResultPath}
                """
                    }
                }
            }
        }

        stage('Process scan result') {
            steps {
                echo 'Build simple report & send slack notification'
                container('talend-jdk8-builder-base') {
                    script {
                        readFile "${scanResultPath}"
                        SCAN_RESULT_URL = sh(
                                script: "jq --raw-output '.records[0].metadata.report' ${scanResultPath}",
                                returnStdout: true
                        ).trim()
                        echo "Scan result URL: ${SCAN_RESULT_URL}"
                    }
                }
            }
        }
    }

    post {
        success {
            script {
                slackSend (
                        color: '#82bd41',
                        channel: slackChannel,
                        message: "SUCCESSFUL: `${env.JOB_NAME.replaceAll('%2F', '/')}` #${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>) <${SCAN_RESULT_URL}|Full scan result>")
            }
        }
        failure {
            script {
                slackSend (
                        color: '#e96065',
                        channel: slackChannel,
                        message: "FAILED: `${env.JOB_NAME.replaceAll('%2F', '/')}` #${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>) <${SCAN_RESULT_URL}|Full scan result>")
            }
        }
    }
}