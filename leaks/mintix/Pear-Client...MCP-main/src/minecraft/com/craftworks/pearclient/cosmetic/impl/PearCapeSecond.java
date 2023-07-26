package com.craftworks.pearclient.cosmetic.impl;

import com.craftworks.pearclient.animation.Animated;
import com.craftworks.pearclient.cosmetic.CosmeticBoolean;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;

public class PearCapeSecond implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer playerRenderer;
    public static Animated CAPEANIMATED = new Animated("pearclient/cape/pearcapesecond", 1, 1);

    public PearCapeSecond(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
    }

    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        if (CosmeticBoolean.PearCape() && entitylivingbaseIn.hasPlayerInfo() && !entitylivingbaseIn.isInvisible() && entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE) && entitylivingbaseIn.getName().equals(Minecraft.getMinecraft().session.getUsername()) && entitylivingbaseIn.getName().equals(Minecraft.getMinecraft().session.getUsername())) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.playerRenderer.bindTexture(CAPEANIMATED.getTexture());
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.125F);
            double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double)partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double)partialTicks);
            double d2 = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double)partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double)partialTicks);
            double d3 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double)partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double)partialTicks);
            float f = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
            double d4 = (double)MathHelper.sin(f * 3.1415927F / 180.0F);
            double d5 = (double)(-MathHelper.cos(f * 3.1415927F / 180.0F));
            float f2 = (float)d2 * 10.0F;
            f2 = MathHelper.clamp_float(f2, -6.0F, 32.0F);
            float f3 = (float)(d0 * d4 + d3 * d5) * 100.0F;
            float f4 = (float)(d0 * d5 - d3 * d4) * 100.0F;
            if (f3 < 0.0F) {
                f3 = 0.0F;
            }

            if (f3 > 165.0F) {
                f3 = 165.0F;
            }

            if (f2 < -5.0F) {
                f2 = -5.0F;
            }

            float f5 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
            f2 += MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f5;
            if (entitylivingbaseIn.isSneaking()) {
                f2 += 25.0F;
                GlStateManager.translate(0.0F, 0.142F, -0.0178F);
            }

            GlStateManager.rotate(6.0F + f3 / 2.0F + f2, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f4 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-f4 / 2.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            this.playerRenderer.getMainModel().renderCape(0.0625F);
            GlStateManager.popMatrix();
        }

        CAPEANIMATED.update();
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
