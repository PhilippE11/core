package de.btu.openinfra.backend.db.rbac.file;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;

import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.daos.file.FilesProjectDao;
import de.btu.openinfra.backend.db.jpa.model.file.File;
import de.btu.openinfra.backend.db.jpa.model.file.FilesProject;
import de.btu.openinfra.backend.db.pojos.file.FilesProjectPojo;
import de.btu.openinfra.backend.db.rbac.OpenInfraHttpMethod;
import de.btu.openinfra.backend.db.rbac.OpenInfraValueRbac;

public class FilesProjectRbac extends OpenInfraValueRbac<
FilesProjectPojo, FilesProject, File, FilesProjectDao> {

	public FilesProjectRbac() {
		super(null, OpenInfraSchemas.FILES, File.class, FilesProjectDao.class);
	}

	public List<FilesProjectPojo> readByProject(
			OpenInfraHttpMethod httpMethod,
			UriInfo uriInfo,
			UUID project) {
		checkPermission(httpMethod, uriInfo);
		return new FilesProjectDao().readByProject(project);
	}

}
