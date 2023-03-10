/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.GLU
 *  org.lwjgl.util.vector.Vector3f
 *  org.lwjgl.util.vector.Vector4f
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lodomir.dev.event.impl.render.EventRender2D;
import lodomir.dev.event.impl.render.EventRender3D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.combat.AntiBot;
import lodomir.dev.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class ESP
extends Module {
    public final Map<Entity, Vector4f> entityPosition = new HashMap<Entity, Vector4f>();
    public static final FloatBuffer windPos = BufferUtils.createFloatBuffer((int)4);
    public static final IntBuffer intBuffer = GLAllocation.createDirectIntBuffer(16);
    public static final FloatBuffer floatBuffer1 = GLAllocation.createDirectFloatBuffer(16);
    public static final FloatBuffer floatBuffer2 = GLAllocation.createDirectFloatBuffer(16);

    public ESP() {
        super("ESP", 0, Category.RENDER);
    }

    @Override
    @Subscribe
    public void on3D(EventRender3D event) {
        this.entityPosition.clear();
        for (Entity entity : ESP.mc.theWorld.loadedEntityList) {
            if (!entity.isEntityAlive() || entity == ESP.mc.thePlayer || AntiBot.bot.contains(entity) || entity.isInvisible()) continue;
            double x = this.interp(entity.posX, entity.lastTickPosX) - Minecraft.getMinecraft().getRenderManager().renderPosX;
            double y = this.interp(entity.posY, entity.lastTickPosY) - Minecraft.getMinecraft().getRenderManager().renderPosY;
            double z = this.interp(entity.posZ, entity.lastTickPosZ) - Minecraft.getMinecraft().getRenderManager().renderPosZ;
            this.entityPosition.put(entity, this.getEntityPositionsOn2D(entity));
        }
    }

    @Override
    @Subscribe
    public void on2D(EventRender2D event) {
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        for (Entity entity : this.entityPosition.keySet()) {
            Vector4f pos = this.entityPosition.get(entity);
            float x = pos.getX();
            float y = pos.getY();
            float right = pos.getZ();
            float bottom = pos.getW();
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase renderingEntity = (EntityLivingBase)entity;
                float healthValue = renderingEntity.getHealth() / renderingEntity.getMaxHealth();
                Color healthColor = (double)healthValue > 0.75 ? new Color(66, 246, 123) : ((double)healthValue > 0.5 ? new Color(228, 255, 105) : ((double)healthValue > 0.35 ? new Color(236, 100, 64) : new Color(255, 65, 68)));
                float height = bottom - y + 1.0f;
                Gui.drawRect(right + 2.5f, y - 0.5f, 2.0, height + 1.0f, new Color(0, 0, 0, 180).getRGB());
                Gui.drawRect(right + 3.0f, y + (height - height * healthValue), 1.0, height * healthValue, healthColor.getRGB());
            }
            float outlineThickness = 0.5f;
            Gui.drawRect(x, y, right - x, 1.0, -1);
            Gui.drawRect(x, y, 1.0, bottom - y, -1);
            Gui.drawRect(x, bottom, right - x, 1.0, -1);
            Gui.drawRect(right, y, 1.0, bottom - y + 1.0f, -1);
            Gui.drawRect(x - 0.5f, y - outlineThickness, right - x + 2.0f, outlineThickness, Color.BLACK.getRGB());
            Gui.drawRect(x - outlineThickness, y, outlineThickness, bottom - y + 1.0f, Color.BLACK.getRGB());
            Gui.drawRect(x - 0.5f, bottom + 1.0f, right - x + 2.0f, outlineThickness, Color.BLACK.getRGB());
            Gui.drawRect(right + 1.0f, y, outlineThickness, bottom - y + 1.0f, Color.BLACK.getRGB());
            Gui.drawRect(x + 1.0f, y + 1.0f, right - x - 1.0f, outlineThickness, Color.BLACK.getRGB());
            Gui.drawRect(x + 1.0f, y + 1.0f, outlineThickness, bottom - y - 1.0f, Color.BLACK.getRGB());
            Gui.drawRect(x + 1.0f, bottom - outlineThickness, right - x - 1.0f, outlineThickness, Color.BLACK.getRGB());
            Gui.drawRect(right - outlineThickness, y + 1.0f, outlineThickness, bottom - y - 1.0f, Color.BLACK.getRGB());
        }
    }

    private double interp(double newPos, double oldPos) {
        return oldPos + (newPos - oldPos) * (double)Minecraft.getMinecraft().timer.renderPartialTicks;
    }

    private double[] getInterpolatedPos(Entity entity) {
        float ticks = ESP.mc.timer.renderPartialTicks;
        double[] dArray = new double[3];
        double d = RenderUtils.interpolate(entity.lastTickPosX, entity.posX, ticks);
        mc.getRenderManager();
        dArray[0] = d - RenderManager.viewerPosX;
        double d2 = RenderUtils.interpolate(entity.lastTickPosY, entity.posY, ticks);
        mc.getRenderManager();
        dArray[1] = d2 - RenderManager.viewerPosY;
        double d3 = RenderUtils.interpolate(entity.lastTickPosZ, entity.posZ, ticks);
        mc.getRenderManager();
        dArray[2] = d3 - RenderManager.viewerPosZ;
        return dArray;
    }

    public static Vector3f projectOn2D(float x, float y, float z, int scaleFactor) {
        GL11.glGetFloat((int)2982, (FloatBuffer)floatBuffer1);
        GL11.glGetFloat((int)2983, (FloatBuffer)floatBuffer2);
        GL11.glGetInteger((int)2978, (IntBuffer)intBuffer);
        if (GLU.gluProject((float)x, (float)y, (float)z, (FloatBuffer)floatBuffer1, (FloatBuffer)floatBuffer2, (IntBuffer)intBuffer, (FloatBuffer)windPos)) {
            return new Vector3f(windPos.get(0) / (float)scaleFactor, ((float)ESP.mc.displayHeight - windPos.get(1)) / (float)scaleFactor, windPos.get(2));
        }
        return null;
    }

    private Vector4f getEntityPositionsOn2D(Entity entity) {
        double[] renderingEntityPos = this.getInterpolatedPos(entity);
        double entityRenderWidth = (double)entity.width / 1.5;
        AxisAlignedBB bb = new AxisAlignedBB(renderingEntityPos[0] - entityRenderWidth, renderingEntityPos[1], renderingEntityPos[2] - entityRenderWidth, renderingEntityPos[0] + entityRenderWidth, renderingEntityPos[1] + (double)entity.height + (entity.isSneaking() ? -0.3 : 0.18), renderingEntityPos[2] + entityRenderWidth).expand(0.15, 0.15, 0.15);
        List<Vector3f> vectors = Arrays.asList(new Vector3f((float)bb.minX, (float)bb.minY, (float)bb.minZ), new Vector3f((float)bb.minX, (float)bb.maxY, (float)bb.minZ), new Vector3f((float)bb.maxX, (float)bb.minY, (float)bb.minZ), new Vector3f((float)bb.maxX, (float)bb.maxY, (float)bb.minZ), new Vector3f((float)bb.minX, (float)bb.minY, (float)bb.maxZ), new Vector3f((float)bb.minX, (float)bb.maxY, (float)bb.maxZ), new Vector3f((float)bb.maxX, (float)bb.minY, (float)bb.maxZ), new Vector3f((float)bb.maxX, (float)bb.maxY, (float)bb.maxZ));
        Vector4f entityPos = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0f, -1.0f);
        ScaledResolution sr = new ScaledResolution(mc);
        for (Vector3f vector3f : vectors) {
            vector3f = ESP.projectOn2D(vector3f.x, vector3f.y, vector3f.z, sr.getScaleFactor());
            if (vector3f == null || !((double)vector3f.z >= 0.0) || !((double)vector3f.z < 1.0)) continue;
            entityPos.x = Math.min(vector3f.x, entityPos.x);
            entityPos.y = Math.min(vector3f.y, entityPos.y);
            entityPos.z = Math.max(vector3f.x, entityPos.z);
            entityPos.w = Math.max(vector3f.y, entityPos.w);
        }
        return entityPos;
    }
}

