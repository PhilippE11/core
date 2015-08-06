package de.btu.openinfra.backend.db.daos;

import java.util.Locale;
import java.util.UUID;

import de.btu.openinfra.backend.db.jpa.model.CharacterCode;
import de.btu.openinfra.backend.db.pojos.CharacterCodePojo;

/**
 * This class represents the CharacterCode and is used to access the underlying
 * layer generated by JPA.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class CharacterCodeDao
	extends OpenInfraDao<CharacterCodePojo, CharacterCode> {

	/**
	 * This is the required constructor which calls the super constructor and in
	 * turn creates the corresponding entity manager.
	 *
	 * @param currentProjectId the current project id (this should be null when
	 *                         the system schema is selected)
	 * @param schema           the required schema
	 */
	public CharacterCodeDao(UUID currentProjectId, OpenInfraSchemas schema) {
		super(currentProjectId, schema, CharacterCode.class);
	}

	@Override
	public CharacterCodePojo mapToPojo(Locale locale, CharacterCode cc) {
		return mapToPojoStatically(cc);
	}

	/**
	 * This method implements the method mapToPojo in a static way.
	 *
	 * @param cc     the model object
	 * @return       the POJO object when the model object is not null else null
	 */
	public CharacterCodePojo mapToPojoStatically(CharacterCode cc) {
		CharacterCodePojo pojo = new CharacterCodePojo();
		pojo.setUuid(cc.getId());
		pojo.setCharacterCode(cc.getCharacterCode());
		return pojo;
	}

	@Override
	public MappingResult<CharacterCode> mapToModel(
			CharacterCodePojo pojo, CharacterCode cc) {

        // TODO set the model values

        // return the model as mapping result
        return new MappingResult<CharacterCode>(cc.getId(), cc);
	}

}
