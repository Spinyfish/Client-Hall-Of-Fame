package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenSwamp extends BiomeGenBase {
   protected BiomeGenSwamp(int id) {
      super(id);
      this.theBiomeDecorator.treesPerChunk = 2;
      this.theBiomeDecorator.flowersPerChunk = 1;
      this.theBiomeDecorator.deadBushPerChunk = 1;
      this.theBiomeDecorator.mushroomsPerChunk = 8;
      this.theBiomeDecorator.reedsPerChunk = 10;
      this.theBiomeDecorator.clayPerChunk = 1;
      this.theBiomeDecorator.waterlilyPerChunk = 4;
      this.theBiomeDecorator.sandPerChunk2 = 0;
      this.theBiomeDecorator.sandPerChunk = 0;
      this.theBiomeDecorator.grassPerChunk = 5;
      this.waterColorMultiplier = 14745518;
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 1, 1, 1));
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return this.worldGeneratorSwamp;
   }

   @Override
   public int getGrassColorAtPos(BlockPos pos) {
      double d0 = GRASS_COLOR_NOISE.func_151601_a((double)pos.getX() * 0.0225, (double)pos.getZ() * 0.0225);
      return d0 < -0.1 ? 5011004 : 6975545;
   }

   @Override
   public int getFoliageColorAtPos(BlockPos pos) {
      return 6975545;
   }

   @Override
   public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
      return BlockFlower.EnumFlowerType.BLUE_ORCHID;
   }

   @Override
   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      double d0 = GRASS_COLOR_NOISE.func_151601_a((double)x * 0.25, (double)z * 0.25);
      if (d0 > 0.0) {
         int i = x & 15;
         int j = z & 15;

         for(int k = 255; k >= 0; --k) {
            if (chunkPrimerIn.getBlockState(j, k, i).getBlock().getMaterial() != Material.air) {
               if (k == 62 && chunkPrimerIn.getBlockState(j, k, i).getBlock() != Blocks.water) {
                  chunkPrimerIn.setBlockState(j, k, i, Blocks.water.getDefaultState());
                  if (d0 < 0.12) {
                     chunkPrimerIn.setBlockState(j, k + 1, i, Blocks.waterlily.getDefaultState());
                  }
               }
               break;
            }
         }
      }

      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }
}
