package de.btu.openinfra.backend.rest;

import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * This class is a helper class which is used to build responses in an uniform
 * way.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class OpenInfraResponseBuilder {

	/**
	 * This variable defines the REST URI used in OpenInfRA. It contains the
	 * schema definition (projects or system) and the UUID of the project
	 * as regular expression. The slash and the project UUID are optional and
	 * are not used for the system schema.
	 */
	public static final String REST_URI_DEFAULT =
			"/v1/{schema:(projects|system)}"
			+ "{optional:(/?)}"
			+ "{projectId:([0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{12})?}";

	public static final String REST_URI_METADATA = "/v1/metadata";
	public static final String REST_URI_PROJECTS = "/v1/{schema:(projects)}/"
			+ "{projectId:([0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{12})}";
	public static final String REST_URI_RBAC = "/v1/rbac";
	public static final String REST_URI_SYSTEM = "/v1/system";
	public static final String REST_URI_SEARCH = "/v1/search";
	public static final String REST_URI_ORDERBY = "v1/orderby";

	/**
	 * This variable defines the REST URI for primer requests. It contains the
	 * schema definition (system, rbac or metadata). The schema projects is
	 * not supported. For the schema projects use REST_URI_PROJECTS instead.
	 */
	public static final String REST_URI_PRIMER =
            "/v1/{schema:(system|rbac|metadata|files|search)}";


	/**
	 * This variable defines the character set utf8 which is returned by the
	 * REST services. This definition has been chosen in order to avoid wrong
	 * character sets while using JSP templates. JSP templates define its own
	 * <i>contentType</i> and <i>pageEncoding</i>. The usage of this variable
	 * overrides the character set defined by a JSP template.
	 */
	public static final String UTF8_CHARSET = ";charset=UTF-8";
	/**
	 * This variable defines the priority of HTML response. Used for older
	 * browsers which don't provide its own priority setting.
	 */
	public static final String HTML_PRIORITY = ";qs=1";
	/**
	 * This variable defines the priority of XML response. Used for older
	 * browsers which don't provide its own priority setting.
	 */
	public static final String XML_PRIORITY = ";qs=.8";
	/**
	 * This variable defines the priority of JSON response. Used for older
	 * browsers which don't provide its own priority setting.
	 */
	public static final String JSON_PRIORITY = ";qs=.5";
	/**
	 * This variable defines the priority of PDF response. Used for older
	 * browsers which don't provide its own priority setting.
	 */
	public static final String PDF_PRIORITY = ";qs=.4";

	/**
	 * This method builds a response for post requests.
	 *
	 * @param id the id of the object
	 * @return   a specific and pre-build response
	 */
	public static Response postResponse(UUID id) {
		if(id != null) {
			return Response.ok().entity(
					new OpenInfraResultMessage("Entity created", id)).build();
		} else {
			return Response.noContent().build();
		} // end if else
	}

	/**
	 * This method builds a response for put requests.
	 *
	 * @param id the id of the object
	 * @return   a specific and pre-build response
	 */
	public static Response putResponse(UUID id) {
		if(id != null) {
			return Response.ok().entity(
					new OpenInfraResultMessage("Entity updated", id)).build();
		} else {
			return Response.noContent().build();
		} // end if else
	}

	/**
	 * This method builds a response for delete requests.
	 *
	 * @param result the result of the delete function
	 * @param id     the id of the deleted object
	 * @return       a specific and pre-build response
	 */
	public static Response deleteResponse(boolean result, UUID id) {
		if(result) {
			return Response.ok().entity(
			        new OpenInfraResultMessage("Entity deleted", id)).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		} // end if else
	}

	/**
	 * This method builds a response for delete requests.
	 *
	 * @param id the id of the deleted object
	 * @return a specific and pre-build response
	 */
	public static Response deleteResponse(UUID id) {
	    if(id != null) {
	        return Response.ok().entity(
	                new OpenInfraResultMessage("Entity deleted", id)).build();
	    }
	    else {
	        return Response.status(Response.Status.NOT_FOUND).build();
	    }
	}

	/**
	 * This method builds a response for get requests.
	 *
	 * @param  result the object that should be returned
	 * @return a response containing the object otherwise an HTTP 404
	 */
	public static Response getResponse(Object result) {
		if(result != null) {
			return Response.ok().entity(result).build();
		} else {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

}
