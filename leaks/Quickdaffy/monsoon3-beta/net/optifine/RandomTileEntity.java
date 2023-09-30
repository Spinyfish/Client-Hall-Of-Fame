/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.IRandomEntity;
import net.optifine.util.TileEntityUtils;

public class RandomTileEntity
implements IRandomEntity {
    private TileEntity tileEntity;

    @Override
    public int getId() {
        return Config.getRandom(this.tileEntity.getPos(), this.tileEntity.getBlockMetadata());
    }

    @Override
    public BlockPos getSpawnPosition() {
        return this.tileEntity.getPos();
    }

    @Override
    public String getName() {
        String s = TileEntityUtils.getTileEntityName(this.tileEntity);
        return s;
    }

    @Override
    public BiomeGenBase getSpawnBiome() {
        return this.tileEntity.getWorld().getBiomeGenForCoords(this.tileEntity.getPos());
    }

    @Override
    public int getHealth() {
        return -1;
    }

    @Override
    public int getMaxHealth() {
        return -1;
    }

    public TileEntity getTileEntity() {
        return this.tileEntity;
    }

    public void setTileEntity(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }
}

