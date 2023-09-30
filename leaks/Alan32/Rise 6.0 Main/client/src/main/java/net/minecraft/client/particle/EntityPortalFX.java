package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityPortalFX extends EntityFX {
    private final float portalParticleScale;
    private final double portalPosX;
    private final double portalPosY;
    private final double portalPosZ;

    protected EntityPortalFX(final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
        this.portalPosX = this.posX = xCoordIn;
        this.portalPosY = this.posY = yCoordIn;
        this.portalPosZ = this.posZ = zCoordIn;
        final float f = this.rand.nextFloat() * 0.6F + 0.4F;
        this.portalParticleScale = this.particleScale = this.rand.nextFloat() * 0.2F + 0.5F;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F * f;
        this.particleGreen *= 0.3F;
        this.particleRed *= 0.9F;
        this.particleMaxAge = (int) (Math.random() * 10.0D) + 40;
        this.noClip = true;
        this.setParticleTextureIndex((int) (Math.random() * 8.0D));
    }

    /**
     * Renders the particle
     *
     * @param worldRendererIn The WorldRenderer instance
     */
    public void renderParticle(final WorldRenderer worldRendererIn, final Entity entityIn, final float partialTicks, final float p_180434_4_, final float p_180434_5_, final float p_180434_6_, final float p_180434_7_, final float p_180434_8_) {
        float f = ((float) this.particleAge + partialTicks) / (float) this.particleMaxAge;
        f = 1.0F - f;
        f = f * f;
        f = 1.0F - f;
        this.particleScale = this.portalParticleScale * f;
        super.renderParticle(worldRendererIn, entityIn, partialTicks, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
    }

    public int getBrightnessForRender(final float partialTicks) {
        final int i = super.getBrightnessForRender(partialTicks);
        float f = (float) this.particleAge / (float) this.particleMaxAge;
        f = f * f;
        f = f * f;
        final int j = i & 255;
        int k = i >> 16 & 255;
        k = k + (int) (f * 15.0F * 16.0F);

        if (k > 240) {
            k = 240;
        }

        return j | k << 16;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(final float partialTicks) {
        final float f = super.getBrightness(partialTicks);
        float f1 = (float) this.particleAge / (float) this.particleMaxAge;
        f1 = f1 * f1 * f1 * f1;
        return f * (1.0F - f1) + f1;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        float f = (float) this.particleAge / (float) this.particleMaxAge;
        f = -f + f * f * 2.0F;
        f = 1.0F - f;
        this.posX = this.portalPosX + this.motionX * (double) f;
        this.posY = this.portalPosY + this.motionY * (double) f + (double) (1.0F - f);
        this.posZ = this.portalPosZ + this.motionZ * (double) f;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
    }

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(final int particleID, final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn, final int... p_178902_15_) {
            return new EntityPortalFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
