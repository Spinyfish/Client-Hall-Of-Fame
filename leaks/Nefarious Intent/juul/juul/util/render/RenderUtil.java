package juul.util.render;

import juul.inject.wrappers.GlStateManagerWrapper;
import juul.util.Utils;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glScissor;

public class RenderUtil implements Utils {


    public static void fixBlendIssues() {
        glStateManager.color(1,1,1,1);
        glStateManager.enableAlpha();
        glStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        glStateManager.enableBlend();
        glStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    //From rise, alan gave me this
    public static void drawFilledCircleNoGL(final int x, final int y, final double r, final int c, int quality) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;

        GL11.glEnable(GL_BLEND);
        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin( ((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }
        GL11.glEnd();
        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glDisable(GL_BLEND);
    }
    
    public static void circleNoSmoothRGB(double x, double y, double radius, int color) {
        radius /= 2;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        color(color);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (double i = 0; i <= 360; i++) {
            double angle = (i * (Math.PI * 2)) / 360;
            GL11.glVertex2d(x + (radius * Math.cos(angle)) + radius, y + (radius * Math.sin(angle)) + radius);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }


    public static void drawRect(double x, double y, double width, double height, int color) {
        RenderUtil.resetColor();
        GLUtil.setup2DRendering(() -> GLUtil.render(GL11.GL_QUADS, () -> {
            RenderUtil.color(color);
            GL11.glVertex2d(x, y);
            GL11.glVertex2d(x, y + height);
            GL11.glVertex2d(x + width, y + height);
            GL11.glVertex2d(x + width, y);
        }));
    }
    

    public static void drawBorderedRect(float x, float y, float width, float height, final float outlineThickness, int rectColor, int outlineColor) {
        drawRect(x, y, width, height, rectColor);
        glEnable(GL_LINE_SMOOTH);
        GLUtil.setup2DRendering(() -> {
            color(outlineColor);
            GL11.glLineWidth(outlineThickness);
            float cornerValue = (float) (outlineThickness * .19);
            glBegin(GL_LINES);
            GL11.glVertex2d(x, y - cornerValue);
            GL11.glVertex2d(x, y + height + cornerValue);
            GL11.glVertex2d(x + width, y + height + cornerValue);
            GL11.glVertex2d(x + width, y - cornerValue);
            GL11.glVertex2d(x, y);
            GL11.glVertex2d(x + width, y);
            GL11.glVertex2d(x, y + height);
            GL11.glVertex2d(x + width, y + height);

        });
        GL11.glDisable(GL_LINE_SMOOTH);
    }


    // Scales the data that you put in the runnable
    public static void scale(float x, float y, float scale, Runnable data) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(scale, scale, 1);
        GL11.glTranslatef(-x, -y, 0);
        data.run();
        GL11.glPopMatrix();
    }

    // Scales the data that you put in the runnable
    public static void scaleStart(float x, float y, float scale) {
        glStateManager.pushMatrix();
        glStateManager.translate(x, y, 0);
        glStateManager.scale(scale, scale, 1);
        glStateManager.translate(-x, -y, 0);
    }

    public static void scaleEnd() {
        glStateManager.popMatrix();
    }


    public static void fakeCircleGlow(float posX, float posY, float radius, Color color, float maxAlpha) {
        setAlphaLimit(0);
        glShadeModel(GL_SMOOTH);
        GLUtil.setup2DRendering(() -> {
            glBegin(GL_TRIANGLE_FAN);
            color(color.getRGB(), maxAlpha);
            glVertex2d(posX, posY);
            color(color.getRGB(), 0);
            for (int i = 0; i <= 100; i++) {
                double angle = (i * .06283) + 3.1415;
                double x2 = Math.sin(angle) * radius;
                double y2 = Math.cos(angle) * radius;
                glVertex2d(posX + x2, posY + y2);
            }
            glEnd();
        });
        glShadeModel(GL_FLAT);
        setAlphaLimit(1);
    }

    // animation for sliders and stuff
    public static double animate(double endPoint, double current, double speed) {
        boolean shouldContinueAnimation = endPoint > current;
        if (speed < 0.0D) {
            speed = 0.0D;
        } else if (speed > 1.0D) {
            speed = 1.0D;
        }

        double dif = Math.max(endPoint, current) - Math.min(endPoint, current);
        double factor = dif * speed;
        return current + (shouldContinueAnimation ? factor : -factor);
    }

    // Draws a circle using traditional methods of rendering
    public static void drawCircleNotSmooth(double x, double y, double radius, int color) {
        radius /= 2;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        color(color);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (double i = 0; i <= 360; i++) {
            double angle = i * .01745;
            GL11.glVertex2d(x + (radius * Math.cos(angle)) + radius, y + (radius * Math.sin(angle)) + radius);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

  /*  public static void scissor(double x, double y, double width, double height, Runnable data) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        scissor(x, y, width, height);
        data.run();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void scissor(double x, double y, double width, double height) {
        ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();
        double finalHeight = height * scale;
        double finalY = (sr.getScaledHeight() - y) * scale;
        double finalX = x * scale;
        double finalWidth = width * scale;
        glScissor((int) finalX, (int) (finalY - finalHeight), (int) finalWidth, (int) finalHeight);
    }*/

    // This will set the alpha limit to a specified value ranging from 0-1
    public static void setAlphaLimit(float limit) {
        glStateManager.enableAlpha();
        glStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    // This method colors the next avalible texture with a specified alpha value ranging from 0-1
    public static void color(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        glStateManager.color(r, g, b, alpha);
    }

    public static void color(float r, float g, float b, float a) {
        glStateManager.color(r, g, b, a);
    }


    // Colors the next texture without a specified alpha value
    public static void color(int color) {
        color(color, (float) (color >> 24 & 255) / 255.0F);
    }

    /**
     * Bind a texture using the specified integer refrence to the texture.
     *
     * @see org.lwjgl.opengl.GL13 for more information about texture bindings
     */
    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    // Sometimes colors get messed up in for loops, so we use this method to reset it to allow new colors to be used
    public static void resetColor() {
        glStateManager.color(1, 1, 1, 1);
    }

    public static boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

}
