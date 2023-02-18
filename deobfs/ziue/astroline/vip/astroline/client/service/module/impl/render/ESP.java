/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.vecmath.Vector3f
 *  javax.vecmath.Vector4f
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GLAllocation
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.Vec3
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.util.glu.GLU
 *  vip.astroline.client.service.event.impl.render.Event2D
 *  vip.astroline.client.service.event.impl.render.Event3D
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.value.BooleanValue
 *  vip.astroline.client.service.module.value.ColorValue
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.service.module.impl.render;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.GLU;
import vip.astroline.client.service.event.impl.render.Event2D;
import vip.astroline.client.service.event.impl.render.Event3D;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.value.BooleanValue;
import vip.astroline.client.service.module.value.ColorValue;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class ESP
extends Module {
    private static Map<EntityPlayer, float[][]> entities = new HashMap<EntityPlayer, float[][]>();
    private final FloatBuffer windowPosition = BufferUtils.createFloatBuffer((int)4);
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer((int)16);
    private final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer((int)16);
    private final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer((int)16);
    private final Map<EntityPlayer, float[]> entityPosMap = new HashMap<EntityPlayer, float[]>();
    public static ColorValue espColor = new ColorValue("ESP", "Box Color", Color.WHITE);
    public static BooleanValue healthBar = new BooleanValue("ESP", "Health Bar", Boolean.valueOf(true));
    public static BooleanValue tags = new BooleanValue("ESP", "Nametags", Boolean.valueOf(true));
    public static BooleanValue box = new BooleanValue("ESP", "Box", Boolean.valueOf(true));

    public ESP() {
        super("ESP", Category.Render, 0, false);
    }

    @EventTarget
    public void onRender2D(Event2D event) {
        Iterator<EntityPlayer> iterator = this.entityPosMap.keySet().iterator();
        while (iterator.hasNext()) {
            EntityPlayer player = iterator.next();
            ScaledResolution sr = new ScaledResolution(mc);
            float middleX = (float)sr.getScaledWidth() / 2.0f;
            float middleY = (float)sr.getScaledHeight() / 2.0f;
            GL11.glPushMatrix();
            float[] positions = this.entityPosMap.get(player);
            float x = positions[0];
            float x2 = positions[1];
            float y = positions[2];
            float y2 = positions[3];
            if (healthBar.getValue().booleanValue()) {
                Gui.drawRect((double)((double)x - 2.5), (double)(y - 0.5f), (double)(x - 0.5f), (double)(y2 + 0.5f), (int)-1778384896);
                float health = player.getHealth();
                float maxHealth = player.getMaxHealth();
                float healthPercentage = health / maxHealth;
                boolean needScissor = health < maxHealth;
                float heightDif = y - y2;
                float healthBarHeight = heightDif * healthPercentage;
                if (needScissor) {
                    ESP.startScissorBox(sr, (int)x - 2, (int)(y2 + healthBarHeight), 2, (int)(-healthBarHeight) + 1);
                }
                int col = RenderUtil.getColorFromPercentage((float)health, (float)maxHealth);
                Gui.drawRect((double)(x - 2.0f), (double)y, (double)(x - 1.0f), (double)y2, (int)col);
                if (needScissor) {
                    ESP.endScissorBox();
                }
            }
            if (tags.getValue().booleanValue()) {
                String text = player.getDisplayName().getUnformattedText();
                float xDif = x2 - x;
                float minScale = 0.65f;
                float scale = Math.max(minScale, Math.min(1.0f, 1.0f - ESP.mc.thePlayer.getDistanceToEntity((Entity)player) / 100.0f));
                float yOff = Math.max(0.0f, Math.min(1.0f, ESP.mc.thePlayer.getDistanceToEntity((Entity)player) / 12.0f));
                float upscale = 1.0f / scale;
                GL11.glPushMatrix();
                GL11.glScalef((float)scale, (float)scale, (float)scale);
                FontManager.normal2.drawStringWithShadow(text, (x + xDif / 2.0f) * upscale - (float)ESP.mc.fontRendererObj.getStringWidth(text) / 2.0f, (y - 9.0f + yOff) * upscale, espColor.getColorInt());
                GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                GL11.glPopMatrix();
            }
            if (box.getValue().booleanValue()) {
                GL11.glDisable((int)3553);
                ESP.enableAlpha();
                GL11.glLineWidth((float)1.3f);
                GL11.glColor4f((float)((float)espColor.getRed() / 255.0f), (float)((float)espColor.getGreen() / 255.0f), (float)((float)espColor.getBlue() / 255.0f), (float)1.0f);
                GL11.glBegin((int)2);
                GL11.glVertex2f((float)x, (float)y);
                GL11.glVertex2f((float)x, (float)y2);
                GL11.glVertex2f((float)x2, (float)y2);
                GL11.glVertex2f((float)x2, (float)y);
                GL11.glEnd();
                ESP.disableAlpha();
                GL11.glEnable((int)3553);
            }
            GL11.glPopMatrix();
        }
    }

    @EventTarget
    public void onRender3D(Event3D event) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        entities.keySet().removeIf(player -> !ESP.mc.theWorld.playerEntities.contains(player));
        if (!this.entityPosMap.isEmpty()) {
            this.entityPosMap.clear();
        }
        if (!box.getValue().booleanValue() && !healthBar.getValue().booleanValue()) {
            if (tags.getValue() == false) return;
        }
        int scaleFactor = scaledResolution.getScaleFactor();
        float partialTicks = event.getPartialTicks();
        Iterator iterator = ESP.mc.theWorld.playerEntities.iterator();
        while (iterator.hasNext()) {
            EntityPlayer player2 = (EntityPlayer)iterator.next();
            if (player2.getDistanceToEntity((Entity)ESP.mc.thePlayer) < 1.0f) continue;
            GL11.glPushMatrix();
            Vec3 vec3 = this.getVec3(player2);
            float posX = (float)(vec3.xCoord - RenderManager.viewerPosX);
            float posY = (float)(vec3.yCoord - RenderManager.viewerPosY);
            float posZ = (float)(vec3.zCoord - RenderManager.viewerPosZ);
            double halfWidth = (double)player2.width / 2.0 + (double)0.18f;
            AxisAlignedBB bb = new AxisAlignedBB((double)posX - halfWidth, (double)posY, (double)posZ - halfWidth, (double)posX + halfWidth, (double)(posY + player2.height) + 0.18, (double)posZ + halfWidth);
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
            GL11.glPopMatrix();
        }
    }

    private Vec3 getVec3(EntityPlayer var0) {
        float timer = ESP.mc.timer.renderPartialTicks;
        double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * (double)timer;
        double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * (double)timer;
        double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * (double)timer;
        return new Vec3(x, y, z);
    }

    private Vector3f project2D(float x, float y, float z, int scaleFactor) {
        GL11.glGetFloat((int)2982, (FloatBuffer)this.modelMatrix);
        GL11.glGetFloat((int)2983, (FloatBuffer)this.projectionMatrix);
        GL11.glGetInteger((int)2978, (IntBuffer)this.viewport);
        if (!GLU.gluProject((float)x, (float)y, (float)z, (FloatBuffer)this.modelMatrix, (FloatBuffer)this.projectionMatrix, (IntBuffer)this.viewport, (FloatBuffer)this.windowPosition)) return null;
        return new Vector3f(this.windowPosition.get(0) / (float)scaleFactor, ((float)ESP.mc.displayHeight - this.windowPosition.get(1)) / (float)scaleFactor, this.windowPosition.get(2));
    }

    public static void startScissorBox(ScaledResolution sr, int x, int y, int width, int height) {
        int sf = sr.getScaleFactor();
        GL11.glEnable((int)3089);
        GL11.glScissor((int)(x * sf), (int)((sr.getScaledHeight() - (y + height)) * sf), (int)(width * sf), (int)(height * sf));
    }

    public static void endScissorBox() {
        GL11.glDisable((int)3089);
    }

    public static void enableAlpha() {
        GL11.glEnable((int)3042);
        GL14.glBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
    }

    public static void disableAlpha() {
        GL11.glDisable((int)3042);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }
}
