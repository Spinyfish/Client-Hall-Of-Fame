
package intentions.modules.player;

import java.lang.annotation.Target;

import org.lwjgl.input.Keyboard;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventUpdate;
import intentions.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoSlowdown extends Module {
	
	public static boolean noSlowdown;

	public NoSlowdown() {
		super("NoSlowdown", Keyboard.KEY_O, Category.PLAYER, "Prevents slowdown from stuff including blocking and eating", true);
	}
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public void onEnable() {
		noSlowdown = true;
	}
	public void onDisable() {
		noSlowdown = false;
	}
}
