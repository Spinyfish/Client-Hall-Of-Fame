package haptic.util.anticheat;

import net.minecraft.entity.player.EntityPlayer;

public class AnticheatMoveUtil {
	
	public static boolean isPlayerWalking(EntityPlayer p) {
		return p.moveForward != 0 && p.moveStrafing != 0;
	} 

}