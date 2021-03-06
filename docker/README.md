# Datapower and SQL Server running in Docker containers

## Why I need to do this?
* When you start up the original image of datapower with SQL Server container, doesn't work because of defaults; datapower runs the container with non-root user, so in consecuense, SQL Server container and Datapower can't see each other containers.
* When you start up the containers, the web-mgmt is disabled. In consecuense, you can't open the website of Datapower to start using it.

## What is this?
"How-to" instructions to build Datapower development environment counting on underlying database to be called.

### Project Content
This project contain the following:

* <b>DataPower</b> Folder with 'config' and 'local' sub folders. The 'config' folder will contain every configuration of Datapower hardware, such as Gateway Services generated, Web Service Proxy generated, etc. The 'local' folder will contain everyfiles updated or importated to datapower.
* <b>Dockerfile</b> with the features necessaries according the specific objetives. This will download (if you don't have) [SQL Server](https://hub.docker.com/r/microsoft/mssql-server-linux/) and [Data Power](https://hub.docker.com/r/ibmcom/datapower/).
* <b>Docker-compose.yml</b> file to configure the connection among two containers. You can feel free to make changes. It depend of your needs.

## How I do this?
* Create a new image of datapower with <b>Dockerfile</b> to:
	* Run as root
	* Enable web-mgmt
* Create a <b>docker-compose</b> file to connect Datapower with SQL Server
* Lift up together the containers

Connect two containers starting from images downloaded from [Docker Hub](https://hub.docker.com/)



### Detailed Steps
* Enter in the path 'docker' of the directory clonated and write <b>docker build . -t autopower/datapower</b>. With this, you will build a new image of ibm-datapower named 'autopower/datapower' with the issures solved mentioned in 'Why I need to do this?'.
* Once created our own image of datapower with <b>Dockerfile</b>, you can continue the following [steps](../README.md)
