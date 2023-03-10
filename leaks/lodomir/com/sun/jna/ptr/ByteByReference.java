/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna.ptr;

import com.sun.jna.ptr.ByReference;

public class ByteByReference
extends ByReference {
    public ByteByReference() {
        this(0);
    }

    public ByteByReference(byte value) {
        super(1);
        this.setValue(value);
    }

    public void setValue(byte value) {
        this.getPointer().setByte(0L, value);
    }

    public byte getValue() {
        return this.getPointer().getByte(0L);
    }
}

