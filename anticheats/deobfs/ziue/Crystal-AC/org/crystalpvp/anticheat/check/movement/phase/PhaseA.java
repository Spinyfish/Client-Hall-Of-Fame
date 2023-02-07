package org.crystalpvp.anticheat.check.movement.phase;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class PhaseA extends PacketCheck {


    public PhaseA(PlayerData playerData) {
        super(playerData, "Phase (A)");
    }

    private int stage;

    @Override
    public void handleCheck(Player player, Packet type) {
        if (ConfigurationManager.isEnabled("PhaseA") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);
        final String simpleName = type.getClass().getSimpleName();
        switch (simpleName) {
            case "PacketPlayInFlying": {
                if (this.stage == 0) {
                    ++this.stage;
                    break;
                }
                this.stage = 0;
                break;
            }
            case "PacketPlayInPosition": {
                if (this.stage >= 2) {
                    ++this.stage;
                    break;
                }
                break;
            }
            case "PacketPlayInEntityAction": {
                if (((PacketPlayInEntityAction)type).b() == PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING) {
                    if (this.stage == 1) {
                        ++this.stage;
                        break;
                    }
                    this.stage = 0;
                    break;
                }
                else {
                    if (((PacketPlayInEntityAction)type).b() == PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING && this.stage >= 3) {
                        alert(AlertType.RELEASE, player, String.format("%s. VL %.1f/%s.", ++vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                        if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("PhaseA") && vl >= ConfigurationManager.banVL("PhaseA")) {
                            punish(player);
                        }
                        break;
                    }
                    break;
                }
            }
            default:
                break;
        }


    }
}

