/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 */
package wtf.monsoon.api.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.api.util.render.StencilUtil;
import wtf.monsoon.api.util.shader.Shader;
import wtf.monsoon.impl.event.EventBlur;

public class BlurUtil {
    private static Shader blur_shader = null;
    public static Framebuffer blur_framebuffer = new Framebuffer(1, 1, false);
    public static Framebuffer blur_shader_2 = new Framebuffer(1, 1, false);
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static int lastScale = -1;
    private static int lastScaleWidth = -1;
    private static int lastScaleHeight = -1;
    public static Framebuffer framebuffer = null;
    private static ShaderGroup blurShader = null;

    public static void blur(float radius, float direction) {
        if (BlurUtil.mc.displayWidth != BlurUtil.blur_framebuffer.framebufferWidth || BlurUtil.mc.displayHeight != BlurUtil.blur_framebuffer.framebufferHeight) {
            blur_framebuffer.deleteFramebuffer();
            blur_framebuffer = new Framebuffer(BlurUtil.mc.displayWidth, BlurUtil.mc.displayHeight, false);
            blur_framebuffer.deleteFramebuffer();
            blur_framebuffer = new Framebuffer(BlurUtil.mc.displayWidth, BlurUtil.mc.displayHeight, false);
        }
        EventBlur eventBlur = new EventBlur();
        ScaledResolution sr = new ScaledResolution(mc);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        BlurUtil.preBlur();
        Wrapper.getEventBus().post(eventBlur);
        BlurUtil.postBlur(radius, direction);
        StencilUtil.bindReadStencilBuffer(1);
    }

    static void start_shader(float radius) {
        if (blur_shader == null) {
            blur_shader = new Shader(new ResourceLocation("monsoon/shader/blur.frag"));
            blur_shader.setupUniform("texture");
            blur_shader.setupUniform("texelSize");
            blur_shader.setupUniform("direction");
            blur_shader.setupUniform("radius");
        }
        GL20.glUseProgram((int)blur_shader.getProgram());
        GL20.glUniform1i((int)blur_shader.getUniform("texture"), (int)0);
        GL20.glUniform2f((int)blur_shader.getUniform("texelSize"), (float)(1.0f / (float)BlurUtil.mc.displayWidth), (float)(1.0f / (float)BlurUtil.mc.displayHeight));
        GL20.glUniform1f((int)blur_shader.getUniform("radius"), (float)MathHelper.ceiling_float_int(2.0f * radius));
    }

    public static void preBlur() {
        if (BlurUtil.mc.displayWidth != BlurUtil.blur_framebuffer.framebufferWidth || BlurUtil.mc.displayHeight != BlurUtil.blur_framebuffer.framebufferHeight) {
            blur_framebuffer.deleteFramebuffer();
            blur_framebuffer = new Framebuffer(BlurUtil.mc.displayWidth, BlurUtil.mc.displayHeight, false);
            blur_framebuffer.deleteFramebuffer();
            blur_framebuffer = new Framebuffer(BlurUtil.mc.displayWidth, BlurUtil.mc.displayHeight, false);
        }
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
    }

    public static void postBlur(float radius, float direction) {
        StencilUtil.bindReadStencilBuffer(1);
        ScaledResolution sr = new ScaledResolution(mc);
        BlurUtil.start_shader(radius);
        blur_framebuffer.framebufferClear();
        blur_framebuffer.bindFramebuffer(false);
        GL20.glUniform2f((int)blur_shader.getUniform("direction"), (float)direction, (float)0.0f);
        GL11.glBindTexture((int)3553, (int)BlurUtil.mc.getFramebuffer().framebufferTexture);
        RoundedUtils.rect(0.0f, 0.0f, sr.getScaledWidth(), sr.getScaledHeight());
        blur_framebuffer.unbindFramebuffer();
        BlurUtil.start_shader(radius);
        mc.getFramebuffer().bindFramebuffer(false);
        GL20.glUniform2f((int)blur_shader.getUniform("direction"), (float)0.0f, (float)direction);
        GL11.glBindTexture((int)3553, (int)BlurUtil.blur_framebuffer.framebufferTexture);
        RoundedUtils.rect(0.0f, 0.0f, sr.getScaledWidth(), sr.getScaledHeight());
        GL20.glUseProgram((int)0);
        StencilUtil.uninitStencilBuffer();
    }

