package cc.swift.events;

import dev.codeman.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public final class StrafeEvent extends Event {

    private float strafe;
    private float forward;
    private float friction;
    private float yaw;

}
