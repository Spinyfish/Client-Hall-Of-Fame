package me.felix.shader.render;

import de.lirium.util.interfaces.IMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL11.*;

public class FramebufferQuads implements IMinecraft {

    public static void drawQuad(final double x, final double y, final double width, final double height) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2d(x + width, y + height);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2d(x + width, y);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
    }

    public static void drawQuads(final ScaledResolution sr) {
        if (Minecraft.getMinecraft().gameSettings.ofFastRender) return;
        final float width = (float) sr.getScaledWidth_double();
        final float height = (float) sr.getScaledHeight_double();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(0, 0);
        glVertex2f(0, height);
        glTexCoord2f(1, 0);
        glVertex2f(width, height);
        glTexCoord2f(1, 1);
        glVertex2f(width, 0);
        glEnd();
    }

    public static void drawQuad() {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        drawQuad(0.0, 0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double());
    }
}
