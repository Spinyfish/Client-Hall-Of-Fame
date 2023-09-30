package club.pulsive.impl.module.impl.visual;

import club.pulsive.api.main.Pulsive;
import club.pulsive.api.yoint.shader.impl.BoxBlur;
import club.pulsive.impl.event.client.ShaderEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.util.math.MathUtil;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.RoundedUtil;
import club.pulsive.impl.util.render.StencilUtil;
import club.pulsive.impl.util.render.secondary.ShaderUtil;
import club.pulsive.impl.util.render.shaders.Bloom;
import club.pulsive.impl.util.render.shaders.Blur;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Shaders", category = Category.CLIENT)
public class Shaders extends Module {
    public static final Property<Boolean> blur = new Property<>("Blur", true);
    public static final DoubleProperty blurRadius = new DoubleProperty("Blur Radius", 10, 1, 50, 1, blur::getValue);
    private final Property<Boolean> shadow = new Property<>("Shadow", true);
    private final DoubleProperty shadowRadius = new DoubleProperty("Shadow Radius", 6, 1, 20,1, shadow::getValue);
    private final DoubleProperty shadowOffset = new DoubleProperty("Shadow Offset", 2, 1, 15,1, shadow::getValue);
    private final Property<Boolean> glow = new Property<>("Glow", false);
    private final DoubleProperty glowRadius = new DoubleProperty("Radius", 4, 2, 30, 1);
    private final DoubleProperty glowExposure = new DoubleProperty("Exposure", 2.2, .5, 3.5, .1);
    private Framebuffer shadowFramebuffer = new Framebuffer(1, 1, false), framebuffer, outlineFrameBuffer, glowFrameBuffer;
    private final ShaderUtil outlineShader = new ShaderUtil("outline.frag"), glowShader = new ShaderUtil("glow.frag");
    
    
    public void renderBlurAndShadow(){
        ShaderEvent shaderEvent = new ShaderEvent(ShaderEvent.ShaderType.BLUR);
        if(mc.thePlayer.ticksExisted < 5 || !this.isToggled()) return;
        if(blur.getValue()){
            StencilUtil.initStencilToWrite();
            shaderEvent.setShaderType(ShaderEvent.ShaderType.BLUR);
            Pulsive.INSTANCE.getEventBus().call(shaderEvent);
            StencilUtil.readStencilBuffer(1);
            Blur.renderBlur(blurRadius.getValue().floatValue());
            StencilUtil.uninitStencilBuffer();
        }
        
        if(shadow.getValue()){
            shadowFramebuffer = RenderUtil.createFramebuffer(shadowFramebuffer, true);
            shadowFramebuffer.framebufferClear();
            shadowFramebuffer.bindFramebuffer(true);
            shaderEvent.setShaderType(ShaderEvent.ShaderType.SHADOW);
            Pulsive.INSTANCE.getEventBus().call(shaderEvent);
            shadowFramebuffer.unbindFramebuffer();
            Bloom.renderBloom(shadowFramebuffer.framebufferTexture, shadowRadius.getValue().intValue(), shadowOffset.getValue().intValue());
        }

        if(glow.getValue()){
            framebuffer = RenderUtil.createFramebuffer(framebuffer, true);
            outlineFrameBuffer = RenderUtil.createFramebuffer(outlineFrameBuffer, true);
            glowFrameBuffer = RenderUtil.createFramebuffer(glowFrameBuffer, true);


            ScaledResolution sr = new ScaledResolution(mc);
            if (framebuffer != null && outlineFrameBuffer != null) {
                GlStateManager.enableAlpha();
                GlStateManager.alphaFunc(516, 0.0f);
                GlStateManager.enableBlend();
                OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

                outlineFrameBuffer.framebufferClear();
                outlineFrameBuffer.bindFramebuffer(true);
                outlineShader.init();
                setupOutlineUniforms(0, 1);
                RenderUtil.bindTexture(framebuffer.framebufferTexture);
                ShaderUtil.drawQuads();
                outlineShader.init();
                setupOutlineUniforms(1, 0);
                RenderUtil.bindTexture(framebuffer.framebufferTexture);
                ShaderUtil.drawQuads();
                outlineShader.unload();
                outlineFrameBuffer.unbindFramebuffer();

                GlStateManager.color(1, 1, 1, 1);
                glowFrameBuffer.framebufferClear();
                glowFrameBuffer.bindFramebuffer(true);
                glowShader.init();
                setupGlowUniforms(1, 0);
                RenderUtil.bindTexture(outlineFrameBuffer.framebufferTexture);
                ShaderUtil.drawQuads();
                glowShader.unload();
                glowFrameBuffer.unbindFramebuffer();

                mc.getFramebuffer().bindFramebuffer(true);
                glowShader.init();
                setupGlowUniforms(0, 1);
                glActiveTexture(GL_TEXTURE16);
                RenderUtil.bindTexture(framebuffer.framebufferTexture);
                glActiveTexture(GL_TEXTURE0);
                RenderUtil.bindTexture(glowFrameBuffer.framebufferTexture);
                ShaderUtil.drawQuads();
                glowShader.unload();

                framebuffer.framebufferClear();
                framebuffer.bindFramebuffer(true);
                shaderEvent.setShaderType(ShaderEvent.ShaderType.GLOW);
                Pulsive.INSTANCE.getEventBus().call(shaderEvent);
                framebuffer.unbindFramebuffer();
                mc.getFramebuffer().bindFramebuffer(true);
            }
        }
    }

    public void setupGlowUniforms(float dir1, float dir2) {
        Color color = new Color(HUD.getColor());
        glowShader.setUniformi("texture", 0);
        glowShader.setUniformi("textureToCheck", 16);
        glowShader.setUniformf("radius", glowRadius.getValue().floatValue());
        glowShader.setUniformf("texelSize", 1.0f / mc.displayWidth, 1.0f / mc.displayHeight);
        glowShader.setUniformf("direction", dir1, dir2);
        glowShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        glowShader.setUniformf("exposure", glowExposure.getValue().floatValue());
        glowShader.setUniformi("avoidTexture", 1);

        final FloatBuffer buffer = BufferUtils.createFloatBuffer(256);
        for (int i = 1; i <= glowRadius.getValue().intValue(); i++) {
            buffer.put(MathUtil.calculateGaussianValue(i, glowRadius.getValue().intValue() / 2));
        }
        buffer.rewind();
        glUniform1(glowShader.getUniform("weights"), buffer);
    }


    public void setupOutlineUniforms(float dir1, float dir2) {
        Color color = new Color(HUD.getColor());
        outlineShader.setUniformi("texture", 0);
        outlineShader.setUniformf("radius", glowRadius.getValue().floatValue() / 1.5f);
        outlineShader.setUniformf("texelSize", 1.0f / mc.displayWidth, 1.0f / mc.displayHeight);
        outlineShader.setUniformf("direction", dir1, dir2);
        outlineShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }
}
