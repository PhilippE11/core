package de.btu.openinfra.backend.db.rbac.file;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;

import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.daos.file.FileDao;
import de.btu.openinfra.backend.db.jpa.model.file.File;
import de.btu.openinfra.backend.db.pojos.file.FilePojo;
import de.btu.openinfra.backend.db.rbac.OpenInfraHttpMethod;
import de.btu.openinfra.backend.db.rbac.OpenInfraRbac;
import de.btu.openinfra.backend.rest.rbac.SubjectResource;

public class FileRbac extends OpenInfraRbac<FilePojo, File, FileDao> {

	public FileRbac() {
		super(null, OpenInfraSchemas.FILES, FileDao.class);
	}

	public long getFilesCountBySubject(
			OpenInfraHttpMethod httpMethod,
			UriInfo uriInfo) {
		checkPermission(httpMethod, uriInfo);
		return new FileDao().countBySubject(
				new SubjectResource().self().getUuid());
	}

	public List<FilePojo> readBySubject(
			OpenInfraHttpMethod httpMethod,
			UriInfo uriInfo,
			int offset,
			int size) {
		checkPermission(httpMethod, uriInfo);
		return new FileDao().readBySubject(
				null, self().getUuid(), offset, size);
	}

	public List<FilePojo> readByProject(
			OpenInfraHttpMethod httpMethod,
			UriInfo uriInfo,
			UUID projectId) {
		checkPermission(httpMethod, uriInfo);
		return new FileDao().readByProject(projectId);
	}

}
