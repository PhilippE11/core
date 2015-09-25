package de.btu.openinfra.backend.rest;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.btu.openinfra.backend.db.OpenInfraOrderByEnum;
import de.btu.openinfra.backend.db.OpenInfraSchemas;
import de.btu.openinfra.backend.db.OpenInfraSortOrder;
import de.btu.openinfra.backend.db.daos.PtLocaleDao;
import de.btu.openinfra.backend.db.pojos.RelationshipTypePojo;
import de.btu.openinfra.backend.db.pojos.TopicCharacteristicToRelationshipTypePojo;
import de.btu.openinfra.backend.db.rbac.RelationshipTypeRbac;
import de.btu.openinfra.backend.db.rbac.TopicCharacteristicToRelationshipTypeRbac;

@Path(OpenInfraResponseBuilder.REST_URI_DEFAULT + "/relationshiptypes")
@Produces({MediaType.APPLICATION_JSON + OpenInfraResponseBuilder.JSON_PRIORITY
    + OpenInfraResponseBuilder.UTF8_CHARSET,
    MediaType.APPLICATION_XML + OpenInfraResponseBuilder.XML_PRIORITY
    + OpenInfraResponseBuilder.UTF8_CHARSET})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class RelationshipTypeResource {

    @GET
    public List <RelationshipTypePojo> get(
            @QueryParam("language") String language,
            @PathParam("projectId") UUID projectId,
            @PathParam("schema") String schema,
			@QueryParam("sortOrder") OpenInfraSortOrder sortOrder,
			@QueryParam("orderBy") OpenInfraOrderByEnum orderBy,
            @QueryParam("offset") int offset,
            @QueryParam("size") int size) {
        return new RelationshipTypeRbac(
                projectId,
                OpenInfraSchemas.valueOf(schema.toUpperCase())).read(
                        PtLocaleDao.forLanguageTag(language),
						sortOrder,
						orderBy,
                        offset,
                        size);
    }

	@GET
	@Path("count")
	@Produces({MediaType.TEXT_PLAIN})
	public long getAttributeTypeGroupCount(
			@PathParam("projectId") UUID projectId,
			@PathParam("schema") String schema) {
		return new RelationshipTypeRbac(
				projectId,
				OpenInfraSchemas.valueOf(schema.toUpperCase())).getCount();
	}

    @GET
    @Path("{relationshipTypeId}")
    public RelationshipTypePojo get(
            @QueryParam("language") String language,
            @PathParam("projectId") UUID projectId,
            @PathParam("schema") String schema,
            @PathParam("relationshipTypeId") UUID relationshipTypeId) {
        return new RelationshipTypeRbac(
                projectId,
                OpenInfraSchemas.valueOf(schema.toUpperCase())).read(
                        PtLocaleDao.forLanguageTag(language),
                        relationshipTypeId);
    }

    @GET
	@Path("{relationshipTypeId}/topiccharacteristics")
	public List<TopicCharacteristicToRelationshipTypePojo>
    	getTopicCharacteristics(
			@QueryParam("language") String language,
			@PathParam("projectId") UUID projectId,
			@PathParam("schema") String schema,
			@PathParam("relationshipTypeId") UUID relationshipTypeId,
			@QueryParam("offset") int offset,
			@QueryParam("size") int size) {

		return new TopicCharacteristicToRelationshipTypeRbac(
				projectId,
				OpenInfraSchemas.valueOf(schema.toUpperCase())).read(
						PtLocaleDao.forLanguageTag(language),
						relationshipTypeId,
						offset,
						size);
	}

    @GET
    @Path("{relationshipTypeId}/topiccharacteristics/count")
    public long getTopicCharacteristicsCount(
            @PathParam("projectId") UUID projectId,
            @PathParam("schema") String schema,
            @PathParam("relationshipTypeId") UUID relationshipTypeId) {

        return new TopicCharacteristicToRelationshipTypeRbac(
                projectId,
                OpenInfraSchemas.valueOf(schema.toUpperCase())).getCount(
                        relationshipTypeId);
    }

	@GET
	@Path("{relationshipTypeId}/topiccharacteristics/{topicCharacteristicId}")
	public List<TopicCharacteristicToRelationshipTypePojo>
		getTopicCharacteristics(
			@QueryParam("language") String language,
			@PathParam("projectId") UUID projectId,
			@PathParam("schema") String schema,
			@PathParam("relationshipTypeId") UUID relationshipTypeId,
			@PathParam("topicCharacteristicId") UUID topicCharacteristicId,
			@QueryParam("offset") int offset,
			@QueryParam("size") int size) {

		return new TopicCharacteristicToRelationshipTypeRbac(
				projectId,
				OpenInfraSchemas.valueOf(schema.toUpperCase())).read(
						PtLocaleDao.forLanguageTag(language),
						relationshipTypeId,
						topicCharacteristicId,
						offset,
						size);
	}

	@GET
    @Path("/new")
    public RelationshipTypePojo newRelationshipType(
            @QueryParam("language") String language,
            @PathParam("projectId") UUID projectId,
            @PathParam("schema") String schema) {
        return new RelationshipTypePojo();
    }

	@POST
    public Response createRelationshipType(
            @PathParam("projectId") UUID projectId,
            @PathParam("schema") String schema,
            RelationshipTypePojo pojo) {
        // call the create or update method for the DAO and return the uuid
        return OpenInfraResponseBuilder.postResponse(
                    new RelationshipTypeRbac(
                            projectId,
                            OpenInfraSchemas.valueOf(schema.toUpperCase()))
                            .createOrUpdate(pojo, pojo.getMetaData()));
    }

	@PUT
	@Path("{relationshipTypeId}")
	public Response update(
	        @QueryParam("language") String language,
	        @PathParam("projectId") UUID projectId,
	        @PathParam("schema") String schema,
	        @PathParam("relationshipTypeId") UUID relationshipTypeId,
	        RelationshipTypePojo pojo) {
	    UUID uuid = new RelationshipTypeRbac(
                projectId,
                OpenInfraSchemas.valueOf(schema.toUpperCase())).createOrUpdate(
                        pojo, relationshipTypeId, pojo.getMetaData());
        return OpenInfraResponseBuilder.postResponse(uuid);
	}

	@DELETE
    @Path("{relationshipTypeId}")
    public Response delete(
            @PathParam("projectId") UUID projectId,
            @PathParam("relationshipTypeId") UUID relationshipTypeId,
            @PathParam("schema") String schema) {
        return OpenInfraResponseBuilder.deleteResponse(
                new RelationshipTypeRbac(
                        projectId,
                        OpenInfraSchemas.valueOf(schema.toUpperCase()))
                    .delete(relationshipTypeId),
                    relationshipTypeId);
    }
}
