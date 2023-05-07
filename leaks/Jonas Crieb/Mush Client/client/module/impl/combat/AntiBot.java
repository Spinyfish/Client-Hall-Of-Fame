package client.module.impl.combat;

import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;

import client.module.impl.combat.antibot.MushAntiBot;
import client.value.impl.ModeValue;

@ModuleInfo(name = "AntiBot", description = "", category = Category.COMBAT)
public class AntiBot extends Module {
    public final ModeValue mode = new ModeValue("Mode", this)
            .add(new MushAntiBot("MushMC", this))
            .setDefault("MushMC");
    @Override
    protected void onEnable() {
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
    }


}
