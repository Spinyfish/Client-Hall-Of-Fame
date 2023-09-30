package dev.tenacity.event.impl.player.movement.correction;

import dev.tenacity.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JumpEvent extends Event {
    private float yaw;
}
