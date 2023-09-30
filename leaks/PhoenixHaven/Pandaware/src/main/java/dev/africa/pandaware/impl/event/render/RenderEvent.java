package dev.africa.pandaware.impl.event.render;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.utils.math.vector.Vec2i;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@AllArgsConstructor
public class RenderEvent extends Event {
    private final float partialTicks;
    private final ScaledResolution resolution;

    private final Vec2i mousePosition;

    private final Type type;

    public enum Type {
        RENDER_2D, RENDER_3D, FRAME, RENDER_SCREEN
    }
}
