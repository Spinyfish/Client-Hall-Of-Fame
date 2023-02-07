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

public class AutoClickerE extends PacketCheck {

    private int movements;
    private int stage;

    public AutoClickerE(PlayerData playerData) {
        super(playerData, "Auto-Clicker (E)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("AutoClickerE") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInFlying || packet instanceof PacketPlayInArmAnimation || packet instanceof PacketPlayInBlockDig) {

            if (this.stage == 0) {
                if (packet instanceof PacketPlayInArmAnimation) {
                    ++this.stage;
                }
            } else if (this.stage == 1) {
                if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                    ++this.stage;
                } else {
                    this.stage = 0;
                }
            } else if (this.stage == 2) {
                if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                    if (++vl >= 5) {
                        try {
                            if (this.movements > 10) {
                                alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AutoClickerE") && vl >= ConfigurationManager.banVL("AutoClickerE")) {
                                    punish(player);
                                }
                            }
                        } finally {
                            this.movements = 0;
                            vl = 0;
                        }
                    }
                    this.stage = 0;
                } else if (packet instanceof PacketPlayInArmAnimation) {
                    ++this.stage;
                } else {
                    this.movements = 0;
                    vl = 0;
                    this.stage = 0;
                }
            } else if (this.stage == 3) {
                if (packet instanceof PacketPlayInFlying) {
                    ++this.stage;
                } else {
                    this.movements = 0;
                    vl = 0;
                    this.stage = 0;
                }
            } else if (this.stage == 4) {
                if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                    ++this.movements;
                    this.stage = 0;
                } else {
                    this.movements = 0;
                    vl = 0;
                    this.stage = 0;
                }
            }
        }
        playerData.setCheckVl(vl, this);
    }

}
