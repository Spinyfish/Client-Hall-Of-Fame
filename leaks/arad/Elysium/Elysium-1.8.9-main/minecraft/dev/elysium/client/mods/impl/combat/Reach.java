package dev.elysium.client.mods.impl.combat;

import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.NumberSetting;

public class Reach extends Mod {

    public NumberSetting combat = new NumberSetting("Hit", 0, 6, 3, 0.01, this);
    public NumberSetting build = new NumberSetting("Build", 0, 8, 4.5, 0.01, this);

    public Reach() {
        super ("Reach","Modifies you combat/interact reach", Category.COMBAT);
    }
}
