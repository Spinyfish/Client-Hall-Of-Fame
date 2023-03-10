/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class PlayerAbilities
extends StoredObject {
    private boolean sprinting;
    private boolean allowFly;
    private boolean flying;
    private boolean invincible;
    private boolean creative;
    private float flySpeed;
    private float walkSpeed;

    public PlayerAbilities(UserConnection user) {
        super(user);
    }

    public byte getFlags() {
        byte flags = 0;
        if (this.invincible) {
            flags = (byte)(flags | 8);
        }
        if (this.allowFly) {
            flags = (byte)(flags | 4);
        }
        if (this.flying) {
            flags = (byte)(flags | 2);
        }
        if (this.creative) {
            flags = (byte)(flags | 1);
        }
        return flags;
    }

    public boolean isSprinting() {
        return this.sprinting;
    }

    public boolean isAllowFly() {
        return this.allowFly;
    }

    public boolean isFlying() {
        return this.flying;
    }

    public boolean isInvincible() {
        return this.invincible;
    }

    public boolean isCreative() {
        return this.creative;
    }

    public float getFlySpeed() {
        return this.flySpeed;
    }

    public float getWalkSpeed() {
        return this.walkSpeed;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public void setAllowFly(boolean allowFly) {
        this.allowFly = allowFly;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public void setCreative(boolean creative) {
        this.creative = creative;
    }

    public void setFlySpeed(float flySpeed) {
        this.flySpeed = flySpeed;
    }

    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerAbilities)) {
            return false;
        }
        PlayerAbilities other = (PlayerAbilities)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isSprinting() != other.isSprinting()) {
            return false;
        }
        if (this.isAllowFly() != other.isAllowFly()) {
            return false;
        }
        if (this.isFlying() != other.isFlying()) {
            return false;
        }
        if (this.isInvincible() != other.isInvincible()) {
            return false;
        }
        if (this.isCreative() != other.isCreative()) {
            return false;
        }
        if (Float.compare(this.getFlySpeed(), other.getFlySpeed()) != 0) {
            return false;
        }
        return Float.compare(this.getWalkSpeed(), other.getWalkSpeed()) == 0;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PlayerAbilities;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isSprinting() ? 79 : 97);
        result = result * 59 + (this.isAllowFly() ? 79 : 97);
        result = result * 59 + (this.isFlying() ? 79 : 97);
        result = result * 59 + (this.isInvincible() ? 79 : 97);
        result = result * 59 + (this.isCreative() ? 79 : 97);
        result = result * 59 + Float.floatToIntBits(this.getFlySpeed());
        result = result * 59 + Float.floatToIntBits(this.getWalkSpeed());
        return result;
    }

    public String toString() {
        return "PlayerAbilities(sprinting=" + this.isSprinting() + ", allowFly=" + this.isAllowFly() + ", flying=" + this.isFlying() + ", invincible=" + this.isInvincible() + ", creative=" + this.isCreative() + ", flySpeed=" + this.getFlySpeed() + ", walkSpeed=" + this.getWalkSpeed() + ")";
    }
}

