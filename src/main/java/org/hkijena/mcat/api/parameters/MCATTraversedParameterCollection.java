package org.hkijena.mcat.api.parameters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.events.ParameterChangedEvent;
import org.hkijena.mcat.api.events.ParameterStructureChangedEvent;
import org.hkijena.mcat.utils.StringUtils;
import org.scijava.Priority;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An {@link MCATParameterCollection} that contains the parameters of one or multiple
 * {@link MCATParameterCollection} instances in a traversed form.
 */
public class MCATTraversedParameterCollection implements MCATParameterCollection, MCATCustomParameterCollection {

    private EventBus eventBus = new EventBus();
    private Set<MCATParameterCollection> registeredSources = new HashSet<>();
    private Map<MCATParameterCollection, String> sourceKeys = new HashMap<>();
    private Map<MCATParameterCollection, MCATDocumentation> sourceDocumentation = new HashMap<>();
    private BiMap<String, MCATParameterAccess> parameters = HashBiMap.create();
    private PriorityQueue<MCATParameterAccess> parametersByPriority = new PriorityQueue<>(MCATParameterAccess::comparePriority);
    private Map<MCATParameterCollection, Integer> sourceOrder = new HashMap<>();

    private boolean ignoreReflectionParameters = false;
    private boolean ignoreCustomParameters = false;
    private boolean forceReflection = false;

    /**
     * Creates a new instance
     *
     * @param sources Parameter collections to add. The list of parents is assumed to be empty for each entry.
     */
    public MCATTraversedParameterCollection(MCATParameterCollection... sources) {
        for (MCATParameterCollection source : sources) {
            add(source, Collections.emptyList());
        }
    }

    /**
     * Sets the key of an {@link MCATParameterCollection}
     * This is used to generate an unique key for sub-parameters
     *
     * @param source the collection
     * @param name   unique key within its parent
     */
    public void setSourceKey(MCATParameterCollection source, String name) {
        sourceKeys.put(source, name);
    }

    /**
     * Gets the key of an {@link MCATParameterCollection}
     *
     * @param source the collection
     * @return the key or null if none was set
     */
    public String getSourceKey(MCATParameterCollection source) {
        return sourceKeys.getOrDefault(source, null);
    }

    /**
     * Gets the UI order of an {@link MCATParameterCollection}
     * This is only used for UI.
     * Lower values indicate that the source should be placed higher.
     *
     * @param source the collection
     * @return UI order. 0 if no order was defined.
     */
    public int getSourceUIOrder(MCATParameterCollection source) {
        return sourceOrder.getOrDefault(source, 0);
    }

    /**
     * Gets the parameters grouped by the source
     *
     * @return
     */
    public Map<MCATParameterCollection, List<MCATParameterAccess>> getGroupedBySource() {
        Map<MCATParameterCollection, List<MCATParameterAccess>> result = parameters.values().stream().collect(Collectors.groupingBy(MCATParameterAccess::getSource));
        for (MCATParameterCollection registeredSource : registeredSources) {
            if (!result.containsKey(registeredSource))
                result.put(registeredSource, new ArrayList<>());
        }
        return result;
    }

    /**
     * Sets the documentation of an {@link MCATParameterCollection}
     * This is queried by UI
     *
     * @param source        the collection
     * @param documentation the documentation
     */
    public void setSourceDocumentation(MCATParameterCollection source, MCATDocumentation documentation) {
        sourceDocumentation.put(source, documentation);
    }

    /**
     * Gets the documentation of an {@link MCATParameterCollection}
     *
     * @param source the collection
     * @return the documentation or null if none was provided
     */
    public MCATDocumentation getSourceDocumentation(MCATParameterCollection source) {
        return sourceDocumentation.getOrDefault(source, null);
    }

    /**
     * Gets the documentation of an {@link MCATParameterCollection}
     *
     * @param source the collection
     * @return the documentation or an empty string if none was provided
     */
    public String getSourceDocumentationName(MCATParameterCollection source) {
        MCATDocumentation documentation = sourceDocumentation.getOrDefault(source, null);
        if (documentation != null) {
            return "" + documentation.name();
        }
        return "";
    }

    /**
     * Gets the unique key of an parameter access
     *
     * @param parameterAccess the access
     * @return unique key
     */
    public String getUniqueKey(MCATParameterAccess parameterAccess) {
        return parameters.inverse().get(parameterAccess);
    }

