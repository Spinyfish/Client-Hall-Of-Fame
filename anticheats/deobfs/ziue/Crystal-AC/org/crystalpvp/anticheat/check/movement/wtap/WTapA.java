package org.crystalpvp.anticheat.check.movement.wtap;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.LinkedList;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class WTapA extends PacketCheck {

    private final Deque<Integer> recentCounts;
    private boolean release;
    private int flyingCount;

    public WTapA(PlayerData playerData) {
        super(playerData, "W-Tap (A)");

        recentCounts = new LinkedList<>();
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("WTapA") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);
        if (packet instanceof PacketPlayInEntityAction) {
            final PacketPlayInEntityAction.EnumPlayerAction playerAction = ((PacketPlayInEntityAction) packet).b();

            if (playerAction == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING) {
                if (playerData.getLastAttackPacket() + 1000L > System.currentTimeMillis() && flyingCount < 10 && !release) {
                    recentCounts.add(flyingCount);

                    if (recentCounts.size() == 20) {
                        double average = 0.0;

                        for (final double flyingCount : recentCounts) {
                            average += flyingCount;
                        }

                        average /= recentCounts.size();

                        double stdDev = 0.0;

                        for (final long l : recentCounts) {
                            stdDev += Math.pow(l - average, 2.0);
                        }

                        stdDev /= recentCounts.size();
                        stdDev = Math.sqrt(stdDev);

                        if (stdDev == 0.0) {
                            if ((vl += 1.2) >= 2.4) {
                                alert(AlertType.RELEASE, player, String.format("STD %.2f. VL %.1f/%s.", stdDev, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), false);
                            }
                            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("WTapA") && vl >= ConfigurationManager.banVL("WTapA")) {
                                punish(player);
                            }
                        } else {
                            vl -= 2.0;
                        }
                        recentCounts.clear();
                    }
                }
            } else if (playerAction == PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING) {
                flyingCount = 0;
            }
        } else if (packet instanceof PacketPlayInFlying) {
            ++flyingCount;

            release = false;
        } else if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM) {
            release = true;
        }
        playerData.setCheckVl(vl, this);
    }

}
