package dev.africa.pandaware.impl.ui.shader.impl;

import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.impl.ui.shader.api.Shader;
import dev.africa.pandaware.utils.math.MathUtils;
import dev.africa.pandaware.utils.render.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class GlowShader implements MinecraftInstance {
    private final Shader outlineShader = new Shader("glowOutline.frag");
    private final Shader glowShader = new Shader("glow.frag");

    private final Map<Float, FloatBuffer> gaussianCache = new HashMap<>();
    private Framebuffer framebuffer;
    private Framebuffer outlineFrameBuffer;
    private Framebuffer glowFrameBuffer;

    public void applyGlow(Runnable runnable) {
        GL11.glPushMatrix();

        this.framebuffer = RenderUtils.createFrameBuffer(this.framebuffer);
        this.outlineFrameBuffer = RenderUtils.createFrameBuffer(this.outlineFrameBuffer);
        this.glowFrameBuffer = RenderUtils.createFrameBuffer(this.glowFrameBuffer);

        this.framebuffer.framebufferClear();
        this.framebuffer.bindFramebuffer(true);

        runnable.run();

        this.framebuffer.unbindFramebuffer();
        mc.getFramebuffer().bindFramebuffer(true);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    public void clearFrameBuffer() {
        if (this.framebuffer != null && this.outlineFrameBuffer != null && this.glowFrameBuffer != null) {
            this.framebuffer = null;
            this.outlineFrameBuffer = null;
            this.glowFrameBuffer = null;
        }
    }

    public void updateBuffer(float radius, float exposure, boolean separateTextures, Color color) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);

        if (this.framebuffer != null && this.outlineFrameBuffer != null && this.glowFrameBuffer != null) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(516, 0.0f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            this.outlineFrameBuffer.framebufferClear();
            this.outlineFrameBuffer.bindFramebuffer(true);
            this.outlineShader.useProgram();
            this.updateOutline(0, 1, radius, color);

            RenderUtils.bindFrameBuffer(0, 0, scaledResolution.getScaledWidth_double(),
                    scaledResolution.getScaledHeight_double(), this.framebuffer);

            this.outlineShader.useProgram();
            this.updateOutline(1, 0, radius, color);

            RenderUtils.bindFrameBuffer(0, 0, scaledResolution.getScaledWidth_double(),
                    scaledResolution.getScaledHeight_double(), this.framebuffer);

            this.outlineShader.disableProgram();
            this.outlineFrameBuffer.unbindFramebuffer();

            GL11.glColor4f(1f, 1f, 1f, 1f);
            this.glowFrameBuffer.framebufferClear();
            this.glowFrameBuffer.bindFramebuffer(true);
            this.glowShader.useProgram();
            this.updateGlow(1, 0, radius, exposure, separateTextures, color);

            RenderUtils.bindFrameBuffer(0, 0, scaledResolution.getScaledWidth_double(),
                    scaledResolution.getScaledHeight_double(), this.outlineFrameBuffer);

            this.glowShader.disableProgram();
            this.glowFrameBuffer.unbindFramebuffer();

            mc.getFramebuffer().bindFramebuffer(true);
            this.glowShader.useProgram();
            this.updateGlow(0, 1, radius, exposure, separateTextures, color);

            GL13.glActiveTexture(GL13.GL_TEXTURE16);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.framebuffer.framebufferTexture);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);

            RenderUtils.bindFrameBuffer(0, 0, scaledResolution.getScaledWidth_double(),
                    scaledResolution.getScaledHeight_double(), this.glowFrameBuffer);

            this.glowShader.disableProgram();

            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        }
    }

    void updateGlow(float directionX, float directionY, float radius, float exposure, boolean separateTextures, Color color) {
        this.glowShader.setUniform1i("texture", 0);
        this.glowShader.setUniform1i("texture2", 16);

        this.glowShader.setUniform2f("texelSize", 1.0f / mc.displayWidth, 1.0f / mc.displayHeight);
        this.glowShader.setUniform1f("radius", radius);
        this.glowShader.setUniform2f("direction", directionX, directionY);
        this.glowShader.setUniform3f("color", color.getRed() / 255f,
                color.getGreen() / 255f, color.getBlue() / 255f);

        this.glowShader.setUniform1f("exposure", exposure);
        this.glowShader.setUniform1i("separateTextures", separateTextures ? 1 : 0);

        float sigma = (radius / 2f);
        if (!this.gaussianCache.containsKey(sigma)) {
            FloatBuffer gaussianBuffer = BufferUtils.createFloatBuffer(256);

            for (int i = 0; i <= radius; i++) {
                gaussianBuffer.put(MathUtils.calculateGaussianOffset(i, sigma));
            }

            gaussianBuffer.rewind();

            this.gaussianCache.put(sigma, gaussianBuffer);
        }

        this.glowShader.setUniform1("weights", this.gaussianCache.get(sigma));
    }

    void updateOutline(float directionX, float directionY, float radius, Color color) {
        this.outlineShader.setUniform1i("texture", 0);

        this.outlineShader.setUniform2f("texelSize", 1.0f / mc.displayWidth, 1.0f / mc.displayHeight);
        this.outlineShader.setUniform1f("radius", radius);
        this.outlineShader.setUniform2f("direction", directionX, directionY);
        this.outlineShader.setUniform3f("color", color.getRed() / 255f,
                color.getGreen() / 255f, color.getBlue() / 255f);
    }
}
