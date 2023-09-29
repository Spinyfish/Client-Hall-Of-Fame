package me.wavelength.baseclient.module.modules.movement;

import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class LegitSpeed extends Module {

    public LegitSpeed() {
        super("LegitSpeed", "AutoJump & Sprint", 0, Category.MOVEMENT);
    }

    public void onDisable() {
        mc.timer.timerSpeed = 1f;
        mc.gameSettings.keyBindJump.pressed = false;
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            mc.gameSettings.keyBindJump.pressed = true;
            mc.timer.timerSpeed = 1.05f;
        } else {
            if (!mc.thePlayer.onGround) {
                mc.gameSettings.keyBindJump.pressed = false;
                mc.timer.timerSpeed = 1.1f;
            }
        }
    }
}
