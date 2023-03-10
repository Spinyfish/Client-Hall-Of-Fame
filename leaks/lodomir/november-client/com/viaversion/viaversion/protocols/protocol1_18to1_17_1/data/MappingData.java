/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.protocols.protocol1_18to1_17_1.data;

import com.viaversion.viaversion.api.data.MappingDataBase;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MappingData
extends MappingDataBase {
    private final Object2IntMap<String> blockEntityIds = new Object2IntOpenHashMap<String>();

    public MappingData() {
        super("1.17", "1.18", true);
        this.blockEntityIds.defaultReturnValue(-1);
    }

    @Override
    protected void loadExtras(JsonObject oldMappings, JsonObject newMappings, @Nullable JsonObject diffMappings) {
        int i = 0;
        for (JsonElement element : newMappings.getAsJsonArray("blockentities")) {
            String id = element.getAsString();
            this.blockEntityIds.put(id, i++);
        }
    }

    public Object2IntMap<String> blockEntityIds() {
        return this.blockEntityIds;
    }
}

