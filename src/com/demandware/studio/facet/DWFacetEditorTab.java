package com.demandware.studio.facet;

import com.demandware.studio.settings.DWSettingsProvider;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DWFacetEditorTab extends FacetEditorTab {
    private final DWSettingsProvider mySettingsProvider;

    public DWFacetEditorTab(FacetEditorContext editorContext) {
        mySettingsProvider = DWSettingsProvider.getInstance(editorContext.getModule());
    }

    private JTextField hostnameField;
    private JTextField usernameField;
    private JTextField versionField;
    private JPasswordField passwordField;
    private JPanel dwFacetEditorTab;
    private JCheckBox autoUploadEnabledField;

    public String getHostname() {
        return hostnameField.getText();
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getVersion() {
        return versionField.getText();
    }

    public String getPassword() {
        return String.valueOf(passwordField.getPassword());
    }

    public boolean getAutoUploadEnabled() {
        return autoUploadEnabledField.isSelected();
    }

    public void setHostname(String hostname) {
        hostnameField.setText(hostname);
    }

    public void setUsername(String username) {
        usernameField.setText(username);
    }

    public void setVersion(String version) {
        versionField.setText(version);
    }

    public void setPassword(String password) {
        passwordField.setText(password);
    }

    public void setAutoUploadEnabled(boolean checked) {
        autoUploadEnabledField.setSelected(checked);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        return dwFacetEditorTab;
    }

    @Override
    public void apply() {
        mySettingsProvider.setHostname(getHostname());
        mySettingsProvider.setUsername(getUsername());
        mySettingsProvider.setPassword(getPassword());
        mySettingsProvider.setVersion(getVersion());
        mySettingsProvider.setAutoUploadEnabled(getAutoUploadEnabled());
    }

    @Override
    public void reset() {
        setHostname(mySettingsProvider.getHostname());
        setUsername(mySettingsProvider.getUsername());
        setPassword(mySettingsProvider.getPassword());
        setVersion(mySettingsProvider.getVersion());
        setAutoUploadEnabled(mySettingsProvider.getAutoUploadEnabled());
    }

    @Override
    public boolean isModified() {
        return !getHostname().equals(mySettingsProvider.getHostname()) ||
                !getUsername().equals(mySettingsProvider.getUsername()) ||
                !getPassword().equals(mySettingsProvider.getPassword()) ||
                !getVersion().equals(mySettingsProvider.getVersion()) ||
                !getAutoUploadEnabled() == mySettingsProvider.getAutoUploadEnabled();
    }


    @Override
    public void disposeUIResources() {

    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Demandware";
    }
}
