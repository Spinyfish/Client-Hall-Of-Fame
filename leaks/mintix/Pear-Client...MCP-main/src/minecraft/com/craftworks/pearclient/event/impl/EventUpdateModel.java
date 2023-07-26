package com.craftworks.pearclient.event.impl;

import com.craftworks.pearclient.event.Event;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;

public class EventUpdateModel extends Event {
    public Entity entity;
    public ModelPlayer modelPlayer;

    public EventUpdateModel(Entity entity, ModelPlayer modelPlayer) {
        this.entity = entity;
        this.modelPlayer = modelPlayer;
    }
}
