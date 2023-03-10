/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_17_1to1_18.data;

import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.Protocol1_18To1_17_1;

public final class BackwardsMappings
extends com.viaversion.viabackwards.api.data.BackwardsMappings {
    private final Int2ObjectMap<String> blockEntities = new Int2ObjectOpenHashMap<String>();

    public BackwardsMappings() {
        super("1.18", "1.17", Protocol1_18To1_17_1.class, true);
    }

    @Override
    protected void loadVBExtras(JsonObject oldMappings, JsonObject newMappings) {
        for (Object2IntMap.Entry entry : Protocol1_18To1_17_1.MAPPINGS.blockEntityIds().object2IntEntrySet()) {
            this.blockEntities.put(entry.getIntValue(), (String)entry.getKey());
        }
    }

    public Int2ObjectMap<String> blockEntities() {
        return this.blockEntities;
    }
}

