/* November.lol © 2023 */
package lol.november.feature.module.impl.visual;

import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "Animations",
  description = "Does cool sword animations",
  category = Category.VISUAL
)
public class AnimationsModule extends Module {

  public final Setting<Mode> mode = new Setting<>("Mode", Mode.OLD_MC);
  public final Setting<Boolean> old = new Setting<>("1.7", true);
  public final Setting<Boolean> oddSwing = new Setting<>("Odd Swing", false);

  public enum Mode {
    OLD,
    OLD_MC {
      @Override
      public String toString() {
        return "1.7";
      }
    },
    SMOOTH,
  }
}
