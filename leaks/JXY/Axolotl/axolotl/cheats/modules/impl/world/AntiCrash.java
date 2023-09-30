package axolotl.cheats.modules.impl.world;

import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.NumberSetting;

public class AntiCrash extends Module {

    public NumberSetting packetMax = new NumberSetting("MPPP", 50, 10, 250, 10),
            secMax = new NumberSetting("MPPS", 500, 250, 1000, 50);

    public AntiCrash() {
        super("AntiCrash", Category.WORLD, true);
        this.addSettings(packetMax, secMax);
    }

}
