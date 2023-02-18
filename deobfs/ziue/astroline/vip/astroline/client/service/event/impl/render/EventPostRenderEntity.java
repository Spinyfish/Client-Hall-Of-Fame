/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  vip.astroline.client.service.event.Event
 */
package vip.astroline.client.service.event.impl.render;

import net.minecraft.entity.Entity;
import vip.astroline.client.service.event.Event;

public class EventPostRenderEntity
extends Event {
    private Entity ent;

    public EventPostRenderEntity(Entity ent) {
        this.ent = ent;
    }

    public Entity getEntity() {
        return this.ent;
    }
}
