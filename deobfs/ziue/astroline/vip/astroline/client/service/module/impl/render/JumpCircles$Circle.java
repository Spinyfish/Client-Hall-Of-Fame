/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Vec3
 *  vip.astroline.client.service.module.Module
 */
package vip.astroline.client.service.module.impl.render;

import net.minecraft.util.Vec3;
import vip.astroline.client.service.module.Module;

public static class JumpCircles.Circle {
    private Vec3 vector;
    private int tick;
    private int prevTick;

    public JumpCircles.Circle(Vec3 vector) {
        this.vector = vector;
        this.prevTick = this.tick = 20;
    }

    public double getAnimation(float pt) {
        return ((float)this.prevTick + (float)(this.tick - this.prevTick) * pt) / 20.0f;
    }

    public boolean update() {
        this.prevTick = this.tick;
        return this.tick-- <= 0;
    }

    public Vec3 pos() {
        return new Vec3(this.vector.xCoord - Module.mc.getRenderManager().renderPosX, this.vector.yCoord - Module.mc.getRenderManager().renderPosY, this.vector.zCoord - Module.mc.getRenderManager().renderPosZ);
    }
}
