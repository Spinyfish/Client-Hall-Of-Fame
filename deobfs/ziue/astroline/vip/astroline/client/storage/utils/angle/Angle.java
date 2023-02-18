/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.storage.utils.vector.impl.Vector2
 */
package vip.astroline.client.storage.utils.angle;

import vip.astroline.client.storage.utils.vector.impl.Vector2;

public class Angle
extends Vector2<Float> {
    public Angle(Float x, Float y) {
        super((Number)x, (Number)y);
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
        return Float.valueOf(this.getX().floatValue());
    }

    public Float getPitch() {
        return Float.valueOf(this.getY().floatValue());
    }

    public Angle constrantAngle() {
        this.setYaw(Float.valueOf(this.getYaw().floatValue() % 360.0f));
        this.setPitch(Float.valueOf(this.getPitch().floatValue() % 360.0f));
        while (this.getYaw().floatValue() <= -180.0f) {
            this.setYaw(Float.valueOf(this.getYaw().floatValue() + 360.0f));
        }
        while (this.getPitch().floatValue() <= -180.0f) {
            this.setPitch(Float.valueOf(this.getPitch().floatValue() + 360.0f));
        }
        while (true) {
            if (!(this.getYaw().floatValue() > 180.0f)) {
                while (this.getPitch().floatValue() > 180.0f) {
                    this.setPitch(Float.valueOf(this.getPitch().floatValue() - 360.0f));
                }
                return this;
            }
            this.setYaw(Float.valueOf(this.getYaw().floatValue() - 360.0f));
        }
    }
}
