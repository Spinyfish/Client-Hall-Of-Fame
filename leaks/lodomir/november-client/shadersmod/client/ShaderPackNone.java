/*
 * Decompiled with CFR 0.152.
 */
package shadersmod.client;

import java.io.InputStream;
import shadersmod.client.IShaderPack;
import shadersmod.client.Shaders;

public class ShaderPackNone
implements IShaderPack {
    @Override
    public void close() {
    }

    @Override
    public InputStream getResourceAsStream(String resName) {
        return null;
    }

    @Override
    public boolean hasDirectory(String name) {
        return false;
    }

    @Override
    public String getName() {
        return Shaders.packNameNone;
    }
}

