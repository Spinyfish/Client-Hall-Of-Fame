/*
 * Copyright (c) 2023. Syncinus / Liticane
 * You cannot redistribute these files
 * without my written permission.
 */

package io.github.raze.utilities.collection.visual;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {

    public static void push() {
        GL11.glPushMatrix();
    }

    public static void pop() {
        GL11.glPopMatrix();
    }

    public static void enable(int enable) {
        GL11.glEnable(enable);
    }

    public static void disable(int disable) {
        GL11.glDisable(disable);
    }

    public static void begin(int mode) {
        GL11.glBegin(mode);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void vertex(double x, double y) {
        GL11.glVertex2d(x, y);
    }

    public static void vertex(float x, float y) {
        GL11.glVertex2f(x, y);
    }

    public static void vertex(int x, int y) {
        GL11.glVertex2i(x, y);
    }

    public static void blend(int one, int two) {
        GL11.glBlendFunc(one, two);
    }

    public static void color(Color color) {
        GlStateManager.color(
                color.getRed() / 255.0F,
                color.getGreen() / 255.0F,
                color.getBlue() / 255.0F,
                color.getAlpha() / 255.0F
        );
    }

    public static void start() {
        push();

        enable(GL_BLEND);

        blend(
            GL_SRC_ALPHA,
            GL_ONE_MINUS_SRC_ALPHA
        );

        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();

        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public static void stop() {
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();

        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();

        disable(GL_BLEND);

        color(Color.WHITE);

        pop();
    }

    public static void rectangle(double x, double y, double width, double height, boolean filled, Color color) {
        start();

        if (color != null)
            color(color);

        begin(filled ? GL_TRIANGLE_FAN : GL_LINE_LOOP);

        {
            vertex(x, y);
            vertex(x + width, y);
            vertex(x + width, y + height);
            vertex(x, y + height);
        }

        end();
        stop();
    }

    public static void rectangle(double x, double y, double width, double height, Color color) {
        rectangle(x, y, width, height, true, color);
    }

    public static void scale(float x, float y, float scale, Runnable runnable) {
        GlStateManager.pushMatrix();

        glTranslatef(x, y, 1);
        glScalef(scale, scale, 1);
        glTranslatef(-x, -y, 1);

        runnable.run();

        GlStateManager.popMatrix();
    }

}
