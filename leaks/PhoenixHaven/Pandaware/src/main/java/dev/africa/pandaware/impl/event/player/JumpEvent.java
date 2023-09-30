package dev.africa.pandaware.impl.event.player;

import dev.africa.pandaware.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class JumpEvent extends Event {
    private float yaw;
}
