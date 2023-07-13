package me.r.touchgrass.module.modules.player;

import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;

/**
 * Created by r on 22/12/2021
 */

@Info(name = "§cPanic", description = "Disables all client functionality. Needs a restart if activated.", category = Category.Player)
public class Panic extends Module {

    public Panic() {}

    @Override
    public void onEnable() {

        for (Module m : h2.moduleManager.getEnabledMods()) {
            m.toggle();
        }

        // disables commands, disables keybinds, disables hud
        // -> mixinminecraft, mixinentityplayers

        h2.panic = true;

        // disables clickgui, if open

        mc.displayGuiScreen(null);
    }
}
