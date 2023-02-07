package org.crystalpvp.anticheat.check.combat.autoclicker;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.BaseBlockPosition;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

import java.util.LinkedList;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AutoClickerG extends PacketCheck {

    private final LinkedList<Integer> recentCounts = new LinkedList<>();
    private BaseBlockPosition lastBlock;
    private int flyingCount;

    public AutoClickerG(PlayerData playerData) {
        super(playerData, "Auto-Clicker (G)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("AutoClickerG") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInFlying) {
            ++this.flyingCount;
        }

        if (packet instanceof PacketPlayInBlockDig) {
            if (((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                if (this.lastBlock != null && this.lastBlock.equals(((PacketPlayInBlockDig) packet).a())) {
                    this.recentCounts.addLast(this.flyingCount);
                    if (this.recentCounts.size() == 20) {
                        double average = 0.0;
                        for (final int i : this.recentCounts) {
                            average += i;
                        }
                        average /= this.recentCounts.size();
                        double stdDev = 0.0;
                        for (final int j : this.recentCounts) {
                            stdDev += Math.pow(j - average, 2.0);
                        }
                        stdDev /= this.recentCounts.size();
                        stdDev = Math.sqrt(stdDev);
                        if (stdDev < 0.45 && ++vl >= 3.0) {

                            alert(AlertType.RELEASE, player, String.format("S %1.f. VL %.1f/%s.", stdDev, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AutoClickerG") && vl >= ConfigurationManager.banVL("AutoClickerG")) {
                                punish(player);
                            }

                        } else {
                            vl -= 0.5;
                        }
                        this.recentCounts.clear();
                    }
                }
                this.flyingCount = 0;
            } else if (((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                this.lastBlock = ((PacketPlayInBlockDig) packet).a();
            }
        }
        playerData.setCheckVl(vl, this);
    }

}
