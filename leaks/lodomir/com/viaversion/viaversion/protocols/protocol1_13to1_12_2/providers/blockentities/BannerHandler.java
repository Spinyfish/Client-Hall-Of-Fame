/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.blockentities;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.BlockStorage;

public class BannerHandler
implements BlockEntityProvider.BlockEntityHandler {
    private static final int WALL_BANNER_START = 7110;
    private static final int WALL_BANNER_STOP = 7173;
    private static final int BANNER_START = 6854;
    private static final int BANNER_STOP = 7109;

    @Override
    public int transform(UserConnection user, CompoundTag tag) {
        Object name;
        Position position;
        BlockStorage storage = user.get(BlockStorage.class);
        if (!storage.contains(position = new Position((int)this.getLong((NumberTag)tag.get("x")), (short)this.getLong((NumberTag)tag.get("y")), (int)this.getLong((NumberTag)tag.get("z"))))) {
            Via.getPlatform().getLogger().warning("Received an banner color update packet, but there is no banner! O_o " + tag);
            return -1;
        }
        int blockId = storage.get(position).getOriginal();
        Object base = tag.get("Base");
        int color = 0;
        if (base != null) {
            color = ((NumberTag)tag.get("Base")).asInt();
        }
        if (blockId >= 6854 && blockId <= 7109) {
            blockId += (15 - color) * 16;
        } else if (blockId >= 7110 && blockId <= 7173) {
            blockId += (15 - color) * 4;
        } else {
            Via.getPlatform().getLogger().warning("Why does this block have the banner block entity? :(" + tag);
        }
        if (tag.get("Patterns") instanceof ListTag) {
            for (Tag pattern : (ListTag)tag.get("Patterns")) {
                Object c;
                if (!(pattern instanceof CompoundTag) || !((c = ((CompoundTag)pattern).get("Color")) instanceof IntTag)) continue;
                ((IntTag)c).setValue(15 - (Integer)((Tag)c).getValue());
            }
        }
        if ((name = tag.get("CustomName")) instanceof StringTag) {
            ((StringTag)name).setValue(ChatRewriter.legacyTextToJsonString(((StringTag)name).getValue()));
        }
        return blockId;
    }

    private long getLong(NumberTag tag) {
        return tag.asLong();
    }
}

