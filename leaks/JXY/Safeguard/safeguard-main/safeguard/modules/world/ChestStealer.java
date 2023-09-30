package intentions.modules.world;

import intentions.Client;
import intentions.modules.Module;
import intentions.settings.BooleanSetting;
import intentions.settings.NumberSetting;
import intentions.settings.Setting;

public class ChestStealer extends Module {
  public static NumberSetting stealSpeed = new NumberSetting("Time (MS)", 250.0D, 0.0D, 1000.0D, 10.0D);
  
  public static BooleanSetting autoSteal = new BooleanSetting("AutoSteal", false);
  
  public static BooleanSetting autoClose = new BooleanSetting("AutoClose", false);
  
  public ChestStealer() {
    super("ChestStealer", 0, Category.WORLD, "Steals from chests", false);
    addSettings(stealSpeed, autoSteal, autoClose);
  }
}
