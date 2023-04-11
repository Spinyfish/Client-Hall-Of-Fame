package byron.Mono.module.impl.movement;

import byron.Mono.Mono;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

    @ModuleInterface(name = "InvWalk", description = "Walk with your inventory.", category = Category.Movement)
    public class InvWalk extends Module {

        @Subscribe
        public void onUpdate(EventUpdate e) {
            if (e instanceof EventUpdate) {
                if (mc.currentScreen instanceof GuiScreen) {
                    EntityPlayerSP thePlayer4;
                    if (Keyboard.isKeyDown(205) && !(mc.currentScreen instanceof GuiChat)) {
                        thePlayer4 = mc.thePlayer;
                        thePlayer4.rotationYaw += 6.0F;
                    }

                    if (Keyboard.isKeyDown(203) && !(mc.currentScreen instanceof GuiChat)) {
                        thePlayer4 = mc.thePlayer;
                        thePlayer4.rotationYaw -= 6.0F;
                    }

                    if (Keyboard.isKeyDown(200) && !(mc.currentScreen instanceof GuiChat)) {
                        thePlayer4 = mc.thePlayer;
                        thePlayer4.rotationPitch -= 6.0F;
                    }

                    if (Keyboard.isKeyDown(208) && !(mc.currentScreen instanceof GuiChat)) {
                        thePlayer4 = mc.thePlayer;
                        thePlayer4.rotationPitch += 6.0F;
                    }
                }

                KeyBinding[] moveKeys = new KeyBinding[]{mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindForward, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint};
                KeyBinding[] array2;
                int length2;
                int j;
                KeyBinding bind;
                if (mc.currentScreen instanceof GuiScreen && !(mc.currentScreen instanceof GuiChat)) {
                    array2 = moveKeys;
                    length2 = moveKeys.length;

                    for(j = 0; j < length2; ++j) {
                        bind = array2[j];
                        bind.pressed = Keyboard.isKeyDown(bind.getKeyCode());
                    }
                } else {
                    array2 = moveKeys;
                    length2 = moveKeys.length;

                    for(j = 0; j < length2; ++j) {
                        bind = array2[j];
                        if (!Keyboard.isKeyDown(bind.getKeyCode())) {
                            KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                        }
                    }
                }
            }

        }

        @Override
        public void onEnable() {
            super.onEnable();
        }

        @Override
        public void onDisable() {
            super.onDisable();
        }
    }

