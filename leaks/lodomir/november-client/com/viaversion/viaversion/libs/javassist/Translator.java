/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.javassist;

import com.viaversion.viaversion.libs.javassist.CannotCompileException;
import com.viaversion.viaversion.libs.javassist.ClassPool;
import com.viaversion.viaversion.libs.javassist.NotFoundException;

public interface Translator {
    public void start(ClassPool var1) throws NotFoundException, CannotCompileException;

    public void onLoad(ClassPool var1, String var2) throws NotFoundException, CannotCompileException;
}

