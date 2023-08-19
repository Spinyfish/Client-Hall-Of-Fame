/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 19:25
 */

package cc.swift.events;

import dev.codeman.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

@AllArgsConstructor
@Getter
public class RenderOverlayEvent extends Event {
    private ScaledResolution scaledResolution;
    private float partialTicks;
}
