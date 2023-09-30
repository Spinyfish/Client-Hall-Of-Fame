/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import net.minecraft.src.Config;
import net.optifine.util.LongSupplier;

public class NativeMemory {
    private static LongSupplier bufferAllocatedSupplier = NativeMemory.makeLongSupplier(new String[][]{{"sun.misc.SharedSecrets", "getJavaNioAccess", "getDirectBufferPool", "getMemoryUsed"}, {"jdk.internal.misc.SharedSecrets", "getJavaNioAccess", "getDirectBufferPool", "getMemoryUsed"}});
    private static LongSupplier bufferMaximumSupplier = NativeMemory.makeLongSupplier(new String[][]{{"sun.misc.VM", "maxDirectMemory"}, {"jdk.internal.misc.VM", "maxDirectMemory"}});

    public static long getBufferAllocated() {
        return bufferAllocatedSupplier == null ? -1L : bufferAllocatedSupplier.getAsLong();
    }

    public static long getBufferMaximum() {
        return bufferMaximumSupplier == null ? -1L : bufferMaximumSupplier.getAsLong();
    }

    private static LongSupplier makeLongSupplier(String[][] paths) {
        ArrayList<Throwable> list = new ArrayList<Throwable>();
        for (int i = 0; i < paths.length; ++i) {
            String[] astring = paths[i];
            try {
                LongSupplier longsupplier = NativeMemory.makeLongSupplier(astring);
                return longsupplier;
            }
            catch (Throwable throwable) {
                list.add(throwable);
                continue;
            }
        }
        for (Throwable throwable1 : list) {
            Config.warn("" + throwable1.getClass().getName() + ": " + throwable1.getMessage());
        }
        return null;
    }

    private static LongSupplier makeLongSupplier(String[] path) throws Exception {
        if (path.length < 2) {
            return null;
        }
        Class<?> oclass = Class.forName(path[0]);
        Method method = oclass.getMethod(path[1], new Class[0]);
        method.setAccessible(true);
        Object object = null;
        for (int i = 2; i < path.length; ++i) {
            String s = path[i];
            object = method.invoke(object, new Object[0]);
            method = object.getClass().getMethod(s, new Class[0]);
            method.setAccessible(true);
        }
        final Method method1 = method;
        final Object o = object;
        LongSupplier longsupplier = new LongSupplier(){
            private boolean disabled = false;

            @Override
            public long getAsLong() {
                if (this.disabled) {
                    return -1L;
                }
                try {
                    return (Long)method1.invoke(o, new Object[0]);
                }
                catch (Throwable throwable) {
                    Config.warn("" + throwable.getClass().getName() + ": " + throwable.getMessage());
                    this.disabled = true;
                    return -1L;
                }
            }
        };
        return longsupplier;
    }
}

