package com.demandware.studio.webdav.clean;

import com.demandware.studio.projectWizard.DWModuleType;
import com.demandware.studio.webdav.DWServerConnection;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleServiceManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;


public class DWCleanAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();

        if (project != null) {
            for (Module module : ModuleManager.getInstance(project).getModules()) {
                if (ModuleType.get(module) instanceof DWModuleType) {
                    ModuleServiceManager.getService(module, DWServerConnection.class);
                    ProgressManager.getInstance().run(
                            new DWCleanTask(project, module, "Cleaning Cartridges", true, PerformInBackgroundOption.ALWAYS_BACKGROUND)
                    );

                }
            }
        }

    }
}
