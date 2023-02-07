package org.crystalpvp.anticheat.check.combat.autoclicker;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AutoClickerN extends PacketCheck {

    private int movements;
    private int failed;
    private int passed;
    private int stage;

    public AutoClickerN(PlayerData playerData) {
        super(playerData, "Auto-Clicker (N)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {

        if (ConfigurationManager.isEnabled("AutoClickerN") == false) {
            return;
        }

        double vl = playerData.getCheckVl(this);

        if (System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L && this.playerData.getLastMovePacket() != null && System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp() < 110L) {
            if(packet instanceof PacketPlayInArmAnimation) {
                if(this.stage == 0 || this.stage == 1) {
                    ++this.stage;
                } else {
                    this.stage = 1;
                }
            } else if (packet instanceof PacketPlayInFlying) {
                if(this.stage == 2) {
                    ++this.stage;
                } else {
                    this.stage = 0;
                }
                ++this.movements;
            } else if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                if(this.stage == 3) {
                    ++this.failed;
                } else {
                    ++this.passed;
                }
                if(this.movements >= 200 && this.failed + this.passed > 60) {
                    final double rat = (this.passed == 0) ? -1.0 : (this.failed / this.passed);
                    if(rat > 2.5) {
                        if((vl += 1.0 + (rat - 2.0) * 0.75) >= 4.0) {
                            alert(AlertType.EXPERIMENTAL, player, String.format("RAT %.2f. VL %.1f/%s.", rat, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AutoClickerN") && vl >= ConfigurationManager.banVL("AutoClickerN")) {
                                punish(player);
                            }
                        }
                    } else {
                        vl -= 2.0;
                    }
                    final boolean failed = false;
                    this.movements = 0;
                    this.passed = 0;
                    this.failed = 0;
                }
            }
        } else {
            this.stage = 0;
        }
        playerData.setCheckVl(vl, this);
    }
}