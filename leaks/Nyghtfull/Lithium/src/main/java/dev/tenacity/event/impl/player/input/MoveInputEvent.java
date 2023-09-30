package dev.tenacity.event.impl.player.input;

import dev.tenacity.event.Event;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class MoveInputEvent extends Event {
    private float forward, strafe;
    private boolean jump, sneak;
    private double sneakSlowDownMultiplier;
}