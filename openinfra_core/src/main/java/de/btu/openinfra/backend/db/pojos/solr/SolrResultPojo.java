package de.btu.openinfra.backend.db.pojos.solr;

import java.util.List;

import de.btu.openinfra.backend.db.pojos.OpenInfraPojo;


/**
 * This POJO is a container for the result of a Solr query. It contains all Solr
 * result pojos and some meta data for the request.
 *
 * TODO The POJOs must extend OpenInfraPojo to be accessible for the primer
 *      class. The UUID and TRID that is provided by the OpenInfraPojo will
 *      never be used.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 */
public class SolrResultPojo extends OpenInfraPojo {

    private long elapsedTime;
    private long resultCount;
    private List<SolrResultDbPojo> databaseResult;

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public long getResultCount() {
        return resultCount;
    }

    public void setResultCount(long resultCount) {
        this.resultCount = resultCount;
    }

    public List<SolrResultDbPojo> getDatabaseResult() {
        return databaseResult;
    }

    public void setDatabaseResult(List<SolrResultDbPojo> databaseResult) {
        this.databaseResult = databaseResult;
    }
}
