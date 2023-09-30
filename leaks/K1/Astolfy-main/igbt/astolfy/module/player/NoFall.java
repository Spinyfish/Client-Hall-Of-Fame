package igbt.astolfy.module.player;

import org.lwjgl.input.Keyboard;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventPacket;
import igbt.astolfy.module.ModuleBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class NoFall extends ModuleBase {

	public NoFall() {
		super("NoFall", Keyboard.KEY_N, Category.PLAYER);
	}


	public void onEvent(Event e) {
		if(e instanceof EventPacket) {

			EventPacket event = (EventPacket)e;
			if(mc.thePlayer != null &&  mc.thePlayer.fallDistance > 2 && !isAboveVoid()) {
				//mc.thePlayer.motionY = 0;
				//C03PacketPlayer c03 = (C03PacketPlayer)event.getPacket();
				//c03.onGround = true;
				mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer(true));
				mc.thePlayer.fallDistance = 0;
			}
		}
	}
	
	public boolean isAboveVoid() {
		for (int i = (int)Math.ceil(mc.thePlayer.posY); i > -1; i--) {
			Block b = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock();
			if(!(b instanceof BlockAir))
				return false;
		}
		return true;
	}
	
	
}
