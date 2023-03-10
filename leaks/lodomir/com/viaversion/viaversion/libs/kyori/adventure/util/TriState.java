/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TriState {
    NOT_SET,
    FALSE,
    TRUE;


    @NotNull
    public static TriState byBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }

    @NotNull
    public static TriState byBoolean(@Nullable Boolean value) {
        return value == null ? NOT_SET : TriState.byBoolean((boolean)value);
    }
}

