package net.minecraft.world.storage;

import net.minecraft.world.WorldSavedData;

public class SaveDataMemoryStorage extends MapStorage {
   public SaveDataMemoryStorage() {
      super((ISaveHandler)null);
   }

   @Override
   public WorldSavedData loadData(Class<? extends WorldSavedData> clazz, String dataIdentifier) {
      return this.loadedDataMap.get(dataIdentifier);
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
