package cc.swift.module.impl.player;

import cc.swift.events.*;
import cc.swift.module.Module;
import cc.swift.util.ChatUtil;
import cc.swift.value.impl.DoubleValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

public class FastPlaceModule extends Module {

    public final DoubleValue speed = new DoubleValue("Speed", 0D, 0, 4, 1);

    public FastPlaceModule() {
        super("FastPlace",Category.PLAYER);
        this.registerValues(this.speed);
    }

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if(event.getDirection() != EventState.SEND || event.getState() != EventState.PRE || !(event.getPacket() instanceof C08PacketPlayerBlockPlacement)) return;

        mc.rightClickDelayTimer = this.speed.getValue().intValue();
    };
}
