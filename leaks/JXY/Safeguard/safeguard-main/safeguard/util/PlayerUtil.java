package intentions.util;

import java.util.List;

import intentions.modules.player.Team;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class PlayerUtil {

	private static Minecraft mc = Minecraft.getMinecraft();
	
	public static int getPlayerHeight() {
		int yHeight = (int)Math.floor(mc.thePlayer.posY);
		int height = 0;
		while(!mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, yHeight, mc.thePlayer.posZ)).getBlock().isSolidFullCube()) {
			height++;
			yHeight--;
			if(height > Math.floor(mc.thePlayer.posY)) {
				break;
			}
		}
		return height;
	}
	
	public static double getPlayerHeightExtra() {
		int yHeight = (int)Math.floor(mc.thePlayer.posY);
		int height = 0;
		Block b = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, yHeight, mc.thePlayer.posZ)).getBlock();
		while(b == Blocks.air || BlockUtils.isLiquidBlock(b)) {
			height++;
			yHeight--;
			if(height > Math.floor(mc.thePlayer.posY)) {
				break;
			}
			b = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, yHeight, mc.thePlayer.posZ)).getBlock();
		}
		return height;
	}
	
	public static double getPlayerHeightDouble() {
		double yHeight = (Math.floor(mc.thePlayer.posY * 10)) / 10;
		double height = 0;
		double depth = 80;
		while(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, yHeight, mc.thePlayer.posZ)).getBlock() == Blocks.air && depth > 0) {
			height += 0.1;
			yHeight -= 0.1;
			depth--; // Prevent crashing above void (for speed)
		}
		return height;
	}
	
	public static List<EntityLivingBase> removeNotNeeded(List<EntityLivingBase> l) {
		while(!shouldAttack(l)) {
	    	  l.remove(0);
	    }
		return l;
	}
	
	public static boolean shouldAttack(List<EntityLivingBase> l) {
		
		if(l.size() > 0 && Team.team && l.get(0) instanceof EntityPlayer && !Team.getIsTeam((EntityPlayer)l.get(0))) {
			return true;
		}else if (l.size() > 0 && Team.team && l.get(0) instanceof EntityPlayer && Team.getIsTeam((EntityPlayer)l.get(0))) {
			return false;
		}
		return true;
	  }
	public static boolean isStrafing() {
		return Math.abs(mc.thePlayer.motionX) > (Math.abs(mc.thePlayer.motionZ)/2) && Math.abs(mc.thePlayer.motionZ) > (Math.abs(mc.thePlayer.motionX)/2);
	}

	public static Block getBlockAt() {
		return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).getBlock();
	}

	public static boolean isAboveLiquids() {
		for(Block b : BlockUtils.getBlocksBelow()) {
			if(!BlockUtils.isLiquidBlock(b)) {
				return false;
			}
		}
		return true;
	}

	
	// CHEAT DETECTOR
	public static boolean isValid(EntityOtherPlayerMP p) {
		return !p.capabilities.allowFlying && p.isEntityAlive() && !p.capabilities.isCreativeMode;
	}

	public static boolean isUsingItem(EntityOtherPlayerMP p) {
		return p.getItemInUse() != null;
	}
	
}
