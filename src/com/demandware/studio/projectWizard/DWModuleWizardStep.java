package com.demandware.studio.projectWizard;

import com.demandware.studio.settings.DWSettingsPanel;
import com.demandware.studio.settings.DWSettingsProvider;
import com.intellij.compiler.ant.ProjectBuild;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;

import javax.swing.*;
import java.util.ResourceBundle;

public class DWModuleWizardStep extends ModuleWizardStep {
    private final ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages");
    private final WizardContext myContext;
    private final DWSettingsPanel myPanel;

    public DWModuleWizardStep(WizardContext context) {
        myContext = context;
        myPanel = new DWSettingsPanel(new DWSettingsProvider(), false);
    }

    @Override
    public JComponent getComponent() {
        return myPanel.createPanel();
    }

    @Override
    public void updateDataModel() {
        final ProjectBuilder projectBuilder = myContext.getProjectBuilder();

        if (projectBuilder instanceof DWModuleBuilder) {
            ((DWModuleBuilder) projectBuilder).updateSettings(
                    myPanel.getHostname(), myPanel.getUsername(), myPanel.getPassword(), myPanel.getVersion());
        }
    }
}
