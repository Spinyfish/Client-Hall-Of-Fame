package dev.africa.pandaware.utils.math.vector;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vec3d {
    private double x, y, z;

    public Vec3d() {
        this(0, 0, 0);
    }

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d copy() {
        return new Vec3d(this.x, this.y, this.z);
    }
}