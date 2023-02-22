package com.alan.clients.component.impl.render.espcomponent.impl;

import com.alan.clients.component.impl.render.espcomponent.api.ESP;
import com.alan.clients.component.impl.render.espcomponent.api.ESPColor;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class SigmaRing extends ESP implements InstanceAccess {

    public SigmaRing(ESPColor espColor) {
        super(espColor);
    }

    @Override
    public void render3D() {

        final float partialTicks = mc.timer.renderPartialTicks;

        EntityLivingBase player = (EntityLivingBase) this.target;

        final Color color = this.getColor(player);

        if (mc.getRenderManager() == null || player == null) return;

        final double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks - (mc.getRenderManager()).renderPosX;
        final double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks + Math.sin(System.currentTimeMillis() / 2E+2) + 1 - (mc.getRenderManager()).renderPosY;
        final double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks - (mc.getRenderManager()).renderPosZ;

        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDepthMask(false);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        for (float i = 0; i <= Math.PI * 2 + ((Math.PI * 2) / 32.F); i += (Math.PI * 2) / 32.F) {
            double vecX = x + 0.67 * Math.cos(i);
            double vecZ = z + 0.67 * Math.sin(i);

            RenderUtil.color(ColorUtil.withAlpha(color, (int) (255 * 0.25)));
            GL11.glVertex3d(vecX, y, vecZ);
        }

        for (float i = 0; i <= Math.PI * 2 + (Math.PI * 2) / 32.F; i += (Math.PI * 2) / 32.F) {
            double vecX = x + 0.67 * Math.cos(i);
            double vecZ = z + 0.67 * Math.sin(i);

            RenderUtil.color(ColorUtil.withAlpha(color, (int) (255 * 0.25)));
            GL11.glVertex3d(vecX, y, vecZ);

            RenderUtil.color(ColorUtil.withAlpha(color, 0));
            GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2.0F, vecZ);
        }

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        RenderUtil.color(Color.WHITE);

    }

    private void drawCircle(final Entity entity, final double rad, final int color, final boolean shade) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDepthMask(false);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        if (shade) GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosX;
        final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosY) + Math.sin(System.currentTimeMillis() / 2E+2) + 1;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosZ;

        Color c;

        for (float i = 0; i < Math.PI * 2; i += Math.PI * 2 / 64.F) {
            final double vecX = x + rad * Math.cos(i);
            final double vecZ = z + rad * Math.sin(i);

            c = Color.WHITE;

            if (shade) {
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0
                );
                GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2.0F, vecZ);
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0.85F
                );
            }
            GL11.glVertex3d(vecX, y, vecZ);
        }

        GL11.glEnd();
        if (shade) GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glColor3f(255, 255, 255);
    }
}
