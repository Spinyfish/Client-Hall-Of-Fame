package club.pulsive.api.yoint.shader.impl;

import club.pulsive.api.yoint.shader.Shader;
import club.pulsive.api.yoint.shader.ShaderInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL20.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL11.*;

@ShaderInfo(name = "boxblur_h.fsh")
@SuppressWarnings("unused")
public class BoxBlur extends Shader {

    private Framebuffer blurBuffer = new Framebuffer(1, 1, false);

    private final BoxBlur_V boxBlur_v;

    private final BoxBlur_H boxBlur_h;

    public BoxBlur() {
        boxBlur_h = new BoxBlur_H();
        boxBlur_v = new BoxBlur_V();
    }

    @Override
    public void setupUniforms() {
    }

    @Override
    public void updateUniforms() {
    }

    public void drawBlur(final Runnable runnable) {
        check(Minecraft.getMinecraft().getFramebuffer());
        blurBuffer = createFramebuffer(blurBuffer);
        boxBlur_h.startShader(true);
        blurBuffer.framebufferClear();
        blurBuffer.bindFramebuffer(true);
        glBindTexture(GL_TEXTURE_2D, Minecraft.getMinecraft().getFramebuffer().framebufferTexture);
        runnable.run();
        blurBuffer.unbindFramebuffer();
        boxBlur_v.startShader(true);
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        glBindTexture(GL_TEXTURE_2D, blurBuffer.framebufferTexture);
        runnable.run();
        boxBlur_v.stopShader();
        boxBlur_h.stopShader();
    }

    public void drawBlur() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        float width = (float) sr.getScaledWidth_double();
        float height = (float) sr.getScaledHeight_double();
        this.drawBlur(() -> this.renderCanvas(0, 0, width, height));
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.bindTexture(0);
    }

    private Framebuffer createFramebuffer(final Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != Minecraft.getMinecraft().displayWidth || framebuffer.framebufferHeight != Minecraft.getMinecraft().displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }

            return new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, true);
        }
        return framebuffer;
    }

    private void renderCanvas(float x, float y, float width, float height) {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(x, y);
        glTexCoord2f(0, 0);
        glVertex2f(x, height);
        glTexCoord2f(1, 0);
        glVertex2f(width, height);
        glTexCoord2f(1, 1);
        glVertex2f(width, y);
        glEnd();
    }

    @ShaderInfo(name = "boxblur_h.fsh")
    private static class BoxBlur_H extends Shader {
        @Override
        public void setupUniforms() {
            setupUniform("texture");
            setupUniform("resolution");
        }

        @Override
        public void updateUniforms() {
            final int textureID = getUniform("texture");
            if (textureID > -1) GL20.glUniform1i(textureID, 0);

            final int resolutionID = getUniform("resolution");
            if (resolutionID > -1)
                GL20.glUniform2f(resolutionID, (float) (Minecraft.getMinecraft().displayWidth), (float) (Minecraft.getMinecraft().displayHeight));
        }
    }

    @ShaderInfo(name = "boxblur_v.fsh")
    private static class BoxBlur_V extends Shader {
        @Override
        public void setupUniforms() {
            setupUniform("texture");
            setupUniform("resolution");
        }

        @Override
        public void updateUniforms() {
            final int textureID = getUniform("texture");
            if (textureID > -1) GL20.glUniform1i(textureID, 0);
            final int resolutionID = getUniform("resolution");
            if (resolutionID > -1)
                GL20.glUniform2f(resolutionID, (float) (Minecraft.getMinecraft().displayWidth), (float) (Minecraft.getMinecraft().displayHeight));
        }
    }

    public void check(final Framebuffer framebuffer) {
        if (framebuffer == null) return;
        if (framebuffer.depthBuffer > -1) {
            EXTFramebufferObject.glDeleteRenderbuffersEXT(framebuffer.depthBuffer);
            int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
            int stencil_texture_buffer_ID = EXTFramebufferObject.glGenFramebuffersEXT();
            EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
            EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
            EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
            EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_texture_buffer_ID, 0);
            framebuffer.depthBuffer = -1;
        }
    }
}