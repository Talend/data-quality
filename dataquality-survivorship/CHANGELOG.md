# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [8.0.3] - 2020-07-16
### Fixed
- fix([TDQ-18567](https://jira.talendforge.org/browse/TDQ-18567)): Clean 'orderMap' in ChainNodeMap for a new dataset

## [8.0.2] - 2020-06-09

## [8.0.1] - 2020-05-21
### Security
- fix([TDQ-18383](https://jira.talendforge.org/browse/TDQ-18383)) Upgrade to log4j to log4j-core 2.13.2

## [8.0.0] - 2020-02-03
### Added
- chore([TDQ-17710](https://jira.talendforge.org/browse/TDQ-17710)): Adopt the "Keep a Changelog" format for changelogs
### Removed
- chore([TDQ-17788](https://jira.talendforge.org/browse/TDQ-17788)): remove deprecated class MatchDictionaryService

## [6.2.0] - 2019-01-02
- [TDQ-16242](https://jira.talendforge.org/browse/TDQ-16242) All the rules of Confilt Resolution can not be executed if disable first one rule
    
## [6.0.0] - 2018-07-03
## [5.0.2] - 2018-04-04
- [TDQ-14225](https://jira.talendforge.org/browse/TDQ-14225) output correct conflicting column
  
## [5.0.1] - 2018-03-28
- [TDQ-14176](https://jira.talendforge.org/browse/TDQ-14176) execute rules by the order defined in the UI for tRuleSurvivorship  
    
## [5.0.0] - 2018-02-12
## [4.0.1] - 2017-12-08
- [TDQ-14481](https://jira.talendforge.org/browse/TDQ-14481) multi tenant index
- [TDQ-14308](https://jira.talendforge.org/browse/TDQ-14308) tRuleSurvivorship fails on DataProc (REST APIs) cluster

## [2.1.1] - 2017-09-11
## [2.1.0] - 2017-08-24
- [TDQ-13981](https://jira.talendforge.org/browse/TDQ-13981) when tRuleSurvivorship input data has two groups can not use "fill empty by" function
- [TDQ-13983](https://jira.talendforge.org/browse/TDQ-13983) "Conflict" column doesn't show conflict columnName when rules doesn't resolve the conflict
- [TDQ-13994](https://jira.talendforge.org/browse/TDQ-13994) fix NPE with "Survivor As" function
- [TDQ-13984](https://jira.talendforge.org/browse/TDQ-13984) "Remove Duplicate" can not run with combination of certain rules, the Conflict value show on a wrong row

## [2.0.3] - 2017-06-09
- [TDQ-13798](https://jira.talendforge.org/browse/TDQ-13798) tRuleSurvivorship improvements

## [2.0.2] - 2017-05-09
- [TDQ-13653](https://jira.talendforge.org/browse/TDQ-13653) fixed the issue which result in wrong default survived value

## [2.0.1] - 2017-05-02
## [2.0.0] - 2017-04-07
- [TDQ-12855](https://jira.talendforge.org/browse/TDQ-12855) move to data-quality repo

## [0.9.9] - 2011-12-2
- [TDQ-4092](https://jira.talendforge.org/browse/TDQ-4092) job export issue fixed
- path auto-correction for case-sensitive operation systems.

## [0.9.8] - 2011-11-21
- [TDQ-3986](https://jira.talendforge.org/browse/TDQ-3986) fixed by setting "mvel2.disable.jit" argument
- removed sysout in studio console

## [0.9.7] - 2011-11-17
- Checked "Ignore blanks" option
- Changed "Operation" label to "Value"

## [0.9.6] - 2011-11-15
- [TDQ-3972](https://jira.talendforge.org/browse/TDQ-3972) fixed
- [TDQ-3973](https://jira.talendforge.org/browse/TDQ-3973) rename recNum to TALEND_INTERNAL_ID
- code cleansing

## [0.9.5] - 2011-11-08
- Added Most Complete rule

## [0.9.4] - 2011-10-18
- Reordered rule table column
- Added disativations of parameters in rule table

## [0.9.3] - 2011-10-14
- resolved repository node duplication
- added org.drools.eclipse in survivorship-feature to let tdqte contain it (the plugin is contained in tispe before)
- complete rule code generation (MC, MT)
- equal results are not considered as conflict now
- corrected initialization of rule count expectation

## [0.9.2] - 2011-10-07
- added generation button to tRuleSurvivorship component
- minor modifications in library to adapt rule generation action

## [0.9.1] - 2011-09-26
- resolved a knowledge base initialization problem 
- updated sample rules

## [0.9.0] - 2011-09-20
- first release of survivorship library, this commit contains 4 projects.
