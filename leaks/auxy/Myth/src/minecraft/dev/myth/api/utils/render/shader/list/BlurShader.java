package dev.myth.api.utils.render.shader.list;


import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import dev.codeman.eventbus.EventPriority;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.utils.render.Rect;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.api.utils.render.StencilUtil;
import dev.myth.events.Render2DEvent;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.shader.Framebuffer;
import dev.myth.api.utils.render.shader.*;
import net.minecraft.util.Matrix4f;

import java.nio.Buffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

@ShaderAnnoation(fragName = "Blur.glsl", renderType = ShaderRenderType.RENDER2D)
public class BlurShader extends ShaderProgram {

    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public static BlurShader INSTANCE;

    private ArrayList<Rect> areas = new ArrayList<>();

    public BlurShader() {
        INSTANCE = this;
    }

    @Override
    public void doRender() {
        this.doRender(22, 22, 1);
    }

    public void doRender(int sigma, int radius, double texelSize) {

        framebuffer = KoksFramebuffer.doFrameBuffer(framebuffer);

        ShaderExtension.useShader(getShaderProgramID());

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        ScaledResolution resolution = new ScaledResolution(MC);

        float width = (float) (MC.displayWidth / resolution.getScaleFactor());
        float height = (float) (MC.displayHeight / resolution.getScaleFactor());

        //first pass
        setUniforms(1, 0, sigma, radius, texelSize);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        glBindTexture(GL_TEXTURE_2D, MC.getFramebuffer().framebufferTexture);
//        KoksFramebuffer.renderTexture();

        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(0, 0, 0).tex(0, 1).endVertex();
        worldRenderer.pos(0, height, 0).tex(0, 0).endVertex();
        worldRenderer.pos(width, height, 0).tex(1, 0).endVertex();
        worldRenderer.pos(width, 0, 0).tex(1, 1).endVertex();
        tessellator.draw();

        framebuffer.unbindFramebuffer();

        //second pass
        ShaderExtension.useShader(getShaderProgramID());
        setUniforms(0, 1, sigma, radius, texelSize);
        MC.getFramebuffer().bindFramebuffer(true);
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
//        KoksFramebuffer.renderTexture();

        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(0, 0, 0).tex(0, 1).endVertex();
        worldRenderer.pos(0, height, 0).tex(0, 0).endVertex();
        worldRenderer.pos(width, height, 0).tex(1, 0).endVertex();
        worldRenderer.pos(width, 0, 0).tex(1, 1).endVertex();
        tessellator.draw();

        ShaderExtension.deleteProgram();

        super.doRender();
    }

    public void startBlur() {
        StencilUtil.initStencilToWrite();
    }

    public void stopBlur() {
        stopBlur(7, 12, 1);
    }

    public void stopBlur(int sigma, int radius, int texelSize) {
        StencilUtil.readStencilBuffer(1);
        doRender(sigma, radius, texelSize);

        StencilUtil.uninitStencilBuffer();
        GlStateManager.enableBlend();
        MC.entityRenderer.setupOverlayRendering();
    }

    private void setUniforms(int xAxis, int yAxis, int sigma, int radius, double texelSize) {
        setShaderUniformI("currentTexture", 0);
        setShaderUniform("texelSize", 1.0F / (float) MC.displayWidth, 1.0F / (float) MC.displayHeight);
        setShaderUniform("coords", xAxis, yAxis);
        setShaderUniform("blurRadius", radius);
        setShaderUniform("uRTPixelSizePixelSizeHalf", 20, 20, 20, 20);
        setShaderUniform("blursigma", sigma);
    }


}