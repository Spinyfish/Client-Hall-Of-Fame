/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.blockentities;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.BlockStorage;

public class BedHandler
implements BlockEntityProvider.BlockEntityHandler {
    @Override
    public int transform(UserConnection user, CompoundTag tag) {
        Position position;
        BlockStorage storage = user.get(BlockStorage.class);
        if (!storage.contains(position = new Position((int)this.getLong((NumberTag)tag.get("x")), (short)this.getLong((NumberTag)tag.get("y")), (int)this.getLong((NumberTag)tag.get("z"))))) {
            Via.getPlatform().getLogger().warning("Received an bed color update packet, but there is no bed! O_o " + tag);
            return -1;
        }
        int blockId = storage.get(position).getOriginal() - 972 + 748;
        Object color = tag.get("color");
        if (color != null) {
            blockId += ((NumberTag)color).asInt() * 16;
        }
        return blockId;
    }

    private long getLong(NumberTag tag) {
        return tag.asLong();
    }
}

