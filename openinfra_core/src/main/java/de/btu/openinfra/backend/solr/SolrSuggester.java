package de.btu.openinfra.backend.solr;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import de.btu.openinfra.backend.exception.OpenInfraWebException;


/**
 * This class provides suggestions from the Solr index.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class SolrSuggester extends SolrServer {

    /**
     * Default constructor
     */
    public SolrSuggester() {
        // connect to the Solr server
        super();
    }

    /**
     * This method realize a suggestion on the Solr index for the specified
     * string. It will return a list of matching strings that were found in the
     * Solr index.
     *
     * @param qString The string that should be the base for the suggestion.
     * @return        A list of strings
     */
    public List<String> getSuggestion(String qString) {

        // create a Solr query object
        SolrQuery query = new SolrQuery();

        // define the request handler for suggestion
        query.setRequestHandler("/suggest");

        // add the specified suggestion to the search query
        query.setQuery(qString);

        try {
            // start the request and receive the response
            QueryResponse response = getSolr().query(query);

            // Get the result list from the suggest response. OpenInfRASuggester
            // is a search component that is defined in the solrconfig.xml
            return response.getSuggesterResponse().getSuggestedTerms()
                    .get("OpenInfRASuggester");
        } catch (SolrServerException | IOException e) {
            throw new OpenInfraWebException(e);
        }
    }
}
