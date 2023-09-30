/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.IRandomEntity;

public class RandomEntity
implements IRandomEntity {
    private Entity entity;

    @Override
    public int getId() {
        UUID uuid = this.entity.getUniqueID();
        long i = uuid.getLeastSignificantBits();
        int j = (int)(i & Integer.MAX_VALUE);
        return j;
    }

    @Override
    public BlockPos getSpawnPosition() {
        return this.entity.getDataWatcher().spawnPosition;
    }

    @Override
    public BiomeGenBase getSpawnBiome() {
        return this.entity.getDataWatcher().spawnBiome;
    }

    @Override
    public String getName() {
        return this.entity.hasCustomName() ? this.entity.getCustomNameTag() : null;
    }

    @Override
    public int getHealth() {
        if (!(this.entity instanceof EntityLiving)) {
            return 0;
        }
        EntityLiving entityliving = (EntityLiving)this.entity;
        return (int)entityliving.getHealth();
    }

    @Override
    public int getMaxHealth() {
        if (!(this.entity instanceof EntityLiving)) {
            return 0;
        }
        EntityLiving entityliving = (EntityLiving)this.entity;
        return (int)entityliving.getMaxHealth();
    }

    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

