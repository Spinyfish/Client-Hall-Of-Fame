/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.legacy.bossbar;

public enum BossColor {
    PINK(0),
    BLUE(1),
    RED(2),
    GREEN(3),
    YELLOW(4),
    PURPLE(5),
    WHITE(6);

    private final int id;

    private BossColor(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}

