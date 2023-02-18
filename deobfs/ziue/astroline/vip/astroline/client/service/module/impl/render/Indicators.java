/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.Vec3
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.service.event.impl.render.Event2D
 *  vip.astroline.client.service.event.impl.render.Event3D
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.service.module.impl.render.Indicators$EntityListener
 *  vip.astroline.client.service.module.value.BooleanValue
 *  vip.astroline.client.service.module.value.FloatValue
 *  vip.astroline.client.storage.utils.render.ColorUtils
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.service.module.impl.render;

import java.awt.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.service.event.impl.render.Event2D;
import vip.astroline.client.service.event.impl.render.Event3D;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.service.module.impl.render.Indicators;
import vip.astroline.client.service.module.value.BooleanValue;
import vip.astroline.client.service.module.value.FloatValue;
import vip.astroline.client.storage.utils.render.ColorUtils;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

/*
 * Exception performing whole class analysis ignored.
 */
public class Indicators
extends Module {
    public FloatValue size = new FloatValue("Indicators", "Size", 10.0f, 5.0f, 25.0f, 1.0f);
    public FloatValue radius = new FloatValue("Indicators", "Radius", 45.0f, 10.0f, 200.0f, 1.0f);
    public BooleanValue fade = new BooleanValue("Indicators", "Fade", Boolean.valueOf(true));
    private int alpha;
    private boolean plus_or_minus;
    private final EntityListener entityListener = new EntityListener();

    public Indicators() {
        super("Indicators", Category.Render, 0, false);
    }

    @EventTarget
    public void onRender3D(Event3D event) {
        EntityListener.access$000((EntityListener)this.entityListener, (Event3D)event);
    }

    @EventTarget
    public void onRender2D(Event2D event) {
        if (this.fade.getValue().booleanValue()) {
            float speed = 0.0025f;
            if ((float)this.alpha <= 60.0f || (float)this.alpha >= 255.0f) {
                this.plus_or_minus = !this.plus_or_minus;
            }
            this.alpha = this.plus_or_minus ? (int)((float)this.alpha + speed) : (int)((float)this.alpha - speed);
            this.alpha = (int)Indicators.clamp(this.alpha, 60.0, 255.0);
        } else {
            this.alpha = 255;
        }
        Indicators.mc.theWorld.loadedEntityList.forEach(o -> {
            if (!(o instanceof EntityPlayer)) return;
            EntityPlayer entity = (EntityPlayer)o;
            Vec3 pos = (Vec3)this.entityListener.getEntityLowerBounds().get(entity);
            if (pos == null) return;
            if (this.isOnScreen(pos)) return;
            int x = Display.getWidth() / 2 / (Indicators.mc.gameSettings.guiScale == 0 ? 1 : Indicators.mc.gameSettings.guiScale);
            int y = Display.getHeight() / 2 / (Indicators.mc.gameSettings.guiScale == 0 ? 1 : Indicators.mc.gameSettings.guiScale);
            float yaw = this.getRotations((EntityLivingBase)entity) - Indicators.mc.thePlayer.rotationYaw;
            GL11.glTranslatef((float)x, (float)y, (float)0.0f);
            GL11.glRotatef((float)yaw, (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glTranslatef((float)(-x), (float)(-y), (float)0.0f);
            int color = this.fade.getValue() != false ? ColorUtils.fadeBetween((int)Hud.hudColor1.getColorInt(), (int)Hud.hudColor2.getColorInt()) : Hud.hudColor1.getColorInt();
            RenderUtil.drawTracerPointer((float)x, (float)((float)y - this.radius.getValue().floatValue()), (float)this.size.getValue().floatValue(), (float)2.0f, (float)1.0f, (int)color);
            GL11.glTranslatef((float)x, (float)y, (float)0.0f);
            GL11.glRotatef((float)(-yaw), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glTranslatef((float)(-x), (float)(-y), (float)0.0f);
        });
    }

    public void onEnable() {
        this.alpha = 0;
        this.plus_or_minus = false;
        super.onEnable();
    }

    private boolean isOnScreen(Vec3 pos) {
        int n3;
        int n2;
        int n;
        if (!(pos.xCoord > -1.0)) {
            return false;
        }
        if (!(pos.zCoord < 1.0)) {
            return false;
        }
        double d = pos.xCoord;
        int n4 = n = Indicators.mc.gameSettings.guiScale == 0 ? 1 : Indicators.mc.gameSettings.guiScale;
        if (!(d / (double)n >= 0.0)) {
            return false;
        }
        double d2 = pos.xCoord;
        int n5 = n2 = Indicators.mc.gameSettings.guiScale == 0 ? 1 : Indicators.mc.gameSettings.guiScale;
        if (!(d2 / (double)n2 <= (double)Display.getWidth())) {
            return false;
        }
        double d3 = pos.yCoord;
        int n6 = n3 = Indicators.mc.gameSettings.guiScale == 0 ? 1 : Indicators.mc.gameSettings.guiScale;
        if (!(d3 / (double)n3 >= 0.0)) {
            return false;
        }
        double d4 = pos.yCoord;
        int n42 = Indicators.mc.gameSettings.guiScale == 0 ? 1 : Indicators.mc.gameSettings.guiScale;
        if (d4 / (double)n42 <= (double)Display.getHeight()) return true;
        return false;
    }

    private float getRotations(EntityLivingBase ent) {
        double x = ent.posX - Indicators.mc.thePlayer.posX;
        double z = ent.posZ - Indicators.mc.thePlayer.posZ;
        float yaw = (float)(-(Math.atan2(x, z) * 57.29577951308232));
        return yaw;
    }

    private Color getColor(EntityLivingBase player, int alpha) {
        float f = Indicators.mc.thePlayer.getDistanceToEntity((Entity)player);
        float f1 = 40.0f;
        float f2 = Math.max(0.0f, Math.min(f, f1) / f1);
        Color clr = new Color(Color.HSBtoRGB(f2 / 3.0f, 1.0f, 1.0f) | 0xFF000000);
        return new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), alpha);
    }

    public static double clamp(double value, double minimum, double maximum) {
        return value > maximum ? maximum : Math.max(value, minimum);
    }
}
