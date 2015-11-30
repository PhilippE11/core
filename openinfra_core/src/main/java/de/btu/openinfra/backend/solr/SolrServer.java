package de.btu.openinfra.backend.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;

import de.btu.openinfra.backend.OpenInfraProperties;
import de.btu.openinfra.backend.OpenInfraPropertyKeys;
import de.btu.openinfra.backend.exception.OpenInfraExceptionTypes;
import de.btu.openinfra.backend.exception.OpenInfraSolrException;
import de.btu.openinfra.backend.exception.OpenInfraWebException;

/**
 * This class creates a connection to the Solr server.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class SolrServer {

    private String url;
    private SolrClient solr;

    /**
     *  Default constructor that uses the Solr connection URL from the
     *  OpenInfra.properties file
     */
    public SolrServer() {
        this(OpenInfraProperties.getProperty(
                OpenInfraPropertyKeys.SOLR_URL.getKey())
               + "/" +
               OpenInfraProperties.getProperty(
                       OpenInfraPropertyKeys.SOLR_CORE.getKey())
               );
    }

    /**
     *  Constructor that allows to override the Solr connection URL from the
     *  OpenInfra.properties file
     */
    public SolrServer(String URL) {
        setUrl(URL);
        // initialize the server connection
        init();
    }

    private String getUrl() {
        return url;
    }

    private void setUrl(String url) {
        this.url = url;
    }

    public SolrClient getSolr() {
        return solr;
    }

    private void setSolr(SolrClient solr) {
        this.solr = solr;
    }

    /**
     * This method connects to the the Solr server and test the connection.
     *
     * @return boolean true if the connection was successful else false
     * @throws OpenInfraWebException if an error occurs while pinging the server
     */
    private void init() {
        try {
            // connects to the Solr server
            setSolr(new HttpSolrClient(getUrl()));

            // test the connection
            getSolr().ping();

        } catch (SolrServerException | IOException | RemoteSolrException e) {
            throw new OpenInfraSolrException(
                    OpenInfraExceptionTypes.SOLR_SERVER_NOT_FOUND);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        getSolr().close();
        super.finalize();
    }
}
