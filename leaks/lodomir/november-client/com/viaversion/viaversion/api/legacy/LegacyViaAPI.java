/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.legacy;

import com.viaversion.viaversion.api.legacy.bossbar.BossBar;
import com.viaversion.viaversion.api.legacy.bossbar.BossColor;
import com.viaversion.viaversion.api.legacy.bossbar.BossStyle;

public interface LegacyViaAPI<T> {
    public BossBar createLegacyBossBar(String var1, float var2, BossColor var3, BossStyle var4);

    default public BossBar createLegacyBossBar(String title, BossColor color, BossStyle style) {
        return this.createLegacyBossBar(title, 1.0f, color, style);
    }
}

