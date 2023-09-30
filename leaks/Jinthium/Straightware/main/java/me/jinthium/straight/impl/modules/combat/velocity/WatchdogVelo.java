package me.jinthium.straight.impl.modules.combat.velocity;


import best.azura.irc.utils.Wrapper;
import com.google.common.base.Stopwatch;
import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.components.BlinkComponent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.combat.Velocity;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import java.util.LinkedList;
import java.util.List;

@ModeInfo(name = "Watchdog", parent = Velocity.class)
public class WatchdogVelo extends ModuleMode<Velocity> {


    private final List<Packet<?>> packetDeque = new LinkedList<>();
    private boolean spike;
    private final TimerUtil stopwatch = new TimerUtil();

    @Callback
    final EventCallback<PacketEvent> packetEventCallback = event -> {
        if(event.getPacketState() == PacketEvent.PacketState.RECEIVING) {
            if (event.getPacket() instanceof S12PacketEntityVelocity s12) {
                if (s12.getEntityID() == mc.thePlayer.getEntityId()) {
                    s12.setMotionX((int) (mc.thePlayer.motionX * 8000.0D));
                    s12.setMotionZ((int) (mc.thePlayer.motionZ * 8000.0D));
                }
            }
        }
    };
}
