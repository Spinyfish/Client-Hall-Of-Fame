/**
 * @project hakarware
 * @author CodeMan
 * @at 26.07.23, 22:45
 */

package cc.swift.events;

import dev.codeman.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class ItemSlowDownEvent extends Event {
    private float forward, strafe;
    private boolean sprinting;
}
