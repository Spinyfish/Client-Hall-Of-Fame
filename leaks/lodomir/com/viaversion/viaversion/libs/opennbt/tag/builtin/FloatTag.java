/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FloatTag
extends NumberTag {
    public static final int ID = 5;
    private float value;

    public FloatTag() {
        this(0.0f);
    }

    public FloatTag(float value) {
        this.value = value;
    }

    @Override
    @Deprecated
    public Float getValue() {
        return Float.valueOf(this.value);
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = in.readFloat();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeFloat(this.value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FloatTag floatTag = (FloatTag)o;
        return this.value == floatTag.value;
    }

    public int hashCode() {
        return Float.hashCode(this.value);
    }

    @Override
    public final FloatTag clone() {
        return new FloatTag(this.value);
    }

    @Override
    public byte asByte() {
        return (byte)this.value;
    }

    @Override
    public short asShort() {
        return (short)this.value;
    }

    @Override
    public int asInt() {
        return (int)this.value;
    }

    @Override
    public long asLong() {
        return (long)this.value;
    }

    @Override
    public float asFloat() {
        return this.value;
    }

    @Override
    public double asDouble() {
        return this.value;
    }

    @Override
    public int getTagId() {
        return 5;
    }
}

