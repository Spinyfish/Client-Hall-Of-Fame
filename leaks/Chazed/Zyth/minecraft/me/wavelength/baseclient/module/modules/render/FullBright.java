package me.wavelength.baseclient.module.modules.render;

import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import org.lwjgl.input.Keyboard;

public class FullBright extends Module {

    public FullBright() {
        super("fullbright", "Reach the outer skies!", Keyboard.KEY_NONE, Category.RENDER);
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

    }

    @Override
    public void onUpdate(UpdateEvent event) {
        mc.gameSettings.gammaSetting = 1000.0f;

    }

}