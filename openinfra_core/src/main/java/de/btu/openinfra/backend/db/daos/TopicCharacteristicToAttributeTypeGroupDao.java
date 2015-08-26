package de.btu.openinfra.backend.db.daos;

import java.util.Locale;
import java.util.UUID;

import de.btu.openinfra.backend.db.MappingResult;
import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.jpa.model.AttributeTypeGroup;
import de.btu.openinfra.backend.db.jpa.model.AttributeTypeGroupToTopicCharacteristic;
import de.btu.openinfra.backend.db.jpa.model.TopicCharacteristic;
import de.btu.openinfra.backend.db.pojos.TopicCharacteristicToAttributeTypeGroupPojo;

/**
 * This class represents the TopicCharacteristicToAttributeTypeGroup and is used
 * to access the underlying layer generated by JPA.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class TopicCharacteristicToAttributeTypeGroupDao extends
	OpenInfraValueValueDao<TopicCharacteristicToAttributeTypeGroupPojo,
	AttributeTypeGroupToTopicCharacteristic, AttributeTypeGroup,
	TopicCharacteristic> {

	/**
     * This is the required constructor which calls the super constructor,
     * creates the corresponding entity manager and initiate
     * topicCharacteristicDao.
     *
     * @param currentProjectId the current project id (this should be null when
     *                         the system schema is selected)
     * @param schema           the required schema
     */
	public TopicCharacteristicToAttributeTypeGroupDao(
			UUID currentProjectId,
			OpenInfraSchemas schema) {
		super(currentProjectId, schema,
				AttributeTypeGroupToTopicCharacteristic.class,
				AttributeTypeGroup.class, TopicCharacteristic.class);
	}

	@Override
	public TopicCharacteristicToAttributeTypeGroupPojo mapToPojo(
			Locale locale,
			AttributeTypeGroupToTopicCharacteristic atgttc) {
        return mapToPojoStatically(locale, atgttc,
                new MetaDataDao(currentProjectId, schema));
	}

	/**
     * This method implements the method mapToPojo in a static way.
     *
     * @param locale the requested language as Java.util locale
     * @param atgttc the model object
     * @param mdDao  the meta data DAO
     * @return       the POJO object when the model object is not null else null
     */
    public static TopicCharacteristicToAttributeTypeGroupPojo
        mapToPojoStatically(
                Locale locale,
                AttributeTypeGroupToTopicCharacteristic atgttc,
                MetaDataDao mdDao) {
        if (atgttc != null) {
            TopicCharacteristicToAttributeTypeGroupPojo pojo =
                    new TopicCharacteristicToAttributeTypeGroupPojo(
                            atgttc, mdDao);

            pojo.setTopicCharacteristic(
                    TopicCharacteristicDao.mapToPojoStatically(
                            locale,
                            atgttc.getTopicCharacteristic(), mdDao));
            pojo.setAttributTypeGroupId(
                    atgttc.getAttributeTypeGroup().getId());
            pojo.setMultiplicity(MultiplicityDao.mapToPojoStatically(
                    atgttc.getMultiplicityBean(), mdDao));

            if(atgttc.getOrder() != null) {
                pojo.setOrder(atgttc.getOrder());
            }

            return pojo;
        } else {
            return null;
        }
    }
	@Override
	public MappingResult<AttributeTypeGroupToTopicCharacteristic> mapToModel(
			TopicCharacteristicToAttributeTypeGroupPojo pojo,
			AttributeTypeGroupToTopicCharacteristic att) {

        // TODO set the model values

        // return the model as mapping result
        return new MappingResult<AttributeTypeGroupToTopicCharacteristic>(
                att.getId(), att);
	}

}
