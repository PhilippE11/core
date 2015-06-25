package de.btu.openinfra.backend.db.daos;

import java.util.Locale;
import java.util.UUID;

import de.btu.openinfra.backend.db.jpa.model.RelationshipType;
import de.btu.openinfra.backend.db.jpa.model.TopicCharacteristic;
import de.btu.openinfra.backend.db.pojos.RelationshipTypePojo;

/**
 * This class represents the RelationshipType and is used to access the
 * underlying layer generated by JPA.
 *
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class RelationshipTypeDao extends
	OpenInfraValueDao<RelationshipTypePojo, RelationshipType,
	TopicCharacteristic> {

	/**
	 * This is the required constructor which calls the super constructor and
	 * in turn creates the corresponding entity manager.
	 *
	 * @param currentProjectId the current project id (this should be null when
	 *                         the system schema is selected)
	 * @param schema           the required schema
	 */
	public RelationshipTypeDao(
			UUID currentProjectId,
			OpenInfraSchemas schema) {
		super(
				currentProjectId,
				schema,
				RelationshipType.class,
				TopicCharacteristic.class);
	}

	@Override
	public RelationshipTypePojo mapToPojo(
			Locale locale,
			RelationshipType modelObject) {
		return mapToPojoStatically(locale, modelObject);
	}

	/**
	 * This method implements the method mapToPojo in a static way.
	 *
	 * @param locale the requested language as Java.util locale
	 * @param rt     the model object
	 * @return       the POJO object
	 */
	public static RelationshipTypePojo mapToPojoStatically(
			Locale locale,
			RelationshipType rt) {

		RelationshipTypePojo pojo = new RelationshipTypePojo();
		pojo.setUuid(rt.getId());
		pojo.setRelationshipType(
				ValueListValueDao.mapToPojoStatically(
						locale,
						rt.getValueListValue2()));
		pojo.setDescription(
				ValueListValueDao.mapToPojoStatically(
						locale,
						rt.getValueListValue1()));
		return pojo;
	}

	@Override
	public MappingResult<RelationshipType> mapToModel(
			RelationshipTypePojo pojo,
			RelationshipType rt) {

        // return null if the pojo is null
        if (pojo != null) {

            // TODO set the model values

            // return the model as mapping result
            return new MappingResult<RelationshipType>(rt.getId(), rt);
        } else {
            return null;
        }
	}

}
