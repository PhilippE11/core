package de.btu.openinfra.backend.db;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import de.btu.openinfra.backend.Reflection;
import de.btu.openinfra.backend.db.daos.PtLocaleDao;
import de.btu.openinfra.backend.db.jpa.model.PtLocale;
import de.btu.openinfra.backend.db.pojos.LocalizedString;
import de.btu.openinfra.backend.db.pojos.OpenInfraPojo;
import de.btu.openinfra.backend.db.pojos.PtLocalePojo;
import de.btu.openinfra.backend.exception.OpenInfraWebException;

/**
 * This class supplies methods to create primer objects and to request
 * primer classes using a singleton implementation.
 *
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class PojoPrimer {

    /**
     * Pojo primer used to create primer objects and to request primer classes.
     */
    private static final PojoPrimer pojoPrimer = new PojoPrimer();

    /**
     * Hash map of OpenInfraPojo subclasses.
     */
    private Map<OpenInfraSchemas,
        Map<String, Class<? extends OpenInfraPojo>>> pojoClasses;

    /**
     * Default non-argument constructor. The constructor initiates
     * the hash map.
     */
    private PojoPrimer() {
        initiatePojoClasses();
    }

    /**
     * Creates a primer object for the given schema and pojo class.
     * @param schema the schema
     * @param projectId the project id
     * @param locale A Java.util locale objects.
     * @param pojoClass the pojo class
     * @return primer object if pojoClass and the schema exist in the pojo
     * hash map otherwise throws OpenInfraWebException
     * @throws OpenInfraWebException
     */
    public static OpenInfraPojo primePojoStatically(
            OpenInfraSchemas schema,
            UUID projectId,
            Locale locale,
            String pojoClass) {
        switch(schema) {
            case SYSTEM:
            case PROJECTS:
                return pojoPrimer.primePojo(
                        schema,
                        new PtLocaleDao(projectId, schema).read(locale),
                        pojoClass);
            default:
                return pojoPrimer.primePojo(schema, null, pojoClass);
        }

    }

    /**
     * Returns all primer class names for the given schema.
     * @param schema the schema
     * @return a list of all primer class names if schema exists in the pojo
     * hash map, otherwise throws OpenInfraWebException
     * @throws OpenInfraWebException
     */
    public static List<String> getPrimerNamesStatically(
            OpenInfraSchemas schema) {
        return pojoPrimer.getPrimerNames(schema);
    }

    /**
     * This methods initiates the hash map of OpenInfraPojo subclasses.
     */
    private void initiatePojoClasses() {
        pojoClasses = new HashMap<OpenInfraSchemas,
                Map<String, Class<? extends OpenInfraPojo>>>();

        // add meta pojo classes
        pojoClasses.put(
                OpenInfraSchemas.META_DATA,
                new HashMap<String, Class<? extends OpenInfraPojo>>());
        addClassesForSchema(
                OpenInfraSchemas.META_DATA,
                Reflection.<OpenInfraPojo>findAllClasses(
                        "de.btu.openinfra.backend.db.pojos.meta"));

        // add rbac pojo classes
        pojoClasses.put(
                OpenInfraSchemas.RBAC,
                new HashMap<String, Class<? extends OpenInfraPojo>>());
        addClassesForSchema(
                OpenInfraSchemas.RBAC,
                Reflection.<OpenInfraPojo>findAllClasses(
                        "de.btu.openinfra.backend.db.pojos.rbac"));

        // add SYSTEM pojo classes
        Reflections reflections = new Reflections(
                new ConfigurationBuilder().addUrls(ClasspathHelper.forPackage(
                        "de.btu.openinfra.backend.db.pojos")).
                        filterInputsBy(new FilterBuilder().
                        include("de\\.btu\\.openinfra\\.backend\\.db\\.pojos\\"
                                + "..*\\.class").
                        exclude("de\\.btu\\.openinfra\\.backend\\.db\\.pojos\\"
                                + "..*\\..*\\.class")).
                        setScanners(
                                new SubTypesScanner(false),
                                new ResourcesScanner(),
                                new TypeElementsScanner()));
        pojoClasses.put(
                OpenInfraSchemas.SYSTEM,
                new HashMap<String, Class<? extends OpenInfraPojo>>());
                addClassesForSchema(
                OpenInfraSchemas.SYSTEM,
                Reflection.<OpenInfraPojo>findAllClasses(reflections));

        // add projects pojo classes
        pojoClasses.put(
                OpenInfraSchemas.PROJECTS,
                new HashMap<String, Class<? extends OpenInfraPojo>>());
        pojoClasses.get(OpenInfraSchemas.PROJECTS).putAll(
                pojoClasses.get(OpenInfraSchemas.SYSTEM));
        addClassesForSchema(
                OpenInfraSchemas.PROJECTS,
                Reflection.<OpenInfraPojo>findAllClasses(
                        "de.btu.openinfra.backend.db.pojos.project"));

        // add file pojos
        pojoClasses.put(
                OpenInfraSchemas.FILES,
                new HashMap<String, Class<? extends OpenInfraPojo>>());
        addClassesForSchema(
                OpenInfraSchemas.FILES,
                Reflection.<OpenInfraPojo>findAllClasses(
                        "de.btu.openinfra.backend.db.pojos.file"));

        // add search pojo classes
        pojoClasses.put(
                OpenInfraSchemas.SEARCH,
                new HashMap<String, Class<? extends OpenInfraPojo>>());
        addClassesForSchema(
                OpenInfraSchemas.SEARCH,
                Reflection.<OpenInfraPojo>findAllClasses(
                        "de.btu.openinfra.backend.db.pojos.solr"));

    }

    /**
     * This method adds all given classes to the hash map (except interfaces
     * and abstract classes) for the given schema.
     * @param schema the schema
     * @param classes the classes
     */
    private void addClassesForSchema(
            OpenInfraSchemas schema,
            List<Class<? extends OpenInfraPojo> > classes) {
        for(Class<? extends OpenInfraPojo> currentClass : classes) {
            if(Modifier.isAbstract(currentClass.getModifiers()) == false &&
                    currentClass.isInterface() == false) {
                pojoClasses.get(schema).put(
                        currentClass.getSimpleName().replaceAll(
                                "Pojo", "").toLowerCase(),
                        currentClass);
            }
        }
    }

    /**
     * This method checks if the given pojo class is a subclass of PtLocalePojo.
     * If it is true then the method initiates the pojo object with the given
     * PtLocale object.
     * @param pojo the pojo object
     * @param pojoClass the pojo class
     * @param locale a PtLocale object
     */
    private void checkAndInitiatePtLocale(
            OpenInfraPojo pojo,
            Class<?> pojoClass,
            PtLocale locale) {
        // check if the pojo class is a subclass of PtLocalePojo
        if(PtLocalePojo.class.isAssignableFrom(pojoClass) == true) {
            // initiate the pojo object with the given PtLocale object.
            PtLocalePojo ptLocalPojo = (PtLocalePojo) pojo;
            ptLocalPojo.setUuid(locale.getId());
            ptLocalPojo.setTrid(locale.getXmin());
            ptLocalPojo.setCharacterCode(
                    locale.getCharacterCode().getCharacterCode());
            ptLocalPojo.setCountryCode(
                    locale.getCountryCode().getCountryCode());
            ptLocalPojo.setLanguageCode(
                    locale.getLanguageCode().getLanguageCode());
        }
    }

    /**
     * This method checks if the parameter of the given method (a setter method
     * of the given pojo) is of the type List<LocalizedString>. If it is true
     * then a new list with one localized string will be created and the
     * reference of new list will be set to the given pojo.
     * @param method the setter method of the given pojo
     * @param pojo the pojo object
     * @return the new list with one localized string if the parameter of the
     * given method is of the type List<LocalizedString>, otherwise null
     */
    private List<LocalizedString> checkCreateAndSetLocalizedStrings(
            Method method,
            OpenInfraPojo pojo) {
        List<LocalizedString> localizedStrings = null;

        // if the method parameter is a subclass of List
        if(List.class.isAssignableFrom(
                method.getParameterTypes()[0]) == true) {
            // and if the list has one generic parameter
            if(method.getGenericParameterTypes().length == 1) {
                // get the class of the generic parameter
                Type type = method.getGenericParameterTypes()[0];
                ParameterizedType pType = (ParameterizedType) type;
                Class<?> genericParameterClass =
                        (Class<?>) pType.getActualTypeArguments()[0];
                // if the class of the generic parameter is a subclass
                // of LocalizedString
                if(LocalizedString.class.isAssignableFrom(
                        genericParameterClass) == true) {
                    // create a new list of LocalizedString
                    localizedStrings = new ArrayList<LocalizedString>();
                    // add one LocalizedString
                    localizedStrings.add(new LocalizedString());
                    // create and set the PtLocalePojo of the
                    // LocalizedString
                    localizedStrings.get(0).setLocale(new PtLocalePojo());
                    try {
                        // invoke the set method
                        method.invoke(pojo, localizedStrings);
                    } catch (IllegalAccessException |
                            IllegalArgumentException |
                            InvocationTargetException e) {
                        localizedStrings = null;
                        e.printStackTrace();
                    }
                }
            }
        }

        return localizedStrings;
    }

    /**
     * This method checks if the parameter of the given method (a setter method
     * of the given pojo) is a subclass of OpenInfraPojo. If it is true then
     * a new sub pojo will be created and the reference of new sub pojo will be
     * set to the given pojo.
     * @param method the setter method of the given pojo
     * @param pojo the pojo object
     * @return the new sub pojo object if the parameter of the given method
     * is a subclass of OpenInfraPojo, otherwise null
     */
    private OpenInfraPojo checkCreateAndSetSubPojo(
            Method method,
            OpenInfraPojo pojo) {
        OpenInfraPojo subPojo = null;
        // if the method parameter is a subclass of OpenInfraPojo
        if(OpenInfraPojo.class.isAssignableFrom(
                method.getParameterTypes()[0]) == true) {
            // create a new pojo and invoke the set method
            subPojo = createAndSetSubPojo(
                    method,
                    pojo,
                    method.getParameterTypes()[0]);
        }

        return subPojo;
    }

    /**
     * This method creates a new sub pojo using the given sub pojo class and
     * sets the reference of new sub pojo to the given pojo using the given
     * setter method.
     * @param method the setter method of the given pojo
     * @param pojo the pojo object
     * @param subPojoClass the sub pojo class
     * @return new sub pojo object if the sub pojo construction and the method
     * invocation are successful, otherwise null
     */
    private OpenInfraPojo createAndSetSubPojo(
            Method method,
            OpenInfraPojo pojo,
            Class<?> subPojoClass) {
        OpenInfraPojo subPojo = null;

        try {
            // create a new pojo and invoke the set method
            subPojo = (OpenInfraPojo) subPojoClass.newInstance();
            method.invoke(pojo, subPojo);
        } catch (IllegalAccessException |
                IllegalArgumentException |
                InvocationTargetException |
                InstantiationException e) {
            subPojo = null;
            e.printStackTrace();
        }

        return subPojo;
    }

    /**
     * This method converts the pojo object into a primer object.
     * A primer object is an empty but structured pojo object. The primer
     * object represents the structure of the pojo object without content.
     * @param pojo the pojo object
     * @param pojoClass the pojo class
     * @param locale a PtLocale object
     */
    private void makePrimer(
            OpenInfraPojo pojo,
            Class<?> pojoClass,
            PtLocale locale) {

        // initiate the pojo object with the given PtLocale object if
        // the pojo class is a subclass of PtLocalePojo
        checkAndInitiatePtLocale(pojo, pojoClass, locale);

        OpenInfraPojo subPojo = null;
        List<LocalizedString> localizedStrings = null;

        for(Method method : pojoClass.getMethods()) {
            // for each setter method with exactly one parameter
            if(method.getName().startsWith("set") &&
                    void.class.isAssignableFrom(method.getReturnType()) &&
                    method.getParameterTypes().length == 1) {

                // check, create and set new sub pojo
                subPojo = checkCreateAndSetSubPojo(method, pojo);

                // if a sub pojo was created
                if(subPojo != null) {
                    // call makePrimer for the new sub pojo
                    makePrimer(subPojo, subPojo.getClass(), locale);
                }

                // check, create and set new list of localized strings
                localizedStrings = checkCreateAndSetLocalizedStrings(
                        method,
                        pojo);

                // if a new list of localized strings was created
                if(localizedStrings != null) {
                    // call makePrimer for the PtLocalePojo
                    makePrimer(
                            localizedStrings.get(0).getLocale(),
                            localizedStrings.get(0).getLocale().getClass(),
                            locale);
                }
            }
        }
    }

    /**
     * Creates a primer object for the given schema and pojo class.
     * @param schema the schema
     * @param locale a PtLocale object
     * @param pojoClass the pojo class
     * @return primer object if pojoClass and the schema exist in the pojo
     * hash map otherwise throws OpenInfraWebException
     * @throws OpenInfraWebException
     */
    private OpenInfraPojo primePojo(
            OpenInfraSchemas schema,
            PtLocale locale,
            String pojoClass) {
        try {
            OpenInfraPojo pojo =
                    pojoClasses.get(schema).get(pojoClass).newInstance();
            makePrimer(pojo, pojo.getClass(), locale);
            return pojo;
        } catch (InstantiationException | IllegalAccessException |
                NullPointerException e) {
            throw new OpenInfraWebException(e);
        }
    }

    /**
     * Returns all primer class names for the given schema.
     * @param schema the schema
     * @return a list of all primer class names if schema exists in the pojo
     * hash map otherwise throws OpenInfraWebException
     * @throws OpenInfraWebException
     */
    private List<String> getPrimerNames(OpenInfraSchemas schema) {
        try {
            return new ArrayList<String>(pojoClasses.get(schema).keySet());
        }
        catch(NullPointerException e) {
            throw new OpenInfraWebException(e);
        }
    }
}
