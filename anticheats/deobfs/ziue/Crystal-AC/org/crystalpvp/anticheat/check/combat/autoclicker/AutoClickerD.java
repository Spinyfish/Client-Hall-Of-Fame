package org.crystalpvp.anticheat.check.combat.autoclicker;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AutoClickerD extends PacketCheck {

    private int movements;
    private int stage;

    public AutoClickerD(PlayerData playerData) {
        super(playerData, "Auto-Clicker (D)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("AutoClickerD") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (stage == 0) {
            if (packet instanceof PacketPlayInArmAnimation) {
                ++stage;
            }
        } else if (stage == 1) {
            if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                ++stage;
            } else {
                stage = 0;
            }
        } else if (stage == 2) {
            if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                if (++vl >= 5) {
                    try {
                        if (movements > 10 && alert(AlertType.RELEASE, player, String.format("M %s. VL %.1f/%s.", movements, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AutoClickerD") && vl >= ConfigurationManager.banVL("AutoClickerD")) {
                                punish(player);
                            }
                        }
                    } finally {
                        final boolean movements = false;
                        this.movements = (movements ? 1 : 0);
                        vl = (movements ? 1 : 0);
                    }
                }

                stage = 0;
            } else if (packet instanceof PacketPlayInArmAnimation) {
                ++stage;
            } else {
                final boolean b = false;
                movements = (b ? 1 : 0);
                vl = (b ? 1 : 0);
                stage = (b ? 1 : 0);
            }
        } else if (stage == 3) {
            if (packet instanceof PacketPlayInFlying) {
                ++stage;
            } else {
                final boolean b2 = false;
                movements = (b2 ? 1 : 0);
                vl = (b2 ? 1 : 0);
                stage = (b2 ? 1 : 0);
            }
        } else if (stage == 4) {
            if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                ++movements;

                stage = 0;
            } else {
                final boolean b3 = false;
                movements = (b3 ? 1 : 0);
                vl = (b3 ? 1 : 0);
                stage = (b3 ? 1 : 0);
            }
        }

        playerData.setCheckVl(vl, this);
    }

}
