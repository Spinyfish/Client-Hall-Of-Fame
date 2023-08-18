package me.jinthium.straight.impl.modules.visual;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.ParentAttribute;
import me.jinthium.straight.api.shader.ShaderUtil;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.movement.WorldEvent;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.event.render.RenderChestEvent;
import me.jinthium.straight.impl.event.render.RenderModelEvent;
import me.jinthium.straight.impl.settings.*;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.impl.DecelerateAnimation;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.GLUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityChest;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1fv;

public class GlowESP extends Module {

    private final BooleanSetting kawaseGlow = new BooleanSetting("Kawase Glow", false);
    private final ModeSetting colorMode = new ModeSetting("Color Mode", "Sync", "Sync", "Random", "Custom");
    private final MultiBoolSetting validEntities = new MultiBoolSetting("Entities",
            new BooleanSetting("Players", true),
            new BooleanSetting("Animals", true),
            new BooleanSetting("Mobs", true),
            new BooleanSetting("Chests", true));
    private final ColorSetting playerColor = new ColorSetting("Player Color", Client.INSTANCE.getClientColor());
    private final ColorSetting animalColor = new ColorSetting("Animal Color", Client.INSTANCE.getClientColor().darker());
    private final ColorSetting mobColor = new ColorSetting("Mob Color", Color.RED);
    private final ColorSetting chestColor = new ColorSetting("Chest Color", Color.GREEN);
    private final ColorSetting hurtTimeColor = new ColorSetting("Hurt Time Color", Color.RED);
    private final NumberSetting radius = new NumberSetting("Radius", 4, 2, 20, 2);
    private final NumberSetting iterationsSetting = new NumberSetting("Iterations", 4, 2, 10, 1);
    private final NumberSetting offsetSetting = new NumberSetting("Offset", 4, 2, 10, 1);
    private final NumberSetting exposure = new NumberSetting("Exposure", 2.2, .5, 3.5, .1);
    private final BooleanSetting seperate = new BooleanSetting("Sep Texture", false);

    public GlowESP() {
        super("GlowESP", Category.VISUALS);
        playerColor.addParent(colorMode, modeSetting -> modeSetting.is("Custom") && validEntities.getSetting("Players").isEnabled());
        animalColor.addParent(colorMode, modeSetting -> modeSetting.is("Custom") && validEntities.getSetting("Animals").isEnabled());
        mobColor.addParent(colorMode, modeSetting -> modeSetting.is("Custom") && validEntities.getSetting("Mobs").isEnabled());
        chestColor.addParent(colorMode, modeSetting -> modeSetting.is("Custom") && validEntities.getSetting("Chests").isEnabled());

        radius.addParent(kawaseGlow, ParentAttribute.BOOLEAN_CONDITION.negate());
        iterationsSetting.addParent(kawaseGlow, ParentAttribute.BOOLEAN_CONDITION);
        offsetSetting.addParent(kawaseGlow, ParentAttribute.BOOLEAN_CONDITION);
        addSettings(kawaseGlow, colorMode, validEntities, playerColor, animalColor, mobColor, chestColor, hurtTimeColor, iterationsSetting, offsetSetting, radius, exposure, seperate);
    }

    public static boolean renderNameTags = true;
    private final ShaderUtil chamsShader = new ShaderUtil("chams");
    private final ShaderUtil outlineShader = new ShaderUtil("outline");
    private final ShaderUtil glowShader = new ShaderUtil("glow");
    private final ShaderUtil kawaseGlowShader = new ShaderUtil("kawaseDownBloom");
    private final ShaderUtil kawaseGlowShader2 = new ShaderUtil("kawaseUpGlow");

    public Framebuffer framebuffer;
    public Framebuffer outlineFrameBuffer;

    public Framebuffer glowFrameBuffer;
    public static boolean renderGlint = true;

    private final List<Entity> entities = new ArrayList<>();
    private final Map<Object, Color> entityColorMap = new HashMap<>();

