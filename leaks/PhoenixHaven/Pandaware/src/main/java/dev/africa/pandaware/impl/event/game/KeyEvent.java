package dev.africa.pandaware.impl.event.game;

import dev.africa.pandaware.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeyEvent extends Event {
    private final int key;
    private final Type type;

    public enum Type {
        NULL_SCREEN, NOT_NULL_SCREEN
    }

    public int getKey() {
        return key;
    }

    public Type getType() {
        return type;
    }
}
