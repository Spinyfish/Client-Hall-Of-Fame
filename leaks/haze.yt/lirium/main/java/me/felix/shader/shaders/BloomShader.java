package me.felix.shader.shaders;


import de.lirium.util.render.shader.FramebufferHelper;
import de.lirium.util.render.shader.ShaderProgram;
import de.lirium.util.render.shader.ShaderType;
import me.felix.shader.annotation.Info;
import me.felix.shader.container.ShaderContainer;
import me.felix.shader.data.ShaderRenderType;
import me.felix.shader.render.FramebufferQuads;
import me.felix.shader.render.Type;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.nio.FloatBuffer;
import java.util.List;

@Info(frag = "/fragment/bloom.glsl")
public class BloomShader extends ShaderContainer {

    public ShaderProgram shaderProgram = new ShaderProgram(vert, frag, ShaderType.GLSL);

    private Framebuffer input = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
    private Framebuffer output = new Framebuffer(mc.displayWidth, mc.displayHeight, true);

    @Override
    public void reload() {
        this.status = -1;
        if (mc.displayWidth != input.framebufferWidth || mc.displayHeight != input.framebufferHeight) {
            input.deleteFramebuffer();
            input = FramebufferHelper.doFrameBuffer(input);

            output.deleteFramebuffer();
            output = FramebufferHelper.doFrameBuffer(output);
        } else {
            input.framebufferClear();
            output.framebufferClear();
        }
        input.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        output.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void doRender(ShaderRenderType type, Type renderType, List<Runnable> runnables) {

        this.reload();

        if (!Display.isVisible() || !Display.isActive()) {
            return;
        }

        if (runnables.isEmpty())
            status = -1;
        else
            status = 1;

        if (status == 1) {
            this.input.bindFramebuffer(true);

            runnables.forEach(Runnable::run);

            this.output.bindFramebuffer(true);
            this.shaderProgram.initShader();

            int radius = 7;


            final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);

            for (int i = 0; i <= radius; i++) {
                weightBuffer.put(calculateGaussianValue(i, radius / 2f));
            }

            weightBuffer.rewind();
            setupUniforms(radius, weightBuffer);
            shaderProgram.setUniformf("direction", 1.0f, 0.0f);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_SRC_ALPHA);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
            input.bindFramebufferTexture();
            FramebufferQuads.drawQuad();
            mc.getFramebuffer().bindFramebuffer(true);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            shaderProgram.setUniformf("direction", 0.0f, 1.0f);
            output.bindFramebufferTexture();
            GL13.glActiveTexture(GL13.GL_TEXTURE16);
            input.bindFramebufferTexture();
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            FramebufferQuads.drawQuad();
            GlStateManager.disableBlend();

            shaderProgram.deleteShader();
        }
    }

    private void setupUniforms(float radius, FloatBuffer buffer) {
        shaderProgram.setUniformi("inTexture", 0);
        shaderProgram.setUniformi("textureToCheck", 16);
        shaderProgram.setUniformf("radius", radius);
        shaderProgram.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        OpenGlHelper.glUniform1(shaderProgram.getUniform("weights"), buffer);
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double PI = Math.PI;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

}
