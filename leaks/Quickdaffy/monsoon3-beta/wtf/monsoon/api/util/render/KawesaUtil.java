/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL20
 */
package wtf.monsoon.api.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.StencilUtil;
import wtf.monsoon.api.util.shader.Shader;
import wtf.monsoon.impl.event.EventBlur;

public class KawesaUtil {
    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    private static final Minecraft mc = Minecraft.getMinecraft();
    static Shader blurShader = null;

    public static void blur(int passes) {
        if (blurShader == null) {
            blurShader = new Shader(new ResourceLocation("monsoon/shader/kawesa.frag"));
            blurShader.setupUniform("iResolution");
            blurShader.setupUniform("iChannelResolution");
            blurShader.setupUniform("tex");
            blurShader.setupUniform("blurStrength");
        }
        EventBlur eventBlur = new EventBlur();
        KawesaUtil.preBlur();
        Wrapper.getEventBus().post(eventBlur);
        KawesaUtil.postBlur(passes);
    }

    static void uniforms(float strength) {
        GL20.glUniform1i((int)blurShader.getUniform("tex"), (int)0);
        GL13.glActiveTexture((int)33984);
        mc.getFramebuffer().bindFramebufferTexture();
        GL20.glUniform2f((int)blurShader.getUniform("iResolution"), (float)KawesaUtil.mc.displayWidth, (float)KawesaUtil.mc.displayHeight);
        GL20.glUniform2f((int)blurShader.getUniform("iChannelResolution"), (float)KawesaUtil.mc.displayWidth, (float)KawesaUtil.mc.displayHeight);
        GL20.glUniform1f((int)blurShader.getUniform("blurStrength"), (float)strength);
    }

    public static void preBlur() {
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
    }

    public static void postBlur(int passes) {
        ScaledResolution sr = new ScaledResolution(mc);
        StencilUtil.bindReadStencilBuffer(1);
        blurShader.init();
        for (float i = 2.5f; i < (float)passes; i += 0.5f) {
            KawesaUtil.uniforms(i);
            blurShader.bind(0.0f, 0.0f, (float)sr.getScaledWidth_double() * (float)sr.getScaleFactor(), (float)sr.getScaledHeight_double() * (float)sr.getScaleFactor());
        }
        blurShader.finish();
        StencilUtil.uninitStencilBuffer();
    }
}

