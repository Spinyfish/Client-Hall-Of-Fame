package dev.elysium.client.mods.impl.combat;

import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.NumberSetting;

public class Hitbox extends Mod {
    public NumberSetting expand_value = new NumberSetting("Expand", 0, 6, 0, 0.05, this);
    public Hitbox() {
        super("Hitbox","Modifies hitboxes", Category.COMBAT);
    }
}
