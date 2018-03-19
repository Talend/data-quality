org.talend.dataquality.semantic
===================

API for semantic category analysis

Changelog
-------------

More information can be found [here](https://github.com/Talend/data-quality/blob/master/dataquality-semantic/changelog.txt).

Where can I get the latest release?
-----------------------------------
You can download latest stable binaries from our [Release Repository](https://artifacts-oss.talend.com/nexus/content/repositories/TalendOpenSourceRelease/org/talend/dataquality/dataquality-semantic).
or snapshot binaries from our [Snapshot Repository](https://artifacts-oss.talend.com/nexus/content/repositories/TalendOpenSourceSnapshot/org/talend/dataquality/dataquality-semantic).

Alternatively you can add the following repository into your pom.xml file:
```xml
<repositories>
  <repository>
    <id>TalendOpenSourceRelease</id>
    <url>https://artifacts-oss.talend.com/nexus/content/repositories/TalendOpenSourceRelease</url>
  </repository>
</repositories>
```

And include the following dependency:
```xml
<dependency>
  <groupId>org.talend.dataquality</groupId>
  <artifactId>dataquality-semantic</artifactId>
  <version>LATEST</version>
</dependency>
```

License
-------
Code is under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt).

Additional Resources
--------------------

+ [Talend Homepage](http://www.talend.com/)
+ [Talend Bugtracker (JIRA)](https://jira.talendforge.org/)

Lucene index tree
--------------------

```bash
.
├── shared
│   └── prod
│       ├── dictionary
│       │   ├── _0.cfe
│       │   ├── _0.cfs
│       │   └── _0.si
│       ├── keyword
│       │   ├── _0.cfe
│       │   ├── _0.cfs
│       │   └── _0.si
│       └── metadata
│           ├── _0.cfe
│           ├── _0.cfs
│           └── _0.si
└── <tenantId>
    ├── prod
    │   ├── dictionary
    │   │   ├── _0.cfe
    │   │   ├── _0.cfs
    │   │   └── _0.si
    │   └── metadata
    │       ├── _0.cfe
    │       ├── _0.cfs
    │       └── _0.si
    └── republish
        ├── dictionary
        │   ├── _0.cfe
        │   ├── _0.cfs
        │   └── _0.si
        └── metadata
            ├── _0.cfe
            ├── _0.cfs
            └── _0.si
```
The directory "shared" is always used in reading, never in writing.

We have as much "tenantId" directories as the number of tenants which modified the dictionary.

The directory "republish" is created while a republish process, then it will replace the "prod" directory and will be deleted.