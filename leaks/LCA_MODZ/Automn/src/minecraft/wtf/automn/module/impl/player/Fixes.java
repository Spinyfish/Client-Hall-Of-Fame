package wtf.automn.module.impl.player;

import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;
import wtf.automn.module.settings.SettingBoolean;

@ModuleInfo(name = "fixes", displayName = "Fixes", category = Category.PLAYER)
public class Fixes extends Module {
    public final SettingBoolean mouseDelayFix = new SettingBoolean("mouseDelayFix", true, "MouseDelayFix", this, "Fixing Mouse Delay");

}
