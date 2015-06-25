package de.btu.openinfra.backend.db.daos;

import java.util.Locale;
import java.util.UUID;

import javax.persistence.Query;

import de.btu.openinfra.backend.db.jpa.model.AttributeTypeToAttributeTypeGroup;
import de.btu.openinfra.backend.db.jpa.model.AttributeValueGeom;
import de.btu.openinfra.backend.db.jpa.model.TopicInstance;
import de.btu.openinfra.backend.db.pojos.AttributeValueGeomPojo;
import de.btu.openinfra.backend.db.pojos.AttributeValueGeomType;

/**
 * This class represents the AttributeValueGeom and is used to access the 
 * underlying layer generated by JPA.
 * 
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class AttributeValueGeomDao 
	extends OpenInfraDao<AttributeValueGeomPojo, AttributeValueGeom> {
	
	/**
	 * This variable defines the default geometry type. The default type is
	 * used to implement the default read methods {@see OpenInfraDao}.
	 */
	// TODO delete this
	private AttributeValueGeomType defaultGeomType = 
			AttributeValueGeomType.TEXT;
	
    /**
     * This variable defines the name of the data type which is specified in the 
     * database.
     */
    public static final String DATA_TYPE_NAME = "geometry(Geometry)";

	/**
	 * This is the required constructor which calls the super constructor and in 
	 * turn creates the corresponding entity manager.
	 * 
	 * @param currentProjectId the current project id (this should be null when 
	 *                         the system schema is selected)
	 * @param schema           the required schema
	 */
	public AttributeValueGeomDao(
			UUID currentProjectId,
			OpenInfraSchemas schema) {
		super(currentProjectId, schema, AttributeValueGeom.class);
	}
	
	/**
	 * This is the required constructor which calls the super constructor and in 
	 * turn creates the corresponding entity manager.
	 * 
	 * @param currentProjectId the current project id (this should be null when 
	 *                         the system schema is selected)
	 * @param schema           the required schema
	 * @param geomType         the required geom type
	 */
	public AttributeValueGeomDao(
			UUID currentProjectId,
			OpenInfraSchemas schema,
			AttributeValueGeomType geomType) {
		super(currentProjectId, schema, AttributeValueGeom.class);
		if(geomType != null) {
			defaultGeomType = geomType;
		} // end if
	}
    
	@Override
	public AttributeValueGeomPojo mapToPojo(
			Locale locale,
			AttributeValueGeom modelObject) {
	    // get the geometry object with the fitting geometry type
		Query qGeom = em.createNativeQuery(String.format(
				AttributeValueDao.GEOM_CLAUSE, 
				defaultGeomType.name(), 
				""));
		qGeom.setParameter(1, modelObject.getId());
		AttributeValueGeomPojo avgPojo = new AttributeValueGeomPojo();
		avgPojo.setGeom(qGeom.getResultList().get(0).toString());
		avgPojo.setGeomType(defaultGeomType);
		
		// get the original geometry object for retrieving
		AttributeValueGeom avg = em.find(
		        AttributeValueGeom.class, modelObject.getId());
		
		avgPojo.setAttributeTypeToAttributeTypeGroupId(
		        avg.getAttributeTypeToAttributeTypeGroup().getId());
		avgPojo.setTopicInstanceId(avg.getTopicInstance().getId());
		
		return avgPojo;
	}

	@Override
	public MappingResult<AttributeValueGeom> mapToModel(
			AttributeValueGeomPojo pojo,
			AttributeValueGeom avg) {
	    
	    // create a mapping result object
        MappingResult<AttributeValueGeom> mr = 
                new MappingResult<AttributeValueGeom>();
        
        // set the attribute type to attribute type group id
        avg.setAttributeTypeToAttributeTypeGroup(em.find(
                AttributeTypeToAttributeTypeGroup.class, 
                pojo.getAttributeTypeToAttributeTypeGroupId()));
        
        // set the geometry value
        avg.setGeom(pojo.getGeom());
        
        // set the topic instance object
        avg.setTopicInstance(
                em.find(TopicInstance.class, pojo.getTopicInstanceId()));
        
        // add the stuff to the mapping result
        mr.setId(avg.getId());
        mr.setModelObject(avg);
        return mr;
	}

}
