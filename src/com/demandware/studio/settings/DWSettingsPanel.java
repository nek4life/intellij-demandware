package com.demandware.studio.settings;

import javax.swing.*;

public class DWSettingsPanel {
    private JTextField hostnameField;
    private JTextField usernameField;
    private JTextField versionField;
    private JPasswordField passwordField;
    private JPanel dwSettingsPanel;
    private JCheckBox autoUploadEnabledField;
    private JLabel enabledAutoUploadsLabel;
    private final DWSettingsProvider mySettingsProvider;

    public DWSettingsPanel(DWSettingsProvider provider, boolean resetPanel) {
        mySettingsProvider = provider;

        if (resetPanel) {
            reset();
        }
    }

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

    public JPanel createPanel() {
        return dwSettingsPanel;
    }

    public void apply() {
        mySettingsProvider.setHostname(getHostname());
        mySettingsProvider.setUsername(getUsername());
        mySettingsProvider.setPassword(getPassword());
        mySettingsProvider.setVersion(getVersion());
        mySettingsProvider.setAutoUploadEnabled(getAutoUploadEnabled());
    }

    public void reset() {
        setHostname(mySettingsProvider.getHostname());
        setUsername(mySettingsProvider.getUsername());
        setPassword(mySettingsProvider.getPassword());
        setVersion(mySettingsProvider.getVersion());
        setAutoUploadEnabled(mySettingsProvider.getAutoUploadEnabled());
    }

    public boolean isModified() {
        return !getHostname().equals(mySettingsProvider.getHostname()) ||
                !getUsername().equals(mySettingsProvider.getUsername()) ||
                !getPassword().equals(mySettingsProvider.getPassword()) ||
                !getVersion().equals(mySettingsProvider.getVersion()) ||
                !getAutoUploadEnabled() == mySettingsProvider.getAutoUploadEnabled();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
