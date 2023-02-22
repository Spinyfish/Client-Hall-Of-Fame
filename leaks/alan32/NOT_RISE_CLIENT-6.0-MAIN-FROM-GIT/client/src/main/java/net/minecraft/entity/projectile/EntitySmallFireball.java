package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySmallFireball extends EntityFireball {
    public EntitySmallFireball(final World worldIn) {
        super(worldIn);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntitySmallFireball(final World worldIn, final EntityLivingBase shooter, final double accelX, final double accelY, final double accelZ) {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntitySmallFireball(final World worldIn, final double x, final double y, final double z, final double accelX, final double accelY, final double accelZ) {
        super(worldIn, x, y, z, accelX, accelY, accelZ);
        this.setSize(0.3125F, 0.3125F);
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(final MovingObjectPosition movingObject) {
        if (!this.worldObj.isRemote) {
            if (movingObject.entityHit != null) {
                final boolean flag = movingObject.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F);

                if (flag) {
                    this.applyEnchantments(this.shootingEntity, movingObject.entityHit);

                    if (!movingObject.entityHit.isImmuneToFire()) {
                        movingObject.entityHit.setFire(5);
                    }
                }
            } else {
                boolean flag1 = true;

                if (this.shootingEntity != null && this.shootingEntity instanceof EntityLiving) {
                    flag1 = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                }

                if (flag1) {
                    final BlockPos blockpos = movingObject.getBlockPos().offset(movingObject.sideHit);

                    if (this.worldObj.isAirBlock(blockpos)) {
                        this.worldObj.setBlockState(blockpos, Blocks.fire.getDefaultState());
                    }
                }
            }

            this.setDead();
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith() {
        return false;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(final DamageSource source, final float amount) {
        return false;
    }
}
