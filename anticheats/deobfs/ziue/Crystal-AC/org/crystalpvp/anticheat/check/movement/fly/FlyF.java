package org.crystalpvp.anticheat.check.movement.fly;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.BlockUtil;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.api.util.TimesUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class FlyF extends PacketCheck {

    public FlyF(PlayerData playerData) {
        super(playerData, "Flight/AirJump (F)");
    }

    private boolean cancel;
    private double height;
    private double lastHeight;
    private int stage;
    private ArrayList<Block> blocks = new ArrayList<>();

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (!ConfigurationManager.isEnabled("FlyF")) {
            return;
        }

        double vl = playerData.getCheckVl(this);
        double height = MathUtil.doubleDecimal(playerData.getLastDistanceY(), 2);


        if (playerData.isOnPiston() || playerData.isWasOnPiston()) {
            return;
        } else if (TimesUtil.differenceTimeSecond(playerData.getLastTeleport(), System.currentTimeMillis()) < 5) {
            return;
        } else if (TimesUtil.differenceTimeSecond(playerData.getLastAttackPacket(), System.currentTimeMillis()) < 3) {
            return;
        }


        if (packet instanceof PacketPlayInFlying) {

            if (!((PacketPlayInFlying) packet).f()) {


                if (playerData.isWasUnderBlock() || playerData.isUnderBlock()) {
                    stage = 0;
                }

                if (!playerData.isOnGround() && height < 0 && stage == 0) {
                    ++stage;
                } else if (playerData.isOnGround() && stage == 1 && height == lastHeight * -1 && height != 0) {

                    stage = 0;
                    cancel = false;
                    blocks = BlockUtil.getBlocksAround(player.getLocation(), 2);

                    for (int i = 0; i < blocks.size(); ++i) {
                        if (String.format("%s", blocks.get(i)).contains("LAVA")
                                || String.format("%s", blocks.get(i)).contains("WATER")
                                || String.format("%s", blocks.get(i)).contains("SLIME")) {
                            cancel = true;
                            break;
                        }
                    }

                    if (!cancel) {

                        if (++vl > 5) {

                            alert(AlertType.RELEASE, player, String.format("LastHeight %s. Height %s. VL %.1f/%s.", lastHeight, height, vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("FlyF") && vl >= ConfigurationManager.banVL("FlyF")) {
                                punish(player);
                            }

                        }
                    }
                } else {
                    stage = 0;
                }




                if (!playerData.isOnGround() && height > 0 && lastHeight < 0 && BlockUtil.isOnPlatform(player.getLocation(), "AIR")) {

                    cancel = false;
                    blocks = BlockUtil.getBlocksAround(player.getLocation(), 2);

                    for (int i = 0; i < blocks.size(); ++i) {
                        if (String.format("%s", blocks.get(i)).contains("LAVA")
                                || String.format("%s", blocks.get(i)).contains("SLIME")) {
                            cancel = true;
                        }
                    }

                    if (!cancel) {

                        if (++vl > 5) {

                            alert(AlertType.RELEASE, player, String.format("LastHeight %s. Height %s. VL %.1f/%s.", lastHeight, height, vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("FlyF") && vl >= ConfigurationManager.banVL("FlyF")) {
                                punish(player);
                            }

                        }
                    }
                }
            }
        }





        vl -= 0.005;
        lastHeight = height;
        playerData.setCheckVl(vl, this);
    }
}