    public static Animation fadeIn = new DecelerateAnimation(250, 1);

    private static int currentIterations;

    private static final List<Framebuffer> framebufferList = new ArrayList<>();

    @Callback
    final EventCallback<WorldEvent> worldEventEventCallback = event -> {
        entityColorMap.clear();
    };

    @Override
    public void onEnable() {
        super.onEnable();
        entityColorMap.clear();
        fadeIn = new DecelerateAnimation(250, 1);
    }

    public void createFrameBuffers() {
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        outlineFrameBuffer = RenderUtil.createFrameBuffer(outlineFrameBuffer);
    }

    @Callback
    final EventCallback<RenderChestEvent> renderChestEventEventCallback = event -> {
        if (validEntities.getSetting("Chests").isEnabled() && framebuffer != null) {
            framebuffer.bindFramebuffer(false);
            chamsShader.init();
            chamsShader.setUniformi("textureIn", 0);
            Color color = getColor(event.getEntity());

            RenderUtil.resetColor();
            chamsShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1);
            event.drawChest();
            chamsShader.unload();

            this.mc.getFramebuffer().bindFramebuffer(false);
        }
    };

    @Callback
    final EventCallback<RenderModelEvent> renderModelEventEventCallback = event -> {
        if (event.isPost() && framebuffer != null) {
            if (!entities.contains(event.getEntity())) return;
            framebuffer.bindFramebuffer(false);
            chamsShader.init();
            chamsShader.setUniformi("textureIn", 0);
            Color color = getColor(event.getEntity());

            // TODO: Fix gradient
            chamsShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1);
            RenderUtil.resetColor();
            GlStateManager.enableCull();
            renderGlint = false;
            event.drawModel();

            //Needed to add the other layers to the entity
            LayerHeldItem.stopRenderering = true;
            event.drawLayers();
            LayerHeldItem.stopRenderering = false;
            renderGlint = true;
            GlStateManager.disableCull();

            chamsShader.unload();


            mc.getFramebuffer().bindFramebuffer(false);
        }
    };

    @Callback
    final EventCallback<Render2DEvent> render2DEventEventCallback = event -> {
        createFrameBuffers();
        collectEntities();

        ScaledResolution sr = ScaledResolution.fetchResolution(mc);
        if (framebuffer != null && outlineFrameBuffer != null && (validEntities.getSetting("Chests").isEnabled() || entities.size() > 0)) {
            RenderUtil.setAlphaLimit(0);
            GLUtil.startBlend();

    /*        RenderUtil.bindTexture(framebuffer.framebufferTexture);
            ShaderUtil.drawQuads();
            framebuffer.framebufferClear();
            mc.getFramebuffer().bindFramebuffer(false);


            if(true) return;*/


            outlineFrameBuffer.framebufferClear();
            outlineFrameBuffer.bindFramebuffer(false);
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


            if (kawaseGlow.isEnabled()) {
                int offset = offsetSetting.getValue().intValue();
                int iterations = 3;

                if (framebufferList.isEmpty() || currentIterations != iterations || (framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight)) {
                    initFramebuffers(iterations);
                    currentIterations = iterations;
                }
                RenderUtil.setAlphaLimit(0);

                glBlendFunc(GL_ONE, GL_ONE);

                GL11.glClearColor(0, 0, 0, 0);
                renderFBO(framebufferList.get(1), outlineFrameBuffer.framebufferTexture, kawaseGlowShader, offset);

                //Downsample
                for (int i = 1; i < iterations; i++) {
                    renderFBO(framebufferList.get(i + 1), framebufferList.get(i).framebufferTexture, kawaseGlowShader, offset);
                }

                //Upsample
                for (int i = iterations; i > 1; i--) {
                    renderFBO(framebufferList.get(i - 1), framebufferList.get(i).framebufferTexture, kawaseGlowShader2, offset);
                }

                Framebuffer lastBuffer = framebufferList.get(0);
                lastBuffer.framebufferClear();
                lastBuffer.bindFramebuffer(false);
                kawaseGlowShader2.init();
                kawaseGlowShader2.setUniformf("offset", offset, offset);
                kawaseGlowShader2.setUniformi("inTexture", 0);
                kawaseGlowShader2.setUniformi("check", seperate.isEnabled() ? 1 : 0);
                kawaseGlowShader2.setUniformf("lastPass", 1);
                kawaseGlowShader2.setUniformf("exposure", (float) (exposure.getValue().floatValue() * fadeIn.getOutput().floatValue()));
                kawaseGlowShader2.setUniformi("textureToCheck", 16);
                kawaseGlowShader2.setUniformf("halfpixel", 1.0f / lastBuffer.framebufferWidth, 1.0f / lastBuffer.framebufferHeight);
                kawaseGlowShader2.setUniformf("iResolution", lastBuffer.framebufferWidth, lastBuffer.framebufferHeight);
                GL13.glActiveTexture(GL13.GL_TEXTURE16);
                RenderUtil.bindTexture(framebuffer.framebufferTexture);
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                RenderUtil.bindTexture(framebufferList.get(1).framebufferTexture);

                ShaderUtil.drawQuads();
                kawaseGlowShader2.unload();

                GL11.glClearColor(0, 0, 0, 0);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                framebuffer.framebufferClear();
                RenderUtil.resetColor();
                mc.getFramebuffer().bindFramebuffer(true);
                RenderUtil.bindTexture(framebufferList.get(0).framebufferTexture);
                ShaderUtil.drawQuads();
                RenderUtil.setAlphaLimit(0);
                GlStateManager.bindTexture(0);
            } else {
                if (!framebufferList.isEmpty()) {
                    for (Framebuffer framebuffer : framebufferList) {
                        framebuffer.deleteFramebuffer();
                    }
                    glowFrameBuffer = null;
                    framebufferList.clear();
                }

                glowFrameBuffer = RenderUtil.createFrameBuffer(glowFrameBuffer);

                GL11.glClearColor(0, 0, 0, 0);
                glowFrameBuffer.framebufferClear();
                glowFrameBuffer.bindFramebuffer(false);
                glowShader.init();
                setupGlowUniforms(1f, 0);
                RenderUtil.bindTexture(outlineFrameBuffer.framebufferTexture);
                GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
                ShaderUtil.drawQuads();
                glowShader.unload();

                mc.getFramebuffer().bindFramebuffer(false);

                GL11.glClearColor(0, 0, 0, 0);
                glowShader.init();
                setupGlowUniforms(0, 1f);
                if (seperate.isEnabled()) {
                    GL13.glActiveTexture(GL13.GL_TEXTURE16);
                    RenderUtil.bindTexture(framebuffer.framebufferTexture);
                }
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                RenderUtil.bindTexture(glowFrameBuffer.framebufferTexture);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                ShaderUtil.drawQuads();
                glowShader.unload();

                framebuffer.framebufferClear();
                mc.getFramebuffer().bindFramebuffer(false);


            }
        }
    };

    private void initFramebuffers(float iterations) {
        for (Framebuffer framebuffer : framebufferList) {
            framebuffer.deleteFramebuffer();
        }
        framebufferList.clear();

        //Have to make the framebuffer null so that it does not try to delete a framebuffer that has already been deleted
        framebufferList.add(glowFrameBuffer = RenderUtil.createFrameBuffer(null));


        for (int i = 1; i <= iterations; i++) {
            Framebuffer currentBuffer = new Framebuffer((int) (mc.displayWidth / Math.pow(2, i)), (int) (mc.displayHeight / Math.pow(2, i)), true);
            currentBuffer.setFramebufferFilter(GL_LINEAR);

            GlStateManager.bindTexture(currentBuffer.framebufferTexture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
            GlStateManager.bindTexture(0);

            framebufferList.add(currentBuffer);
        }
    }

    private static void renderFBO(Framebuffer framebuffer, int framebufferTexture, ShaderUtil shader, float offset) {
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(false);
        shader.init();
        RenderUtil.bindTexture(framebufferTexture);
        shader.setUniformf("offset", offset, offset);
        shader.setUniformi("inTexture", 0);
        shader.setUniformi("check", 0);
        shader.setUniformf("lastPass", 0);
        shader.setUniformf("halfpixel", 1.0f / framebuffer.framebufferWidth, 1.0f / framebuffer.framebufferHeight);
        shader.setUniformf("iResolution", framebuffer.framebufferWidth, framebuffer.framebufferHeight);

        ShaderUtil.drawQuads();
        shader.unload();
    }


    public void setupGlowUniforms(float dir1, float dir2) {
        glowShader.setUniformi("texture", 0);
        if (seperate.isEnabled()) {
            glowShader.setUniformi("textureToCheck", 16);
        }
        glowShader.setUniformf("radius", radius.getValue().floatValue());
        glowShader.setUniformf("texelSize", 1.0f / mc.displayWidth, 1.0f / mc.displayHeight);
        glowShader.setUniformf("direction", dir1, dir2);
        glowShader.setUniformf("exposure", exposure.getValue().floatValue() * fadeIn.getOutput().floatValue());
        glowShader.setUniformi("avoidTexture", seperate.isEnabled() ? 1 : 0);

        final FloatBuffer buffer = BufferUtils.createFloatBuffer(256);
        for (int i = 1; i <= radius.getValue().floatValue(); i++) {
            buffer.put(MathUtils.calculateGaussianValue(i, radius.getValue().floatValue() / 2));
        }
        buffer.rewind();

        glUniform1fv(glowShader.getUniform("weights"), buffer);
    }


    public void setupOutlineUniforms(float dir1, float dir2) {
        outlineShader.setUniformi("textureIn", 0);
        float iterations = kawaseGlow.isEnabled() ? (iterationsSetting.getValue().floatValue() * 2f) : radius.getValue().floatValue() / 1.5f;
        outlineShader.setUniformf("radius", iterations);
        outlineShader.setUniformf("texelSize", 1.0f / mc.displayWidth, 1.0f / mc.displayHeight);
        outlineShader.setUniformf("direction", dir1, dir2);
    }


    private Color getColor(Object entity) {
        Color color = Color.WHITE;
        switch (colorMode.getMode()) {
            case "Custom" -> {
                if (entity instanceof EntityPlayer) {
                    color = playerColor.getColor();
                }
                if (entity instanceof EntityMob) {
                    color = mobColor.getColor();
                }
                if (entity instanceof EntityAnimal) {
                    color = animalColor.getColor();
                }
                if (entity instanceof TileEntityChest) {
                    color = chestColor.getColor();
                }
            }
            case "Sync" -> {
                Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
                color = hud.getHudColor(0);
            }
            case "Random" -> {
                if (entityColorMap.containsKey(entity)) {
                    color = entityColorMap.get(entity);
                } else {
                    color = new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255));
                    entityColorMap.put(entity, color);
                }
            }
        }

        if (entity instanceof EntityLivingBase entityLivingBase) {
            if (entityLivingBase.hurtTime > 0) {
                //We use a the first part of the sine wave to make the color more red as the entity gets hurt and animate it back to normal
                color = ColorUtil.interpolateColorC(color, hurtTimeColor.getColor(), (float) Math.sin(entityLivingBase.hurtTime * (18 * Math.PI / 180)));
            }
        }

        return color;
    }

    public void collectEntities() {
        entities.clear();
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (!RenderUtil.isInView(entity)) continue;
            if (entity == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) continue;
            if (entity instanceof EntityAnimal && validEntities.getSetting("animals").isEnabled()) {
                entities.add(entity);
            }

            if (entity instanceof EntityPlayer && validEntities.getSetting("players").isEnabled()) {
                entities.add(entity);
            }

            if (entity instanceof EntityMob && validEntities.getSetting("mobs").isEnabled()) {
                entities.add(entity);
            }
        }
    }
}
