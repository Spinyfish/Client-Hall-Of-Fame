/* November.lol © 2023 */
package lol.november.feature.module.impl.ghost;

import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.input.EventDelayAttack;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "NoHitDelay",
  description = "Removes the attack delay",
  category = Category.GHOST
)
public class NoHitDelayModule extends Module {

  @Subscribe
  private final Listener<EventDelayAttack> delayAttack = event -> {
    event.setLeftClickCounter(0);
  };
}
