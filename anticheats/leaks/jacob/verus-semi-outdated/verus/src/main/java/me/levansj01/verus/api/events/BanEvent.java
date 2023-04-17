package me.levansj01.verus.api.events;

import lombok.Getter;
import lombok.Setter;
import me.levansj01.verus.api.check.Check;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Deprecated
@Getter
@Setter
public class BanEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Check check;
    private boolean cancelled;

    @Deprecated
    public BanEvent(Player player, String checkType, String checkSubType) {
        this(player, new Check(checkType, checkSubType, null));
    }

    public BanEvent(Player player, Check check) {
        this.player = player;
        this.check = check;
    }

    @Deprecated
    public String getCheckType() {
        return this.check.getType();
    }

    @Deprecated
    public String getCheckSubType() {
        return this.check.getSubType();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
