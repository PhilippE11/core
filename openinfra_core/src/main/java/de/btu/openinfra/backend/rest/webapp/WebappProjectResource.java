package de.btu.openinfra.backend.rest.webapp;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.btu.openinfra.backend.db.daos.webapp.WebappProjectDao;
import de.btu.openinfra.backend.db.pojos.webapp.WebappProjectPojo;
import de.btu.openinfra.backend.rest.OpenInfraResponseBuilder;

@Path("/v1/webapp/{webappId}/projects")
@Produces({MediaType.APPLICATION_JSON + OpenInfraResponseBuilder.JSON_PRIORITY
    + OpenInfraResponseBuilder.UTF8_CHARSET,
    MediaType.APPLICATION_XML + OpenInfraResponseBuilder.XML_PRIORITY
    + OpenInfraResponseBuilder.UTF8_CHARSET})
public class WebappProjectResource {

	@GET
	@Path("/{webappProjectId}")
	public WebappProjectPojo get(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request,
			@PathParam("webappProjectId") UUID webappProjectId) {
		return new WebappProjectDao().read(null, webappProjectId);
	}

	@GET
	public List<WebappProjectPojo> get(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request,
			@QueryParam("offset") int offset,
			@QueryParam("size") int size,
			@PathParam("webappId") UUID webappId) {
		return new WebappProjectDao().read(null, webappId, offset, size);
	}

	@PUT
	@Path("/{webappProjectId}")
	public Response put(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request,
			@PathParam("webappProjectId") UUID webappProjectId,
			WebappProjectPojo pojo) {
		return OpenInfraResponseBuilder.putResponse(
				new WebappProjectDao().createOrUpdate(pojo, webappProjectId));
	}

	@POST
	public Response post(
		@Context UriInfo uriInfo,
		@Context HttpServletRequest request,
		@PathParam("webappId") UUID webappId,
		WebappProjectPojo pojo) {
		pojo.setWebapp(webappId);
		return OpenInfraResponseBuilder.postResponse(
				new WebappProjectDao().createOrUpdate(pojo, null));
	}

	@DELETE
	@Path("/{webappProjectId}")
	public Response delete(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request,
			@PathParam("webappProjectId") UUID webappProjectId) {
		return OpenInfraResponseBuilder.deleteResponse(
				new WebappProjectDao().delete(webappProjectId),
				webappProjectId);
	}

}
