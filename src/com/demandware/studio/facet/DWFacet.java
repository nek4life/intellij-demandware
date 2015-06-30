package com.demandware.studio.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Disposer;

public class DWFacet extends Facet<DWFacetConfiguration> {
    public DWFacet(FacetType facetType,
                   Module module,
                   String name,
                   DWFacetConfiguration configuration,
                   Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);
        Disposer.register(this, configuration);
    }
}
