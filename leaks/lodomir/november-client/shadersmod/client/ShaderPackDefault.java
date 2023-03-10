/*
 * Decompiled with CFR 0.152.
 */
package shadersmod.client;

import java.io.InputStream;
import shadersmod.client.IShaderPack;
import shadersmod.client.Shaders;

public class ShaderPackDefault
implements IShaderPack {
    @Override
    public void close() {
    }

    @Override
    public InputStream getResourceAsStream(String resName) {
        return ShaderPackDefault.class.getResourceAsStream(resName);
    }

    @Override
    public String getName() {
        return Shaders.packNameDefault;
    }

    @Override
    public boolean hasDirectory(String name) {
        return false;
    }
}

