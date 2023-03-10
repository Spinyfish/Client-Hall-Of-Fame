/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.api.data.type.HandTypes
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.item.inventory.ItemStack
 */
package com.viaversion.viaversion.sponge.listeners.protocol1_9to1_8.sponge5;

import com.viaversion.viaversion.sponge.listeners.protocol1_9to1_8.ItemGrabber;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

public class Sponge5ItemGrabber
implements ItemGrabber {
    @Override
    public ItemStack getItem(Player player) {
        return player.getItemInHand(HandTypes.MAIN_HAND).orElse(null);
    }
}

