/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySmallFireball
extends EntityFireball {
    public EntitySmallFireball(World worldIn) {
        super(worldIn);
        this.setSize(0.3125f, 0.3125f);
    }

    public EntitySmallFireball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3125f, 0.3125f);
    }

    public EntitySmallFireball(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
        super(worldIn, x, y, z, accelX, accelY, accelZ);
        this.setSize(0.3125f, 0.3125f);
    }

    @Override
    protected void onImpact(MovingObjectPosition movingObject) {
        if (!this.worldObj.isRemote) {
            if (movingObject.entityHit != null) {
                boolean flag = movingObject.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0f);
                if (flag) {
                    this.applyEnchantments(this.shootingEntity, movingObject.entityHit);
                    if (!movingObject.entityHit.isImmuneToFire()) {
                        movingObject.entityHit.setFire(5);
                    }
                }
            } else {
                BlockPos blockpos;
                boolean flag1 = true;
                if (this.shootingEntity != null && this.shootingEntity instanceof EntityLiving) {
                    flag1 = this.worldObj.getGameRules().getBoolean("mobGriefing");
                }
                if (flag1 && this.worldObj.isAirBlock(blockpos = movingObject.getBlockPos().offset(movingObject.sideHit))) {
                    this.worldObj.setBlockState(blockpos, Blocks.fire.getDefaultState());
                }
            }
            this.setDead();
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }
}

