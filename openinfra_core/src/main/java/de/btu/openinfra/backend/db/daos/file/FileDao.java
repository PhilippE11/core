package de.btu.openinfra.backend.db.daos.file;

import java.util.Locale;
import java.util.UUID;

import de.btu.openinfra.backend.OpenInfraTime;
import de.btu.openinfra.backend.db.MappingResult;
import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.daos.OpenInfraDao;
import de.btu.openinfra.backend.db.jpa.model.file.File;
import de.btu.openinfra.backend.db.pojos.file.FilePojo;

public class FileDao extends OpenInfraDao<FilePojo, File> {

	public FileDao() {
		super(null, OpenInfraSchemas.FILE, File.class);
	}

	public FileDao(UUID currentProject, OpenInfraSchemas schema) {
		super(currentProject, schema, File.class);
	}

	@Override
	public FilePojo mapToPojo(Locale locale, File modelObject) {
		FilePojo pojo = new FilePojo(modelObject);
		pojo.setMimeType(modelObject.getMimeType());
		pojo.setOriginFileName(modelObject.getOriginFileName());
		pojo.setSubject(modelObject.getSubject());
		pojo.setUploadedOn(OpenInfraTime.format(modelObject.getUploadedOn()));
		pojo.setExifData(modelObject.getExifData());
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
