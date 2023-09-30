package felix.util.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import felix.events.player.EventMotionUpdate;
import felix.util.other.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.optifine.reflect.Reflector;

public class RotationUtils {

	private static Minecraft mc = Minecraft.getMinecraft();

	public static float[] getNeededRotations(Entity entity) {
		double d0 = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
		double d1 = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
		double d2 = entity.posY + entity.getEyeHeight()
				- (Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minY
						+ (Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().maxY
								- Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minY));
		double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
		float f = (float) (MathHelper.func_181159_b(d1, d0) * 180.0D / Math.PI) - 90.0F;
		float f1 = (float) (-(MathHelper.func_181159_b(d2, d3) * 180.0D / Math.PI));
		return new float[] { f, f1 };
	}

	public static float getDistanceBetweenAngles(float angle1, float angle2) {
		float angle = Math.abs(angle1 - angle2) % 360.0f;
		if (angle > 180.0f) {
			angle = 360.0f - angle;
		}
		return angle;
	}

	public static void rotate(final EventMotionUpdate event, final float[] rotations, final float aimSpeed, final boolean lockView) {
		final float[] prevRotations = {event.getPrevYaw(), event.getPrevPitch()};

		final float[] cappedRotations = {
				maxAngleChange(prevRotations[0], rotations[0], aimSpeed),
				maxAngleChange(prevRotations[1], rotations[1], aimSpeed)
		};

		event.setYaw(cappedRotations[0]);
		event.setPitch(cappedRotations[1]);

		if (lockView) {
			PlayerUtil.getLocalPlayer().rotationYaw = cappedRotations[0];
			PlayerUtil.getLocalPlayer().rotationPitch = cappedRotations[1];
		}
	}

	public static float maxAngleChange(final float prev, final float now, final float maxTurn) {
		float dif = MathHelper.wrapAngleTo180_float(now - prev);
		if (dif > maxTurn) dif = maxTurn;
		if (dif < -maxTurn) dif = -maxTurn;
		return prev + dif;
	}

	public static float getPitchChange(float pitch, Entity entity, double posY) {
		double deltaX = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
		double deltaZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
		double deltaY = posY - 2.2D + entity.getEyeHeight() - Minecraft.getMinecraft().thePlayer.posY;
		double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
		double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
		return -MathHelper.wrapAngleTo180_float(pitch - (float) pitchToEntity) - 2.5F;
	}

	public static float getYawChange(float yaw, double posX, double posZ) {
		double deltaX = posX - Minecraft.getMinecraft().thePlayer.posX;
		double deltaZ = posZ - Minecraft.getMinecraft().thePlayer.posZ;
		double yawToEntity = 0;
		if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
			if (deltaX != 0)
				yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
		} else if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
			if (deltaX != 0)
				yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
		} else {
			if (deltaZ != 0)
				yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
		}

		return MathHelper.wrapAngleTo180_float(-(yaw - (float) yawToEntity));
	}

	public static float[] getRotationsBlock(BlockPos block, EnumFacing face) {
		double x = block.getX() + 0.5 - mc.thePlayer.posX + (double) face.getFrontOffsetX() / 2;
		double z = block.getZ() + 0.5 - mc.thePlayer.posZ + (double) face.getFrontOffsetZ() / 2;
		double y = (block.getY() + 0.5);
		double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
		double d3 = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) (Math.atan2(d1, d3) * 180.0D / Math.PI);
		if (yaw < 0.0F) {
			yaw += 360f;
		}
		return new float[] { yaw, pitch };
	}

	public static float[] getBowAngles(final Entity entity) {
		final double xDelta = (entity.posX - entity.lastTickPosX) * 0.4;
		final double zDelta = (entity.posZ - entity.lastTickPosZ) * 0.4;
		double d = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity);
		d -= d % 0.8;
		double xMulti = 1.0;
		double zMulti = 1.0;
		final boolean sprint = entity.isSprinting();
		xMulti = d / 0.8 * xDelta * (sprint ? 1.25 : 1.0);
		zMulti = d / 0.8 * zDelta * (sprint ? 1.25 : 1.0);
		final double x = entity.posX + xMulti - Minecraft.getMinecraft().thePlayer.posX;
		final double z = entity.posZ + zMulti - Minecraft.getMinecraft().thePlayer.posZ;
		final double y = Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight()
				- (entity.posY + entity.getEyeHeight());
		final double dist = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity);
		final float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90.0f;
		double d1 = MathHelper.sqrt_double(x * x + z * z);
		final float pitch = (float) -(Math.atan2(y, d1) * 180.0D / Math.PI) + (float) dist * 0.11f;

		return new float[] { yaw, -pitch };
	}

	public static float getYawToEntity(Entity entity, boolean useOldPos) {
		final EntityPlayerSP player = mc.thePlayer;
		double xDist = (useOldPos ? entity.prevPosX : entity.posX) - (useOldPos ? player.prevPosX : player.posX);
		double zDist = (useOldPos ? entity.prevPosZ : entity.posZ) - (useOldPos ? player.prevPosZ : player.posZ);
		float rotationYaw = useOldPos ? player.prevRotationYaw : player.rotationYaw;
		float var1 = (float) (Math.atan2(zDist, xDist) * 180.0D / Math.PI) - 90.0F;
		return rotationYaw + MathHelper.wrapAngleTo180_float(var1 - rotationYaw);
	}

	public static Vec3 getRotationVector(float pitch, float yaw) {
		float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
		float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
		float f2 = -MathHelper.cos(-pitch * 0.017453292F);
		float f3 = MathHelper.sin(-pitch * 0.017453292F);
		return new Vec3((double) (f1 * f2), (double) f3, (double) (f * f2));
	}
}
