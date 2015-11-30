package de.btu.openinfra.backend;

/**
 * This enumeration defines the used property keys. The values are defined in
 * another enumeration. {@see OpenInfraDbProperties}
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public enum OpenInfraPropertyKeys {

	/**
	 * This variable defines the JDBC driver property.
	 */
	JDBC_DRIVER("javax.persistence.jdbc.driver"),

	/**
	 * This variable defines the user property.
	 */
	USER("javax.persistence.jdbc.user"),

	/**
	 * This variable defines the password property.
	 */
	PASSWORD("javax.persistence.jdbc.password"),

	/**
	 * This variable defines the used server.
	 */
	SERVER("de.btu.openinfra.backend.db.jpa.server"),

	/**
	 * This variable defines the server port.
	 */
	PORT("de.btu.openinfra.backend.db.jpa.port"),

	/**
	 * This variable defines the database name.
	 */
	DB_NAME("de.btu.openinfra.backend.db.jpa.dbname"),

	/**
	 * This variable defines the URL property.
	 */
	URL("javax.persistence.jdbc.url"),

	/**
	 * This variable defines the default offset of the window functionality.
	 */
	DEFAULT_OFFSET("de.btu.openinfra.backend.rest.defaultOffset"),

	/**
     * This variable defines the default size of the window functionality.
     */
    DEFAULT_SIZE("de.btu.openinfra.backend.rest.defaultSize"),

    /**
     * This variable defines a max size for the window functionality.
     */
    MAX_SIZE("de.btu.openinfra.backend.rest.maxSize"),

    /**
     * This variable defines the default language key.
     */
    DEFAULT_LANGUAGE("de.btu.openinfra.backend.language.default"),

    /**
     * This variable defines the default order key.
     */
    DEFAULT_ORDER("de.btu.openinfra.backend.order"),

    /**
     * This variable defines the maximum number of results for suggestion.
     */
    MAX_SUGGESTION("de.btu.openinfra.backend.suggestion.maxResults"),

    /**
     * This variable defines the default file path for unix key.
     */
    UNIX_FILE_PATH("de.btu.openinfra.backend.unix.file.path"),

    /**
     * This variable defines the default file path for Windows key.
     */
    WIN_FILE_PATH("de.btu.openinfra.backend.win.file.path"),

    /**
     * This variable defines the default ImageMagick path.
     */
	WIN_IMAGEMAGICK_PATH("de.btu.openinfra.backend.win.imagemagick.path"),

	/**
	 * This variable defines the default thumbnail dimension.
	 */
	IMG_THUMBNAIL_DIMENSION("de.btu.openinfra.backend.img.thumbnail.dimension"),

	/**
	 * This variable defines the default image middle dimension.
	 */
	IMG_MIDDLE_DIMENSION("de.btu.openinfra.backend.img.middle.dimension"),

	/**
	 * This variable defines the default image popup dimension.
	 */
	IMG_POPUP_DIMENSION("de.btu.openinfra.backend.img.popup.dimension"),

	/**
     * This variable defines the URL to the Solr server.
     */
    SOLR_URL("de.btu.openinfra.backend.solr.url"),

    /**
     * This variable defines the name of the Solr core.
     */
    SOLR_CORE("de.btu.openinfra.backend.solr.core"),

    /**
     * This variable defines the index window size for indexing projects.
     */
    SOLR_INDEX_WINDOW(
            "de.btu.openinfra.backend.solr.indexWindow"),

    /**
     * This variable defines the default fuzzy parameter.
     */
    SOLR_DEFAULT_FUZZY("de.btu.openinfra.backend.solr.defaultFuzzy"),

    /**
     * This variable defines the default maximum number of results per request
     */
    SOLR_DEFAULT_RESULTS_PER_PAGE(
            "de.btu.openinfra.backend.solr.defaultResultsPerPage");

	private String key;
	private OpenInfraPropertyKeys(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}

}
