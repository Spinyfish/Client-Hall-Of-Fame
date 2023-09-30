/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;

public class Vec3 {
    public final double x;
    public final double y;
    public final double z;

    public Vec3(double x, double y, double z) {
        if (x == -0.0) {
            x = 0.0;
        }
        if (y == -0.0) {
            y = 0.0;
        }
        if (z == -0.0) {
            z = 0.0;
        }
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(Vec3i p_i46377_1_) {
        this(p_i46377_1_.getX(), p_i46377_1_.getY(), p_i46377_1_.getZ());
    }

    public Vec3 subtractReverse(Vec3 vec) {
        return new Vec3(vec.x - this.x, vec.y - this.y, vec.z - this.z);
    }

    public Vec3 normalize() {
        double d0 = MathHelper.sqrt_double(this.x * this.x + this.y * this.y + this.z * this.z);
        return d0 < 1.0E-4 ? new Vec3(0.0, 0.0, 0.0) : new Vec3(this.x / d0, this.y / d0, this.z / d0);
    }

    public double dotProduct(Vec3 vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }

    public Vec3 crossProduct(Vec3 vec) {
        return new Vec3(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
    }

    public Vec3 subtract(Vec3 vec) {
        return this.subtract(vec.x, vec.y, vec.z);
    }

    public Vec3 subtract(double x, double y, double z) {
        return this.addVector(-x, -y, -z);
    }

    public Vec3 add(Vec3 vec) {
        return this.addVector(vec.x, vec.y, vec.z);
    }

    public Vec3 addVector(double x, double y, double z) {
        return new Vec3(this.x + x, this.y + y, this.z + z);
    }

    public double distanceTo(Vec3 vec) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;
        return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public double squareDistanceTo(Vec3 vec) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double lengthVector() {
        return MathHelper.sqrt_double(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Vec3 getIntermediateWithXValue(Vec3 vec, double x) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;
        if (d0 * d0 < (double)1.0E-7f) {
            return null;
        }
        double d3 = (x - this.x) / d0;
        return d3 >= 0.0 && d3 <= 1.0 ? new Vec3(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
    }

    public Vec3 getIntermediateWithYValue(Vec3 vec, double y) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;
        if (d1 * d1 < (double)1.0E-7f) {
            return null;
        }
        double d3 = (y - this.y) / d1;
        return d3 >= 0.0 && d3 <= 1.0 ? new Vec3(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
    }

    public Vec3 getIntermediateWithZValue(Vec3 vec, double z) {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;
        if (d2 * d2 < (double)1.0E-7f) {
            return null;
        }
        double d3 = (z - this.z) / d2;
        return d3 >= 0.0 && d3 <= 1.0 ? new Vec3(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public Vec3 rotatePitch(float pitch) {
        float f = MathHelper.cos(pitch);
        float f1 = MathHelper.sin(pitch);
        double d0 = this.x;
        double d1 = this.y * (double)f + this.z * (double)f1;
        double d2 = this.z * (double)f - this.y * (double)f1;
        return new Vec3(d0, d1, d2);
    }

    public Vec3 rotateYaw(float yaw) {
        float f = MathHelper.cos(yaw);
        float f1 = MathHelper.sin(yaw);
        double d0 = this.x * (double)f + this.z * (double)f1;
        double d1 = this.y;
        double d2 = this.z * (double)f - this.x * (double)f1;
        return new Vec3(d0, d1, d2);
    }
}

