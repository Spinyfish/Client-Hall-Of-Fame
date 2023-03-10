/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.gson.internal.reflect;

import com.viaversion.viaversion.libs.gson.internal.reflect.ReflectionAccessor;
import java.lang.reflect.AccessibleObject;

final class PreJava9ReflectionAccessor
extends ReflectionAccessor {
    PreJava9ReflectionAccessor() {
    }

    @Override
    public void makeAccessible(AccessibleObject ao) {
        ao.setAccessible(true);
    }
}

