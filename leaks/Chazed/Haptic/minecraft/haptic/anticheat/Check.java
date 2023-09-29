package haptic.anticheat;

import com.mojang.realmsclient.gui.ChatFormatting;

import haptic.Haptic;
import haptic.util.base.AnticheatBaseUtil;

public class Check implements AnticheatBaseUtil {
	
	private final String name;
	protected final User user;
	private int vl;
	
	public Check(String name, User user) {
		this.name = name;
		this.user = user;
	}
	
	public String getName() {
		return name;
	}
	
	public void handle() {
		
	}
	
	public void violation() {
		if(Haptic.getModuleManager().getModuleByName("Anticheat").isEnabled()) {
			vl++;
			Haptic.addChatMessage(user.getPlayer().getGameProfile().getName() + " has failed " + this.getName());
		}
	}
	
	public void violation(String message) {
		if(Haptic.getModuleManager().getModuleByName("Anticheat").isEnabled()) {
			vl++;
			Haptic.addChatMessage(user.getPlayer().getGameProfile().getName() + " has failed " + this.getName() + ChatFormatting.GRAY + " | ");
		}
	}
	
}
