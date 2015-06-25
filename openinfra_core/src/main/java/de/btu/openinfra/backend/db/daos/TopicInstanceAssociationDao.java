package de.btu.openinfra.backend.db.daos;

import java.util.Locale;
import java.util.UUID;

import de.btu.openinfra.backend.db.jpa.model.TopicInstance;
import de.btu.openinfra.backend.db.jpa.model.TopicInstanceXTopicInstance;
import de.btu.openinfra.backend.db.pojos.TopicInstanceAssociationPojo;

/**
 * This class represents the TopicInstanceAssociation and is used to access the
 * underlying layer generated by JPA.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class TopicInstanceAssociationDao extends OpenInfraValueDao<
	TopicInstanceAssociationPojo,
	TopicInstanceXTopicInstance,
	TopicInstance> {

	/**
	 * This is the required constructor which calls the super constructor and
	 * in turn creates the corresponding entity manager.
	 *
	 * @param currentProjectId the current project id (this should be null when
	 *                         the system schema is selected)
	 * @param schema           the required schema
	 */
	public TopicInstanceAssociationDao(
			UUID currentProjectId,
			OpenInfraSchemas schema) {
		super(
				currentProjectId,
				schema,
				TopicInstanceXTopicInstance.class,
				TopicInstance.class);
	}

	@Override
	public TopicInstanceAssociationPojo mapToPojo(
			Locale locale,
			TopicInstanceXTopicInstance txt) {
		TopicInstanceDao tiDao = new TopicInstanceDao(currentProjectId, schema);
		TopicInstanceAssociationPojo pojo = new TopicInstanceAssociationPojo();
		pojo.setUuid(txt.getId());
		pojo.setRelationshipType(
				RelationshipTypeDao.mapToPojoStatically(
						locale,
						txt.getRelationshipType()));
		pojo.setAssociatedInstance(
				tiDao.mapToPojo(locale, txt.getTopicInstance2Bean()));
		return pojo;
	}

	@Override
	public MappingResult<TopicInstanceXTopicInstance> mapToModel(
			TopicInstanceAssociationPojo pojo,
			TopicInstanceXTopicInstance tixti) {

        // return null if the pojo is null
        if (pojo != null) {

            // TODO set the model values

            // return the model as mapping result
            return new MappingResult<TopicInstanceXTopicInstance>(
                    tixti.getId(), tixti);
        } else {
            return null;
        }
	}

}
