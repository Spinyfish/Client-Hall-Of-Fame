package me.jinthium.straight.impl.modules.combat.velocity;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.combat.Test;
import me.jinthium.straight.impl.modules.combat.Velocity;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

@ModeInfo(name = "Cancel", parent = Velocity.class)
public class CancelVelo extends ModuleMode<Velocity> {


    @Callback
    final EventCallback<PacketEvent> packetEventCallback = event -> {
        if(event.getPacketState() == PacketEvent.PacketState.RECEIVING){
            if(event.getPacket() instanceof S12PacketEntityVelocity s12)
                if(s12.getEntityID() == mc.thePlayer.getEntityId())
                    event.cancel();

            if(event.getPacket() instanceof S27PacketExplosion)
                event.cancel();
        }
    };
}
