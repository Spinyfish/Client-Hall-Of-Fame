/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.event.events.rendering;

import Reality.Realii.event.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class EventRenderCape
extends Event {
    private ResourceLocation capeLocation;
    private final EntityPlayer player;

    public EventRenderCape(ResourceLocation capeLocation, EntityPlayer player) {
        this.capeLocation = capeLocation;
        this.player = player;
    }

    public ResourceLocation getLocation() {
        return this.capeLocation;
    }

    public void setLocation(ResourceLocation location) {
        this.capeLocation = location;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }
}

