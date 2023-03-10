/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.plugin.Plugin
 */
package com.viaversion.viabackwards;

import com.viaversion.viabackwards.api.ViaBackwardsPlatform;
import com.viaversion.viaversion.api.Via;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin
extends Plugin
implements ViaBackwardsPlatform {
    public void onLoad() {
        Via.getManager().addEnableListener(() -> this.init(this.getDataFolder()));
    }

    @Override
    public void disable() {
    }
}

