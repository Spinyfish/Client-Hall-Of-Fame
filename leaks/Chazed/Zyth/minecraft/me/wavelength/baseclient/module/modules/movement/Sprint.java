package me.wavelength.baseclient.module.modules.movement;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class Sprint extends Module {

    public Sprint() {
        super("sprint", "Auto sprinting!", 0, Category.MOVEMENT, AntiCheat.VANILLA, AntiCheat.AAC);
    }

    private boolean isFlying;
    private boolean allowFlying;

    @Override
    public void setup() {
        moduleSettings.addDefault("speed", 1.0D);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        mc.thePlayer.setSprinting(false);
    }

    @Override
    public void onUpdate(UpdateEvent event) {
    	if(mc.gameSettings.keyBindForward.isKeyDown()) {
            mc.thePlayer.setSprinting(true);
    	}else {
            mc.thePlayer.setSprinting(false);
    	}


    }

}