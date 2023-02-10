package dev.myth.api.utils.render.shader.list;

import dev.myth.api.interfaces.IMethods;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.api.utils.render.shader.KoksFramebuffer;
import dev.myth.api.utils.render.shader.other.ShaderProgramOther;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniform1;
import static org.lwjgl.opengl.GL20.glUseProgram;


public class DropShadowUtil implements IMethods {

    private static ShaderProgramOther shaderProgramOther = new ShaderProgramOther("shaderesp.frag");
    private static Framebuffer bloomFramebuffer = new Framebuffer(1, 1, false);
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public static void start() {
        bloomFramebuffer = KoksFramebuffer.doFrameBuffer(bloomFramebuffer);

        bloomFramebuffer.framebufferClear();
        bloomFramebuffer.bindFramebuffer(true);
    }

    public static void stop(int radius, Color color) {
        bloomFramebuffer.unbindFramebuffer();
        framebuffer = KoksFramebuffer.doFrameBuffer(framebuffer);

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        setAlphaLimit(0.0F);
        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(MathUtil.calculate(i, radius));
        }
        weightBuffer.rewind();

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        glUseProgram(shaderProgramOther.getShaderProgramID());
        setupUniforms(radius, 1, 0, weightBuffer, color);
        glBindTexture(GL_TEXTURE_2D, bloomFramebuffer.framebufferTexture);
        KoksFramebuffer.drawQuads();
        glUseProgram(0);
        framebuffer.unbindFramebuffer();

        MC.getFramebuffer().bindFramebuffer(true);

        glUseProgram(shaderProgramOther.getShaderProgramID());
        setupUniforms(radius, 0, 1, weightBuffer, color);
        GL13.glActiveTexture(GL13.GL_TEXTURE16);
        glBindTexture(GL_TEXTURE_2D, bloomFramebuffer.framebufferTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        KoksFramebuffer.bindTexture(framebuffer.framebufferTexture);
        KoksFramebuffer.drawQuads();
        glUseProgram(0);

        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlpha();

        GlStateManager.bindTexture(0);
    }

    public static void stop() {
        bloomFramebuffer.unbindFramebuffer();
        int radius = 6;
        framebuffer = KoksFramebuffer.doFrameBuffer(framebuffer);

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        setAlphaLimit(0.0F);
        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(MathUtil.calculate(i, radius - 20));
        }
        weightBuffer.rewind();

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        glUseProgram(shaderProgramOther.getShaderProgramID());
        setupUniforms(radius, 1, 0, weightBuffer, Color.BLACK);
        glBindTexture(GL_TEXTURE_2D, bloomFramebuffer.framebufferTexture);
        KoksFramebuffer.drawQuads();
        glUseProgram(0);
        framebuffer.unbindFramebuffer();

        MC.getFramebuffer().bindFramebuffer(true);

        glUseProgram(shaderProgramOther.getShaderProgramID());
        setupUniforms(radius, 0, 1, weightBuffer, Color.BLACK);
        GL13.glActiveTexture(GL13.GL_TEXTURE16);
        glBindTexture(GL_TEXTURE_2D, bloomFramebuffer.framebufferTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        KoksFramebuffer.bindTexture(framebuffer.framebufferTexture);
        KoksFramebuffer.drawQuads();
        glUseProgram(0);

        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlpha();

        GlStateManager.bindTexture(0);
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public static void setupUniforms(int radius, int directionX, int directionY, FloatBuffer weights, Color color) {
//        Color color = Color.BLACK;
        shaderProgramOther.setShaderUniformI("inTexture", 0);
        shaderProgramOther.setShaderUniformI("textureToCheck", 16);
        shaderProgramOther.setShaderUniform("radius", (float) radius);
        shaderProgramOther.setShaderUniform("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        shaderProgramOther.setShaderUniform("texelSize", 1.0F / (float) MC.displayWidth, 1.0F / (float) MC.displayHeight);
        shaderProgramOther.setShaderUniform("direction", directionX, directionY);
        glUniform1(shaderProgramOther.getUniform("weights"), weights);
    }

}