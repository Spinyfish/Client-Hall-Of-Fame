package cc.swift.events;

import dev.codeman.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RenderScreenEvent extends Event {
    private final int mouseX, mouseY;
    private final float partialTicks;
}
