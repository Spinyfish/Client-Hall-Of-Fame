package intentions.modules.player;

import intentions.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class Team extends Module{

	public Team() {
		super("Team", 0, Category.PLAYER, "Don't attack players on your team", true);
	}
	
	public static boolean team=false;public void onEnable() {team=true;}public void onDisable() {team=false;} private static Minecraft mc = Minecraft.getMinecraft();
	
	public static boolean getIsTeam(EntityPlayer e) {
		return e.getTeam() == mc.thePlayer.getTeam();
	}
	
}
