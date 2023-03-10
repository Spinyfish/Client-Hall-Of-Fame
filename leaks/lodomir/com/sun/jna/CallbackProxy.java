/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.Callback;

public interface CallbackProxy
extends Callback {
    public Object callback(Object[] var1);

    public Class<?>[] getParameterTypes();

    public Class<?> getReturnType();
}

