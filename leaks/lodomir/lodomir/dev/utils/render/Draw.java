/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package lodomir.dev.utils.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class Draw {
    public static void drawRoundedRect(double x, double y, double width, double height, float cornerRadius) {
        int slices = 10;
        Draw.drawFillRectangle(x + (double)cornerRadius, y, width - (double)(2.0f * cornerRadius), height);
        Draw.drawFillRectangle(x, y + (double)cornerRadius, cornerRadius, height - (double)(2.0f * cornerRadius));
        Draw.drawFillRectangle(x + width - (double)cornerRadius, y + (double)cornerRadius, cornerRadius, height - (double)(2.0f * cornerRadius));
        Draw.drawCirclePart(x + (double)cornerRadius, y + (double)cornerRadius, (float)(-Math.PI), -1.5707964f, cornerRadius, 10);
        Draw.drawCirclePart(x + (double)cornerRadius, y + height - (double)cornerRadius, -1.5707964f, 0.0f, cornerRadius, 10);
        Draw.drawCirclePart(x + width - (double)cornerRadius, y + (double)cornerRadius, 1.5707964f, (float)Math.PI, cornerRadius, 10);
        Draw.drawCirclePart(x + width - (double)cornerRadius, y + height - (double)cornerRadius, 0.0f, 1.5707964f, cornerRadius, 10);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)3553);
        GlStateManager.disableBlend();
    }

    public static void drawFillRectangle(double x, double y, double width, double height) {
        GlStateManager.enableBlend();
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glVertex2d((double)(x + width), (double)(y + height));
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glEnd();
    }

    public static void drawCirclePart(double x, double y, float fromAngle, float toAngle, float radius, int slices) {
        GlStateManager.enableBlend();
        GL11.glBegin((int)6);
        GL11.glVertex2d((double)x, (double)y);
        float increment = (toAngle - fromAngle) / (float)slices;
        for (int i = 0; i <= slices; ++i) {
            float angle = fromAngle + (float)i * increment;
            float dX = MathHelper.sin(angle);
            float dY = MathHelper.cos(angle);
            GL11.glVertex2d((double)(x + (double)(dX * radius)), (double)(y + (double)(dY * radius)));
        }
        GL11.glEnd();
    }

    public static void color(int color) {
        float red = (float)(color & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color >> 16 & 0xFF) / 255.0f;
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        GlStateManager.color(red, green, blue, alpha);
    }

    public static void colorRGBA(int color) {
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        GlStateManager.color(r, g, b, a);
    }
}

