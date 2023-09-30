package net.minecraft.client.renderer.entity.layers;

import dev.africa.pandaware.impl.ui.UISettings;
import dev.africa.pandaware.utils.math.MathUtils;
import dev.africa.pandaware.utils.render.CapeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;

public class LayerCape implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer playerRenderer;

    public LayerCape(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
    }

    public void doRenderLayer(AbstractClientPlayer entity, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        if (entity.hasPlayerInfo() && !entity.isInvisible() &&
                entity.isWearing(EnumPlayerModelParts.CAPE) &&
                CapeUtils.shouldRenderCape(entity) && !UISettings.WAVEY_CAPES) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            CapeUtils.bindTexture(entity, this.playerRenderer);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.125F);
            double d0 = entity.prevChasingPosX + (entity.chasingPosX - entity.prevChasingPosX) * (double) partialTicks - (entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks);
            double d1 = entity.prevChasingPosY + (entity.chasingPosY - entity.prevChasingPosY) * (double) partialTicks - (entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks);
            double d2 = entity.prevChasingPosZ + (entity.chasingPosZ - entity.prevChasingPosZ) * (double) partialTicks - (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks);
            float f = entity.prevRenderYawOffset + (entity.renderYawOffset
                    - entity.prevRenderYawOffset) * partialTicks;

            if (entity instanceof EntityPlayerSP) {
                if (Minecraft.getMinecraft().thePlayer.isRotationDifferent()) {
                    f = MathUtils.interpolate(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
                }
            }

            double d3 = MathHelper.sin(f * (float) Math.PI / 180.0F);
            double d4 = -MathHelper.cos(f * (float) Math.PI / 180.0F);
            float f1 = (float) d1 * 10.0F;
            f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
            float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
            float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

            if (f2 < 0.0F) {
                f2 = 0.0F;
            }

            if (f2 > 165.0F) {
                f2 = 165.0F;
            }

            if (f1 < -5.0F) {
                f1 = -5.0F;
            }

            float f4 = entity.prevCameraYaw + (entity.cameraYaw - entity.prevCameraYaw) * partialTicks;
            f1 = f1 + MathHelper.sin((entity.prevDistanceWalkedModified + (entity.distanceWalkedModified - entity.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

            if (entity.isSneaking()) {
                f1 += 25.0F;
                GlStateManager.translate(0.0F, 0.142F, -0.0178F);
            }

            GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);

            GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);

            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            this.playerRenderer.getMainModel().renderCape(0.0625F);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
