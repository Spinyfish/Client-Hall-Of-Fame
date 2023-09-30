/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.util.glu.GLU
 *  org.lwjgl.util.vector.Vector3f
 *  org.lwjgl.util.vector.Vector4f
 */
package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.DrawUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.event.EventBlur;
import wtf.monsoon.impl.event.EventRender2D;
import wtf.monsoon.impl.event.EventRender3D;

public class ESP
extends Module {
    public Setting<Mode> mode = new Setting<Mode>("Mode", Mode.HOLLOW).describedBy("The mode of ESP");
    public Setting<Boolean> blur = new Setting<Boolean>("Blur", false).describedBy("Whether to draw blurred rect");
    public Setting<Boolean> glow = new Setting<Boolean>("Glow", false).describedBy("Whether to render the glow.");
    public Setting<Boolean> healthbar = new Setting<Boolean>("Health", true).describedBy("Whether to draw the health bar");
    public Setting<Boolean> outline = new Setting<Boolean>("Outlines", true).describedBy("Whether to draw the outlines");
    public Setting<Boolean> barry = new Setting<Boolean>("Barry", false).describedBy("Barry.");
    private final Setting<String> targets = new Setting<String>("Entities", "Entities").describedBy("Set valid targets for Aura.");
    private final Setting<Boolean> targetPlayers = new Setting<Boolean>("Players", true).describedBy("Target players.").childOf(this.targets);
    private final Setting<Boolean> targetAnimals = new Setting<Boolean>("Animals", false).describedBy("Target animals.").childOf(this.targets);
    private final Setting<Boolean> targetMonsters = new Setting<Boolean>("Monsters", false).describedBy("Target monsters.").childOf(this.targets);
    private final Setting<Boolean> targetInvisibles = new Setting<Boolean>("Invisibles", false).describedBy("Target invisibles.").childOf(this.targets);
    private final FloatBuffer windowPosition = BufferUtils.createFloatBuffer((int)4);
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final Map<EntityLivingBase, float[]> entityPosMap = new HashMap<EntityLivingBase, float[]>();
    private static final Map<EntityLivingBase, float[][]> entities = new HashMap<EntityLivingBase, float[][]>();
    @EventLink
    public final Listener<EventRender3D> eventRender3D = e -> {
        if (Minecraft.getMinecraft().thePlayer.ticksExisted % 150 == 0 || Wrapper.getMonsoon().getTargetManager().getLoadedEntitySize() != Minecraft.getMinecraft().theWorld.loadedEntityList.size()) {
            Wrapper.getMonsoon().getTargetManager().updateTargets(this.targetPlayers.getValue(), this.targetAnimals.getValue(), this.targetMonsters.getValue(), this.targetInvisibles.getValue());
            Wrapper.getMonsoon().getTargetManager().setLoadedEntitySize(Minecraft.getMinecraft().theWorld.loadedEntityList.size());
        }
        if (this.mode.getValue() == Mode.FILLED || this.mode.getValue() == Mode.HOLLOW || this.mode.getValue() == Mode.BOTH || this.mode.getValue() == Mode.HEALTH_ONLY) {
            ScaledResolution sr = new ScaledResolution(this.mc);
            entities.keySet().removeIf(player -> !this.mc.theWorld.loadedEntityList.contains(player));
            if (!this.entityPosMap.isEmpty()) {
                this.entityPosMap.clear();
            }
            int scaleFactor = sr.getScaleFactor();
            List targets = this.mc.theWorld.getLoadedEntityLivingBases().stream().filter(entity -> entity != Minecraft.getMinecraft().thePlayer).filter(entity -> entity.ticksExisted > 15).filter(entity -> this.mc.thePlayer.getDistanceToEntity((Entity)entity) <= 250.0f).filter(entity -> Minecraft.getMinecraft().theWorld.loadedEntityList.contains(entity)).filter(this::validTarget).sorted(Comparator.comparingDouble(entity -> Minecraft.getMinecraft().thePlayer.getDistanceSqToEntity((Entity)entity))).collect(Collectors.toList());
            for (EntityLivingBase player2 : targets) {
                if (player2.getDistanceToEntity(this.mc.thePlayer) < 1.0f && this.mc.gameSettings.thirdPersonView < 1) continue;
                GlStateManager.pushMatrix();
                Vec3 vec3 = this.getVec3(player2);
                float posX = (float)(vec3.x - this.mc.getRenderManager().viewerPosX);
                float posY = (float)(vec3.y - this.mc.getRenderManager().viewerPosY);
                float posZ = (float)(vec3.z - this.mc.getRenderManager().viewerPosZ);
                double halfWidth = (double)player2.width / 2.0 + (double)0.18f;
                AxisAlignedBB bb = new AxisAlignedBB((double)posX - halfWidth, posY, (double)posZ - halfWidth, (double)posX + halfWidth, (double)(posY + player2.height) + 0.18, (double)posZ + halfWidth);
                double[][] vectors = new double[][]{{bb.minX, bb.minY, bb.minZ}, {bb.minX, bb.maxY, bb.minZ}, {bb.minX, bb.maxY, bb.maxZ}, {bb.minX, bb.minY, bb.maxZ}, {bb.maxX, bb.minY, bb.minZ}, {bb.maxX, bb.maxY, bb.minZ}, {bb.maxX, bb.maxY, bb.maxZ}, {bb.maxX, bb.minY, bb.maxZ}};
                Vector4f position = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0f, -1.0f);
                for (double[] vec : vectors) {
                    Vector3f projection = this.project2D((float)vec[0], (float)vec[1], (float)vec[2], scaleFactor);
                    if (projection == null || !(projection.z >= 0.0f) || !(projection.z < 1.0f)) continue;
                    position.x = Math.min(position.x, projection.x);
                    position.y = Math.min(position.y, projection.y);
                    position.z = Math.max(position.z, projection.x);
                    position.w = Math.max(position.w, projection.y);
                }
                this.entityPosMap.put(player2, new float[]{position.x, position.z, position.y, position.w});
                GlStateManager.popMatrix();
            }
        }
    };
    @EventLink
    public final Listener<EventBlur> eventBlur = e -> {
        if (this.blur.getValue().booleanValue()) {
            for (EntityLivingBase player : this.entityPosMap.keySet()) {
                if (player.isInvisible()) continue;
                GL11.glPushMatrix();
                float[] positions = this.entityPosMap.get(player);
                float x = positions[0];
                float x2 = positions[1];
                float y = positions[2];
                float y2 = positions[3];
                Gui.drawRect(x, y, x2, y2, -1);
                GL11.glPopMatrix();
            }
        }
    };
    @EventLink
    public final Listener<EventRender2D> eventRender2D = e -> {
        Color color = ColorUtil.fadeBetween(20, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);
        Color alphaColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
        int colorInt = color.getRGB();
        if (this.mode.getValue() == Mode.FILLED || this.mode.getValue() == Mode.HOLLOW || this.mode.getValue() == Mode.BOTH || this.mode.getValue() == Mode.HEALTH_ONLY) {
            for (EntityLivingBase player : this.entityPosMap.keySet()) {
                if (player.isInvisible()) continue;
                GL11.glPushMatrix();
                float[] positions = this.entityPosMap.get(player);
                float x = positions[0];
                float x2 = positions[1];
                float y = positions[2];
                float y2 = positions[3];
                if (this.healthbar.getValue().booleanValue() || this.mode.getValue() == Mode.HEALTH_ONLY) {
                    Gui.drawRect((double)x - 2.5, y - 0.5f, x - 0.5f, y2 + 0.5f, -1778384896);
                    float health = player.getHealth();
                    float maxHealth = player.getMaxHealth();
                    float healthPercentage = health / maxHealth;
                    float heightDif = y - y2;
                    float healthBarHeight = heightDif * healthPercentage;
                    int col = ESP.getColorFromPercentage(health, maxHealth);
                    Gui.drawRect(x - 2.0f, y, x - 1.0f, y - healthBarHeight, col);
                    double armorPercentage = (double)player.getTotalArmorValue() / 20.0;
                    double armorBarWidth = (double)(x2 - x) * armorPercentage;
                    Gui.drawRect(x, y2, x2, y2 + 2.0f, -1778384896);
                    if (armorPercentage > 0.0) {
                        Gui.drawRect((double)x + 0.5, (double)y2 + 0.5, (double)x + armorBarWidth - 0.5, (double)y2 + 1.5, ColorUtil.darker(colorInt, 10.0f));
                    }
                }
                switch (this.mode.getValue()) {
                    case FILLED: {
                        Color shit1 = new Color(colorInt);
                        Color shit2 = new Color(shit1.getRed(), shit1.getGreen(), shit1.getBlue(), 100);
                        Gui.drawRect(x, y, x2, y2, shit2.getRGB());
                        break;
                    }
                    case HOLLOW: {
                        GL11.glDisable((int)3553);
                        ESP.enableAlpha();
                        ESP.disableAlpha();
                        if (this.outline.getValue().booleanValue()) {
                            DrawUtil.drawHollowRectDefineWidth(x - 0.5f, y - 0.5f, x2 - 0.5f, y2 - 0.5f, 0.5f, -1778384896);
                            DrawUtil.drawHollowRectDefineWidth(x + 0.5f, y + 0.5f, x2 + 0.5f, y2 + 0.5f, 0.5f, -1778384896);
                        }
                        DrawUtil.drawHollowRectDefineWidth(x, y, x2, y2, 0.5f, colorInt);
                        GL11.glEnable((int)3553);
                        break;
                    }
                    case BOTH: {
                        Color shit3 = new Color(colorInt);
                        Color shit4 = new Color(shit3.getRed(), shit3.getGreen(), shit3.getBlue(), 100);
                        Gui.drawRect(x, y, x2, y2, shit4.getRGB());
                        GL11.glDisable((int)3553);
                        ESP.enableAlpha();
                        ESP.disableAlpha();
                        if (this.outline.getValue().booleanValue()) {
                            DrawUtil.drawHollowRectDefineWidth(x - 0.5f, y - 0.5f, x2 - 0.5f, y2 - 0.5f, 0.5f, -1778384896);
                            DrawUtil.drawHollowRectDefineWidth(x + 0.5f, y + 0.5f, x2 + 0.5f, y2 + 0.5f, 0.5f, -1778384896);
                        }
                        DrawUtil.drawHollowRectDefineWidth(x, y, x2, y2, 0.5f, colorInt);
                        GL11.glEnable((int)3553);
                    }
                }
                float width = x2 - x;
                float height = y2 - y;
                float g = width / 5.05f;
                float h = height / 5.05f;
                if (this.glow.getValue().booleanValue() && this.mode.getValue() != Mode.HEALTH_ONLY) {
                    RoundedUtils.shadow(x, y, width, height, 0.0f, 10.0f, new Color(colorInt));
                }
                if (this.barry.getValue().booleanValue()) {
                    ResourceLocation barry = new ResourceLocation("monsoon/characters/barry.png");
                    this.mc.getTextureManager().bindTexture(barry);
                    GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, x2 - x, y2 - y, x2 - x, y2 - y);
                }
                GL11.glPopMatrix();
            }
        }
    };

    public ESP() {
        super("ESP", "Helps you see entities in the world.", Category.VISUAL);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static int getColorFromPercentage(float current, float max) {
        float percentage = current / max / 3.0f;
        return Color.HSBtoRGB(percentage, 1.0f, 1.0f);
    }

    private Vector3f project2D(float x, float y, float z, int scaleFactor) {
        GL11.glGetFloat((int)2982, (FloatBuffer)this.modelMatrix);
        GL11.glGetFloat((int)2983, (FloatBuffer)this.projectionMatrix);
        GL11.glGetInteger((int)2978, (IntBuffer)this.viewport);
        if (GLU.gluProject((float)x, (float)y, (float)z, (FloatBuffer)this.modelMatrix, (FloatBuffer)this.projectionMatrix, (IntBuffer)this.viewport, (FloatBuffer)this.windowPosition)) {
            return new Vector3f(this.windowPosition.get(0) / (float)scaleFactor, ((float)this.mc.displayHeight - this.windowPosition.get(1)) / (float)scaleFactor, this.windowPosition.get(2));
        }
        return null;
    }

    public static void enableAlpha() {
        GL11.glEnable((int)3042);
        GL14.glBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
    }

    public static void disableAlpha() {
        GL11.glDisable((int)3042);
    }

    private Vec3 getVec3(EntityLivingBase var0) {
        float timer = this.mc.getTimer().renderPartialTicks;
        double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * (double)timer;
        double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * (double)timer;
        double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * (double)timer;
        return new Vec3(x, y, z);
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

    static enum Mode {
        HOLLOW,
        FILLED,
        BOTH,
        HEALTH_ONLY;

    }
}

