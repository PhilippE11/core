package de.btu.openinfra.backend.db.rbac;

import java.util.UUID;

import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.daos.AttributeTypeGroupToAttributeTypeDao;
import de.btu.openinfra.backend.db.jpa.model.AttributeType;
import de.btu.openinfra.backend.db.jpa.model.AttributeTypeGroup;
import de.btu.openinfra.backend.db.jpa.model.AttributeTypeToAttributeTypeGroup;
import de.btu.openinfra.backend.db.pojos.AttributeTypeGroupToAttributeTypePojo;

/**
 * This class represents the AttributeTypeGroupToAttributeType and is used to
 * access the underlying layer generated by JPA.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class AttributeTypeGroupToAttributeTypeRbac extends
	OpenInfraValueValueRbac<AttributeTypeGroupToAttributeTypePojo,
	AttributeTypeToAttributeTypeGroup, AttributeType, AttributeTypeGroup, 
	AttributeTypeGroupToAttributeTypeDao> {

	public AttributeTypeGroupToAttributeTypeRbac(
			UUID currentProjectId,
			OpenInfraSchemas schema) {
		super(currentProjectId, schema, 
				AttributeType.class, AttributeTypeGroup.class, 
				AttributeTypeGroupToAttributeTypeDao.class);
	}
}
