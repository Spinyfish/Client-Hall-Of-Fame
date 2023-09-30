package axolotl.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class BlockUtils {
	public static BlockUtils inst;

	public static BlockUtils startUp() {
		inst = new BlockUtils();
		return inst;
	}

	public Minecraft mc;

	public float[] getFacePos(Vec3 vec) {
		double diffX = vec.x + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
		double diffY = vec.y + 0.5
				- (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
		double diffZ = vec.z + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		float p = Minecraft.getMinecraft().thePlayer.rotationPitch
				+ MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch);
		return new float[] {
				Minecraft.getMinecraft().thePlayer.rotationYaw
						+ MathHelper.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw),
				p};
	}

	public boolean isAir(Block block) {
		return block.getMaterial() == Material.air;
	}

	public Block getBlock(BlockPos var0) {
		return mc.theWorld.getBlockState(var0).getBlock();
	}

	public boolean isLiquidBlock(Block block) {
		return block == Blocks.water || block == Blocks.lava || block == Blocks.flowing_water || block == Blocks.flowing_lava;
	}

	public Vec3 getVec3(BlockPos blockPos) {
		return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

}