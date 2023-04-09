package tech.dort.dortware.impl.events;

import net.minecraft.entity.EntityLivingBase;
import tech.dort.dortware.api.event.Event;

public class RenderPlayerTagEvent extends Event {

    private final EntityLivingBase entity;
    private final double x;
    private final double y;

    public EntityLivingBase getEntity() {
        return entity;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    private final double z;

    public RenderPlayerTagEvent(EntityLivingBase entity, double x, double y, double z) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
