package intentions.modules.movement;

import org.lwjgl.input.Keyboard;

import intentions.Client;
import intentions.modules.Module;

import net.minecraft.client.Minecraft;

public class AirHop extends Module {

	public AirHop() {
		super("AirHop", Keyboard.KEY_H, Category.MOVEMENT, "Allows you to jump in the air", true);
	}
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public void onTick() {
		if (this.toggled && mc.thePlayer != null) {
			mc.thePlayer.onGround = true;
		}
	}
}
