package com.demandware.studio.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DWSettingsConfigurable implements SearchableConfigurable, Configurable.NoScroll, Disposable {
    private DWSettingsPanel settingsPanel;
    private final DWSettingsProvider mySettingsProvider;

    public DWSettingsConfigurable(Project project) {
        this.mySettingsProvider = DWSettingsProvider.getInstance(project);
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Demandware";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public void dispose() {
        settingsPanel = null;
    }

    @NotNull
    @Override
    public String getId() {
        return "Demandware";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        settingsPanel = new DWSettingsPanel(mySettingsProvider, true);
        return settingsPanel.createPanel();
    }

    @Override
    public boolean isModified() {
        return settingsPanel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        settingsPanel.apply();
    }

    @Override
    public void reset() {
        settingsPanel.reset();
    }

    @Override
    public void disposeUIResources() {
        Disposer.dispose(this);
    }
}
