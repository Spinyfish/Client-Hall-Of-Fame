package org.crystalpvp.anticheat.api.event.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

@Getter
public class PlayerAlertEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST;

    static {
        HANDLER_LIST = new HandlerList();
    }

    private final AlertType alertType;
    private final Player player;
    private final String checkName;
    private final String extra;
    @Setter
    private boolean cancelled;

    public PlayerAlertEvent(AlertType alertType, Player player, String checkName, String extra) {
        this.alertType = alertType;
        this.player = player;
        this.checkName = checkName;
        this.extra = extra;
    }

    public HandlerList getHandlers() {
        return PlayerAlertEvent.HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return PlayerAlertEvent.HANDLER_LIST;
    }

}
