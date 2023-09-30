package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySnowball extends EntityThrowable {
    public EntitySnowball(final World worldIn) {
        super(worldIn);
    }

    public EntitySnowball(final World worldIn, final EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntitySnowball(final World worldIn, final double x, final double y, final double z) {
        super(worldIn, x, y, z);
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(final MovingObjectPosition p_70184_1_) {
        if (p_70184_1_.entityHit != null) {
            int i = 0;

            if (p_70184_1_.entityHit instanceof EntityBlaze) {
                i = 3;
            }

            p_70184_1_.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float) i);
        }

        for (int j = 0; j < 8; ++j) {
            this.worldObj.spawnParticle(EnumParticleTypes.SNOWBALL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }

        if (!this.worldObj.isRemote) {
            this.setDead();
        }
    }
}
