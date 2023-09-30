package me.jinthium.scripting.api.objects;

import me.jinthium.straight.impl.utils.render.RenderUtil;
import net.minecraft.client.shader.Framebuffer;

public class ScriptFramebuffer {

    private Framebuffer framebuffer;

    public ScriptFramebuffer() {
        this.framebuffer = new Framebuffer(1, 1, true);
    }


    public void resize() {
        framebuffer = RenderUtil.createFrameBuffer(framebuffer, true);
    }

    public void bind() {
        framebuffer.bindFramebuffer(false);
    }

    public void clear() {
        framebuffer.framebufferClear();
    }

    public void unbind() {
        framebuffer.unbindFramebuffer();
    }

    public int getTextureID() {
        return framebuffer.framebufferTexture;
    }

    public int getWidth() {
        return framebuffer.framebufferWidth;
    }

    public int getHeight() {
        return framebuffer.framebufferHeight;
    }


}
