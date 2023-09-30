package intentions.modules.world;

import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.modules.Module;
import intentions.settings.ModeSetting;
import intentions.util.PlayerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class AntiVoid extends Module {

	public static ModeSetting mode = new ModeSetting("Mode", "Hypixel", new String[] {"Hypixel"});
	
	public AntiVoid() {
		super("AntiVoid", 0, Category.WORLD, "Prevents you from falling into the void", true);
		this.addSettings(mode);
	}
	
	Vec3 lastOnGround = null;
	
	public void onEvent(Event e) {
		if (e instanceof EventMotion) {
			if(!mode.getMode().equalsIgnoreCase("Hypixel"))return;
			if(mc.thePlayer.fallDistance > 3 && mc.thePlayer.motionY < 0 && PlayerUtil.getPlayerHeight() > 1){
			    for(int i = 0;i<30;i++){
			        if(!mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - i, mc.thePlayer.posZ)))return;
			    }
			    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 5, mc.thePlayer.posZ, false));
				return;
			}
		}
	}
	
}
