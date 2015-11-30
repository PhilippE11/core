package de.btu.openinfra.backend.db.rbac;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;

import de.btu.openinfra.backend.db.OpenInfraOrderBy;
import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.OpenInfraSortOrder;
import de.btu.openinfra.backend.db.daos.TopicCharacteristicDao;
import de.btu.openinfra.backend.db.jpa.model.Project;
import de.btu.openinfra.backend.db.jpa.model.TopicCharacteristic;
import de.btu.openinfra.backend.db.pojos.TopicCharacteristicPojo;

public class TopicCharacteristicRbac extends OpenInfraValueRbac<
	TopicCharacteristicPojo, TopicCharacteristic, Project,
	TopicCharacteristicDao> {

	public TopicCharacteristicRbac(
			UUID currentProjectId,
			OpenInfraSchemas schema) {
		super(currentProjectId,
				schema,
				Project.class,
				TopicCharacteristicDao.class);
	}

	@Override
	public List<TopicCharacteristicPojo> read(
			OpenInfraHttpMethod httpMethod,
			UriInfo uriInfo, Locale locale, int offset, int size) {
		checkPermission(httpMethod, uriInfo);
		return removeItems(
				super.read(httpMethod, uriInfo, locale, offset, size));
	};

	@Override
	public List<TopicCharacteristicPojo> read(
			OpenInfraHttpMethod httpMethod,
			UriInfo uriInfo, Locale locale,
			OpenInfraSortOrder order, OpenInfraOrderBy column,
			int offset, int size) {
		checkPermission(httpMethod, uriInfo);
		return removeItems(
				super.read(httpMethod, uriInfo, locale,
						order, column, offset, size));
	};

	@Override
	public List<TopicCharacteristicPojo> read(
			OpenInfraHttpMethod httpMethod,
			UriInfo uriInfo, Locale locale, UUID valueId,
			int offset, int size) {
		checkPermission(httpMethod, uriInfo);
		return removeItems(
				super.read(httpMethod, uriInfo, locale, valueId, offset, size));
	};

	@Override
	public List<TopicCharacteristicPojo> read(OpenInfraHttpMethod httpMethod,
			UriInfo uriInfo, Locale locale, UUID valueId,
			OpenInfraSortOrder order, OpenInfraOrderBy column,
			int offset, int size) {
		checkPermission(httpMethod, uriInfo);
		return removeItems(
				super.read(httpMethod, uriInfo, locale, valueId, order,
						column, offset, size));
	};

	public List<TopicCharacteristicPojo> read(
			OpenInfraHttpMethod httpMethod,
			UriInfo uriInfo, Locale locale, String filter) {
		checkPermission(httpMethod, uriInfo);
		return removeItems(new TopicCharacteristicDao(
				currentProjectId,
				schema).read(locale, filter));
	}

	public List<TopicCharacteristicPojo> readByTopicInstanceAssociationTo(
			OpenInfraHttpMethod httpMethod,
			UriInfo uriInfo,
			Locale locale, UUID topicInstance, int offset, int size) {
		checkPermission(httpMethod, uriInfo);
		return removeItems(new TopicCharacteristicDao(
				currentProjectId, schema).readByTopicInstanceAssociationTo(
						locale, topicInstance, offset, size));
	}

	public List<TopicCharacteristicPojo> readByTopicInstanceAssociationFrom(
			OpenInfraHttpMethod httpMethod,
			UriInfo uriInfo,
			Locale locale, UUID topicInstance, int offset, int size) {
		checkPermission(httpMethod, uriInfo);
		return removeItems(new TopicCharacteristicDao(
				currentProjectId, schema)
		.readByTopicInstanceAssociationFrom(locale, topicInstance,
				offset, size));
	}

	/**
	 * This method is used to remove items the current subject doesn't has
	 * access.
	 *
	 * @param pojos
	 * @return
	 */
	private List<TopicCharacteristicPojo> removeItems(
			List<TopicCharacteristicPojo> pojos) {
		Iterator<TopicCharacteristicPojo> it = pojos.iterator();
		while(it.hasNext()) {
			if(!user.isPermitted(
					"/projects/" + currentProjectId +
					"/topiccharacteristics/{id}:r:" + it.next().getUuid())) {
				it.remove();
			}
		}
		return pojos;
	}

	public List<String> getSuggestion(
            UriInfo uriInfo,
            OpenInfraHttpMethod httpMethod,
            Locale locale,
            UUID topicCharacteristicId,
            UUID attributeTypeId,
            String qString) {
        checkPermission(httpMethod, uriInfo);
        return new TopicCharacteristicDao(currentProjectId, schema)
            .getSuggestion(
                    locale,
                    topicCharacteristicId,
                    attributeTypeId,
                    qString);
    }
}
