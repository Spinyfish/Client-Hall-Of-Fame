package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class EntityBreakingFX extends EntityFX {
    protected EntityBreakingFX(final World worldIn, final double posXIn, final double posYIn, final double posZIn, final Item p_i1195_8_) {
        this(worldIn, posXIn, posYIn, posZIn, p_i1195_8_, 0);
    }

    protected EntityBreakingFX(final World worldIn, final double posXIn, final double posYIn, final double posZIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn, final Item p_i1197_14_, final int p_i1197_15_) {
        this(worldIn, posXIn, posYIn, posZIn, p_i1197_14_, p_i1197_15_);
        this.motionX *= 0.10000000149011612D;
        this.motionY *= 0.10000000149011612D;
        this.motionZ *= 0.10000000149011612D;
        this.motionX += xSpeedIn;
        this.motionY += ySpeedIn;
        this.motionZ += zSpeedIn;
    }

    protected EntityBreakingFX(final World worldIn, final double posXIn, final double posYIn, final double posZIn, final Item p_i1196_8_, final int p_i1196_9_) {
        super(worldIn, posXIn, posYIn, posZIn, 0.0D, 0.0D, 0.0D);
        this.setParticleIcon(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(p_i1196_8_, p_i1196_9_));
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleGravity = Blocks.snow.blockParticleGravity;
        this.particleScale /= 2.0F;
    }

    public int getFXLayer() {
        return 1;
    }

    /**
     * Renders the particle
     *
     * @param worldRendererIn The WorldRenderer instance
     */
    public void renderParticle(final WorldRenderer worldRendererIn, final Entity entityIn, final float partialTicks, final float p_180434_4_, final float p_180434_5_, final float p_180434_6_, final float p_180434_7_, final float p_180434_8_) {
        float f = ((float) this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
        float f1 = f + 0.015609375F;
        float f2 = ((float) this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
        float f3 = f2 + 0.015609375F;
        final float f4 = 0.1F * this.particleScale;

        if (this.particleIcon != null) {
            f = this.particleIcon.getInterpolatedU(this.particleTextureJitterX / 4.0F * 16.0F);
            f1 = this.particleIcon.getInterpolatedU((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F);
            f2 = this.particleIcon.getInterpolatedV(this.particleTextureJitterY / 4.0F * 16.0F);
            f3 = this.particleIcon.getInterpolatedV((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F);
        }

        final float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        final float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        final float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        final int i = this.getBrightnessForRender(partialTicks);
        final int j = i >> 16 & 65535;
        final int k = i & 65535;
        worldRendererIn.pos(f5 - p_180434_4_ * f4 - p_180434_7_ * f4, f6 - p_180434_5_ * f4, f7 - p_180434_6_ * f4 - p_180434_8_ * f4).tex(f, f3).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).func_181671_a(j, k).endVertex();
        worldRendererIn.pos(f5 - p_180434_4_ * f4 + p_180434_7_ * f4, f6 + p_180434_5_ * f4, f7 - p_180434_6_ * f4 + p_180434_8_ * f4).tex(f, f2).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).func_181671_a(j, k).endVertex();
        worldRendererIn.pos(f5 + p_180434_4_ * f4 + p_180434_7_ * f4, f6 + p_180434_5_ * f4, f7 + p_180434_6_ * f4 + p_180434_8_ * f4).tex(f1, f2).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).func_181671_a(j, k).endVertex();
        worldRendererIn.pos(f5 + p_180434_4_ * f4 - p_180434_7_ * f4, f6 - p_180434_5_ * f4, f7 + p_180434_6_ * f4 - p_180434_8_ * f4).tex(f1, f3).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).func_181671_a(j, k).endVertex();
    }

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(final int particleID, final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn, final int... p_178902_15_) {
            final int i = p_178902_15_.length > 1 ? p_178902_15_[1] : 0;
            return new EntityBreakingFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, Item.getItemById(p_178902_15_[0]), i);
        }
    }

    public static class SlimeFactory implements IParticleFactory {
        public EntityFX getEntityFX(final int particleID, final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn, final int... p_178902_15_) {
            return new EntityBreakingFX(worldIn, xCoordIn, yCoordIn, zCoordIn, Items.slime_ball);
        }
    }

    public static class SnowballFactory implements IParticleFactory {
        public EntityFX getEntityFX(final int particleID, final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn, final int... p_178902_15_) {
            return new EntityBreakingFX(worldIn, xCoordIn, yCoordIn, zCoordIn, Items.snowball);
        }
    }
}
