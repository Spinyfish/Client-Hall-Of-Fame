/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.unsupported;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

public final class UnsupportedMethods {
    private final String className;
    private final Set<String> methodNames;

    public UnsupportedMethods(String className, Set<String> methodNames) {
        this.className = className;
        this.methodNames = Collections.unmodifiableSet(methodNames);
    }

    public String getClassName() {
        return this.className;
    }

    public final boolean findMatch() {
        try {
            for (Method method : Class.forName(this.className).getDeclaredMethods()) {
                if (!this.methodNames.contains(method.getName())) continue;
                return true;
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        return false;
    }
}

