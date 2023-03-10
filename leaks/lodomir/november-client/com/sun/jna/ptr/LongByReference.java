/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna.ptr;

import com.sun.jna.ptr.ByReference;

public class LongByReference
extends ByReference {
    public LongByReference() {
        this(0L);
    }

    public LongByReference(long value) {
        super(8);
        this.setValue(value);
    }

    public void setValue(long value) {
        this.getPointer().setLong(0L, value);
    }

    public long getValue() {
        return this.getPointer().getLong(0L);
    }
}

