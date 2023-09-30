package club.pulsive.impl.event.render;

import club.pulsive.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Render3DEvent extends Event {
    private final float partialTicks;
}
