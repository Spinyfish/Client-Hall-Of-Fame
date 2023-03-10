/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna.win32;

import com.sun.jna.FunctionMapper;
import com.sun.jna.NativeLibrary;
import java.lang.reflect.Method;

public class W32APIFunctionMapper
implements FunctionMapper {
    public static final FunctionMapper UNICODE = new W32APIFunctionMapper(true);
    public static final FunctionMapper ASCII = new W32APIFunctionMapper(false);
    private final String suffix;

    protected W32APIFunctionMapper(boolean unicode) {
        this.suffix = unicode ? "W" : "A";
    }

    @Override
    public String getFunctionName(NativeLibrary library, Method method) {
        String name = method.getName();
        if (!name.endsWith("W") && !name.endsWith("A")) {
            try {
                name = library.getFunction(name + this.suffix, 63).getName();
            }
            catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                // empty catch block
            }
        }
        return name;
    }
}

