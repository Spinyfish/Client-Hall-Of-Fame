/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viabackwards.api.data;

import com.viaversion.viabackwards.utils.Block;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MappedLegacyBlockItem {
    private final int id;
    private final short data;
    private final String name;
    private final Block block;
    private BlockEntityHandler blockEntityHandler;

    public MappedLegacyBlockItem(int id, short data, @Nullable String name, boolean block) {
        this.id = id;
        this.data = data;
        this.name = name != null ? "\u00a7f" + name : null;
        this.block = block ? new Block(id, data) : null;
    }

    public int getId() {
        return this.id;
    }

    public short getData() {
        return this.data;
    }

    public String getName() {
        return this.name;
    }

    public boolean isBlock() {
        return this.block != null;
    }

    public Block getBlock() {
        return this.block;
    }

    public boolean hasBlockEntityHandler() {
        return this.blockEntityHandler != null;
    }

    public @Nullable BlockEntityHandler getBlockEntityHandler() {
        return this.blockEntityHandler;
    }

    public void setBlockEntityHandler(@Nullable BlockEntityHandler blockEntityHandler) {
        this.blockEntityHandler = blockEntityHandler;
    }

    @FunctionalInterface
    public static interface BlockEntityHandler {
        public CompoundTag handleOrNewCompoundTag(int var1, CompoundTag var2);
    }
}

