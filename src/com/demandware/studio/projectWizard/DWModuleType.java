package com.demandware.studio.projectWizard;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DWModuleType extends ModuleType<DWModuleBuilder> {
    private static final String ID = "DW_MODULE_TYPE";

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
        return AllIcons.General.Information;
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean isOpened) {
        return AllIcons.General.Information;
    }
}
