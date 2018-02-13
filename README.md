# AutoPower

## What is this?
<b>AutoPower</b> is a project to automate the creation of [IBM Datapower Gateway](https://www-03.ibm.com/software/products/en/datapower-gateway) Multi-Protocol gateways.  
A [Multi-Protocol Gateway](https://www.ibm.com/support/knowledgecenter/SS9H2Y_6.0.1/com.ibm.dp.xg.doc/mpgw_wizard.html) is a middleware that exposes a backend (web service, database stored procedure, queues, etc.) through a frontend (tipically a RESTful or SOAP web service).  
For now Autopower supports DB stored procedure as backend.  

<b>TODO</b> Support other backends

## How to

### Requirements

* [java](https://java.com/en/)
* [gradle](https://gradle.org/)
* [ant](http://ant.apache.org/)
* [docker](https://www.docker.com/)

To run <b>AutoPower</b>, you need to follow the following steps:

* Clone this repository
* Follow this [instructions](docker/README.md)
* Finally, in the root of the project, run:

```
gradle clean test
```


## How it works?

<b>AutoPower</b> connects to a database using JDBC and extracts metadata of all stored procedures of one catalog and schema. Then, <b>AutoPower</b> create a template XML with the stored procedure data and inject in a folder for default of a Multi-Protocol Gateway. Finally, the folder generated is compresssed and are ready to import in DataPower Hardware.

### Detailed steps
<b>Autopower</b> generates one file: procedures.json which contains a list of all stored procedures. Each stored procedure has a name and parameters. Parameters are grouped as: In, Out, InOut, Result and Unknown.

```
[
    {
        "name": "dm_cryptographic_provider_algorithms;0",
        "Result": [
            {
                "columnName": "@TABLE_RETURN_VALUE",
                "dataType": null,
                "typeName": "table"
            }
        ],
        "In": [
            {
                "columnName": "@ProviderId",
                "dataType": "INTEGER",
                "typeName": "int"
            }
        ]
    },
...
```
To generate procedures.json file, <b>AutoPower</b> work on a file created for us :

* [typeNames.json](src/main/resources/typeNames.json). This was created starting a extension of IBM DataPower called [sql-execute](https://www.ibm.com/support/knowledgecenter/SS9H2Y_6.0.1/com.ibm.dp.xg.doc/sql-execute_element.html) for the mapping from SQL type names (according to IBM DataPower) to engine specific data types.
* [columnTypes.json](src/main/resources/columnTypes.json). This was created to map out the stored procedure with the values of default to replace their for understandable names.

This you can found in [MetaDBBuilder.groovy](src/main/groovy/MetaDBBuilder.groovy) file.

To generate a template XML file, <b>AutoPower</b> use [Hogan](https://github.com/plecong/Hogan.groovy) to inject stored procedures metadata in a DataPower object template. This you can found in [RenderBuilder.groovy](src/main/groovy/RenderBuilder.groovy) file.

## TODO
What is next? 

* Add support for Sybase
* Complementary README.md

