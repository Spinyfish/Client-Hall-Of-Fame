package shadersmod.client;

import optifine.*;
import java.io.*;

public class ShaderPackFolder implements IShaderPack
{
    protected File packFile;
    
    public ShaderPackFolder(final String name, final File file) {
        this.packFile = file;
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public InputStream getResourceAsStream(final String resName) {
        try {
            final String s = StrUtils.removePrefixSuffix(resName, "/", "/");
            final File file1 = new File(this.packFile, s);
            InputStream inputStream;
            if (!file1.exists()) {
                inputStream = null;
            }
            else {
                final FileInputStream fileInputStream;
                inputStream = new BufferedInputStream(fileInputStream);
                fileInputStream = new FileInputStream(file1);
            }
            return inputStream;
        }
        catch (Exception var4) {
            return null;
        }
    }
    
    @Override
    public boolean hasDirectory(final String name) {
        final File file1 = new File(this.packFile, name.substring(1));
        return file1.exists() && file1.isDirectory();
    }
    
    @Override
    public String getName() {
        return this.packFile.getName();
    }
}
