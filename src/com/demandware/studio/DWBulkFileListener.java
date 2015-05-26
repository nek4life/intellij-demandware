package com.demandware.studio;

import com.demandware.studio.projectWizard.DWModuleType;
import com.demandware.studio.settings.DWSettingsProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.messages.MessageBusConnection;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class DWBulkFileListener implements ApplicationComponent, BulkFileListener {
    private MessageBusConnection connection;


    public DWBulkFileListener() {
        connection = ApplicationManager.getApplication().getMessageBus().connect();
    }

    public void initComponent() {
        connection.subscribe(VirtualFileManager.VFS_CHANGES, this);
    }

    public void disposeComponent() {
        connection.disconnect();
    }

    @NotNull
    public String getComponentName() {
        return "DWBulkFileListener";
    }

    @Override
    public void before(@NotNull List<? extends VFileEvent> events) {

    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        DWServerConnection serverConnection = null;

        for (VFileEvent event : events) {
            VirtualFile eventFile = event.getFile();

            if (eventFile != null && eventFile.getPath().contains(File.separator + "cartridge" + File.separator)) {
                for (Project project : projects) {
                    Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(eventFile);

                    if (module != null) {
                        ModuleType CurrentModuleType = ModuleType.get(module);
                        if (CurrentModuleType instanceof DWModuleType) {
                            if (serverConnection == null) {
                                serverConnection = new DWServerConnection(DWSettingsProvider.getInstance(project));
                            }

                            HttpUriRequest request = RequestBuilder.create("GET")
                                    .setUri(serverConnection.getBasePath())
                                    .build();

                            ApplicationManager.getApplication().executeOnPooledThread(new DWServerConnection.RequestThread(
                                    serverConnection.getClient(), serverConnection.getCredientials(), request
                            ));
                        }
                    }
                }
            }
        }
    }
}
