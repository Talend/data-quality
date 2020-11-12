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
### Changed
- chore([TDQ-17788](https://jira.talendforge.org/browse/TDQ-17788)): change RandomWrapper class to package protected
### Removed
- chore([TDQ-17788](https://jira.talendforge.org/browse/TDQ-17788)): remove unused AllDataqualitySamplingTests class

## [7.0.0]
- Move dataquality.datamasking package in EE repo
- Move semantic and smeantic-model modules to EE repo

## [6.0.0] - 2018-07-03
- [TDQ-14227](https://jira.talendforge.org/browse/TDQ-14227) Add setRandom method to avoid mask action make invalid
- [TDQ-14001](https://jira.talendforge.org/browse/TDQ-14001) improve pattern regex to valid asian languages
- [TDQ-14221](https://jira.talendforge.org/browse/TDQ-14221) update google libphonenumber and log to info
- [TDQ-14001](https://jira.talendforge.org/browse/TDQ-14001) check all Locales for Date discovery
- [TDQ-14996](https://jira.talendforge.org/browse/TDQ-14996) Make functionTest support to linux case
- [TDQ-15171](https://jira.talendforge.org/browse/TDQ-15171) fix build by ignoring a failing UT from [TDQ-14996](https://jira.talendforge.org/browse/TDQ-14996)
- [TDQ-14996](https://jira.talendforge.org/browse/TDQ-14996) Datamasking not work with the function "Replace by
- [TDQ-15013](https://jira.talendforge.org/browse/TDQ-15013) remove deprecated methods

## [5.0.2] - 2018-04-04
## [5.0.1] - 2018-03-28
## [5.0.0] - 2018-02-12
- [TDQ-14147](https://jira.talendforge.org/browse/TDQ-14147) data masking with dictionaries
- [TDQ-14536](https://jira.talendforge.org/browse/TDQ-14536) only mask valid data with DICT
- [TDQ-14668](https://jira.talendforge.org/browse/TDQ-14668) upgrade jackson library to 2.9.4

## [4.0.1] - 2017-12-08
- [TDQ-14481](https://jira.talendforge.org/browse/TDQ-14481) multi tenant index
- [TDQ-14367](https://jira.talendforge.org/browse/TDQ-14367) Support multi-tenant lucene index for on-premise
- TDQ 14314 tPattern masking big integer
- TDQ 11977 unique generic data masking

## [2.5.1] - 2017-09-11
## [2.5.0] - 2017-08-24
- [TDQ-13809](https://jira.talendforge.org/browse/TDQ-13809) change regex to valid emails with special characters
- [TDQ-13935](https://jira.talendforge.org/browse/TDQ-13935) sync 'DateRegexesGrouped.txt' in 'sampling' and 'statistics'

## [2.4.3] - 2017-06-09
## [2.4.2] - 2017-05-09
## [2.4.1] - 2017-05-02
## [2.4.0] - 2017-04-07
- [TDQ-13127](https://jira.talendforge.org/browse/TDQ-13127) code clean based on sonar tip

## [2.3.6] - 2016-12-09
## [2.3.5] - 2016-12-02
## [2.3.4] - 2016-10-20
- [TDQ-12269](https://jira.talendforge.org/browse/TDQ-12269) fix an NPE due to uninitialized Random generator in data masking functions

## [2.3.3] - 2016-09-28
- [TDQ-11865](https://jira.talendforge.org/browse/TDQ-11865) add keepEmpty option in the API
- [TDQ-12595](https://jira.talendforge.org/browse/TDQ-12595) Generate phone numbers which are unique and valid

## [2.3.2] - 2016-09-16
- [TDQ-12185](https://jira.talendforge.org/browse/TDQ-12185) & [TDQ-12364](https://jira.talendforge.org/browse/TDQ-12364) & [TDQ-12366](https://jira.talendforge.org/browse/TDQ-12366) &[TDQ-12367](https://jira.talendforge.org/browse/TDQ-12367) Mask email local part bug fixes
- [TDQ-12477](https://jira.talendforge.org/browse/TDQ-12477) Reservoir Sampling API on Spark DataFrame

## [2.3.1] - 2016-06-27 (with DQ library release 1.5.1)
- [TDQ-11437](https://jira.talendforge.org/browse/TDQ-11437) fix FR_PHONE generation function of data masking
- [TDQ-11293](https://jira.talendforge.org/browse/TDQ-11293) rename Function package to lowercase
- [TDQ-11437](https://jira.talendforge.org/browse/TDQ-11437) avoid generating 0 as street number
- [TDQ-12030](https://jira.talendforge.org/browse/TDQ-12030) allow the hyphen character in domain part of email regex
- [TDQ-12156](https://jira.talendforge.org/browse/TDQ-12156) In tDataMasking component, fix the "Replace by (consistent)  items from input list/file properties

## [2.3.0] - 2016-05-10
- rename artifact ID to dataquality-sampling
- [TDQ-11339](https://jira.talendforge.org/browse/TDQ-11339) fix a "String index out of range: -1" exception for datamasking
- [TDQ-11437](https://jira.talendforge.org/browse/TDQ-11437) support GENERATE_FROM_LIST_STRING function with fixed file	
- [TDQ-11437](https://jira.talendforge.org/browse/TDQ-11437) API of data masking actions for data-prep
- [TDQ-11437](https://jira.talendforge.org/browse/TDQ-11437) fix FR_PHONE generation issue

## [2.2.1] - 2016-05-04 (for studio 6.2.0)
- [TDQ-11339](https://jira.talendforge.org/browse/TDQ-11339) fix a "String index out of range: -1" exception for datamasking

## [2.2.0] - 2016-04-27
- [TDQ-11438](https://jira.talendforge.org/browse/TDQ-11438) implement fast reservoir sampling
- [TDQ-11491](https://jira.talendforge.org/browse/TDQ-11491) API for Reservoir Sampling component on Spark
- [TDQ-10917](https://jira.talendforge.org/browse/TDQ-10917) [TDQ-11108](https://jira.talendforge.org/browse/TDQ-11108) [TDQ-11448](https://jira.talendforge.org/browse/TDQ-11448) fix junits

## [2.1.5] - 2015-12-30
- move to data-quality repository, change parent pom

## [2.1.4] - 2015-11-26
- [TDQ-11187](https://jira.talendforge.org/browse/TDQ-11187) use BufferedReader instead of Scanner and remove parameter keyDelimiter from method KeysLoader.loadKeys

## [2.1.3] - 2015-11-19
- [TDQ-11292](https://jira.talendforge.org/browse/TDQ-11292) use index instead of header to retrieve sample data

## [2.1.2] - 2015-11-13 
- [TDQ-11187](https://jira.talendforge.org/browse/TDQ-11187) use line separator depending on system

## [2.1.1] - 2015-10-22 (for Studio 6.1.0)
- [TDQ-11108](https://jira.talendforge.org/browse/TDQ-11108) absolute paths issue in junits
- [TDQ-11109](https://jira.talendforge.org/browse/TDQ-11109) soundex replace junit failure
- [TDQ-11046](https://jira.talendforge.org/browse/TDQ-11046) problem when masking null values

## [2.1.0] - 2015-09-15 ([TDQ-10456](https://jira.talendforge.org/browse/TDQ-10456))
- Creating new GenerateSequence function
- Removing the logical implementation from the javajet

## [2.0.0] - 2015-09-10 ([TDQ-10888](https://jira.talendforge.org/browse/TDQ-10888))
- Code refactoring
- New system for artifact build

## [1.3.0] - 2015-08-24
- Adding HellWeek Suggestions

## [1.2.0] - 2015-06-25
- Implementing factory pattern

## [1.1.0] - 2015-06-12
- Adding new functions + Code cleansing

## [1.0.1] - 2015-04-03
- API for DataMasker
