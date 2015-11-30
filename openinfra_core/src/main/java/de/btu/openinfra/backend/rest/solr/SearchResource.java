package de.btu.openinfra.backend.rest.solr;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.btu.openinfra.backend.db.pojos.solr.SolrIndexPojo;
import de.btu.openinfra.backend.db.pojos.solr.SolrResultPojo;
import de.btu.openinfra.backend.db.pojos.solr.SolrSearchPojo;
import de.btu.openinfra.backend.rest.OpenInfraResponseBuilder;
import de.btu.openinfra.backend.solr.SolrIndexer;
import de.btu.openinfra.backend.solr.SolrSearcher;
import de.btu.openinfra.backend.solr.SolrSuggester;

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
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class SearchResource {

    /**
     * This resource awaits a SolrSearchPojo that will be the base for a search
     * request on the Solr index. The parameter UUID and TRID of the
     * SolrSearchPojo must not be set.
     *
     * @param locale     The language of the search request as string.
     * @param start      The offset parameter for the results.
     * @param rows       The count of results that should be returned.
     * @param searchPojo The SearchPojo that contains the request.
     * @return           A SolrResultPojo that contains the result, a query time
     *                   and a count of the retrieved results.
     *
     * @response.representation.200.qname SolrResultPojo
     * @response.representation.200.doc   This is the representation returned by
     *                                    default.
     *
     * @response.representation.500.qname OpenInfraWebException
     * @response.representation.500.doc   An internal error occurs while
     *                                    processing the requested query.
     */
    @POST
    public SolrResultPojo get(
            @QueryParam("language") String locale,
            @QueryParam("start") int start,
            @QueryParam("rows") int rows,
            SolrSearchPojo searchPojo) {
        return new SolrSearcher().search(searchPojo, start, rows);
    }

    /**
     * This resource starts the process that generates the Solr index. The
     * specified SolrIndexPojo can contain a list of project ids the index
     * should created for. If the list is empty all projects will be indexed.
     * The parameter UUID and TRID of the SolrSearchPojo must not be set.
     *
     * @param projects The SolrIndexPojo that contains the list of project ids
     *                 that should be indexed.
     * @return         True if the process was successful.
     *
     * @response.representation.200.qname boolean
     * @response.representation.200.doc   True is the representation returned by
     *                                    default.
     *
     * @response.representation.500.qname OpenInfraSolrException
     * @response.representation.500.doc   An internal error occurs while
     *                                    indexing the database.
     *
     */
    @POST
    @Path("/index")
    public boolean index(
            SolrIndexPojo projects) {
        SolrIndexer indexer = new SolrIndexer();
        return indexer.indexProjects(projects);
    }

    /**
     * This resource provides a suggestion on the Solr index for a specified
     * query string.
     *
     * @response.representation.200.qname A list of strings
     * @response.representation.200.doc   This is the representation returned by
     *                                    default.
     *
     * @response.representation.500.qname OpenInfraWebException
     * @response.representation.500.doc   An internal error occurs while
     *                                    processing the suggestion query.
     */
    @GET
    @Path("/suggest")
    public List<String> getSuggestion(
            @QueryParam("q") String qString) {
        return new SolrSuggester().getSuggestion(qString);
    }
}
