package haptic.util.world;

import java.util.concurrent.ThreadLocalRandom;

import haptic.util.base.BaseUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class WorldUtils implements BaseUtil {
	
	public static boolean negativeExpand(double negativeExpandValue) {
		if(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + negativeExpandValue, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ + negativeExpandValue)).getBlock() instanceof BlockAir && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX - negativeExpandValue, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ - negativeExpandValue)).getBlock() instanceof BlockAir && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX - negativeExpandValue, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ)).getBlock() instanceof BlockAir && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + negativeExpandValue, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ)).getBlock() instanceof BlockAir && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ + negativeExpandValue)).getBlock() instanceof BlockAir && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ - negativeExpandValue)).getBlock() instanceof BlockAir) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isBlockUnderPlayer() {
		for(int i = 0; i < (int) mc.thePlayer.posY; i++) {
			if(!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)) {
				return true;
			}
		}
		return false;
	}
	
	public static Vec3 getVec3(BlockPos pos, EnumFacing facing) {
		double x = (double) pos.getX();
		double y = (double) pos.getY();
		double z = (double) pos.getZ();
		double random1 = ThreadLocalRandom.current().nextDouble(0.6D, 1.0D);
		double random2 = ThreadLocalRandom.current().nextDouble(0.9D, 1.0D);
		if(facing.equals(EnumFacing.UP)) {
			x += random1;
			z += random1;
			++y;
		} else if(facing.equals(EnumFacing.DOWN)) {
			x += random1;
			z += random1;
        } else if(facing.equals(EnumFacing.WEST)) {
        	y += random2;
        	z += random1;
        } else if(facing.equals(EnumFacing.EAST)) {
        	y += random2;
        	z += random1;
        	++x;
        } else if(facing.equals(EnumFacing.SOUTH)) {
        	y += random2;
        	x += random1;
        	++z;
        } else if(facing.equals(EnumFacing.NORTH)) {
        	y += random2;
        	x += random1;
        }

        return new Vec3(x, y, z);
	}

}
