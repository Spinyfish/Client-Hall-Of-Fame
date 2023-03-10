/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_14to1_13_2.data;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.RecipeRewriter1_13_2;

public class RecipeRewriter1_14
extends RecipeRewriter1_13_2 {
    public RecipeRewriter1_14(Protocol protocol) {
        super(protocol);
        this.recipeHandlers.put("stonecutting", this::handleStonecutting);
        this.recipeHandlers.put("blasting", this::handleSmelting);
        this.recipeHandlers.put("smoking", this::handleSmelting);
        this.recipeHandlers.put("campfire_cooking", this::handleSmelting);
    }

    public void handleStonecutting(PacketWrapper wrapper) throws Exception {
        Item[] items;
        wrapper.passthrough(Type.STRING);
        for (Item item : items = wrapper.passthrough(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT)) {
            this.rewrite(item);
        }
        this.rewrite(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
    }
}

