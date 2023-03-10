/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 *  org.lwjgl.opengl.GL11
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import lodomir.dev.event.impl.render.EventRender3D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.render.Interface;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ChinaHat
extends Module {
    public ChinaHat() {
        super("ChinaHat", 0, Category.RENDER);
    }

    @Override
    @Subscribe
    public void on3D(EventRender3D event) {
        if (ChinaHat.mc.gameSettings.thirdPersonView != 0 && !ChinaHat.mc.thePlayer.isInvisible()) {
            for (int i = 0; i < 165; ++i) {
                ChinaHat.drawHat(ChinaHat.mc.thePlayer, 0.001 + (double)((float)i * 0.0045f), event.getPartialTicks(), 20, 2.0f, (ChinaHat.mc.thePlayer.isSneaking() ? 2.0f : 2.15f) - (float)i * 0.002f, Interface.getColorInt());
            }
        }
    }

    public static void drawHat(Entity entity, double radius, float partialTicks, int points, float width, float yAdd, int color) {
        GL11.glPushMatrix();
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glDisable((int)2929);
        GL11.glLineWidth((float)width);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)2929);
        GL11.glBegin((int)3);
        double d = ChinaHat.mc.thePlayer.lastTickPosX + (ChinaHat.mc.thePlayer.posX - ChinaHat.mc.thePlayer.lastTickPosX) * (double)ChinaHat.mc.timer.renderPartialTicks;
        mc.getRenderManager();
        double x = d - RenderManager.viewerPosX;
        double d2 = ChinaHat.mc.thePlayer.lastTickPosY + (ChinaHat.mc.thePlayer.posY - ChinaHat.mc.thePlayer.lastTickPosY) * (double)ChinaHat.mc.timer.renderPartialTicks;
        mc.getRenderManager();
        double y = d2 - RenderManager.viewerPosY + (double)yAdd + (ChinaHat.mc.thePlayer.isSneaking() ? -0.2 : 0.0);
        double d3 = ChinaHat.mc.thePlayer.lastTickPosZ + (ChinaHat.mc.thePlayer.posZ - ChinaHat.mc.thePlayer.lastTickPosZ) * (double)ChinaHat.mc.timer.renderPartialTicks;
        mc.getRenderManager();
        double z = d3 - RenderManager.viewerPosZ;
        GL11.glColor4f((float)((float)new Color(color).getRed() / 255.0f), (float)((float)new Color(color).getGreen() / 255.0f), (float)((float)new Color(color).getBlue() / 255.0f), (float)0.15f);
        for (int i = 0; i <= points; ++i) {
            GL11.glVertex3d((double)(x + radius * Math.cos((double)i * (Math.PI * 2) / (double)points)), (double)y, (double)(z + radius * Math.sin((double)i * (Math.PI * 2) / (double)points)));
        }
        GL11.glEnd();
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)2929);
        GL11.glEnable((int)3553);
        GlStateManager.enableDepth();
        GL11.glPopMatrix();
    }
}

