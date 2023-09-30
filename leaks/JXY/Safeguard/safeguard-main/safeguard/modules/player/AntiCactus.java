
package intentions.modules.player;

import org.lwjgl.input.Keyboard;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventUpdate;
import intentions.modules.Module;
import net.minecraft.network.play.client.C03PacketPlayer;

public class AntiCactus extends Module {

	public AntiCactus() {
		super("AntiCactus", Keyboard.KEY_J, Category.PLAYER, "Prevents cactus damage", true);
	}
	
	public static boolean noCactus;
	
	public void onEnable() {
		noCactus = true;
	}
	public void onDisable() {
		noCactus = false;
	}
}
