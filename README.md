# pill-project

The project is structured with a parent pom.xml at the base and multiple modules inheriting from it, to ensure that dependencies and versions stay consistent.

## Modules

### pill-matcher-app
This module contains the actual pill-matcher web application, built using the Spring Boot framework.  This includes the web page, REST controllers, and functionality to contact the model prediction web services and use the results to generate matching pills. 

### pill-matcher-lib
This is the library module containing the database entities, repository classes, and services used to interact with the pill database.

Currently there are only methods for retrieving pills by shape and color, allowing search with either a single color or two colors.  (For simplicity, it doesn't currently support more than two colors, and there aren't any pills in the database that have more than two colors at the moment.)

### pill-db-fill
This is the module used to parse data from the C3PI XML files and fill a database with pill data.  This module shouldn't be used for the actual application.

## sql-scripts
This directory contains SQL scripts.  They're written for a MySQL database.

* **create_pilldb_mysql_full.sql** - This SQL script contains only creation SQL, and is meant for use with the pill-db-fill module.  It includes creation of the additional PillPhoto table not used by the pill-matcher application.

The other SQL scripts are meant for use in creating and filling the database to be used by the pill-matcher application, and should be executed in the following order to ensure foreign key consistency.
1) **pilldb_genericdrug.sql** - This SQL script creates and fills the GenericDrug table.
2) **pilldb_ndc.sql** - This SQL script creates and fills the Ndc table, which has a many-to-one relationship with GenericDrug.
2) **pilldb_pill.sql** - This SQL script creates and fills the Pill table, which has a many-to-one relationship with Ndc.
3) **pilldb_pillcolor.sql** - This SQL script creates and fills the PillColor table, which has a many-to-one relationship with Pill.

GenericDrug is separated into its own table since there are multiple pills from different labelers that contain the same drug.  The idea is that this could (potentially) be used as a reach goal to allow refining the list of possible pill matches by user input of the medications they take.

Ndc is separated into its own table from necessity, because an NDC may consist of multiple pills, each identified by the Part number within that NDC.

## SQL Commands
### To run SQL scripts on the CLI on Windows for app setup:

`mysql -h {hostname} -u username -p {databasename}`

i.e.
`mysql -h localhost -u pill -p pilldb`

database password can be found in the application.properties

i.e. `p1llZ`

See a list of the database:
`show databases`

Set a database for use:
`use pilldb`

Load a SQL file:
`source C:\dev\Classes\DGMD14\pill-project\sql-scripts\pilldb_genericdrug.sql`

## Run Web Application
To run the web application:

1. Build the MySQL pill database using the SQL scripts, and update the config file pill-matcher-app\src\main\resources\mysql-datasource.properties appropriately.
2. Update the `UploadController` static constant `UPLOAD_DIRECTORY` to a local directory that will be used by both this application and the Python web services application that this one uses for model predictions.  
3. Use Maven to build pill-matcher-parent (this will build all three modules).
4. Run the resulting pill-matcher-app jar in the target directory using `java -jar pill-matcher-app-(version).jar`

Using the current configuration, the application can be accessed at https://servername:8080/app.html

(Note that the project is currently setup for **Intellij**.)

## Generate Report
Two types of reports can be generated - a comparison report (pill picture and data base info printed to compare against the prediction list) or a prediction accuracy report.

### Comparison Report

Run junit test: `testRandomPillSet` in the `BatchAccuracyTesting` class

### Prediction Accuracy Report

Run junit test: `testAllPillsInCsv` in the `BatchAccuracyTesting` class

**Make sure** to update the `testFilePath` in the test file with the location of your test image set.