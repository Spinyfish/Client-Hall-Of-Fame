package dev.africa.pandaware.impl.event.player;

import dev.africa.pandaware.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Setter
@Getter
@AllArgsConstructor
public class StepEvent extends Event {

    private float stepHeight;
    private Entity entity;
    private boolean shouldStep;
    private EventState state;
}
