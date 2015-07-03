package com.demandware.studio.settings;

import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.intellij.openapi.util.text.StringUtil;

import java.util.UUID;


@State(
    name = "DWSettingsProvider",
    storages = {
        @Storage(file = StoragePathMacros.MODULE_FILE)
    }
)
public class DWSettingsProvider implements PersistentStateComponent<DWSettingsProvider.State> {
    public static final Logger LOG = Logger.getInstance(DWSettingsProvider.class);
    private State myState = new State();

    public static DWSettingsProvider getInstance(Module module) {
        return ModuleServiceManager.getService(module, DWSettingsProvider.class);
    }

    public String getHostname() {
        return myState.hostname;
    }

    public void setHostname(String hostname) {
        myState.hostname = hostname;
    }

    public String getUsername() {
        return myState.username;
    }

    public void setUsername(String username) {
        myState.username = username;
    }

    public String getPassword() {
        String password;
        try {
            password = PasswordSafe.getInstance().getPassword(null, DWSettingsProvider.class, myState.passwordKey);
        } catch (PasswordSafeException e) {
            LOG.info("Couldn't get password for key " + myState.passwordKey, e);
            password = "";
        }
        return StringUtil.notNullize(password);
    }

    public void setPassword(String password) {
        try {
            PasswordSafe.getInstance().storePassword(null, DWSettingsProvider.class, myState.passwordKey, password != null ? password : "");
        } catch (PasswordSafeException e) {
            LOG.info("Couldn't set password for key " + myState.passwordKey, e);
        }
    }

    public String getVersion() {
        return myState.version;
    }

    public void setVersion(String version) {
        myState.version = version;
    }

    public boolean getAutoUploadEnabled() {
        return myState.autoUploadEnabled;
    }

    public void setAutoUploadEnabled(boolean autoUploadEnabled) {
        myState.autoUploadEnabled = autoUploadEnabled;
    }

    public String getPasswordKey() {
        return myState.passwordKey;
    }

    public void setPasswordKey(String passwordKey) {
        myState.passwordKey = passwordKey;
    }

    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(State state) {
        myState.hostname = state.hostname;
        myState.username = state.username;
        myState.passwordKey = state.passwordKey;
        myState.version = state.version;
        myState.autoUploadEnabled = state.autoUploadEnabled;
    }

    public static class State {
        public String hostname;
        public String username;
        public String passwordKey = UUID.randomUUID().toString();
        public String version;
        public boolean autoUploadEnabled;
    }
}