    public static void preBlurNoStencil() {
        if (BlurUtil.mc.displayWidth != BlurUtil.blur_framebuffer.framebufferWidth || BlurUtil.mc.displayHeight != BlurUtil.blur_framebuffer.framebufferHeight) {
            blur_framebuffer.deleteFramebuffer();
            blur_framebuffer = new Framebuffer(BlurUtil.mc.displayWidth, BlurUtil.mc.displayHeight, false);
            blur_framebuffer.deleteFramebuffer();
            blur_framebuffer = new Framebuffer(BlurUtil.mc.displayWidth, BlurUtil.mc.displayHeight, false);
        }
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    }

    public static void postBlurNoStencil(float radius, float direction) {
        ScaledResolution sr = new ScaledResolution(mc);
        BlurUtil.start_shader(radius);
        blur_framebuffer.framebufferClear();
        blur_framebuffer.bindFramebuffer(false);
        GL20.glUniform2f((int)blur_shader.getUniform("direction"), (float)direction, (float)0.0f);
        GL11.glBindTexture((int)3553, (int)BlurUtil.mc.getFramebuffer().framebufferTexture);
        RoundedUtils.rect(0.0f, 0.0f, sr.getScaledWidth(), sr.getScaledHeight());
        blur_framebuffer.unbindFramebuffer();
        BlurUtil.start_shader(radius);
        mc.getFramebuffer().bindFramebuffer(false);
        GL20.glUniform2f((int)blur_shader.getUniform("direction"), (float)0.0f, (float)direction);
        GL11.glBindTexture((int)3553, (int)BlurUtil.blur_framebuffer.framebufferTexture);
        RoundedUtils.rect(0.0f, 0.0f, sr.getScaledWidth(), sr.getScaledHeight());
        GL20.glUseProgram((int)0);
    }

    public static void alternateBlur(float x, float y, float width, float height, int intensity) {
        ScaledResolution sr = new ScaledResolution(mc);
        int currentScale = sr.getScaleFactor();
        BlurUtil.checkScale(currentScale, sr.getScaledWidth(), sr.getScaledHeight());
        if (OpenGlHelper.isFramebufferEnabled()) {
            RenderUtil.pushScissor(x, y + 1.0f, width, height);
            BlurUtil.blurShader.listShaders.get(0).getShaderManager().getShaderUniform("Radius").set(intensity);
            BlurUtil.blurShader.listShaders.get(1).getShaderManager().getShaderUniform("Radius").set(intensity);
            BlurUtil.blurShader.listShaders.get(0).getShaderManager().getShaderUniform("BlurDir").set(2.0f);
            BlurUtil.blurShader.listShaders.get(1).getShaderManager().getShaderUniform("BlurDir").set(1.0f);
            framebuffer.bindFramebuffer(true);
            blurShader.loadShaderGroup(BlurUtil.mc.getTimer().renderPartialTicks);
            mc.getFramebuffer().bindFramebuffer(true);
            RenderUtil.popScissor();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            framebuffer.framebufferRenderExt(BlurUtil.mc.displayWidth, BlurUtil.mc.displayHeight, false);
            GlStateManager.disableBlend();
            GL11.glScalef((float)currentScale, (float)currentScale, (float)0.0f);
        }
    }

    private static void checkScale(int scaleFactor, int widthFactor, int heightFactor) {
        if (lastScale != scaleFactor || lastScaleWidth != widthFactor || lastScaleHeight != heightFactor || framebuffer == null || blurShader == null) {
            try {
                blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), new ResourceLocation("shaders/post/blur.json"));
                blurShader.createBindFramebuffers(BlurUtil.mc.displayWidth, BlurUtil.mc.displayHeight);
                framebuffer = BlurUtil.blurShader.mainFramebuffer;
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        lastScale = scaleFactor;
        lastScaleWidth = widthFactor;
        lastScaleHeight = heightFactor;
    }
}

