package de.btu.openinfra.backend.solr;

import java.text.Normalizer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import jersey.repackaged.com.google.common.collect.ImmutableMap;

/**
 * This class will convert special characters. This is necessary because Solr
 * does not accept every character as field name. The conversion is necessary
 * while indexing the data and while querying.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class SolrCharacterConverter {

    /*
     * This is a list of characters that must not be converted.
     */
    private static final String DISALLOWED_CHARS = "[^0-9A-z-._]";

    /*
     * This character should replace every disallowed character.
     */
    private static final String CHAR_REPLACEMENT = "_";

    /*
     * This map contains the mapping information for characters.
     */
    private static final Map<String, String> CHAR_MAPPING =
            ImmutableMap.of("ß", "ss");


    /**
     * This method converts the input string. It will map all defined characters
     * and disallowed characters.
     *
     * @param input The string that should be converted
     * @return      The converted string in lower case
     */
    public static String convert(String input) {
        input = mapCharacters(input);
        input = deAccent(input);
        input = replaceCharacters(input);
        return input.toLowerCase();
    }

    /**
     * This function maps a defined list of characters.
     *
     * @param input The string that holds characters that should be mapped.
     * @return      The converted input string.
     */
    private static String mapCharacters(String input) {
        // run through all defined entries in the map
        Iterator<Entry<String, String>> it = CHAR_MAPPING.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> pair = it.next();
            // and replace every character as defined in the map
            input = input.replaceAll(
                    pair.getKey().toString(), pair.getValue().toString());
        }
        return input;
    }

    /**
     * This function maps all accent characters to their counter parts.
     *
     * @param input The string that holds accent characters that should be
     *              replaced.
     * @return      The converted input string.
     */
    private static String deAccent(String input) {
        String ndfNormalizedString =
                Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(ndfNormalizedString).replaceAll("");
    }

    /**
     * This function replaces all characters that are no digits, no normal word,
     * no point, no hyphen and no underscore. It also removes leading and
     * trailing whitespace and quotation marks.
     *
     * @param input The string that holds characters that should be replaced.
     * @return      The converted input string.
     */
    private static String replaceCharacters(String input) {
        input = input.trim();
        // remove quotation marks before replacing disallowed characters
        input = input.replaceAll("\"", "");
        // replace disallowed characters with a regex
        input = input.replaceAll(DISALLOWED_CHARS, CHAR_REPLACEMENT);
        return input;
    }
}
