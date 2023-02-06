package dev.elysium.client.mods.impl.movement;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.client.events.EventUpdate;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class InventoryMove extends Mod {
    public InventoryMove() {
        super("InventoryMove","Allows you to move whilst in menus", Category.MOVEMENT);
    }

    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        if(mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindForward));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindBack));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindLeft));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindRight));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindJump));
        }
    }
}
