package org.crystalpvp.anticheat.check.movement.nofall;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.BlockUtil;
import org.crystalpvp.anticheat.api.util.TimesUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class NoFallD extends PacketCheck {

    private int duplicateBounce;
    private double height;

    private ArrayList<Block> blocks = new ArrayList<>();

    public NoFallD(PlayerData playerData) {
        super(playerData, "NoFall (D)");
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (ConfigurationManager.isEnabled("NoFallD") == false || player.hasPermission("core.command.vanish")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (TimesUtil.differenceTimeSecond(playerData.getLastTeleport(), System.currentTimeMillis()) < 4) {
            return;
        } else if (TimesUtil.differenceTimeSecond(playerData.getRespawnTime(), System.currentTimeMillis()) < 4) {
            return;
        } else if (TimesUtil.differenceTimeSecond(playerData.getJoinTime(), System.currentTimeMillis()) < 8) {
            return;
        } else if (TimesUtil.differenceTimeMillis(playerData.getLastMove(), System.currentTimeMillis()) > 110) {
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE || player.isFlying()) {
            return;
        }





        if (packet instanceof PacketPlayInFlying) {

            height = playerData.getLastDistanceY();

            if (height == 0.09999999999999432) {

                blocks = BlockUtil.getBlocksAround(player.getLocation(), 2);

                ++duplicateBounce;

                for (int i = 0; i < blocks.size(); ++i) {
                    if (String.format("%s", blocks.get(i)).contains("LAVA")
                            || String.format("%s", blocks.get(i)).contains("WATER")
                            || String.format("%s", blocks.get(i)).contains("SLIME")) {
                        duplicateBounce = 0;
                        break;
                    }
                }

                if (duplicateBounce > 2) {


                    if (++vl > 5) {

                        alert(AlertType.RELEASE, player, String.format("Bounce %s. VL %.1f/%s.", duplicateBounce, vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                        if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("NoFallD") && vl >= ConfigurationManager.banVL("NoFallD")  / 2) {
                            punish(player);
                        }

                    }

                }
            } else {
                duplicateBounce = 0;
            }
        }

        vl -= 0.005;
        playerData.setCheckVl(vl, this);
    }
}