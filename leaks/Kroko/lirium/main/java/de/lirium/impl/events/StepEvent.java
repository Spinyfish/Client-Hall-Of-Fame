package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Getter
@Setter
@AllArgsConstructor
public class StepEvent implements Event {
    private double stepHeight;
    private Entity entity;
    private State state;

    public enum State {
        PRE, ON_STEP, POST
    }
}