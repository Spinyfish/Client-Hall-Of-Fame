/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.impl.ui.particle;

import java.awt.Color;
import java.util.Random;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.ui.particle.ParticleSystem;

public class Particle {
    private float x;
    private float y;
    private float yaw;
    private final ParticleSystem system;

    public Particle(ParticleSystem system) {
        this.system = system;
        Random random = new Random();
        this.x = random.nextFloat() * (float)Minecraft.getMinecraft().displayWidth / 2.0f;
        this.y = random.nextFloat() * (float)Minecraft.getMinecraft().displayHeight / 2.0f;
        this.yaw = random.nextFloat() * 360.0f;
    }

    public void render() {
        float nextX = (float)((double)this.x + Math.sin(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.yaw)) * 1.0);
        float nextY = (float)((double)this.y + Math.cos(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.yaw)) * 1.0);
        RenderUtil.drawRect(nextX - 2.0f, nextY - 2.0f, 4.0, 4.0, Color.RED.getRGB());
        if (nextX <= 0.0f || nextX >= (float)Minecraft.getMinecraft().displayWidth / 2.0f || nextY <= 0.0f || nextY >= 200.0f) {
            this.yaw += 45.0f;
            if (nextY >= 200.0f) {
                this.yaw = 0.0f;
            }
            nextX = (float)((double)this.x + Math.sin(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.yaw)) * 1.0);
            nextY = (float)((double)this.y + Math.cos(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.yaw)) * 1.0);
        }
        if (this.yaw > 360.0f) {
            this.yaw -= 360.0f;
        }
        this.x = nextX;
        this.y = nextY;
        Particle nearest = this.system.getNearest(this);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3008);
        GL11.glShadeModel((int)7425);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)0.5f);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glBegin((int)3);
        GL11.glVertex2f((float)this.x, (float)this.y);
        GL11.glVertex2f((float)nearest.x, (float)nearest.y);
        GL11.glEnd();
        GL11.glDisable((int)2848);
        GL11.glShadeModel((int)7424);
        GL11.glEnable((int)3008);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getYaw() {
        return this.yaw;
    }
}

