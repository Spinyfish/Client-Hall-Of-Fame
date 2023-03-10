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

public class SkullHandler
implements BlockEntityProvider.BlockEntityHandler {
    private static final int SKULL_WALL_START = 5447;
    private static final int SKULL_END = 5566;

    @Override
    public int transform(UserConnection user, CompoundTag tag) {
        Position position;
        BlockStorage storage = user.get(BlockStorage.class);
        if (!storage.contains(position = new Position((int)this.getLong((NumberTag)tag.get("x")), (short)this.getLong((NumberTag)tag.get("y")), (int)this.getLong((NumberTag)tag.get("z"))))) {
            Via.getPlatform().getLogger().warning("Received an head update packet, but there is no head! O_o " + tag);
            return -1;
        }
        int id = storage.get(position).getOriginal();
        if (id >= 5447 && id <= 5566) {
            Object skullType = tag.get("SkullType");
            if (skullType != null) {
                id += ((NumberTag)tag.get("SkullType")).asInt() * 20;
            }
            if (tag.contains("Rot")) {
                id += ((NumberTag)tag.get("Rot")).asInt();
            }
        } else {
            Via.getPlatform().getLogger().warning("Why does this block have the skull block entity? " + tag);
            return -1;
        }
        return id;
    }

    private long getLong(NumberTag tag) {
        return tag.asLong();
    }
}

