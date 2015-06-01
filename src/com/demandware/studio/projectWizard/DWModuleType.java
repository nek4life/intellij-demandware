package com.demandware.studio.projectWizard;

import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DWModuleType extends ModuleType<DWModuleBuilder> {
    private static final String ID = "DWModuleType";

    public DWModuleType() {
        super(ID);
    }

    public static DWModuleType getInstance() {
        return (DWModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public DWModuleBuilder createModuleBuilder() {
        return new DWModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "Demandware";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Demandware Studio Module";
    }

    @Override
    public Icon getBigIcon() {
        return IconLoader.getIcon("/icons/demandware.png");
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean isOpened) {
        return IconLoader.getIcon("/icons/demandware.png");
    }
}
