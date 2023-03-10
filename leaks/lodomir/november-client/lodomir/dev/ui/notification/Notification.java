/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.notification;

import java.awt.Color;
import lodomir.dev.November;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.ui.notification.NotificationType;
import lodomir.dev.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Notification {
    private NotificationType type;
    private ResourceLocation image;
    private String title;
    private String messsage;
    private long start;
    private long fadedIn;
    private long fadeOut;
    private long end;

    public Notification(NotificationType type, String title, String messsage, int seconds) {
        this.type = type;
        this.title = title;
        this.messsage = messsage;
        this.fadedIn = 200 * seconds;
        this.fadeOut = this.fadedIn + (long)(500 * seconds);
        this.end = this.fadeOut + this.fadedIn;
    }

    public void show() {
        this.start = System.currentTimeMillis();
    }

    public boolean isShown() {
        return this.getTime() <= this.end;
    }

    private long getTime() {
        return System.currentTimeMillis() - this.start;
    }

    public void render() {
        Color color1;
        TTFFontRenderer fontRenderer = November.INSTANCE.fm.getFont("PRODUCT SANS 18");
        int width = fontRenderer.getStringWidth(this.messsage + this.title);
        int height = 30;
        long time = this.getTime();
        double offset = time < this.fadedIn ? Math.tanh((double)time / (double)this.fadedIn * 3.0) * (double)width : (time > this.fadeOut ? Math.tanh(3.0 - (double)(time - this.fadeOut) / (double)(this.end - this.fadeOut) * 3.0) * (double)width : (double)width);
        Color color = new Color(0, 0, 0, 70);
        if (this.type == NotificationType.INFO) {
            color1 = new Color(197, 197, 197);
            this.image = new ResourceLocation("november/info.png");
        } else if (this.type == NotificationType.WARNING) {
            color1 = new Color(227, 220, 62);
            this.image = new ResourceLocation("november/warning.png");
        } else if (this.type == NotificationType.SUCCESS) {
            color1 = new Color(35, 206, 21);
            this.image = new ResourceLocation("november/success.png");
        } else {
            color1 = new Color(204, 0, 18);
            this.image = new ResourceLocation("november/error.png");
        }
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        Notification.drawRect((double)sr.getScaledWidth() - offset - 10.0, sr.getScaledHeight() - 5 - height, sr.getScaledWidth(), sr.getScaledHeight() - 5, color.getRGB());
        Notification.drawRect((double)sr.getScaledWidth() - (0.0 - (double)(time - this.fadeOut) / (double)(this.end - this.fadeOut) / 5.0) * (double)width, sr.getScaledHeight() + 27 - height, sr.getScaledWidth(), sr.getScaledHeight() - 5, color1.getRGB());
        TTFFontRenderer bfr = November.INSTANCE.fm.getFont("PRODUCT SANS 20");
        bfr.drawStringWithShadow(this.title, (int)((double)sr.getScaledWidth() - offset + 25.0), sr.getScaledHeight() - 2 - height, new Color(255, 255, 255).getRGB());
        fontRenderer.drawStringWithShadow(this.messsage, (int)((double)sr.getScaledWidth() - offset + 25.0), sr.getScaledHeight() - 15, new Color(255, 255, 255).getRGB());
        RenderUtils.drawImg(this.image, (int)((double)sr.getScaledWidth() - offset - 7.0), (int)((double)(sr.getScaledHeight() - height) - (this.type == NotificationType.ERROR && this.type == NotificationType.INFO ? 3.5 : 2.5)), 28, 28);
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
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0).endVertex();
        worldrenderer.pos(right, bottom, 0.0).endVertex();
        worldrenderer.pos(right, top, 0.0).endVertex();
        worldrenderer.pos(left, top, 0.0).endVertex();
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
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(mode, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0).endVertex();
        worldrenderer.pos(right, bottom, 0.0).endVertex();
        worldrenderer.pos(right, top, 0.0).endVertex();
        worldrenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}

