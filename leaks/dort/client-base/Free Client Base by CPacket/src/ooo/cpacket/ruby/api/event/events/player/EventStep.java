package ooo.cpacket.ruby.api.event.events.player;

import net.minecraft.entity.Entity;
import ooo.cpacket.ruby.api.event.events.AbstractSkippableEvent;

public class EventStep extends AbstractSkippableEvent {
    private float stepHeight;
    private Entity entity;
    private Type type;

    public EventStep(Entity entity, float stepHeight, Type type) {
        this.entity = entity;
        this.stepHeight = stepHeight;
        this.type = type;
    }

    public float getStepHeight() {
        return stepHeight;
    }

    public void setStepHeight(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public Entity getEntity() {
        return entity;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        PRE, POST
    }
}