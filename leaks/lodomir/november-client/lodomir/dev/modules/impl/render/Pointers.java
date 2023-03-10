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
import java.util.HashMap;
import java.util.Map;
import lodomir.dev.event.impl.Event2D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.render.HUD;
import lodomir.dev.settings.NumberSetting;
import lodomir.dev.utils.render.RenderUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class Pointers
extends Module {
    private final Map<EntityPlayer, float[]> entityPosMap = new HashMap<EntityPlayer, float[]>();
    public NumberSetting radius = new NumberSetting("Radius", 10.0, 100.0, 30.0, 1.0);
    public NumberSetting size = new NumberSetting("Size", 3.0, 30.0, 6.0, 1.0);

    public Pointers() {
        super("Pointers", 0, Category.RENDER);
        this.addSettings(this.radius, this.size);
    }

    @Subscribe
    public void on2D(Event2D event) {
        ScaledResolution sr = new ScaledResolution(mc);
        float middleX = (float)sr.getScaledWidth() / 2.0f;
        float middleY = (float)sr.getScaledHeight() / 2.0f;
        RenderUtils.startBlending();
        for (EntityPlayer player : this.entityPosMap.keySet()) {
            if (!(player instanceof EntityOtherPlayerMP)) continue;
            GL11.glPushMatrix();
            float arrowSize = (float)this.size.getValue();
            float alpha = Math.max(1.0f - Pointers.mc.thePlayer.getDistanceToEntity(player) / 30.0f, 0.3f);
            int color = new Color(HUD.red.getValueInt(), HUD.green.getValueInt(), HUD.blue.getValueInt()).getRGB();
            GL11.glTranslatef((float)(middleX + 0.5f), (float)middleY, (float)1.0f);
            GL11.glTranslatef((float)0.0f, (float)((float)(-this.radius.getValue() - this.size.getValue())), (float)0.0f);
            GL11.glDisable((int)3553);
            GL11.glBegin((int)5);
            GL11.glColor4ub((byte)((byte)(color >> 16 & 0xFF)), (byte)((byte)(color >> 8 & 0xFF)), (byte)((byte)(color & 0xFF)), (byte)((byte)(alpha * 255.0f)));
            GL11.glVertex2f((float)0.0f, (float)0.0f);
            GL11.glVertex2f((float)(-arrowSize), (float)arrowSize);
            GL11.glVertex2f((float)arrowSize, (float)arrowSize);
            GL11.glEnd();
            GL11.glEnable((int)3553);
            GL11.glPopMatrix();
        }
    }
}

