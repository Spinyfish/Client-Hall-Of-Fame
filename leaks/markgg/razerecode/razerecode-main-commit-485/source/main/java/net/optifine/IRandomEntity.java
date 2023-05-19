package net.optifine;

import net.minecraft.util.BlockPosition;
import net.minecraft.world.biome.BiomeGenBase;

public interface IRandomEntity
{
    int getId();

    BlockPosition getSpawnPosition();

    BiomeGenBase getSpawnBiome();

    String getName();

    int getHealth();

    int getMaxHealth();
}
