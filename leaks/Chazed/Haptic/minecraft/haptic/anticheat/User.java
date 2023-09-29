package haptic.anticheat;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;

public class User {
	
	private final EntityPlayer player;
	
	private final ArrayList<Check> checks = new ArrayList<>();
	
	public User(EntityPlayer p) {
		this.player = p;
	}

	public EntityPlayer getPlayer() {
		return player;
	}
	
	public ArrayList<Check> getChecks() {
		return checks;
	}
	
}