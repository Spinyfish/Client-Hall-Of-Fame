package me.wavelength.baseclient.module.modules.render;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class AntiCobweb extends Module {

    public AntiCobweb() {
        super("AntiCobweb", "No More Slowness when on cobweb!",0, Category.RENDER);
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
    	if(this.isToggled()) {
			mc.thePlayer.isInWeb = false;
		}
    }
}