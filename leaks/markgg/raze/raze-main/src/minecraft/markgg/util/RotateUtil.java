package markgg.util;

import java.util.ArrayList;
import java.util.HashSet;
import markgg.events.listeners.EventMotion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;


public class RotateUtil
{
	static Minecraft mc = Minecraft.getMinecraft();

	public static float Setpitch = 0.0F;
	public static float Setyaw = 0.0F;

	public static String getFacingSide() {
		float yaw = mc.thePlayer.rotationYaw;
        yaw = Math.abs(yaw);

        if(yaw > 360 || 0 > yaw) {
            mc.thePlayer.rotationYaw = 0;
        }
        String direction = "Unknown";

        if(yaw > 45 && yaw < 135) {
            direction = "WEST";
        }

        if(yaw > 135 && yaw < 225) {
            direction = "NORTH";
        }

        if(yaw > 225 && yaw < 315) {
            direction = "EAST";
        }

        if(yaw > 315 && yaw < 360) {
            direction = "SOUTH";
        }

        if(yaw > 360 && yaw < 45) {
            direction = "SOUTH";
        }
        return direction;
	}
	
	public static float[] getRotations(Entity e) {
		double deltaX = e.posX + e.posX - e.lastTickPosX - mc.thePlayer.posX;
		double deltaY = e.posY - 3.5D + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
		double deltaZ = e.posZ + e.posZ - e.lastTickPosZ - mc.thePlayer.posZ;
		double distance = Math.sqrt(Math.pow(deltaX, 2.0D) + Math.pow(deltaZ, 2.0D));

		float yaw = (float)Math.toDegrees(-Math.atan(deltaX - deltaZ));
		float pitch = (float)-Math.toDegrees(Math.atan(deltaY / distance));

		if (deltaX < 0.0D && deltaZ < 0.0D) {
			yaw = (float)(90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX)));
		} else if (deltaX > 0.0D && deltaZ < 0.0D) {
			yaw = (float)(-90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX)));
		} 

		return new float[] { yaw, pitch };
	}

	public static float[] getRotationFromPosition(double x, double z, double y) {
		double xDiff = x - (Minecraft.getMinecraft()).thePlayer.posX;
		double zDiff = z - (Minecraft.getMinecraft()).thePlayer.posZ;
		double yDiff = y - (Minecraft.getMinecraft()).thePlayer.posY - 1.2D;

		double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
		float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float)-(Math.atan2(yDiff, dist) * 180.0D / Math.PI);
		return new float[] { yaw, pitch };
	}

	public static float[] getPredictedRotations(EntityLivingBase ent) {
		double x = ent.posX + ent.posX - ent.lastTickPosX + randomNumber(0.03D, -0.03D);
		double z = ent.posZ + ent.posZ - ent.lastTickPosZ + randomNumber(0.03D, -0.03D);
		double y = ent.posY + (ent.getEyeHeight() / 2.0F);
		return getRotationFromPosition(x, z, y);
	}

	public static float[] getRotationss(double posX, double posY, double posZ) {
		Minecraft mc = Minecraft.getMinecraft();

		double x = posX - mc.thePlayer.posX + randomNumber(0.03D, -0.03D);
		double y = posY - mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
		double z = posZ - mc.thePlayer.posZ + randomNumber(0.03D, -0.03D);

		double dist = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float)(Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float)-(Math.atan2(y, dist) * 180.0D / Math.PI);
		return new float[] { yaw, pitch };
	}

	public static void RotatePlayer(EventMotion event, double pitch, double yaw, double speed) {
		if (Setpitch == 0.0F) {
			Setpitch = mc.thePlayer.rotationPitch;
		}

		if (Setyaw == 0.0F) {
			Setpitch = mc.thePlayer.rotationYaw;
		}

		if (Setpitch > pitch + speed || Setpitch < pitch - speed) {
			if (Setpitch > pitch) {
				Setpitch = (float)(Setpitch - speed);
			}

			if (Setpitch < pitch) {
				Setpitch = (float)(Setpitch + speed);
			}
		} 

		if (Setyaw > yaw - speed || Setyaw < yaw + speed) {
			if (Setyaw > yaw) {
				Setyaw = (float)(Setyaw - speed);
			}

			if (Setyaw < yaw) {
				Setyaw = (float)(Setyaw + speed);
			}
		} 
	}

	public static float[] getRotationsEntity(EntityLivingBase entity) {
		Minecraft mc = Minecraft.getMinecraft();

		if (mc.thePlayer.isSprinting()) {
			return getRotationss(entity.posX + randomNumber(0.03D, -0.03D), entity.posY + entity.getEyeHeight() - 0.4D + randomNumber(0.07D, -0.07D), entity.posZ + randomNumber(0.03D, -0.03D));
		}
		return getRotationss(entity.posX, entity.posY + entity.getEyeHeight() - 0.4D, entity.posZ);
	}

	private static MovingObjectPosition tracePath(World world, float x, float y, float z, float tx, float ty, float tz, float borderSize, HashSet<Entity> excluded) {
		Vec3 startVec = new Vec3(x, y, z);
		Vec3 endVec = new Vec3(tx, ty, tz);
		float minX = (x < tx) ? x : tx;
		float minY = (y < ty) ? y : ty;
		float minZ = (z < tz) ? z : tz;
		float maxX = (x > tx) ? x : tx;
		float maxY = (y > ty) ? y : ty;
		float maxZ = (z > tz) ? z : tz;
		AxisAlignedBB bb = (new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)).expand(borderSize, borderSize, borderSize);
		ArrayList<Entity> allEntities = (ArrayList<Entity>)world.getEntitiesWithinAABBExcludingEntity(null, bb);
		MovingObjectPosition blockHit = world.rayTraceBlocks(startVec, endVec);
		startVec = new Vec3(x, y, z);
		endVec = new Vec3(tx, ty, tz);
		Entity closestHitEntity = null;
		float closestHit = Float.POSITIVE_INFINITY;

		for (Entity ent : allEntities) {
			if (ent.canBeCollidedWith() && !excluded.contains(ent)) {
				float entBorder = ent.getCollisionBorderSize();
				AxisAlignedBB entityBb = ent.getEntityBoundingBox();
				if (entityBb == null) {
					continue;
				}
				entityBb = entityBb.expand(entBorder, entBorder, entBorder);
				MovingObjectPosition intercept = entityBb.calculateIntercept(startVec, endVec);
				if (intercept == null) {
					continue;
				}
				float currentHit = (float)intercept.hitVec.distanceTo(startVec);
				if (currentHit >= closestHit && currentHit != 0.0F) {
					continue;
				}
				closestHit = currentHit;
				closestHitEntity = ent;
			} 
		} 
		if (closestHitEntity != null) {
			blockHit = new MovingObjectPosition(closestHitEntity);
		}
		return blockHit;
	}

	private static MovingObjectPosition tracePathD(World w, double posX, double posY, double posZ, double v, double v1, double v2, float borderSize, HashSet<Entity> exclude) {
		return tracePath(w, (float)posX, (float)posY, (float)posZ, (float)v, (float)v1, (float)v2, borderSize, exclude);
	}

	public static MovingObjectPosition rayCast(EntityPlayerSP player, double x, double y, double z) {
		HashSet<Entity> excluded = new HashSet<>();
		excluded.add(player);
		return tracePathD(player.worldObj, player.posX, player.posY + player.getEyeHeight(), player.posZ, x, y, z, 1.0F, excluded);
	}

	public static int randomNumber(double d, double e) {
		int ii = (int)(-e + (int)(Math.random() * (d - -e + 1.0D)));
		return ii;
	}
}
