package com.demandware.studio.settings;

import javax.swing.*;

public class DWSettingsPanel {
    private JTextField hostnameField;
    private JTextField usernameField;
    private JTextField versionField;
    private JPasswordField passwordField;
    private JPanel dwSettingsPanel;
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

    public JPanel createPanel() {
        return dwSettingsPanel;
    }

    public void apply() {
        mySettingsProvider.setHostname(getHostname());
        mySettingsProvider.setUsername(getUsername());
        mySettingsProvider.setPassword(getPassword());
        mySettingsProvider.setVersion(getVersion());
    }

    public void reset() {
        setHostname(mySettingsProvider.getHostname());
        setUsername(mySettingsProvider.getUsername());
        setPassword(mySettingsProvider.getPassword());
        setVersion(mySettingsProvider.getVersion());
    }

    public boolean isModified() {
        return !getHostname().equals(mySettingsProvider.getHostname()) ||
                !getUsername().equals(mySettingsProvider.getUsername()) ||
                !getPassword().equals(mySettingsProvider.getPassword()) ||
                !getVersion().equals(mySettingsProvider.getVersion());
    }
}
