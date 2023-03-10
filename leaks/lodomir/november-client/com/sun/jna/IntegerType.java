/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.FromNativeContext;
import com.sun.jna.NativeMapped;

public abstract class IntegerType
extends Number
implements NativeMapped {
    private static final long serialVersionUID = 1L;
    private int size;
    private Number number;
    private boolean unsigned;
    private long value;

    public IntegerType(int size) {
        this(size, 0L, false);
    }

    public IntegerType(int size, boolean unsigned) {
        this(size, 0L, unsigned);
    }

    public IntegerType(int size, long value) {
        this(size, value, false);
    }

    public IntegerType(int size, long value, boolean unsigned) {
        this.size = size;
        this.unsigned = unsigned;
        this.setValue(value);
    }

    public void setValue(long value) {
        long truncated = value;
        this.value = value;
        switch (this.size) {
            case 1: {
                if (this.unsigned) {
                    this.value = value & 0xFFL;
                }
                truncated = (byte)value;
                this.number = (byte)value;
                break;
            }
            case 2: {
                if (this.unsigned) {
                    this.value = value & 0xFFFFL;
                }
                truncated = (short)value;
                this.number = (short)value;
                break;
            }
            case 4: {
                if (this.unsigned) {
                    this.value = value & 0xFFFFFFFFL;
                }
                truncated = (int)value;
                this.number = (int)value;
                break;
            }
            case 8: {
                this.number = value;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported size: " + this.size);
            }
        }
        if (this.size < 8) {
            long mask = (1L << this.size * 8) - 1L ^ 0xFFFFFFFFFFFFFFFFL;
            if (value < 0L && truncated != value || value >= 0L && (mask & value) != 0L) {
                throw new IllegalArgumentException("Argument value 0x" + Long.toHexString(value) + " exceeds native capacity (" + this.size + " bytes) mask=0x" + Long.toHexString(mask));
            }
        }
    }

    @Override
    public Object toNative() {
        return this.number;
    }

    @Override
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        long value = nativeValue == null ? 0L : ((Number)nativeValue).longValue();
        try {
            IntegerType number = (IntegerType)this.getClass().newInstance();
            number.setValue(value);
            return number;
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException("Can't instantiate " + this.getClass());
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Not allowed to instantiate " + this.getClass());
        }
    }

    @Override
    public Class<?> nativeType() {
        return this.number.getClass();
    }

    @Override
    public int intValue() {
        return (int)this.value;
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return this.number.floatValue();
    }

    @Override
    public double doubleValue() {
        return this.number.doubleValue();
    }

    public boolean equals(Object rhs) {
        return rhs instanceof IntegerType && this.number.equals(((IntegerType)rhs).number);
    }

    public String toString() {
        return this.number.toString();
    }

    public int hashCode() {
        return this.number.hashCode();
    }

    public static <T extends IntegerType> int compare(T v1, T v2) {
        if (v1 == v2) {
            return 0;
        }
        if (v1 == null) {
            return 1;
        }
        if (v2 == null) {
            return -1;
        }
        return IntegerType.compare(v1.longValue(), v2.longValue());
    }

    public static int compare(IntegerType v1, long v2) {
        if (v1 == null) {
            return 1;
        }
        return IntegerType.compare(v1.longValue(), v2);
    }

    public static final int compare(long v1, long v2) {
        if (v1 == v2) {
            return 0;
        }
        if (v1 < v2) {
            return -1;
        }
        return 1;
    }
}

