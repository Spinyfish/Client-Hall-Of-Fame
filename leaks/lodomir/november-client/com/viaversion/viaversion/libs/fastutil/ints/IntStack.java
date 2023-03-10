/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Stack;

public interface IntStack
extends Stack<Integer> {
    @Override
    public void push(int var1);

    public int popInt();

    public int topInt();

    public int peekInt(int var1);

    @Override
    @Deprecated
    default public void push(Integer o) {
        this.push((int)o);
    }

    @Override
    @Deprecated
    default public Integer pop() {
        return this.popInt();
    }

    @Override
    @Deprecated
    default public Integer top() {
        return this.topInt();
    }

    @Override
    @Deprecated
    default public Integer peek(int i) {
        return this.peekInt(i);
    }
}

