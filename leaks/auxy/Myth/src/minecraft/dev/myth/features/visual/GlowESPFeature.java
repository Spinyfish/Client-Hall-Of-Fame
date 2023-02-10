/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 11.09.22, 12:53
 */
package dev.myth.features.visual;

import dev.codeman.eventbus.EventPriority;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.api.utils.render.shader.KoksFramebuffer;
import dev.myth.api.utils.render.shader.ShaderExtension;
import dev.myth.api.utils.render.shader.other.ShaderProgramOther;
import dev.myth.events.Render2DEvent;
import dev.myth.events.Render3DEvent;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.ColorSetting;
import dev.myth.settings.ListSetting;
import dev.myth.settings.NumberSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.FloatBuffer;

import static net.minecraft.client.renderer.OpenGlHelper.glUniform1;

@Feature.Info(name = "GlowESP", category = Feature.Category.VISUAL)
public class GlowESPFeature extends Feature {

    public Framebuffer framebuffer;
    public Framebuffer outlineFrameBuffer;
    public Framebuffer glowFrameBuffer;

    public final ListSetting<Targets> targets = new ListSetting<>("Targets", Targets.PLAYER);
    public final ColorSetting espColor = new ColorSetting("Color", new Color(244, 0, 0, 255));
    public final BooleanSetting seperate = new BooleanSetting("Seperate", true);
    public final NumberSetting radius = new NumberSetting("Radius", 1, 1, 20, 1);
    public final NumberSetting sigma = new NumberSetting("Sigma", 1, 1, 80, 1);
    public final NumberSetting alpha = new NumberSetting("Alpha", 1, 1, 40, 1);

    public static ShaderProgramOther glowShader;
    public static ShaderProgramOther outlineShader;

    public GlowESPFeature() {
//        glowShader = new ShaderProgramOther("esp.glsl");
    }

    public void createFrameBuffers() {
        framebuffer = KoksFramebuffer.doFrameBuffer(framebuffer);
        outlineFrameBuffer = KoksFramebuffer.doFrameBuffer(outlineFrameBuffer);
        glowFrameBuffer = KoksFramebuffer.doFrameBuffer(glowFrameBuffer);
    }

    public boolean shouldRender(Entity entity) {

        if(entity == null) return false;

        if (entity.isInvisibleToPlayer(MC.thePlayer) && !targets.isEnabled("Invisibles")) return false;

        if (entity instanceof EntityPlayer && targets.isEnabled("Players")){
            if(entity != MC.thePlayer || getGameSettings().thirdPersonView != 0) return true;
        }
        if (entity instanceof EntityAnimal && targets.isEnabled("Animals")) return true;
        if (entity instanceof EntityAmbientCreature && targets.isEnabled("Animals")) return true;
        if (entity instanceof EntityWaterMob && targets.isEnabled("Animals")) return true;
        if (entity instanceof EntityMob && targets.isEnabled("Mobs")) return true;
        if (entity instanceof EntityItem && targets.isEnabled("Items")) return true;
        if (entity instanceof EntityVillager && targets.isEnabled("Villager")) return true;

        return false;
    }

    @Handler(EventPriority.HIGHEST)
    public final Listener<Render2DEvent> eventListener = event -> {
        if (getPlayer() == null || getWorld() == null || getWorld().loadedEntityList.isEmpty())
            return;

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        ShaderExtension.useShader(glowShader.getShaderProgramID());
        glowShader.setShaderUniformI("u_texture", 0);
        glowShader.setShaderUniform("u_texelSize", 1.0F / (float) framebuffer.framebufferWidth, 1.0F / (float) this.framebuffer.framebufferHeight);
        glowShader.setShaderUniform("u_color", espColor.getRed() / 255.0F, espColor.getGreen() / 255.0F, espColor.getBlue() / 255.0F);
        glowShader.setShaderUniform("u_radius", radius.getValue().floatValue());
        glowShader.setShaderUniform("u_alpha", alpha.getValue().floatValue());
        glowShader.setShaderUniform("u_direction", 1, 0);

        MC.entityRenderer.setupOverlayRendering();
        bindTexture(framebuffer.framebufferTexture);
        KoksFramebuffer.drawQuads();
        ShaderExtension.deleteProgram();

        ShaderExtension.useShader(glowShader.getShaderProgramID());
        glowShader.setShaderUniform("u_direction", 0, 1);

        MC.entityRenderer.setupOverlayRendering();
        bindTexture(framebuffer.framebufferTexture);
        KoksFramebuffer.drawQuads();
        ShaderExtension.deleteProgram();

        GlStateManager.bindTexture(0);

        GlStateManager.disableBlend();

//        GlStateManager.enableAlpha();
//        GlStateManager.alphaFunc(516, 0.0f);
//        GlStateManager.enableBlend();
//        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
//
//        outlineFrameBuffer.framebufferClear();
//        outlineFrameBuffer.bindFramebuffer(true);
//        ShaderExtension.useShader(outlineShader.getShaderProgramID());
//        setupOutlineUniforms(0, 1, espColor.getValue());
//        bindTexture(framebuffer.framebufferTexture);
//        KoksFramebuffer.drawQuads();
//        ShaderExtension.useShader(outlineShader.getShaderProgramID());
//        setupOutlineUniforms(1, 0, espColor.getValue());
//        bindTexture(framebuffer.framebufferTexture);
//        KoksFramebuffer.drawQuads();
//        ShaderExtension.deleteProgram();
//        outlineFrameBuffer.unbindFramebuffer();
//
//        GlStateManager.color(1, 1, 1, 1);
//        glowFrameBuffer.framebufferClear();
//        glowFrameBuffer.bindFramebuffer(true);
//        ShaderExtension.useShader(glowShader.getShaderProgramID());
//        setupGlowUniforms(1F, (float) 0, espColor.getValue());
//        bindTexture(outlineFrameBuffer.framebufferTexture);
//        KoksFramebuffer.drawQuads();
//        ShaderExtension.deleteProgram();
//        glowFrameBuffer.unbindFramebuffer();
//
//        MC.getFramebuffer().bindFramebuffer(true);
//        ShaderExtension.useShader(glowShader.getShaderProgramID());
//        setupGlowUniforms((float) 0, 1F, espColor.getValue());
//        if (seperate.getValue()) {
//            GL13.glActiveTexture(GL13.GL_TEXTURE16);
//            bindTexture(framebuffer.framebufferTexture);
//        }
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        bindTexture(glowFrameBuffer.framebufferTexture);
//        KoksFramebuffer.drawQuads();
//        ShaderExtension.deleteProgram();
//        GlStateManager.disableBlend();
//        MC.entityRenderer.disableLightmap();

//        GlStateManager.disableDepth();
    };

