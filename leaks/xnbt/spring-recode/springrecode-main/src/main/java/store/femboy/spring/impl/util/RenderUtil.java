package store.femboy.spring.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL13.*;


public class RenderUtil {
    public static void drawRoundedRect(double x, double y, double width, double height, double radius, int color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        double x1 = x + width;
        double y1 = y + height;
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);

        x *= 2;
        y *= 2;
        x1 *= 2;
        y1 *= 2;

        glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(f1, f2, f3, f);
        glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glBegin(GL11.GL_POLYGON);

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x + radius + +(Math.sin((i * Math.PI / 180)) * (radius * -1)), y + radius + (Math.cos((i * Math.PI / 180)) * (radius * -1)));
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x + radius + (Math.sin((i * Math.PI / 180)) * (radius * -1)), y1 - radius + (Math.cos((i * Math.PI / 180)) * (radius * -1)));
        }

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x1 - radius + (Math.sin((i * Math.PI / 180)) * radius), y1 - radius + (Math.cos((i * Math.PI / 180)) * radius));
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x1 - radius + (Math.sin((i * Math.PI / 180)) * radius), y + radius + (Math.cos((i * Math.PI / 180)) * radius));
        }

        GL11.glEnd();

        glEnable(GL11.GL_TEXTURE_2D);
        glDisable(GL11.GL_LINE_SMOOTH);
        glEnable(GL11.GL_TEXTURE_2D);

        GL11.glScaled(2, 2, 2);

        GL11.glPopAttrib();
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

    }

    public static void drawFrameBuffer(final ScaledResolution scaledResolution, final Framebuffer framebuffer) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 1);
            glVertex2i(0, 0);

            glTexCoord2f(0, 0);
            glVertex2i(0, scaledResolution.getScaledHeight());

            glTexCoord2f(1, 0);
            glVertex2i(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());

            glTexCoord2f(1, 1);
            glVertex2i(scaledResolution.getScaledWidth(), 0);
        }
        glEnd();
    }

    public static void bindTexture(int texture) {
        glActiveTexture(GL_TEXTURE20);
        glBindTexture(GL_TEXTURE_2D, texture);
    }
    public static boolean isHovered(float x, float y, float w, float h, int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= (x + w) && mouseY >= y && mouseY <= (y + h));
    }

    public double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }
    public static void drawImage(final ResourceLocation image, final int x, final int y, final int width, final int height) {
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float)width, (float)height);
    }

    public void drawBlurredRect(float x, float y, float x2, float y2) {
        BlurUtil.setupBlur();
        Gui.drawRect((int) x, (int) y, (int) x2, (int) y2, new Color(0, 0, 0, 255).getRGB());
        BlurUtil.doBlur();
    }

    public void drawColoredBlurredRect(float x, float y, float x2, float y2, int color) {
        BlurUtil.setupBlur();
        Gui.drawRect((int) x, (int) y, (int) x2, (int) y2, new Color(0, 0, 0, 255).getRGB());
        BlurUtil.doBlur();
        Gui.drawRect((int) x, (int) y, (int) x2, (int) y2, color);
    }

    public void drawColoredBlurredRect2(float x, float y, float width, float height, int color) {
        float x2 = x + width, y2 = y + height;

        BlurUtil.setupBlur();
        Gui.drawRect((int) x, (int) y, (int) x2, (int) y2, new Color(0, 0, 0, 255).getRGB());
        BlurUtil.doBlur();
        Gui.drawRect((int) x, (int) y, (int) x2, (int) y2, color);
    }

    public void drawBlurredRect2(float x, float y, float width, float height) {
        float x2 = x + width, y2 = y + height;
        BlurUtil.setupBlur();
        Gui.drawRect((int) x, (int) y, (int) x2, (int) y2, new Color(0, 0, 0, 255).getRGB());
        BlurUtil.doBlur();
    }

    public static void drawRect(double x, double y, double width, double height, int color) {
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glColor4f(f1, f2, f3, f);
        Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), color);
    }

    public void drawGradientRect(float x, float y, float width, float height, int firstColor, int secondColor, boolean perpendicular) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glPushMatrix();
        glShadeModel(GL_SMOOTH);
        glBegin(GL_QUADS);

        color(firstColor);
        glVertex2d(width, y);
        if(perpendicular)
            color(secondColor);
        glVertex2d(x, y);
        color(secondColor);
        glVertex2d(x, height);
        if(perpendicular)
            color(firstColor);
        glVertex2d(width, height);
        glEnd();
        glShadeModel(GL_FLAT);
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }

    public static void color(int color) {
        float[] rgba = convertRGB(color);
        glColor4f(rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    public void color(Color color, float alpha) {
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha / 255f);
    }

    public Color toColorRGB(int rgb, float alpha) {
        float[] rgba = convertRGB(rgb);
        return new Color(rgba[0], rgba[1], rgba[2], alpha / 255f);
    }

    public static float[] convertRGB(int rgb) {
        float a = (rgb >> 24 & 0xFF) / 255.0f;
        float r = (rgb >> 16 & 0xFF) / 255.0f;
        float g = (rgb >> 8 & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;
        return new float[]{r, g, b, a};
    }
}
