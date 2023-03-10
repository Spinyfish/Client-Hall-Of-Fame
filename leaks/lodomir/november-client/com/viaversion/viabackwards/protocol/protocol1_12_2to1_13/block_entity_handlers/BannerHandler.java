/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.block_entity_handlers;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.providers.BackwardsBlockEntityProvider;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;

public class BannerHandler
implements BackwardsBlockEntityProvider.BackwardsBlockEntityHandler {
    private static final int WALL_BANNER_START = 7110;
    private static final int WALL_BANNER_STOP = 7173;
    private static final int BANNER_START = 6854;
    private static final int BANNER_STOP = 7109;

    @Override
    public CompoundTag transform(UserConnection user, int blockId, CompoundTag tag) {
        int color;
        if (blockId >= 6854 && blockId <= 7109) {
            color = blockId - 6854 >> 4;
            tag.put("Base", new IntTag(15 - color));
        } else if (blockId >= 7110 && blockId <= 7173) {
            color = blockId - 7110 >> 2;
            tag.put("Base", new IntTag(15 - color));
        } else {
            ViaBackwards.getPlatform().getLogger().warning("Why does this block have the banner block entity? :(" + tag);
        }
        Object patternsTag = tag.get("Patterns");
        if (patternsTag instanceof ListTag) {
            for (Tag pattern : (ListTag)patternsTag) {
                if (!(pattern instanceof CompoundTag)) continue;
                IntTag c = (IntTag)((CompoundTag)pattern).get("Color");
                c.setValue(15 - c.asInt());
            }
        }
        return tag;
    }
}

