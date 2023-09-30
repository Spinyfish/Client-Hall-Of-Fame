package me.wavelength.baseclient.module.modules.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class AutoRespawn extends Module {

    public AutoRespawn() {
        super("AutoRespawn", "Respawn You Automatically", 0, Category.PLAYER);
    }

    @Override
    public void onUpdate(UpdateEvent event) {
    	if(this.isToggled()) {
			if(mc.thePlayer.isDead) {
				mc.thePlayer.respawnPlayer();
			}
		}
    }
}