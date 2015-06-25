package de.btu.openinfra.backend.db.daos;

import java.util.Locale;
import java.util.UUID;

import de.btu.openinfra.backend.db.jpa.model.AttributeType;
import de.btu.openinfra.backend.db.jpa.model.AttributeTypeGroup;
import de.btu.openinfra.backend.db.jpa.model.PtLocale;
import de.btu.openinfra.backend.db.pojos.AttributeTypePojo;
import de.btu.openinfra.backend.db.pojos.AttributeValueTypes;

/**
 * This class represents the AttributeType and is used to access the underlying
 * layer generated by JPA.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class AttributeTypeDao
	extends OpenInfraValueDao<AttributeTypePojo, AttributeType,
	AttributeTypeGroup> {

	private static final String GEOMETRY_GEOM = "geometry(Geometry)";
	private static final String GEOMETRY_GEOMZ = "geometry(GeometryZ)";

	/**
	 * This is the required constructor which calls the super constructor and in
	 * turn creates the corresponding entity manager.
	 *
	 * @param currentProjectId the current project id (this should be null when
	 *                         the system schema is selected)
	 * @param schema           the required schema
	 */
	public AttributeTypeDao(UUID currentProjectId, OpenInfraSchemas schema) {
		super(currentProjectId, schema, AttributeType.class,
				AttributeTypeGroup.class);
	}

	@Override
	public AttributeTypePojo mapToPojo(Locale locale, AttributeType at) {
		return mapToPojoStatically(locale, at);
	}

	/**
	 * This method implements the method mapToPojo in a static way.
	 *
	 * @param locale the requested language as Java.util locale
	 * @param at     the model object
	 * @return       the POJO object when the model object is not null else null
	 */
	public static AttributeTypePojo mapToPojoStatically(
			Locale locale,
			AttributeType at) {
		if(at != null) {
			AttributeTypePojo pojo = new AttributeTypePojo();
			pojo.setDomain(ValueListDao.mapToPojoStatically(
					locale,
					at.getValueList()));
			pojo.setUnit(ValueListValueDao.mapToPojoStatically(
					locale,
					at.getValueListValue2()));
			pojo.setDataType(ValueListValueDao.mapToPojoStatically(
					locale,
					at.getValueListValue1()));
			pojo.setDescriptions(DescriptionDao.mapToPojoStatically(
					locale,
					at.getPtFreeText1()));
			pojo.setNames(NameDao.mapToPojoStatically(
					locale,
					at.getPtFreeText2()));
			pojo.setUuid(at.getId());

			// This part is used to specify the awaited attribute value type.
			if(pojo.getDomain() != null) {
				pojo.setType(AttributeValueTypes.ATTRIBUTE_VALUE_DOMAIN);
			} else {
			    String dataType = pojo.getDataType().getNames()
			            .getLocalizedStrings().get(0).getCharacterString();
				if(pojo.getDataType() != null &&
				        dataType.equals(GEOMETRY_GEOMZ)) {
					pojo.setType(AttributeValueTypes.ATTRIBUTE_VALUE_GEOMZ);
				} else if(pojo.getDataType() != null &&
				        dataType.equals(GEOMETRY_GEOM)) {
					pojo.setType(AttributeValueTypes.ATTRIBUTE_VALUE_GEOM);
				} else if(pojo.getDataType() != null) {
					pojo.setType(AttributeValueTypes.ATTRIBUTE_VALUE_VALUE);
				} else {
					System.out.println("hier: " + pojo.getDataType());
				}
			} // end if else
			return pojo;
		} else {
			return null;
		} // end if else
	}

	/**
	 * This is a special method which returns a specific attribute type
	 * defined by name of the required data type.
	 *
	 * @param locale   the locale
	 * @param dataType the required data type
	 * @return         an attribute type pojo object
	 */
	public AttributeTypePojo read(Locale locale, String dataType) {

	    PtLocale pl = new PtLocaleDao(currentProjectId, schema).read(locale);
	    return AttributeTypeDao.mapToPojoStatically(
	            locale,
	            em.createNamedQuery(
	                    "AttributeType.findByDataType",
	                    AttributeType.class)
	                    .setParameter("ptl", pl)
	                    .setParameter("dataType", dataType)
	                    .getSingleResult());
	}

	@Override
	public MappingResult<AttributeType> mapToModel(
			AttributeTypePojo pojo,
			AttributeType at) {

	    // return null if the pojo is null
        if (pojo != null) {

            // TODO set the model values

            // return the model as mapping result
            return new MappingResult<AttributeType>(at.getId(), at);
        } else {
            return null;
        }
	}

}
