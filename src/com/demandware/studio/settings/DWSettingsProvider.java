package com.demandware.studio.settings;

import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;


@State(
        name = "DWSettingsProvider",
        storages = {
                @Storage(file = StoragePathMacros.WORKSPACE_FILE)
        }
)
public class DWSettingsProvider implements PersistentStateComponent<DWSettingsProvider.State> {
    private State myState = new State();
    public static final Logger LOG = Logger.getInstance(DWSettingsProvider.class);

    public static DWSettingsProvider getInstance(Project project) {
        return ServiceManager.getService(project, DWSettingsProvider.class);
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
            password = PasswordSafe.getInstance().getPassword(null, DWSettingsPanel.class, "DW_SETTINGS_PASSWORD_KEY");
        } catch (PasswordSafeException e) {
            LOG.info("Couldn't get password for key [DW_SETTINGS_PASSWORD_KEY]", e);
            password = "";
        }
        return StringUtil.notNullize(password);
    }


    public void setPassword(String password) {
        try {
            PasswordSafe.getInstance().storePassword(null, DWSettingsPanel.class, "DW_SETTINGS_PASSWORD_KEY", password != null ? password : "");
        } catch (PasswordSafeException e) {
            LOG.info("Couldn't set password for key [DW_SETTINGS_PASSWORD_KEY]", e);
        }
    }

    public String getVersion() {
        return myState.version;
    }

    public void setVersion(String version) {
        myState.version = version;
    }

    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(State state) {
        myState.hostname = state.hostname;
        myState.username = state.username;
        myState.password = state.password;
        myState.version = state.version;
    }

    public static class State {
        public String hostname;
        public String username;
        public String password;
        public String version;
    }
}
