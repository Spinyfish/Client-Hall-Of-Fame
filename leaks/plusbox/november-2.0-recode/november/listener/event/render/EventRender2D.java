/* November.lol © 2023 */
package lol.november.listener.event.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
@AllArgsConstructor
public class EventRender2D {
  private final ScaledResolution resolution;
  private final float partialTicks;
}
