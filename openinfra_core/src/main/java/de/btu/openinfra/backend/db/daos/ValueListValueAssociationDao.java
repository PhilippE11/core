package de.btu.openinfra.backend.db.daos;

import java.util.Locale;
import java.util.UUID;

import de.btu.openinfra.backend.db.jpa.model.ValueListValue;
import de.btu.openinfra.backend.db.jpa.model.ValueListValuesXValueListValue;
import de.btu.openinfra.backend.db.pojos.ValueListValueAssociationPojo;

/**
 * This class represents the ValueListValueAssociation and is used to access the
 * underlying layer generated by JPA.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class ValueListValueAssociationDao
	extends OpenInfraValueValueDao<ValueListValueAssociationPojo,
	ValueListValuesXValueListValue, ValueListValue, ValueListValue>{

    /**
     * This is the required constructor which calls the super constructor and in
     * turn creates the corresponding entity manager.
     *
     * @param currentProjectId the current project id (this should be null when
     *                         the system schema is selected)
     * @param schema           the required schema
     */
	public ValueListValueAssociationDao(
			UUID currentProjectId,
			OpenInfraSchemas schema) {
		super(currentProjectId, schema, ValueListValuesXValueListValue.class,
				ValueListValue.class, ValueListValue.class);
	}

	@Override
	public ValueListValueAssociationPojo mapToPojo(
			Locale locale,
			ValueListValuesXValueListValue association) {
		return mapToPojoStatically(locale, association);
	}

	@Override
	public MappingResult<ValueListValuesXValueListValue> mapToModel(
			ValueListValueAssociationPojo pojo,
			ValueListValuesXValueListValue vlvxvlv) {

        // TODO set the model values

        // return the model as mapping result
        return new MappingResult<ValueListValuesXValueListValue>(
                vlvxvlv.getId(), vlvxvlv);
	}

	/**
     * This method implements the method mapToPojo in a static way.
     *
     * @param locale       the requested language as Java.util locale
     * @param association  the model object
     * @return             the POJO object when the model object is not null
     *                     else null
     */
	public static ValueListValueAssociationPojo mapToPojoStatically(
			Locale locale,
			ValueListValuesXValueListValue association) {

		if(association != null) {
			ValueListValueAssociationPojo pojo =
					new ValueListValueAssociationPojo();

			pojo.setUuid(association.getId());
			pojo.setRelationship(ValueListValueDao.mapToPojoStatically(locale,
					association.getValueListValue1()));
			pojo.setAssociatedValueListValue(
					ValueListValueDao.mapToPojoStatically(
							locale, association.getValueListValue3()));

			return pojo;
		}
		else {
			return null;
		}
	}
}
