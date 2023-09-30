package dev.elysium.client.mods.impl.settings;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventRenderHUD;
import dev.elysium.client.events.EventUpdate;
import dev.elysium.client.ui.widgets.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.potion.PotionEffect;

public class Widgets extends Mod {
    public BooleanSetting keystrokes = new BooleanSetting("Keystrokes", true, this);

    public Widgets() {
        super("Widgets","Allows you to see widgets", Category.SETTINGS);
    }

    double prevScreenWidth = 0;
    double prevScreenHeight = 0;

    ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

    @EventTarget
    public void onEvent(EventRenderHUD e) {
        sr = new ScaledResolution(Minecraft.getMinecraft());

        if(!(mc.ingameGUI instanceof GuiIngame)) return;
        if(keystrokes.isEnabled()) {
            Elysium.getInstance().getWidgetManager().byName("Keystrokes").draw();
        }
    }
}
