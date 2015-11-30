package de.btu.openinfra.backend.db.daos.file;

import java.util.Locale;
import java.util.UUID;

import de.btu.openinfra.backend.db.MappingResult;
import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.daos.OpenInfraDao;
import de.btu.openinfra.backend.db.jpa.model.file.SupportedMimeType;
import de.btu.openinfra.backend.db.pojos.file.SupportedMimeTypePojo;

public class SupportedMimeTypeDao extends
	OpenInfraDao<SupportedMimeTypePojo, SupportedMimeType> {

	public SupportedMimeTypeDao() {
		super(null, OpenInfraSchemas.FILES, SupportedMimeType.class);
	}

	public SupportedMimeTypeDao(UUID currentProject, OpenInfraSchemas schema) {
		super(currentProject, schema, SupportedMimeType.class);
	}

	@Override
	public SupportedMimeTypePojo mapToPojo(Locale locale,
			SupportedMimeType modelObject) {
		SupportedMimeTypePojo pojo = new SupportedMimeTypePojo(modelObject);
		pojo.setMimeType(modelObject.getMimeType());
		return pojo;
	}

	@Override
	public MappingResult<SupportedMimeType> mapToModel(
			SupportedMimeTypePojo pojoObject,
			SupportedMimeType modelObject) {
		modelObject.setMimeType(pojoObject.getMimeType());
		return new MappingResult<SupportedMimeType>(
				modelObject.getId(), modelObject);
	}

}
