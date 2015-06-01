package com.demandware.studio.isml.codeInsight.tags;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlTagNameProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ISMLTagDescriptorsProvider implements XmlElementDescriptorProvider, XmlTagNameProvider {
    private final String[] ismlTagNames = {
            "isactivedatacontext",
            "isactivedatahead",
            "isanalyticsoff",
            "isbreak",
            "iscache",
            "iscomment",
            "iscomponent",
            "iscontent",
            "iscontinue",
            "iscookie",
            "isdecorate",
            "iselse",
            "iselseif",
            "isif",
            "isinclude",
            "isloop",
            "ismodule",
            "isnext",
            "isobject",
            "isprint",
            "isredirect",
            "isremove",
            "isreplace",
            "isscript",
            "isselect",
            "isset",
            "isslot",
            "isstatus",
    };

    @Nullable
    @Override
    public XmlElementDescriptor getDescriptor(XmlTag tag) {
        return new ISMLTagDescriptor(tag.getName(), tag);
    }

    @Override
    public void addTagNameVariants(List<LookupElement> elements, XmlTag tag, String prefix) {
        for (String tagName : ismlTagNames) {
            elements.add(LookupElementBuilder.create(tagName));
        }
    }
}
