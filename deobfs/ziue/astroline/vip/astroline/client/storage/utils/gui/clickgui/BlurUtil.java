/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.shader.Framebuffer
 *  net.minecraft.client.shader.Shader
 *  net.minecraft.client.shader.ShaderGroup
 *  net.minecraft.util.ResourceLocation
 */
package vip.astroline.client.storage.utils.gui.clickgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

public class BlurUtil {
    private static ShaderGroup blurShader;
    private static Minecraft mc;
    private static Framebuffer buffer;
    private static float lastScale;
    private static int lastScaleWidth;
    private static int lastScaleHeight;
    private static ResourceLocation shader;

    public static void initFboAndShader() {
        try {
            blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), shader);
            blurShader.createBindFramebuffers(BlurUtil.mc.displayWidth, BlurUtil.mc.displayHeight);
            buffer = new Framebuffer(BlurUtil.mc.displayWidth, BlurUtil.mc.displayHeight, true);
            buffer.setFramebufferColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setShaderConfigs(float intensity, float blurWidth, float blurHeight) {
        ((Shader)blurShader.getShaders().get(0)).getShaderManager().getShaderUniform("Radius").set(intensity);
        ((Shader)blurShader.getShaders().get(1)).getShaderManager().getShaderUniform("Radius").set(intensity);
        ((Shader)blurShader.getShaders().get(0)).getShaderManager().getShaderUniform("BlurDir").set(blurWidth, blurHeight);
        ((Shader)blurShader.getShaders().get(1)).getShaderManager().getShaderUniform("BlurDir").set(blurHeight, blurWidth);
    }

    public static void blurAll(float intensity) {
        ScaledResolution scale = new ScaledResolution(mc);
        float factor = scale.getScaleFactor();
        int factor2 = scale.getScaledWidth();
        int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
            BlurUtil.initFboAndShader();
        }
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;
        BlurUtil.setShaderConfigs(intensity, 0.0f, 1.0f);
        buffer.bindFramebuffer(true);
        blurShader.loadShaderGroup(Minecraft.getMinecraft().timer.renderPartialTicks);
        mc.getFramebuffer().bindFramebuffer(true);
    }

    static {
        mc = Minecraft.getMinecraft();
        shader = new ResourceLocation("shaders/post/blur.json");
    }
}
