package dev.africa.pandaware.impl.event.game;

import dev.africa.pandaware.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MouseEvent extends Event {
    private int mouseX, mouseY, mouseButton;
    private Type type;

    public enum Type {
        CLICK,
        CLICK_MOVE,
        RELEASED,
        NO_SCREEN
    }
}
