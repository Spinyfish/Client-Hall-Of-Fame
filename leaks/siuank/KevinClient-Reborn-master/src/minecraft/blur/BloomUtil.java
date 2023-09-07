package blur;

import kevin.persional.milk.utils.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.awt.*;
import java.nio.FloatBuffer;

import static blur.GaussianBlur.calculateGaussianValue;
import static blur.KawaseBlur.bindTexture;
import static blur.KawaseBlur.createFrameBuffer;
import static net.minecraft.client.renderer.OpenGlHelper.glUniform1;
import static net.minecraft.client.renderer.OpenGlHelper.glUniform2;
import static org.lwjgl.opengl.GL11.*;

public class BloomUtil {

    public static Minecraft mc=Minecraft.getMinecraft();
    public static ShaderUtil gaussianBloom = new ShaderUtil("shaders/Shaders/bloom.frag");

    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public static void renderBlur(int sourceTexture, int radius, int offset, Color color) {
        framebuffer = createFrameBuffer(framebuffer);
        final boolean enableBlend = GL11.glIsEnabled(3042);
        final boolean disableAlpha = !GL11.glIsEnabled(3008);
        if (!enableBlend) {
            GL11.glEnable(3042);
        }
        if (!disableAlpha) {
            GL11.glDisable(3008);
        }
        GlStateManager.alphaFunc(516, 0.0f);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(calculateGaussianValue(i, radius));
        }
        weightBuffer.rewind();
        setAlphaLimit(1F);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        gaussianBloom.init();
        setupUniforms(radius, offset, 0, weightBuffer , color);
        bindTexture(sourceTexture);
        ShaderUtil.drawQuads();
        gaussianBloom.unload();
        framebuffer.unbindFramebuffer();

        mc.getFramebuffer().bindFramebuffer(true);

        gaussianBloom.init();
        setupUniforms(radius, 0, offset, weightBuffer , color);
        GL13.glActiveTexture(GL13.GL_TEXTURE16);
        bindTexture(sourceTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        bindTexture(framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        gaussianBloom.unload();

        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlpha();

        GlStateManager.bindTexture(0);
    }

    public static void setupUniforms(int radius, int directionX, int directionY, FloatBuffer weights,Color color) {
        gaussianBloom.setUniformi("inTexture", 0);
        gaussianBloom.setUniformi("textureToCheck", 16);
        gaussianBloom.setUniformf("radius", radius);
        gaussianBloom.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        gaussianBloom.setUniformf("direction", directionX, directionY);
        float r=color.getRed()/255f;
        float g=color.getGreen()/255f;
        float b=color.getBlue()/255f;
        glUniform1(gaussianBloom.getUniform("weights"), weights);
        gaussianBloom.setUniformf("colorR", r);
        gaussianBloom.setUniformf("colorG", g);
        gaussianBloom.setUniformf("colorB", b);
    }

}
