package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityReddustFX extends EntityFX {
    float reddustParticleScale;

    protected EntityReddustFX(final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final float p_i46349_8_, final float p_i46349_9_, final float p_i46349_10_) {
        this(worldIn, xCoordIn, yCoordIn, zCoordIn, 1.0F, p_i46349_8_, p_i46349_9_, p_i46349_10_);
    }

    protected EntityReddustFX(final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final float p_i46350_8_, float p_i46350_9_, final float p_i46350_10_, final float p_i46350_11_) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.10000000149011612D;
        this.motionY *= 0.10000000149011612D;
        this.motionZ *= 0.10000000149011612D;

        if (p_i46350_9_ == 0.0F) {
            p_i46350_9_ = 1.0F;
        }

        final float f = (float) Math.random() * 0.4F + 0.6F;
        this.particleRed = ((float) (Math.random() * 0.20000000298023224D) + 0.8F) * p_i46350_9_ * f;
        this.particleGreen = ((float) (Math.random() * 0.20000000298023224D) + 0.8F) * p_i46350_10_ * f;
        this.particleBlue = ((float) (Math.random() * 0.20000000298023224D) + 0.8F) * p_i46350_11_ * f;
        this.particleScale *= 0.75F;
        this.particleScale *= p_i46350_8_;
        this.reddustParticleScale = this.particleScale;
        this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
        this.particleMaxAge = (int) ((float) this.particleMaxAge * p_i46350_8_);
        this.noClip = false;
    }

    /**
     * Renders the particle
     *
     * @param worldRendererIn The WorldRenderer instance
     */
    public void renderParticle(final WorldRenderer worldRendererIn, final Entity entityIn, final float partialTicks, final float p_180434_4_, final float p_180434_5_, final float p_180434_6_, final float p_180434_7_, final float p_180434_8_) {
        float f = ((float) this.particleAge + partialTicks) / (float) this.particleMaxAge * 32.0F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        this.particleScale = this.reddustParticleScale * f;
        super.renderParticle(worldRendererIn, entityIn, partialTicks, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY) {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9599999785423279D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(final int particleID, final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn, final int... p_178902_15_) {
            return new EntityReddustFX(worldIn, xCoordIn, yCoordIn, zCoordIn, (float) xSpeedIn, (float) ySpeedIn, (float) zSpeedIn);
        }
    }
}
