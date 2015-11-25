package de.btu.openinfra.backend;

import java.util.List;

import org.glassfish.jersey.server.wadl.config.WadlGeneratorConfig;
import org.glassfish.jersey.server.wadl.config.WadlGeneratorDescription;
import org.glassfish.jersey.server.wadl.internal.generators.WadlGeneratorApplicationDoc;
import org.glassfish.jersey.server.wadl.internal.generators.WadlGeneratorGrammarsSupport;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.WadlGeneratorResourceDocSupport;

/**
 * This class is used to extend the build-in generation of the WADL by Java-DOC
 * enriched information.
 *
 * This class is intrinsingly part of the REST API. But it is also configured
 * by the {@see OpenInfraApplication} class. We decided to place it in this
 * package next to where it is registered.
 *
 * See:
 * https://irfannagoo.wordpress.com/2012/12/20/jersey-extended-wadl-support/
 * http://razvancaraghin.blogspot.de/2014/01/html-documentation-for-your-rest.html
 * http://biemond.blogspot.de/2013/08/custom-jersey-wadl-generation.html
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class OpenInfraWadlConfig extends WadlGeneratorConfig {

    @Override
    public List<WadlGeneratorDescription> configure() {

        WadlGeneratorConfigDescriptionBuilder builder =
        		generator(WadlGeneratorApplicationDoc.class)
        		.prop("applicationDocsStream", "application-doc.xml")
        		.generator(WadlGeneratorGrammarsSupport.class)
        		.prop("grammarsStream", "application-grammars.xml")
        		.generator(WadlGeneratorResourceDocSupport.class)
        		.prop("resourceDocStream", "resourcedoc.xml");
        return builder.descriptions();
    }

}
