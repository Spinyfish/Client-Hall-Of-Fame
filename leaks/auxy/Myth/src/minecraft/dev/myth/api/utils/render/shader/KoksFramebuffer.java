package dev.myth.api.utils.render.shader;

import dev.myth.api.interfaces.IMethods;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

@UtilityClass
public class KoksFramebuffer implements IMethods {

    /* Credits Felix */

    public void renderTexture() {
        final ScaledResolution resolution = new ScaledResolution(MC);
        drawQuads(0, 0, (float) resolution.getScaledWidth(), (float) resolution.getScaledHeight());
    }

    public void drawQuads() {
        if (MC.gameSettings.ofFastRender) return;
        ScaledResolution sr = new ScaledResolution(MC);
        float width = (float) sr.getScaledWidth_double();
        float height = (float) sr.getScaledHeight_double();
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

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public static void drawQuads(float x, float y, float width, float height) {
        if (MC.gameSettings.ofFastRender) return;
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(x, y);
        glTexCoord2f(0, 1);
        glVertex2f(x, y + height);
        glTexCoord2f(1, 1);
        glVertex2f(x + width, y + height);
        glTexCoord2f(1, 0);
        glVertex2f(x + width, y);
        glEnd();
    }


    public Framebuffer doFrameBuffer(final Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != MC.displayWidth || framebuffer.framebufferHeight != MC.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(MC.displayWidth, MC.displayHeight, true);
        }
        return framebuffer;
    }


    public void renderFRFscreen(final int framebuffer) {
        if (MC.gameSettings.ofFastRender) return;
        final ScaledResolution resolution = new ScaledResolution(MC);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer);
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex2d(0, 0);
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex2d(0, resolution.getScaledHeight());
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex2d(resolution.getScaledWidth(), resolution.getScaledHeight());
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex2d(resolution.getScaledWidth(), 0);
        }
        GL11.glEnd();
        ShaderExtension.deleteProgram();
    }

    //"RenderFrameBufferFullScreen"
    public void renderFRFscreen(final Framebuffer framebuffer) {
        if (MC.gameSettings.ofFastRender) return;
        final ScaledResolution resolution = new ScaledResolution(MC);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex2d(0, 0);
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex2d(0, resolution.getScaledHeight());
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex2d(resolution.getScaledWidth(), resolution.getScaledHeight());
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex2d(resolution.getScaledWidth(), 0);
        }
        GL11.glEnd();
        ShaderExtension.deleteProgram();
    }
}