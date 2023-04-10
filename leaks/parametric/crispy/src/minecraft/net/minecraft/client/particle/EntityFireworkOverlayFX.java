package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFireworkOverlayFX extends EntityFX
{

    protected EntityFireworkOverlayFX(World worldIn, double posXIn, double posYIn, double posZIn)
    {
        super(worldIn, posXIn, posYIn, posZIn);
        this.particleMaxAge = 4;
    }

    /**
     * Renders the particle
     *  
     * @param worldRendererIn The WorldRenderer instance
     */
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_)
    {
        float var9 = 0.25F;
        float var10 = var9 + 0.25F;
        float var11 = 0.125F;
        float var12 = var11 + 0.25F;
        float var13 = 7.1F * MathHelper.sin(((float)this.particleAge + partialTicks - 1.0F) * 0.25F * (float)Math.PI);
        this.particleAlpha = 0.6F - ((float)this.particleAge + partialTicks - 1.0F) * 0.25F * 0.5F;
        float var14 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float var15 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float var16 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        worldRendererIn.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        worldRendererIn.addVertexWithUV((double)(var14 - p_180434_4_ * var13 - p_180434_7_ * var13), (double)(var15 - p_180434_5_ * var13), (double)(var16 - p_180434_6_ * var13 - p_180434_8_ * var13), (double)var10, (double)var12);
        worldRendererIn.addVertexWithUV((double)(var14 - p_180434_4_ * var13 + p_180434_7_ * var13), (double)(var15 + p_180434_5_ * var13), (double)(var16 - p_180434_6_ * var13 + p_180434_8_ * var13), (double)var10, (double)var11);
        worldRendererIn.addVertexWithUV((double)(var14 + p_180434_4_ * var13 + p_180434_7_ * var13), (double)(var15 + p_180434_5_ * var13), (double)(var16 + p_180434_6_ * var13 + p_180434_8_ * var13), (double)var9, (double)var11);
        worldRendererIn.addVertexWithUV((double)(var14 + p_180434_4_ * var13 - p_180434_7_ * var13), (double)(var15 - p_180434_5_ * var13), (double)(var16 + p_180434_6_ * var13 - p_180434_8_ * var13), (double)var9, (double)var12);
    }
}
