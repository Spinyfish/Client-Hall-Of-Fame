package dev.thread.api.util.render;

import dev.thread.api.util.IWrapper;
import lombok.experimental.UtilityClass;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;

import static org.lwjgl.opengl.GL11.*;

@UtilityClass
public class StencilUtil implements IWrapper {
    //had to do this since plusbox is being wining all the time abt not being able to round the fuckin blur
    public void checkFramebuffer(Framebuffer buffer) {
        if(buffer != null){
            if(buffer.depthBuffer > -1){
                setupFramebuffer(buffer);
                buffer.depthBuffer = -1;
            }
        }
    }

    public void setupFramebuffer(Framebuffer buffer) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(buffer.depthBuffer);
        int bufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, bufferID);
        EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, mc.displayWidth, mc.displayHeight);

        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, bufferID);

        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, bufferID);
    }

    public void initStencil() {
        mc.getFramebuffer().bindFramebuffer(false);
        checkFramebuffer(mc.getFramebuffer());
        glClear(GL_STENCIL_BUFFER_BIT);
        glEnable(GL_STENCIL_TEST);
        glStencilFunc(GL_ALWAYS, 1, 1);
        glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
        glColorMask(false, false, false, false);
    }

    public void readBuffer(int ref) {
        glColorMask(true, true, true, true);
        glStencilFunc(GL_EQUAL, ref, 1);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
    }

    public void endStencil(){
        glDisable(GL_STENCIL_TEST);
    }

    //note for plusbox, what you want to render, put as the initRunnable, what you want to do in the buffer, put in the end runnable. example being blured rect
    //initRunnable would be the drawRect, and endRunnable would be the Blur.blur(radius) or whatever tf u gonna do.
    public void renderStencil(Runnable initRunnable, Runnable endRunnable) {
        initStencil();
        initRunnable.run();
        readBuffer(1);
        endRunnable.run();
        endStencil();
    }
}