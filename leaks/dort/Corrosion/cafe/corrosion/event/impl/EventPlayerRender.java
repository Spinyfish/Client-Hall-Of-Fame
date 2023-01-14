package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;
import cafe.corrosion.event.attribute.EventCancellable;
import net.minecraft.client.entity.AbstractClientPlayer;

public class EventPlayerRender extends Event implements EventCancellable {
    private AbstractClientPlayer player;
    private boolean pre;

    public void setCancelled(boolean cancelled) {
        this.cancelled = true;
    }

    public EventPlayerRender(AbstractClientPlayer player, boolean pre) {
        this.player = player;
        this.pre = pre;
    }

    public void setPlayer(AbstractClientPlayer player) {
        this.player = player;
    }

    public void setPre(boolean pre) {
        this.pre = pre;
    }

    public AbstractClientPlayer getPlayer() {
        return this.player;
    }

    public boolean isPre() {
        return this.pre;
    }
}
