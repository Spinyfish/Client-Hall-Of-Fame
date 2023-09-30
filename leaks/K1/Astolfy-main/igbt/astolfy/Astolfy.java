package igbt.astolfy;

import club.minnced.discord.rpc.DiscordRichPresence;
import org.lwjgl.opengl.Display;

import igbt.astolfy.module.ModuleManager;
import igbt.astolfy.ui.Notifications.NotificationManager;

public class Astolfy {
	
	public static Astolfy i = new Astolfy();
	public String clientName = "Astolfy";
	public String buildVersion = "B1.0";
	public static ModuleManager moduleManager = new ModuleManager();
	public static NotificationManager notificationManager = new NotificationManager();
	
	public void setupClient() {
		Display.setTitle(clientName + " " + buildVersion);
		moduleManager.setupModules();
	}
	
	public void onClientClose() {
		
	}
	
}



/*

 	We all love FuzzySalt,
 	He was the best java developer,
 	We all miss FuzzySalt.
 
*/
