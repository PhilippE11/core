package de.btu.openinfra.backend.db.daos.project;

import java.util.Locale;
import java.util.UUID;

import de.btu.openinfra.backend.db.MappingResult;
import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.daos.OpenInfraDao;
import de.btu.openinfra.backend.db.daos.ValueListValueDao;
import de.btu.openinfra.backend.db.jpa.model.AttributeTypeToAttributeTypeGroup;
import de.btu.openinfra.backend.db.jpa.model.AttributeValueDomain;
import de.btu.openinfra.backend.db.jpa.model.TopicInstance;
import de.btu.openinfra.backend.db.jpa.model.ValueListValue;
import de.btu.openinfra.backend.db.pojos.project.AttributeValueDomainPojo;
import de.btu.openinfra.backend.exception.OpenInfraEntityException;
import de.btu.openinfra.backend.exception.OpenInfraExceptionTypes;

/**
 * This class represents the AttributeValueDomain and is used to access the
 * underlying layer generated by JPA.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class AttributeValueDomainDao extends
	OpenInfraDao<AttributeValueDomainPojo, AttributeValueDomain> {

	/**
	 * This is the required constructor which calls the super constructor and in
	 * turn creates the corresponding entity manager.
	 *
	 * @param currentProjectId the current project id (this should be null when
	 *                         the system schema is selected)
	 * @param schema           the required schema
	 */
	public AttributeValueDomainDao(
			UUID currentProjectId,
			OpenInfraSchemas schema) {
		super(currentProjectId, schema, AttributeValueDomain.class);
	}

	@Override
	public AttributeValueDomainPojo mapToPojo(
			Locale locale,
			AttributeValueDomain avd) {
	    if (avd != null) {
    		AttributeValueDomainPojo pojo =
    				new AttributeValueDomainPojo(avd);

    		// set the topic instance id
            pojo.setTopicInstanceId(avd.getTopicInstance().getId());
            // set the value list value of the object
    		pojo.setDomain(new ValueListValueDao(
    		        currentProjectId,
    		        schema).mapToPojo(
    		                locale,
    		                avd.getValueListValue()));
    		// set the attribute type to attribute type id group of the value
            pojo.setAttributeTypeToAttributeTypeGroupId(
                    avd.getAttributeTypeToAttributeTypeGroup().getId());

    		return pojo;
        } else {
            return null;
        }
	}

	@Override
	public MappingResult<AttributeValueDomain> mapToModel(
			AttributeValueDomainPojo pojo,
			AttributeValueDomain avd) {

        try {
            // set value list value
            avd.setValueListValue(em.find(ValueListValue.class,
                    pojo.getDomain().getUuid()));

            // set the attribute type to attribute type group
            avd.setAttributeTypeToAttributeTypeGroup(em.find(
                    AttributeTypeToAttributeTypeGroup.class,
                    pojo.getAttributeTypeToAttributeTypeGroupId()));

            // set the topic instance
            avd.setTopicInstance(
                    em.find(TopicInstance.class, pojo.getTopicInstanceId()));
        } catch (NullPointerException npe) {
            throw new OpenInfraEntityException(
                    OpenInfraExceptionTypes.MISSING_DATA_IN_POJO);
        }

        return new MappingResult<AttributeValueDomain>(avd.getId(), avd);
	}

}