package net.minecraft.world;

import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderHell;

public class WorldProviderHell extends WorldProvider {
   @Override
   public void registerWorldChunkManager() {
      this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.hell, 0.0F);
      this.isHellWorld = true;
      this.hasNoSky = true;
      this.dimensionId = -1;
   }

   @Override
   public Vec3 getFogColor(float p_76562_1_, float p_76562_2_) {
      return new Vec3(0.2F, 0.03F, 0.03F);
   }

   @Override
   protected void generateLightBrightnessTable() {
      float f = 0.1F;

      for(int i = 0; i <= 15; ++i) {
         float f1 = 1.0F - (float)i / 15.0F;
         this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
      }
   }

   @Override
   public IChunkProvider createChunkGenerator() {
      return new ChunkProviderHell(this.worldObj, this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.worldObj.getSeed());
   }

   @Override
   public boolean isSurfaceWorld() {
      return false;
   }

   @Override
   public boolean canCoordinateBeSpawn(int x, int z) {
      return false;
   }

   @Override
   public float calculateCelestialAngle(long worldTime, float partialTicks) {
      return 0.5F;
   }

   @Override
   public boolean canRespawnHere() {
      return false;
   }

   @Override
   public boolean doesXZShowFog(int x, int z) {
      return true;
   }

   @Override
   public String getDimensionName() {
      return "Nether";
   }

   @Override
   public String getInternalNameSuffix() {
      return "_nether";
   }

   @Override
   public WorldBorder getWorldBorder() {
      return new WorldBorder() {
         @Override
         public double getCenterX() {
            return super.getCenterX() / 8.0;
         }

         @Override
         public double getCenterZ() {
            return super.getCenterZ() / 8.0;
         }
      };
   }
}
