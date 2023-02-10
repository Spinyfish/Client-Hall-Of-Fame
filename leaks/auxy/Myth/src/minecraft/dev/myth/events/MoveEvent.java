/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 22:54
 */
package dev.myth.events;

import dev.codeman.eventbus.Event;
import lombok.Getter;
import lombok.Setter;

public class MoveEvent extends Event {

    @Getter @Setter private double x, y, z;

    public MoveEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
