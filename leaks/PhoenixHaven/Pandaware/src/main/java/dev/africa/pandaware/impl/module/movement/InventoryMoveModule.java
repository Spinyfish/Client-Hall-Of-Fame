package dev.africa.pandaware.impl.module.movement;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.ui.clickgui.ClickGUI;
import lombok.var;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "Inventory Move", category = Category.MOVEMENT)
public class InventoryMoveModule extends Module {
    private final BooleanSetting inClickGui = new BooleanSetting("Only In ClickGUI",true);

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> {
        var keyBinds = new KeyBinding[]{
                mc.gameSettings.keyBindForward,
                mc.gameSettings.keyBindLeft,
                mc.gameSettings.keyBindRight,
                mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindJump,
                mc.gameSettings.keyBindSprint
        };

        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat) && !inClickGui.getValue()) {
            for (KeyBinding keyBind : keyBinds) {
                keyBind.pressed = Keyboard.isKeyDown(keyBind.getKeyCode());
            }
        }
        if (inClickGui.getValue() && mc.currentScreen instanceof ClickGUI) {
            for (KeyBinding keyBind : keyBinds) {
                keyBind.pressed = Keyboard.isKeyDown(keyBind.getKeyCode());
            }
        }
    };

    public InventoryMoveModule() {
        this.registerSettings(
                this.inClickGui
        );
    }
}
