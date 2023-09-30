package intentions.modules.movement;

import org.lwjgl.input.Keyboard;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.modules.Module;
import net.minecraft.client.Minecraft;

public class BHop extends Module {

	public BHop() {
		super("BHop", Keyboard.KEY_Z, Category.MOVEMENT, "Automatically jumps for you (Speed with jump on is fast hop btw)", true);
	}
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public void onTick() {
		if(mc.thePlayer != null && mc.thePlayer.onGround && this.toggled) {
			mc.thePlayer.jump();
		}
	}
}
