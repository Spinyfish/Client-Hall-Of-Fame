/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;

public class NativeLong
extends IntegerType {
    private static final long serialVersionUID = 1L;
    public static final int SIZE = Native.LONG_SIZE;

    public NativeLong() {
        this(0L);
    }

    public NativeLong(long value) {
        this(value, false);
    }

    public NativeLong(long value, boolean unsigned) {
        super(SIZE, value, unsigned);
    }
}

