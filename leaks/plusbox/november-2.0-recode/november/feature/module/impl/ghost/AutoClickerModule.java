/* November.lol © 2023 */
package lol.november.feature.module.impl.ghost;

import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "AutoClicker",
  description = "Automatically clicks for you",
  category = Category.GHOST
)
public class AutoClickerModule extends Module {

  private final Setting<Integer> minCps = new Setting<>(
    "Min CPS",
    12,
    1,
    0,
    20
  );
  private final Setting<Integer> maxCps = new Setting<>(
    "Max CPS",
    16,
    1,
    0,
    20
  );

  private final Setting<Boolean> autoBlock = new Setting<>("Auto Block", true);
}
