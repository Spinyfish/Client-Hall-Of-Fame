package net.minecraft.client.renderer.entity.layers;


import crispy.Crispy;
import crispy.util.server.CapeManager;
import crispy.util.server.cape.CapeUser;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class LayerCape implements LayerRenderer
{
    private final RenderPlayer playerRenderer;
    public LayerCape(RenderPlayer playerRendererIn)
    {
        this.playerRenderer = playerRendererIn;
    }

    public void doRenderLayer(AbstractClientPlayer clientPlayer, float p_177166_2_, float p_177166_3_, float p_177166_4_, float p_177166_5_, float p_177166_6_, float p_177166_7_, float p_177166_8_)
    {

        if (clientPlayer.hasPlayerInfo() && !clientPlayer.isInvisible() && clientPlayer.isWearing(EnumPlayerModelParts.CAPE) && clientPlayer.getLocationCape() != null)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.playerRenderer.bindTexture(clientPlayer.getLocationCape());
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.125F);
            double var9 = clientPlayer.prevChasingPosX + (clientPlayer.chasingPosX - clientPlayer.prevChasingPosX) * (double)p_177166_4_ - (clientPlayer.prevPosX + (clientPlayer.posX - clientPlayer.prevPosX) * (double)p_177166_4_);
            double var11 = clientPlayer.prevChasingPosY + (clientPlayer.chasingPosY - clientPlayer.prevChasingPosY) * (double)p_177166_4_ - (clientPlayer.prevPosY + (clientPlayer.posY - clientPlayer.prevPosY) * (double)p_177166_4_);
            double var13 = clientPlayer.prevChasingPosZ + (clientPlayer.chasingPosZ - clientPlayer.prevChasingPosZ) * (double)p_177166_4_ - (clientPlayer.prevPosZ + (clientPlayer.posZ - clientPlayer.prevPosZ) * (double)p_177166_4_);
            float var15 = clientPlayer.prevRenderYawOffset + (clientPlayer.renderYawOffset - clientPlayer.prevRenderYawOffset) * p_177166_4_;
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

            float var23 = clientPlayer.prevCameraYaw + (clientPlayer.cameraYaw - clientPlayer.prevCameraYaw) * p_177166_4_;
            var20 += MathHelper.sin((clientPlayer.prevDistanceWalkedModified + (clientPlayer.distanceWalkedModified - clientPlayer.prevDistanceWalkedModified) * p_177166_4_) * 6.0F) * 32.0F * var23;

            if (clientPlayer.isSneaking())
            {
                var20 += 25.0F;
                GlStateManager.translate(0.0F, 0.142F, -0.0178F);
            }

            GlStateManager.rotate(6.0F + var21 / 2.0F + var20, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(var22 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-var22 / 2.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            this.playerRenderer.getPlayerModel().renderCape(0.0625F);
            GlStateManager.popMatrix();
        } else {
            CapeManager capeManager = new CapeManager();

            if(containsName(clientPlayer.getCommandSenderName())) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                this.playerRenderer.bindTexture(getResource(clientPlayer.getCommandSenderName()));
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, 0.0F, 0.125F);
                double var9 = clientPlayer.prevChasingPosX + (clientPlayer.chasingPosX - clientPlayer.prevChasingPosX) * (double)p_177166_4_ - (clientPlayer.prevPosX + (clientPlayer.posX - clientPlayer.prevPosX) * (double)p_177166_4_);
                double var11 = clientPlayer.prevChasingPosY + (clientPlayer.chasingPosY - clientPlayer.prevChasingPosY) * (double)p_177166_4_ - (clientPlayer.prevPosY + (clientPlayer.posY - clientPlayer.prevPosY) * (double)p_177166_4_);
                double var13 = clientPlayer.prevChasingPosZ + (clientPlayer.chasingPosZ - clientPlayer.prevChasingPosZ) * (double)p_177166_4_ - (clientPlayer.prevPosZ + (clientPlayer.posZ - clientPlayer.prevPosZ) * (double)p_177166_4_);
                float var15 = clientPlayer.prevRenderYawOffset + (clientPlayer.renderYawOffset - clientPlayer.prevRenderYawOffset) * p_177166_4_;
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

                float var23 = clientPlayer.prevCameraYaw + (clientPlayer.cameraYaw - clientPlayer.prevCameraYaw) * p_177166_4_;
                var20 += MathHelper.sin((clientPlayer.prevDistanceWalkedModified + (clientPlayer.distanceWalkedModified - clientPlayer.prevDistanceWalkedModified) * p_177166_4_) * 6.0F) * 32.0F * var23;

                if (clientPlayer.isSneaking())
                {
                    var20 += 25.0F;
                    GlStateManager.translate(0.0F, 0.142F, -0.0178F);
                }

                GlStateManager.rotate(6.0F + var21 / 2.0F + var20, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(var22 / 2.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(-var22 / 2.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                this.playerRenderer.getPlayerModel().renderCape(0.0625F);
                GlStateManager.popMatrix();
            }

        }
    }
    public boolean containsName(String name) {
        for(CapeUser capeUser : CapeManager.capeUsers) {
            if(capeUser.player.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    public ResourceLocation getResource(String name) {
        for(CapeUser capeUser : CapeManager.capeUsers) {
            if(capeUser.player.equalsIgnoreCase(name)) {
                if(capeUser.resourceLocation.getResourcePath().equalsIgnoreCase("Crispy")) {

                    return new ResourceLocation("Client/capes/CrispySpaceCape/" + Crispy.INSTANCE.getCapeManager().i + ".png");
                } else {
                    return capeUser.resourceLocation;
                }
            }
        }
        return null;
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        this.doRenderLayer((AbstractClientPlayer)entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
    }
}
