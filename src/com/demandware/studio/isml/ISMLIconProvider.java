package com.demandware.studio.isml;

import com.demandware.studio.AllIcons;
import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ISMLIconProvider extends IconProvider {
    @Nullable
    @Override
    public Icon getIcon(@NotNull PsiElement element, int flags) {
        PsiFile containingFile = element.getContainingFile();
        if (containingFile != null) {
            if (containingFile.getName().endsWith(".isml")) {
                return AllIcons.DW_ISML_ICON;
            }
        }
        return null;
    }
}
