package com.demandware.studio.toolWindow;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

public class DWToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        DWConsoleService consoleService = ServiceManager.getService(project, DWConsoleService.class);
        ConsoleView consoleView = consoleService.getConsoleView();
        Content content = toolWindow.getContentManager().getFactory().createContent(consoleView.getComponent(), "", true);
        toolWindow.getContentManager().addContent(content);
    }
}
