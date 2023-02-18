/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.shader.Framebuffer
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL20
 *  vip.astroline.client.service.event.impl.render.Event2D
 *  vip.astroline.client.service.event.impl.render.EventShader
 *  vip.astroline.client.service.event.impl.render.EventShader$ShaderType
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.service.module.value.BooleanValue
 *  vip.astroline.client.service.module.value.FloatValue
 *  vip.astroline.client.storage.utils.other.MathUtils
 *  vip.astroline.client.storage.utils.render.ShaderUtil
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 *  vip.astroline.client.storage.utils.render.shaders.Bloom
 *  vip.astroline.client.storage.utils.render.shaders.Blur
 *  vip.astroline.client.storage.utils.render.shaders.StencilUtil
 */
package vip.astroline.client.service.module.impl.render;

import java.awt.Color;
import java.nio.FloatBuffer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import vip.astroline.client.service.event.impl.render.Event2D;
import vip.astroline.client.service.event.impl.render.EventShader;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.service.module.value.BooleanValue;
import vip.astroline.client.service.module.value.FloatValue;
import vip.astroline.client.storage.utils.other.MathUtils;
import vip.astroline.client.storage.utils.render.ShaderUtil;
import vip.astroline.client.storage.utils.render.render.RenderUtil;
import vip.astroline.client.storage.utils.render.shaders.Bloom;
import vip.astroline.client.storage.utils.render.shaders.Blur;
import vip.astroline.client.storage.utils.render.shaders.StencilUtil;

