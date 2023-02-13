package juul.event;

import net.minecraft.entity.Entity;

public class EventClickMouse extends Event<EventClickMouse>{

    public boolean contact;
    public Entity entity;

    public void setEventData(boolean contact, Entity entity) {
        this.contact = contact;
        this.entity = entity;
    }
}
