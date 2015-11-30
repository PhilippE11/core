package de.btu.openinfra.backend.db.daos.file;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import de.btu.openinfra.backend.OpenInfraTime;
import de.btu.openinfra.backend.db.MappingResult;
import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.daos.OpenInfraDao;
import de.btu.openinfra.backend.db.jpa.model.file.File;
import de.btu.openinfra.backend.db.pojos.file.FilePojo;
import de.btu.openinfra.backend.db.pojos.file.FilesProjectPojo;

public class FileDao extends OpenInfraDao<FilePojo, File> {

	public FileDao() {
		super(null, OpenInfraSchemas.FILES, File.class);
	}

	public FileDao(UUID currentProject, OpenInfraSchemas schema) {
		super(currentProject, schema, File.class);
	}

	/**
	 * This method deletes a file from database when the file belongs to the
	 * user which is logged in.
	 *
	 * @param file
	 * @param subject
	 * @return
	 */
	public boolean delete(UUID file, UUID subject) {
		FilePojo pojo = read(null, file);
		if(pojo.getSubject().equals(subject)) {
			return delete(file);
		} else {
			// TODO Better make use of OpenInfraWebException to lead all
			// exceptions through one class.
			throw new WebApplicationException(Status.FORBIDDEN);
		}
	}

	public List<FilePojo> readBySubject(
			Locale locale,
			UUID subject,
			int offset,
			int size) {
		List<File> files = em.createNamedQuery(
				"File.findBySubject", File.class)
				.setFirstResult(offset)
				.setMaxResults(size)
				.setParameter("subject", subject)
				.getResultList();
		List<FilePojo> res = new LinkedList<FilePojo>();
		for(File f : files) {
			res.add(mapToPojo(locale, f));
		}
		return res;
	}

	/**
	 * This method retrieves a list of FilesProject POJOs and uses this list
	 * to retrieve the real File POJOs.
	 *
	 * @param project the project id
	 * @return
	 */
	public List<FilePojo> readByProject(UUID project) {
		List<FilesProjectPojo> fps =
				new FilesProjectDao().readByProject(project);
		List<FilePojo> files = new LinkedList<FilePojo>();
		for(FilesProjectPojo fp : fps) {
			files.add(read(null, fp.getFile()));
		}
		return files;
	}

	public long countBySignature(String signature) {
		Long count = 0L;
		count = em.createNamedQuery(
            "File.countBySignature",
            Long.class).setParameter("signature", signature)
            .getSingleResult().longValue();
		return count;
	}

	public long countBySubject(UUID subject) {
		Long count = 0L;
		count = em.createNamedQuery(
            "File.countBySubject",
            Long.class).setParameter("subject", subject)
            .getSingleResult().longValue();
		return count;
	}

	public long countByProject(UUID projectId) {
		return new FilesProjectDao().countByProject(projectId);
	}

	@Override
	public FilePojo mapToPojo(Locale locale, File modelObject) {
		FilePojo pojo = new FilePojo(modelObject);
		pojo.setMimeType(modelObject.getMimeType());
		pojo.setOriginFileName(modelObject.getOriginFileName());
		pojo.setSubject(modelObject.getSubject());
		pojo.setUploadedOn(OpenInfraTime.format(modelObject.getUploadedOn()));
		pojo.setExifData(modelObject.getExifData());
		pojo.setSignature(modelObject.getSignature());
		pojo.setMiddleDimension(modelObject.getMiddleDimension());
		pojo.setOriginDimension(modelObject.getOriginDimension());
		pojo.setPopupDimension(modelObject.getPopupDimension());
		pojo.setThumbnailDimension(modelObject.getThumbnailDimension());
		return pojo;
	}

	@Override
	public MappingResult<File> mapToModel(
			FilePojo pojoObject, File modelObject) {
		modelObject.setMimeType(pojoObject.getMimeType());
		modelObject.setOriginFileName(pojoObject.getOriginFileName());
		modelObject.setSubject(pojoObject.getSubject());
		modelObject.setUploadedOn(OpenInfraTime.now());
		modelObject.setExifData(pojoObject.getExifData());
		modelObject.setMiddleDimension(pojoObject.getMiddleDimension());
		modelObject.setOriginDimension(pojoObject.getOriginDimension());
		modelObject.setPopupDimension(pojoObject.getPopupDimension());
		modelObject.setSignature(pojoObject.getSignature());
		modelObject.setThumbnailDimension(pojoObject.getThumbnailDimension());
		return new MappingResult<File>(modelObject.getId(), modelObject);
	}

}
