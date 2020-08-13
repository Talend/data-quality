# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [8.0.4] - 2020-08-13

## [8.0.3] - 2020-07-16

## [8.0.2] - 2020-06-09
### Fixed
- fix([TDQ-18347](https://jira.talendforge.org/browse/TDQ-18347)) Modify the concatenate function when matching: only use the original values to match, but not the concatenated value.

## [8.0.1] - 2020-05-21
### Security
- fix([TDQ-18383](https://jira.talendforge.org/browse/TDQ-18383)) Upgrade to log4j to log4j-core 2.13.2

## [8.0.0] - 2020-02-03
### Added
- chore([TDQ-17710](https://jira.talendforge.org/browse/TDQ-17710)): Adopt the "Keep a Changelog" format for changelogs
### Removed
- chore([TDQ-17788](https://jira.talendforge.org/browse/TDQ-17788)): remove unused AllRecordLinkageTests
### Fixed
- fix([TDQ-17709](https://jira.talendforge.org/browse/TDQ-17709)): fix precision issues to ensure that two records match if the score is greater than or equal to the threshold
- fix([TDQ-17851](https://jira.talendforge.org/browse/TDQ-17851)): improve the performance and support surrogate pair

## [6.2.0] - 2019-01-02
- [TDQ-16256](https://jira.talendforge.org/browse/TDQ-16256) t-Swoosh: children attributes are not matched

## [6.0.0] - 2018-07-03
- [TDQ-15012](https://jira.talendforge.org/browse/TDQ-15012) add dq unit tests
- [TDQ-14598](https://jira.talendforge.org/browse/TDQ-14598) Remove the "COMPUTE_GRP_QUALITY" in tmatchgroup
- [TDQ-15013](https://jira.talendforge.org/browse/TDQ-15013) remove deprecated methods
- [TDQ-15079](https://jira.talendforge.org/browse/TDQ-15079) Support surrogate pair characters
- [TDQ-14633](https://jira.talendforge.org/browse/TDQ-14633) add Reference column support

## [5.0.2] - 2018-04-04

## [5.0.1] - 2018-03-28
- [TDQ-14964](https://jira.talendforge.org/browse/TDQ-14964) check null when compare date

## [5.0.0] - 2018-02-12
- [TDQ-14276](https://jira.talendforge.org/browse/TDQ-14276) support "most recent" and "most ancient" in match analysis
- [TDQ-14218](https://jira.talendforge.org/browse/TDQ-14218) "shortest" function issue with NULL in data

## [4.0.2] - 2018-01-15
- [TDQ-14276](https://jira.talendforge.org/browse/TDQ-14276) support "most recent" and "most ancient" in matchAnalysis and tMatchGroup

## [4.0.1] - 2017-12-08
- [TDQ-14481](https://jira.talendforge.org/browse/TDQ-14481) multi tenant index
- [TDQ-14296](https://jira.talendforge.org/browse/TDQ-14296) fixed get error GID issue for tmatch group
- TDQ 14137 add support for survivorship for columns
- [TDQ-14229](https://jira.talendforge.org/browse/TDQ-14229) tMatchGroup-tswoosh multiple pass

## [3.4.1] - 2017-09-12
- [TDQ-14134](https://jira.talendforge.org/browse/TDQ-14134) 
- TDQ-14136
- TDQ-14136
- Add support for survivorship rules for columns 
  
## [3.4.0] - 2017-08-24
## [3.3.3] - 2017-06-09
## [3.3.2] - 2017-05-09

## [3.3.1] - 2017-05-02
- [TDQ-13490](https://jira.talendforge.org/browse/TDQ-13490) avoid to output duplicate none-master records when swoosh with store on disk

## [3.3.0] - 2017-04-07
- [TDQ-13511](https://jira.talendforge.org/browse/TDQ-13511) refactoring inner class TSwooshGrouping.GroupingCallBack make it become a Outer Class
- TMDM-10649 : Adapt to CombinedRecordMatcher
- [TDQ-13225](https://jira.talendforge.org/browse/TDQ-13225) Swoosh with the option "store on disk"
- bug fixed [TDQ-13053](https://jira.talendforge.org/browse/TDQ-13053) SurvivorShip rules impact the grouping of swoosh and fixed dailog can not be open issue when multipass
- [TDQ-13120](https://jira.talendforge.org/browse/TDQ-13120) wrong GID in multipass swoosh--revert [TDQ-12851](https://jira.talendforge.org/browse/TDQ-12851)
- (origin/[TDQ-13131](https://jira.talendforge.org/browse/TDQ-13131)_unique_default_survivorship_function) bug fixed [TDQ-13131](https://jira.talendforge.org/browse/TDQ-13131) use the "most common" function as a default when the column is not selected. 
- bug fixed  [TDQ-13060](https://jira.talendforge.org/browse/TDQ-13060) t-swoosh tMatchGroup use error default survivship paramter
- buf fixed [TDQ-13050](https://jira.talendforge.org/browse/TDQ-13050) resotre log information on the soundex matcher
- [TDQ-12976](https://jira.talendforge.org/browse/TDQ-12976) when has two rule tabs, the matching distance can not get correct result(has some redundant value) 
- bug fixed [TDQ-13025](https://jira.talendforge.org/browse/TDQ-13025) make sure correctly GROUP_QUALITY when has two rule tab andd add junit test
- bug fixed [TDQ-12919](https://jira.talendforge.org/browse/TDQ-12919) fix a special case for 0.8333333334 and add new junit for it

## [3.2.6] - 2016-12-09
- T-Swoosh bug fixes

## [3.2.5] - 2016-12-02
- T-Swoosh improvements
- Tokenization improvements

## [3.2.4] - 2016-10-20
- [TDQ-12693](https://jira.talendforge.org/browse/TDQ-12693) fix the wrong function name in the MatchRule/match analysis function list
- TDQ_12703 LCS algorithm is not expected to appear in match analysis

## [3.2.3] - 2016-09-28
- [TDQ-12057](https://jira.talendforge.org/browse/TDQ-12057) Multipass matching - add option to compare to original records as well as survived master

## [3.2.2] - 2016-09-16
- [TDQ-12121](https://jira.talendforge.org/browse/TDQ-12121) Distance with tokenization

## [3.2.1] - 2016-06-27 (with DQ library release 1.5.1)
- [TDQ-12038](https://jira.talendforge.org/browse/TDQ-12038) rename datascience.common package to dataquality.common
- [TDQ-12031](https://jira.talendforge.org/browse/TDQ-12031) improve speed of q-Gram metric

## [3.2.0] - 2016-05-10
- rename artifact ID to dataquality-record-linkage

## [3.1.2] - 2016-05-04 (for Studio 6.2.0)
- [TDQ-11779](https://jira.talendforge.org/browse/TDQ-11779) fixed t-swoosh algorithm in the tMatchgroup components with multi-pass mode can not get correctly group quality value
- [TDQ-11949](https://jira.talendforge.org/browse/TDQ-11949) for the match rule which use the custom type algorithm, we will use the threshold and weight from UI; and deprecated IAttributeMatcher.isDummyMatcher

## [3.1.1] - 2016-04-27
- [TDQ-11666](https://jira.talendforge.org/browse/TDQ-11666) Integrate the t-swoosh algorithm in the tMatchgroup components (step 3),extract a sub class for : AnalysisSwooshMatchRecordGrouping, to used by the component
- [TDQ-11536](https://jira.talendforge.org/browse/TDQ-11536) move TalendURLClassLoader class from record-linkage library to dataquality.common
- [TDQ-11779](https://jira.talendforge.org/browse/TDQ-11779) fixed t-swoosh algorithm in the tMatchgroup components with mutipass mode can not get correctly group quality value
- [TDQ-11949](https://jira.talendforge.org/browse/TDQ-11949) for the match rule which use the custom type algorithm, we will use the threshold and weight from UI and add test; and deprecated IAttributeMatcher.isDummyMatcher
- [TDQ-11599](https://jira.talendforge.org/browse/TDQ-11599) return null if the number is null, then the smallest will be 2 in (2,5,null)

## [3.1.0] - 2016-02-16
- [TDQ-11599](https://jira.talendforge.org/browse/TDQ-11599) add catch NumberFormatException, return 0 when catch exception.
- [TDQ-11627](https://jira.talendforge.org/browse/TDQ-11627) change the field in AbstractRecordGrouping.java:RecordMatcherType matchAlgo, from private to protected
- [TDQ-11668](https://jira.talendforge.org/browse/TDQ-11668) fix the multipass of tswoosh
- [TDQ-11496](https://jira.talendforge.org/browse/TDQ-11496) add T-Swoosh support to tMatchGroup component

## [3.0.3] - 2016-01-02
- move to data-quality repository, change parent pom
- [TDQ-11425](https://jira.talendforge.org/browse/TDQ-11425) Add catch SQLException, continue to do next when execute "next()" and get SQLException

## [3.0.2] - 2015-12-01 (for Studio 6.1.1)
- Update dependency version of org.talend.utils to 6.1.1

## [3.0.1] - 2015-10-22 (for Studio 6.1.0)
- Update dependency version of org.talend.utils to 6.1.0

## [3.0.0] 
- Code refactoring
- New system for artifact build

## [2.0.2]
- when compute the weight and it throw an Exception,catch the Excetpion and return 0.([TDQ-10366](https://jira.talendforge.org/browse/TDQ-10366))
## [2.0.1]
- Allow to specify classloader to be used for loading custom matcher class on the match analysis side.([TDQ-8027](https://jira.talendforge.org/browse/TDQ-8027))
## [2.0.0]
- Windows key algorithms integrated into record linkage library
## [1.0.1]
- the record matcher threshold can be different for each matcher of the combined matcher + attribute name stored in the attribute matcher for information.
## [1.0.0]
- added the possibility to combine several matchers + handle null options
## [0.9.7]
- fix bug with Jaro/Jaro-Winkler when comparing 2 blank fields. 
- fix bug with DoubleMataphone when encoded string contains less than 2 chars.
## [0.9.6]
- fix bug with Levenshtein distance computation
## [0.9.5]
- added the ability to create a custom AttributeMatcher
