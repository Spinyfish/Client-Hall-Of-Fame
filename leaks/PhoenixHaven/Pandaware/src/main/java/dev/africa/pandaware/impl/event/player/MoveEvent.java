package dev.africa.pandaware.impl.event.player;

import dev.africa.pandaware.api.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MoveEvent extends Event {
    public double x, y, z;
}
