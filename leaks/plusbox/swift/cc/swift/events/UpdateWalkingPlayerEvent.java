/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 19:17
 */

package cc.swift.events;

import dev.codeman.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class UpdateWalkingPlayerEvent extends Event {
    EventState state;
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround, sneaking, sprinting;
    private double lastX, lastY, lastZ;
    private float lastYaw, lastPitch;
}
