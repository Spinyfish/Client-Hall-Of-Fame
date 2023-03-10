/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_8to1_9.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class PlayerPosition
extends StoredObject {
    private double posX;
    private double posY;
    private double posZ;
    private float yaw;
    private float pitch;
    private boolean onGround;
    private int confirmId = -1;

    public PlayerPosition(UserConnection user) {
        super(user);
    }

    public void setPos(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw % 360.0f;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch % 360.0f;
    }

    public double getPosX() {
        return this.posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public double getPosZ() {
        return this.posZ;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public int getConfirmId() {
        return this.confirmId;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setConfirmId(int confirmId) {
        this.confirmId = confirmId;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerPosition)) {
            return false;
        }
        PlayerPosition other = (PlayerPosition)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Double.compare(this.getPosX(), other.getPosX()) != 0) {
            return false;
        }
        if (Double.compare(this.getPosY(), other.getPosY()) != 0) {
            return false;
        }
        if (Double.compare(this.getPosZ(), other.getPosZ()) != 0) {
            return false;
        }
        if (Float.compare(this.getYaw(), other.getYaw()) != 0) {
            return false;
        }
        if (Float.compare(this.getPitch(), other.getPitch()) != 0) {
            return false;
        }
        if (this.isOnGround() != other.isOnGround()) {
            return false;
        }
        return this.getConfirmId() == other.getConfirmId();
    }

    protected boolean canEqual(Object other) {
        return other instanceof PlayerPosition;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $posX = Double.doubleToLongBits(this.getPosX());
        result = result * 59 + (int)($posX >>> 32 ^ $posX);
        long $posY = Double.doubleToLongBits(this.getPosY());
        result = result * 59 + (int)($posY >>> 32 ^ $posY);
        long $posZ = Double.doubleToLongBits(this.getPosZ());
        result = result * 59 + (int)($posZ >>> 32 ^ $posZ);
        result = result * 59 + Float.floatToIntBits(this.getYaw());
        result = result * 59 + Float.floatToIntBits(this.getPitch());
        result = result * 59 + (this.isOnGround() ? 79 : 97);
        result = result * 59 + this.getConfirmId();
        return result;
    }

    public String toString() {
        return "PlayerPosition(posX=" + this.getPosX() + ", posY=" + this.getPosY() + ", posZ=" + this.getPosZ() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ", onGround=" + this.isOnGround() + ", confirmId=" + this.getConfirmId() + ")";
    }
}

