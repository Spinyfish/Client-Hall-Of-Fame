package club.mineman.antigamingchair.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAlertEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST;

    static {
        HANDLER_LIST = new HandlerList();
    }

    private final AlertType alertType;
    private final Player player;
    private final String alert;
    private boolean cancelled;

    public PlayerAlertEvent(final AlertType alertType, final Player player, final String alert) {
        this.alertType = alertType;
        this.player = player;
        this.alert = alert;
    }

    public static HandlerList getHandlerList() {
        return PlayerAlertEvent.HANDLER_LIST;
    }

    public HandlerList getHandlers() {
        return PlayerAlertEvent.HANDLER_LIST;
    }

    public AlertType getAlertType() {
        return this.alertType;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getAlert() {
        return this.alert;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public enum AlertType {
        RELEASE("RELEASE", 0),
        EXPERIMENTAL("EXPERIMENTAL", 1),
        DEVELOPMENT("DEVELOPMENT", 2);

        AlertType(final String s, final int n) {
        }
    }
}
