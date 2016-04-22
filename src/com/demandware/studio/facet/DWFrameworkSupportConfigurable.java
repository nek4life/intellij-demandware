package com.demandware.studio.facet;

import com.demandware.studio.settings.DWSettingsProvider;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.ModifiableFacetModel;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportConfigurable;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.libraries.Library;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.UUID;

public class DWFrameworkSupportConfigurable extends FrameworkSupportConfigurable {
    DWFrameworkSupportConfigurablePanel dwFrameworkSupportConfigurablePanel;

    public DWFrameworkSupportConfigurable(FrameworkSupportModel model) {
        model.setFrameworkComponentEnabled("Demandware", true);
        dwFrameworkSupportConfigurablePanel = new DWFrameworkSupportConfigurablePanel();
    }

    @Nullable
    @Override
    public JComponent getComponent() {
        return dwFrameworkSupportConfigurablePanel.createPanel();
    }

    @Override
    public void addSupport(@NotNull Module module, @NotNull ModifiableRootModel modifiableRootModel, @Nullable Library library) {
        final FacetManager facetManager = FacetManager.getInstance(module);
        ModifiableFacetModel facetModel = facetManager.createModifiableModel();
        DWSettingsProvider dwSettingsProvider = ModuleServiceManager.getService(module, DWSettingsProvider.class);
        dwSettingsProvider.setHostname(dwFrameworkSupportConfigurablePanel.getHostname());
        dwSettingsProvider.setUsername(dwFrameworkSupportConfigurablePanel.getUsername());
        dwSettingsProvider.setPassword(dwFrameworkSupportConfigurablePanel.getPassword());
        dwSettingsProvider.setVersion(dwFrameworkSupportConfigurablePanel.getVersion());
        dwSettingsProvider.setAutoUploadEnabled(dwFrameworkSupportConfigurablePanel.getAutoUploadEnabled());
        Facet facet = FacetManager.getInstance(modifiableRootModel.getModule()).addFacet(DWFacetType.INSTANCE, "Demandware", null);
        facetModel.addFacet(facet);
        facetModel.commit();
    }
}
