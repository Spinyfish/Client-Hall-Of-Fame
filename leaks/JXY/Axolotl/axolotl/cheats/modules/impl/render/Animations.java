package axolotl.cheats.modules.impl.render;

import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.ModeSetting;

public class Animations extends Module {

    public static ModeSetting mode = new ModeSetting("Mode","Down", "Down", "1.7");

    public Animations() {
        super("Animations", Category.RENDER, true);
        this.addSettings(mode);
        this.setSpecialSetting(mode);
    }

}