package me.levansj01.verus.compat.api.wrapper;

import me.levansj01.verus.compat.packets.VPacketPlayOutAbilities;
import org.bukkit.GameMode;

public class PlayerAbilities {
    private final boolean disableDamage;
    private final boolean allowEdit;
    private final boolean allowFlying;
    private final boolean isFlying;
    private final boolean isCreativeMode;
    private final float walkSpeed;
    private final float flySpeed;

    public PlayerAbilities then(GameMode var1) {
        boolean var3 = this.isFlying;
        boolean var6 = var1 != GameMode.ADVENTURE;
        boolean var2;
        boolean var4;
        boolean var5;
        if (var1 == GameMode.CREATIVE) {
            var4 = true;
            var5 = true;
            var2 = true;
        } else if (var1.getValue() == 3) {
            var4 = true;
            var5 = false;
            var2 = true;
            var3 = true;
        } else {
            var4 = false;
            var5 = false;
            var2 = false;
            var3 = false;
        }

        return new PlayerAbilities(var2, var3, var4, var5, var6, this.flySpeed, this.walkSpeed);
    }

    /** @deprecated */
    @Deprecated
    public PlayerAbilities(boolean var1, boolean var2, boolean var3, boolean var4, float var5, float var6) {
        this(var1, var2, var3, var4, true, var5, var6);
    }

    public PlayerAbilities(boolean var1, boolean var2, boolean var3, boolean var4, boolean var5, float var6, float var7) {
        this.disableDamage = var1;
        this.isFlying = var2;
        this.allowFlying = var3;
        this.isCreativeMode = var4;
        this.allowEdit = var5;
        this.flySpeed = var6;
        this.walkSpeed = var7;
    }

    public boolean isCreativeMode() {
        return this.isCreativeMode;
    }

    public float getFlySpeed() {
        return this.flySpeed;
    }

    public boolean fly() {
        return this.allowFlying || this.isFlying;
    }

    public boolean isDisableDamage() {
        return this.disableDamage;
    }

    protected boolean canEqual(Object var1) {
        return var1 instanceof PlayerAbilities;
    }

    public PlayerAbilities then(VPacketPlayOutAbilities var1) {
        return new PlayerAbilities(var1.isInvulnerable(), var1.isFlying(), var1.isCanFly(), var1.isCanInstantlyBuild(), this.allowEdit, var1.getFlySpeed(), var1.getWalkSpeed());
    }

    public boolean isFlying() {
        return this.isFlying;
    }

    public boolean isAllowFlying() {
        return this.allowFlying;
    }

    public boolean isAllowEdit() {
        return this.allowEdit;
    }

    public float getWalkSpeed() {
        return this.walkSpeed;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        var2 = var2 * 59 + (this.isDisableDamage() ? 79 : 97);
        var2 = var2 * 59 + (this.isFlying() ? 79 : 97);
        var2 = var2 * 59 + (this.isAllowFlying() ? 79 : 97);
        var2 = var2 * 59 + (this.isCreativeMode() ? 79 : 97);
        var2 = var2 * 59 + (this.isAllowEdit() ? 79 : 97);
        var2 = var2 * 59 + Float.floatToIntBits(this.getFlySpeed());
        var2 = var2 * 59 + Float.floatToIntBits(this.getWalkSpeed());
        return var2;
    }

    public boolean equals(Object var1) {
        if (var1 == this) {
            return true;
        } else if (!(var1 instanceof PlayerAbilities)) {
            return false;
        } else {
            PlayerAbilities var2 = (PlayerAbilities)var1;
            if (!var2.canEqual(this)) {
                return false;
            } else if (this.isDisableDamage() != var2.isDisableDamage()) {
                return false;
            } else if (this.isFlying() != var2.isFlying()) {
                return false;
            } else if (this.isAllowFlying() != var2.isAllowFlying()) {
                return false;
            } else if (this.isCreativeMode() != var2.isCreativeMode()) {
                return false;
            } else if (this.isAllowEdit() != var2.isAllowEdit()) {
                return false;
            } else if (Float.compare(this.getFlySpeed(), var2.getFlySpeed()) != 0) {
                return false;
            } else {
                return Float.compare(this.getWalkSpeed(), var2.getWalkSpeed()) == 0;
            }
        }
    }
}
