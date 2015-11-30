/**
 * This package contains the OpenInfRA main application class (Webbrowser:
 * http://localhost:8080/openinfra_backend/rest/v1/projects). The class
 * {@see OpenInfraApplication} is configured in the file 'web.xml' and will be
 * loaded at application start.
 *
 * The package structure of OpenInfRA is as follows:
 * db - Contains database related classes
 * db.jpa - JPA model classes optimized for EclipseLink including SQL queries
 * db.converter - PostgreSQL specific converter classes
 * db.daos - DAO pattern which utilizes the JPA classes
 * db.pojos - POJO's are part of the DAO pattern and used as data containers
 * db.rbac - Role-based access contorl
 * exeption - System wide exceptions
 * filter - HTTP response and request filter
 * rest - Jersey related registration of REST API
 * rest.view - JSP-based technical GUI
 * solr - Search engine
 * helper - Some helper classes (usually not required by the application)
 *
 * Please don't write SQL queries directly in the Java code. Use the JPA-based
 * NamedQueries or NativeQueries in order to place them into the model classes.
 * This helps to keep the code nicely and clean. Moreover, it helps a lot to
 * avoid redundant queries.
 *
 * As stated above, POJO's are used as data containers. These data containers
 * are processed (filled/extracted) by the DAO classes. This is more complex
 * then the usage of the model classes itself but leads to maximum control of
 * content which is shipped to the REST API.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
package de.btu.openinfra.backend;