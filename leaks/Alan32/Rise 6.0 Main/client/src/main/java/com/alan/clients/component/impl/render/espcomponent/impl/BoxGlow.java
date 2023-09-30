package com.alan.clients.component.impl.render.espcomponent.impl;

import com.alan.clients.component.impl.render.espcomponent.api.ESP;
import com.alan.clients.component.impl.render.espcomponent.api.ESPColor;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BoxGlow extends ESP implements InstanceAccess {

    public BoxGlow(ESPColor espColor) {
        super(espColor);
    }

    @Override
    public void render3D() {

        final float partialTicks = mc.timer.renderPartialTicks;
        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            final Render<EntityPlayer> render = mc.getRenderManager().getEntityRenderObject(player);

            if (mc.getRenderManager() == null || render == null || (player == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) || !RenderUtil.isInViewFrustrum(player) || player.isDead) {
                continue;
            }

            final Color color = this.getColor(player);

            if (player.getEntityBoundingBox() == null || color.getAlpha() <= 0) continue;

            final double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
            final double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
            final double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
            RenderUtil.color(color);

            NORMAL_OUTLINE_RUNNABLES.add(() -> {
                GlStateManager.pushMatrix();
                GlStateManager.pushAttrib();
                GlStateManager.enableBlend();
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                GL11.glDepthMask(false);

                double expand = 0.14;

                RenderUtil.drawBoundingBox(player.getEntityBoundingBox().offset(-player.posX, -player.posY, -player.posZ).
                        offset(x, y, z).expand(expand, expand, expand));

                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.disableBlend();
                GL11.glDepthMask(true);
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
                GlStateManager.resetColor();
            });

        }

        RenderHelper.disableStandardItemLighting();
        mc.entityRenderer.disableLightmap();
    }
}
