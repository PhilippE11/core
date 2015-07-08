package de.btu.openinfra.backend.rest.system;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.btu.openinfra.backend.db.daos.LanguageCodeDao;
import de.btu.openinfra.backend.db.daos.OpenInfraOrderBy;
import de.btu.openinfra.backend.db.daos.OpenInfraSchemas;
import de.btu.openinfra.backend.db.daos.OpenInfraSortOrder;
import de.btu.openinfra.backend.db.daos.PtLocaleDao;
import de.btu.openinfra.backend.db.pojos.LanguageCodePojo;
import de.btu.openinfra.backend.rest.OpenInfraResponseBuilder;

/**
 * This class represents the LanguageCodes resource of the REST API. 
 * 
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
@Path("/system/languagecodes")
@Produces({MediaType.APPLICATION_JSON + OpenInfraResponseBuilder.JSON_PRIORITY, 
    MediaType.APPLICATION_XML + OpenInfraResponseBuilder.XML_PRIORITY})
public class LanguageCodesResource {
	
	@GET
	public List<LanguageCodePojo> get(
			@QueryParam("language") String language,
			@QueryParam("sortOrder") OpenInfraSortOrder sortOrder,
			@QueryParam("orderBy") OpenInfraOrderBy orderBy,
			@QueryParam("offset") int offset, 
			@QueryParam("size") int size) {
		return new LanguageCodeDao(
				null, 
				OpenInfraSchemas.SYSTEM ).read(
						PtLocaleDao.forLanguageTag(language),
						sortOrder,
						orderBy,
						offset, 
						size);
	}
	
	@GET
	@Path("{languageCodeId}")
	public LanguageCodePojo get(
			@QueryParam("language") String language,
			@PathParam("languageCodeId") UUID languageCodeId) {
		return new LanguageCodeDao(
				null, 
				OpenInfraSchemas.SYSTEM ).read(
						PtLocaleDao.forLanguageTag(language),
						languageCodeId);
	}

}
