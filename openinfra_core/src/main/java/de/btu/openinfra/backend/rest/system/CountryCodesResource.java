package de.btu.openinfra.backend.rest.system;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.btu.openinfra.backend.db.OpenInfraOrderByEnum;
import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.OpenInfraSortOrder;
import de.btu.openinfra.backend.db.daos.PtLocaleDao;
import de.btu.openinfra.backend.db.pojos.CountryCodePojo;
import de.btu.openinfra.backend.db.rbac.CountryCodeRbac;
import de.btu.openinfra.backend.db.rbac.OpenInfraHttpMethod;
import de.btu.openinfra.backend.rest.OpenInfraResponseBuilder;

@Path(OpenInfraResponseBuilder.REST_URI_SYSTEM + "/countrycodes")
@Produces({MediaType.APPLICATION_JSON + OpenInfraResponseBuilder.JSON_PRIORITY
    + OpenInfraResponseBuilder.UTF8_CHARSET,
    MediaType.APPLICATION_XML + OpenInfraResponseBuilder.XML_PRIORITY
    + OpenInfraResponseBuilder.UTF8_CHARSET})
public class CountryCodesResource {

	@GET
	public List<CountryCodePojo> get(
			@Context UriInfo uriInfo,
			@QueryParam("language") String language,
			@QueryParam("sortOrder") OpenInfraSortOrder sortOrder,
			@QueryParam("orderBy") OpenInfraOrderByEnum orderBy,
			@QueryParam("offset") int offset,
			@QueryParam("size") int size) {
		return new CountryCodeRbac(
					null,
					OpenInfraSchemas.SYSTEM).read(
							OpenInfraHttpMethod.GET, 
							uriInfo,
							PtLocaleDao.forLanguageTag(language),
							sortOrder,
							orderBy,
							offset,
							size);
	}

	@GET
	@Path("{countryCodeId}")
	public CountryCodePojo get(
			@Context UriInfo uriInfo,
			@QueryParam("language") String language,
			@PathParam("countryCodeId") UUID countryCodeId) {
		return new CountryCodeRbac(
				null,
				OpenInfraSchemas.SYSTEM).read(
						OpenInfraHttpMethod.GET, 
						uriInfo,
						PtLocaleDao.forLanguageTag(language),
						countryCodeId);
	}

	@GET
    @Path("count")
    @Produces({MediaType.TEXT_PLAIN})
    public long getCountryCodeCount(@Context UriInfo uriInfo) {
        return new CountryCodeRbac(
                null,
                OpenInfraSchemas.SYSTEM).getCount(
                		OpenInfraHttpMethod.GET, 
						uriInfo);
    }

}
