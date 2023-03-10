/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.util;

import java.time.Duration;
import org.jetbrains.annotations.NotNull;

public interface Ticks {
    public static final int TICKS_PER_SECOND = 20;
    public static final long SINGLE_TICK_DURATION_MS = 50L;

    @NotNull
    public static Duration duration(long ticks) {
        return Duration.ofMillis(ticks * 50L);
    }
}

