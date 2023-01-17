package com.alan.clients.newevent.impl.render;

import com.alan.clients.newevent.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;

@Getter
@AllArgsConstructor
public final class EntityRenderEvent implements Event {

    private final boolean pre;
    private final Entity entity;

}
