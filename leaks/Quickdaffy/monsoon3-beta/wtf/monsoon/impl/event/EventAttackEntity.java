/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.event;

import net.minecraft.entity.Entity;
import wtf.monsoon.api.event.Event;

public class EventAttackEntity
extends Event {
    private Entity target;

    public EventAttackEntity(Entity target) {
        this.target = target;
    }

    public Entity getTarget() {
        return this.target;
    }
}

