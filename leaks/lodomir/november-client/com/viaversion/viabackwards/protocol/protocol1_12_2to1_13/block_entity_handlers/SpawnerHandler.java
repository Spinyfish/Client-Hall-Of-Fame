/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.block_entity_handlers;

import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data.EntityNameRewrites;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.providers.BackwardsBlockEntityProvider;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;

public class SpawnerHandler
implements BackwardsBlockEntityProvider.BackwardsBlockEntityHandler {
    @Override
    public CompoundTag transform(UserConnection user, int blockId, CompoundTag tag) {
        CompoundTag data;
        Object idTag;
        Object dataTag = tag.get("SpawnData");
        if (dataTag instanceof CompoundTag && (idTag = (data = (CompoundTag)dataTag).get("id")) instanceof StringTag) {
            StringTag s = (StringTag)idTag;
            s.setValue(EntityNameRewrites.rewrite(s.getValue()));
        }
        return tag;
    }
}

