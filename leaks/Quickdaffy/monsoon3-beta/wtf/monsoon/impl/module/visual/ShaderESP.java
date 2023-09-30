/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 */
package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.shader.OutlineShader;
import wtf.monsoon.impl.event.EventRenderHotbar;

public class ShaderESP
extends Module {
    public Setting<Boolean> outline = new Setting<Boolean>("Outline", true).describedBy("Whether to render the outlines.");
    public Setting<Boolean> filled = new Setting<Boolean>("Filled", true).describedBy("Whether to render filled inside of players.");
    private final Setting<String> targets = new Setting<String>("Entities", "Entities").describedBy("Set valid targets for Aura.");
    private final Setting<Boolean> targetPlayers = new Setting<Boolean>("Players", true).describedBy("Target players.").childOf(this.targets);
    private final Setting<Boolean> targetAnimals = new Setting<Boolean>("Animals", false).describedBy("Target animals.").childOf(this.targets);
    private final Setting<Boolean> targetMonsters = new Setting<Boolean>("Monsters", false).describedBy("Target monsters.").childOf(this.targets);
    private final Setting<Boolean> targetInvisibles = new Setting<Boolean>("Invisibles", false).describedBy("Target invisibles.").childOf(this.targets);
    public OutlineShader outlineShader;
    public Framebuffer framebuffer;
    public float lastScaleFactor;
    public float lastScaleWidth;
    public float lastScaleHeight;
    @EventLink
    public final Listener<EventRenderHotbar.Pre> eventRender2D = event -> {
        if (this.mc.thePlayer.ticksExisted < 60) {
            return;
        }
        ScaledResolution sr = new ScaledResolution(this.mc);
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        if (this.framebuffer != null) {
            this.framebuffer.framebufferClear();
            if (this.lastScaleFactor != (float)sr.getScaleFactor() || this.lastScaleWidth != (float)sr.getScaledWidth() || this.lastScaleHeight != (float)sr.getScaledHeight()) {
                this.framebuffer.deleteFramebuffer();
                this.framebuffer = new Framebuffer(this.mc.displayWidth, this.mc.displayHeight, true);
                this.framebuffer.framebufferClear();
            }
            this.lastScaleFactor = sr.getScaleFactor();
            this.lastScaleWidth = sr.getScaledWidth();
            this.lastScaleHeight = sr.getScaledHeight();
        } else {
            this.framebuffer = new Framebuffer(this.mc.displayWidth, this.mc.displayHeight, true);
        }
        this.framebuffer.bindFramebuffer(false);
        boolean previousShadows = this.mc.gameSettings.field_181151_V;
        this.mc.gameSettings.field_181151_V = false;
        this.mc.entityRenderer.setupCameraTransform(this.mc.getTimer().renderPartialTicks, 0);
        Color color = ColorUtil.fadeBetween(20, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);
        int colorInt = color.getRGB();
        if (Minecraft.getMinecraft().thePlayer.ticksExisted % 150 == 0 || Wrapper.getMonsoon().getTargetManager().getLoadedEntitySize() != Minecraft.getMinecraft().theWorld.loadedEntityList.size()) {
            Wrapper.getMonsoon().getTargetManager().updateTargets(this.targetPlayers.getValue(), this.targetAnimals.getValue(), this.targetMonsters.getValue(), this.targetInvisibles.getValue());
            Wrapper.getMonsoon().getTargetManager().setLoadedEntitySize(Minecraft.getMinecraft().theWorld.loadedEntityList.size());
        }
        List targets = this.mc.theWorld.getLoadedEntityLivingBases().stream().filter(entity -> entity != Minecraft.getMinecraft().thePlayer).filter(entity -> entity.ticksExisted > 15).filter(entity -> this.mc.thePlayer.getDistanceToEntity((Entity)entity) <= 250.0f).filter(entity -> Minecraft.getMinecraft().theWorld.loadedEntityList.contains(entity)).filter(this::validTarget).sorted(Comparator.comparingDouble(entity -> Minecraft.getMinecraft().thePlayer.getDistanceSqToEntity((Entity)entity))).collect(Collectors.toList());
        for (Entity entity2 : targets) {
            if (entity2 == null || entity2 == this.mc.thePlayer || !(entity2 instanceof EntityLivingBase)) continue;
            this.mc.getRenderManager().renderEntityStatic(entity2, this.mc.getTimer().renderPartialTicks, false);
        }
        this.mc.gameSettings.field_181151_V = previousShadows;
        GlStateManager.enableBlend();
        GL11.glBlendFunc((int)770, (int)771);
        this.framebuffer.unbindFramebuffer();
        this.mc.getFramebuffer().bindFramebuffer(true);
        this.mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.pushMatrix();
        this.outlineShader.setColour(new Color(colorInt));
        this.outlineShader.setWidth(1.0f);
        this.outlineShader.setFill(this.filled.getValue() != false ? 1 : 0);
        this.outlineShader.setOutline(this.outline.getValue() != false ? 1 : 0);
        this.outlineShader.startShader();
        this.mc.entityRenderer.setupOverlayRendering();
        GL11.glBindTexture((int)3553, (int)this.framebuffer.framebufferTexture);
        GL11.glBegin((int)7);
        GL11.glTexCoord2d((double)0.0, (double)1.0);
        GL11.glVertex2d((double)0.0, (double)0.0);
        GL11.glTexCoord2d((double)0.0, (double)0.0);
        GL11.glVertex2d((double)0.0, (double)sr.getScaledHeight());
        GL11.glTexCoord2d((double)1.0, (double)0.0);
        GL11.glVertex2d((double)sr.getScaledWidth(), (double)sr.getScaledHeight());
        GL11.glTexCoord2d((double)1.0, (double)1.0);
        GL11.glVertex2d((double)sr.getScaledWidth(), (double)0.0);
        GL11.glEnd();
        GL20.glUseProgram((int)0);
        GL11.glPopMatrix();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
        this.mc.entityRenderer.setupOverlayRendering();
    };

    public ShaderESP() {
        super("Shader ESP", "Looks hot.", Category.VISUAL);
        this.outlineShader = new OutlineShader();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private boolean validTarget(EntityLivingBase entity) {
        if (entity.isInvisible()) {
            return this.validTargetLayer2(entity) && this.targetInvisibles.getValue() != false;
        }
        return this.validTargetLayer2(entity);
    }

    private boolean validTargetLayer2(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            return this.targetPlayers.getValue();
        }
        if (entity instanceof EntityAnimal) {
            return this.targetAnimals.getValue();
        }
        if (entity instanceof EntityMob) {
            return this.targetMonsters.getValue();
        }
        if (entity instanceof EntityVillager || entity instanceof EntityArmorStand) {
            return false;
        }
        return false;
    }

    public Setting<String> getTargets() {
        return this.targets;
    }

    public Setting<Boolean> getTargetPlayers() {
        return this.targetPlayers;
    }

    public Setting<Boolean> getTargetAnimals() {
        return this.targetAnimals;
    }

    public Setting<Boolean> getTargetMonsters() {
        return this.targetMonsters;
    }

    public Setting<Boolean> getTargetInvisibles() {
        return this.targetInvisibles;
    }
}

