package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import best.azura.eventbus.events.CancellableEvent;
import lombok.AllArgsConstructor;
import net.minecraft.entity.Entity;

@AllArgsConstructor
public class OutlineColorEvent extends CancellableEvent {
    public int color;
    public final Entity entity;
}
