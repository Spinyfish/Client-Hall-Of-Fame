/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.event;

import net.minecraft.entity.EntityLivingBase;
import wtf.monsoon.api.event.Event;

public class EventPlayerHurtSound
extends Event {
    private final EntityLivingBase entity;

    public EventPlayerHurtSound(EntityLivingBase entityLivingBase) {
        this.entity = entityLivingBase;
    }

    public EntityLivingBase getEntity() {
        return this.entity;
    }
}

