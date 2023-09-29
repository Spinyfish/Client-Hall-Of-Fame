package me.jinthium.straight.impl.modules.player.nofall;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.components.FallDistanceComponent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.movement.Flight;
import me.jinthium.straight.impl.modules.movement.Longjump;
import me.jinthium.straight.impl.modules.player.NoFall;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import net.minecraft.network.play.client.C03PacketPlayer;

import static net.optifine.reflect.Reflector.Event;

@ModeInfo(name = "Vulcan", parent = NoFall.class)
public class VulcanNoFall extends ModuleMode<NoFall> {
    private boolean fixed;
    private int count;

    @Callback
    final EventCallback<PlayerUpdateEvent> onMotion = event -> {
        if(Client.INSTANCE.getModuleManager().getModule(Flight.class).isEnabled() || Client.INSTANCE.getModuleManager().getModule(Longjump.class).isEnabled()){
        if (event.isPre()) {
            if (FallDistanceComponent.distance > 0 && mc.thePlayer.ticksExisted % 16 == 0) {
                event.setOnGround(true);
                mc.thePlayer.speedInAir = 0.025f;
            } else if (FallDistanceComponent.distance > 0) {
                event.setOnGround(false);
                mc.thePlayer.speedInAir = 0.02f;
            }
        }
        }
    };
}