package me.levansj01.verus.api.events;

import lombok.Getter;
import lombok.Setter;
import me.levansj01.verus.api.check.Check;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class ViolationEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Check check;
    private final int violations;

    private boolean cancelled;

    @Deprecated
    public ViolationEvent(Player player, String checkType, String checkSubType, int violations) {
        this(player, new Check(checkType, checkSubType, null), violations);
    }

    public ViolationEvent(Player player, Check check, int violations) {
        super(true);
        this.player = player;
        this.check = check;
        this.violations = violations;
    }

    @Deprecated
    public String getCheckType() {
        return this.check.getType();
    }

    @Deprecated
    public String getSubType() {
        return this.check.getSubType();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
