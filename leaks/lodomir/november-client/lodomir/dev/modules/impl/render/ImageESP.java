/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 *  org.lwjgl.opengl.GL11
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.render.EventRender3D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.combat.AntiBot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ImageESP
extends Module {
    private final ResourceLocation texture = new ResourceLocation("november/esp/image.jpg");

    public ImageESP() {
        super("ImageESP", 0, Category.RENDER);
    }

    @Override
    @Subscribe
    public void on3D(EventRender3D event) {
        for (EntityPlayer player : ImageESP.mc.theWorld.playerEntities) {
            if (!player.isEntityAlive() || player == ImageESP.mc.thePlayer || AntiBot.bot.contains(player) || player.isInvisible()) continue;
            double x = ImageESP.interp(player.posX, player.lastTickPosX) - Minecraft.getMinecraft().getRenderManager().renderPosX;
            double y = ImageESP.interp(player.posY, player.lastTickPosY) - Minecraft.getMinecraft().getRenderManager().renderPosY;
            double z = ImageESP.interp(player.posZ, player.lastTickPosZ) - Minecraft.getMinecraft().getRenderManager().renderPosZ;
            GlStateManager.pushMatrix();
            GL11.glColor4d((double)1.0, (double)1.0, (double)1.0, (double)1.0);
            GL11.glDisable((int)2929);
            float distance = MathHelper.clamp_float(ImageESP.mc.thePlayer.getDistanceToEntity(player), 20.0f, Float.MAX_VALUE);
            double scale = 0.005 * (double)distance;
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(-ImageESP.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.scale(-0.1, -0.1, 0.0);
            mc.getTextureManager().bindTexture(this.texture);
            Gui.drawScaledCustomSizeModalRect((int)(player.width / 2.0f - distance / 3.0f), (int)(-player.height - distance), 0.0f, 0.0f, 1, 1, (int)(252.0 * (scale / 2.0)), (int)(476.0 * (scale / 2.0)), 1.0f, 1.0f);
            GL11.glEnable((int)2929);
            GlStateManager.popMatrix();
        }
    }

    public static double interp(double newPos, double oldPos) {
        return oldPos + (newPos - oldPos) * (double)Minecraft.getMinecraft().timer.renderPartialTicks;
    }
}

