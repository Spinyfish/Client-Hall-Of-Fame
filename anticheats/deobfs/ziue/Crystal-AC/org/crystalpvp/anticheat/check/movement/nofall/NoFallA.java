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

public class NoFallA extends PacketCheck {

    private boolean false1;
    private boolean cancelType1;

    private ArrayList<Block> blocks = new ArrayList<>();

    public NoFallA(PlayerData playerData) {
        super(playerData, "NoFall (A)");
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (!ConfigurationManager.isEnabled("NoFallA") || player.hasPermission("core.command.vanish")) {
            return;
        }
        double vl = playerData.getCheckVl(this);
        double x = playerData.getSpawnPoint().getX() - player.getLocation().getX();
        double z = playerData.getSpawnPoint().getZ() - player.getLocation().getZ();

        if (TimesUtil.differenceTimeSecond(playerData.getLastTeleport(), System.currentTimeMillis()) < 3) {
           return;
        } else if (TimesUtil.differenceTimeSecond(playerData.getRespawnTime(), System.currentTimeMillis()) < 10) {
            return;
        } else if (TimesUtil.differenceTimeSecond(playerData.getJoinTime(), System.currentTimeMillis()) < 10) {
            return;
        } else if (TimesUtil.differenceTimeMillis(playerData.getLastMove(), System.currentTimeMillis()) > 110) {
            return;
        } else if ((z < 2 && z > -2) || (x < 2 && x > -2)) {
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE || player.isFlying()) {
            return;
        }


        if (packet instanceof PacketPlayInFlying) {

            if (((PacketPlayInFlying) packet).f()) {


                if (!playerData.isOnGround()) {

                    blocks = BlockUtil.getBlocksAround(player.getLocation(), 2);

                    for (int i = 0; i < blocks.size(); ++i) {
                        if (String.format("%s", blocks.get(i)).contains("LAVA")
                                || String.format("%s", blocks.get(i)).contains("WATER")
                                || String.format("%s", blocks.get(i)).contains("WEB")
                                || String.format("%s", blocks.get(i)).contains("TRAP")
                                || String.format("%s", blocks.get(i)).contains("DOOR")
                                || String.format("%s", blocks.get(i)).contains("VINE")
                                || String.format("%s", blocks.get(i)).contains("LADDER")) {
                            cancelType1 = true;
                            break;
                        }
                    }


                    if (false1) {
                        if (!cancelType1) {
                            if (++vl > 5) {

                                if (playerData.getPing() > 100) {
                                    return;
                                }

                                false1 = false;
                                alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("NoFallA") && vl >= ConfigurationManager.banVL("NoFallA")) {
                                    punish(player);
                                }

                            }
                        }

                    } else {
                        false1 = true;
                    }
                }
            }
        }

        vl -= 0.005;
        cancelType1 = false;
        playerData.setCheckVl(vl, this);
    }
}
