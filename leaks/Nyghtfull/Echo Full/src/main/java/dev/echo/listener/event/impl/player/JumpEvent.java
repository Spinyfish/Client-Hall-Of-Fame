package dev.echo.listener.event.impl.player;

import dev.echo.listener.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JumpEvent extends Event {
    private float yaw;
}
