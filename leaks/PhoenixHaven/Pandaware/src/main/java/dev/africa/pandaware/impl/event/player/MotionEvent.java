package dev.africa.pandaware.impl.event.player;

import dev.africa.pandaware.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class MotionEvent extends Event {
    private double x, y, z;
    private float yaw, pitch;

    private boolean onGround;

    private EventState eventState;
}
