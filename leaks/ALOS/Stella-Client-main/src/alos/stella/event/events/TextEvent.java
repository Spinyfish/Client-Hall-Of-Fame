package alos.stella.event.events;

import alos.stella.event.Event;
import org.jetbrains.annotations.Nullable;

public final class TextEvent extends Event {
    @Nullable
    private String text;

    @Nullable
    public final String getText() {
        return this.text;
    }

    public final void setText(@Nullable String var1) {
        this.text = var1;
    }

    public TextEvent(@Nullable String text) {
        this.text = text;
    }
}