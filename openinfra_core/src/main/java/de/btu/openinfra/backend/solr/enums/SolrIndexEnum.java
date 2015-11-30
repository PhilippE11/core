package de.btu.openinfra.backend.solr.enums;

import de.btu.openinfra.backend.solr.SolrCharacterConverter;

/**
 * This enum contains the standard Solr index fields.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 */
public enum SolrIndexEnum {

    /*
     * This variable represents the Solr field that holds the topic instance id.
     */
    TOPIC_INSTANCE_ID("id"),

    /*
     * This variable represents the Solr field that holds the topic
     * characteristic id.
     */
    PROJECT_ID("projectId"),

    /*
     * This variable represents the Solr field that holds the project id.
     */
    TOPIC_CHARACTERISTIC_ID("topicCharacteristicId"),

    /*
     * This variable represents the Solr field that defines the default search
     * field. In this field all possible values of all other fields will be
     * added.
     */
    DEFAULT_SEARCH_FIELD("default_search"),

    /*
     * This variable represents the Solr field that holds all attribute values
     * that have no translated attribute type names.
     */
    NO_TRANSLATION_FIELD("_notranslation_"),

    /*
     * This variable represents the Solr field for lookups.UUIDs will not be
     * added to this field.
     */
    LOOKUP_FIELD("lookup_field");

    private String value;

    private SolrIndexEnum(String value) {
        this.value = value;
    }

    public String getString() {
        return SolrCharacterConverter.convert(this.value);
    }
}
