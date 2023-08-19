package dev.thread.impl.event;

import dev.thread.api.event.Event;
import dev.thread.api.event.EventStage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MotionEvent extends Event {
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;

    public MotionEvent(EventStage stage, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        super(stage);
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }
}
