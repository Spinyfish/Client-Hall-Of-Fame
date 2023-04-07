
package intentions.modules.render;

import org.lwjgl.input.Keyboard;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventUpdate;
import intentions.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FullBright extends Module {
	
	public static Minecraft mc = Minecraft.getMinecraft();

	public FullBright() {
		super("FullBright", Keyboard.KEY_B, Category.RENDER, "Removes darkness from the world", true);
	}
	
	public void onDisable() {
		mc.gameSettings.gammaSetting = 1;
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			if(e.isPre()) {
				mc.gameSettings.gammaSetting = 16;
			}
		}
	}

}
