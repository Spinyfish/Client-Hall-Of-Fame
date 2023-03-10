/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 *  org.lwjgl.opengl.GL11
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import lodomir.dev.event.impl.render.EventRender2D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.render.Interface;
import lodomir.dev.utils.render.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class Radar
extends Module {
    public Radar() {
        super("Radar", 0, Category.RENDER);
    }

    @Subscribe
    public void onEvent(EventRender2D e) {
        double center = 50.0;
        double toAddX = 3.0;
        double toAddZ = 15.0;
        RenderUtils.drawRect(3.0, 14.0, 103.0, 15.0, Interface.getColorInt());
        RenderUtils.prepareScissorBox(3.0f, 15.0f, 103.0f, 115.0f);
        GL11.glEnable((int)3089);
        RenderUtils.drawRect(3.0, 15.0, 103.0, 115.0, -1879048192);
        for (Entity ent : Radar.mc.theWorld.getLoadedEntityList()) {
            if (ent == Radar.mc.thePlayer) continue;
            int color = 0;
            if (ent instanceof EntityPlayer) {
                color = new Color(255, 255, 255, 128).getRGB();
            }
            double drawX = 50.0 + (double)(Math.round(Radar.mc.thePlayer.posX) - Math.round(ent.posX));
            double drawZ = 50.0 + (double)(Math.round(Radar.mc.thePlayer.posZ) - Math.round(ent.posZ));
            RenderUtils.drawRect(drawX + 3.0, drawZ + 15.0, drawX + 1.0 + 3.0, drawZ + 1.0 + 15.0, color);
        }
        GL11.glDisable((int)3089);
        RenderUtils.drawRect(52.0, 15.0, 53.0, 115.0, new Color(0x56FFFFFF, true).getRGB());
        RenderUtils.drawRect(3.0, 64.0, 103.0, 65.0, new Color(0x56FFFFFF, true).getRGB());
    }
}

