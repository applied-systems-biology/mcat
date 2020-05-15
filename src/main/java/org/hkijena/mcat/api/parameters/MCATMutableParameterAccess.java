package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.hkijena.mcat.api.events.ParameterChangedEvent;
import org.hkijena.mcat.utils.JsonUtils;
import org.hkijena.mcat.utils.StringUtils;
import org.scijava.Priority;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * A mutable implementation of {@link MCATParameterAccess}
 */
@JsonDeserialize(using = MCATMutableParameterAccess.Deserializer.class)
public class MCATMutableParameterAccess implements MCATParameterAccess {
    private MCATParameterCollection parameterHolder;
    private String holderName;
    private String holderDescription;
    private String key;
    private String shortKey;
    private String name;
    private String description;
    private MCATParameterVisibility visibility = MCATParameterVisibility.TransitiveVisible;
    private Class<?> fieldClass;
    private Object value;
    private double priority = Priority.NORMAL;
    private Map<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<>();
    private int uiOrder;

    /**
     * Creates a new instance
     */
    public MCATMutableParameterAccess() {
    }

    /**
     * Creates a new independent parameter from the provided access
     *
     * @param other the parameter
     */
    public MCATMutableParameterAccess(MCATParameterAccess other) {
        this.parameterHolder = other.getSource();
        this.key = other.getKey();
        this.name = other.getName();
        this.description = other.getDescription();
        this.visibility = other.getVisibility();
        this.fieldClass = other.getFieldClass();
        this.value = other.get();
    }

    /**
     * Creates a new instance
     *
     * @param parameterHolder The object that holds the parameter
     * @param key             Unique parameter key
     * @param fieldClass      Parameter field type
     */
    public MCATMutableParameterAccess(MCATParameterCollection parameterHolder, String key, Class<?> fieldClass) {
        this.parameterHolder = parameterHolder;
        this.key = key;
        this.fieldClass = fieldClass;
    }

    /**
     * Copies the parameter access
     *
     * @param other The original
     */
    public MCATMutableParameterAccess(MCATMutableParameterAccess other) {
        this.parameterHolder = other.parameterHolder;
        this.holderName = other.holderName;
        this.holderDescription = other.holderDescription;
        this.key = other.key;
        this.name = other.name;
        this.description = other.description;
        this.visibility = other.visibility;
        this.fieldClass = other.fieldClass;
        this.value = other.value;
        this.priority = other.priority;
    }

    @Override
    public String getKey() {
        return key;
    }

    /**
     * For internal use only
     *
     * @param key key of this access
     */
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    @JsonGetter("name")
    public String getName() {
        return name;
    }

    /**
     * Sets the name
     *
     * @param name The name
     */
    @JsonSetter("name")
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @JsonGetter("description")
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description
     *
     * @param description The description
     */
    @JsonSetter("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    @JsonGetter("visibility")
    public MCATParameterVisibility getVisibility() {
        if (visibility == null)
            return MCATParameterVisibility.TransitiveVisible;
        return visibility;
    }

    /**
     * Sets the visibility
     *
     * @param visibility The visibilities
     */
    @JsonSetter("visibility")
    public void setVisibility(MCATParameterVisibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public <T extends Annotation> T getAnnotationOfType(Class<T> klass) {
        return (T) annotationMap.getOrDefault(klass, null);
    }

    @Override
    @JsonGetter("field-class")
    public Class<?> getFieldClass() {
        return fieldClass;
    }

    /**
     * For internal usage only
     *
     * @param fieldClass The parameter class
     */
    @JsonSetter("field-class")
    public void setFieldClass(Class<?> fieldClass) {
        this.fieldClass = fieldClass;
    }

    @Override
    @JsonGetter("value")
    public <T> T get() {
        return (T) value;
    }

    @Override
    @JsonSetter("value")
    public <T> boolean set(T value) {
        this.value = value;

        // Trigger change in parent parameter holder
        if (parameterHolder != null)
            parameterHolder.getEventBus().post(new ParameterChangedEvent(this, getKey()));
        return true;
    }

    @Override
    public MCATParameterCollection getSource() {
        return parameterHolder;
    }

    /**
     * For internal usage only
     *
     * @param parameterHolder The object that holds the parameter
     */
    public void setParameterHolder(MCATParameterCollection parameterHolder) {
        this.parameterHolder = parameterHolder;
    }


    @Override
    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    /**
     * Mutable access to the annotations
     *
     * @return access to the annotations
     */
    public Map<Class<? extends Annotation>, Annotation> getAnnotationMap() {
        return annotationMap;
    }

    public void setAnnotationMap(Map<Class<? extends Annotation>, Annotation> annotationMap) {
        this.annotationMap = annotationMap;
    }

    @Override
    public String getShortKey() {
        return !StringUtils.isNullOrEmpty(shortKey) ? shortKey : getKey();
    }

    public void setShortKey(String shortKey) {
        this.shortKey = shortKey;
    }

    @Override
    public int getUIOrder() {
        return uiOrder;
    }

    public void setUIOrder(int uiOrder) {
        this.uiOrder = uiOrder;
    }

    /**
     * Deserializes {@link MCATMutableParameterAccess}
     */
    public static class Deserializer extends JsonDeserializer<MCATMutableParameterAccess> {
        @Override
        public MCATMutableParameterAccess deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode jsonNode = p.readValueAsTree();
            MCATMutableParameterAccess result = new MCATMutableParameterAccess();
            result.setName(jsonNode.get("name").textValue());
            result.setDescription(jsonNode.get("description").textValue());
            result.setVisibility(JsonUtils.getObjectMapper().readerFor(MCATParameterVisibility.class).readValue(jsonNode.get("visibility")));
            result.setFieldClass(JsonUtils.getObjectMapper().readerFor(Class.class).readValue(jsonNode.get("field-class")));
            result.set(JsonUtils.getObjectMapper().readerFor(result.getFieldClass()).readValue(jsonNode.get("value")));
            return result;
        }
    }
}
