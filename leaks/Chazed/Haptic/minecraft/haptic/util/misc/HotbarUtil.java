package haptic.util.misc;

import haptic.util.base.BaseUtil;
import haptic.util.network.PacketUtils;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

public class HotbarUtil implements BaseUtil {
	
	public static int getAirSlot() {
		for(int i = 0; i < 9; i++) {
			if(mc.thePlayer.inventory.mainInventory[i] == null) {
				return i;
			}
		}
		return -1;
	}
	
}