    /**
     * Adds a new {@link MCATParameterCollection} into this collection.
     * Ignores sources that have already been added
     *
     * @param source    the added collection
     * @param hierarchy hierarchy behind this parameter collection
     */
    public void add(MCATParameterCollection source, List<MCATParameterCollection> hierarchy) {
        if (registeredSources.contains(source))
            return;
        if (!forceReflection && source instanceof MCATCustomParameterCollection) {
            if (ignoreCustomParameters)
                return;
            for (Map.Entry<String, MCATParameterAccess> entry : ((MCATCustomParameterCollection) source).getParameters().entrySet()) {
                addParameter(entry.getKey(), entry.getValue(), hierarchy);
            }
        } else {
            if (ignoreReflectionParameters)
                return;
            addReflectionParameters(source, hierarchy);
        }
        registeredSources.add(source);
        source.getEventBus().register(this);
    }

    private void addParameter(String initialKey, MCATParameterAccess parameterAccess, List<MCATParameterCollection> hierarchy) {
        List<String> keys = new ArrayList<>();
        for (MCATParameterCollection collection : hierarchy) {
            keys.add(sourceKeys.getOrDefault(collection, ""));
        }
        keys.add(initialKey);
        String key = String.join("/", keys);
        parameters.put(key, parameterAccess);
        parametersByPriority.add(parameterAccess);
    }

