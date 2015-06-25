package de.btu.openinfra.backend.db.daos.meta;

import java.util.Locale;

import de.btu.openinfra.backend.db.daos.MappingResult;
import de.btu.openinfra.backend.db.daos.OpenInfraDao;
import de.btu.openinfra.backend.db.daos.OpenInfraSchemas;
import de.btu.openinfra.backend.db.jpa.model.meta.Logger;
import de.btu.openinfra.backend.db.pojos.meta.LoggerPojo;

/**
 * This class represents the Logger and is used to access the underlying layer
 * generated by JPA.
 * 
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class LoggerDao
    extends OpenInfraDao<LoggerPojo, Logger>{

    /**
     * This is the required constructor which calls the super constructor and in 
     * turn creates the corresponding entity manager.
     * 
     * @param schema           the required schema
     */
    public LoggerDao(OpenInfraSchemas schema) {
        super(null, schema, Logger.class);
    }

    @Override
    public LoggerPojo mapToPojo(Locale locale, Logger l) {
        return mapPojoStatically(l);
    }

    /**
     * This method implements the method mapToPojo in a static way.
     * 
     * @param at     the model object
     * @return       the POJO object when the model object is not null else null
     */
    public static LoggerPojo mapPojoStatically(Logger l) {
        if (l != null) {
            LoggerPojo pojo = new LoggerPojo();
            pojo.setUuid(l.getId());
            pojo.setLogger(l.getLogger());
            return pojo;
        } else {
            return null;
        }
    }
    
    @Override
    public MappingResult<Logger> mapToModel(LoggerPojo pojo, Logger log) {
        // TODO Auto-generated method stub
        return null;
    }

}
