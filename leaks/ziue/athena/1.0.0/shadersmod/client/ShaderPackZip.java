package shadersmod.client;

import optifine.*;
import java.util.zip.*;
import java.io.*;

public class ShaderPackZip implements IShaderPack
{
    protected File packFile;
    protected ZipFile packZipFile;
    
    public ShaderPackZip(final String name, final File file) {
        this.packFile = file;
        this.packZipFile = null;
    }
    
    @Override
    public void close() {
        if (this.packZipFile != null) {
            try {
                this.packZipFile.close();
            }
            catch (Exception ex) {}
            this.packZipFile = null;
        }
    }
    
    @Override
    public InputStream getResourceAsStream(final String resName) {
        try {
            if (this.packZipFile == null) {
                this.packZipFile = new ZipFile(this.packFile);
            }
            final String s = StrUtils.removePrefix(resName, "/");
            final ZipEntry zipentry = this.packZipFile.getEntry(s);
            return (zipentry == null) ? null : this.packZipFile.getInputStream(zipentry);
        }
        catch (Exception var4) {
            return null;
        }
    }
    
    @Override
    public boolean hasDirectory(final String resName) {
        try {
            if (this.packZipFile == null) {
                this.packZipFile = new ZipFile(this.packFile);
            }
            final String s = StrUtils.removePrefix(resName, "/");
            final ZipEntry zipentry = this.packZipFile.getEntry(s);
            return zipentry != null;
        }
        catch (IOException var4) {
            return false;
        }
    }
    
    @Override
    public String getName() {
        return this.packFile.getName();
    }
}
