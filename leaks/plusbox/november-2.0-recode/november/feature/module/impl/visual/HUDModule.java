/* November.lol © 2023 */
package lol.november.feature.module.impl.visual;

import lol.november.November;
import lol.november.feature.hud.HUDElement;
import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.render.EventRender2D;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "HUD",
  description = "Draws an overlay for November client",
  category = Category.VISUAL
)
public class HUDModule extends Module {

  public HUDModule() {
    // default to on
    setState(true);
  }

  @Subscribe
  private final Listener<EventRender2D> render2D = event -> {
    if (
      mc.gameSettings.hideGUI || mc.gameSettings.showDebugProfilerChart
    ) return;

    for (HUDElement element : November.instance().hud().values()) {
      element.render(event.getResolution(), event.getPartialTicks());
    }
    //Fonts.poppins.drawString("Hello, world!", 20, 20, -1);
  };
}
