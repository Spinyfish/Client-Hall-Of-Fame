/**
 * @project Myth
 * @author CodeMan
 * @at 25.08.22, 21:09
 */
package dev.myth.events;

import dev.codeman.eventbus.Event;
import lombok.*;

@AllArgsConstructor
public class SlowDownEvent extends Event {

    @Getter @Setter private float strafeMultiplier, forwardMultiplier;

}
