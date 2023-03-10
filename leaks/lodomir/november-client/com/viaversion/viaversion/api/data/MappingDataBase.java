/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.data;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.IntArrayMappings;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.TagData;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.util.Int2IntBiHashMap;
import com.viaversion.viaversion.util.Int2IntBiMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MappingDataBase
implements MappingData {
    protected final String oldVersion;
    protected final String newVersion;
    protected final boolean hasDiffFile;
    protected Int2IntBiMap itemMappings;
    protected ParticleMappings particleMappings;
    protected Mappings blockMappings;
    protected Mappings blockStateMappings;
    protected Mappings blockEntityMappings;
    protected Mappings soundMappings;
    protected Mappings statisticsMappings;
    protected Map<RegistryType, List<TagData>> tags;
    protected boolean loadItems = true;

    public MappingDataBase(String oldVersion, String newVersion) {
        this(oldVersion, newVersion, false);
    }

    public MappingDataBase(String oldVersion, String newVersion, boolean hasDiffFile) {
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.hasDiffFile = hasDiffFile;
    }

    @Override
    public void load() {
        this.getLogger().info("Loading " + this.oldVersion + " -> " + this.newVersion + " mappings...");
        JsonObject diffmapping = this.hasDiffFile ? this.loadDiffFile() : null;
        JsonObject oldMappings = MappingDataLoader.loadData("mapping-" + this.oldVersion + ".json", true);
        JsonObject newMappings = MappingDataLoader.loadData("mapping-" + this.newVersion + ".json", true);
        this.blockMappings = this.loadFromObject(oldMappings, newMappings, diffmapping, "blocks");
        this.blockStateMappings = this.loadFromObject(oldMappings, newMappings, diffmapping, "blockstates");
        this.blockEntityMappings = this.loadFromArray(oldMappings, newMappings, diffmapping, "blockentities");
        this.soundMappings = this.loadFromArray(oldMappings, newMappings, diffmapping, "sounds");
        this.statisticsMappings = this.loadFromArray(oldMappings, newMappings, diffmapping, "statistics");
        Mappings particles = this.loadFromArray(oldMappings, newMappings, diffmapping, "particles");
        if (particles != null) {
            this.particleMappings = new ParticleMappings(oldMappings.getAsJsonArray("particles"), newMappings.getAsJsonArray("particles"), particles);
        }
        if (this.loadItems && newMappings.has("items")) {
            this.itemMappings = new Int2IntBiHashMap();
            this.itemMappings.defaultReturnValue(-1);
            MappingDataLoader.mapIdentifiers(this.itemMappings, oldMappings.getAsJsonObject("items"), newMappings.getAsJsonObject("items"), diffmapping != null ? diffmapping.getAsJsonObject("items") : null, true);
        }
        if (diffmapping != null && diffmapping.has("tags")) {
            this.tags = new EnumMap<RegistryType, List<TagData>>(RegistryType.class);
            JsonObject tags = diffmapping.getAsJsonObject("tags");
            if (tags.has(RegistryType.ITEM.resourceLocation())) {
                this.loadTags(RegistryType.ITEM, tags, MappingDataLoader.indexedObjectToMap(newMappings.getAsJsonObject("items")));
            }
            if (tags.has(RegistryType.BLOCK.resourceLocation())) {
                this.loadTags(RegistryType.BLOCK, tags, MappingDataLoader.indexedObjectToMap(newMappings.getAsJsonObject("blocks")));
            }
        }
        this.loadExtras(oldMappings, newMappings, diffmapping);
    }

    private void loadTags(RegistryType type, JsonObject object, Object2IntMap<String> typeMapping) {
        JsonObject tags = object.getAsJsonObject(type.resourceLocation());
        ArrayList<TagData> tagsList = new ArrayList<TagData>(tags.size());
        for (Map.Entry<String, JsonElement> entry : tags.entrySet()) {
            JsonArray array = entry.getValue().getAsJsonArray();
            int[] entries = new int[array.size()];
            int i = 0;
            for (JsonElement element : array) {
                String stringId = element.getAsString();
                if (!typeMapping.containsKey(stringId) && !typeMapping.containsKey(stringId = stringId.replace("minecraft:", ""))) {
                    this.getLogger().warning((Object)((Object)type) + " Tags contains invalid type identifier " + stringId + " in tag " + entry.getKey());
                    continue;
                }
                entries[i++] = typeMapping.getInt(stringId);
            }
            tagsList.add(new TagData(entry.getKey(), entries));
        }
        this.tags.put(type, tagsList);
    }

    @Override
    public int getNewBlockStateId(int id) {
        return this.checkValidity(id, this.blockStateMappings.getNewId(id), "blockstate");
    }

    @Override
    public int getNewBlockId(int id) {
        return this.checkValidity(id, this.blockMappings.getNewId(id), "block");
    }

    @Override
    public int getNewItemId(int id) {
        return this.checkValidity(id, this.itemMappings.get(id), "item");
    }

    @Override
    public int getOldItemId(int id) {
        int oldId = this.itemMappings.inverse().get(id);
        return oldId != -1 ? oldId : 1;
    }

    @Override
    public int getNewParticleId(int id) {
        return this.checkValidity(id, this.particleMappings.getMappings().getNewId(id), "particles");
    }

    @Override
    public @Nullable List<TagData> getTags(RegistryType type) {
        return this.tags != null ? this.tags.get((Object)type) : null;
    }

    @Override
    public @Nullable Int2IntBiMap getItemMappings() {
        return this.itemMappings;
    }

    @Override
    public @Nullable ParticleMappings getParticleMappings() {
        return this.particleMappings;
    }

    @Override
    public @Nullable Mappings getBlockMappings() {
        return this.blockMappings;
    }

    @Override
    public @Nullable Mappings getBlockEntityMappings() {
        return this.blockEntityMappings;
    }

    @Override
    public @Nullable Mappings getBlockStateMappings() {
        return this.blockStateMappings;
    }

    @Override
    public @Nullable Mappings getSoundMappings() {
        return this.soundMappings;
    }

    @Override
    public @Nullable Mappings getStatisticsMappings() {
        return this.statisticsMappings;
    }

    protected @Nullable Mappings loadFromArray(JsonObject oldMappings, JsonObject newMappings, @Nullable JsonObject diffMappings, String key) {
        if (!oldMappings.has(key) || !newMappings.has(key)) {
            return null;
        }
        JsonObject diff = diffMappings != null ? diffMappings.getAsJsonObject(key) : null;
        return IntArrayMappings.builder().unmapped(oldMappings.getAsJsonArray(key)).mapped(newMappings.getAsJsonArray(key)).diffMappings(diff).build();
    }

    protected @Nullable Mappings loadFromObject(JsonObject oldMappings, JsonObject newMappings, @Nullable JsonObject diffMappings, String key) {
        if (!oldMappings.has(key) || !newMappings.has(key)) {
            return null;
        }
        JsonObject diff = diffMappings != null ? diffMappings.getAsJsonObject(key) : null;
        return IntArrayMappings.builder().unmapped(oldMappings.getAsJsonObject(key)).mapped(newMappings.getAsJsonObject(key)).diffMappings(diff).build();
    }

    protected @Nullable JsonObject loadDiffFile() {
        return MappingDataLoader.loadData("mappingdiff-" + this.oldVersion + "to" + this.newVersion + ".json");
    }

    protected Logger getLogger() {
        return Via.getPlatform().getLogger();
    }

    protected int checkValidity(int id, int mappedId, String type) {
        if (mappedId == -1) {
            this.getLogger().warning(String.format("Missing %s %s for %s %s %d", this.newVersion, type, this.oldVersion, type, id));
            return 0;
        }
        return mappedId;
    }

    protected void loadExtras(JsonObject oldMappings, JsonObject newMappings, @Nullable JsonObject diffMappings) {
    }
}

