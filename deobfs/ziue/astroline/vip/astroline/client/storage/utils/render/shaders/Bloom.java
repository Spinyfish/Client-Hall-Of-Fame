/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.shader.Framebuffer
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL13
 *  vip.astroline.client.storage.utils.other.MathUtils
 *  vip.astroline.client.storage.utils.render.ShaderUtil
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.storage.utils.render.shaders;

import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import vip.astroline.client.storage.utils.other.MathUtils;
import vip.astroline.client.storage.utils.render.ShaderUtil;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class Bloom {
    static Minecraft mc = Minecraft.getMinecraft();
    public static ShaderUtil gaussianBloom = new ShaderUtil("astroline/Shaders/bloom.frag");
    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public static void renderBlur(int sourceTexture, int radius, int offset) {
        framebuffer = RenderUtil.createFrameBuffer((Framebuffer)framebuffer);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc((int)516, (float)0.0f);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc((int)770, (int)771, (int)1, (int)0);
        FloatBuffer weightBuffer = BufferUtils.createFloatBuffer((int)256);
        int i = 0;
        while (true) {
            if (i > radius) {
                weightBuffer.rewind();
                RenderUtil.setAlphaLimit((float)0.0f);
                framebuffer.framebufferClear();
                framebuffer.bindFramebuffer(true);
                gaussianBloom.init();
                Bloom.setupUniforms(radius, offset, 0, weightBuffer);
                RenderUtil.bindTexture((int)sourceTexture);
                ShaderUtil.drawQuads();
                gaussianBloom.unload();
                framebuffer.unbindFramebuffer();
                mc.getFramebuffer().bindFramebuffer(true);
                gaussianBloom.init();
                Bloom.setupUniforms(radius, 0, offset, weightBuffer);
                GL13.glActiveTexture((int)34000);
                RenderUtil.bindTexture((int)sourceTexture);
                GL13.glActiveTexture((int)33984);
                RenderUtil.bindTexture((int)Bloom.framebuffer.framebufferTexture);
                ShaderUtil.drawQuads();
                gaussianBloom.unload();
                GlStateManager.alphaFunc((int)516, (float)0.1f);
                GlStateManager.enableAlpha();
                GlStateManager.bindTexture((int)0);
                return;
            }
            weightBuffer.put(MathUtils.calculateGaussianValue((float)i, (float)radius));
            ++i;
        }
    }

    public static void setupUniforms(int radius, int directionX, int directionY, FloatBuffer weights) {
        gaussianBloom.setUniformi("inTexture", new int[]{0});
        gaussianBloom.setUniformi("textureToCheck", new int[]{16});
        gaussianBloom.setUniformf("radius", new float[]{radius});
        gaussianBloom.setUniformf("texelSize", new float[]{1.0f / (float)Bloom.mc.displayWidth, 1.0f / (float)Bloom.mc.displayHeight});
        gaussianBloom.setUniformf("direction", new float[]{directionX, directionY});
        OpenGlHelper.glUniform1((int)gaussianBloom.getUniform("weights"), (FloatBuffer)weights);
    }
}
