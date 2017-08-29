// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.libraries.devops;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Application to bump versions automatically to facilitate DQ library releases.
 * 
 * @author sizhaoliu
 */
public class ReleaseVersionBumper {

    private String targetVersion = "1.5.9-SNAPSHOT";

    private static final String DATAQUALITY_PREFIX = "dataquality.";

    private static final String SNAPSHOT_VERSION_SUFFIX = "-SNAPSHOT";

    private static final String SNAPSHOT_STRING = ".snapshot.";

    private static final String RELEASED_STRING = ".released.";

    private static final String BUNDLE_VERSION_STRING = "Bundle-Version: ";

    private static final String MANIFEST_SNAPSHOT_SUFFIX = ".SNAPSHOT";

    private XPath xPath = XPathFactory.newInstance().newXPath();

    private Transformer xTransformer;

    private String microVersion;

    private ReleaseVersionBumper() throws TransformerConfigurationException, TransformerFactoryConfigurationError {
        xTransformer = TransformerFactory.newInstance().newTransformer();
        xTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        microVersion = targetVersion.substring(4);
    }

    private void bumpPomVersion() throws Exception {

        final String resourcePath = ReleaseVersionBumper.class.getResource(".").getFile();
        final String projectRoot = new File(resourcePath).getParentFile().getParentFile().getParentFile().getParentFile()
                .getParentFile().getParentFile().getParentFile().getPath() + File.separator;

        String parentPomPath = "./pom.xml";
        File inputFile = new File(projectRoot + parentPomPath);
        if (inputFile.exists()) {
            System.out.println("Updating: " + inputFile.getAbsolutePath());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile);

            // replace version value of this project
            Node parentVersion = (Node) xPath.evaluate("/project/version", doc, XPathConstants.NODE);
            parentVersion.setTextContent(targetVersion);

            // replace property value of this project
            NodeList propertyNodes = ((Node) xPath.evaluate("/project/properties", doc, XPathConstants.NODE)).getChildNodes();
            for (int idx = 0; idx < propertyNodes.getLength(); idx++) {
                Node node = propertyNodes.item(idx);
                String propertyName = node.getNodeName();
                String propertyValue = node.getTextContent();
                if (propertyName.startsWith(DATAQUALITY_PREFIX)) {
                    if (targetVersion.endsWith(SNAPSHOT_VERSION_SUFFIX) && propertyName.contains(SNAPSHOT_STRING)) {
                        node.setTextContent(propertyValue.substring(0, 4) + microVersion);
                    } else if (!targetVersion.endsWith(SNAPSHOT_VERSION_SUFFIX) && propertyName.contains(RELEASED_STRING)) {
                        node.setTextContent(propertyValue.substring(0, 4) + microVersion);
                    }
                }
            }
            // re-write pom.xml file
            xTransformer.transform(new DOMSource(doc), new StreamResult(inputFile));

            // update manifest of this project
            Path manifestPath = Paths.get(inputFile.getParentFile().getAbsolutePath(), "META-INF", "MANIFEST.MF");
            updateManifestVersion(manifestPath);

            // update modules
            NodeList moduleNodes = (NodeList) xPath.evaluate("/project/modules/module", doc, XPathConstants.NODESET);
            for (int idx = 0; idx < moduleNodes.getLength(); idx++) {
                String modulePath = moduleNodes.item(idx).getTextContent();
                updateChildModules(new File(projectRoot + modulePath + "/pom.xml"));
            }
        }
    }

    private void updateChildModules(File inputFile) throws Exception {
        if (inputFile.exists()) {
            System.out.println("Updating: " + inputFile.getAbsolutePath());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile);

            // replace parent version value
            Node parentVersion = (Node) xPath.evaluate("/project/parent/version", doc, XPathConstants.NODE);
            parentVersion.setTextContent(targetVersion);

            // replace project version value
            Node projectVersion = (Node) xPath.evaluate("/project/version", doc, XPathConstants.NODE);
            String projectVersionValue = projectVersion.getTextContent();
            if (targetVersion.endsWith(SNAPSHOT_VERSION_SUFFIX)) {
                projectVersion.setTextContent(projectVersionValue.replace(RELEASED_STRING, SNAPSHOT_STRING));
            } else {
                projectVersion.setTextContent(projectVersionValue.replace(SNAPSHOT_STRING, RELEASED_STRING));
            }

            // replace dependency version value
            NodeList nodes = (NodeList) xPath.evaluate("/project/dependencies/dependency/version", doc, XPathConstants.NODESET);
            for (int idx = 0; idx < nodes.getLength(); idx++) {
                Node node = nodes.item(idx);
                String depVersionvalue = node.getTextContent();
                if (depVersionvalue.startsWith("${dataquality.")) {
                    if (targetVersion.endsWith(SNAPSHOT_VERSION_SUFFIX)) {
                        node.setTextContent(depVersionvalue.replace(RELEASED_STRING, SNAPSHOT_STRING));
                    } else {
                        node.setTextContent(depVersionvalue.replace(SNAPSHOT_STRING, RELEASED_STRING));
                    }
                }
            }

            // re-write pom.xml file
            xTransformer.transform(new DOMSource(doc), new StreamResult(inputFile));

            // update manifest file of child project
            Path manifestPath = Paths.get(inputFile.getParentFile().getAbsolutePath(), "META-INF", "MANIFEST.MF");
            updateManifestVersion(manifestPath);
        }
    }

    private void updateManifestVersion(Path manifestPath) throws IOException {
        File manifestFile = manifestPath.toFile();
        if (manifestFile.exists()) {
            System.out.println("Updating: " + manifestFile.getAbsolutePath());
            FileInputStream fis = new FileInputStream(manifestFile);
            List<String> lines = IOUtils.readLines(fis);
            FileOutputStream fos = new FileOutputStream(manifestFile);

            for (String line : lines) {
                if (line.startsWith(BUNDLE_VERSION_STRING)) {
                    String currentVersion = line.substring(BUNDLE_VERSION_STRING.length()).replace(MANIFEST_SNAPSHOT_SUFFIX, "");
                    String modifiedVersion = currentVersion.substring(0, currentVersion.lastIndexOf('.')) + "." + microVersion;
                    if (targetVersion.endsWith(SNAPSHOT_VERSION_SUFFIX)) {
                        modifiedVersion += MANIFEST_SNAPSHOT_SUFFIX;
                    }
                    IOUtils.write(BUNDLE_VERSION_STRING + modifiedVersion.replace(SNAPSHOT_VERSION_SUFFIX, "") + "\n", fos);
                } else {
                    IOUtils.write(line + "\n", fos);
                }
            }
            fos.flush();
            fos.close();
            fis.close();
        }
    }

    public static void main(String[] args) throws Exception {
        ReleaseVersionBumper appli = new ReleaseVersionBumper();
        appli.bumpPomVersion();
    }

}
