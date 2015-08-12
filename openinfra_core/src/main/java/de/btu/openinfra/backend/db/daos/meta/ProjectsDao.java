package de.btu.openinfra.backend.db.daos.meta;

import java.util.Locale;

import de.btu.openinfra.backend.db.MappingResult;
import de.btu.openinfra.backend.db.daos.OpenInfraDao;
import de.btu.openinfra.backend.db.daos.OpenInfraSchemas;
import de.btu.openinfra.backend.db.jpa.model.meta.Projects;
import de.btu.openinfra.backend.db.pojos.meta.ProjectsPojo;

/**
 * This class represents the Projects and is used to access the underlying 
 * layer generated by JPA.
 * 
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class ProjectsDao
    extends OpenInfraDao<ProjectsPojo, Projects>{

    /**
     * This is the required constructor which calls the super constructor and in 
     * turn creates the corresponding entity manager.
     * 
     * @param schema           the required schema
     */
    public ProjectsDao(OpenInfraSchemas schema) {
        super(null, schema, Projects.class);
    }

    @Override
    public ProjectsPojo mapToPojo(Locale locale, Projects p) {
        return mapPojoStatically(p);
    }

    /**
     * This method implements the method mapToPojo in a static way.
     * 
     * @param p      the model object
     * @return       the POJO object when the model object is not null else null
     */
    public static ProjectsPojo mapPojoStatically(Projects p) {
        if (p != null) {
            ProjectsPojo pojo = new ProjectsPojo();
            pojo.setUuid(p.getId());
            pojo.setTrid(p.getXmin());
            pojo.setDatabaseConnection(
                    DatabaseConnectionDao.mapPojoStatically(
                            p.getDatabaseConnection()));
            pojo.setIsSubproject(p.getIsSubproject());
            return pojo;
        } else {
            return null;
        }
    }

    @Override
    public MappingResult<Projects> mapToModel(ProjectsPojo pojo, Projects ps) {
        if(pojo != null) {            
            mapToModelStatically(pojo, ps);
            return new MappingResult<Projects>(ps.getId(), ps);
        }
        else {
            return null;
        }
    }
    
    /**
     * This method implements the method mapToModel in a static way.
     * @param pojo the POJO object
     * @param projects the pre initialized model object
     * @return return a corresponding JPA model object or null if the pojo
     * object is null
     */
    public static Projects mapToModelStatically(
            ProjectsPojo pojo, Projects projects) {
        Projects resultProjects = null;
        if(pojo != null) {
            resultProjects = projects;
            if(resultProjects == null) {
                resultProjects = new Projects();
                resultProjects.setId(pojo.getUuid());
            }
            resultProjects.setIsSubproject(pojo.getIsSubproject());
            resultProjects.setDatabaseConnection(
                    DatabaseConnectionDao.mapToModelStatically(
                            pojo.getDatabaseConnection(),
                            null));
        }
        return resultProjects;
    }

    /**
     * Creates an empty projects pojo.
     * @return an empty projects pojo
     */
    public ProjectsPojo newProjects() {
       return newPojoStatically();
    }

    /**
     * This method implements the method newProjects in a static way.
     * @return an empty projects pojo
     */
    public static ProjectsPojo newPojoStatically() {
        ProjectsPojo newProjectsPojo = new ProjectsPojo();
        newProjectsPojo.setDatabaseConnection(
                DatabaseConnectionDao.newPojoStatically());      
        return newProjectsPojo;
    }
}
