/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna.ptr;

import com.sun.jna.Memory;
import com.sun.jna.PointerType;

public abstract class ByReference
extends PointerType {
    protected ByReference(int dataSize) {
        this.setPointer(new Memory(dataSize));
    }
}

