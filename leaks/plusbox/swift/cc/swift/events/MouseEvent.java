package cc.swift.events;

import dev.codeman.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MouseEvent extends Event {
    private final MouseAction action;
    private final int mouseX, mouseY, mouseButton;

    public enum MouseAction {
        CLICK,
        RELEASE
    }
}
