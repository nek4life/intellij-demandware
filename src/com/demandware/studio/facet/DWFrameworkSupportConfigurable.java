package com.demandware.studio.facet;

import com.demandware.studio.settings.DWSettingsPanel;
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
    DWSettingsPanel dwSettingsPanel;

    public DWFrameworkSupportConfigurable(FrameworkSupportModel model) {
        dwSettingsPanel = new DWSettingsPanel(new DWSettingsProvider());
    }

    @Nullable
    @Override
    public JComponent getComponent() {
        return dwSettingsPanel.createPanel();
    }

    @Override
    public void addSupport(@NotNull Module module, @NotNull ModifiableRootModel modifiableRootModel, @Nullable Library library) {
        final FacetManager facetManager = FacetManager.getInstance(module);
        ModifiableFacetModel facetModel = facetManager.createModifiableModel();
        DWSettingsProvider dwSettingsProvider = ModuleServiceManager.getService(module, DWSettingsProvider.class);
        dwSettingsProvider.setPasswordKey(UUID.randomUUID().toString());
        dwSettingsProvider.setHostname(dwSettingsPanel.getHostname());
        dwSettingsProvider.setUsername(dwSettingsPanel.getUsername());
        dwSettingsProvider.setPassword(dwSettingsPanel.getPassword());
        dwSettingsProvider.setVersion(dwSettingsPanel.getVersion());
        dwSettingsProvider.setAutoUploadEnabled(dwSettingsPanel.getAutoUploadEnabled());
        Facet facet = FacetManager.getInstance(modifiableRootModel.getModule()).addFacet(DWFacetType.INSTANCE, "Demandware", null);
        facetModel.addFacet(facet);
        facetModel.commit();
    }
}
