package me.tojatta.api.utilities.angle;

import me.tojatta.api.utilities.vector.impl.Vector2;

/**
 * Created by Tojatta on 12/17/2016.
 */
public class Angle extends Vector2<Float> {

    public Angle(Float x, Float y) {
        super(x, y);
    }

    public Angle setYaw(Float yaw) {
        this.setX(yaw);
        return this;
    }

    public Angle setPitch(Float pitch) {
        this.setY(pitch);
        return this;
    }

    public Float getYaw() {
        return this.getX().floatValue();
    }

    public Float getPitch() {
        return this.getY().floatValue();
    }

    public Angle constrantAngle() {

        this.setYaw(this.getYaw() % 360F);
        this.setPitch(this.getPitch() % 360F);

        while (this.getYaw() <= -180F) {
            this.setYaw(this.getYaw() + 360F);
        }

        while (this.getPitch() <= -180F) {
            this.setPitch(this.getPitch() + 360F);
        }

        while (this.getYaw() > 180F) {
            this.setYaw(this.getYaw() - 360F);
        }

        while (this.getPitch() > 180F) {
            this.setPitch(this.getPitch() - 360F);
        }

        return this;
    }
}
