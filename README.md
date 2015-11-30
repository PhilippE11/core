# Core
This folder contains the core Java and Maven based implementation of OpenInfRA. The project page can be found [here](http://www.b-tu.de/openinfra/).

# Installation

To run OpenInfRA different prerequisites must be complied. At this point we will give a broad overview about the necessary components and their configuration.
OpenInfRA consists of the following parts:

## Application
The application is written in Java and must be compiled with Java 7. It must be packed into a _war_ file and run on a server. Currently, the application is optimezed for Apache Tomcat. There are only a few handles necessary to configure the application. The main [configuration file](openinfra_core/src/main/resources/de/btu/openinfra/backend/properties/OpenInfRA.properties) must be adapted to the current needs. The different configuration parameters are commented and need no further explanations. The prime configurations are the database connection and file path properties. These must be set correctly in order to run the application.

The OpenInfRA application is based on the following software stack:
- JAX-RS: [Jersey](https://jersey.java.net/)
- Security: [Apache Shiro](http://shiro.apache.org/)
- JPA: [EclipseLink](http://www.eclipse.org/eclipselink/)
- Database: [PostgreSQL](http://www.postgresql.org/) + [PostGis](http://postgis.net/)

![OpenInfRA Softwarestack](https://github.com/OpenInfRA/core/blob/master/img/sw-stack.JPG "OpenInfRA Softwarestack")

## Database
The [database](https://github.com/OpenInfRA/database) is necessary to provide a data storage for OpenInfRA. Further instructions can be found in the appropriated repositoriy.

## Solr
To make use of Solr in OpenInfRA two prerequisites must be complied. First the [server](https://github.com/OpenInfRA/solr_server) itself must be installed. Second the [core definition](https://github.com/OpenInfRA/solr_core) must be installed in the specified file path. Further instructions can be found in the appropriated repositories.

## WebGis/GeoServer
A detailed description is provided in the specific folder: [GXC](https://github.com/OpenInfRA/GXC).

## ImageMagick
OpenInfRA provides a file upload. This upload requires [ImageMagick](http://www.imagemagick.org) to generate and produce different image representations.

- PDF conversion requires [ghostscript](http://www.ghostscript.com/)
- Conversion of raw file formats (such as DNG) require [UFRaw](http://ufraw.sourceforge.net/) under Linux-based systems.

# Some Internals
This section shows some starting points and describes a few details. _Project_ and _TopicCharacteristic_ are used as running examples. As the name states, 'Project' refers to an OpenInfRA project. A 'TopicCharacteristic' is an abstract container which groups a set of objects by the description and consolidation of specific attributes. An object is called _TopicInstance_ and it defines attribute values consolidated by a 'TopicCharacteristic'. This leads to the following correlation: a set of TopicInstances are type of a specific TopicCharacteristic.

## Model
The following picture shows the 'Project' and the 'TopicCharacteristic' as _model_ _objects_. 'Model objects' refer to the persistence layer.

![OpenInfRA Model](https://github.com/OpenInfRA/core/blob/master/img/model.png "OpenInfRA Model")

## POJO
The following picture shows the 'Project' and the 'TopicCharacteristic' as _POJO_ _objects_. 'POJO objects' are data containers. These containers are used to transfer data from the application core to the REST API.

![OpenInfRA POJOI](https://github.com/OpenInfRA/core/blob/master/img/pojo.png "OpenInfRA POJO")

## DAO
Database access is utilized by means of a _DAO_ _pattern_. There exists a DAO class for each _model_ _object_. DAO classes are used to transform 'model objects' into 'POJO objects' and vice versa. This is a litle bit implementation intensive but leads to a maximum of data control. Thus, it is possible to hide data in the REST API and to enrich the REST API with additional information without invoking side effects to the persistence layer.

![OpenInfRA DAO](https://github.com/OpenInfRA/core/blob/master/img/dao.png "OpenInfRA DAO")

## Entity Manager
The _entity_ _manager_ is very impartant for the DAO classes and the reaction time of the application since it utilizes the database access. Thus, each DAO class uses its own 'entity manager'. In order to provide fast access it exists an _EntityManagerFactoryCache_ which provides administation of 'entity manager' objects for DAO classes.

![OpenInfRA EM](https://github.com/OpenInfRA/core/blob/master/img/em.png "OpenInfRA EM")

# TODO
- The JUnit tests have to be extended.
- There is currently no Loggin-Framework available.
