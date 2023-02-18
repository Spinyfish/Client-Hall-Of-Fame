/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.shader.Framebuffer
 *  net.minecraft.client.shader.Shader
 *  net.minecraft.client.shader.ShaderGroup
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.EXTFramebufferObject
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.storage.utils.other.TimeHelper
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.layout.dropdown.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.storage.utils.other.TimeHelper;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class BlurBuffer {
    private static ShaderGroup blurShader;
    private static final Minecraft mc;
    private static Framebuffer buffer;
    private static float lastScale;
    private static int lastScaleWidth;
    private static int lastScaleHeight;
    private static ResourceLocation shader;
    private static TimeHelper updateTimer;

    public static void initFboAndShader() {
        try {
            buffer = new Framebuffer(BlurBuffer.mc.displayWidth, BlurBuffer.mc.displayHeight, true);
            buffer.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f);
            blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), buffer, shader);
            blurShader.createBindFramebuffers(BlurBuffer.mc.displayWidth, BlurBuffer.mc.displayHeight);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void setShaderConfigs(float intensity, float blurWidth, float blurHeight) {
        ((Shader)blurShader.getShaders().get(0)).getShaderManager().getShaderUniform("Radius").set(intensity);
        ((Shader)blurShader.getShaders().get(1)).getShaderManager().getShaderUniform("Radius").set(intensity);
        ((Shader)blurShader.getShaders().get(0)).getShaderManager().getShaderUniform("BlurDir").set(blurWidth, blurHeight);
        ((Shader)blurShader.getShaders().get(1)).getShaderManager().getShaderUniform("BlurDir").set(blurHeight, blurWidth);
    }

    public static void blurArea(float x, float y, float width, float height, boolean setupOverlay) {
        ScaledResolution scale = new ScaledResolution(mc);
        float factor = scale.getScaleFactor();
        int factor2 = scale.getScaledWidth();
        int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
            BlurBuffer.initFboAndShader();
        }
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;
        GL11.glEnable((int)3089);
        RenderUtil.doGlScissor((float)x, (float)y, (float)width, (float)height);
        GL11.glPushMatrix();
        buffer.framebufferRenderExt(BlurBuffer.mc.displayWidth, BlurBuffer.mc.displayHeight, true);
        GL11.glPopMatrix();
        GL11.glDisable((int)3089);
        if (setupOverlay) {
            BlurBuffer.mc.entityRenderer.setupOverlayRendering();
        }
        GlStateManager.enableDepth();
    }

    public static void blurRoundArea(float x, float y, float width, float height, float roundRadius, boolean setupOverlay) {
        ScaledResolution scale = new ScaledResolution(mc);
        float factor = scale.getScaleFactor();
        int factor2 = scale.getScaledWidth();
        int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
            BlurBuffer.initFboAndShader();
        }
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;
        GL11.glEnable((int)3089);
        RenderUtil.doGlScissor((float)x, (float)y, (float)width, (float)height);
        GL11.glPushMatrix();
        buffer.framebufferRenderExt(BlurBuffer.mc.displayWidth, BlurBuffer.mc.displayHeight, true);
        GL11.glPopMatrix();
        GL11.glDisable((int)3089);
        if (setupOverlay) {
            BlurBuffer.mc.entityRenderer.setupOverlayRendering();
        }
        GlStateManager.enableDepth();
    }

    public static void updateBlurBuffer(boolean setupOverlay) {
        if (!updateTimer.delay(16.666666f, true)) return;
        if (blurShader == null) return;
        mc.getFramebuffer().unbindFramebuffer();
        BlurBuffer.setShaderConfigs(50.0f, 0.0f, 1.0f);
        buffer.bindFramebuffer(true);
        mc.getFramebuffer().framebufferRenderExt(BlurBuffer.mc.displayWidth, BlurBuffer.mc.displayHeight, true);
        if (OpenGlHelper.shadersSupported) {
            GlStateManager.matrixMode((int)5890);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            blurShader.loadShaderGroup(BlurBuffer.mc.timer.renderPartialTicks);
            GlStateManager.popMatrix();
        }
        buffer.unbindFramebuffer();
        mc.getFramebuffer().bindFramebuffer(true);
        if (mc.getFramebuffer() != null && BlurBuffer.mc.getFramebuffer().depthBuffer > -1) {
            BlurBuffer.setupFBO(mc.getFramebuffer());
            BlurBuffer.mc.getFramebuffer().depthBuffer = -1;
        }
        if (!setupOverlay) return;
        BlurBuffer.mc.entityRenderer.setupOverlayRendering();
    }

    public static void setupFBO(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT((int)fbo.depthBuffer);
        int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT((int)36161, (int)stencil_depth_buffer_ID);
        EXTFramebufferObject.glRenderbufferStorageEXT((int)36161, (int)34041, (int)BlurBuffer.mc.displayWidth, (int)BlurBuffer.mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT((int)36160, (int)36128, (int)36161, (int)stencil_depth_buffer_ID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT((int)36160, (int)36096, (int)36161, (int)stencil_depth_buffer_ID);
    }

    public static boolean blurEnabled() {
        if (!OpenGlHelper.shadersSupported) return false;
        if (!(mc.getRenderViewEntity() instanceof EntityPlayer)) return false;
        return !Hud.NoShader.getValueState();
    }

    static {
        mc = Minecraft.getMinecraft();
        shader = new ResourceLocation("shaders/post/blur.json");
        updateTimer = new TimeHelper();
    }
}
