/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.MathHelper
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.service.event.impl.render.Event2D
 *  vip.astroline.client.service.event.impl.render.EventShader
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.service.module.value.FloatValue
 *  vip.astroline.client.storage.utils.angle.RotationUtil
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.service.module.impl.render;

import java.awt.Color;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.service.event.impl.render.Event2D;
import vip.astroline.client.service.event.impl.render.EventShader;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.service.module.value.FloatValue;
import vip.astroline.client.storage.utils.angle.RotationUtil;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class Radar
extends Module {
    public FloatValue xPos = new FloatValue("Radar", "X", 2.0f, 1.0f, 1000.0f, 1.0f);
    public FloatValue yPos = new FloatValue("Radar", "Y", 19.0f, 1.0f, 1000.0f, 1.0f);
    public FloatValue size = new FloatValue("Radar", "Size", 80.0f, 25.0f, 200.0f, 1.0f);

    public Radar() {
        super("Radar", Category.Render, 0, false);
    }

    @EventTarget
    public void onRender(Event2D event) {
        GL11.glPushMatrix();
        int x = this.xPos.getValue().intValue();
        int y = this.yPos.getValue().intValue();
        int width = this.size.getValue().intValue();
        int height = this.size.getValue().intValue();
        float cx = (float)x + (float)width / 2.0f;
        float cy = (float)y + (float)height / 2.0f;
        RenderUtil.drawRoundedRect((float)x, (float)y, (float)(x + width), (float)(y + height), (float)0.8f, (int)new Color(0, 0, 0, 40).getRGB());
        RenderUtil.drawRectSized((float)((float)x + (float)width / 2.0f), (float)y, (float)1.0f, (float)height, (int)Hud.hudColor1.getColorInt());
        RenderUtil.drawRectSized((float)x, (float)((float)y + (float)height / 2.0f), (float)width, (float)1.0f, (int)Hud.hudColor1.getColorInt());
        int maxDist = this.size.getValue().intValue() / 2;
        Iterator iterator = Radar.mc.theWorld.loadedEntityList.iterator();
        while (true) {
            double zd;
            double xd;
            if (!iterator.hasNext()) {
                GL11.glPopMatrix();
                return;
            }
            Entity entity = (Entity)iterator.next();
            if (!this.checkEntity(entity) || !((xd = RenderUtil.lerp((double)entity.prevPosX, (double)entity.posX, (double)event.getTicks()) - RenderUtil.lerp((double)Radar.mc.thePlayer.prevPosX, (double)Radar.mc.thePlayer.posX, (double)event.getTicks())) * xd + (zd = RenderUtil.lerp((double)entity.prevPosZ, (double)entity.posZ, (double)event.getTicks()) - RenderUtil.lerp((double)Radar.mc.thePlayer.prevPosZ, (double)Radar.mc.thePlayer.posZ, (double)event.getTicks())) * zd <= (double)(maxDist * maxDist))) continue;
            float dist = MathHelper.sqrt_double((double)(xd * xd + zd * zd));
            double[] vector = this.getLookVector((float)((double)RotationUtil.getRotationsRadar((Entity)entity)[0] - RenderUtil.lerp((double)Radar.mc.thePlayer.prevRotationYawHead, (double)Radar.mc.thePlayer.rotationYawHead, (double)event.getTicks())));
            if (entity instanceof EntityPlayer) {
                RenderUtil.drawRectSized((float)(cx - 1.0f - (float)vector[0] * dist), (float)(cy - 1.0f - (float)vector[1] * dist), (float)2.0f, (float)2.0f, (int)Hud.hudColor1.getColorInt());
                continue;
            }
            if (!(entity instanceof EntityMob)) continue;
            RenderUtil.drawRectSized((float)(cx - 1.0f - (float)vector[0] * dist), (float)(cy - 1.0f - (float)vector[1] * dist), (float)2.0f, (float)2.0f, (int)Hud.hudColor1.getColorInt());
        }
    }

    @EventTarget
    public void onShader(EventShader event) {
        int x = this.xPos.getValue().intValue();
        int y = this.yPos.getValue().intValue();
        int width = this.size.getValue().intValue();
        int height = this.size.getValue().intValue();
        RenderUtil.drawRoundedRect((float)(x + 1), (float)(y + 1), (float)(x + width - 1), (float)(y + height - 1), (float)0.8f, (int)-1);
    }

    public double[] getLookVector(float yaw) {
        return new double[]{-MathHelper.sin((float)(yaw *= (float)Math.PI / 180)), MathHelper.cos((float)yaw)};
    }

    private boolean checkEntity(Entity entity) {
        return entity instanceof EntityPlayer || entity instanceof EntityMob;
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }
}
