package org.crystalpvp.anticheat.check.movement.nofall;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
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

public class NoFallC extends PacketCheck {

    private boolean packetBool;
    private double duplicateY;
    private double height;
    private boolean false3;

    private ArrayList<Block> blocks = new ArrayList<>();

    public NoFallC(PlayerData playerData) {
        super(playerData, "NoFall (C)");
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (!ConfigurationManager.isEnabled("NoFallC") || player.hasPermission("core.command.vanish")) {
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

        if (player.getGameMode() == GameMode.CREATIVE || player.isFlying() || playerData.getPing() > 300) {
            return;
        }



        if (packet instanceof PacketPlayInFlying) {

            height = MathUtil.doubleDecimal(playerData.getLastDistanceY(), 2);



            if (((PacketPlayInFlying) packet).f()) {


                if (!playerData.isOnGround()) {

                    if (false3) {

                        if (height == duplicateY && height != 0 && !packetBool) {

                            false3 = false;

                            if (++vl > 5) {
                                alert(AlertType.RELEASE, player, String.format("DuplicateY %s. VL %.1f/%s.", height, vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("NoFallC") && vl >= ConfigurationManager.banVL("NoFallC")) {
                                    punish(player);
                                }
                            }
                        }

                    } else {
                        false3 = true;
                    }

                    duplicateY = height;
                } else {
                    false3 = false;
                }

                packetBool = true;

            } else {


                if (!playerData.isOnGround()) {


                    if (height == duplicateY && height != 0 && packetBool) {

                        if (playerData.getPing() > 100) {
                            false3 = true;
                        }

                        if (false3) {

                            false3 = false;

                            if (++vl > 5) {
                                alert(AlertType.RELEASE, player, String.format("DuplicateY %s. VL %.1f/%s.", height, vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("NoFallC") && vl >= ConfigurationManager.banVL("NoFallC")) {
                                    punish(player);
                                }
                            }
                        } else {
                            false3 = true;
                        }

                    }


                    duplicateY = height;
                } else {
                    false3 = false;
                }

                packetBool = false;
            }

        }

        vl -= 0.005;
        playerData.setCheckVl(vl, this);
    }
}