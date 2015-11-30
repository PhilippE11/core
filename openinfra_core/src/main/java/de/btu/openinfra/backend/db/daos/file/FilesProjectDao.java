package de.btu.openinfra.backend.db.daos.file;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.btu.openinfra.backend.db.MappingResult;
import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.daos.OpenInfraValueDao;
import de.btu.openinfra.backend.db.jpa.model.file.File;
import de.btu.openinfra.backend.db.jpa.model.file.FilesProject;
import de.btu.openinfra.backend.db.pojos.file.FilesProjectPojo;

public class FilesProjectDao extends
	OpenInfraValueDao<FilesProjectPojo, FilesProject, File> {

	public FilesProjectDao(UUID currentProject, OpenInfraSchemas schema) {
		super(currentProject, schema, FilesProject.class, File.class);
	}

	public FilesProjectDao() {
		super(null, OpenInfraSchemas.FILES, FilesProject.class, File.class);
	}

	public List<FilesProjectPojo> readByProject(UUID project) {
		return readByValue("FilesProject.findByProject", project);
	}

	public long countByProject(UUID projectId) {
		Long count = 0L;
		count = em.createNamedQuery(
            "FilesProject.countByProject",
            Long.class).setParameter("value", projectId)
            .getSingleResult().longValue();
		return count;
	}

	private List<FilesProjectPojo> readByValue(String namedQuery, UUID value) {
		List<FilesProject> fps =
				em.createNamedQuery(namedQuery,
						FilesProject.class).setParameter("value", value)
						.getResultList();
		List<FilesProjectPojo> pojos = new LinkedList<FilesProjectPojo>();
		for(FilesProject fp : fps) {
			pojos.add(mapToPojo(null, fp));
		}
		return pojos;
	}

	@Override
	public FilesProjectPojo mapToPojo(Locale locale, FilesProject modelObject) {
		FilesProjectPojo pojo = new FilesProjectPojo(modelObject);
		pojo.setProject(modelObject.getProjectId());
		pojo.setFile(modelObject.getFile().getId());
		return pojo;
	}

	@Override
	public MappingResult<FilesProject> mapToModel(FilesProjectPojo pojoObject,
			FilesProject modelObject) {
		modelObject.setFile(em.find(File.class, pojoObject.getFile()));
		modelObject.setProjectId(pojoObject.getProject());
		return new MappingResult<FilesProject>(
				modelObject.getId(), modelObject);
	}

}
