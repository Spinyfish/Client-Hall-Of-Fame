/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.event;

import net.minecraft.entity.Entity;
import wtf.monsoon.api.event.Event;

public class EventRenderVanillaNametag
extends Event {
    private final Entity entity;

    public EventRenderVanillaNametag(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}

