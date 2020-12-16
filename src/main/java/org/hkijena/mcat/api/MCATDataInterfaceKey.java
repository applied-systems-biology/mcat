/*******************************************************************************
 * Copyright by Dr. Bianca Hoffmann, Ruman Gerst, Dr. Zoltán Cseresnyés and Prof. Dr. Marc Thilo Figge
 * 
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 * 
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 ******************************************************************************/
package org.hkijena.mcat.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hkijena.mcat.api.parameters.MCATParameterCollection;

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
