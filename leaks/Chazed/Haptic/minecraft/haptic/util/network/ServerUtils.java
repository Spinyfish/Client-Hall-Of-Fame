package haptic.util.network;

import haptic.util.base.BaseUtil;

public class ServerUtils implements BaseUtil {
	
	public static boolean isOnHypixel() {
		if(mc.getCurrentServerData() == null || mc.thePlayer == null)
			return false;
		
		return mc.getCurrentServerData().serverIP.contains("hypixel.net");
	}
	
	public static boolean isOnRedesky() {
		if(mc.getCurrentServerData() == null || mc.thePlayer == null)
			return false;
		
		return mc.getCurrentServerData().serverIP.contains("redesky.gg") || mc.getCurrentServerData().serverIP.contains("redesky.com") || mc.getCurrentServerData().serverIP.contains("redesky.net");
	}
	
}