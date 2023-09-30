package me.jinthium.straight.impl.event.game;


import me.jinthium.straight.api.event.Event;


public class TickEvent extends Event.StateEvent{
    private final int ticks;

    public TickEvent(int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }
}
