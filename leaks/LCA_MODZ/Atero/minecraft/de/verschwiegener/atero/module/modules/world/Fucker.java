package de.verschwiegener.atero.module.modules.world;


import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import god.buddy.aot.BCompiler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockCake;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class Fucker extends Module {
    TimeUtils timeUtils;
    private BlockPos pos;
    public Fucker() {
        super("Fucker", "Fucker", Keyboard.KEY_NONE, Category.World);
        timeUtils = new TimeUtils();
    }

    public void onEnable() {

        super.onEnable();
    }

    public void onDisable() {

        super.onDisable();
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void onUpdate() {
	if (this.isEnabled()) {
	    super.onUpdate();
	    for (int y = 3; y >= -3; --y) {
		for (int x = -3; x <= 3; ++x) {
		    for (int z = -3; z <= 3; ++z) {
			int posX = (int) (mc.thePlayer.posX - 0.5 + x);
			int posZ = (int) (mc.thePlayer.posZ - 0.5 + z);
			int posY = (int) (mc.thePlayer.posY - 0.5 + y);
			pos = new BlockPos(posX, posY, posZ);
			Block block = mc.theWorld.getBlockState(pos).getBlock();
			if (block instanceof BlockBed || block instanceof BlockCake) {
			    final PlayerControllerMP playerController = mc.playerController;
			    final long timeLeft = (long) (PlayerControllerMP.curBlockDamageMP / 2.0f);
			    // this.facing = rotations(pos);
			    if (timeUtils.hasReached(100F)) {
				timeUtils.reset();
				System.out.println("Lol");
				mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
					C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
				mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
					C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
				mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
			    }

			}
		    }
		}
	    }
	}

    }

}



