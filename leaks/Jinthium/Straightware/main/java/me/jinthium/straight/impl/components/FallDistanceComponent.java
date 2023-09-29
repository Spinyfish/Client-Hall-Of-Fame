package me.jinthium.straight.impl.components;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.component.Component;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.render.Render2DEvent;

public final class FallDistanceComponent extends Component {

    public static float distance;
    private float lastDistance;


    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(!event.isPre())
            return;

        final float fallDistance = mc.thePlayer.fallDistance;

        if (fallDistance == 0) {
            distance = 0;
        }

        distance += fallDistance - lastDistance;
        lastDistance = fallDistance;
    };
}
