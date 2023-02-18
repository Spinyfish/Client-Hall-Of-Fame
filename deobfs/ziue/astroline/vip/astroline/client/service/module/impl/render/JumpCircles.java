/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.Vec3
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.service.event.impl.move.EventEntityOptionalForce
 *  vip.astroline.client.service.event.impl.move.EventUpdate
 *  vip.astroline.client.service.event.impl.render.Event3D
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.render.JumpCircles$Circle
 *  vip.astroline.client.service.module.value.ColorValue
 */
package vip.astroline.client.service.module.impl.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.service.event.impl.move.EventEntityOptionalForce;
import vip.astroline.client.service.event.impl.move.EventUpdate;
import vip.astroline.client.service.event.impl.render.Event3D;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.render.JumpCircles;
import vip.astroline.client.service.module.value.ColorValue;

public class JumpCircles
extends Module {
    public static final List<Circle> circles = new ArrayList<Circle>();
    private ColorValue color = new ColorValue("JumpCircles", "Color", new Color(0xFF4545));

    public JumpCircles() {
        super("JumpCircles", Category.Render, 0, false);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        Iterator iterator = JumpCircles.mc.thePlayer.getEntityWorld().playerEntities.iterator();
        while (iterator.hasNext()) {
            EntityPlayer player = (EntityPlayer)iterator.next();
            player.circles.removeIf(Circle::update);
        }
    }

    @EventTarget
    public void onEvent(Event3D event) {
        JumpCircles.mc.entityRenderer.setupCameraTransform(JumpCircles.mc.timer.renderPartialTicks, 2);
        Iterator iterator = JumpCircles.mc.thePlayer.getEntityWorld().playerEntities.iterator();
        while (iterator.hasNext()) {
            EntityPlayer player = (EntityPlayer)iterator.next();
            GL11.glPushMatrix();
            GL11.glEnable((int)3042);
            GL11.glDisable((int)3008);
            GL11.glDisable((int)2884);
            GL11.glDisable((int)3553);
            GL11.glShadeModel((int)7425);
            for (Circle circle : player.circles) {
                GL11.glBegin((int)8);
                for (int i = 0; i <= 360; i += 5) {
                    float red = (float)this.color.getRed() / 255.0f;
                    float green = (float)this.color.getGreen() / 255.0f;
                    float blue = (float)this.color.getBlue() / 255.0f;
                    Vec3 pos = circle.pos();
                    double x = Math.cos(Math.toRadians(i)) * JumpCircles.createAnimation(1.0 - circle.getAnimation(JumpCircles.mc.timer.renderPartialTicks)) * 0.6;
                    double z = Math.sin(Math.toRadians(i)) * JumpCircles.createAnimation(1.0 - circle.getAnimation(JumpCircles.mc.timer.renderPartialTicks)) * 0.6;
                    GL11.glColor4d((double)red, (double)green, (double)blue, (double)(1.0 * circle.getAnimation(JumpCircles.mc.timer.renderPartialTicks)));
                    GL11.glVertex3d((double)(pos.xCoord + x), (double)(pos.yCoord + (double)0.2f), (double)(pos.zCoord + z));
                    GL11.glColor4d((double)red, (double)green, (double)blue, (double)(0.2 * circle.getAnimation(JumpCircles.mc.timer.renderPartialTicks)));
                    GL11.glVertex3d((double)(pos.xCoord + x * 1.4), (double)(pos.yCoord + (double)0.2f), (double)(pos.zCoord + z * 1.4));
                }
                GL11.glEnd();
            }
            GL11.glEnable((int)3553);
            GL11.glDisable((int)3042);
            GL11.glEnable((int)3008);
            GL11.glShadeModel((int)7424);
            GL11.glEnable((int)2884);
            GL11.glPopMatrix();
            GlStateManager.resetColor();
        }
    }

    @EventTarget
    public void onEventEntityOptionalForce(EventEntityOptionalForce event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer)event.getEntity();
        double motionY = event.getEntity().posY - event.getMinor().yCoord;
        if (!player.onGround && player.onGround != player.raycastGround && motionY > 0.0) {
            player.circles.add(new Circle(event.getMinor()));
        }
        player.raycastGround = player.onGround;
    }

    public static double createAnimation(double value) {
        return Math.sqrt(1.0 - Math.pow(value - 1.0, 2.0));
    }
}
