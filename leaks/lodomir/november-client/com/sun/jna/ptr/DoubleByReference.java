/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna.ptr;

import com.sun.jna.ptr.ByReference;

public class DoubleByReference
extends ByReference {
    public DoubleByReference() {
        this(0.0);
    }

    public DoubleByReference(double value) {
        super(8);
        this.setValue(value);
    }

    public void setValue(double value) {
        this.getPointer().setDouble(0L, value);
    }

    public double getValue() {
        return this.getPointer().getDouble(0L);
    }
}

