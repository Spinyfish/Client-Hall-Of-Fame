package axolotl.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class PlayerUtil {

	public static Minecraft mc = Minecraft.getMinecraft();	
	
	public static int getPlayerHeight() {
		
		int y = (int)Math.floor(mc.thePlayer.posY) - 1;
		int l = 0;
		
		while(BlockUtils.inst.isAir(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, y, mc.thePlayer.posZ)).getBlock())) {
			y -= 1;
			l++;
			if(y < 0)break;
		}
		
		return l + 1;
		
	}

    public static Block getBlockAt() {
		return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).getBlock();
    }

	public static Block getBlockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
		return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + offsetX, mc.thePlayer.posY + offsetY, mc.thePlayer.posZ + offsetZ)).getBlock();
	}

}
