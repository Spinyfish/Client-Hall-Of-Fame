/* November.lol © 2023 */
package lol.november.feature.module.impl.ghost;

import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.input.EventAttackReach;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "Reach",
  description = "Reaches further than you can in vanilla",
  category = Category.GHOST
)
public class ReachModule extends Module {

  private final Setting<Double> attack = new Setting<>(
    "Reach",
    3.0,
    0.01,
    1.0,
    6.0
  );

  @Subscribe
  private final Listener<EventAttackReach> attackReach = event -> {
    event.setReach(attack.getValue());
  };
}
