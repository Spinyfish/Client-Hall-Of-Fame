package dev.tenacity.event.impl.player.input;

import dev.tenacity.event.Event;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class KeyInputEvent extends Event {

    private final int key;

    public KeyInputEvent(int key) {
        this.key = key;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public int getKey() {
        return key;
    }

}
