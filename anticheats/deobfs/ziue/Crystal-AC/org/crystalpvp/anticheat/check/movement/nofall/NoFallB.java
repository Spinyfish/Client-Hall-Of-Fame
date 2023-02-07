package org.crystalpvp.anticheat.check.movement.nofall;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.BlockUtil;
import org.crystalpvp.anticheat.api.util.MathUtil;
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

public class NoFallB extends PacketCheck {

    private boolean tpDown;
    private double tpDownHeight;
    private double height;
    private double lastHeight;
    private boolean cancelType2;

    private ArrayList<Block> blocks = new ArrayList<>();

    public NoFallB(PlayerData playerData) {
        super(playerData, "NoFall (B)");
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (ConfigurationManager.isEnabled("NoFallB") == false || player.hasPermission("core.command.vanish")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (TimesUtil.differenceTimeSecond(playerData.getLastTeleport(), System.currentTimeMillis()) < 4) {
            return;
        } else if (TimesUtil.differenceTimeSecond(playerData.getRespawnTime(), System.currentTimeMillis()) < 4) {
            return;
        } else if (TimesUtil.differenceTimeSecond(playerData.getJoinTime(), System.currentTimeMillis()) < 8) {
            return;
        } else if (TimesUtil.differenceTimeSecond(playerData.getLastAttackPacket(), System.currentTimeMillis()) < 3) {
            return;
        } else if (TimesUtil.differenceTimeMillis(playerData.getLastMove(), System.currentTimeMillis()) > 110) {
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE || player.isFlying()) {
            return;
        }





        if (packet instanceof PacketPlayInFlying) {

            height = MathUtil.doubleDecimal(playerData.getLastDistanceY(), 2);

            /*Bukkit.broadcastMessage(String.format("\n\nTp %s. Height %s. LastH %s.", tpDown, height, lastHeight));

            if (playerData.isOnGround()) {
                Bukkit.broadcastMessage("true");
            } else {
                Bukkit.broadcastMessage("false");
            }*/



            if (((PacketPlayInFlying) packet).f()) {
                if (playerData.isOnGround()) {

                    if (tpDown && height == 0.0) {
                        tpDown = false;

                        blocks = BlockUtil.getBlocksAround(player.getLocation(), 2);

                        for (int i = 0; i < blocks.size(); ++i) {
                            if (String.format("%s", blocks.get(i)).contains("TRAP")
                                    || String.format("%s", blocks.get(i)).contains("DOOR")
                                    || String.format("%s", blocks.get(i)).contains("LADDER")) {
                                cancelType2 = true;
                                break;
                            }
                        }


                        if (!cancelType2) {
                            if (++vl > 5) {
                                alert(AlertType.RELEASE, player, String.format("TPDown %s. VL %.1f/%s.", tpDownHeight, vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("NoFallB") && vl >= ConfigurationManager.banVL("NoFallB")) {
                                    punish(player);
                                }
                            }
                        }

                    } else {
                        tpDown = false;
                    }




                    if (((height * -1) > 0.9) && lastHeight == 0.0) {
                        tpDown = true;
                        tpDownHeight = height;
                    }

                }

            }

        }




        vl -= 0.005;
        cancelType2 = false;
        lastHeight = height;
        playerData.setCheckVl(vl, this);
    }
}
