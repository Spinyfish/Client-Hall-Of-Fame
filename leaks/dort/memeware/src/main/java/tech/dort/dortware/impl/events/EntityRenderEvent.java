package tech.dort.dortware.impl.events;

import net.minecraft.entity.Entity;
import tech.dort.dortware.api.event.Event;

public class EntityRenderEvent extends Event {

    private final boolean isPre;
    public float var17;
    public float var16;
    public float var14;
    public float var12;
    public float var201;

    public EntityRenderEvent(boolean isPre, Entity entity, float var17, float var16, float var14, float var12, float var201) {
        this.isPre = isPre;
        this.var17 = var17;
        this.var16 = var16;
        this.var14 = var14;
        this.var12 = var12;
        this.var201 = var201;
        this.entity = entity;
    }


    private final Entity entity;

    public EntityRenderEvent(Entity entity) {
        this.isPre = true;
        this.entity = entity;
    }

    public EntityRenderEvent(boolean isPre, Entity entity) {

        this.isPre = isPre;
        this.entity = entity;
    }

    public boolean isPre() {
        return isPre;
    }

    public Entity getEntity() {
        return entity;
    }
}
