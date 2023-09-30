package me.jinthium.straight.impl.modules.player.nofall;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.components.FallDistanceComponent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.player.NoFall;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModeInfo(name = "Reduce", parent = NoFall.class)
public class ReduceNoFall extends ModuleMode<NoFall> {
    private final BooleanSetting packet = new BooleanSetting("Packet", true);

    public ReduceNoFall(){
        this.registerSettings(packet);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> onMotion = event -> {
        if (MovementUtil.canFall() && FallDistanceComponent.distance > 4) {
            if (this.packet.isEnabled()) {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer(true));
            } else {
                event.setOnGround(true);
            }

            FallDistanceComponent.distance = 0;
        }
    };
}
