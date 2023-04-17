package club.mineman.antigamingchair.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BanWaveEvent extends Event {
    private static final HandlerList HANDLER_LIST;

    static {
        HANDLER_LIST = new HandlerList();
    }

    private final String instigator;

    public BanWaveEvent(final String instigator) {
        this.instigator = instigator;
    }

    public static HandlerList getHandlerList() {
        return BanWaveEvent.HANDLER_LIST;
    }

    public HandlerList getHandlers() {
        return BanWaveEvent.HANDLER_LIST;
    }

    public String getInstigator() {
        return this.instigator;
    }
}
