package org.crystalpvp.anticheat.check.combat.killaura;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.location.CrystalLocation;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class KillAuraE extends PacketCheck {

    private long lastAttack;
    private boolean attack;
    
    public KillAuraE(PlayerData playerData) {
        super(playerData, "Kill-Aura / Criticals (E)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("KillAuraE") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);
        if (packet instanceof PacketPlayInUseEntity && ((PacketPlayInUseEntity)packet).a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK && System.currentTimeMillis() - playerData.getLastDelayedMovePacket() > 220L && !playerData.isAllowTeleport()) {
            final CrystalLocation lastMovePacket = playerData.getLastMovePacket();

            if (lastMovePacket == null) {
                return;
            }

            final long delay = System.currentTimeMillis() - lastMovePacket.getTimestamp();

            if (delay <= 25.0) {
                lastAttack = System.currentTimeMillis();
                attack = true;
            } else {
                vl -= 0.25;
            }
        } else if (packet instanceof PacketPlayInFlying && attack) {
            final long time = System.currentTimeMillis() - lastAttack;

            if (time >= 25L) {
                if (++vl >= 10.0 && alert(AlertType.RELEASE, player, String.format("T %s. VL %.1f/%s.", time, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), false)) {

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("KillAuraE") && vl >= ConfigurationManager.banVL("KillAuraE")) {
                        punish(player);
                    }
                }
            } else {
                vl -= 0.25;
            }

            attack = false;
        }

        playerData.setCheckVl(vl, this);
    }

}
