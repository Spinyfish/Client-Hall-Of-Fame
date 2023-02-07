package org.crystalpvp.anticheat.check.combat.autoclicker;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
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

public class AutoClickerO extends PacketCheck {

    private boolean failed;
    private boolean sent;
    private int count;

    public AutoClickerO(PlayerData playerData) {
        super(playerData, "AutoClicker (O)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("AutoClickerO") == false) {
            return;
        }

        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInArmAnimation && !this.playerData.isDigging() && !this.playerData.isPlacing() && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L && this.playerData.getLastMovePacket() != null && System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp() < 110L && !this.playerData.isFakeDigging()) {
            if(this.sent) {
                ++this.count;
                if(!this.failed) {
                    if(++vl > 5) {
                        alert(AlertType.RELEASE, player,String.format("%s. VL %.1f/%s.", this.count, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())) , true);
                        if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AutoClickerO") && vl >= ConfigurationManager.banVL("AutoClickerO")) {
                            punish(player);
                        }
                    } else {
                        this.failed = true;
                    }
                } else {
                    this.sent = true;
                    this.count = 0;
                }
            } else if (packet instanceof PacketPlayInFlying) {
                this.failed = false;
                this.sent = false;
                this.count = 0;
            }
        }
        playerData.setCheckVl(vl, this);
    }
}
