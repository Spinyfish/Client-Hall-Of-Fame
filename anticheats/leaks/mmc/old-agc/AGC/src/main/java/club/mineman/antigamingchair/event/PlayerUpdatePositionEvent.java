package club.mineman.antigamingchair.event;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerUpdatePositionEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private boolean cancel;
    private Location from;
    private Location to;

    public PlayerUpdatePositionEvent(final Player player, final Location from, final Location to) {
        super(player);
        this.cancel = false;
        this.from = from;
        this.to = to;
    }

    public static HandlerList getHandlerList() {
        return PlayerUpdatePositionEvent.handlers;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }

    public Location getFrom() {
        return this.from;
    }

    public void setFrom(final Location from) {
        this.validateLocation(from);
        this.from = from;
    }

    public Location getTo() {
        return this.to;
    }

    public void setTo(final Location to) {
        this.validateLocation(to);
        this.to = to;
    }

    private void validateLocation(final Location loc) {
        Preconditions.checkArgument(loc != null, "Cannot use null location!");
        Preconditions.checkArgument(loc.getWorld() != null, "Cannot use null location with null world!");
    }

    public HandlerList getHandlers() {
        return PlayerUpdatePositionEvent.handlers;
    }
}
