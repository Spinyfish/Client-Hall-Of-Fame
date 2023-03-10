/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.NativeLibrary;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public interface InvocationMapper {
    public InvocationHandler getInvocationHandler(NativeLibrary var1, Method var2);
}

