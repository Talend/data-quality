
#![alt text](https://www.talend.com/wp-content/uploads/talend_logo_.svg "Talend")
# Data Quality Libraries

This repository contains the source files of Talend Data Quality libraries.

## Content structure
| _Project_                                                 | _Description_                                                        |
|:----------------------------------------------------------|----------------------------------------------------------------------|
| [dataquality-common](dataquality-common)                  | *Abstractions of data analysis, and low-level utilities such as East Asian text pattern recognition* |
| [dataquality-converter](dataquality-converter)            | *Conversion tools for datetime, distance, japanese characters, etc.* |
| [dataquality-email](dataquality-email)                    | *Email validation library*                                           |
| [dataquality-libraries](dataquality-libraries)            | *Parent pom aggregating other library projects*                      |
| [dataquality-phone](dataquality-phone)                    | *Phone number validation and conversion tools*                       |
| [dataquality-record-linkage](dataquality-record-linkage)  | *Record Matching algorithms, blocking key calculation and T-Swoosh*  |
| [dataquality-sampling](dataquality-sampling)              | *Reservoir sampling, data duplication*                               |
| [dataquality-standardization](dataquality-standardization)| *Standardization library based on Apache Lucene*                     |
| [dataquality-statistics](dataquality-statistics)          | *API for data analysis and statistics*                               |
| [dataquality-survivorship](dataquality-survivorship)      | *Data survivorship library based on Drools*                          |
| [dataquality-text-japanese](dataquality-text-japanese)    | *API for japanese text analysis*                                     |
| [dataquality-wordnet](dataquality-wordnet)                | *Content validation API based on WordNet dictionary*                 |


## Product Download

Talend Open Studio for Data Quality can be download from the [Talend website](http://www.talend.com/download/talend-open-studio?qt-product_tos_download_new=2&utm_medium=communityext&utm_source=github&utm_campaign=tosdq).

## Build
- All project are maven based.
- The parent pom builds all the libraries.

## License

Copyright (c) 2006-2020 Talend

Licensed under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt)
