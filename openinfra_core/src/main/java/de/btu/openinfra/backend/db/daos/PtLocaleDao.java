package de.btu.openinfra.backend.db.daos;

import java.util.Locale;
import java.util.UUID;

import de.btu.openinfra.backend.db.jpa.model.CountryCode;
import de.btu.openinfra.backend.db.jpa.model.LanguageCode;
import de.btu.openinfra.backend.db.jpa.model.PtLocale;
import de.btu.openinfra.backend.db.pojos.PtLocalePojo;

/**
 * This class represents the PtLocale and is used to access the underlying
 * layer generated by JPA.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class PtLocaleDao extends OpenInfraDao<PtLocalePojo, PtLocale> {

	/**
	 * This is the required constructor which calls the super constructor and in
	 * turn creates the corresponding entity manager.
	 *
	 * @param currentProjectId the current project id (this should be null when
	 *                         the system schema is selected)
	 * @param schema           the required schema
	 */
	public PtLocaleDao(UUID currentProjectId, OpenInfraSchemas schema) {
		super(currentProjectId, schema, PtLocale.class);
	}

	@Override
	public PtLocalePojo mapToPojo(Locale locale, PtLocale ptLocale) {
		return mapToPojoStatically(locale, ptLocale);
	}

	/**
	 * This method reads a PtLocale object from database using a specific locale
	 * object.
	 *
	 * @param locale the locale object
	 * @return       a PtLocale object
	 */
	public PtLocale read(Locale locale) {
	    if (locale == null) {
	    	return null;
	    }
		// 1. Get country code object
	    CountryCode cc = null;
	    if (locale.getCountry() != null && !locale.getCountry().equals("")) {
    		 cc = new CountryCodeDao(
    				currentProjectId,
    				schema).read(locale.getCountry());
	    }
		// 2. Get language code object
		LanguageCode lc = new LanguageCodeDao(
				currentProjectId,
				schema).read(locale.getLanguage());
        // 3. Put things together and return the PtLocale object
		PtLocale ptl = null;
		if(cc == null) {
		    ptl = em.createNamedQuery(
	                "PtLocale.xx",
	                PtLocale.class)
	                .setParameter("languageCode", lc)
	                .getSingleResult();
		} else {
		    ptl = em.createNamedQuery(
	                "PtLocale.findByLocale",
	                PtLocale.class)
	                .setParameter("countryCode", cc)
	                .setParameter("languageCode", lc)
	                .getSingleResult();
		}
		return ptl;
	}

	/**
	 * This method implements the method mapToPojo in a static way.
	 *
	 * @param locale the requested language as Java.util locale
	 * @param avv    the model object
	 * @return       the POJO object
	 */
	public static PtLocalePojo mapToPojoStatically(
			Locale locale,
			PtLocale ptLocale) {
		if(ptLocale != null) {
			PtLocalePojo ptLocalePojo = new PtLocalePojo();
			ptLocalePojo.setCharacterCode(
					ptLocale.getCharacterCode().getCharacterCode());
			ptLocalePojo.setLanguageCode(
					ptLocale.getLanguageCode().getLanguageCode());
			// Null values are allowed for column: country code
			if(ptLocale.getCountryCode() != null) {
				ptLocalePojo.setCountryCode(
						ptLocale.getCountryCode().getCountryCode());
			}
			ptLocalePojo.setUuid(ptLocale.getId());
			return ptLocalePojo;
		} else {
			return null;
		} // end if else
	}

	@Override
	public MappingResult<PtLocale> mapToModel(PtLocalePojo pojo, PtLocale ptl) {

        // return null if the pojo is null
        if (pojo != null) {

            // TODO set the model values

            // return the model as mapping result
            return new MappingResult<PtLocale>(ptl.getId(), ptl);
        } else {
            return null;
        }
	}

	/**
	 * This mehtod is a wrapper method of the 'forLanguageTag' of the
	 * Java.util Locale object. It returns null when the locale string
	 * is null.
	 *
	 * @param locale the locale which should be parsed
	 * @return       the locale object or null
	 */
	public static Locale forLanguageTag(String locale) {
		if(locale != null) {
			return Locale.forLanguageTag(locale);
		} else {
			return null;
		} // end if
	}

}
