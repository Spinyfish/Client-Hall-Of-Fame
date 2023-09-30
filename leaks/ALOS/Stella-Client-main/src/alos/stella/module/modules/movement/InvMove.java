package alos.stella.module.modules.movement;

import alos.stella.event.EventTarget;
import alos.stella.event.events.MoveEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.ui.clickgui.astolfo.AstolfoClickGui;
import alos.stella.ui.clickgui.dropdown.DropdownGUI;
import alos.stella.value.BoolValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

@ModuleInfo(name = "InvMove", description = "Move in Inventory", category = ModuleCategory.MOVEMENT)
public class InvMove extends Module {

    public BoolValue sneak = new BoolValue("Sneak", false);
    public BoolValue sprint = new BoolValue("Sprint", false);

    private final KeyBinding[] AFFECTED_BINDINGS = new KeyBinding[]{
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindJump
    };
    private final KeyBinding[] SPRINT = new KeyBinding[]{
            mc.gameSettings.keyBindSprint,
    };
    private final KeyBinding[] SNEAK = new KeyBinding[]{
            mc.gameSettings.keyBindSneak,
    };


    @EventTarget
    public void onMove(MoveEvent event){
        if (mc.currentScreen instanceof GuiChat || mc.currentScreen == new AstolfoClickGui() || mc.currentScreen == new DropdownGUI()) {
            return;
        }

        for (final KeyBinding bind : AFFECTED_BINDINGS) {
            bind.setPressed(GameSettings.isKeyDown(bind));
            if (sprint.get()){
                for (final KeyBinding sprint : SPRINT) {
                    sprint.setPressed(GameSettings.isKeyDown(sprint));
                }
            }
            if (sneak.get()){
                for (final KeyBinding sneak : SNEAK) {
                    sneak.setPressed(GameSettings.isKeyDown(sneak));
                }
            }
        }
    }

}
