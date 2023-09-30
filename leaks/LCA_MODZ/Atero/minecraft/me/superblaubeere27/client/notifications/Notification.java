package me.superblaubeere27.client.notifications;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.font.FontManager;
import de.verschwiegener.atero.ui.guiingame.CustomGUIIngame;
import de.verschwiegener.atero.util.Util;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

public class Notification {
    private me.superblaubeere27.client.notifications.NotificationType type;
    private String title;
    private String messsage;
    private long start;

    private long fadedIn;
    private long fadeOut;
    private long end;


    public Notification(me.superblaubeere27.client.notifications.NotificationType type, String title, String messsage, int length) {
        this.type = type;
        this.title = title;
        this.messsage = messsage;

        fadedIn = 200 * length;
        fadeOut = fadedIn + 10 * length;
        end = fadeOut + fadedIn;
    }

    public void show() {
        start = System.currentTimeMillis();
    }

    public boolean isShown() {
        return getTime() <= end;
    }

    private long getTime() {
        return System.currentTimeMillis() - start;
    }

    public void render() {
        double offset = 0;
        int width = 120;
        int height = 30;
        long time = getTime();

        if (time < fadedIn) {
            offset = Math.tanh(time / (double) (fadedIn) * 25.0) * width;
        } else if (time > fadeOut) {
            offset = (Math.tanh(3.0 - (time - fadeOut) / (double) (end - fadeOut) * 25.0) * width);
        } else {
            offset = width;
        }

        Color color = new Color(0, 0, 0, 180);
        Color color1;

        if (type == me.superblaubeere27.client.notifications.NotificationType.INFO)
            color1 = new Color(0, 26, 169);
        else if (type == me.superblaubeere27.client.notifications.NotificationType.WARNING)
            color1 = new Color(204, 193, 0);
        else {
            color1 = new Color(204, 0, 18);
            int i = Math.max(0, Math.min(255, (int) (Math.sin(time / 100.0) * 255.0 / 2 + 127.5)));
            color = new Color(i, 0, 0, 220);
        }

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        drawRect(GuiScreen.width - offset, GuiScreen.height - 5 - height, GuiScreen.width, GuiScreen.height - 5, color.getRGB());
        drawRect(GuiScreen.width - offset, GuiScreen.height - 5 - height, GuiScreen.width - offset + 1, GuiScreen.height - 5, Management.instance.colorBlue.getRGB());
        drawRect(GuiScreen.width - offset, GuiScreen.height - 5 - height, GuiScreen.width, GuiScreen.height - 5 - height +1 ,Management.instance.colorBlue.getRGB());
        drawRect(GuiScreen.width - offset, GuiScreen.height - 4, GuiScreen.width, GuiScreen.height - 5, Management.instance.colorBlue.getRGB());

       // FontManager.VistolSans_Light.drawString(title, (int) (GuiScreen.width - offset + 25), GuiScreen.height - 2 - height, -1,true);
        FontManager.VistolSans_Light.drawString(messsage, (int) (GuiScreen.width - offset + 8), GuiScreen.height - 24 + 0.5F, -1,true);
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRect(int mode, double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(mode, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }


}
