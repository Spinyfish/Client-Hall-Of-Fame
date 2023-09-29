package me.jinthium.straight.impl.components;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.component.Component;
import me.jinthium.straight.impl.event.movement.PlayerMoveUpdateEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;

public final class PacketlessDamageComponent extends Component {

    private static boolean active;
    private static float timer;
    private static int jumps;

    public static boolean isActive() {
        return active;
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event ->  {

        if(!event.isPre())
            return;

        if (active) {
            if (jumps < 3) {
                mc.timer.timerSpeed = timer;

                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    jumps++;
                }
                event.setOnGround(false);
            } else if (mc.thePlayer.onGround) {
                mc.timer.timerSpeed = 1.0F;
                active = false;
                timer = 1.0F;
                jumps = 0;
            }
        }
    };

    @Callback
    final EventCallback<PlayerMoveUpdateEvent> playerMoveUpdateEventEventCallback = event -> {
        if (active) {
            event.setForward(0);
            event.setStrafe(0);
        }
    };

    public static void setActive(final float timer) {
        PacketlessDamageComponent.active = true;
        PacketlessDamageComponent.timer = timer;
        jumps = 0;
    }
}
