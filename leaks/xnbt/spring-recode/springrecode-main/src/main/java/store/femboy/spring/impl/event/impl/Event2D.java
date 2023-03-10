package store.femboy.spring.impl.event.impl;

import net.minecraft.client.gui.ScaledResolution;
import store.femboy.spring.impl.event.Event;

public final class Event2D extends Event {
    private final ScaledResolution sr;

    public Event2D(ScaledResolution sr) {
        this.sr = sr;
    }

    public ScaledResolution getSr() {
        return sr;
    }
}
