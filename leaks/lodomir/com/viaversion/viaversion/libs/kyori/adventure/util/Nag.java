/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.util;

import org.jetbrains.annotations.NotNull;

public abstract class Nag
extends RuntimeException {
    public static void print(@NotNull Nag nag) {
        nag.printStackTrace();
    }

    protected Nag(String message) {
        super(message);
    }
}

