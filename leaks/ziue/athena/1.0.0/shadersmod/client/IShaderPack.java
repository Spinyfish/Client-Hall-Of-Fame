package shadersmod.client;

import java.io.*;

public interface IShaderPack
{
    String getName();
    
    InputStream getResourceAsStream(final String p0);
    
    boolean hasDirectory(final String p0);
    
    void close();
}
