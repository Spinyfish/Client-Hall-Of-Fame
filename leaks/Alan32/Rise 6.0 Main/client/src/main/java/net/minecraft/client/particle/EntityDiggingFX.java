package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityDiggingFX extends EntityFX {
    private final IBlockState field_174847_a;
    private BlockPos field_181019_az;

    protected EntityDiggingFX(final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn, final IBlockState state) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.field_174847_a = state;
        this.setParticleIcon(Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state));
        this.particleGravity = state.getBlock().blockParticleGravity;
        this.particleRed = this.particleGreen = this.particleBlue = 0.6F;
        this.particleScale /= 2.0F;
    }

    public EntityDiggingFX func_174846_a(final BlockPos pos) {
        this.field_181019_az = pos;

        if (this.field_174847_a.getBlock() == Blocks.grass) {
            return this;
        } else {
            final int i = this.field_174847_a.getBlock().colorMultiplier(this.worldObj, pos);
            this.particleRed *= (float) (i >> 16 & 255) / 255.0F;
            this.particleGreen *= (float) (i >> 8 & 255) / 255.0F;
            this.particleBlue *= (float) (i & 255) / 255.0F;
            return this;
        }
    }

    public EntityDiggingFX func_174845_l() {
        this.field_181019_az = new BlockPos(this.posX, this.posY, this.posZ);
        final Block block = this.field_174847_a.getBlock();

        if (block == Blocks.grass) {
            return this;
        } else {
            final int i = block.getRenderColor(this.field_174847_a);
            this.particleRed *= (float) (i >> 16 & 255) / 255.0F;
            this.particleGreen *= (float) (i >> 8 & 255) / 255.0F;
            this.particleBlue *= (float) (i & 255) / 255.0F;
            return this;
        }
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

    public int getBrightnessForRender(final float partialTicks) {
        final int i = super.getBrightnessForRender(partialTicks);
        int j = 0;

        if (this.worldObj.isBlockLoaded(this.field_181019_az)) {
            j = this.worldObj.getCombinedLight(this.field_181019_az, 0);
        }

        return i == 0 ? j : i;
    }

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(final int particleID, final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn, final int... p_178902_15_) {
            return (new EntityDiggingFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, Block.getStateById(p_178902_15_[0]))).func_174845_l();
        }
    }
}
