package dev.elysium.client.mods.impl.render;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventUpdate;
import dev.elysium.client.utils.api.Hypixel;
import net.minecraft.potion.PotionEffect;

public class FullBright extends Mod {
    public ModeSetting mode = new ModeSetting("Mode", this, "Gamma", "Potion");

    public FullBright() {
        super("FullBright","Allows you to see in the dark", Category.RENDER);
    }

    public void onEnable() {
        if(mode.is("Gamma"))
            mc.gameSettings.gammaSetting = 1000f;
        super.onEnable();
    }

    public void onDisable() {
        if(mode.is("Gamma"))
            mc.gameSettings.gammaSetting = 1f;
        else
            mc.thePlayer.removePotionEffect(16);
        super.onEnable();
    }

    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        if(mode.is("Potion"))
            mc.thePlayer.addPotionEffect(new PotionEffect(16, 5200));
    }
}
