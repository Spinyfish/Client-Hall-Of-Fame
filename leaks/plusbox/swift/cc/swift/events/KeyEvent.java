/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 19:17
 */

package cc.swift.events;

import dev.codeman.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class KeyEvent extends Event {
    private int key;
}
