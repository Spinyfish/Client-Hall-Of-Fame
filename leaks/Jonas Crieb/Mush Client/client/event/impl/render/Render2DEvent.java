package client.event.impl.render;

import client.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@AllArgsConstructor
public final class Render2DEvent implements Event {
    private final ScaledResolution scaledResolution;
    private final float partialTicks;
}
