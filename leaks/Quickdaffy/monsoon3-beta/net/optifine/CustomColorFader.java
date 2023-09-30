/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import net.minecraft.src.Config;
import net.minecraft.util.Vec3;

public class CustomColorFader {
    private Vec3 color = null;
    private long timeUpdate = System.currentTimeMillis();

    public Vec3 getColor(double x, double y, double z) {
        if (this.color == null) {
            this.color = new Vec3(x, y, z);
            return this.color;
        }
        long i = System.currentTimeMillis();
        long j = i - this.timeUpdate;
        if (j == 0L) {
            return this.color;
        }
        this.timeUpdate = i;
        if (Math.abs(x - this.color.x) < 0.004 && Math.abs(y - this.color.y) < 0.004 && Math.abs(z - this.color.z) < 0.004) {
            return this.color;
        }
        double d0 = (double)j * 0.001;
        d0 = Config.limit(d0, 0.0, 1.0);
        double d1 = x - this.color.x;
        double d2 = y - this.color.y;
        double d3 = z - this.color.z;
        double d4 = this.color.x + d1 * d0;
        double d5 = this.color.y + d2 * d0;
        double d6 = this.color.z + d3 * d0;
        this.color = new Vec3(d4, d5, d6);
        return this.color;
    }
}

