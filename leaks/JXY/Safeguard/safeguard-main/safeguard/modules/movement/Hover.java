package intentions.modules.movement;

import org.lwjgl.input.Keyboard;

import intentions.Client;
import intentions.modules.Module;

import net.minecraft.client.Minecraft;

public class Hover extends Module {
	public Hover() {
		super("Hover", Keyboard.KEY_M, Category.MOVEMENT, "Makes you hover in the air", true);
	}
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public void onTick() {
		if (this.toggled && mc.thePlayer != null) {
			mc.thePlayer.motionY = 0;
		}
	}
}
