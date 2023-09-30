package dev.echo.listener.event.impl.game;

import dev.echo.listener.event.Event;
import lombok.Getter;


@Getter
public class TickEvent extends Event {

    private final int ticks;

    public TickEvent(int ticks) {
        this.ticks = ticks;
    }

}
