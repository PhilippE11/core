package de.btu.openinfra.backend.rest.project;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.btu.openinfra.backend.OpenInfraProperties;
import de.btu.openinfra.backend.db.OpenInfraOrderBy;
import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.OpenInfraSortOrder;
import de.btu.openinfra.backend.db.daos.AttributeValueGeomType;
import de.btu.openinfra.backend.db.daos.PtLocaleDao;
import de.btu.openinfra.backend.db.pojos.TopicGeomzPojo;
import de.btu.openinfra.backend.db.pojos.project.TopicInstancePojo;
import de.btu.openinfra.backend.db.rbac.OpenInfraHttpMethod;
import de.btu.openinfra.backend.db.rbac.TopicCharacteristicRbac;
import de.btu.openinfra.backend.db.rbac.TopicGeomzRbac;
import de.btu.openinfra.backend.db.rbac.TopicInstanceRbac;
import de.btu.openinfra.backend.rest.OpenInfraResponseBuilder;

@Path(OpenInfraResponseBuilder.REST_URI_PROJECTS + "/topiccharacteristics/"
		+ "{topicCharacteristicId}/")
@Produces({MediaType.APPLICATION_JSON + OpenInfraResponseBuilder.JSON_PRIORITY
    + OpenInfraResponseBuilder.UTF8_CHARSET,
    MediaType.APPLICATION_XML + OpenInfraResponseBuilder.XML_PRIORITY
    + OpenInfraResponseBuilder.UTF8_CHARSET})
public class TopicCharacteristicResource {

	/**
	 * This method will read a list of topic instances for a specific topic
	 * characteristic id. Additional to the standard query parameter it will
	 * accept a filter parameter and orderBy parameter. It is possible to set
	 * both orderBy parameter simultaneously but the
	 *
	 * @param language
	 * @param projectId
	 * @param topicCharacteristicId
	 * @param filter
	 * @param sortOrder
	 * @param orderByEnum
	 * @param orderByUuid
	 * @param offset
	 * @param size
	 * @return
	 */
	@GET
	@Path("topicinstances")
	public List<TopicInstancePojo> getTopicInstances(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request,
			@QueryParam("language") String language,
			@PathParam("projectId") UUID projectId,
			@PathParam("topicCharacteristicId") UUID topicCharacteristicId,
			@QueryParam("filter") String filter,
			@QueryParam("sortOrder") OpenInfraSortOrder sortOrder,
            @QueryParam("orderBy") OpenInfraOrderBy orderBy,
			@QueryParam("offset") int offset,
			@QueryParam("size") int size) {
		// Define the specific parameters when not specified correctly
		if(size == 0) {
			offset = OpenInfraProperties.DEFAULT_OFFSET;
			size = OpenInfraProperties.DEFAULT_SIZE;
		} // end if

		if(filter != null && filter.length() > 0) {
			return new TopicInstanceRbac(
					projectId,
					OpenInfraSchemas.PROJECTS).read(
							OpenInfraHttpMethod.valueOf(request.getMethod()),
							uriInfo,
							PtLocaleDao.forLanguageTag(language),
							topicCharacteristicId,
							filter,
							offset,
							size);
		} else {
			return new TopicInstanceRbac(
					projectId,
					OpenInfraSchemas.PROJECTS).read(
							OpenInfraHttpMethod.valueOf(request.getMethod()),
							uriInfo,
					PtLocaleDao.forLanguageTag(language),
					topicCharacteristicId,
					sortOrder,
					orderBy,
					offset,
					size);
		} // end if else
	}

	@GET
	@Path("topicinstances/count")
	@Produces({MediaType.TEXT_PLAIN})
	public long getTopicInstancesCount(
			@Context UriInfo uriInfo,
			@Context HttpServletRequest request,
			@PathParam("projectId") UUID projectId,
			@PathParam("topicCharacteristicId") UUID topicCharacteristicId) {
		return new TopicInstanceRbac(
				projectId,
				OpenInfraSchemas.PROJECTS).getCount(
						OpenInfraHttpMethod.valueOf(request.getMethod()),
						uriInfo,
						topicCharacteristicId);
	}

