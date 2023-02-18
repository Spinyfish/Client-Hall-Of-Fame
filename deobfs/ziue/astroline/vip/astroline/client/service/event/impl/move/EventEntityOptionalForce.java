/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.Vec3
 *  vip.astroline.client.service.event.Event
 */
package vip.astroline.client.service.event.impl.move;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import vip.astroline.client.service.event.Event;

public class EventEntityOptionalForce
extends Event {
    public Entity entity;
    public Vec3 minor;

    public EventEntityOptionalForce(Entity e, Vec3 vec3) {
        this.entity = e;
        this.minor = vec3;
    }

    public Vec3 getMinor() {
        return this.minor;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
