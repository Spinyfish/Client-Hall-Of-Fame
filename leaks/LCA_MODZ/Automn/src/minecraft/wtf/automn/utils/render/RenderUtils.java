package wtf.automn.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static net.minecraft.client.renderer.GlStateManager.*;

public final class RenderUtils {

    public static void drawImage(final float x, final float y, final float width, final float height, final ResourceLocation resourceLocation) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
    }

    public static void drawRoundedRect2(double x, double y, double width, double height, double cornerRadius, int color) {
        drawRoundedRect2(x, y, width, height, cornerRadius, true, true, true, true, color);
    }


    public static void drawRoundedRect(final double x, final double y, final double width, final double height, final double cornerRadius, final int color) {
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        setGLColor(color);
        GL11.glBegin(GL_POLYGON);
        double cornerX = x + width - cornerRadius;
        double cornerY = y + height - cornerRadius;
        for (int i = 0; i <= 90; i += 30)
            glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius,
                    cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
        cornerX = x + width - cornerRadius;
        cornerY = y + cornerRadius;
        for (int i = 90; i <= 180; i += 30)
            glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius,
                    cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
        cornerX = x + cornerRadius;
        cornerY = y + cornerRadius;
        for (int i = 180; i <= 270; i += 30)
            glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius,
                    cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
        cornerX = x + cornerRadius;
        cornerY = y + height - cornerRadius;
        for (int i = 270; i <= 360; i += 30)
            glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius,
                    cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
        GL11.glEnd();
        GL11.glColor4f(1, 1, 1, 1);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
        setGLColor(Color.white);
    }


    public static void drawRoundedRect2(double x, double y, double width, double height, double cornerRadius, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom, int color) {
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_BLEND);
        tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 1);
        setGLColor(color);
        GL11.glBegin(GL_POLYGON);
        double cornerX = x + width - cornerRadius;
        double cornerY = y + height - cornerRadius;
        if (rightBottom) {
            for (int i = 0; i <= 90; i += 1) {
                glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius,
                        cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
            }
        } else {
            glVertex2d(x + width, y + height);
        }
        if (rightTop) {
            cornerX = x + width - cornerRadius;
            cornerY = y + cornerRadius;
            for (int i = 90; i <= 180; i += 1) {
                glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius,
                        cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
            }
        } else {
            glVertex2d(x + width, y);
        }
        if (leftTop) {
            cornerX = x + cornerRadius;
            cornerY = y + cornerRadius;
            for (int i = 180; i <= 270; i += 1) {
                glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius,
                        cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
            }
        } else {
            glVertex2d(x, y);
        }
        if (leftBottom) {
            cornerX = x + cornerRadius;
            cornerY = y + height - cornerRadius;
            for (int i = 270; i <= 360; i += 1) {
                glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius,
                        cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
            }
        } else {
            glVertex2d(x, y + height);
        }
        GL11.glEnd();
        setGLColor(new Color(255, 255, 255, 255));
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);

        glPopMatrix();
    }

    public static int getColor(final int red, final int green, final int blue, final int alpha) {
        int color = MathHelper.clamp_int(alpha, 0, 255) << 24;
        color |= MathHelper.clamp_int(red, 0, 255) << 16;
        color |= MathHelper.clamp_int(green, 0, 255) << 8;
        color |= MathHelper.clamp_int(blue, 0, 255);
        return color;
    }

    public static boolean isInside(final int mouseX, final int mouseY, final double x, final double y, final double width, final double height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static void scissor(final int x, final int y, final int width, final int height) {
        final Minecraft mc = Minecraft.getMinecraft();
        final int scale = new ScaledResolution(mc).getScaleFactor();
        glScissor(x * scale, mc.displayHeight - (y + height) * scale, (width) * scale, height * scale);
        glEnable(GL_SCISSOR_TEST);
    }

    public static void setGLColor(final Color color) {
        final float r = color.getRed() / 255F;
        final float g = color.getGreen() / 255F;
        final float b = color.getBlue() / 255F;
        final float a = color.getAlpha() / 255F;
        glColor4f(r, g, b, a);
    }

    public static void setGLColor(final int color) {
        setGLColor(new Color(color));
    }

    public static final void circle(final double x, final double y, double radius, final int color) {
        radius *= 0.5;
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_CULL_FACE);

        disableAlpha();

        setGLColor(color);

        glLineWidth(10);
        glEnable(GL_LINE_SMOOTH);
        // since its filled, otherwise GL_LINE_STRIP
        GL11.glBegin(GL_TRIANGLE_FAN);

        for (double i = 0; i <= (radius * 3); i++) {
            final double angle = i * (Math.PI * 2) / (radius * 3);

            glVertex2d(x + (radius * Math.cos(angle)), y + (radius * Math.sin(angle)));
        }

        GL11.glEnd();
        glDisable(GL_LINE_SMOOTH);

        enableAlpha();
        disableBlend();
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        setGLColor(Color.white.getRGB());
    }

}

