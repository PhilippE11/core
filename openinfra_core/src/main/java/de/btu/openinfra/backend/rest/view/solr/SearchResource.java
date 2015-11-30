package de.btu.openinfra.backend.rest.view.solr;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.mvc.Template;

import de.btu.openinfra.backend.rest.OpenInfraResponseBuilder;




/**
 * This class represents and implements the Search resource.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */

@Path(OpenInfraResponseBuilder.REST_URI_SEARCH)
@Produces(MediaType.TEXT_HTML +
        OpenInfraResponseBuilder.UTF8_CHARSET +
        OpenInfraResponseBuilder.HTML_PRIORITY)
public class SearchResource {

    @GET
    @Path("/result")
    @Template(name="/views/list/SearchResult.jsp")
    public boolean getView(
            @QueryParam("language") String locale,
            @QueryParam("query") String query) {
        return true;
    }

    @GET
    @Path("/extended")
    @Template(name="/views/ExtendedSearch.jsp")
    public boolean getExtSearchView(
            @QueryParam("language") String locale,
            @QueryParam("query") String query) {
        return true;
    }
}

