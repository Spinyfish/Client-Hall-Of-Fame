package club.pulsive.impl.util.network;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.event.player.WorldLoadEvent;
import lombok.Getter;
import net.minecraft.network.play.client.C03PacketPlayer;

@Getter
public enum BalanceUtil {
    INSTANCE;

    private long balance, lastPacket;

    @EventHandler
    private final Listener<WorldLoadEvent> worldLoadEventListener = event ->{
        balance = lastPacket = 0;
    };

    @EventHandler
    private final Listener<PacketEvent> packetEventListener = event -> {
        if(event.getEventState() != PacketEvent.EventState.SENDING) return;
        if(event.getPacket() instanceof C03PacketPlayer){
            if (lastPacket == 0) lastPacket = System.currentTimeMillis();
            long delay = System.currentTimeMillis() - lastPacket;
            balance += event.isCancelled() ? -delay : 50 - delay;
            lastPacket = System.currentTimeMillis();
        }
    };
}