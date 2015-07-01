package com.demandware.studio.webdav;

import com.demandware.studio.projectWizard.DWModuleType;
import com.demandware.studio.settings.DWSettingsProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DWBulkFileListener implements ApplicationComponent, BulkFileListener {
    private MessageBusConnection connection;
    private static Logger LOG = Logger.getInstance(DWBulkFileListener.class);

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

        for (VFileEvent event : events) {
            VirtualFile eventFile = event.getFile();

            if (eventFile != null && !eventFile.isDirectory()) {
                for (Project project : projects) {
                    Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(eventFile);

                    if (module != null) {
                        ModuleType CurrentModuleType = ModuleType.get(module);

                        // Bail out if auto uploads are not enabled.
                        if (!DWSettingsProvider.getInstance(module).getAutoUploadEnabled()) {
                            return;
                        }

                        if (CurrentModuleType instanceof DWModuleType) {
                            for (VirtualFile sourceRoot : ModuleRootManager.getInstance(module).getSourceRoots()) {
                                if (eventFile.getPath().contains(sourceRoot.getPath())) {
                                    ProgressManager.getInstance().run(
                                        new DWUpdateFileTask(
                                            project,
                                            module,
                                            "Syncing files to: " + DWSettingsProvider.getInstance(module).getHostname(),
                                            true,
                                            PerformInBackgroundOption.ALWAYS_BACKGROUND,
                                            sourceRoot.getPath(),
                                            eventFile.getPath()
                                        )
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
