package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Setter
@Getter
@AllArgsConstructor
public class IsOutlineEvent implements Event {
    private boolean overrideOutline;
    private Entity entity;
}