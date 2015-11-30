package de.btu.openinfra.backend.exception;

/**
 * These enumeration define the OpenInfRA exception types and their
 * corresponding error messages.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public enum OpenInfraExceptionTypes {

	INTERNAL_SERVER_EXCEPTION(""),

	EMPTY_POJO("The specified POJO is empty. Progress aborted."),

    CREATE_SCHEMA("Failed to create the new project schema."),

    RENAME_SCHEMA("Failed to rename the project schema."),

    INSERT_INITIAL_DATA("Failed to load the static value lists."),

    INSERT_META_DATA("Failed to create an entry in the table "
    		+ "database_connection."),

    INSERT_BASIC_PROJECT_DATA("Failed to insert the initial project "
    		+ "information into the projects table."),

    MERGE_SYSTEM("Failed to merge the content of the system database into the"
            + " project schema."),

    INCOMPATIBLE_UUIDS("UUIDs are incompatible (URI vs Entity)."),

    NO_TRID_PASSED("Entity requires trid field."),

    ENTITY_NOT_FOUND("The requested entitiy was not found."),

    ENTITY_EXPIRED("The entity is expired."),

    MISSING_NAME_IN_POJO("The name of the object has not been set."),

    MISSING_DESCRIPTION_IN_POJO("The description of the object has not been "
            + "set."),

    MISSING_GEOM_IN_POJO("The geometry value must not be empty."),

    MISSING_VALUE_IN_POJO("The attribute value must not be empty."),

    MISSING_DATA_IN_POJO("Necessary information of the object has not been "
            + "set"),

    WRONG_SORT_TYPE("The requested sort type is not supported for this "
            + "object."),

	NO_SORT_TYPE("This object does not contain any orderBy parameter."),

	MISSING_PARAMETER("This URI requires a parameter."),

	NO_CLASS_IN_SCHEMA("The requested class is not part of the specified "
	        + "schema."),

	INVALID_DATE_FORMAT("The specified date format is not supported."),

	PLUGIN_NOT_FOUND("The requested plugin was not found."),

	// TODO the exception handling must be reworked to support specific
	//      exception type enums.
	SOLR_INDEX_FAILED("Failed to create the Solr index."),

	SOLR_SEARCH_POJO_EMPTY("The passed POJO for the search request is empty."),

	SOLR_REQUEST_PARSE("An error has occured while parsing the request POJO."),

	SOLR_REQUEST_NUMERIC_EXPECTED("A numeric value was expected but not "
	        + "found."),

	SOLR_SERVER_NOT_FOUND("The connection to the Solr server could not be "
	        + "established."),

	PASSWORD_IN_BLACKLIST("The used password is in blacklist.");

    private String msg;

    private OpenInfraExceptionTypes(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return this.msg;
    }
}
