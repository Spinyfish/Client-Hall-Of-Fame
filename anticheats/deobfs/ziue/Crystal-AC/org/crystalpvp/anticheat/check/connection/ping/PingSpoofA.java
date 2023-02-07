package org.crystalpvp.anticheat.check.connection.ping;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInTransaction;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class PingSpoofA extends PacketCheck {

    public PingSpoofA(PlayerData playerData) {
        super(playerData, "PingSpoof (A)");
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (!ConfigurationManager.isEnabled("PingSpoofA")) {
            return;
        }

        if (packet instanceof PacketPlayInTransaction) {

            double vl = playerData.getCheckVl(this);

            long transactionPing = playerData.getTransactionPing();
            long keepAlivePing = playerData.getPing();

            //Bukkit.broadcastMessage(String.format("T %s. K %s.", transactionPing, keepAlivePing));

            if (keepAlivePing - 100 < 0) {
                return;
            }

            if (keepAlivePing - 100 > transactionPing) {
                alert(AlertType.RELEASE, player, String.format("Transaction %s. KeepAlive %s. VL %.1f/%s.", transactionPing, keepAlivePing, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("PingSpoofA") && vl >= ConfigurationManager.banVL("PingSpoofA")) {
                    punish(player);
                }


                playerData.setCheckVl(vl, this);

            } else {
                return;
            }

        }

    }
}
