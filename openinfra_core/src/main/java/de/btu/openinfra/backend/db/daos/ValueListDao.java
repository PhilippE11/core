package de.btu.openinfra.backend.db.daos;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.btu.openinfra.backend.db.jpa.model.ValueList;
import de.btu.openinfra.backend.db.pojos.LocalizedString;
import de.btu.openinfra.backend.db.pojos.PtFreeTextPojo;
import de.btu.openinfra.backend.db.pojos.ValueListPojo;

/**
 * This class represents the ValueListValue and is used to access the underlying
 * layer generated by JPA.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class ValueListDao extends OpenInfraDao<ValueListPojo, ValueList> {

	/**
	 * This is the required constructor which calls the super constructor and in
	 * turn creates the corresponding entity manager.
	 *
	 * @param currentProjectId the current project id (this should be null when
	 *                         the system schema is selected)
	 * @param schema           the required schema
	 */
	public ValueListDao(UUID currentProjectId, OpenInfraSchemas schema) {
		super(currentProjectId, schema, ValueList.class);
	}

	@Override
	public ValueListPojo mapToPojo(Locale locale, ValueList vl) {
		return mapToPojoStatically(locale, vl);
	}

	public static ValueListPojo mapToPojoStatically(
			Locale locale,
			ValueList vl) {
		if(vl != null) {
			ValueListPojo vlPojo = new ValueListPojo();
			vlPojo.setNames(PtFreeTextDao.mapToPojoStatically(
					locale,
					vl.getPtFreeText2()));
			vlPojo.setDescriptions(PtFreeTextDao.mapToPojoStatically(
					locale,
					vl.getPtFreeText1()));
			vlPojo.setUuid(vl.getId());
			return vlPojo;
		} else {
			return null;
		} // end if else
	}

	@Override
	public MappingResult<ValueList> mapToModel(
			ValueListPojo pojo,
			ValueList vl) {

        // in case the name is empty
        if (pojo.getNames() == null) {
            return null;
        }

        // in case the name value is empty
        if (pojo.getNames().getLocalizedStrings().get(0)
                .getCharacterString().equals("")) {
            return null;
        }

    	PtFreeTextDao ptfDao =
    			new PtFreeTextDao(currentProjectId, schema);
        // set the description (is optional)
        if (pojo.getDescriptions() != null) {
            vl.setPtFreeText1(
            		ptfDao.getPtFreeTextModel(pojo.getDescriptions()));
        }

        // set the name
        vl.setPtFreeText2(ptfDao.getPtFreeTextModel(pojo.getNames()));

        // return the model as mapping result
        return new MappingResult<ValueList>(vl.getId(), vl);
	}

	/**
     * This method creates a ValueListPojo shell that contains some
     * informations about the name, the description and the locale.
     *
     * @param locale the locale the informations should be saved at
     * @return       the ValueListPojo
     */
	public ValueListPojo newValueList(Locale locale) {
	    // create the return pojo
        ValueListPojo pojo = new ValueListPojo();

        PtLocaleDao ptl = new PtLocaleDao(currentProjectId, schema);
        List<LocalizedString> lcs = new LinkedList<LocalizedString>();
        LocalizedString ls = new LocalizedString();

        // set an empty character string
        ls.setCharacterString("");

        // set the locale of the character string
        ls.setLocale(PtLocaleDao.mapToPojoStatically(
                locale,
                ptl.read(locale)));
        lcs.add(ls);

        // add the localized string for the name
        pojo.setNames(new PtFreeTextPojo(lcs, null));

        // add the localized string for the description
        pojo.setDescriptions(new PtFreeTextPojo(lcs, null));

        return pojo;
	}
}
