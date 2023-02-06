package dev.elysium.client.mods.impl.settings;

import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;

public class Targets extends Mod {

    public BooleanSetting Invisible = new BooleanSetting("Invisible",false, this);
    public BooleanSetting Players = new BooleanSetting("Players",true, this);
    public BooleanSetting Animals = new BooleanSetting("Animals",false, this);
    public BooleanSetting Dead = new BooleanSetting("Dead",false, this);
    public BooleanSetting Mobs = new BooleanSetting("Mobs",false, this);

    public Targets() {
        super("Targets","Filter targets to your need", Category.SETTINGS);
    }
}
