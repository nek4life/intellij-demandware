package com.demandware.studio.ds;

import com.demandware.studio.AllIcons;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DSFileType implements FileType {
    @NonNls
    public static final String DOT_DEFAULT_EXTENSION = ".ds";
    public static final DSFileType INSTANCE = new DSFileType();


    @NotNull
    @Override
    public String getName() {
        return "Demandware Script";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Demandware Script File";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "ds";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return AllIcons.DW_DS_ICON;
    }

    @Override
    public boolean isBinary() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Nullable
    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
        return null;
    }
}
