package shadersmod.client;

import java.io.*;

public class ShaderPackNone implements IShaderPack
{
    @Override
    public void close() {
    }
    
    @Override
    public InputStream getResourceAsStream(final String resName) {
        return null;
    }
    
    @Override
    public boolean hasDirectory(final String name) {
        return false;
    }
    
    @Override
    public String getName() {
        return Shaders.packNameNone;
    }
}
