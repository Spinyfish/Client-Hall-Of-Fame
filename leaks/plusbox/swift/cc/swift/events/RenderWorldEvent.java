/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 19:43
 */

package cc.swift.events;

import dev.codeman.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RenderWorldEvent extends Event {
    private final float partialTicks;
}
