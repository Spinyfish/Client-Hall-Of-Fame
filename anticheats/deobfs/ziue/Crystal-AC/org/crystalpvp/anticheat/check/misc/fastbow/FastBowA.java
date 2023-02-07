package org.crystalpvp.anticheat.check.misc.fastbow;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.TimesUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class FastBowA extends PacketCheck {

    private long diffShoot;

    public FastBowA(PlayerData playerData) {
        super(playerData, "FastBow (A)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (!ConfigurationManager.isEnabled("FastBowA")) {
            return;
        }

        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInBlockDig) {

            diffShoot = TimesUtil.differenceTimeMillis(playerData.getLastShoot(), System.currentTimeMillis());

            if (diffShoot < 40) {

                alert(AlertType.RELEASE, player, String.format("Delay %s. VL %.1f/%s", diffShoot, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), false);

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("FastBowA") && vl >= ConfigurationManager.banVL("FastBowA")) {
                    punish(player);
                }
            }

        }


        playerData.setCheckVl(vl, this);
    }

}
