# What is it ?

This module will generate the changelog of the version by connecting to jira.

# How to use it ?

Launch the following Maven command

	> mvn generate-sources -Dversion="<DQ_Lib_Version>" -Duser=<YOUR_JIRA_USERNAME> -Dpassword=<YOUR_JIRA_PASSWORD> -Dproject=TDQ -Dname="Data Quality".
This will generate a <DQ_Lib_Version>.adoc file, you have to copy/paste the content as the tag label in Github.

