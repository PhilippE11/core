package de.btu.openinfra.backend.db.daos;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import jersey.repackaged.com.google.common.collect.Lists;
import de.btu.openinfra.backend.OpenInfraProperties;
import de.btu.openinfra.backend.OpenInfraPropertyKeys;
import de.btu.openinfra.backend.db.MappingResult;
import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.daos.meta.CredentialsDao;
import de.btu.openinfra.backend.db.daos.meta.DatabaseConnectionDao;
import de.btu.openinfra.backend.db.daos.meta.DatabasesDao;
import de.btu.openinfra.backend.db.daos.meta.PortsDao;
import de.btu.openinfra.backend.db.daos.meta.ProjectsDao;
import de.btu.openinfra.backend.db.daos.meta.SchemasDao;
import de.btu.openinfra.backend.db.daos.meta.ServersDao;
import de.btu.openinfra.backend.db.jpa.model.Project;
import de.btu.openinfra.backend.db.jpa.model.meta.Credentials;
import de.btu.openinfra.backend.db.jpa.model.meta.Databases;
import de.btu.openinfra.backend.db.jpa.model.meta.Ports;
import de.btu.openinfra.backend.db.jpa.model.meta.Servers;
import de.btu.openinfra.backend.db.pojos.LocalizedString;
import de.btu.openinfra.backend.db.pojos.ProjectPojo;
import de.btu.openinfra.backend.db.pojos.PtFreeTextPojo;
import de.btu.openinfra.backend.db.pojos.meta.DatabaseConnectionPojo;
import de.btu.openinfra.backend.db.pojos.meta.ProjectsPojo;
import de.btu.openinfra.backend.db.pojos.meta.SchemasPojo;
import de.btu.openinfra.backend.exception.OpenInfraDatabaseException;
import de.btu.openinfra.backend.exception.OpenInfraExceptionTypes;

