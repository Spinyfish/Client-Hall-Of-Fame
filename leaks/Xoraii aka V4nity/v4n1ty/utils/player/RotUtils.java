package v4n1ty.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import v4n1ty.utils.player.ScaffoldUtils;

public class RotUtils {

    /*
     * Sets the player's head rotations to the given yaw and pitch (visual-only).
     */

    public static void setRotations(float yaw, float pitch) {
        Minecraft.getMinecraft().thePlayer.rotationYawHead = Minecraft.getMinecraft().thePlayer.renderYawOffset = yaw;
        Minecraft.getMinecraft().thePlayer.rotationPitchHead = pitch;
    }

    public static float clampRotation() {
        float rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        float n = 1.0f;
        if (Minecraft.getMinecraft().thePlayer.movementInput.moveForward < 0.0f) {
            rotationYaw += 180.0f;
            n = -0.5f;
        }
        else if (Minecraft.getMinecraft().thePlayer.movementInput.moveForward > 0.0f) {
            n = 0.5f;
        }
        if (Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe > 0.0f) {
            rotationYaw -= 90.0f * n;
        }
        if (Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe < 0.0f) {
            rotationYaw += 90.0f * n;
        }
        return rotationYaw * 0.017453292f;
    }

    public static float getSensitivityMultiplier() {
        float SENSITIVITY = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        return (SENSITIVITY * SENSITIVITY * SENSITIVITY * 8.0F) * 0.15F;
    }

    public static float smoothRotation(float from, float to, float speed) {
        float f = MathHelper.wrapAngleTo180_float(to - from);

        if (f > speed) {
            f = speed;
        }

        if (f < -speed) {
            f = -speed;
        }

        return from + f;
    }

    /*
     * Sets the player's head rotations to the given yaw and pitch (visual-only).
     */
    public static void setRotations(float[] rotations) {
        setRotations(rotations[0], rotations[1]);
    }

    public static float[] getFacingRotations(ScaffoldUtils.BlockCache blockCache) {
        double d1 = blockCache.position.getX() + 0.5D - Minecraft.getMinecraft().thePlayer.posX + blockCache.facing.getFrontOffsetX() / 2.0D;
        double d2 = blockCache.position.getZ() + 0.5D - Minecraft.getMinecraft().thePlayer.posZ + blockCache.facing.getFrontOffsetZ() / 2.0D;
        double d3 = Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight() - (blockCache.position.getY());
        double d4 = MathHelper.sqrt_double(d1 * d1 + d2 * d2);
        float f1 = (float) (Math.atan2(d2, d1) * 180.0D / Math.PI) - 90.0F;
        float f2 = (float) (Math.atan2(d3, d4) * 180.0D / Math.PI);
        if (f1 < 0.0F) {
            f1 += 360.0F;
        }
        return new float[]{f1, f2};
    }

    public static float[] getRotationsNeeded(final Entity entity) {
        if (entity == null) {
            return null;
        }
        Minecraft mc = Minecraft.getMinecraft();
        final double xSize = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
        final double ySize = entity.posY + entity.getEyeHeight() / 2 - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
        final double zSize = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        final double theta = MathHelper.sqrt_double(xSize * xSize + zSize * zSize);
        final float yaw = (float) (Math.atan2(zSize, xSize) * 180 / Math.PI) - 90;
        final float pitch = (float) (-(Math.atan2(ySize, theta) * 180 / Math.PI));
        return new float[]{(Minecraft.getMinecraft().thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw)) % 360, (Minecraft.getMinecraft().thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch)) % 360.0f};
    }

    public static float[] getFacingRotations2(final int paramInt1, final double d, final int paramInt3) {
        final EntitySnowball localEntityPig = new EntitySnowball(Minecraft.getMinecraft().theWorld);
        localEntityPig.posX = paramInt1 + 0.5;
        localEntityPig.posY = d + 0.5;
        localEntityPig.posZ = paramInt3 + 0.5;
        return getRotationsNeeded(localEntityPig);
    }

    public static float getYaw(Vec3 to) {
        float x = (float) (to.xCoord - Minecraft.getMinecraft().thePlayer.posX);
        float z = (float) (to.zCoord - Minecraft.getMinecraft().thePlayer.posZ);
        float var1 = (float) (StrictMath.atan2(z, x) * 180.0D / StrictMath.PI) - 90.0F;
        float rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        return rotationYaw + MathHelper.wrapAngleTo180_float(var1 - rotationYaw);
    }

    public static Vec3 getVecRotations(float yaw, float pitch) {
        double d = Math.cos(Math.toRadians(-yaw) - Math.PI);
        double d1 = Math.sin(Math.toRadians(-yaw) - Math.PI);
        double d2 = -Math.cos(Math.toRadians(-pitch));
        double d3 = Math.sin(Math.toRadians(-pitch));
        return new Vec3(d1 * d2, d3, d * d2);
    }

    public static float[] getRotations(double posX, double posY, double posZ) {
        double x = posX - Minecraft.getMinecraft().thePlayer.posX, z = posZ - Minecraft.getMinecraft().thePlayer.posZ, y = posY + Minecraft.getMinecraft().thePlayer.getEyeHeight() - Minecraft.getMinecraft().thePlayer.posY;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (MathHelper.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(y, d3) * 180.0D / Math.PI));
        return new float[]{yaw, pitch};
    }

}
