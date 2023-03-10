/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 *  org.lwjgl.opengl.GL11
 */
package lodomir.dev.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import lodomir.dev.November;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.event.impl.render.EventRender3D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.combat.KillAura;
import lodomir.dev.modules.impl.render.Interface;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class TargetStrafe
extends Module {
    public static transient boolean direction = false;
    public final NumberSetting radius = new NumberSetting("Radius", 1.0, 6.0, 3.0, 0.1);

    public TargetStrafe() {
        super("TargetStrafe", 0, Category.COMBAT);
        this.addSettings(this.radius);
    }

    @Override
    @Subscribe
    public void on3D(EventRender3D event) {
        if (KillAura.target == null) {
            return;
        }
        if (November.INSTANCE.getModuleManager().getModule("KillAura").isEnabled()) {
            this.cicrle(KillAura.target, this.radius.getValueFloat(), Interface.getColor());
        }
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
    }

    private void cicrle(Entity entity, double rad, Color color) {
        GL11.glPushMatrix();
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glEnable((int)2832);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);
        GL11.glHint((int)3153, (int)4354);
        GL11.glDepthMask((boolean)false);
        GlStateManager.disableCull();
        GL11.glBegin((int)5);
        double d = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)TargetStrafe.mc.timer.renderPartialTicks;
        mc.getRenderManager();
        double x = d - RenderManager.viewerPosX;
        double d2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)TargetStrafe.mc.timer.renderPartialTicks;
        mc.getRenderManager();
        double y = d2 - RenderManager.viewerPosY + 0.01;
        double d3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)TargetStrafe.mc.timer.renderPartialTicks;
        mc.getRenderManager();
        double z = d3 - RenderManager.viewerPosZ;
        for (int i = 0; i <= 90; ++i) {
            RenderUtils.color(color);
            GL11.glVertex3d((double)(x + rad * Math.cos((double)i * (Math.PI * 2) / 45.0)), (double)y, (double)(z + rad * Math.sin((double)i * (Math.PI * 2) / 45.0)));
        }
        GL11.glEnd();
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2929);
        GlStateManager.enableCull();
        GL11.glDisable((int)2848);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)2832);
        GL11.glEnable((int)3553);
        GL11.glPopMatrix();
        GL11.glColor3f((float)255.0f, (float)255.0f, (float)255.0f);
    }
}

