package cc.swift.events;

import dev.codeman.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor @Getter @Setter
public final class TimeEvent extends Event {
    private double time; // omgard
    private boolean limitTicks;
}
