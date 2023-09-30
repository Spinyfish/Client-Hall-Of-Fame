package me.jinthium.straight.impl.modules.player.nofall;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.components.FallDistanceComponent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.player.NoFall;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModeInfo(name = "Packet", parent = NoFall.class)
public class PacketNoFall extends ModuleMode<NoFall> {

    @Callback
    EventCallback<PlayerUpdateEvent> onMotion = event -> {
        if (MovementUtil.canFall() && FallDistanceComponent.distance > 2.5) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer(true));
            FallDistanceComponent.distance = 0;
        }
    };
}
