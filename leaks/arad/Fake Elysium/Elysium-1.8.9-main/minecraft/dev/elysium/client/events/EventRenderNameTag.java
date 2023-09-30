package dev.elysium.client.events;

import dev.elysium.base.events.types.Event;
import net.minecraft.entity.EntityLivingBase;

public class EventRenderNameTag extends Event {
    public EntityLivingBase entity;
    public String nametagname;

    public EventRenderNameTag(EntityLivingBase entity, String nametagname){
        this.entity = entity;
        this.nametagname = nametagname;
    }

    public EntityLivingBase getEntity(){
        return this.entity;
    }

    public String getNameTagName(){
        return this.nametagname;
    }

    public void setRenderedName(String nametagname){
        this.nametagname = nametagname;
    }
}
