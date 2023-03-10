/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna.ptr;

import com.sun.jna.ptr.ByReference;

public class FloatByReference
extends ByReference {
    public FloatByReference() {
        this(0.0f);
    }

    public FloatByReference(float value) {
        super(4);
        this.setValue(value);
    }

    public void setValue(float value) {
        this.getPointer().setFloat(0L, value);
    }

    public float getValue() {
        return this.getPointer().getFloat(0L);
    }
}

