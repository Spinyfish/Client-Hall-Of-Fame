package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class Barrier extends EntityFX {
    protected Barrier(final World worldIn, final double p_i46286_2_, final double p_i46286_4_, final double p_i46286_6_, final Item p_i46286_8_) {
        super(worldIn, p_i46286_2_, p_i46286_4_, p_i46286_6_, 0.0D, 0.0D, 0.0D);
        this.setParticleIcon(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(p_i46286_8_));
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.motionX = this.motionY = this.motionZ = 0.0D;
        this.particleGravity = 0.0F;
        this.particleMaxAge = 80;
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
        final float f = this.particleIcon.getMinU();
        final float f1 = this.particleIcon.getMaxU();
        final float f2 = this.particleIcon.getMinV();
        final float f3 = this.particleIcon.getMaxV();
        final float f4 = 0.5F;
        final float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        final float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        final float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        final int i = this.getBrightnessForRender(partialTicks);
        final int j = i >> 16 & 65535;
        final int k = i & 65535;
        worldRendererIn.pos(f5 - p_180434_4_ * 0.5F - p_180434_7_ * 0.5F, f6 - p_180434_5_ * 0.5F, f7 - p_180434_6_ * 0.5F - p_180434_8_ * 0.5F).tex(f1, f3).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).func_181671_a(j, k).endVertex();
        worldRendererIn.pos(f5 - p_180434_4_ * 0.5F + p_180434_7_ * 0.5F, f6 + p_180434_5_ * 0.5F, f7 - p_180434_6_ * 0.5F + p_180434_8_ * 0.5F).tex(f1, f2).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).func_181671_a(j, k).endVertex();
        worldRendererIn.pos(f5 + p_180434_4_ * 0.5F + p_180434_7_ * 0.5F, f6 + p_180434_5_ * 0.5F, f7 + p_180434_6_ * 0.5F + p_180434_8_ * 0.5F).tex(f, f2).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).func_181671_a(j, k).endVertex();
        worldRendererIn.pos(f5 + p_180434_4_ * 0.5F - p_180434_7_ * 0.5F, f6 - p_180434_5_ * 0.5F, f7 + p_180434_6_ * 0.5F - p_180434_8_ * 0.5F).tex(f, f3).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).func_181671_a(j, k).endVertex();
    }

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(final int particleID, final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn, final int... p_178902_15_) {
            return new Barrier(worldIn, xCoordIn, yCoordIn, zCoordIn, Item.getItemFromBlock(Blocks.barrier));
        }
    }
}
