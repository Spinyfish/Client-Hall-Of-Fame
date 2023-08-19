/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 15:24
 */

package cc.swift.events;

import dev.codeman.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class MovementInputEvent extends Event {
    private float moveForward, moveStrafe;
    private boolean jump, sneak;
    private double sneakSlowDown;
}
