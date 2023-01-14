package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;
import net.minecraft.entity.EntityLivingBase;

public class EventRenderEntity extends Event {
    private EntityLivingBase entity;
    private boolean pre;

    public EventRenderEntity(EntityLivingBase entity, boolean pre) {
        this.entity = entity;
        this.pre = pre;
    }

    public EntityLivingBase getEntity() {
        return this.entity;
    }

    public boolean isPre() {
        return this.pre;
    }

    public void setEntity(EntityLivingBase entity) {
        this.entity = entity;
    }

    public void setPre(boolean pre) {
        this.pre = pre;
    }
}
