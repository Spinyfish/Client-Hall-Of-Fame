/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.data;

import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import java.util.HashMap;
import java.util.Map;

public class BackwardsMappings
extends com.viaversion.viabackwards.api.data.BackwardsMappings {
    private final Map<String, String> attributeMappings = new HashMap<String, String>();

    public BackwardsMappings() {
        super("1.16", "1.15", Protocol1_16To1_15_2.class, true);
    }

    @Override
    protected void loadVBExtras(JsonObject oldMappings, JsonObject newMappings) {
        for (Map.Entry entry : Protocol1_16To1_15_2.MAPPINGS.getAttributeMappings().entrySet()) {
            this.attributeMappings.put((String)entry.getValue(), (String)entry.getKey());
        }
    }

    public Map<String, String> getAttributeMappings() {
        return this.attributeMappings;
    }
}