public class Shaders
extends Module {
    public static BooleanValue blur = new BooleanValue("Shaders", "Blur", Boolean.valueOf(true));
    public static FloatValue blurRadius = new FloatValue("Shaders", "Blur Radius", 10.0f, 1.0f, 50.0f, 1.0f);
    public static BooleanValue shadow = new BooleanValue("Shaders", "Shadow", Boolean.valueOf(true));
    public static FloatValue shadowRadius = new FloatValue("Shaders", "Shadow Radius", 6.0f, 1.0f, 20.0f, 1.0f);
    public static FloatValue shadowOffset = new FloatValue("Shaders", "Shadow Offset", 2.0f, 1.0f, 15.0f, 1.0f);
    public static BooleanValue glow = new BooleanValue("Shaders", "Glow", Boolean.valueOf(false));
    public static FloatValue glowRadius = new FloatValue("Shaders", "Glow Radius", 4.0f, 2.0f, 30.0f, 1.0f);
    public static FloatValue glowExposure = new FloatValue("Shaders", "Glow Exposure", 2.2f, 0.5f, 3.5f, 0.1f);
    private Framebuffer shadowFramebuffer = new Framebuffer(1, 1, false);
    private Framebuffer framebuffer;
    private Framebuffer outlineFrameBuffer;
    private Framebuffer glowFrameBuffer;
    private final ShaderUtil outlineShader = new ShaderUtil("astroline/Shaders/outline.frag");
    private final ShaderUtil glowShader = new ShaderUtil("astroline/Shaders/glow.frag");

    public Shaders() {
        super("Shaders", Category.Render, 0, false);
    }

    @EventTarget
    public void renderBlurAndShadow(Event2D event) {
        EventShader shaderEvent = new EventShader(EventShader.ShaderType.BLUR);
        if (Shaders.mc.thePlayer.ticksExisted < 5) return;
        if (!this.isToggled()) {
            return;
        }
        if (blur.getValue().booleanValue()) {
            StencilUtil.initStencilToWrite();
            shaderEvent.setShaderType(EventShader.ShaderType.BLUR);
            shaderEvent.call();
            StencilUtil.readStencilBuffer((int)1);
            Blur.renderBlur((float)blurRadius.getValue().floatValue());
            StencilUtil.uninitStencilBuffer();
        }
        if (shadow.getValue().booleanValue()) {
            this.shadowFramebuffer = RenderUtil.createFramebuffer((Framebuffer)this.shadowFramebuffer, (boolean)true);
            this.shadowFramebuffer.framebufferClear();
            this.shadowFramebuffer.bindFramebuffer(true);
            shaderEvent.setShaderType(EventShader.ShaderType.SHADOW);
            shaderEvent.call();
            this.shadowFramebuffer.unbindFramebuffer();
            Bloom.renderBlur((int)this.shadowFramebuffer.framebufferTexture, (int)shadowRadius.getValue().intValue(), (int)shadowOffset.getValue().intValue());
        }
        if (glow.getValue() == false) return;
        this.framebuffer = RenderUtil.createFramebuffer((Framebuffer)this.framebuffer, (boolean)true);
        this.outlineFrameBuffer = RenderUtil.createFramebuffer((Framebuffer)this.outlineFrameBuffer, (boolean)true);
        this.glowFrameBuffer = RenderUtil.createFramebuffer((Framebuffer)this.glowFrameBuffer, (boolean)true);
        ScaledResolution sr = new ScaledResolution(mc);
        if (this.framebuffer == null) return;
        if (this.outlineFrameBuffer == null) return;
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc((int)516, (float)0.0f);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc((int)770, (int)771, (int)1, (int)0);
        this.outlineFrameBuffer.framebufferClear();
        this.outlineFrameBuffer.bindFramebuffer(true);
        this.outlineShader.init();
        this.setupOutlineUniforms(0.0f, 1.0f);
        RenderUtil.bindTexture((int)this.framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        this.outlineShader.init();
        this.setupOutlineUniforms(1.0f, 0.0f);
        RenderUtil.bindTexture((int)this.framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        this.outlineShader.unload();
        this.outlineFrameBuffer.unbindFramebuffer();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.glowFrameBuffer.framebufferClear();
        this.glowFrameBuffer.bindFramebuffer(true);
        this.glowShader.init();
        this.setupGlowUniforms(1.0f, 0.0f);
        RenderUtil.bindTexture((int)this.outlineFrameBuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        this.glowShader.unload();
        this.glowFrameBuffer.unbindFramebuffer();
        mc.getFramebuffer().bindFramebuffer(true);
        this.glowShader.init();
        this.setupGlowUniforms(0.0f, 1.0f);
        GL13.glActiveTexture((int)34000);
        RenderUtil.bindTexture((int)this.framebuffer.framebufferTexture);
        GL13.glActiveTexture((int)33984);
        RenderUtil.bindTexture((int)this.glowFrameBuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        this.glowShader.unload();
        this.framebuffer.framebufferClear();
        this.framebuffer.bindFramebuffer(true);
        shaderEvent.setShaderType(EventShader.ShaderType.GLOW);
        shaderEvent.call();
        this.framebuffer.unbindFramebuffer();
        mc.getFramebuffer().bindFramebuffer(true);
    }

    public void setupGlowUniforms(float dir1, float dir2) {
        Color color = new Color(Hud.hudColor1.getColorInt());
        this.glowShader.setUniformi("texture", new int[]{0});
        this.glowShader.setUniformi("textureToCheck", new int[]{16});
        this.glowShader.setUniformf("radius", new float[]{glowRadius.getValue().floatValue()});
        this.glowShader.setUniformf("texelSize", new float[]{1.0f / (float)Shaders.mc.displayWidth, 1.0f / (float)Shaders.mc.displayHeight});
        this.glowShader.setUniformf("direction", new float[]{dir1, dir2});
        this.glowShader.setUniformf("color", new float[]{(float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f});
        this.glowShader.setUniformf("exposure", new float[]{glowExposure.getValue().floatValue()});
        this.glowShader.setUniformi("avoidTexture", new int[]{1});
        FloatBuffer buffer = BufferUtils.createFloatBuffer((int)256);
        int i = 1;
        while (true) {
            if (i > glowRadius.getValue().intValue()) {
                buffer.rewind();
                GL20.glUniform1((int)this.glowShader.getUniform("weights"), (FloatBuffer)buffer);
                return;
            }
            buffer.put(MathUtils.calculateGaussianValue((float)i, (float)(glowRadius.getValue().floatValue() / 2.0f)));
            ++i;
        }
    }

    public void setupOutlineUniforms(float dir1, float dir2) {
        Color color = new Color(Hud.hudColor1.getColorInt());
        this.outlineShader.setUniformi("texture", new int[]{0});
        this.outlineShader.setUniformf("radius", new float[]{glowRadius.getValue().floatValue() / 1.5f});
        this.outlineShader.setUniformf("texelSize", new float[]{1.0f / (float)Shaders.mc.displayWidth, 1.0f / (float)Shaders.mc.displayHeight});
        this.outlineShader.setUniformf("direction", new float[]{dir1, dir2});
        this.outlineShader.setUniformf("color", new float[]{(float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f});
    }
}
