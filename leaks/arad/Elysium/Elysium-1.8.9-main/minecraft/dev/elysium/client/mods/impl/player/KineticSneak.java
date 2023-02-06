package dev.elysium.client.mods.impl.player;

import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;

public class KineticSneak extends Mod {

    public BooleanSetting clientSide = new BooleanSetting("Client-side",true, this);

    public KineticSneak() {
        super("KineticSneak","Auto-sneak when you're standing still", Category.PLAYER);
    }
}