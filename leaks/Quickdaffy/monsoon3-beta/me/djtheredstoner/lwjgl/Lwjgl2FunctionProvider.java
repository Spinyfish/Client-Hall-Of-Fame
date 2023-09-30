/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GLContext
 *  org.lwjgl.system.FunctionProvider
 */
package me.djtheredstoner.lwjgl;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.FunctionProvider;

public class Lwjgl2FunctionProvider
implements FunctionProvider {
    private final Method m_getFunctionAddress;

    public Lwjgl2FunctionProvider() {
        try {
            this.m_getFunctionAddress = GLContext.class.getDeclaredMethod("getFunctionAddress", String.class);
            this.m_getFunctionAddress.setAccessible(true);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getFunctionAddress(CharSequence functionName) {
        try {
            return (Long)this.m_getFunctionAddress.invoke(null, functionName.toString());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getFunctionAddress(ByteBuffer byteBuffer) {
        throw new UnsupportedOperationException();
    }
}

