package de.btu.openinfra.backend.db.daos;

import java.util.Locale;
import java.util.UUID;

import de.btu.openinfra.backend.db.MappingResult;
import de.btu.openinfra.backend.db.jpa.model.CountryCode;
import de.btu.openinfra.backend.db.pojos.CountryCodePojo;

/**
 * This class represents the CountryCode and is used to access the underlying
 * layer generated by JPA.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class CountryCodeDao
	extends OpenInfraDao<CountryCodePojo, CountryCode> {

	/**
	 * This is the required constructor which calls the super constructor and in
	 * turn creates the corresponding entity manager.
	 *
	 * @param currentProjectId the current project id (this should be null when
	 *                         the system schema is selected)
	 * @param schema           the required schema
	 */
	public CountryCodeDao(UUID currentProjectId, OpenInfraSchemas schema) {
		super(currentProjectId, schema, CountryCode.class);
	}

	@Override
	public CountryCodePojo mapToPojo(Locale locale, CountryCode cc) {
		return mapToPojoStatically(cc);
	}

	/**
	 * This method implements the method mapToPojo in a static way.
	 *
	 * @param cc     the model object
	 * @return       the POJO object when the model object is not null else null
	 */
	public static CountryCodePojo mapToPojoStatically(CountryCode cc) {
		CountryCodePojo pojo = new CountryCodePojo();
		pojo.setCountryCode(cc.getCountryCode());
		pojo.setUuid(cc.getId());
		pojo.setTrid(cc.getXmin());
		return pojo;
	}

	/**
	 * This method reads a country code object using a string object.
	 *
	 * @param country the county code as string
	 * @return        the country code object
	 */
	protected CountryCode read(String country) {
		return em.createNamedQuery(
				"CountryCode.findByString",
				CountryCode.class)
				.setParameter("value", country)
				.getSingleResult();
	}

	@Override
	public MappingResult<CountryCode> mapToModel(
			CountryCodePojo pojo,
			CountryCode cc) {

        // TODO set the model values

        // return the model as mapping result
        return new MappingResult<CountryCode>(cc.getId(), cc);
	}

}