	/**
	 * This is a special representation of the topic object. It delivers always
	 * a list of topic instances with corresponding 3D attribute values as X3D.
	 *
	 * @param language
	 * @param projectId
	 * @param topicCharacteristicId
	 * @param geomType
	 * @param offset
	 * @param size
	 * @return
	 */
	@GET
    @Path("topicinstances/geomz")
    public List<TopicGeomzPojo> getTopicInstancesGeomz(
    		@Context UriInfo uriInfo,
    		@Context HttpServletRequest request,
            @QueryParam("language") String language,
            @PathParam("projectId") UUID projectId,
            @PathParam("topicCharacteristicId") UUID topicCharacteristicId,
            @QueryParam("geomType") AttributeValueGeomType geomType,
            @QueryParam("offset") int offset,
            @QueryParam("size") int size) {
	    return new TopicGeomzRbac(
	            projectId,
	            OpenInfraSchemas.PROJECTS,
	            geomType).read(
	            		OpenInfraHttpMethod.valueOf(request.getMethod()),
						uriInfo,
	                    PtLocaleDao.forLanguageTag(language),
	                    topicCharacteristicId,
	                    offset,
	                    size);
    }

	/**
     * This is a special representation of the topic object. It delivers always
     * a list of topic instances with corresponding 3D attribute values as X3D.
     *
     * @param language
     * @param projectId
     * @param topicCharacteristicId
     * @param geomType
     * @return
     */
    @GET
    @Path("topicinstances/geomz/count")
    public long getTopicInstancesGeomzCount(
    		@Context UriInfo uriInfo,
    		@Context HttpServletRequest request,
            @QueryParam("language") String language,
            @PathParam("projectId") UUID projectId,
            @PathParam("topicCharacteristicId") UUID topicCharacteristicId,
            @QueryParam("geomType") AttributeValueGeomType geomType) {
        return new TopicGeomzRbac(
                projectId,
                OpenInfraSchemas.PROJECTS,
                geomType).getCount(
                		OpenInfraHttpMethod.valueOf(request.getMethod()),
						uriInfo,
						topicCharacteristicId);
    }

    /**
     * This resource will return a list of attribute values for a given query
     * string. The list will only contain values that belong to a specified
     * topic characteristic and a specified attribute type. The localization of
     * the string will depend on the requested language.
     *
     * @param uriInfo
     * @param request
     * @param language              The language of the query string.
     * @param projectId             The project id ...
     * @param topicCharacteristicId The topic characteristic id the values
     *                              should be part of.
     * @param attributeTypeId       The attribute type id the values should be
     *                              part of. The attribute type must be used in
     *                              the topic characteristic.
     * @param qString               The query string that should be searched
     *                              for.
     * @return                      A list of strings that match to the
     *                              specified parameter or null if the locale
     *                              and / or the qString is not specified.
     */
    @GET
    @Path("/attributetypes/{attributeTypeId}/suggest")
    public List<String> getSuggestion(
            @Context UriInfo uriInfo,
            @Context HttpServletRequest request,
            @QueryParam("language") String language,
            @PathParam("projectId") UUID projectId,
            @PathParam("topicCharacteristicId") UUID topicCharacteristicId,
            @PathParam("attributeTypeId") UUID attributeTypeId,
            @QueryParam("q") String qString) {
        return new TopicCharacteristicRbac(
                projectId,
                OpenInfraSchemas.PROJECTS).getSuggestion(
                        uriInfo,
                        OpenInfraHttpMethod.valueOf(request.getMethod()),
                        PtLocaleDao.forLanguageTag(language),
                        topicCharacteristicId,
                        attributeTypeId,
                        qString);
    }
}
