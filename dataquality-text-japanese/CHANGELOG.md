# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
N/A
### Changed
N/A
### Removed
N/A
### Deprecated
N/A
### Fixed
N/A
### Security
N/A

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
