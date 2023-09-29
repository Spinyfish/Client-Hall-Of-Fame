package me.felix.shader.render;

import de.lirium.util.interfaces.IMinecraft;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

public class MCFramebufferRender implements IMinecraft {

    public static void renderFramebuffer(final Framebuffer framebuffer) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);
        framebuffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false);
    }

}
