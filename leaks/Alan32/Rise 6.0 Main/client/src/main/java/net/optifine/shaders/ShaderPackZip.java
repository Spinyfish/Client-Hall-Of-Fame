package net.optifine.shaders;

import com.google.common.base.Joiner;
import net.minecraft.src.Config;
import net.optifine.util.StrUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ShaderPackZip implements IShaderPack {
    protected File packFile;
    protected ZipFile packZipFile;
    protected String baseFolder;

    public ShaderPackZip(final String name, final File file) {
        this.packFile = file;
        this.packZipFile = null;
        this.baseFolder = "";
    }

    public void close() {
        if (this.packZipFile != null) {
            try {
                this.packZipFile.close();
            } catch (final Exception var2) {
            }

            this.packZipFile = null;
        }
    }

    public InputStream getResourceAsStream(final String resName) {
        try {
            if (this.packZipFile == null) {
                this.packZipFile = new ZipFile(this.packFile);
                this.baseFolder = this.detectBaseFolder(this.packZipFile);
            }

            String s = StrUtils.removePrefix(resName, "/");

            if (s.contains("..")) {
                s = this.resolveRelative(s);
            }

            final ZipEntry zipentry = this.packZipFile.getEntry(this.baseFolder + s);
            return zipentry == null ? null : this.packZipFile.getInputStream(zipentry);
        } catch (final Exception var4) {
            return null;
        }
    }

    private String resolveRelative(final String name) {
        final Deque<String> deque = new ArrayDeque();
        final String[] astring = Config.tokenize(name, "/");

        for (int i = 0; i < astring.length; ++i) {
            final String s = astring[i];

            if (s.equals("..")) {
                if (deque.isEmpty()) {
                    return "";
                }

                deque.removeLast();
            } else {
                deque.add(s);
            }
        }

        final String s1 = Joiner.on('/').join(deque);
        return s1;
    }

    private String detectBaseFolder(final ZipFile zip) {
        final ZipEntry zipentry = zip.getEntry("shaders/");

        if (zipentry != null && zipentry.isDirectory()) {
            return "";
        } else {
            final Pattern pattern = Pattern.compile("([^/]+/)shaders/");
            final Enumeration<? extends ZipEntry> enumeration = zip.entries();

            while (enumeration.hasMoreElements()) {
                final ZipEntry zipentry1 = enumeration.nextElement();
                final String s = zipentry1.getName();
                final Matcher matcher = pattern.matcher(s);

                if (matcher.matches()) {
                    final String s1 = matcher.group(1);

                    if (s1 != null) {
                        if (s1.equals("shaders/")) {
                            return "";
                        }

                        return s1;
                    }
                }
            }

            return "";
        }
    }

    public boolean hasDirectory(final String resName) {
        try {
            if (this.packZipFile == null) {
                this.packZipFile = new ZipFile(this.packFile);
                this.baseFolder = this.detectBaseFolder(this.packZipFile);
            }

            final String s = StrUtils.removePrefix(resName, "/");
            final ZipEntry zipentry = this.packZipFile.getEntry(this.baseFolder + s);
            return zipentry != null;
        } catch (final IOException var4) {
            return false;
        }
    }

    public String getName() {
        return this.packFile.getName();
    }
}
