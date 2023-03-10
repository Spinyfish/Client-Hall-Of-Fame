/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.types.minecraft.BaseItemArrayType;
import io.netty.buffer.ByteBuf;

public class ItemArrayType
extends BaseItemArrayType {
    public ItemArrayType() {
        super("Item Array");
    }

    @Override
    public Item[] read(ByteBuf buffer) throws Exception {
        int amount = SHORT.readPrimitive(buffer);
        Item[] array = new Item[amount];
        for (int i = 0; i < amount; ++i) {
            array[i] = (Item)ITEM.read(buffer);
        }
        return array;
    }

    @Override
    public void write(ByteBuf buffer, Item[] object) throws Exception {
        SHORT.writePrimitive(buffer, (short)object.length);
        for (Item o : object) {
            ITEM.write(buffer, o);
        }
    }
}

