
#![alt text](https://www.talend.com/wp-content/uploads/2016/07/talend-logo.png "Talend")
# Data Quality Libraries

This repository contains the source files of Talend Data Quality libraries.

## Content structure
| _Project_                                                 | _Description_                                                        |
|:----------------------------------------------------------|----------------------------------------------------------------------|
| [dataquality-common](dataquality-common)                  | *Abstractions of data analysis, and low-level utilities such as East Asian text pattern recognition* |
| [dataquality-email](dataquality-email)                    | *Email validation library*        |
| [dataquality-libraries](dataquality-libraries)            | *Parent pom aggregating other library projects, devops tools*        |
| [dataquality-record-linkage](dataquality-record-linkage)  | *Record Matching algorithms, blocking key calculation and T-Swoosh* |
| [dataquality-sampling](dataquality-sampling)              | *Reservoir sampling, data masking, data duplication*                 |
| [dataquality-semantic-model](dataquality-semantic-model)  | *Definition of semantic category related objects*                      |
| [dataquality-semantic](dataquality-semantic)              | *API for semantic category analysis*                                 |
| [dataquality-standardization](dataquality-standardization)| *Standardization library based on Apache Lucene*                     |
| [dataquality-statistics](dataquality-statistics)          | *API for data analysis and statistics (require JDK1.8)*              |
| [dataquality-wordnet](dataquality-wordnet)                | *Content validation API based on WordNet dictionary*                 |
| [dataquality-text-japanese](dataquality-text-japanese)      | *API for japanese text analysis*                 |


## Product Download

Talend Open Studio for Data Quality can be download from the [Talend website](http://www.talend.com/download/talend-open-studio?qt-product_tos_download_new=2&utm_medium=communityext&utm_source=github&utm_campaign=tosdq).

## Build
- All project are maven based.
- The parent pom builds all the libraries.

## License

Copyright (c) 2006-2016 Talend

Licensed under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt)
