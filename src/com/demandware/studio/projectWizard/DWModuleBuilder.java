package com.demandware.studio.projectWizard;

import com.demandware.studio.settings.DWSettingsProvider;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import org.jetbrains.annotations.Nullable;

public class DWModuleBuilder extends ModuleBuilder {
    private String hostname;
    private String username;
    private String password;
    private String version;
    private boolean autoUploadEnabled;

    @Override
    public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        ContentEntry entry = doAddContentEntry(modifiableRootModel);
        DWSettingsProvider settingsProvider = DWSettingsProvider.getInstance(modifiableRootModel.getProject());
        settingsProvider.setHostname(hostname);
        settingsProvider.setUsername(username);
        settingsProvider.setPassword(password);
        settingsProvider.setVersion(version);
        settingsProvider.setAutoUploadEnabled(autoUploadEnabled);
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
