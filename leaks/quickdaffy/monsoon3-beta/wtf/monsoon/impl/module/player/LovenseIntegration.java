/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.impl.module.player;

import java.awt.Color;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.api.util.render.StencilUtil;

public class LovenseIntegration
extends HUDModule {
    ArrayList<Float> speeds = new ArrayList();
    Timer updateTimer = new Timer();

    public LovenseIntegration() {
        super("Lovense Integration", "trpedyjbjrcdaopntkmnqfjrcwizs \ud83d\udc49 \ud83d\udc48", 4.0f, 208.0f);
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
        float vibration = Wrapper.getSexToyManager().getVibrationIncrement();
        if (this.updateTimer.hasTimeElapsed(50L, true)) {
            this.speeds.add(Float.valueOf(vibration));
        }
        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
        RoundedUtils.round(this.getX() + 2.0f, this.getY() + 2.0f, this.getWidth() - 4.0f, this.getHeight() - 4.0f, 7.0f, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.85f));
        StencilUtil.bindReadStencilBuffer(0);
        RoundedUtils.gradient(this, 10.0f, 1.0f, ColorUtil.fadeBetween(10, 270, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 180, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 90, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)));
        StencilUtil.uninitStencilBuffer();
        RoundedUtils.round(this.getX() + 1.5f, this.getY() + 1.5f, this.getWidth() - 3.0f, this.getHeight() - 3.0f, 7.5f, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.2f));
        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
        RoundedUtils.glRound(this.getX() + 4.0f, this.getY() + 4.0f, this.getWidth() - 8.0f, this.getHeight() - 8.0f, 6.0f, -1);
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
        for (float s : this.speeds) {
            Color c = ColorUtil.fadeBetween(10, (int)(i * 2.0f) * 4, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255));
            float red = (float)c.getRed() / 255.0f;
            float green = (float)c.getGreen() / 255.0f;
            float blue = (float)c.getBlue() / 255.0f;
            float alpha = (float)c.getAlpha() / 255.0f;
            GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
            float g = s > 35.0f ? 2.0f : 1.0f;
            GL11.glVertex2f((float)(this.getX() + 4.0f + i * 2.0f), (float)(this.getY() + this.getHeight() - 7.0f - s / g));
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
        Wrapper.getFont().drawString("Vibration Strength: " + vibration, this.getX() + this.getWidth() - 6.0f - (float)Wrapper.getFont().getStringWidth("Vibration Strength: " + vibration), this.getY() + 4.0f, Color.WHITE, false);
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

