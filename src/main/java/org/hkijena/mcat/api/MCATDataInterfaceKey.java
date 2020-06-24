package org.hkijena.mcat.api;

import org.hkijena.mcat.api.parameters.MCATParameterCollection;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;

import java.util.*;

/**
 * Key used to uniquely identify a {@link MCATDataInterface}
 */
public class MCATDataInterfaceKey {
    private Set<String> dataSetNames = new HashSet<>();
    private Set<MCATParameterCollection> parameters = new HashSet<>();
    private String dataInterfaceName;

    public MCATDataInterfaceKey(String dataInterfaceName) {
        this.dataInterfaceName = dataInterfaceName;
    }

    public void add(MCATDataInterfaceKey key) {
        dataSetNames.addAll(key.dataSetNames);
        parameters.addAll(key.parameters);
    }

    public void addDataSet(String dataSetName) {
        dataSetNames.add(dataSetName);
    }

    public void addParameter(MCATParameterCollection parameter) {
        parameters.add(parameter);
    }

    public void addDataSets(Collection<String> dataSetNames) {
        this.dataSetNames.addAll(dataSetNames);
    }

    public void addParameters(Collection<MCATParameterCollection> parameters) {
        this.parameters.addAll(parameters);
    }

    public Set<String> getDataSetNames() {
        return Collections.unmodifiableSet(dataSetNames);
    }

    public Set<MCATParameterCollection> getParameters() {
        return Collections.unmodifiableSet(parameters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCATDataInterfaceKey that = (MCATDataInterfaceKey) o;
        return dataSetNames.equals(that.dataSetNames) &&
                parameters.equals(that.parameters) &&
                dataInterfaceName.equals(that.dataInterfaceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataSetNames, parameters, dataInterfaceName);
    }

    public String getDataInterfaceName() {
        return dataInterfaceName;
    }

    @Override
    public String toString() {
        return getDataInterfaceName() + " -> @(" + String.join(",", dataSetNames) + ") # " + parameters;
    }

    public <T extends MCATParameterCollection> T getParameterOfType(Class<? extends MCATParameterCollection> parametersClass) {
        return (T) parameters.stream().filter(p -> parametersClass.isAssignableFrom(p.getClass())).findFirst().orElse(null);
    }
}
