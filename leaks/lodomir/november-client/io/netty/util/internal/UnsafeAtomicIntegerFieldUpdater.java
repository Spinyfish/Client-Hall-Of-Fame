/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import sun.misc.Unsafe;

final class UnsafeAtomicIntegerFieldUpdater<T>
extends AtomicIntegerFieldUpdater<T> {
    private final long offset;
    private final Unsafe unsafe;

    UnsafeAtomicIntegerFieldUpdater(Unsafe unsafe, Class<?> tClass, String fieldName) throws NoSuchFieldException {
        Field field = tClass.getDeclaredField(fieldName);
        if (!Modifier.isVolatile(field.getModifiers())) {
            throw new IllegalArgumentException("Must be volatile");
        }
        this.unsafe = unsafe;
        this.offset = unsafe.objectFieldOffset(field);
    }

    @Override
    public boolean compareAndSet(T obj, int expect, int update) {
        return this.unsafe.compareAndSwapInt(obj, this.offset, expect, update);
    }

    @Override
    public boolean weakCompareAndSet(T obj, int expect, int update) {
        return this.unsafe.compareAndSwapInt(obj, this.offset, expect, update);
    }

    @Override
    public void set(T obj, int newValue) {
        this.unsafe.putIntVolatile(obj, this.offset, newValue);
    }

    @Override
    public void lazySet(T obj, int newValue) {
        this.unsafe.putOrderedInt(obj, this.offset, newValue);
    }

    @Override
    public int get(T obj) {
        return this.unsafe.getIntVolatile(obj, this.offset);
    }
}

