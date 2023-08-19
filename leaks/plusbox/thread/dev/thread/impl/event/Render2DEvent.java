package dev.thread.impl.event;

import dev.thread.api.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.ScaledResolution;

@RequiredArgsConstructor
@Getter
public class Render2DEvent extends Event {
    private final ScaledResolution scaledResolution;
    private final float partialTicks;
}
