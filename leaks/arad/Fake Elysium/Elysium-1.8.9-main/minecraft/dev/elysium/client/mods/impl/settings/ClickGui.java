package dev.elysium.client.mods.impl.settings;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventUpdate;
import dev.elysium.client.ui.gui.clickgui.dadik.ClickGUI;
import dev.elysium.client.ui.gui.clickgui.old.COMPACT_CG;
import net.minecraft.item.ItemSeeds;
import org.lwjgl.input.Keyboard;

public class ClickGui extends Mod {
    public ClickGui() {
        super("ClickGUI", "The thing you're in right now", Category.SETTINGS);
        if(keyCode.getKeyCode() == 0)
            keyCode.setKeyCode(Keyboard.KEY_RSHIFT);
        show = false;
    }

    public void onEnable() {
        if(mc.thePlayer.getHeldItem() != null) {
            if(mc.thePlayer.getHeldItem().getItem() instanceof ItemSeeds) {
                mc.displayGuiScreen(new ClickGUI());
                return;
            }
        }
        mc.displayGuiScreen(new ClickGUI());
        if(mc.thePlayer.isSneaking()) mc.displayGuiScreen(new COMPACT_CG());
    }

    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        if(!(mc.currentScreen instanceof COMPACT_CG))
            toggled = false;
    }
}
