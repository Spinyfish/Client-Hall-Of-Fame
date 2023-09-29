package dev.elysium.client.mods.impl.player;

import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.NumberSetting;

public class ChestStealer extends Mod {

    public NumberSetting delay = new NumberSetting("Delay",0,2500,250,1,this);
    public BooleanSetting namecheck = new BooleanSetting("Check Name",true,this);
    public NumberSetting closedelay = new NumberSetting("Close Delay",0,2500,250,1,this);
    public BooleanSetting autoclose = new BooleanSetting("Auto Close",true,this);

    public ChestStealer() {
        super("ChestStealer","Steals items from chests", Category.PLAYER);
    }
}
