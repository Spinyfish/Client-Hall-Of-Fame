package me.jinthium.straight.impl.modules.combat.velocity;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.combat.Velocity;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

@ModeInfo(name = "Reverse", parent = Velocity.class)
public class ReverseVelo extends ModuleMode<Velocity> {

    @Callback
    final EventCallback<PacketEvent> packetEventCallback = event -> {
        if(event.getPacketState() == PacketEvent.PacketState.RECEIVING){
            if(event.getPacket() instanceof S12PacketEntityVelocity s12){
                if(s12.getEntityID() != mc.thePlayer.getEntityId())
                    return;

                s12.motionX += StrictMath.cos(MovementUtil.getMovementDirection());
                s12.motionZ += -StrictMath.sin(MovementUtil.getMovementDirection1());
                event.setPacket(s12);
            }

            if(event.getPacket() instanceof S27PacketExplosion s27){
                s27.posX = -s27.posX;
                s27.posZ = -s27.posZ;
                event.setPacket(s27);
            }
        }
    };
}
