package de.btu.openinfra.backend.solr.enums;

/**
 * This enum maps the possible mandatory choices to the Solr syntax.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 */
public enum SolrMandatoryEnum {

    NEVER_MIND(""),
    MUST_NOT("-"),
    MUST("+");

    private String value;

    private SolrMandatoryEnum(String value) {
        this.value = value;
    }

    public String getString() {
        return this.value;
    }
}
