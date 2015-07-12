package com.demandware.studio.isml;

import com.demandware.studio.AllIcons;
import com.intellij.ide.highlighter.XmlLikeFileType;
import com.intellij.lang.Language;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.text.XmlCharsetDetector;
import com.intellij.xml.util.HtmlUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class ISMLFileType extends XmlLikeFileType {
    @NonNls public static final String DOT_DEFAULT_EXTENSION = ".isml";

    public static final ISMLFileType INSTANCE = new ISMLFileType();

    private ISMLFileType() {
        super(HTMLLanguage.INSTANCE);
    }

    ISMLFileType(Language language) {
        super(language);
    }

    @Override
    @NotNull
    public String getName() {
        return "ISML";
    }

    @Override
    @NotNull
    public String getDescription() {
        return "ISML File";
    }

    @Override
    @NotNull
    public String getDefaultExtension() {
        return "isml";
    }

    @Override
    public Icon getIcon() {
        return AllIcons.DW_ISML_ICON;
    }

    @Override
    public String getCharset(@NotNull final VirtualFile file, @NotNull final byte[] content) {
        String charset = XmlCharsetDetector.extractXmlEncodingFromProlog(content);
        if (charset != null) return charset;
        @NonNls String strContent;
        try {
            strContent = new String(content, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
        Charset c = HtmlUtil.detectCharsetFromMetaTag(strContent);
        return c == null ? null : c.name();
    }

    @Override
    public Charset extractCharsetFromFileContent(@Nullable final Project project, @Nullable final VirtualFile file, @NotNull final CharSequence content) {
        String name = XmlCharsetDetector.extractXmlEncodingFromProlog(content);
        Charset charset = CharsetToolkit.forName(name);

        if (charset != null) {
            return charset;
        }
        return HtmlUtil.detectCharsetFromMetaTag(content);
    }
}
