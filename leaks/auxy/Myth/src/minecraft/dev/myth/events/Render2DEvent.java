/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 14:12
 */
package dev.myth.events;

import dev.codeman.eventbus.Event;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent extends Event {

    @Getter private final ScaledResolution scaledResolution;

    public Render2DEvent(ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
    }

}
