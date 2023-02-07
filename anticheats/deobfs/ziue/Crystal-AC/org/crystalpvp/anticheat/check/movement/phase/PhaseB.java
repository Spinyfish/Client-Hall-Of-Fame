package org.crystalpvp.anticheat.check.movement.phase;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.event.player.PlayerAlertEvent;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.data.location.CrystalLocation;
import org.crystalpvp.anticheat.data.player.PlayerData;

import java.util.Arrays;
import java.util.List;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class PhaseB extends PositionCheck {

    private static final List<Material> PHASE_BLOCKS;
    private CrystalLocation lastNotInBlockLocation;
    private boolean inBlock;
    private int blocksPhased;

    public PhaseB(PlayerData playerData) {
        super(playerData, "Phase (B)");
    }

    @Override
    public void handleCheck(Player player, final PositionUpdate update) {
        if (ConfigurationManager.isEnabled("PhaseB") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);
        final boolean inBlock = this.inBlock;
        final Location to = update.getTo();
        try {
            if (PhaseB.PHASE_BLOCKS.contains(to.getBlock().getType())) {
                this.inBlock = false;
                return;
            }
            if (to.getBlock().getType().name().contains("FENCE") || to.getBlock().getType().name().contains("DOOR") || !to.getBlock().getType().isSolid()) {
                this.inBlock = false;
                return;
            }
            this.inBlock = true;
            final Location from = update.getFrom();
            if (inBlock && (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ())) {
                vl += 1.0 + ++this.blocksPhased / 10.0;
                if (vl > 5.0) {
                    alert(AlertType.RELEASE, player, String.format("BP %s. VL %.2f.", ++vl, ConfigurationManager.alertVl(getClass().getSimpleName()), this.blocksPhased), false);

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("PhaseB") && vl >= ConfigurationManager.banVL("PhaseB")) {
                        punish(player);
                    }
                }
            }
        }
        finally {
            if (inBlock && !this.inBlock) {
                this.lastNotInBlockLocation = CrystalLocation.fromBukkitLocation(to);
                this.blocksPhased = 0;
                vl -= 0.45;
            }
            this.setVl(vl);
        }
    }

    static {
        PHASE_BLOCKS = Arrays.asList(Material.LAVA, Material.STATIONARY_LAVA, Material.WATER, Material.STATIONARY_WATER, Material.WATER_LILY, Material.LADDER, Material.AIR, Material.ANVIL, Material.RAILS, Material.ACTIVATOR_RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL, Material.TORCH, Material.BED, Material.BED_BLOCK, Material.BREWING_STAND, Material.BREWING_STAND_ITEM);
    }
}
