package club.mineman.antigamingchair.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerBanWaveEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST;

    static {
        HANDLER_LIST = new HandlerList();
    }

    private final Player player;
    private final String reason;
    private boolean cancelled;

    public PlayerBanWaveEvent(final Player player, final String reason) {
        this.player = player;
        this.reason = reason;
    }

    public static HandlerList getHandlerList() {
        return PlayerBanWaveEvent.HANDLER_LIST;
    }

    public HandlerList getHandlers() {
        return PlayerBanWaveEvent.HANDLER_LIST;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getReason() {
        return this.reason;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
