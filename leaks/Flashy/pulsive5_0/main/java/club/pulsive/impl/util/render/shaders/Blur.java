package club.pulsive.impl.util.render.shaders;

import club.pulsive.api.minecraft.MinecraftUtil;
import club.pulsive.impl.util.math.MathUtil;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.secondary.ShaderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL20.glUniform1;

public class Blur implements MinecraftUtil {

    public static ShaderUtil blurShader = new ShaderUtil("gaussainBlur.frag");

    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    public static float prevRadius;


    public static void setupUniforms(float dir1, float dir2, float radius) {
        blurShader.setUniformi("textureIn", 0);
        blurShader.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        blurShader.setUniformf("direction", dir1, dir2);
        blurShader.setUniformf("radius", radius);

        if(prevRadius != radius) {
            final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
            for (int i = 0; i <= radius; i++) {
                weightBuffer.put(MathUtil.calculateGaussianValue(i, radius / 2));
            }

            weightBuffer.rewind();
            glUniform1(blurShader.getUniform("weights"), weightBuffer);
            prevRadius = radius;
        }
    }

    public static void renderBlur(float radius) {
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);


        framebuffer = RenderUtil.createFramebuffer(framebuffer, true);

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        blurShader.init();
        setupUniforms(1, 0, radius);

        glBindTexture(GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);

        ShaderUtil.drawQuads();
        framebuffer.unbindFramebuffer();
        blurShader.unload();

        mc.getFramebuffer().bindFramebuffer(true);
        blurShader.init();
        setupUniforms(0, 1, radius);

        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        blurShader.unload();

        glColor4f(1, 1, 1, 1);
        GlStateManager.bindTexture(0);
    }

}
