package tech.dort.dortware.impl.events;

import net.minecraft.entity.Entity;
import tech.dort.dortware.api.event.Event;

public class EntityStepEvent extends Event {

    private float height;

    private final boolean pre;

    private final Entity entity;

    public EntityStepEvent(float height, boolean pre, Entity entity) {
        this.height = height;
        this.pre = pre;
        this.entity = entity;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    public boolean isPre() {
        return pre;
    }

    public Entity getEntity() {
        return entity;
    }
}
