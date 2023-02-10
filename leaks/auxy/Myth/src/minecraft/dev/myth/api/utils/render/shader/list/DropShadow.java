package dev.myth.api.utils.render.shader.list;

import dev.myth.api.utils.math.MathUtil;
import dev.myth.api.utils.render.shader.KoksFramebuffer;
import dev.myth.api.utils.render.shader.ShaderAnnoation;
import dev.myth.api.utils.render.shader.ShaderProgram;
import dev.myth.api.utils.render.shader.ShaderRenderType;
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

@ShaderAnnoation(fragName = "shaderesp.frag", renderType = ShaderRenderType.NONE)
public class DropShadow extends ShaderProgram {

    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    @Override
    public void doRender() {
        this.doRender(22, 2, 1);
    }

    public void doRender(int sourceTexture, int radius, int offset) {

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
        glUseProgram(getShaderProgramID());
        setupUniforms(radius, offset, 0, weightBuffer);
        glBindTexture(GL_TEXTURE_2D, sourceTexture);
        KoksFramebuffer.drawQuads();
        glUseProgram(0);
        framebuffer.unbindFramebuffer();

        MC.getFramebuffer().bindFramebuffer(true);

        glUseProgram(getShaderProgramID());
        setupUniforms(radius, 0, offset, weightBuffer);
        GL13.glActiveTexture(GL13.GL_TEXTURE16);
        glBindTexture(GL_TEXTURE_2D, sourceTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        KoksFramebuffer.bindTexture(framebuffer.framebufferTexture);
        KoksFramebuffer.drawQuads();
        glUseProgram(0);

        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlpha();

        GlStateManager.bindTexture(0);
        super.doRender();
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public void setupUniforms(int radius, int directionX, int directionY, FloatBuffer weights) {
        Color color = Color.BLACK;
        setShaderUniformI("inTexture", 0);
        setShaderUniformI("textureToCheck", 16);
        setShaderUniform("radius", radius);
        setShaderUniform("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        setShaderUniform("texelSize", 1.0F / (float) MC.displayWidth, 1.0F / (float) MC.displayHeight);
        setShaderUniform("direction", directionX, directionY);
        glUniform1(getUniform("weights"), weights);
    }

}