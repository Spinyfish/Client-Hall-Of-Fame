package byron.Mono.module.impl.visual;

import byron.Mono.Mono;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;

@ModuleInterface(name = "Fullbright", description = "See everything clearly!", category = Category.Visual)
public class Fullbright extends Module {

    @Override
    public void onEnable() {
        super.onEnable();
        mc.gameSettings.gammaSetting = 1000.0F;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.gameSettings.gammaSetting = 1.0F;
    }


}
