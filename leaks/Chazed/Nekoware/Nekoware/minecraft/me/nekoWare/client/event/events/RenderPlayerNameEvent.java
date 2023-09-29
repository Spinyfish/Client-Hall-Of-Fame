
package me.nekoWare.client.event.events;

import me.nekoWare.client.event.Event;
import net.minecraft.entity.Entity;

public class RenderPlayerNameEvent extends Event {
    public Entity p;

    public RenderPlayerNameEvent(final Entity p2) {
        this.p = p2;
    }
}
