package de.btu.openinfra.plugins.solr.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.btu.openinfra.backend.rest.OpenInfraResponseBuilder;
import de.btu.openinfra.plugins.solr.SolrIndexer;

/**
 * This class represents and implements the Search resource.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
@Path(OpenInfraResponseBuilder.REST_URI_SEARCH)
@Produces({MediaType.APPLICATION_JSON + OpenInfraResponseBuilder.JSON_PRIORITY
    + OpenInfraResponseBuilder.UTF8_CHARSET,
    MediaType.APPLICATION_XML + OpenInfraResponseBuilder.XML_PRIORITY
    + OpenInfraResponseBuilder.UTF8_CHARSET})
public class SearchResource {

//    @GET
//    public List<Result> get(
//            @QueryParam("query") String query,
//            @QueryParam("language") String locale) {
//        return new Searcher().search(query, locale);
//    }

    @GET
    @Path("/index")
    public boolean index() {
        SolrIndexer indexer = new SolrIndexer();
        return indexer.start();
    }
}
