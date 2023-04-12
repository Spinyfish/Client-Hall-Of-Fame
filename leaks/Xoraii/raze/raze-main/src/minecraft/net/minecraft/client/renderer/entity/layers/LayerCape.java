package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;

public class LayerCape implements LayerRenderer
{
    private final RenderPlayer playerRenderer;
    private static final String __OBFID = "CL_00002425";

    public LayerCape(RenderPlayer p_i46123_1_)
    {
        this.playerRenderer = p_i46123_1_;
    }

    public void doRenderLayer(AbstractClientPlayer thisPlayer, float p_177166_2_, float p_177166_3_, float p_177166_4_, float p_177166_5_, float p_177166_6_, float p_177166_7_, float p_177166_8_)
    {
        if (!thisPlayer.isInvisible() && thisPlayer.func_175148_a(EnumPlayerModelParts.CAPE) && thisPlayer.getLocationCape() != null)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.playerRenderer.bindTexture(thisPlayer.getLocationCape());
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.125F);
            double var9 = thisPlayer.field_71091_bM + (thisPlayer.field_71094_bP - thisPlayer.field_71091_bM) * (double)p_177166_4_ - (thisPlayer.prevPosX + (thisPlayer.posX - thisPlayer.prevPosX) * (double)p_177166_4_);
            double var11 = thisPlayer.field_71096_bN + (thisPlayer.field_71095_bQ - thisPlayer.field_71096_bN) * (double)p_177166_4_ - (thisPlayer.prevPosY + (thisPlayer.posY - thisPlayer.prevPosY) * (double)p_177166_4_);
            double var13 = thisPlayer.field_71097_bO + (thisPlayer.field_71085_bR - thisPlayer.field_71097_bO) * (double)p_177166_4_ - (thisPlayer.prevPosZ + (thisPlayer.posZ - thisPlayer.prevPosZ) * (double)p_177166_4_);
            float var15 = thisPlayer.prevRenderYawOffset + (thisPlayer.renderYawOffset - thisPlayer.prevRenderYawOffset) * p_177166_4_;
            double var16 = (double)MathHelper.sin(var15 * (float)Math.PI / 180.0F);
            double var18 = (double)(-MathHelper.cos(var15 * (float)Math.PI / 180.0F));
            float var20 = (float)var11 * 10.0F;
            var20 = MathHelper.clamp_float(var20, -6.0F, 32.0F);
            float var21 = (float)(var9 * var16 + var13 * var18) * 100.0F;
            float var22 = (float)(var9 * var18 - var13 * var16) * 100.0F;

            if (var21 < 0.0F)
            {
                var21 = 0.0F;
            }

            if (var21 > 165.0F)
            {
                var21 = 165.0F;
            }

            float var23 = thisPlayer.prevCameraYaw + (thisPlayer.cameraYaw - thisPlayer.prevCameraYaw) * p_177166_4_;
            var20 += MathHelper.sin((thisPlayer.prevDistanceWalkedModified + (thisPlayer.distanceWalkedModified - thisPlayer.prevDistanceWalkedModified) * p_177166_4_) * 6.0F) * 32.0F * var23;

            if (thisPlayer.isSneaking())
            {
                var20 += 25.0F;
                GlStateManager.translate(0.0F, 0.142F, -0.0178F);
            }

            GlStateManager.rotate(6.0F + var21 / 2.0F + var20, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(var22 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-var22 / 2.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            this.playerRenderer.func_177136_g().func_178728_c(0.0625F);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }

    public void doRenderLayer(EntityLivingBase p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_)
    {
        this.doRenderLayer((AbstractClientPlayer)p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
    }
}
