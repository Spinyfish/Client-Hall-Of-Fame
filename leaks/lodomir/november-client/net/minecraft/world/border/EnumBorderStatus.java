/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.border;

public enum EnumBorderStatus {
    GROWING(4259712),
    SHRINKING(0xFF3030),
    STATIONARY(2138367);

    private final int id;

    private EnumBorderStatus(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }
}