    private void addReflectionParameters(MCATParameterCollection source, List<MCATParameterCollection> hierarchy) {

        // Find getter and setter pairs
        Map<String, GetterSetterPair> getterSetterPairs = new HashMap<>();
        for (Method method : source.getClass().getMethods()) {
            MCATParameter[] parameterAnnotations = method.getAnnotationsByType(MCATParameter.class);
            if (parameterAnnotations.length == 0)
                continue;
            MCATParameter parameterAnnotation = parameterAnnotations[0];

            String key = parameterAnnotation.value();
            GetterSetterPair pair = getterSetterPairs.getOrDefault(key, null);
            if (pair == null) {
                pair = new GetterSetterPair();
                getterSetterPairs.put(key, pair);
            }
            if (method.getParameters().length == 1) {
                // This is a setter
                pair.setter = method;
            } else {
                pair.getter = method;
            }
        }

        // Add parameters of this source. Sub-parameters are excluded
        for (Map.Entry<String, GetterSetterPair> entry : getterSetterPairs.entrySet()) {
            GetterSetterPair pair = entry.getValue();
            if (pair.getFieldClass() != null && !MCATParameterCollection.class.isAssignableFrom(pair.getFieldClass())) {
                if (pair.getter == null || pair.setter == null)
                    throw new RuntimeException("Invalid parameter definition: Getter or setter could not be found for key '" + entry.getKey() + "' in " + source);

                MCATReflectionParameterAccess parameterAccess = new MCATReflectionParameterAccess();
                parameterAccess.setSource(source);
                parameterAccess.setKey(entry.getKey());
                parameterAccess.setGetter(pair.getter);
                parameterAccess.setSetter(pair.setter);
                parameterAccess.setShortKey(pair.getShortKey());
                parameterAccess.setUIOrder(pair.getUIOrder());
                parameterAccess.setDocumentation(pair.getDocumentation());
                parameterAccess.setVisibility(pair.getVisibility());
                parameterAccess.setPriority(pair.getPriority());

                addParameter(entry.getKey(), parameterAccess, hierarchy);
            }
        }

        // Add sub-parameters
        for (Map.Entry<String, GetterSetterPair> entry : getterSetterPairs.entrySet()) {
            GetterSetterPair pair = entry.getValue();
            if (pair.getFieldClass() != null && MCATParameterCollection.class.isAssignableFrom(pair.getFieldClass())) {
                try {
                    MCATParameterCollection subParameters = (MCATParameterCollection) pair.getter.invoke(source);
                    if (subParameters == null)
                        continue;
                    setSourceDocumentation(subParameters, pair.getDocumentation());
                    setSourceKey(subParameters, entry.getKey());
                    setSourceUIOrder(subParameters, entry.getValue().getUIOrder());
                    List<MCATParameterCollection> subParameterHierarchy = new ArrayList<>(hierarchy);
                    subParameterHierarchy.add(subParameters);
                    add(subParameters, subParameterHierarchy);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Sets the UI order of a source
     *
     * @param collection the source
     * @param uiOrder    the UI order
     */
    public void setSourceUIOrder(MCATParameterCollection collection, int uiOrder) {
        sourceOrder.put(collection, uiOrder);
    }

    @Override
    public Map<String, MCATParameterAccess> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    /**
     * Triggered when a source informs that the parameter structure was changed.
     * Passes the event to listeners.
     *
     * @param event generated event
     */
    @Subscribe
    public void onParameterStructureChanged(ParameterStructureChangedEvent event) {
        eventBus.post(event);
    }

    /**
     * Triggered when a parameter value was changed.
     * Passes the event to listeners.
     *
     * @param event generated event
     */
    @Subscribe
    public void onParameterChangedEvent(ParameterChangedEvent event) {
        eventBus.post(event);
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    public boolean isIgnoreReflectionParameters() {
        return ignoreReflectionParameters;
    }

    public void setIgnoreReflectionParameters(boolean ignoreReflectionParameters) {
        this.ignoreReflectionParameters = ignoreReflectionParameters;
    }

    public boolean isIgnoreCustomParameters() {
        return ignoreCustomParameters;
    }

    public void setIgnoreCustomParameters(boolean ignoreCustomParameters) {
        this.ignoreCustomParameters = ignoreCustomParameters;
    }

    public PriorityQueue<MCATParameterAccess> getParametersByPriority() {
        return parametersByPriority;
    }

    public Set<MCATParameterCollection> getRegisteredSources() {
        return Collections.unmodifiableSet(registeredSources);
    }

    public boolean isForceReflection() {
        return forceReflection;
    }

    public void setForceReflection(boolean forceReflection) {
        this.forceReflection = forceReflection;
    }

    /**
     * Accesses the parameters of a collection
     *
     * @param collection the collection
     * @return traversed parameters
     */
    public static Map<String, MCATParameterAccess> getParameters(MCATParameterCollection collection) {
        return (new MCATTraversedParameterCollection(collection)).getParameters();
    }

    /**
     * Accesses the parameters of a collection
     *
     * @param collection     the collection
     * @param withReflection if reflection parameters are included
     * @param withDynamic    if dynamic/custom parameters are included
     * @return traversed parameters
     */
    public static Map<String, MCATParameterAccess> getParameters(MCATParameterCollection collection, boolean withReflection, boolean withDynamic) {
        MCATTraversedParameterCollection traversedParameterCollection = new MCATTraversedParameterCollection();
        traversedParameterCollection.setIgnoreReflectionParameters(!withReflection);
        traversedParameterCollection.setIgnoreCustomParameters(!withDynamic);
        traversedParameterCollection.add(collection, Collections.emptyList());
        return traversedParameterCollection.parameters;
    }

    /**
     * Pair of getter and setter
     */
    private static class GetterSetterPair {
        public Method getter;
        public Method setter;

        public Class<?> getFieldClass() {
            return getter != null ? getter.getReturnType() : null;
        }

        public MCATParameterVisibility getVisibility() {
            MCATParameter getterAnnotation = getter.getAnnotation(MCATParameter.class);
            if (setter == null)
                return getterAnnotation.visibility();
            MCATParameter setterAnnotation = setter.getAnnotation(MCATParameter.class);
            return getterAnnotation.visibility().intersectWith(setterAnnotation.visibility());
        }

        public double getPriority() {
            MCATParameter getterAnnotation = getter.getAnnotation(MCATParameter.class);
            if (setter == null)
                return getterAnnotation.priority();
            MCATParameter setterAnnotation = setter.getAnnotation(MCATParameter.class);
            return getterAnnotation.priority() != Priority.NORMAL ? getterAnnotation.priority() : setterAnnotation.priority();
        }

        public String getShortKey() {
            MCATParameter getterAnnotation = getter.getAnnotation(MCATParameter.class);
            if (!StringUtils.isNullOrEmpty(getterAnnotation.shortKey()))
                return getterAnnotation.shortKey();
            MCATParameter setterAnnotation = setter.getAnnotation(MCATParameter.class);
            return setterAnnotation.shortKey();
        }

        public int getUIOrder() {
            MCATParameter getterAnnotation = getter.getAnnotation(MCATParameter.class);
            if (setter == null)
                return getterAnnotation.uiOrder();
            MCATParameter setterAnnotation = setter.getAnnotation(MCATParameter.class);
            return getterAnnotation.uiOrder() != 0 ? getterAnnotation.uiOrder() : setterAnnotation.uiOrder();
        }

        public MCATDocumentation getDocumentation() {
            MCATDocumentation[] documentations = getter.getAnnotationsByType(MCATDocumentation.class);
            if (documentations.length > 0)
                return documentations[0];
            if (setter == null)
                return null;
            documentations = setter.getAnnotationsByType(MCATDocumentation.class);
            return documentations.length > 0 ? documentations[0] : null;
        }
    }
}
