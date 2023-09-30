package de.lirium.util.render.shader;

import de.lirium.util.interfaces.IMinecraft;
import god.buddy.aot.BCompiler;
import net.minecraft.client.shader.Framebuffer;

public class FramebufferHelper implements IMinecraft {

    public static Framebuffer doFrameBuffer(final Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }


}