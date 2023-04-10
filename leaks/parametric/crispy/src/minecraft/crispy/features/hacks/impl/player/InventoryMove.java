package crispy.features.hacks.impl.player;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

@HackInfo(name = "InventoryMove", category = Category.PLAYER)
public class InventoryMove extends Hack {
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {


            if ((mc.currentScreen != null) && (!(mc.currentScreen instanceof GuiChat))) {
                KeyBinding[] key = {mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
                        mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindJump};
                KeyBinding[] arrayOfKeyBinding1;
                int j = (arrayOfKeyBinding1 = key).length;
                for (int i = 0; i < j; i++) {
                    KeyBinding b = arrayOfKeyBinding1[i];
                    KeyBinding.setKeyBindState(b.getKeyCode(), Keyboard.isKeyDown(b.getKeyCode()));
                }
            }
        }
    }
}
