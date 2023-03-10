/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viabackwards.api.data;

import com.google.common.base.Preconditions;
import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.MappedItem;
import com.viaversion.viabackwards.api.data.VBMappingDataLoader;
import com.viaversion.viabackwards.api.data.VBMappings;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataBase;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.util.Map;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BackwardsMappings
extends MappingDataBase {
    private final Class<? extends Protocol> vvProtocolClass;
    private Int2ObjectMap<MappedItem> backwardsItemMappings;
    private Map<String, String> backwardsSoundMappings;
    private Map<String, String> entityNames;

    public BackwardsMappings(String oldVersion, String newVersion, @Nullable Class<? extends Protocol> vvProtocolClass) {
        this(oldVersion, newVersion, vvProtocolClass, false);
    }

    public BackwardsMappings(String oldVersion, String newVersion, @Nullable Class<? extends Protocol> vvProtocolClass, boolean hasDiffFile) {
        super(oldVersion, newVersion, hasDiffFile);
        Preconditions.checkArgument((vvProtocolClass == null || !vvProtocolClass.isAssignableFrom(BackwardsProtocol.class) ? 1 : 0) != 0);
        this.vvProtocolClass = vvProtocolClass;
        this.loadItems = false;
    }

    @Override
    protected void loadExtras(JsonObject oldMappings, JsonObject newMappings, @Nullable JsonObject diffMappings) {
        if (diffMappings != null) {
            JsonObject diffEntityNames;
            JsonObject diffSounds;
            JsonObject diffItems = diffMappings.getAsJsonObject("items");
            if (diffItems != null) {
                this.backwardsItemMappings = VBMappingDataLoader.loadItemMappings(oldMappings.getAsJsonObject("items"), newMappings.getAsJsonObject("items"), diffItems, this.shouldWarnOnMissing("items"));
            }
            if ((diffSounds = diffMappings.getAsJsonObject("sounds")) != null) {
                this.backwardsSoundMappings = VBMappingDataLoader.objectToNamespacedMap(diffSounds);
            }
            if ((diffEntityNames = diffMappings.getAsJsonObject("entitynames")) != null) {
                this.entityNames = VBMappingDataLoader.objectToMap(diffEntityNames);
            }
        }
        if (this.vvProtocolClass != null) {
            this.itemMappings = Via.getManager().getProtocolManager().getProtocol(this.vvProtocolClass).getMappingData().getItemMappings().inverse();
        }
        this.loadVBExtras(oldMappings, newMappings);
    }

    @Override
    protected @Nullable Mappings loadFromArray(JsonObject oldMappings, JsonObject newMappings, @Nullable JsonObject diffMappings, String key) {
        if (!oldMappings.has(key) || !newMappings.has(key)) {
            return null;
        }
        JsonObject diff = diffMappings != null ? diffMappings.getAsJsonObject(key) : null;
        return VBMappings.vbBuilder().unmapped(oldMappings.getAsJsonArray(key)).mapped(newMappings.getAsJsonArray(key)).diffMappings(diff).warnOnMissing(this.shouldWarnOnMissing(key)).build();
    }

    @Override
    protected @Nullable Mappings loadFromObject(JsonObject oldMappings, JsonObject newMappings, @Nullable JsonObject diffMappings, String key) {
        if (!oldMappings.has(key) || !newMappings.has(key)) {
            return null;
        }
        JsonObject diff = diffMappings != null ? diffMappings.getAsJsonObject(key) : null;
        return VBMappings.vbBuilder().unmapped(oldMappings.getAsJsonObject(key)).mapped(newMappings.getAsJsonObject(key)).diffMappings(diff).warnOnMissing(this.shouldWarnOnMissing(key)).build();
    }

    @Override
    protected JsonObject loadDiffFile() {
        return VBMappingDataLoader.loadFromDataDir("mapping-" + this.newVersion + "to" + this.oldVersion + ".json");
    }

    protected void loadVBExtras(JsonObject oldMappings, JsonObject newMappings) {
    }

    protected boolean shouldWarnOnMissing(String key) {
        return !key.equals("blocks") && !key.equals("statistics");
    }

    @Override
    protected Logger getLogger() {
        return ViaBackwards.getPlatform().getLogger();
    }

    @Override
    public int getNewItemId(int id) {
        return this.itemMappings.get(id);
    }

    @Override
    public int getNewBlockId(int id) {
        return this.blockMappings.getNewId(id);
    }

    @Override
    public int getOldItemId(int id) {
        return this.checkValidity(id, this.itemMappings.inverse().get(id), "item");
    }

    public @Nullable MappedItem getMappedItem(int id) {
        return this.backwardsItemMappings != null ? (MappedItem)this.backwardsItemMappings.get(id) : null;
    }

    public @Nullable String getMappedNamedSound(String id) {
        if (this.backwardsSoundMappings == null) {
            return null;
        }
        if (id.indexOf(58) == -1) {
            id = "minecraft:" + id;
        }
        return this.backwardsSoundMappings.get(id);
    }

    public @Nullable String mappedEntityName(String entityName) {
        return this.entityNames.get(entityName);
    }

    public @Nullable Int2ObjectMap<MappedItem> getBackwardsItemMappings() {
        return this.backwardsItemMappings;
    }

    public @Nullable Map<String, String> getBackwardsSoundMappings() {
        return this.backwardsSoundMappings;
    }
}

