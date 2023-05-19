package club.marsh.bloom.impl.events;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public abstract class Event {
    protected boolean cancelled;
}
