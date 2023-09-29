
package me.nekoWare.client.event.events;

import me.nekoWare.client.event.Event;
import net.minecraft.entity.EntityLivingBase;

public class RenderEntityEvent extends Event {

    private final EntityLivingBase entityLivingBase;
    public boolean pre;

    public RenderEntityEvent(final EntityLivingBase entityLivingBase, boolean pre) {
        this.entityLivingBase = entityLivingBase;
        this.pre = pre;
    }

    public EntityLivingBase getEntityLivingBase() {
        return entityLivingBase;
    }

}
