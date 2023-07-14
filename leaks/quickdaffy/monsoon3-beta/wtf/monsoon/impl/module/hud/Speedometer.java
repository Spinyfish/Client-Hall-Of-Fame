/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.impl.module.hud;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.api.util.render.StencilUtil;

public class Speedometer
extends HUDModule {
    public static Setting<Float> delay = new Setting<Float>("Delay", Float.valueOf(50.0f)).minimum(Float.valueOf(0.0f)).maximum(Float.valueOf(100.0f)).incrementation(Float.valueOf(1.0f)).describedBy("The delay between adding points to the graph");
    private final ArrayList<Float> speeds = new ArrayList();
    private final Timer updateTimer = new Timer();

    public Speedometer() {
        super("Speedometer", "Displays your BPS on a graph.", 4.0f, 88.0f);
    }

    @Override
    public void render() {
        if ((float)this.speeds.size() < this.getWidth() - 6.0f) {
            int i = 0;
            while ((float)i < this.getWidth() - 6.0f) {
                this.speeds.add(Float.valueOf(0.0f));
                ++i;
            }
        }
        if ((float)this.speeds.size() > this.getWidth() - 6.0f) {
            this.speeds.remove(0);
        }
        float speed = (float)this.mc.thePlayer.getDistance(this.mc.thePlayer.lastTickPosX, this.mc.thePlayer.posY, this.mc.thePlayer.lastTickPosZ) * (Minecraft.getMinecraft().getTimer().ticksPerSecond * Minecraft.getMinecraft().getTimer().timerSpeed);
        String bps = new DecimalFormat("#.##").format(speed);
        if (this.updateTimer.hasTimeElapsed(delay.getValue().floatValue(), true)) {
            this.speeds.add(Float.valueOf(speed));
        }
        RenderUtil.getDefaultHudRenderer(this);
        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
        RoundedUtils.round(this.getX() + 3.0f, this.getY() + 4.0f, this.getWidth() - 6.0f, this.getHeight() - 8.0f, 10.0f, Color.WHITE);
        StencilUtil.bindReadStencilBuffer(1);
        GL11.glPushMatrix();
        GL11.glShadeModel((int)7425);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)1.5f);
        GL11.glBegin((int)2);
        float i = 0.0f;
        GL11.glVertex2f((float)(this.getX() - 6.0f), (float)(this.getY() + this.getHeight() + 6.0f));
        for (float speedPoint : this.speeds) {
            ColorUtil.glColor(ColorUtil.fadeBetween(10, (int)(i * 2.0f) * 4, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]).getRGB());
            float g = speedPoint > 35.0f ? 2.0f : 1.0f;
            GL11.glVertex2f((float)(this.getX() + 4.0f + i * 2.0f), (float)(this.getY() + this.getHeight() - 7.0f - speedPoint / g));
            i += 0.5f;
        }
        GL11.glVertex2f((float)(this.getX() + this.getWidth() + 6.0f), (float)(this.getY() + this.getHeight() + 6.0f));
        GL11.glEnd();
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glShadeModel((int)7424);
        GL11.glLineWidth((float)2.0f);
        GL11.glPopMatrix();
        StencilUtil.uninitStencilBuffer();
        Wrapper.getFont().drawString("Speed:", this.getX() + 6.0f, this.getY() + 4.0f, Color.WHITE, false);
        Wrapper.getFont().drawString(bps + " b/s", this.getX() + this.getWidth() - 6.0f - (float)Wrapper.getFont().getStringWidth(bps + " b/s"), this.getY() + 4.0f, Color.WHITE, false);
    }

    @Override
    public void blur() {
        RoundedUtils.glRound(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 10.0f, Wrapper.getPallet().getBackground().getRGB());
    }

    @Override
    public float getWidth() {
        return 100.0f;
    }

    @Override
    public float getHeight() {
        return 50.0f;
    }
}

