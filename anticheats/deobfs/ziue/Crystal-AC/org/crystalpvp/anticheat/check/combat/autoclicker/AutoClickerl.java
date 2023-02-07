package org.crystalpvp.anticheat.check.combat.autoclicker;

import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AutoClickerl extends PacketCheck {

    private int ticks = 0;
    private boolean swung = false;
    private boolean place = false;
    private Queue<Integer> intervals = new ConcurrentLinkedQueue<Integer>();

	public AutoClickerl(PlayerData playerData) {
		super(playerData, "Auto-Clicker (L)");
	}

	@Override
	public void handleCheck(Player player, Packet packet) {
		if (ConfigurationManager.isEnabled("AutoClickerL") == false) {
			return;
		}
		double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInFlying) {
            if (this.swung && !playerData.isDigging() && !this.place) {
                if (this.ticks < 8) {
                    this.intervals.add(this.ticks);
                    if (this.intervals.size() >= 40) {
                        double deviation = MathUtil.deviation(this.intervals);
                        vl += (0.325 - deviation) * 2.0 + 0.675;
                        if (vl < -6.0) {
                            vl= -6.0;
                        }
                        if (deviation < 0.325) {
                            alert(AlertType.RELEASE, player,String.format(" VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())) , true);
                            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AutoClickerL") && vl >= ConfigurationManager.banVL("AutoClickerL")) {
                                punish(player);
                            }
                        }
                        this.intervals.clear();
                    }
                }
                this.ticks = 0;
            }
            this.place = false;
            this.swung = false;
            ++this.ticks;
        } else if (packet instanceof PacketPlayInArmAnimation) {
            this.swung = true;
        } else if (packet instanceof PacketPlayInBlockPlace) {
            this.place = true;
        }
        playerData.setCheckVl(vl, this);

    }


}
