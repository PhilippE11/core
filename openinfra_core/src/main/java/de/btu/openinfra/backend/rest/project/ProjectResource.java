package de.btu.openinfra.backend.rest.project;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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

import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.daos.OrderByDao;
import de.btu.openinfra.backend.db.daos.PtLocaleDao;
import de.btu.openinfra.backend.db.pojos.OrderByNamesPojo;
import de.btu.openinfra.backend.db.pojos.OrderByPojo;
import de.btu.openinfra.backend.db.pojos.file.FilePojo;
import de.btu.openinfra.backend.db.pojos.project.ProjectPojo;
import de.btu.openinfra.backend.db.rbac.OpenInfraHttpMethod;
import de.btu.openinfra.backend.db.rbac.ProjectRbac;
import de.btu.openinfra.backend.db.rbac.file.FileRbac;
import de.btu.openinfra.backend.rest.OpenInfraResponseBuilder;

/**
 * This class represents the project resource of the REST API. You can access
 * an overview of all available HTTP methods by calling the following link:
 * <a href="http://localhost:8080/openinfra_backend/rest/application.wadl">
 * http://localhost:8080/openinfra_backend/rest/application.wadl</a>
 *
 * The project resource is available by calling the following link:
 * <a href="http://localhost:8080/openinfra_backend/rest/projects">
 * http://localhost:8080/openinfra_backend/rest/projects</a>
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
@Path("/v1/projects")
@Produces({MediaType.APPLICATION_JSON + OpenInfraResponseBuilder.JSON_PRIORITY
    + OpenInfraResponseBuilder.UTF8_CHARSET,
	MediaType.APPLICATION_XML + OpenInfraResponseBuilder.XML_PRIORITY
	+ OpenInfraResponseBuilder.UTF8_CHARSET})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ProjectResource {

	/**
	 * This method provides a list of all projects. The provided parameters are
	 * used for paging.
	 *
	 * @param offset the number where to start
	 * @param size   the size of items to provide
	 * @return       a list of projects &lt;= size (window within offset
	 *               and size)
	 */
	@GET
	public List<ProjectPojo> get(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request,
			@QueryParam("language") String language,
			@QueryParam("offset") int offset,
			@QueryParam("size") int size) {
		return new ProjectRbac(
				null,
				OpenInfraSchemas.META_DATA).readMainProjects(
						PtLocaleDao.forLanguageTag(language));
	}

	@GET
	@Path("count")
	@Produces({MediaType.TEXT_PLAIN})
	public long getMainProjectsCount(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request) {
		return new ProjectRbac(
				null,
				OpenInfraSchemas.META_DATA).getMainProjectsCount();
	}

	/**
	 * This method provides a specific project.
	 *
	 * @param projectId the id of the requested project
	 * @return          the requested project
	 */
	@GET
	@Path("{projectId}")
	public Response get(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request,
			@QueryParam("language") String language,
			@PathParam("projectId") UUID projectId) {
		return OpenInfraResponseBuilder.getResponse(
				new ProjectRbac(
						projectId,
						OpenInfraSchemas.PROJECTS).read(
								OpenInfraHttpMethod.valueOf(
										request.getMethod()),
								uriInfo,
								PtLocaleDao.forLanguageTag(language),
								projectId));
	}

	/**
	 * This method delivers a list of sub projects specified by the current
	 * project id in only one hierarchy level.
	 *
	 *
	 * @param projectId the id of the current project
	 * @param offset    the number where to start
	 * @param size      the size of items to provide
	 * @return          a list of sub projects
	 */
	@GET
	@Path("{projectId}/subprojects")
	public List<ProjectPojo> getSubProjects(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request,
			@QueryParam("language") String language,
			@PathParam("projectId") UUID projectId,
			@QueryParam("offset") int offset,
			@QueryParam("size") int size) {
		return new ProjectRbac(
				projectId,
				OpenInfraSchemas.PROJECTS).readSubProjects(
						OpenInfraHttpMethod.valueOf(request.getMethod()),
						uriInfo,
						PtLocaleDao.forLanguageTag(language),
						offset,
						size);
	}

	@GET
	@Path("{projectId}/subprojects/count")
	@Produces({MediaType.TEXT_PLAIN})
	public long getSubProjectsCount(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request,
			@PathParam("projectId") UUID projectId) {
		return new ProjectRbac(
				projectId,
				OpenInfraSchemas.PROJECTS).getSubProjectsCount(
						OpenInfraHttpMethod.valueOf(request.getMethod()),
						uriInfo);
	}

	/**
	 * This method creates a new project.
	 *
	 * You can access this method via curl. An example of the command line could
	 * be as follows:
	 *
	 * curl.exe -X "POST" -H "Content-Type: application/xml"
	 * -d &#64;path/to/file http://localhost:8080/openinfra_backend/projects
	 *
	 * Use the following command to crate an empty schema which is required by
	 * the XML import/export interface:
	 *
	 * curl -i -b cookie.txt -X POST -H "Content-Type: application/json" -d
	 * @NewProject.json "http://localhost:8080/openinfra_core/rest/v1/projects?
	 * createEmpty=true&loadIntitialData=false"
	 *
	 * @param project a project object (JSON or XML representations are
	 *                converted into real objects via the JAX-RS stack)
	 * @param createEmpty The application creates an empty new schema when true.
	 *                    This is necessary by the external XML interface
	 *                    (XML import/export interface by HTW Dresden)
	 * @param loadInitialData This parameter prerequisites 'createEmpty=true'.
	 *                        There is no initial data stored in the new data
	 *                        base when false. This is required by the
	 *                        XML import/export interface.
	 * @return        an UUID of the created project
	 */
	@POST
	public Response createProject(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request,
			@QueryParam("createEmpty") boolean createEmpty,
			@QueryParam("loadInitialData") boolean loadInitialData,
			ProjectPojo project) {
	    // create the project
		UUID id = new ProjectRbac(
		        null, OpenInfraSchemas.PROJECTS).createProject(
		                project,
		                createEmpty,
		                loadInitialData,
		                OpenInfraHttpMethod.valueOf(
		                        request.getMethod()), uriInfo);

		// TODO add informations to the meta data schema, this is necessary for
		//      every REST end point this project should use
		return OpenInfraResponseBuilder.postResponse(id);
	}

	/**
	 * Currently, this method should update a project. In contrast to the
	 * definition of the HTTP PUT method, this method updates an existing
	 * resource and doesn't replace  it. This should be discussed/fixed in
	 * the future.
	 *
	 * You can access this method via curl. An example of the command line could
	 * be as follows:
	 *
	 * curl.exe -X "PUT" -H "Content-Type: application/xml" -d
	 * &#64;path/to/file http://localhost:8080/openinfra_backend/projects/{id}
	 *
	 * @param projectId the id of project which should be updated
	 * @return          a response with the specific status
	 */
    @PUT
    @Path("{projectId}")
    public Response updateProject(
    		@Context UriInfo uriInfo,
    		@Context HttpServletRequest request,
    		@PathParam("projectId") UUID projectId,
    		ProjectPojo project) {
        // TODO compare projectId in createOrUpdate method?
    	UUID uuid = new ProjectRbac(
    			projectId,
    			OpenInfraSchemas.PROJECTS).createOrUpdate(
    					OpenInfraHttpMethod.valueOf(request.getMethod()),
						uriInfo,
						projectId,
						project);
    	return OpenInfraResponseBuilder.putResponse(uuid);
    }

	/**
	 * This method deletes a project resource.
	 *
	 * You can access this method via curl. An example of the command line could
	 * be as follows:
	 *
	 * curl.exe -I -X "DELETE"
	 * http://localhost:8080/openinfra_backend/projects/{id}
	 *
	 * @param projectId the project which should be deleted
	 * @return          a response with the specific status
	 */
	@DELETE
	@Path("{projectId}")
	public Response deleteProject(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request,
	        @PathParam("projectId") UUID projectId) {
	    return OpenInfraResponseBuilder.deleteResponse(
	            new ProjectRbac(
    	            projectId,
    	            OpenInfraSchemas.PROJECTS).deleteProject(
    	                    OpenInfraHttpMethod.valueOf(request.getMethod()),
    	                    uriInfo),
                projectId);
	}

	@GET
	@Path("{projectId}/parents")
	public List<ProjectPojo> readParents(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request,
			@QueryParam("language") String language,
			@PathParam("projectId") UUID projectId) {
		return new ProjectRbac(
				projectId,
				OpenInfraSchemas.PROJECTS).readParents(
						OpenInfraHttpMethod.valueOf(request.getMethod()),
						uriInfo,
						PtLocaleDao.forLanguageTag(language));
	}

    @GET
	@Path("{projectId}/files")
	public List<FilePojo> readFilesByProject(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request,
			@PathParam("projectId") UUID projectId) {
		return new FileRbac().readByProject(
				OpenInfraHttpMethod.valueOf(request.getMethod()),
				uriInfo, projectId);
	}

	@GET
    @Path("/orderby")
    public OrderByPojo getP(
            @PathParam("schema") OpenInfraSchemas schema,
            @QueryParam("class") String classObject) {
        return OrderByDao.read(OpenInfraSchemas.PROJECTS, classObject);
    }

    @GET
    @Path("/orderby/names")
    public OrderByNamesPojo getNamesP(
            @PathParam("schema") OpenInfraSchemas schema) {
        return OrderByDao.getNames(OpenInfraSchemas.PROJECTS);
    }
}
