package de.btu.openinfra.backend.db.rbac.file;

import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.daos.file.SupportedMimeTypeDao;
import de.btu.openinfra.backend.db.jpa.model.file.SupportedMimeType;
import de.btu.openinfra.backend.db.pojos.file.SupportedMimeTypePojo;
import de.btu.openinfra.backend.db.rbac.OpenInfraRbac;

public class SupportedMimeTypeRbac extends OpenInfraRbac<
	SupportedMimeTypePojo, SupportedMimeType, SupportedMimeTypeDao> {

	public SupportedMimeTypeRbac() {
		super(null, OpenInfraSchemas.FILES, SupportedMimeTypeDao.class);
	}

}
