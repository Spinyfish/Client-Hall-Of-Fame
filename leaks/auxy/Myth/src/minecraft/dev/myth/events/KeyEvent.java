/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 14:12
 */
package dev.myth.events;

import dev.codeman.eventbus.Event;
import lombok.Getter;

public class KeyEvent extends Event {

    @Getter private final int key;

    public KeyEvent(int key) {
        this.key = key;
    }
}
