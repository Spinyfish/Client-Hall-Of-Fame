package me.levansj01.verus.type.custom.checks.autoclicker;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInArmAnimation;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockPlace;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.util.java.BasicDeque;
import me.levansj01.verus.util.java.CappedQueue;
import me.levansj01.verus.util.java.Moment;
import me.levansj01.verus.verus2.data.player.TickerMap;
import me.levansj01.verus.verus2.data.player.TickerType;

@CheckInfo(
        type = CheckType.AUTO_CLICKER,
        subType = "K",
        friendlyName = "Auto Clicker",
        version = CheckVersion.RELEASE,
        minViolations = -1.0D
)
public class AutoClickerK extends Check implements PacketHandler {
    private int ticks, lastTicks;
    private boolean swung;
    private final BasicDeque intervals;
    private boolean place;

    public void handle(VPacketPlayInBlockPlace vPacket) {
        this.place = true;
    }

    public AutoClickerK() {
        this.intervals = new CappedQueue(200);
        this.ticks = 0;
        this.lastTicks = 0;
    }

    public void handle(VPacketPlayInFlying vPacket) {
        if (this.swung && !this.playerData.isSwingDigging() && !this.place && this.playerData.isSurvival()) {
            if (this.ticks < 9) {
                if (this.lastTicks > 0) {
                    this.intervals.addFirst((byte)(this.ticks - this.lastTicks));
                    int intervalsSize = this.intervals.size();
                    if (intervalsSize == 200) {
                        Moment moment = new Moment(this.intervals);
                        if (moment.getKurtosis() < 0.1D) {
                            TickerMap tickerMap = this.playerData.getTickerMap();
                            if (tickerMap.get(TickerType.DOUBLE_SWING) - tickerMap.get(TickerType.LEGIT_SWING) > 20) {
                                this.handleViolation(String.format("1: %.3f 2: %.3f 3: %.3f 4: %.3f", moment.getMean(), moment.getVariance(), moment.getSkewness(), moment.getKurtosis()), moment.getKurtosis() < -0.1D ? 2.0D : 1.0D, true);
                            }
                        } else {
                            this.decreaseVL(0.1D);
                        }
                        this.intervals.clear();
                    }
                }
                this.lastTicks = this.ticks;
            }
            this.ticks = 0;
        }
        this.place = false;
        this.swung = false;
        this.ticks += 1;
    }

    public void handle(VPacketPlayInArmAnimation vPacket) {
        this.swung = true;
    }
}
