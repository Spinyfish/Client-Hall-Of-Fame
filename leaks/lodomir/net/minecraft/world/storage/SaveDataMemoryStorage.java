/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.storage;

import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class SaveDataMemoryStorage
extends MapStorage {
    public SaveDataMemoryStorage() {
        super(null);
    }

    @Override
    public WorldSavedData loadData(Class<? extends WorldSavedData> clazz, String dataIdentifier) {
        return (WorldSavedData)this.loadedDataMap.get(dataIdentifier);
    }

    @Override
    public void setData(String dataIdentifier, WorldSavedData data) {
        this.loadedDataMap.put(dataIdentifier, data);
    }

    @Override
    public void saveAllData() {
    }

    @Override
    public int getUniqueDataId(String key) {
        return 0;
    }
}

