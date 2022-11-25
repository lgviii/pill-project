# pill-project

The project is structured with a parent pom.xml at the base and multiple modules inheriting from it, to ensure that dependencies and versions stay consistent.

## pill-matcher-app
This module is intended to be the actual pill-matcher web application.  It's currently empty except for the PillMatcherApplication and ServletInitializer classes which can be used to start the application, along with the Spring Initializer-generated mvnw files, etc.  It runs successfully on my local machine with the values specified in mysql-datasource.properties set to my local database.  This property file will need to be modified to point to whatever container/server DB is actually used for the application.

## pill-matcher-lib
This is the library module containing the database entities, repository classes, and services used to interact with the pill database.

Currently there are only methods for retrieving pills by shape and color, allowing search with either a single color or two colors.  (For simplicity, it doesn't currently support more than two colors, and there aren't any pills in the database that have more than two colors at the moment.)

## pill-db-fill
This is the module used to parse data from the C3PI XML files and fill a database with pill data.  This module shouldn't be used for the actual application.

## sql-scripts
This directory contains SQL scripts.

* **create_pilldb_mysql_full.sql** - This SQL script contains only creation SQL, and is meant for use with the pill-db-fill module.  It includes creation of the additional PillPhoto table not used by the pill-matcher application.

The other SQL scripts are meant for use in creating and filling the database to be used by the pill-matcher application, and should be executed in the following order to ensure foreign key consistency.
1) **pilldb_genericdrug.sql** - This SQL script creates and fills the GenericDrug table.
2) **pilldb_ndc.sql** - This SQL script creates and fills the Ndc table, which has a many-to-one relationship with GenericDrug.
2) **pilldb_pill.sql** - This SQL script creates and fills the Pill table, which has a many-to-one relationship with Ndc.
3) **pilldb_pillcolor.sql** - This SQL script creates and fills the PillColor table, which has a many-to-one relationship with Pill.

GenericDrug is separated into its own table since there are multiple pills from different labelers that contain the same drug.  The idea is that this could (potentially) be used as a reach goal to allow refining the list of possible pill matches by user input of the medications they take.

Ndc is separated into its own table from necessity, because an NDC may consist of multiple pills, each identified by the Part number within that NDC.
