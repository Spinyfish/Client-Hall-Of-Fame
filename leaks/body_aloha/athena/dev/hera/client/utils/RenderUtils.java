package dev.hera.client.utils;

import org.lwjgl.opengl.GL11;

public class RenderUtils {

    public static void drawRoundedRect(double x, double y, double x1, double y1, double radius, int color) {
        float alpha = (color >> 24 & 255) / 255.0f;
        float red = (color >> 16 & 255) / 255.0f;
        float green = (color >> 8 & 255) / 255.0f;
        float blue = (color & 255) / 255.0f;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glPushMatrix();
        GL11.glColor4f(red, green, blue, alpha);
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        GL11.glBegin(9);

        int i;
        for(i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x + radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D, y + radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D);
        }

        for(i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x + radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D, y1 - radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D);
        }

        for(i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x1 - radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius, y1 - radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius);
        }

        for(i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x1 - radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius, y + radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius);
        }


        GL11.glEnd();
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();
        GL11.glColor3f(255, 255, 255);

    }

    public static void drawRoundedRect(float x, float y, float x1, float y1, float radius, int color) {
        drawRoundedRect((double)x, (double)y, (double)x1, (double)y1, (float)radius, color);
    }

    public static void drawRect(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, int paramColor) {
        float alpha = (float)(paramColor >> 24 & 255) / 255.0F;
        float red = (float)(paramColor >> 16 & 255) / 255.0F;
        float green = (float)(paramColor >> 8 & 255) / 255.0F;
        float blue = (float)(paramColor & 255) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(7);
        GL11.glVertex2d((double)paramXEnd, (double)paramYStart);
        GL11.glVertex2d((double)paramXStart, (double)paramYStart);
        GL11.glVertex2d((double)paramXStart, (double)paramYEnd);
        GL11.glVertex2d((double)paramXEnd, (double)paramYEnd);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
    }

    public static void drawRect(double x, double y, double x2, double y2, int color){
        drawRect((float)x, (float)y, (float) x2, (float)y2, color);
    }

    public static void drawBorder(float x, float y, float f, float g, int i, int rgb) {
        drawRect(x, y, f, y + (float)i, rgb);
        drawRect(x, g, f, g + (float)i, rgb);
        drawRect(x, y, x + (float)i, g + (float)i, rgb);
        drawRect(f, y, f + (float)i, g + (float)i, rgb);
    }

    public static void drawBorder(double x, double y, double f, double g, double i, int rgb) {
        drawRect(x, y, f, y + i, rgb);
        drawRect(x, g, f, g + i, rgb);
        drawRect(x, y, x + i, g + i, rgb);
        drawRect(f, y, f + i, g + i, rgb);
    }

}