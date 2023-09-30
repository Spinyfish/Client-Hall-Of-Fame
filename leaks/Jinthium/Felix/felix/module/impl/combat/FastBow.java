package felix.module.impl.combat;

import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FastBow extends Module {
	
	public FastBow() {
		super("FastBow", 0, ModuleCategory.COMBAT);
	}
	
	@Handler
	public void onMotionUpdate(final EventMotionUpdate event) {
		final EntityPlayerSP player = mc.thePlayer;
	    if (player.getItemInUse() != null && player.getItemInUse().getItem() instanceof ItemBow && player.getItemInUseDuration() == 5) {
	        mc.playerController.onStoppedUsingItem(player);
	    }
	    if (player.getItemInUse() != null && player.getItemInUse().getItem() instanceof ItemBow) {
	        	for (int i = 0; i < 25; ++i) {
	        	player.sendQueue.addToSendQueue(new C03PacketPlayer());
	        }
	    }
	}
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
	}
}
