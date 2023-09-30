package intentions.util;

import net.minecraft.client.Minecraft;

public class MovementUtils {

	private static Minecraft mc = Minecraft.getMinecraft();
	
	public static boolean isMoving() {
		return (mc.thePlayer.motionY != 0 || mc.thePlayer.motionZ != 0 || mc.thePlayer.motionX != 0);
	}
	
}
