package com.demandware.studio.toolWindow;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;

public class DWConsoleService {
    private final ConsoleView consoleView;

    public DWConsoleService(Project project) {
        consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
    }

    public ConsoleView getConsoleView() {
        return consoleView;
    }
}
