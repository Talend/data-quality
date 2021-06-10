# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [8.0.13] - 2021-06-10

## [8.0.11] - 2021-04-15

## [8.0.10] - 2021-03-11
### Security
- fix([TDQ-19073](https://jira.talendforge.org/browse/TDQ-19073)): Fix JUnit issue

## [8.0.9] - 2021-02-07

## [8.0.8] - 2021-01-14

## [8.0.7] - 2020-12-10

## [8.0.6] - 2020-11-12

## [8.0.5] - 2020-09-17

## [8.0.4] - 2020-08-13

## [8.0.3] - 2020-07-16

## [8.0.2] - 2020-06-09

## [8.0.1] - 2020-05-21
### Security
- fix([TDQ-18383](https://jira.talendforge.org/browse/TDQ-18383)) Upgrade to log4j to log4j-core 2.13.2

## [8.0.0] - 2020-02-03
### Added
- chore([TDQ-17710](https://jira.talendforge.org/browse/TDQ-17710)): Adopt the "Keep a Changelog" format for changelogs

## [6.0.0] - 2018-07-03
- [TDQ-15285](https://jira.talendforge.org/browse/TDQ-15285) correct initialization-on-demand holder, the init of tokenizer should inside constructor
- [TDQ-14628](https://jira.talendforge.org/browse/TDQ-14628) japanese to arabic numbers transliteration
- [TDQ-15470](https://jira.talendforge.org/browse/TDQ-15470) do not allow bnd to generate package import to avoid breaking the build even we forget to export the new packages + rename the project name to org.talend... to align with other projects
- [TDQ-15470](https://jira.talendforge.org/browse/TDQ-15470) export package to fix compiler error
- [TDQ-14587](https://jira.talendforge.org/browse/TDQ-14587) api jp transliteration
- [TDQ-15345](https://jira.talendforge.org/browse/TDQ-15345) rename dq text japanese