    @Handler(EventPriority.HIGHEST)
    public final Listener<Render3DEvent> render3DEventListener = event -> {
        if (getPlayer() == null || getWorld() == null || getWorld().loadedEntityList.isEmpty())
            return;
        createFrameBuffers();
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        RendererLivingEntity.NAME_TAG_RANGE = 0;
        RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 0;
        renderEntities(event.getPartialTicks());
        RendererLivingEntity.NAME_TAG_RANGE = 64;
        RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 32;
        framebuffer.unbindFramebuffer();
        MC.getFramebuffer().bindFramebuffer(true);
        GlStateManager.disableLighting();
    };


    public void renderEntities(float ticks) {
        for (Entity e : MC.theWorld.loadedEntityList) {
//            if (e == MC.thePlayer) continue;
            if (RenderUtil.isInViewFrustrum(e) && shouldRender(e)) {
                MC.getRenderManager().renderEntityStatic(e, ticks, false);
            }
        }
    }


    public void setupOutlineUniforms(float dir1, float dir2, Color color) {
        outlineShader.setShaderUniformI("texture", 0);
        outlineShader.setShaderUniform("radius", radius.getValue().floatValue());
        outlineShader.setShaderUniform("texelSize", 1.1f / MC.displayWidth, 1.1f / MC.displayHeight);
        outlineShader.setShaderUniform("direction", dir1, dir2);
        outlineShader.setShaderUniform("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }

    public void setupGlowUniforms(float dir1, float dir2, Color color) {
        glowShader.setShaderUniformI("u_texture", 0);
        if (seperate.getValue()) {
            glowShader.setShaderUniformI("u_texture2", 16);
        }


        glowShader.setShaderUniform("u_blurRadius", radius.getValue().floatValue());
        glowShader.setShaderUniform("u_texelSize", 1.0f / MC.displayWidth, 1.0f / MC.displayHeight);
        glowShader.setShaderUniform("u_direction", dir1, dir2);
        glowShader.setShaderUniform("u_color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        glowShader.setShaderUniform("u_alphaModifier", alpha.getValue().floatValue());
        glowShader.setShaderUniformI("avoidTexture", seperate.getValue() ? 1 : 0);
        glowShader.setShaderUniformI("overrideTexture", 1);
        glowShader.setShaderUniform("u_sigma", sigma.getValue().floatValue());
        final FloatBuffer buffer = BufferUtils.createFloatBuffer(122);
        for (int i = 1; i <= radius.getValue().floatValue(); i++) {
            buffer.put(MathUtil.calculate(i, radius.getValue().floatValue() / 2));
        }
        buffer.rewind();


        glUniform1(glowShader.getUniform("weights"), buffer);
        glowShader.setShaderUniformI("avoidTexture", seperate.getValue() ? 1 : 0);
        glowShader.setShaderUniformI("full", 1);
    }

    public static void bindTexture(int texture) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
    }

    public enum Targets {
        PLAYER("Players"),
        ANIMALS("Animals"),
        MOBS("Mobs"),
        VILLAGERS("Villager"),
        INVISIBLES("Invisibles"),
        ITEMS("Items");

        private final String name;

        Targets(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}