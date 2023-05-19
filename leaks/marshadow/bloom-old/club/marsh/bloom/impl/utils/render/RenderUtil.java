package club.marsh.bloom.impl.utils.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

public class RenderUtil {
    public static float clamp(final float value, final float min, final float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double clamp(final double value, final double min, final double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static Color multiply(final Color color, final float factor) {
        return new Color(
                (int) clamp(color.getRed() * factor, 0, 255),
                (int) clamp(color.getGreen() * factor, 0, 255),
                (int) clamp(color.getBlue() * factor, 0, 255),
                color.getAlpha()
        );
    }

    public static Color alpha(final Color colour, final int alpha) {
        return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), (int) clamp(alpha, 0, 255));
    }

    public static void colour(final double red, final double green, final double blue, final double alpha) {
        glColor4d(red, green, blue, alpha);
    }

    public static void colour(Color colour) {
        if (colour == null)
            colour = Color.white;
        colour(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f, colour.getAlpha() / 255f);
    }
    public static void prepareScissorBox(float x, float y, float x2, float y2) {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scale.getScaleFactor();
        glScissor((int) (x * (float) factor), (int) (((float) scale.getScaledHeight() - y2) * (float) factor), (int) ((x2 - x) * (float) factor), (int) ((y2 - y) * (float) factor));
    }

    public static void drawRoundedRect(float left, float top, float right, float bottom, double radius, Color color) {
    	float f3 = (float)(color.getRGB() >> 24 & 255) / 255.0F;
        float f = (float)(color.getRGB() >> 16 & 255) / 255.0F;
        float f1 = (float)(color.getRGB() >> 8 & 255) / 255.0F;
        float f2 = (float)(color.getRGB() & 255) / 255.0F;
    	GL11.glScaled(0.5D, 0.5D, 0.5D);
        left *= 2.0D;
        top *= 2.0D;
        right *= 2.0D;
        bottom *= 2.0D;
        GL11.glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.color(f, f1, f2, f3);
        //GlStateManager.enableAlpha();
        //GlStateManager.enableBlend();
        GL11.glBegin(9);
        int i;
        for (i = 0; i <= 90; i += 1)
            GL11.glVertex2d(left + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, top + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 1)
            GL11.glVertex2d(left + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, bottom - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 0; i <= 90; i += 1)
            GL11.glVertex2d(right - radius + Math.sin(i * Math.PI / 180.0D) * radius, bottom - radius + Math.cos(i * Math.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 1)
            GL11.glVertex2d(right - radius + Math.sin(i * Math.PI / 180.0D) * radius, top + radius + Math.cos(i * Math.PI / 180.0D) * radius);
        GL11.glEnd();
        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        glColor4d(1, 1, 1, 1);
//        GlStateManager.enableAlpha();
//        GlStateManager.enableBlend();
//        GL11.glPopAttrib();

    }

    public static void drawRoundedRect(int left, int top, int right, int bottom, double radius, Color color) {
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        left *= 2.0D;
        top *= 2.0D;
        right *= 2.0D;
        bottom *= 2.0D;
        GL11.glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.color(color.getRed()/255f,color.getBlue()/255f,color.getGreen()/255f,color.getAlpha()/255f);
        //GlStateManager.enableAlpha();
        //GlStateManager.enableBlend();
        GL11.glBegin(9);
        int i;
        for (i = 0; i <= 90; i += 1)
            GL11.glVertex2d(left + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, top + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 1)
            GL11.glVertex2d(left + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, bottom - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 0; i <= 90; i += 1)
            GL11.glVertex2d(right - radius + Math.sin(i * Math.PI / 180.0D) * radius, bottom - radius + Math.cos(i * Math.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 1)
            GL11.glVertex2d(right - radius + Math.sin(i * Math.PI / 180.0D) * radius, top + radius + Math.cos(i * Math.PI / 180.0D) * radius);
        GL11.glEnd();
        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        glColor4d(1, 1, 1, 1);
//        GlStateManager.enableAlpha();
//        GlStateManager.enableBlend();
//        GL11.glPopAttrib();

    }
}
