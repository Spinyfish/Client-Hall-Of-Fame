package com.alan.clients.newevent.impl.render;


import com.alan.clients.newevent.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Getter
@Setter
@AllArgsConstructor
public final class RenderNameEvent extends CancellableEvent {

    private final Entity entity;

}
