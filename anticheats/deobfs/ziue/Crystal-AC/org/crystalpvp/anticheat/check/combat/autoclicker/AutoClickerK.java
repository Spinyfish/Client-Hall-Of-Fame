package org.crystalpvp.anticheat.check.combat.autoclicker;

import net.minecraft.server.v1_8_R3.*;
import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AutoClickerK extends PacketCheck {

    private Integer releaseTime;
    private Queue<Integer> clickQueue;
    private int start;

    public AutoClickerK(PlayerData playerData) {
        super(playerData, "Auto-Clicker (K)");
        this.releaseTime = null;
        this.clickQueue = new ConcurrentLinkedQueue<Integer>();
        this.start = 0;

    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("AutoClickerK") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInFlying) {
            if (playerData.isDigging()) {
                ++this.start;
            }
            if (this.releaseTime != null) {
                ++this.releaseTime;



            }
        }
        else if (packet instanceof PacketPlayInBlockDig) {
            final PacketPlayInBlockDig packetPlayInBlockDig = (PacketPlayInBlockDig)packet;
            if (packetPlayInBlockDig.c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                this.releaseTime = 0;
            }
            else if (packetPlayInBlockDig.c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK && this.releaseTime != null && this.releaseTime < 4 && this.releaseTime > 0) {
                this.clickQueue.add(this.releaseTime);
                if (this.clickQueue.size() > 10) {
                    final double value = MathUtil.variance(1, this.clickQueue) / this.start;
                    if (value > 0.2) {
                        alert(AlertType.RELEASE, player, String.format("%s. VL %.1f/%s.", value, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                        if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AutoClickerK") && vl >= ConfigurationManager.banVL("AutoClickerK")) {
                            punish(player);
                        }
                    }
                    else {
                        vl -= Math.min(vl + 3.0, 0.25);
                    }
                    this.clickQueue.clear();
                    this.start = 0;
                }
                this.releaseTime = null;
            }
        }
        playerData.setCheckVl(vl, this);
    }

}