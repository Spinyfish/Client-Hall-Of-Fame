package org.crystalpvp.anticheat.check.movement.scaffold;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.location.CrystalLocation;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class ScaffoldB extends PacketCheck {

    private long lastPlace;
    private boolean place;

    public ScaffoldB(PlayerData playerData) {
        super(playerData, "Scaffold (B)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (!ConfigurationManager.isEnabled("ScaffoldB")) {
            return;
        }
        double vl = playerData.getCheckVl(this);
        if (packet instanceof PacketPlayInBlockPlace && System.currentTimeMillis() - playerData
                .getLastDelayedMovePacket() > 220L && !playerData.isAllowTeleport()) {
            final CrystalLocation lastMovePacket = playerData.getLastMovePacket();
            if (lastMovePacket == null) {
                return;
            }
            final long delay = System.currentTimeMillis() - lastMovePacket.getTimestamp();
            if (delay <= 25.0) {
                lastPlace = System.currentTimeMillis();
                place = true;
            } else {
                vl -= 0.25;
            }
        } else if (packet instanceof PacketPlayInFlying && place) {
            final long time = System.currentTimeMillis() - lastPlace;
            if (time >= 25L) {
                if (++vl >= 10.0) {
                    alert(AlertType.RELEASE, player, String.format("T %s. VL %.1f/%s.", time, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
                }

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("ScaffoldB") && vl >= ConfigurationManager.banVL("ScaffoldB")) {
                    punish(player);
                }
            } else {
                vl -= 0.25;
            }
            place = false;
        }
        playerData.setCheckVl(vl, this);
    }

}
