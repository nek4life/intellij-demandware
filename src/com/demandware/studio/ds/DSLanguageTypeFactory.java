package com.demandware.studio.ds;

import com.intellij.lang.javascript.JavaScriptFileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class DSLanguageTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(JavaScriptFileType.INSTANCE, "ds");
    }
}
