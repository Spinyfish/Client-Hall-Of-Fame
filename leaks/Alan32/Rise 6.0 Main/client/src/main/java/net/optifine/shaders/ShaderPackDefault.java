package net.optifine.shaders;

import java.io.InputStream;

public class ShaderPackDefault implements IShaderPack {
    public void close() {
    }

    public InputStream getResourceAsStream(final String resName) {
        return ShaderPackDefault.class.getResourceAsStream(resName);
    }

    public String getName() {
        return "(internal)";
    }

    public boolean hasDirectory(final String name) {
        return false;
    }
}
