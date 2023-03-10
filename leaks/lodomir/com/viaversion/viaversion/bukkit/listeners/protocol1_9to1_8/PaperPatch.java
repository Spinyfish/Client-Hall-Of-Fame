/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.plugin.Plugin
 */
package com.viaversion.viaversion.bukkit.listeners.protocol1_9to1_8;

import com.viaversion.viaversion.bukkit.listeners.ViaBukkitListener;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

public class PaperPatch
extends ViaBukkitListener {
    public PaperPatch(Plugin plugin) {
        super(plugin, Protocol1_9To1_8.class);
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent e) {
        if (this.isOnPipe(e.getPlayer())) {
            Material block = e.getBlockPlaced().getType();
            if (this.isPlacable(block)) {
                return;
            }
            Location location = e.getPlayer().getLocation();
            Block locationBlock = location.getBlock();
            if (locationBlock.equals(e.getBlock())) {
                e.setCancelled(true);
            } else if (locationBlock.getRelative(BlockFace.UP).equals(e.getBlock())) {
                e.setCancelled(true);
            } else {
                Location diff = location.clone().subtract(e.getBlock().getLocation().add(0.5, 0.0, 0.5));
                if (Math.abs(diff.getX()) <= 0.8 && Math.abs(diff.getZ()) <= 0.8) {
                    if (diff.getY() <= 0.1 && diff.getY() >= -0.1) {
                        e.setCancelled(true);
                        return;
                    }
                    BlockFace relative = e.getBlockAgainst().getFace(e.getBlock());
                    if (relative == BlockFace.UP && diff.getY() < 1.0 && diff.getY() >= 0.0) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    private boolean isPlacable(Material material) {
        if (!material.isSolid()) {
            return true;
        }
        switch (material.getId()) {
            case 63: 
            case 68: 
            case 176: 
            case 177: {
                return true;
            }
        }
        return false;
    }
}

