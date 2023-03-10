/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.api.block.BlockSnapshot
 *  org.spongepowered.api.data.Transaction
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.event.Listener
 *  org.spongepowered.api.event.block.ChangeBlockEvent$Place
 *  org.spongepowered.api.event.filter.cause.Root
 *  org.spongepowered.api.world.Location
 */
package com.viaversion.viaversion.sponge.listeners.protocol1_9to1_8;

import com.viaversion.viaversion.SpongePlugin;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import com.viaversion.viaversion.sponge.listeners.ViaSpongeListener;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.world.Location;

public class BlockListener
extends ViaSpongeListener {
    public BlockListener(SpongePlugin plugin) {
        super(plugin, Protocol1_9To1_8.class);
    }

    @Listener
    public void placeBlock(ChangeBlockEvent.Place e, @Root Player player) {
        if (this.isOnPipe(player.getUniqueId())) {
            Location loc = (Location)((BlockSnapshot)((Transaction)e.getTransactions().get(0)).getFinal()).getLocation().get();
            EntityTracker1_9 tracker = (EntityTracker1_9)this.getUserConnection(player.getUniqueId()).getEntityTracker(Protocol1_9To1_8.class);
            tracker.addBlockInteraction(new Position(loc.getBlockX(), (short)loc.getBlockY(), loc.getBlockZ()));
        }
    }
}

