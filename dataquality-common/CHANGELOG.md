# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
N/A
### Changed
- chore(TPRUN-514): Refactor AvroUtils for TPRUN-14
### Removed
N/A
### Deprecated
N/A
### Fixed
N/A
### Security
- fix(TDQ-19066): Fix jackson-databind issues
- fix(TDQ-19073): Fix JUnit issue
- fix(TDQ-19045): Update Jackson Databind to 2.11.4
- fix(TDQ-19124): Upgrade Daikon to 5.3.0

## [8.3.1] - 2020-11-18
### Security
- fix([TDQ-17694](https://jira.talendforge.org/browse/TDQ-17694)): Fix Jackson Data Mapper Issues
- fix([TDQ-18385](https://jira.talendforge.org/browse/TDQ-18385)): Fix Apache Common Codec issues

## [8.3.0] - 2020-07-16
### Added
- feat([TDQ-18422](https://jira.talendforge.org/browse/TDQ-18422)): Avro analyzers management

## [8.2.0] - 2020-06-18

## [8.1.0] - 2020-03-13
### Added
- feat([TDQ-18062](https://jira.talendforge.org/browse/TDQ-18062)): Add StringChecker, StringHandler, TokenizedString and Acronym utility classes in new "character" package

## [8.0.0] - 2020-02-03
### Added
- chore([TDQ-17710](https://jira.talendforge.org/browse/TDQ-17710)): Adopt the "Keep a Changelog" format for changelogs
### Changed
- chore([TDQ-17788](https://jira.talendforge.org/browse/TDQ-17788)): cancel the deprecation of setStoreInvalidValues in Quality Analyzers
### Removed
- chore([TDQ-17788](https://jira.talendforge.org/browse/TDQ-17788)): remove deprecated class HiraganaSmall
- chore([TDQ-17788](https://jira.talendforge.org/browse/TDQ-17788)): remove unused Tests class
### Security
- chore([TDQ-17923](https://jira.talendforge.org/browse/TDQ-17923)): Rely on daikon's jackson version (currently 2.10.1)

## [6.0.0] - 2018-07-03
- [TDQ-15013](https://jira.talendforge.org/browse/TDQ-15013) remove deprecated methods

## [5.0.2] - 2018-04-04
## [5.0.1] - 2018-03-28
## [5.0.0] - 2018-02-12

## [4.0.1] - 2017-12-08
- [TDQ-14481](https://jira.talendforge.org/browse/TDQ-14481) multi tenant index

## [1.7.1] - 2017-09-11
## [1.7.0] - 2017-08-24
## [1.6.3] - 2017-06-09
## [1.6.2] - 2017-05-09
## [1.6.1] - 2017-05-02

## [1.6.0] - 2017-04-07
- [TDQ-13127](https://jira.talendforge.org/browse/TDQ-13127) code cleansing based on sonar tip

## [1.5.6] - 2016-12-09
## [1.5.5] - 2016-12-02
## [1.5.4] - 2016-10-20
## [1.5.3] - 2016-09-28
## [1.5.2] - 2016-09-16

## [1.5.1] - 2016-06-27
- [TDQ-12038](https://jira.talendforge.org/browse/TDQ-12038) rename datascience.common package to dataquality.common

## [1.5.0] - 2016-05-10
- rename artifact ID to dataquality-common

## [1.4.4] - 2016-04-27 (for Studio 6.2.0)
## [1.4.3] - 2016-03-25
## [1.4.2] - 2016-01-26

## [1.4.1] - 2015-12-30
- move to data-quality repository, change parent pom

## [1.4.0] - 2015-12-17
- refactor date time pattern analyzers
- use generated pattern format list with regexes instead of the previous one
- add some additional common patterns

## [1.3.4] - 2015-12-10
- bugfix for invalid custom date patterns

## [1.3.3] - 2015-11-4
- change parent pom
- Make it possible to add customized date and time pattern		
- Specify java compilation version 1.7 in pom.xml 
- Remove the singleton of DateTimeManager, and rename it to SystemDatetimeManager
- Add setParameter , addParameters and related remove methods.

## [1.3.2] - 2015-10-29
- [TDQ-10903](https://jira.talendforge.org/browse/TDQ-10903) optimize dictionaries
- adjust OPEN/CLOSE type for some indexes

## [1.3.1] - 2015-10-22 (for Studio 6.1.0)
- [TDQ-10413](https://jira.talendforge.org/browse/TDQ-10413) compute list of invalid values according to semantic type
- [TDQ-10981](https://jira.talendforge.org/browse/TDQ-10981) concurrent analyzer
- [TDQ-10988](https://jira.talendforge.org/browse/TDQ-10988) latin1 supplement support in pattern statistics
