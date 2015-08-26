package de.btu.openinfra.backend.db.daos;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import jersey.repackaged.com.google.common.collect.Lists;
import de.btu.openinfra.backend.db.MappingResult;
import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.daos.meta.ProjectsDao;
import de.btu.openinfra.backend.db.jpa.model.Project;
import de.btu.openinfra.backend.db.pojos.LocalizedString;
import de.btu.openinfra.backend.db.pojos.ProjectPojo;
import de.btu.openinfra.backend.db.pojos.PtFreeTextPojo;
import de.btu.openinfra.backend.db.pojos.meta.ProjectsPojo;

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
		return getMainProjects(null).size();
	}

	/**
	 * This method delivers a list of main projects retrieved from metadata
	 * database. This method also retrieves the name and description of
	 * all main projects specified by the locale parameter.
	 *
	 * @param locale the required language
	 * @return       a list of main projects
	 */
	public List<ProjectPojo> getMainProjects(Locale locale) {
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
		return getMainProjects(null).size();
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
	public List<ProjectPojo> getParents(Locale locale) {
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
					OpenInfraSchemas.PROJECTS).getParents(locale);
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
		if(split.length >= 5 && split[4] != null) {
			return split[4];
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
	public static UUID createProject(ProjectPojo project) {

	    UUID id = null;

	    // determine if we want to create a sub or a main project
	    if (project.getSubprojectOf() != null) {
	        id = new ProjectDao(project.getSubprojectOf(),
	                OpenInfraSchemas.PROJECTS).createOrUpdate(project);
	    } else {
	        // TODO implement the creation of a new project database schema
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
}
