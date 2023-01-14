package net.minecraft.world.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;

public class SaveHandlerMP implements ISaveHandler {
   @Override
   public WorldInfo loadWorldInfo() {
      return null;
   }

   @Override
   public void checkSessionLock() {
   }

   @Override
   public IChunkLoader getChunkLoader(WorldProvider provider) {
      return null;
   }

   @Override
   public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
   }

   @Override
   public void saveWorldInfo(WorldInfo worldInformation) {
   }

   @Override
   public IPlayerFileData getPlayerNBTManager() {
      return null;
   }

   @Override
   public void flush() {
   }

   @Override
   public File getMapFileFromName(String mapName) {
      return null;
   }

   @Override
   public String getWorldDirectoryName() {
      return "none";
   }

   @Override
   public File getWorldDirectory() {
      return null;
   }
}
