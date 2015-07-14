package com.demandware.studio.ds;

import com.demandware.studio.AllIcons;
import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DSIconProvider extends IconProvider {
    @Nullable
    @Override
    public Icon getIcon(@NotNull PsiElement element, int flags) {
        PsiFile containingFile = element.getContainingFile();
        if (containingFile != null) {
            if (containingFile.getName().contains(".ds")) {
                return AllIcons.DW_DS_ICON;
            }
        }
        return null;
    }
}
