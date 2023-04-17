package club.mineman.antigamingchair.check.impl.autoclicker;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.LinkedList;

public class AutoClickerF extends PacketCheck {
    private final Deque<Long> recentDelays;
    private BlockPosition lastBlock;
    private long blockDigTime;

    public AutoClickerF(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
        this.recentDelays = new LinkedList<Long>();
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (packet instanceof PacketPlayInBlockDig) {
            final PacketPlayInBlockDig blockDig = (PacketPlayInBlockDig) packet;
            if (blockDig.c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                this.blockDigTime = System.currentTimeMillis();
                this.lastBlock = blockDig.a();
            } else if (blockDig.c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                if (!this.lastBlock.equals(blockDig.a())) {
                    this.lastBlock = blockDig.a();
                    return;
                }
                final long delay = System.currentTimeMillis() - this.blockDigTime;
                if (delay > 500L) {
                    return;
                }
                double vl = this.playerData.getCheckVl(this);
                this.recentDelays.addLast(delay);
                if (this.recentDelays.size() == 20) {
                    double average = 0.0;
                    for (final Long l : this.recentDelays) {
                        if (l == null) {
                            continue;
                        }
                        average += l;
                    }
                    average /= this.recentDelays.size();
                    double stdDev = 0.0;
                    for (final Long i : this.recentDelays) {
                        if (i == null) {
                            continue;
                        }
                        stdDev += Math.pow((double) i - average, 2.0);
                    }
                    stdDev /= this.recentDelays.size();
                    stdDev = Math.sqrt(stdDev);
                    if (stdDev < 25.0 && ++vl > 5.0) {
                        this.alert(PlayerAlertEvent.AlertType.EXPERIMENTAL, player, String.format("failed Auto Clicker Check F (Experimental). STD %.1f. VL %.1f.", stdDev, vl));
                    } else {
                        vl -= 0.5;
                    }
                    this.recentDelays.clear();
                }
                this.playerData.setCheckVl(vl, this);
            }
        }
    }
}
