package org.hkijena.mcat.extension.dataproviders.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.extension.parameters.editors.FilePathParameterSettings;
import org.hkijena.mcat.ui.components.FileSelection;
import org.hkijena.mcat.utils.api.ACAQDocumentation;
import org.hkijena.mcat.utils.api.ACAQValidityReport;
import org.hkijena.mcat.utils.api.events.ParameterChangedEvent;
import org.hkijena.mcat.utils.api.parameters.ACAQParameter;
import org.hkijena.mcat.utils.api.parameters.ACAQParameterCollection;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class FileDataProvider implements ACAQParameterCollection, MCATDataProvider  {
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

    @ACAQDocumentation(name = "File path")
    @ACAQParameter("file-path")
    @JsonGetter("file-path")
    @FilePathParameterSettings(ioMode = FileSelection.IOMode.Open, pathMode = FileSelection.PathMode.FilesOnly)
    public Path getFilePath() {
        return filePath;
    }

    @ACAQParameter("file-path")
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
    public void reportValidity(ACAQValidityReport report) {
        if(!Files.exists(filePath)) {
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
