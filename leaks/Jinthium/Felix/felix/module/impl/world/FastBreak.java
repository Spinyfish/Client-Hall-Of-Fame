package felix.module.impl.world;

import felix.api.annotations.Handler;
import felix.events.player.EventBlockDamaged;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.BlockPos;

public class FastBreak extends Module {
	
	public FastBreak() {
		super("SpeedMine", 0, ModuleCategory.WORLD);
	}
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
	}
	
	@Handler
	public void onMotionUpdate(final EventMotionUpdate event) {
		if (event.isPre()) {
			mc.playerController.blockHitDelay = 0;
		}
	}

	@Handler
	public void onBlockDamaged(final EventBlockDamaged event) {
		PlayerControllerMP playerController = mc.playerController;
		BlockPos pos = event.getBlockPos();
		mc.thePlayer.swingItem();
		playerController.curBlockDamageMP += this.getBlock((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()).getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, pos) * 0.186F;
	}

	public Block getBlock(double posX, double posY, double posZ) {
		BlockPos pos = new BlockPos((int)posX, (int)posY, (int)posZ);
		return mc.theWorld.getChunkFromBlockCoords(pos).getBlock(pos);
	}
}
