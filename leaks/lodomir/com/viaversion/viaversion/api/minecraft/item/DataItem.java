/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.minecraft.item;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.gson.annotations.SerializedName;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DataItem
implements Item {
    @SerializedName(value="identifier", alternate={"id"})
    private int identifier;
    private byte amount;
    private short data;
    private CompoundTag tag;

    public DataItem() {
    }

    public DataItem(int identifier, byte amount, short data, @Nullable CompoundTag tag) {
        this.identifier = identifier;
        this.amount = amount;
        this.data = data;
        this.tag = tag;
    }

    public DataItem(Item toCopy) {
        this(toCopy.identifier(), (byte)toCopy.amount(), toCopy.data(), toCopy.tag());
    }

    @Override
    public int identifier() {
        return this.identifier;
    }

    @Override
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    @Override
    public int amount() {
        return this.amount;
    }

    @Override
    public void setAmount(int amount) {
        if (amount > 127 || amount < -128) {
            throw new IllegalArgumentException("Invalid item amount: " + amount);
        }
        this.amount = (byte)amount;
    }

    @Override
    public short data() {
        return this.data;
    }

    @Override
    public void setData(short data) {
        this.data = data;
    }

    @Override
    public @Nullable CompoundTag tag() {
        return this.tag;
    }

    @Override
    public void setTag(@Nullable CompoundTag tag) {
        this.tag = tag;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DataItem item = (DataItem)o;
        if (this.identifier != item.identifier) {
            return false;
        }
        if (this.amount != item.amount) {
            return false;
        }
        if (this.data != item.data) {
            return false;
        }
        return Objects.equals(this.tag, item.tag);
    }

    public int hashCode() {
        int result = this.identifier;
        result = 31 * result + this.amount;
        result = 31 * result + this.data;
        result = 31 * result + (this.tag != null ? this.tag.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Item{identifier=" + this.identifier + ", amount=" + this.amount + ", data=" + this.data + ", tag=" + this.tag + '}';
    }
}

