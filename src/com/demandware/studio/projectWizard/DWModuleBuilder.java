package com.demandware.studio.projectWizard;

import com.demandware.studio.facet.DWFacetType;
import com.demandware.studio.settings.DWSettingsProvider;
import com.intellij.facet.FacetManager;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DWModuleBuilder extends ModuleBuilder {
    private String hostname;
    private String username;
    private String password;
    private String version;
    private boolean autoUploadEnabled;

    @Override
    public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        ContentEntry entry = doAddContentEntry(modifiableRootModel);
        DWSettingsProvider settingsProvider = DWSettingsProvider.getInstance(modifiableRootModel.getModule());
        // Must come before Password is set.
        settingsProvider.setPasswordKey(UUID.randomUUID().toString());
        settingsProvider.setHostname(hostname);
        settingsProvider.setUsername(username);
        settingsProvider.setPassword(password);
        settingsProvider.setVersion(version);
        settingsProvider.setAutoUploadEnabled(autoUploadEnabled);

        FacetManager.getInstance(modifiableRootModel.getModule()).addFacet(DWFacetType.INSTANCE, "Demandware", null);
    }

    @Override
    public ModuleType getModuleType() {
        return DWModuleType.getInstance();
    }

    public void updateSettings(String hostname, String username, String password, String version, boolean autoUploadEnabled) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.version = version;
        this.autoUploadEnabled = autoUploadEnabled;
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new DWModuleWizardStep(context);
    }

}
