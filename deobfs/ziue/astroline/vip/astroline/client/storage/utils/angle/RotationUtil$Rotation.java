/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.storage.utils.angle;

public static class RotationUtil.Rotation {
    float yaw;
    float pitch;

    public RotationUtil.Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
