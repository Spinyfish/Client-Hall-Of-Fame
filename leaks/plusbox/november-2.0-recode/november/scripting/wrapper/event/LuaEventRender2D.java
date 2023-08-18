/* November.lol © 2023 */
package lol.november.scripting.wrapper.event;

import lol.november.listener.event.render.EventRender2D;
import lol.november.protect.DontObfuscate;
import lombok.Getter;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
@DontObfuscate
public class LuaEventRender2D {

  private final int width, height, scale;
  private final float partialTicks;

  public LuaEventRender2D(EventRender2D event) {
    width = event.getResolution().getScaledWidth();
    height = event.getResolution().getScaledHeight();
    scale = event.getResolution().getScaleFactor();
    partialTicks = event.getPartialTicks();
  }
}
