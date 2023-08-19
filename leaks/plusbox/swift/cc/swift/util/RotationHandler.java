package cc.swift.util;

import cc.swift.events.RotationEvent;
import cc.swift.events.UpdateWalkingPlayerEvent;
import dev.codeman.eventbus.EventPriority;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import lombok.Getter;

@Getter
public final class RotationHandler {

    private float yaw, pitch, lastYaw, lastPitch;

    @Handler(EventPriority.LOWEST)
    public final Listener<RotationEvent> rotationEventListener = event -> {
        yaw = event.getYaw();
        pitch = event.getPitch();
    };

    @Handler(EventPriority.LOW)
    public final Listener<UpdateWalkingPlayerEvent> updateWalkingPlayerEventListener = event -> {
        lastYaw = yaw;
        lastPitch = pitch;
        event.setYaw(yaw);
        event.setPitch(pitch);
    };

}
