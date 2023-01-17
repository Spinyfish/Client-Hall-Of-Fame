package com.alan.clients.module.impl.combat.criticals;

import com.alan.clients.module.impl.combat.Criticals;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.AttackEvent;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.value.Mode;
import com.alan.clients.util.packet.PacketUtil;
import util.time.StopWatch;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class PacketCriticals extends Mode<Criticals> {

    private final NumberValue delay = new NumberValue("Delay", this, 500, 0, 1000, 1);

    private final double[] VALUES = new double[]{0.0005D, 0.0001D};
    private final StopWatch stopwatch = new StopWatch();
    private boolean attacked;

    public PacketCriticals(String name, Criticals parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<AttackEvent> onAttack = event -> {

        attacked = true;
        mc.thePlayer.onCriticalHit(event.getTarget());

        for (final double d : VALUES) {
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ, false));
        }
    };
}
