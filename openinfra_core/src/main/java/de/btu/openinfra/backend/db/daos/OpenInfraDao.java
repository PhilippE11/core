package de.btu.openinfra.backend.db.daos;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.eclipse.persistence.jpa.JpaQuery;

import de.btu.openinfra.backend.OpenInfraProperties;
import de.btu.openinfra.backend.db.EntityManagerFactoryCache;
import de.btu.openinfra.backend.db.MappingResult;
import de.btu.openinfra.backend.db.OpenInfraOrderBy;
import de.btu.openinfra.backend.db.OpenInfraOrderByEnum;
import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.OpenInfraSortOrder;
import de.btu.openinfra.backend.db.jpa.model.MetaData;
import de.btu.openinfra.backend.db.jpa.model.OpenInfraModelObject;
import de.btu.openinfra.backend.db.jpa.model.PtLocale;
import de.btu.openinfra.backend.db.pojos.OpenInfraPojo;
import de.btu.openinfra.backend.exception.OpenInfraEntityException;
import de.btu.openinfra.backend.exception.OpenInfraExceptionTypes;
import de.btu.openinfra.backend.exception.OpenInfraWebException;

/**
 * This class is used to provide a sophisticated way to manage the JPA entity
 * manager.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 * @param <TypePojo>  defines the referring POJO class of the DAO class
 * @param <TypeModel> defines the referring JPA model class of the DAO class
 */
