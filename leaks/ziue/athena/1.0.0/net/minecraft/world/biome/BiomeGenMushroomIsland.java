package net.minecraft.world.biome;

import net.minecraft.init.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.*;

public class BiomeGenMushroomIsland extends BiomeGenBase
{
    public BiomeGenMushroomIsland(final int p_i1984_1_) {
        super(p_i1984_1_);
        this.theBiomeDecorator.treesPerChunk = -100;
        this.theBiomeDecorator.flowersPerChunk = -100;
        this.theBiomeDecorator.grassPerChunk = -100;
        this.theBiomeDecorator.mushroomsPerChunk = 1;
        this.theBiomeDecorator.bigMushroomsPerChunk = 1;
        this.topBlock = Blocks.mycelium.getDefaultState();
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCreatureList.add(new SpawnListEntry(EntityMooshroom.class, 8, 4, 8));
    }
}
