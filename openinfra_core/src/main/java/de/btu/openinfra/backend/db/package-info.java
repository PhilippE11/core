/**
 * This package contains database related stuff which is required to bootstrap
 * the database connections.
 *
 * The class {@see OpenInfraDao} implements the database access for all DAO
 * classes and provides the entity manager which is set by the constructor
 * method. Therefore, the class {@see EntityManagerFactoryCache} is utilized.
 *
 * You have to extend the following classes in order to create a new database
 * schema: {@see OpenInfraSchemas}, {@see EntityManagerFactoryCache}
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
package de.btu.openinfra.backend.db;