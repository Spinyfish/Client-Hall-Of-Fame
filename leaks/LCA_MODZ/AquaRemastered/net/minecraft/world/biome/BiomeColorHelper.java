package net.minecraft.world.biome;

import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BiomeColorHelper {
   private static final BiomeColorHelper.ColorResolver GRASS_COLOR = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition) {
         return biome.getGrassColorAtPos(blockPosition);
      }
   };
   private static final BiomeColorHelper.ColorResolver FOLIAGE_COLOR = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition) {
         return biome.getFoliageColorAtPos(blockPosition);
      }
   };
   private static final BiomeColorHelper.ColorResolver WATER_COLOR_MULTIPLIER = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition) {
         return biome.waterColorMultiplier;
      }
   };

   private static int getColorAtPos(IBlockAccess blockAccess, BlockPos pos, BiomeColorHelper.ColorResolver colorResolver) {
      int i = 0;
      int j = 0;
      int k = 0;

      for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(pos.add(-1, 0, -1), pos.add(1, 0, 1))) {
         int l = colorResolver.getColorAtPos(blockAccess.getBiomeGenForCoords(blockpos$mutableblockpos), blockpos$mutableblockpos);
         i += (l & 0xFF0000) >> 16;
         j += (l & 0xFF00) >> 8;
         k += l & 0xFF;
      }

      return (i / 9 & 0xFF) << 16 | (j / 9 & 0xFF) << 8 | k / 9 & 0xFF;
   }

   public static int getGrassColorAtPos(IBlockAccess p_180286_0_, BlockPos p_180286_1_) {
      return getColorAtPos(p_180286_0_, p_180286_1_, GRASS_COLOR);
   }

   public static int getFoliageColorAtPos(IBlockAccess p_180287_0_, BlockPos p_180287_1_) {
      return getColorAtPos(p_180287_0_, p_180287_1_, FOLIAGE_COLOR);
   }

   public static int getWaterColorAtPos(IBlockAccess p_180288_0_, BlockPos p_180288_1_) {
      return getColorAtPos(p_180288_0_, p_180288_1_, WATER_COLOR_MULTIPLIER);
   }

   interface ColorResolver {
      int getColorAtPos(BiomeGenBase var1, BlockPos var2);
   }
}