public abstract class OpenInfraDao<TypePojo extends OpenInfraPojo,
	TypeModel extends OpenInfraModelObject> {

	/**
	 * The JPA entity manager used to access the project or the system database.
	 */
	protected EntityManager em;

	/**
	 * The UUID of the current project required for creating the entity manager.
	 */
	protected UUID currentProjectId;

	/**
	 * This variable defines the underlying JPA model class.
	 */
	protected Class<TypeModel> modelClass;

	/**
	 * The currently used schema.
	 */
	protected OpenInfraSchemas schema;

	/**
	 * This method represents the super constructor in order to create an entity
	 * manager for each data access object (DAO) in a sophisticated way. We
	 * decided to use an application managed entity manager in order to have
	 * more control about its properties and its life cycle. Each DAO provides
	 * its own entity manager and the lifetime of the entity manager depends on
	 * the lifetime of the DAO object. The entity manager is automatically
	 * closed by the finalize method. Thus, don't call the close method on the
	 * entity manager otherwise you'll get an exception if you try to access the
	 * entity manager again.
	 *
	 * This method sets the properties of the connection dynamically. If you
	 * would like to change the connection, this should be the first class to
	 * look at. The connection strings should be managed by a configuration
	 * (properties) file or by a database.
	 *
	 * The super constructor will use the current project id in order to access
	 * the meta database and to retrieve the credentials of the currently used
	 * project database. If the current project id is set to null and the
	 * corresponding schema is specified, the super constructor will provide
	 * access to the system database.
	 *
	 * In order to change the schema value you have to consider query parameters
	 * of the connection URL like so:
	 * "jdbc:postgresql://localhost:5432/openinfra?currentSchema=project".
	 * By considering this, you'll be able to modify the required schema easily,
	 * nicely and dynamically during runtime.
	 *
	 * TODO in order to add a User Rights Management system add specific
	 * functions here
	 *
	 * @param currentProjectId The identifier of the current project.
	 * @param schema           This parameter defines the schema.
	 * @param modelClass       The underlying JPA model class.
	 */
	protected OpenInfraDao(
			UUID currentProjectId,
			OpenInfraSchemas schema,
			Class<TypeModel> modelClass) {
		// 1. Set local parameters
		this.currentProjectId = currentProjectId;
		this.schema = schema;
		this.modelClass = modelClass;

		// 2. Create the final entity manager
		em = EntityManagerFactoryCache.getEntityManagerFactory(
		        currentProjectId,
		        schema).createEntityManager();
	}

	/**
	 * This is the default generic method which provides read access to the
	 * selected database schema without sorting. It is almost the same routine
	 * for all DAO classes to access the database.
	 *
     * @param locale     A Java.util locale objects.
	 * @param offset     the number where to start
	 * @param size       the size of items to provide
	 * @return           a list of objects of type POJO class
	 */
    public List<TypePojo> read(Locale locale, int offset, int size) {
		// 1. Define a map which holds the POJO objects
		List<TypePojo> pojos = new LinkedList<TypePojo>();
		// 2. Define a list of model objects
		List<TypeModel> models = null;
		// 3. Define the named query
	    String namedQuery = modelClass.getSimpleName() + ".findAll";
	    // 4. Retrieve the requested model objects from database
	    models = em.createNamedQuery(
	                namedQuery,
	                modelClass)
	                .setFirstResult(offset)
	                .setMaxResults(size)
	                .getResultList();
		// 5. Finally, map the JPA model objects to POJO objects
		for(TypeModel modelItem : models) {
		    pojos.add(mapToPojo(locale, modelItem));
		} // end for
		return pojos;
	}

	/**
	 * This is a generic method which provides read access to the selected
	 * database schema. It is almost the same routine for all DAO classes to
	 * access the database. If not, this method should be extended in order
	 * to avoid overrides. Overrides could increase the effort regarding the
	 * integration and maintenance of a rights management system.
	 *
     * Since the system schema doesn't provide the project_id column for topic
     * characteristic objects it is necessary to handle this request separately.
     *
     * The meta data schema is also handled separately.
     *
     * Don't use this method while using RBAC, FILE or WEBAPP schemas!
	 *
	 *
     * @param locale     A Java.util locale objects.
	 * @param order      the sort order (ascending or descending)
	 * @param column     the column to sort
	 * @param offset     the number where to start
	 * @param size       the size of items to provide
	 * @return           a list of objects of type POJO class
	 * @throws           OpenInfraEntityException for unsupported orderBy types
	 * @throws           OpenInfraWebException for internal server errors
	 */
	@SuppressWarnings("unchecked")
    public List<TypePojo> read(
    		Locale locale,
    		OpenInfraSortOrder order,
    		OpenInfraOrderBy column,
    		int offset,
    		int size) {
		// 1. Define a list which holds the POJO objects
		List<TypePojo> pojos = new LinkedList<TypePojo>();
		// 2. Define a list of model objects
		List<TypeModel> models = null;
		// 3. Use the default values for language and order when null.
		if(locale == null) {
			locale = OpenInfraProperties.DEFAULT_LANGUAGE;
		}
		if(order == null) {
			order = OpenInfraProperties.DEFAULT_ORDER;
		}
		// 4. Read the required ptlocale object if exists
		String ptlId = "";
		try {
		    PtLocale ptl = new PtLocaleDao(currentProjectId, schema)
		        .read(locale);
		    ptlId = ptl.getId().toString();
		} catch (PersistenceException e) {
		    // The pt locale table only exists in projects and system schemas.
		    // If a sorting is requested by another schema, we must catch the
		    // exception and just do nothing.
		}
		// 5. Handle requests that contains an order by column
        if(column == null) {
            // 5.a When the column is null redirect to another method
            return read(locale, offset, size);
        } else {
            // orderBy UUIDs is not supported
            if (column.isUuid()) {
                throw new OpenInfraEntityException(
                        OpenInfraExceptionTypes.WRONG_SORT_TYPE);
            }
            // check if the orderBy column is supported for the current object
            checkOrderBy(column);

            // 5.a Construct the origin SQL-based named query and replace the
            //     the placeholder by the required column and sort order.
	        String sqlString = em.createNamedQuery(
	        		modelClass.getSimpleName() + ".findAllByLocaleAndOrder")
	        		.unwrap(JpaQuery.class).getDatabaseQuery().getSQLString();
            sqlString = String.format(sqlString, column.getColumn().name());
            sqlString += " " + order.name();

	        // 5.b Retrieve the requested model objects from database.
	        try {
	            models = em.createNativeQuery(
	                    sqlString,
	                    modelClass)
	                    .setParameter(1, ptlId)
	                    .setFirstResult(offset)
	                    .setMaxResults(size)
	                    .getResultList();
            } catch (PersistenceException e) {
                // can be thrown if the orderByEnum is incorrectly configured
                throw new OpenInfraWebException(e);
            }

		} // end if else

		// 6. Finally, map the JPA model objects to POJO objects
		for(TypeModel modelItem : models) {
		    pojos.add(mapToPojo(locale, modelItem));
		} // end for
		return pojos;
	}

	/**
     * A special method which returns model objects. This method is primarily
     * used to create a search index.
     *
     * @return a list of all model objects
     */
    public List<TypeModel> read() {
        return read(0, Integer.MAX_VALUE);
    }

    /**
	 * A special method which returns model objects. This method is primarily
	 * used to create a search index. Window functionality is provided to avoid
	 * out of memory exceptions.
	 *
	 * @param offset This will determine the offset
	 * @param size
	 * @return a list of all model objects
	 */
	public List<TypeModel> read(int offset, int size) {
		String namedQuery = modelClass.getSimpleName() + ".findAll";
		return em.createNamedQuery(
				namedQuery,
				modelClass)
				.setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();
	}

	/**
	 * This function creates a new or updates an existing object in the
	 * database. The update function depends on the specified language.
	 *
	 * @param pojo    the POJO object which should be stored in the database
	 * @param valueId the UUID of the specific object - should be null for
	 *                create or POST requests
	 * @return        the UUID of the newly created or replaced object
	 * @throws RuntimeException
	 */
	public UUID createOrUpdate(TypePojo pojo, UUID valueId) {

		if(valueId != null && !valueId.equals(pojo.getUuid())) {
			throw new OpenInfraEntityException(
					OpenInfraExceptionTypes.INCOMPATIBLE_UUIDS);
		}

	    TypeModel model = createModelObject(valueId);
        // abort if the POJO or the type model is null
        if (pojo == null || model == null) {
            return null;
        }

        // check if we come from a PUT request (special case handling for
        // projects, because the can perform a POST with a passed value id)
        // TODO this will avoid the TRID check for updating projects /
        //      subprojects
        if (valueId != null && !modelClass.getSimpleName().equals("Project")) {
            // check if a TRID was sent by the client
            if (pojo.getTrid() == 0) {
                throw new OpenInfraEntityException(
                		OpenInfraExceptionTypes.NO_TRID_PASSED);
            }
            // check if the TRID from the POJO equals the models xmin
            if (pojo.getTrid() != model.getXmin()) {
            	throw new OpenInfraEntityException(
            			OpenInfraExceptionTypes.ENTITY_EXPIRED);
            }
        }

        // 1. Map the POJO object to a JPA model object
        MappingResult<TypeModel> result = mapToModel(pojo, model);

        // abort here if the result is null
        if (result == null) {
            return null;
        }

		// 2. Get the transaction and merge (create or replace) the JPA model
		// object.
		EntityTransaction et = em.getTransaction();
		try {
			et.begin();
			em.merge(result.getModelObject());
			et.commit();
			return result.getId();
		} catch(RuntimeException ex) {
			if(et != null && et.isActive()) {
				et.rollback();
			} // end if
			throw new OpenInfraWebException(ex);
		} // end try catch
    }

	/**
	 * This is a generic method which reads a specific pojo object. We decided
	 * to hide this functionality behind a protected class so that each deriving
	 * class must implement its own read method. This should provide more type
	 * safety.
	 *
     * @param locale             A Java.util locale object.
	 * @param id                 the UUID of the required model class
	 * @return                   The requested POJO object if present otherwise
	 *                           null.
	 */
    public TypePojo read(Locale locale, UUID id) {
		TypePojo pojo = null;
		try {
            TypeModel tm = em.find(modelClass, id);
            if(tm != null) {
                pojo = mapToPojo(locale, tm);
            }
		} catch(Exception ex) {
            ex.printStackTrace();
        } // end try catch
		return pojo;
	}

	/**
	 * This is an abstract method which must be implemented by deriving classes.
	 * It should map a JPA model object into corresponding a POJO object.
	 *
	 * This method is really slow. Thus, don't use this method too often. It's
	 * recommended to provide another static method which should be much faster.
	 *
	 * @param modelObject the JPA model object
	 * @return            a corresponding POJO object
	 */
	public abstract TypePojo mapToPojo(Locale locale, TypeModel modelObject);

	/**
	 * This is an abstract method which must be implemented by deriving classes.
	 * It should map a POJO object into a corresponding JPA model object.
	 *
	 * @param modelObject the pre initialized model object
	 * @param pojoObject  the POJO object
	 * @return            a corresponding JPA model object
	 * @throws            OpenInfraEntityException
	 */
	public abstract MappingResult<TypeModel> mapToModel(
			TypePojo pojoObject, TypeModel modelObject);

	/**
	 * This method deletes an entity from the database. Previously it checks if
	 * meta data exists for this entity and delete it as well.
	 *
	 * @param uuid the uuid which specifies the entity
	 * @return     true when the entity was deleted false when the uuid doesn't
	 *             exists
	 */
	public boolean delete(UUID uuid) {
	    // only check for meta data in project and system schema
	    switch (schema) {
            case PROJECTS:
            case SYSTEM:
        	    try {
        	        // retrieve meta data for the entity if exists
                    MetaData md = em.createNamedQuery(
                            "MetaData.findByObjectId", MetaData.class)
                            .setParameter("oId", uuid).getSingleResult();
                    // delete the meta data
                    if (new MetaDataDao(currentProjectId, schema).delete(
                            md.getId())) {
                        // TODO give feedback about meta data deletion?
                    }
                } catch (NoResultException nre) { /* do nothing */ }
        	    break;
            default:
                break;
        }

	    // delete the common entity
		TypeModel o = em.find(modelClass, uuid);
		EntityTransaction et = em.getTransaction();
		if(o != null) {
			try {
				et.begin();
				em.remove(o);
				et.commit();
				return true;
			} catch(RuntimeException ex) {
				if(et != null && et.isActive()) {
					et.rollback();
				} // end if
				throw ex;
			} // end try catch
		} else {
			return false;
		} // end if else
	}

	/**
	 * This method delivers a model object. When the UUID is not null this
	 * method tries to deliver an existing object. When the UUID is null it
	 * creates a new object and generates a random UUID.
	 *
	 * This is a very central method which is always called when a POJO object
	 * is mapped into a model object.
	 *
	 * @param id the UUID of the requested object or null
	 * @return   a model object (new or old) or null if the pojo id doesn't
	 *           exists
	 */
	protected TypeModel createModelObject(UUID id) {
		TypeModel tm = null;
		UUID uuid = UUID.randomUUID();

		if(id != null) {
		    // retrieve the object from the database
			tm = em.find(modelClass, id);
			// set the passed id as POJO id if we came from project class
			if (modelClass.getSimpleName().equals("Project")) {
                uuid = id;
            }
		}

		if(tm == null) {
			try {
				tm = modelClass.newInstance();
				tm.setId(uuid);
			} catch(Exception ex) {
				ex.printStackTrace();
			} // end try catch
		}

		return tm;
	}

	/**
	 * This method retrieves the count of objects and uses a named query. If the
	 * query doesn't exists, it will throw an exception.
	 *
	 * @return count of objects or zero
	 */
	public long getCount() {
		String namedQuery = modelClass.getSimpleName() + ".count";
		Long count = 0L;
		count = em.createNamedQuery(
            namedQuery,
            Long.class).getSingleResult().longValue();
		return count;
	}

	/**
	 * This method closes the entity manager automatically. Don't close the
	 * entity manager manually. This will cause an exception when you try to
	 * access the entity manager again during life time of the current object
	 * instance.
	 */
	@Override
	protected void finalize() throws Throwable {
		if(em != null) {
			em.close();
		} // end if
		super.finalize();
	}

	/**
     * This method retrieves the list of objects from the OpenInfraOrderByEnum
     * for the passed orderBy column and compares it with the current object. If
     * the current object is not part of the retrieved list, an
     * OpenInfraEntityException is thrown.
     *
     * @param column
     * @throws OpenInfraEntityException if the current object is not supported
     *         by the OpenInfraOrderByEnum for the passed column
     */
    protected void checkOrderBy(OpenInfraOrderBy column) {
        // flag to determine if the orderBy type is supported
        boolean wrongSortType = true;

        // get the list of classes that fit to the orderBy column
        List<String> orderByClasses =
                OpenInfraOrderByEnum.valueOf(
                        column.getColumn().name()).getList();

        // iterate over all class names
        for (String cl : orderByClasses) {
            // check if the current modelClass fits to a class from the
            // orderBy column
            if (modelClass.getSimpleName().equals(cl)) {
                wrongSortType = false;
                break;
            }
        }

        // throw an exception if a wrong sort type was detected
        if (wrongSortType) {
            throw new OpenInfraEntityException(
                    OpenInfraExceptionTypes.WRONG_SORT_TYPE);
        }

    }

}
