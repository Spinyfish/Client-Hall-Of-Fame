/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna.win32;

import com.sun.jna.Function;
import com.sun.jna.FunctionMapper;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Pointer;
import java.lang.reflect.Method;

public class StdCallFunctionMapper
implements FunctionMapper {
    protected int getArgumentNativeStackSize(Class<?> cls) {
        if (NativeMapped.class.isAssignableFrom(cls)) {
            cls = NativeMappedConverter.getInstance(cls).nativeType();
        }
        if (cls.isArray()) {
            return Pointer.SIZE;
        }
        try {
            return Native.getNativeSize(cls);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown native stack allocation size for " + cls);
        }
    }

    @Override
    public String getFunctionName(NativeLibrary library, Method method) {
        Class<?>[] argTypes;
        String name = method.getName();
        int pop = 0;
        for (Class<?> cls : argTypes = method.getParameterTypes()) {
            pop += this.getArgumentNativeStackSize(cls);
        }
        String decorated = name + "@" + pop;
        int conv = 63;
        try {
            Function func = library.getFunction(decorated, conv);
            name = func.getName();
        }
        catch (UnsatisfiedLinkError e) {
            try {
                Function func = library.getFunction("_" + decorated, conv);
                name = func.getName();
            }
            catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                // empty catch block
            }
        }
        return name;
    }
}

