package me.levansj01.verus.type.premium.checks.killaura;

import me.levansj01.verus.check.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.data.transaction.tracker.*;
import me.levansj01.verus.util.location.*;
import me.levansj01.verus.util.java.*;
import java.util.function.*;
import java.util.*;
import java.util.stream.*;

@CheckInfo(type = CheckType.KILL_AURA, subType = "Q", friendlyName = "KillAura", version = CheckVersion.DEVELOPMENT, minViolations = -5.0, maxViolations = 20)
public class KillAuraQ extends AimCheck
{
    private final Queue values;

    
    private static double lambda$handle$1(final double n, final Double n2) {
        return Math.abs(n2 - n) + Math.abs(n2 / 2.0);
    }
    
    private static Double lambda$handle$0(final PlayerLocation playerLocation, final PacketLocation packetLocation) {
        return MathUtil.getLuckyAura(playerLocation, (IVector3d)packetLocation);
    }
    
    public KillAuraQ() {
        this.values = new LinkedList();
    }
    
    @Override
    public void handle(final PlayerLocation playerLocation, final PlayerLocation playerLocation2, final long n) {
        if (this.playerData.getLastAttackTicks() <= (1) && this.playerData.getLastAttacked() != null && Math.min(Math.abs(playerLocation.getYaw() - playerLocation2.getYaw()), Math.abs(playerLocation.getPitch() - playerLocation2.getPitch())) > 0.5) {
            final AtomicCappedQueue atomicCappedQueue = this.playerData.getRecentMoveMap().get(this.playerData.getLastAttackedId());
            if (atomicCappedQueue != null && atomicCappedQueue.size() > (5)) {
                final ArrayList<PacketLocation> list = new ArrayList<PacketLocation>();
                final long n2 = n - (125) - this.playerData.getTransactionPing();
                final Iterator iterator = atomicCappedQueue.iterator();
                PacketLocation packetLocation = iterator.next();
                while (iterator.hasNext()) {
                    final PacketLocation packetLocation2 = iterator.next();
                    final long n3 = packetLocation2.getTimestamp() - n2;
                    if (n3 > (0)) {
                        list.add(packetLocation);
                        if (n3 > (75)) {
                            packetLocation = packetLocation2;
                     
                            break;
                        }
                    }
                    packetLocation = packetLocation2;
                 
                }
                if (list.isEmpty()) {
                    list.add(packetLocation);
                }
                final Stream<Object> map = list.stream().map((Function<? super Object, ?>)KillAuraQ::lambda$handle$0);
                if (this.values.size() > (10)) {
                    final double average = MathUtil.average(this.values);
                    final Double n4 = map.min(Comparator.comparingDouble(KillAuraQ::lambda$handle$1)).orElse(null);
                    if (n4 != null) {
                        this.values.add(n4);
                        final double deviation = MathUtil.deviation(this.values);
                        if (deviation < 0.1 && average < 0.3) {
                            final String s = "AVG: %.2f DEV: %.3f";
                            final Object[] array = new Object[2];
                            array[0] = average;
                            array[1] = deviation;
                            this.handleViolation(String.format(s, array), 1.25 - deviation * 10.0);
                          
                        }
                        else {
                            this.decreaseVL(0.1);
                        }
                        this.values.poll();
                    }
                
                }
                else {
                    this.values.add(MathUtil.average(map.collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList())));
                }
            }
        }
    }
}
