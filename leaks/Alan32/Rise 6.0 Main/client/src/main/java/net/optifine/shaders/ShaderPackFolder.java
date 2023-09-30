package net.optifine.shaders;

import net.optifine.util.StrUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ShaderPackFolder implements IShaderPack {
    protected File packFile;

    public ShaderPackFolder(final String name, final File file) {
        this.packFile = file;
    }

    public void close() {
    }

    public InputStream getResourceAsStream(final String resName) {
        try {
            final String s = StrUtils.removePrefixSuffix(resName, "/", "/");
            final File file1 = new File(this.packFile, s);
            return !file1.exists() ? null : new BufferedInputStream(new FileInputStream(file1));
        } catch (final Exception var4) {
            return null;
        }
    }

    public boolean hasDirectory(final String name) {
        final File file1 = new File(this.packFile, name.substring(1));
        return file1.exists() && file1.isDirectory();
    }

    public String getName() {
        return this.packFile.getName();
    }
}