/**
 * This class represents the Project and is used to access the underlying layer
 * generated by JPA.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class ProjectDao extends OpenInfraDao<ProjectPojo, Project> {

	/**
	 * This is the required constructor which calls the super constructor and in
	 * turn creates the corresponding entity manager.
	 *
	 * @param currentProjectId the current project id (this should be null when
	 *                         the system schema is selected)
	 * @param schema           the required schema
	 */
	public ProjectDao(UUID currentProjectId, OpenInfraSchemas schema) {
		super(currentProjectId, schema, Project.class);
	}

	/**
	 * This method delivers a list of sub projects specified by the current
	 * project id {@see OpenInfraDao} in only one hierarchy level.
	 *
     * @param locales    A list of Java.util locale objects. All locals which
     *                   this list contains will be returned. As defined by
     *         <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html">
     *                   rfc2616</a>, the asterisk is used to retrieve all
     *                   available languages.
	 * @param offset     the number where to start
	 * @param size       the size of items to provide
	 * @return           a list of sub projects referring to the specified
	 *                   project in only one hierarchy level
	 */
	public List<ProjectPojo> readSubProjects(
			Locale locale,
			int offset,
			int size) {
		Project p = em.find(Project.class, currentProjectId);
		List<ProjectPojo> pojos = new LinkedList<ProjectPojo>();
		for(Project project : p.getProjects()) {
			pojos.add(mapToPojo(locale, project));
		} // end for
		return pojos;
	}

	/**
	 * This method returns the count of sub projects.
	 *
	 * @return count of sub projects
	 */
	public long getSubProjectsCount() {
		return readSubProjects(null, 0, Integer.MAX_VALUE).size();
	}

	@Override
	public long getCount() {
		return readMainProjects(null).size();
	}

	/**
	 * This method delivers a list of main projects retrieved from metadata
	 * database. This method also retrieves the name and description of
	 * all main projects specified by the locale parameter.
	 *
	 * @param locale the required language
	 * @return       a list of main projects
	 */
	public List<ProjectPojo> readMainProjects(Locale locale) {
		// 1. We need to deliver each main Project from meta data database
		List<ProjectsPojo> projects = new ProjectsDao(
				OpenInfraSchemas.META_DATA).read(
						locale,
						0,
						Integer.MAX_VALUE);
		Iterator<ProjectsPojo> it = projects.iterator();
		// 2. Only keep main projects in the list
		while (it.hasNext()) {
			if(it.next().getIsSubproject()) {
				it.remove();
			} // end if
		} // end while
		// 3. Create a list of main projects and add a corresponding main
		//    project to the list found in the metadata database
		List<ProjectPojo> mainProjects = new LinkedList<ProjectPojo>();
		for(ProjectsPojo item : projects) {
			ProjectPojo pp = new ProjectDao(
					 item.getUuid(),
					 OpenInfraSchemas.PROJECTS).read(locale, item.getUuid());
			if(pp != null) {
				mainProjects.add(pp);
			}
		} // end for
		return mainProjects;
	}

	/**
	 * This method returns the count of main projects.
	 *
	 * @return the count of main projects
	 */
	public long getMainProjectsCount() {
		return readMainProjects(null).size();
	}

	@Override
	public ProjectPojo mapToPojo(Locale locale, Project p) {
		return mapToPojoStatically(locale, p,
		        new MetaDataDao(currentProjectId, schema));
	}

	/**
     * This method implements the method mapToPojo in a static way.
     *
     * @param locale the requested language as Java.util locale
     * @param p      the model object
     * @param mdDao  the meta data DAO
     * @return       the POJO object when the model object is not null else null
     */
	public static ProjectPojo mapToPojoStatically(Locale locale, Project p,
	        MetaDataDao mdDao) {
		if(p != null) {
			ProjectPojo pojo = new ProjectPojo(p, mdDao);

			Project parent = p.getProject();

			pojo.setNames(PtFreeTextDao.mapToPojoStatically(
					locale,
					p.getPtFreeText2()));
			pojo.setDescriptions(PtFreeTextDao.mapToPojoStatically(
					locale,
					p.getPtFreeText1()));
			if(parent != null) {
				pojo.setSubprojectOf(parent.getId());
			} // end if

			return pojo;
		} else {
			return null;
		} // end if else
	}

	@Override
	public MappingResult<Project> mapToModel(ProjectPojo pojo, Project p) {

	    // return null if the pojo is null
	    if (pojo != null) {

            // avoid empty names
            if (pojo.getNames().getLocalizedStrings().get(0)
                    .getCharacterString().equals("")) {
                return null;
            }

        	PtFreeTextDao ptfDao =
                    new PtFreeTextDao(currentProjectId, schema);
            // set the description (optional)
            if (pojo.getDescriptions() != null) {
                p.setPtFreeText1(
                        ptfDao.getPtFreeTextModel(pojo.getDescriptions()));
            }

            // set the name
            p.setPtFreeText2(ptfDao.getPtFreeTextModel(pojo.getNames()));

    		// set the sub project (optional)
    		if(pojo.getSubprojectOf() != null) {
    			p.setProject(em.find(Project.class, pojo.getSubprojectOf()));
    		} else {
    		    // reset the subProject id
    		    p.setProject(null);
    		}

    		return new MappingResult<Project>(p.getId(), p);
	    } else {
	        return null;
	    }
	}

	/**
	 * This method delivers a list of parents in reverse order.
	 *
	 * @param locale the required locale
	 * @return       a list of parents in reverse order
	 */
	public List<ProjectPojo> readParents(Locale locale) {
		Project self = em.find(Project.class, currentProjectId);
		List<ProjectPojo> parents = new LinkedList<ProjectPojo>();
		MetaDataDao mdDao = new MetaDataDao(currentProjectId, schema);
		parents.add(mapToPojoStatically(locale, self, mdDao));

		while(self.getProject() != null) {
			self = self.getProject();
			parents.add(mapToPojoStatically(locale, self, mdDao));
		} // end while
		return Lists.reverse(parents);
	}

	/**
	 * This is a special method which returns a hierarchical list of parent
	 * projects plus the current project.
	 *
	 * @param locale the locale of the request (shouldn't be null)
	 * @param url    the current URL
	 * @return       list of parents or a list containing a null value
	 */
	public static List<ProjectPojo> getParents(Locale locale, String url) {
		String currentProject = getCurrentProject(url);
		if(currentProject != null) {
			return new ProjectDao(
					UUID.fromString(currentProject),
					OpenInfraSchemas.PROJECTS).readParents(locale);
		} else {
			return new LinkedList<ProjectPojo>();
		} // end if else
	}

	/**
	 * This is a static method which returns the current project id by the
	 * given URL string. It'll return an empty String object when the current
	 * URL doesn't contain a specific project id.
	 *
	 * @param url the URL to parse
	 * @return    the current project id or null when not specified
	 */
	public static String getCurrentProject(String url) {
		String[] split = url.split("/");
		if(split.length >= 6 && split[5] != null) {
			return split[5];
		} else {
			return "";
		} // end if else
	}

	/**
	 * This method decides if the project that should be created is a new sub
	 * project or a new main project. If it is a new main project it is
	 * necessary to create a new database schema and write some data into the
	 * meta data schema.
	 *
	 * @param project the project pojo
	 * @return        the UUID of the new created project or NULL if something
	 *                went wrong
	 */
	public UUID createProject(ProjectPojo pojo) {

		// the UUID that will be returned
	    UUID id = null;

	    // generate a UUID for the new project
        UUID newProjectId = UUID.randomUUID();

	    // determine if we want to create a sub or a main project
	    if (pojo.getSubprojectOf() != null) {
	        id = new ProjectDao(pojo.getSubprojectOf(),
	                OpenInfraSchemas.PROJECTS).createOrUpdate(pojo);
	    } else {
	        try {
    	        // create the database schema
                createSchema(pojo, newProjectId);

                // insert the necessary data into the meta data schema
                writeMetaData(newProjectId);

                // insert the basic project data into the project table in the
                // new project schema
                writeBasicProjectData(pojo, newProjectId);

                // copy the initial data from the system schema into the new
                // project schema
                mergeSystemData(newProjectId);

                id = newProjectId;

	        } catch (OpenInfraDatabaseException e) {
	            // execute a roll back
	            // 1) remove entries from meta data
	            // 2) delete database schema

	            switch (e.getType()) {
	            case MERGE_SYSTEM:
	                /* run through */
	            case INSERT_BASIC_PROJECT_DATA:
	                /* run through */
                case INSERT_META_DATA:
                    /* run through */
                case RENAME_SCHEMA:
                    /* run through */
                case CREATE_SCHEMA:

                    break;




                default:
                    /* no roll back necessary */
                    break;
                }
            }
	    }
	    return id;
	}

	/**
	 * This method decides if the project that should be deleted is a sub
     * project or a main project. If it is a main project it is necessary to
     * delete the database schema and remove the project entry in the meta data
     * schema.
     *
	 * @return true if the project was deleted otherwise false
	 */
	public boolean deleteProject() {

	    boolean result = false;

	    // determine if we want to delete a sub or a main project
	    if (em.find(Project.class, currentProjectId)
	            .getProject().getId() != null) {
	        // delete a sub project
	        result = delete(currentProjectId);
	    } else {
	        // TODO implement the deletion of a project database schema
	    }
	    return result;
	}

    /**
     * This method creates a ProjectPojo shell that contains informations about
     * the name, the description and the parent project.
     *
     * @param locale the locale the informations should be saved at
     * @return       the ProjectPojo
     */
    public ProjectPojo newSubProject(Locale locale) {
        // create the return pojo
        ProjectPojo pojo = new ProjectPojo();

        PtLocaleDao ptl = new PtLocaleDao(currentProjectId, schema);
        List<LocalizedString> lcs = new LinkedList<LocalizedString>();
        LocalizedString ls = new LocalizedString();

        // set an empty character string
        ls.setCharacterString("");

        // set the locale of the character string
        ls.setLocale(PtLocaleDao.mapToPojoStatically(
                locale,
                ptl.read(locale)));
        lcs.add(ls);

        // add the localized string for the name
        pojo.setNames(new PtFreeTextPojo(lcs, null));

        // add the localized string for the description
        pojo.setDescriptions(new PtFreeTextPojo(lcs, null));

        // set the id of the main project to the current project
        pojo.setSubprojectOf(currentProjectId);

        return pojo;
    }

    /**
     * This method creates a new project schema with the given project id. The
     * schema creation will be handled by JPA using a special persistence
     * context. After schema creation a stored procedure is called that will
     * rename the schema.
     *
     * @param pojo
     * @param newProjectId
     * @throws OpenInfraDatabaseException if the creation or renaming of the
     *         schema failed.
     * @return
     */
    private void createSchema(ProjectPojo pojo, UUID newProjectId)
            throws OpenInfraDatabaseException {
        try {
            // set the default database connection properties
            Map<String, String> properties =
                    OpenInfraProperties.getConnectionProperties();

            // create the new project schema with trigger and initial project
            // data
            Persistence.generateSchema("openinfra_schema_creation", properties);

            // rename the project schema
            if (!em.createStoredProcedureQuery(
                "rename_project_schema", Boolean.class)
                    .registerStoredProcedureParameter(
                        "name",
                        String.class,
                        ParameterMode.IN)
                    .registerStoredProcedureParameter(
                        "uuid",
                        UUID.class,
                        ParameterMode.IN)
                    .setParameter(
                        "name",
                        pojo.getNames().getLocalizedStrings().get(0)
                            .getCharacterString())
                    .setParameter("uuid", newProjectId)
                                .execute()) {
                throw new OpenInfraDatabaseException(
                        OpenInfraExceptionTypes.RENAME_SCHEMA);
            }
        } catch (PersistenceException pe) {
            throw new OpenInfraDatabaseException(
                    OpenInfraExceptionTypes.CREATE_SCHEMA);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method writes all necessary meta data for the new project into the
     * meta data schema.
     *
     * @param newProjectId
     * @throws SchemaCreationException
     * @return
     */
    private void writeMetaData(UUID newProjectId)
            throws OpenInfraDatabaseException {
        try {
            // create a POJO for the schema in the meta data schema
            SchemasPojo metaSchemasPojo = new SchemasPojo();
            // set all necessary data for the schema
            metaSchemasPojo.setSchema("project_" + newProjectId);
            // create the DAO for the schema
            SchemasDao schemaDao = new SchemasDao(OpenInfraSchemas.META_DATA);
            // insert the data
            // TODO: createOrUpdate can throw an exception!
            UUID schemaId = schemaDao.createOrUpdate(metaSchemasPojo);

            // create a POJO for the database connection in the meta data schema
            DatabaseConnectionPojo dbCPojo = new DatabaseConnectionPojo();
            // set all necessary data for the database connection
            dbCPojo.setSchema(schemaDao.read(null, schemaId));
            // create necessary DAOs for the credentials, ports, databases and
            // servers
            CredentialsDao credentialsDao =
                    new CredentialsDao(OpenInfraSchemas.META_DATA);
            PortsDao portDao = new PortsDao(OpenInfraSchemas.META_DATA);
            DatabasesDao dbDao = new DatabasesDao(OpenInfraSchemas.META_DATA);
            ServersDao serverDao = new ServersDao(OpenInfraSchemas.META_DATA);

            // TODO Only the default values from the properties file will be
            //      used for the new connection. Find a better way?
            // retrieve the default credentials
            dbCPojo.setCredentials(
                    credentialsDao.mapToPojo(
                            null,
                            em.createNamedQuery(
                                    "Credentials.findByUsernameAndPassword",
                                    Credentials.class)
                              .setParameter(
                                      "username",
                                      OpenInfraProperties.getProperty(
                                              OpenInfraPropertyKeys.USER
                                              .toString()))
                              .setParameter(
                                      "password",
                                      OpenInfraProperties.getProperty(
                                              OpenInfraPropertyKeys.PASSWORD
                                              .toString()))
                              .getSingleResult()));
            // retrieve the default port
            dbCPojo.setPort(
                    portDao.mapToPojo(
                            null,
                            em.createNamedQuery(
                                    "Ports.findByPort",
                                    Ports.class)
                              .setParameter(
                                      "port",
                                      Integer.parseInt(
                                              OpenInfraProperties.getProperty(
                                                      OpenInfraPropertyKeys.PORT
                                                      .toString())))
                              .getSingleResult()));
            // retrieve the default database
            dbCPojo.setDatabase(
                    dbDao.mapToPojo(
                            null,
                            em.createNamedQuery(
                                    "Databases.findByDatabase",
                                    Databases.class)
                              .setParameter(
                                      "database",
                                      OpenInfraProperties.getProperty(
                                              OpenInfraPropertyKeys.DB_NAME
                                              .toString()))
                              .getSingleResult()));
            // retrieve the default server
            dbCPojo.setServer(
                    serverDao.mapToPojo(
                            null,
                            em.createNamedQuery(
                                    "Servers.findByServer",
                                    Servers.class)
                              .setParameter(
                                      "server",
                                      OpenInfraProperties.getProperty(
                                              OpenInfraPropertyKeys.SERVER
                                              .toString()))
                              .getSingleResult()));
            // create the DAO for the database connection
            DatabaseConnectionDao dbCDao =
                    new DatabaseConnectionDao(OpenInfraSchemas.META_DATA);
            // insert the database connection information
            UUID dbCId = dbCDao.createOrUpdate(dbCPojo);
            if (dbCId == null) {
                throw new OpenInfraDatabaseException(
                        OpenInfraExceptionTypes.INSERT_META_DATA);
            }

            // create a POJO for the project in the meta data schema
            ProjectsPojo metaProjectsPojo = new ProjectsPojo();
            metaProjectsPojo.setUuid(newProjectId);
            // set the subproject flag to false
            metaProjectsPojo.setIsSubproject(false);
            // set the database connection information
            metaProjectsPojo.setDatabaseConnection(dbCDao.read(null, dbCId));
            // insert the informations into the meta_data schema
            new ProjectsDao(OpenInfraSchemas.META_DATA)
                .createOrUpdate(metaProjectsPojo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteMetaData() {
        // delete from meta_data
    }

    /**
     * This method writes the basic project data in the new created project
     * schema. This covers only an initial entry in the table project.
     *
     * @param pojo
     * @param newProjectId
     * @throws SchemaCreationException
     * @return
     */
    private void writeBasicProjectData(ProjectPojo pojo, UUID newProjectId)
            throws OpenInfraDatabaseException {
        try {
            // create a POJO for the project in the recent created project
            // schema
            ProjectPojo newProjectPojo = new ProjectPojo();
            // set the recently generated UUID as id of the project
            newProjectPojo.setUuid(newProjectId);
            // take on the parameters of the passed POJO
            newProjectPojo.setNames(pojo.getNames());
            newProjectPojo.setDescriptions(pojo.getDescriptions());
            // set the sub project to null because it is a main project
            newProjectPojo.setSubprojectOf(null);

            // write the data of the project
            new ProjectDao(newProjectId, OpenInfraSchemas.PROJECTS)
                    .createOrUpdate(newProjectPojo);

        } catch (RuntimeException re) {
            throw new OpenInfraDatabaseException(
                    OpenInfraExceptionTypes.INSERT_BASIC_PROJECT_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method merges the initial topic framework from the system schema
     * into the project schema with the passed project id.
     *
     * @param newProjectId
     * @throws SchemaCreationException
     * @return
     */
    private void mergeSystemData(UUID newProjectId)
            throws OpenInfraDatabaseException {
        try {
            // merge the content of the system schema into the new project
            // schema
            if(em.createStoredProcedureQuery(
                    "merge_system_schema", Boolean.class)
                    .registerStoredProcedureParameter(
                            "project_id",
                            UUID.class,
                            ParameterMode.IN)
                    .setParameter("project_id", newProjectId)
                    .execute()) {

            } else {
                throw new OpenInfraDatabaseException(
                        OpenInfraExceptionTypes.MERGE_SYSTEM);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
