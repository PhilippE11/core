package de.btu.openinfra.backend.db.daos.meta;

import java.util.Locale;

import de.btu.openinfra.backend.db.daos.MappingResult;
import de.btu.openinfra.backend.db.daos.OpenInfraDao;
import de.btu.openinfra.backend.db.daos.OpenInfraSchemas;
import de.btu.openinfra.backend.db.jpa.model.meta.Databases;
import de.btu.openinfra.backend.db.pojos.meta.DatabasesPojo;

/**
 * This class represents the Databases and is used to access the underlying 
 * layer generated by JPA.
 * 
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class DatabasesDao
    extends OpenInfraDao<DatabasesPojo, Databases> {

    /**
     * This is the required constructor which calls the super constructor and in 
     * turn creates the corresponding entity manager.
     * 
     * @param schema           the required schema
     */
    public DatabasesDao(OpenInfraSchemas schema) {
        super(null, schema, Databases.class);
    }

    @Override
    public DatabasesPojo mapToPojo(Locale locale, Databases d) {
        return mapPojoStatically(d);
    }

    /**
     * This method implements the method mapToPojo in a static way.
     * 
     * @param at     the model object
     * @return       the POJO object when the model object is not null else null
     */
    public static DatabasesPojo mapPojoStatically(Databases d) {
        if (d != null) {
            DatabasesPojo pojo = new DatabasesPojo();
            pojo.setUuid(d.getId());
            pojo.setDatabase(d.getDatabase());
            return pojo;
        } else {
            return null;
        }
    }

    @Override
    public MappingResult<Databases> mapToModel(
    		DatabasesPojo pojo, 
    		Databases dbs) {
        if(pojo != null) {            
            mapToModelStatically(pojo, dbs);
            return new MappingResult<Databases>(dbs.getId(), dbs);
        }
        else {
            return null;
        }
    }
    
    /**
     * This method implements the method mapToModel in a static way.
     * @param pojo the POJO object
     * @param dbs the pre initialized model object
     * @return return a corresponding JPA model object or null if the pojo
     * object is null
     */
    public static Databases mapToModelStatically(
            DatabasesPojo pojo, 
            Databases dbs) {
        Databases resultDatabases = null;
        if(pojo != null) {
            resultDatabases = dbs;
            if(resultDatabases == null) {
                resultDatabases = new Databases();
                resultDatabases.setId(pojo.getUuid());
            }
            resultDatabases.setDatabase(pojo.getDatabase());
        }
        return resultDatabases;
    }

}
