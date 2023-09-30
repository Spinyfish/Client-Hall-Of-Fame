package dev.africa.pandaware.impl.event.player;

import dev.africa.pandaware.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Setter
@Getter
@AllArgsConstructor
public class AttackEvent extends Event {
    private final Entity entity;
    private Event.EventState eventState;
}