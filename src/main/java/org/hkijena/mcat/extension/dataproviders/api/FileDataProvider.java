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
package org.hkijena.mcat.extension.dataproviders.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.events.ParameterChangedEvent;
import org.hkijena.mcat.api.parameters.MCATParameter;
import org.hkijena.mcat.api.parameters.MCATParameterCollection;
import org.hkijena.mcat.extension.parameters.editors.FilePathParameterSettings;
import org.hkijena.mcat.ui.components.FileSelection;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class FileDataProvider implements MCATParameterCollection, MCATDataProvider {
    private EventBus eventBus = new EventBus();
    private Path filePath;

    public FileDataProvider() {

    }

    public FileDataProvider(FileDataProvider other) {
        this.filePath = other.filePath;
    }

    @Override
    public MCATDataProvider duplicate() {
        try {
            return getClass().getConstructor(getClass()).newInstance(this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @MCATDocumentation(name = "File path")
    @MCATParameter("file-path")
    @JsonGetter("file-path")
    @FilePathParameterSettings(ioMode = FileSelection.IOMode.Open, pathMode = FileSelection.PathMode.FilesOnly)
    public Path getFilePath() {
        return filePath;
    }

    @MCATParameter("file-path")
    @JsonSetter("file-path")
    public void setFilePath(Path filePath) {
        this.filePath = filePath;
        eventBus.post(new ParameterChangedEvent(this, "file-path"));
    }

    @Override
    public boolean isValid() {
        return filePath != null && Files.exists(filePath);
    }

    @Override
    public void reportValidity(MCATValidityReport report) {
        if (!Files.exists(filePath)) {
            report.forCategory("File path").reportIsInvalid("File path is invalid!",
                    "The selected file '" + filePath + "' does not exist!",
                    "Please select a valid file.",
                    this);
        }
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
}
