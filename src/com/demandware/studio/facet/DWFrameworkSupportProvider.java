package com.demandware.studio.facet;

import com.intellij.facet.ui.FacetBasedFrameworkSupportProvider;
import com.intellij.ide.util.frameworkSupport.FrameworkVersion;
import com.intellij.openapi.roots.ModifiableRootModel;

public class DWFrameworkSupportProvider extends FacetBasedFrameworkSupportProvider<DWFacet> {
    public DWFrameworkSupportProvider() {
        super(DWFacetType.INSTANCE);
    }

    @Override
    protected void setupConfiguration(DWFacet facet, ModifiableRootModel rootModel, FrameworkVersion version) {

    }

    @Override
    public String getTitle() {
        return "Demandware";
    }


}
