package dev.echo.listener.event.impl.game;

import dev.echo.listener.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author cedo
 * @since 03/30/2022
 */
@AllArgsConstructor
@Getter
public class RenderTickEvent extends Event {
    private final float ticks;
}
