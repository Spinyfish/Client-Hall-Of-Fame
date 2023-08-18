package me.jinthium.straight.impl.modules.misc;

import me.jinthium.straight.api.module.Module;

public class ModuleSounds extends Module {

    public ModuleSounds(){
        super("ModuleSounds", Category.MISC);
    }

    @Override
    public void onEnable() {
        Module.sounds = true;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        Module.sounds = false;
        super.onDisable();
    }
}
