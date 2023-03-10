/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.util.IJsonSerializable;

public class TupleIntJsonSerializable {
    private int integerValue;
    private IJsonSerializable jsonSerializableValue;

    public int getIntegerValue() {
        return this.integerValue;
    }

    public void setIntegerValue(int integerValueIn) {
        this.integerValue = integerValueIn;
    }

    public <T extends IJsonSerializable> T getJsonSerializableValue() {
        return (T)this.jsonSerializableValue;
    }

    public void setJsonSerializableValue(IJsonSerializable jsonSerializableValueIn) {
        this.jsonSerializableValue = jsonSerializableValueIn;
    }
}

