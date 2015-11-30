package de.btu.openinfra.backend.solr;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.client.solrj.response.LukeResponse.FieldInfo;

import de.btu.openinfra.backend.OpenInfraProperties;
import de.btu.openinfra.backend.OpenInfraPropertyKeys;
import de.btu.openinfra.backend.db.pojos.solr.SolrComplexQueryPartPojo;
import de.btu.openinfra.backend.db.pojos.solr.SolrSearchPojo;
import de.btu.openinfra.backend.exception.OpenInfraExceptionTypes;
import de.btu.openinfra.backend.exception.OpenInfraSolrException;
import de.btu.openinfra.backend.solr.enums.SolrDataTypeEnum;

/**
 * This class handles the parsing of the query. It would be better to implement
 * an own queryParser and add it to the solrconfig.xml!
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class SolrQueryParser {

    private SolrClient connection;
    private Map<String, String> indexMap;
    private boolean isDate;

    public SolrQueryParser(SolrClient connection) {
        this.connection = connection;
    }

    /**
     * This method returns a search query in Solr syntax. It will parse the
     * requested SolrSearchPojo and replace the field names for correct matching
     * to the Solr index.
     *
     * @return A String that represents the search query in Solr syntax.
     */
    public String getSolrSyntaxQuery(SolrSearchPojo pojo) {
        try {
            if (pojo != null) {
                // Determine which query is set.
                if (pojo.getRawSolrQuery() != null && !pojo.getRawSolrQuery()
                        .equals("")) {
                    // start the parse process for a simple Solr query
                    return parseRawSolrQuery(pojo.getRawSolrQuery());
                } else if (pojo.getComplexQueryPart().size() > 0) {
                    // start the parse process for a complex Solr query
                    return parseComplexSolrQuery(pojo.getComplexQueryPart());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OpenInfraSolrException(
                    OpenInfraExceptionTypes.SOLR_REQUEST_PARSE);
        }
        throw new OpenInfraSolrException(
                OpenInfraExceptionTypes.SOLR_SEARCH_POJO_EMPTY);
    }

    /**
     * This method parses a raw Solr query. It only converts the field
     * identifier.
     *
     * @param rawQuery The raw query in Solr syntax that should be converted.
     * @return         The converted Solr query.
     */
    private String parseRawSolrQuery(String rawQuery) {

        for (String attributeTypeName : extractFields(rawQuery)) {
            // convert the type name and replace it in the raw query
            rawQuery = rawQuery.replaceAll(
                    attributeTypeName,
                    checkForDate(
                            SolrCharacterConverter.convert(attributeTypeName)));

        }

        // convert all dates into a Solr formatted date
        rawQuery = replaceDate(rawQuery);
        return rawQuery;
    }

    /**
     * This method will replace all dates in a raw query that stands behind the
     * string "_date:" with the same date in Solr format.
     *
     * @param rawQuery The raw query where dates should be replaced.
     * @return         The raw query with replaced dates.
     */
    private String replaceDate(String rawQuery) {
        // regex pattern to retrieve everything behind "_date:" until the next
        // whitespace
        Pattern roughPattern = Pattern.compile(
                "_" + SolrDataTypeEnum.DATE.getString() +
                ":\\[([^\\s]+)\\s+TO\\s+([^\\]]+)|_" +
                SolrDataTypeEnum.DATE.getString() +
                ":([^\\s]+)");

        // regex pattern to retrieve every date / time combination
        Pattern exactPattern = Pattern.compile(
                "(\\d+-?\\d+-?\\d+\\s?\\d+:?\\d+:?\\d+|\\d+-?\\d+-?\\d+)");

        // search with the regex in the query
        Matcher roughMatcher = roughPattern.matcher(rawQuery);

        // match the first pattern and run through the results
        while (roughMatcher.find()) {
            // save the matches
            String match = roughMatcher.group();
            String result = match;

            // apply the second pattern and run through the results
            Matcher exactMatcher = exactPattern.matcher(roughMatcher.group());

            while (exactMatcher.find()) {
                // save the matches for further replacements
                String replacement = exactMatcher.group();

                // Replace the short date with the long date. Additionally it is
                // necessary to add a lot backslashes before replacing. This
                // will guarantee that at least one backslash survives.
                result = match.replaceAll(
                        Pattern.quote(replacement),
                        convertToDate(replacement)
                        .replaceAll("\\:", "\\\\\\\\\\\\\\\\:"));
                match = result;
            }

            // replace the original date with the converted date in the query
            rawQuery = rawQuery.replaceAll(
                    Pattern.quote(roughMatcher.group()), result);
        }
        return rawQuery;
    }

    /**
     * This method parses a complex Solr query into a Solr syntax string.
     *
     * @param complexQueryList A list of complex queries that should be parsed.
     * @return                 The Solr syntax string.
     */
    private String parseComplexSolrQuery(
            List<SolrComplexQueryPartPojo> complexQueryList) {
        String query = "";
        boolean firstRun = true;
        for (SolrComplexQueryPartPojo part : complexQueryList) {
            query += " " + parsePart(part, firstRun);
            firstRun = false;
        }
        // remove leading and trailing whitespaces
        return query.trim();
    }

    /**
     * This method parses a SolrComplexQueryPartPojo to Solr syntax. There are
     * some special definitions for the POJO properties.
     *
     * @param part     The part of the complex query that should be parsed.
     * @param firstRun Defines if it is the first part of the query.
     * @return         The complex query part as string in Solr syntax.
     */
    private String parsePart(SolrComplexQueryPartPojo part, boolean firstRun) {
        String query = "";
        boolean fuzzyFlag = part.isFuzziness();
        isDate = false;

        // add the attribute type
        if (part.getAttributeType() != null) {
            query += checkForDate(SolrCharacterConverter.convert(
                    part.getAttributeType()));
        }

        // add the delimiter between attribute type and attribute value
        if (!query.equals("")) {
            query += ":";
        }

        // add the mandatory identifier
        if (part.getMandatory() != null) {
            query = part.getMandatory().getString() + query;
        }


        // add the attribute value in different ways depending on the relational
        // operator
        if (part.getAttributeValue() != null) {
            // convert the value to a date if the type is a date and escape the
            // colons
            if (isDate) {
                part.setAttributeValue(
                        convertToDate(part.getAttributeValue()));
                // also convert the date for the till value if it exists
                if (part.getTillAttributeValue() != null &&
                        part.getTillAttributeValue() != "") {
                    part.setTillAttributeValue(
                            convertToDate(part.getTillAttributeValue()));
                }
            }
            if (part.getRelationalOperator() != null) {
                switch (part.getRelationalOperator()) {
                case GREATER_THAN:
                case SMALLER_THAN:
                    // Only sensible for numeric and date values. The date check
                    // will removes the last character that was added by
                    // converting the date to a Solr date format.
                    if (NumberUtils.isNumber(part.getAttributeValue()) ||
                            isDate) {
                        query += String.format(
                                part.getRelationalOperator().getString(),
                                part.getAttributeValue());
                    } else {
                        throw new OpenInfraSolrException(
                                OpenInfraExceptionTypes
                                .SOLR_REQUEST_NUMERIC_EXPECTED);
                    }
                    break;
                case BETWEEN:
                    query += String.format(
                            part.getRelationalOperator().getString(),
                            part.getAttributeValue(),
                            part.getTillAttributeValue());
                    // fuzzy search must not be used in combination with range
                    // queries
                    fuzzyFlag = false;
                    break;
                default:
                    query += maskString(part.getAttributeValue());
                    break;
                }
            } else {
                query += part.getAttributeValue();
            }
        } else {
            // return an empty string if no attribute value is passed
            return "";
        }

        // only add the logic operator if it is not the first part we match
        if (!firstRun) {
            if (part.getLogicOperator() != null) {
                query = part.getLogicOperator().getString() + " " + query;
            }
        }

        // add fuzziness if required
        if (fuzzyFlag) {
            query += "~" + OpenInfraProperties.getProperty(
                    OpenInfraPropertyKeys.SOLR_DEFAULT_FUZZY.getKey());
        }

        // add the relevance if it was passed
        if (part.getRelevance() != 0) {
            query += "^" + part.getRelevance();
        }

        return query;
    }

    /**
     * This method checks if a string contains whitespaces. If so the string
     * will be surrounded by quotation marks.
     *
     * @param str The input string that should be checked.
     * @return    The modified string.
     */
    private String maskString(String str) {
        if (str.contains(" ")) {
            // we must be sure that the client does not added quotation marks
            // as well
            if (!str.startsWith(("\"")) && !str.endsWith(("\""))) {
                str = "\"" + str +"\"";
            };
        }
        return str;
    }

    /**
     * This method extracts the name of every field from a raw Solr query and
     * return them in a list.
     *
     * @param query The raw Solr query.
     * @return      A list of fields extracted from the raw Solr query.
     */
    List<String> extractFields(String query) {
        List<String> lst = new ArrayList<String>();

        // pattern to capture the attribute type name in front of a colon
        Pattern pattern = Pattern.compile(
                "(\"{1}\\w+(\\s*|(\\s*\\w+\\s*)*)\\w+\"{1}$|\\w+$)");

        Matcher matcher = null;
        String attributeTypeName = "";

        // split the query on every colon to separate the attribute types
        String[] parts = query.split(":");

        for (int i = 0; i < parts.length; i++) {
            // the last entry is no attribute type
            if (i < parts.length - 1) {
                // retrieve the attribute type via regex
                matcher = pattern.matcher(parts[i]);
                if (matcher.find()) {
                    // save the attribute type name
                    attributeTypeName = matcher.group(0);

                    // remove chars that are not part of the attribute type name
                    attributeTypeName = attributeTypeName.replaceAll("\\+", "");
                    attributeTypeName = attributeTypeName.replaceAll("\\-", "");
                    attributeTypeName = attributeTypeName.replaceAll("\\(", "");
                    attributeTypeName = attributeTypeName.replaceAll("\\)", "");

                    // add the attribute type name to the result list
                    lst.add(attributeTypeName);
                }
            }
        }
        return lst;
    }

    /**
     * This method checks the Solr index if the specified field name exists with
     * the suffix _date. If it matches and the type is date it will append the
     * suffix to the specified field and return it.
     *
     * @param field The field that should be checked for date.
     * @return      The field name with the date suffix or in its original form.
     */
    private String checkForDate(String field) {
        try {
            if (field.equals("")) {
                return field;
            }

            if (indexMap == null) {
                // retrieve the Solr index
                LukeRequest lukeRequest = new LukeRequest();
                lukeRequest.setNumTerms(1);
                LukeResponse lukeResponse = lukeRequest.process(connection);

                // initialize the hash map
                indexMap = new HashMap<String, String>();

                // add the field names and types to the hash map
                for (FieldInfo indexEntry : lukeResponse.getFieldInfo()
                        .values()) {
                    indexMap.put(indexEntry.getName(), indexEntry.getType());
                }
            }

            // retrieve the field type from the hash map for the field name
            String type = indexMap.get(
                    field + "_" + SolrDataTypeEnum.DATE.getString());

            // check if the field name is part of the index and has the type
            // date
            if (type != null && type.equals(
                    SolrDataTypeEnum.DATE.getString())) {
                isDate = true;
                return field + "_" + SolrDataTypeEnum.DATE.getString();
            }
        } catch (SolrServerException | IOException e) {
            throw new OpenInfraSolrException(
                    OpenInfraExceptionTypes.SOLR_SERVER_NOT_FOUND);
        }
        return field;
    }

    /**
     * This method parses a date string into a Solr date string. This will only
     * works for specified input date formats.
     *
     * @param date The string that should be formated.
     * @return        The date string in the Solr date format.
     */
    private String convertToDate(String date) {
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        /*
         * The following date formats will be parsed. This is not part of the
         * properties file because the date formats should be tested. The order
         * of the dates is important. The fewer informations the date contains
         * the further at the end the format must be placed.
         */
        String[] dateFormats = {
                "yyyy-MM-dd hh:mm:ss", "dd.MM.yyyy hh:mm:ss",
                "yyyy-MM-dd hh:mm", "dd.MM.yyyy hh:mm",
                "yyyy-MM-dd", "dd.MM.yyyy",
                "yyyy-MM", "MM.yyyy",
                "yyyy"};
        for (String dateFormat : dateFormats) {
            try {
                return out.format(
                        new SimpleDateFormat(dateFormat).parse(date))
                        .replaceAll("\\:", "\\\\:");
            } catch (ParseException ignore) { }
        }
        throw new OpenInfraSolrException(
                OpenInfraExceptionTypes.INVALID_DATE_FORMAT);
    }
}
