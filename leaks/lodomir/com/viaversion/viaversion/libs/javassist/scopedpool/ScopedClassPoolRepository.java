/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.javassist.scopedpool;

import com.viaversion.viaversion.libs.javassist.ClassPool;
import com.viaversion.viaversion.libs.javassist.scopedpool.ScopedClassPool;
import com.viaversion.viaversion.libs.javassist.scopedpool.ScopedClassPoolFactory;
import java.util.Map;

public interface ScopedClassPoolRepository {
    public void setClassPoolFactory(ScopedClassPoolFactory var1);

    public ScopedClassPoolFactory getClassPoolFactory();

    public boolean isPrune();

    public void setPrune(boolean var1);

    public ScopedClassPool createScopedClassPool(ClassLoader var1, ClassPool var2);

    public ClassPool findClassPool(ClassLoader var1);

    public ClassPool registerClassLoader(ClassLoader var1);

    public Map<ClassLoader, ScopedClassPool> getRegisteredCLs();

    public void clearUnregisteredClassLoaders();

    public void unregisterClassLoader(ClassLoader var1);
}

