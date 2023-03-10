/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.compatibility.unsafe;

import com.viaversion.viaversion.compatibility.ForcefulFieldModifier;
import java.lang.reflect.Field;
import java.util.Objects;
import sun.misc.Unsafe;

@Deprecated
public final class UnsafeBackedForcefulFieldModifier
implements ForcefulFieldModifier {
    private final Unsafe unsafe;

    public UnsafeBackedForcefulFieldModifier() throws ReflectiveOperationException {
        Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeField.setAccessible(true);
        this.unsafe = (Unsafe)theUnsafeField.get(null);
    }

    @Override
    public void setField(Field field, Object holder, Object object) {
        Objects.requireNonNull(field, "field must not be null");
        Object ufo = holder != null ? holder : this.unsafe.staticFieldBase(field);
        long offset = holder != null ? this.unsafe.objectFieldOffset(field) : this.unsafe.staticFieldOffset(field);
        this.unsafe.putObject(ufo, offset, object);
    }
}

