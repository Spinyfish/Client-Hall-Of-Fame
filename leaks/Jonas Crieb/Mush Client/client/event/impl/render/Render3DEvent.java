package client.event.impl.render;

import client.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class Render3DEvent implements Event {
    public final float partialTicks;
}
