package intentions.waypoints;

import java.io.File;

import intentions.Client;
import intentions.modules.Module;
import intentions.ui.GuiAltManager;
import net.minecraft.client.Minecraft;

public class Waypoint {

	/**
	 * 
	 * Called every tick
	 * 
	 */
	
	private static Minecraft mc = Minecraft.getMinecraft();
	
	public static void onTick() {
		
		if(mc.thePlayer != null && mc.thePlayer.ticksExisted % 200 == 0) {
			
			File folder = new File(System.getProperty("user.dir") + "\\SafeGuard\\");
			
			if(!folder.exists()) {
				folder.mkdir();
			}
			
			StringBuilder sb = new StringBuilder();
			for(Module m : Client.modules) {
				sb.append(m.isEnabled() + ";" + m.name + "\n");
			}
			//sb.append(""); // Soon TM (Waypoints?)
			
			String value = sb.toString();
			
			GuiAltManager.write("settings.txt", value);
		}
		
		if(mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null) {
			String server = mc.getCurrentServerData().serverIP;
			
			
		} else {
			
		}
	}
	
}
