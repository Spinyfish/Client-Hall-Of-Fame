package wtf.automn.utils.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import wtf.automn.utils.interfaces.MC;

public class RotationUtil implements wtf.automn.utils.interfaces.MC {

    public static Vec3 getBestHitVec(Entity entity) {
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
        float f11 = entity.getCollisionBorderSize();
        AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox().expand(f11, f11, f11);
        double ex = MathHelper.clamp_double(positionEyes.xCoord, entityBoundingBox.minX, entityBoundingBox.maxX);
        double ey = MathHelper.clamp_double(positionEyes.yCoord, entityBoundingBox.minY, entityBoundingBox.maxY);
        double ez = MathHelper.clamp_double(positionEyes.zCoord, entityBoundingBox.minZ, entityBoundingBox.maxZ);
        return new Vec3(ex, ey, ez);
    }

    public static float updateRotation(float current, float calc, float maxDelta) {
        float f = MathHelper.wrapAngleTo180_float(calc - current);
        if (f > maxDelta) {
            f = maxDelta;
        }

        if (f < -maxDelta) {
            f = -maxDelta;
        }
        return current + f;
    }

    public static float[] mouseSens(float yaw, float pitch, float lastYaw, float lastPitch) {
        if (mc.gameSettings.mouseSensitivity == 0.5) {
            mc.gameSettings.mouseSensitivity = 0.47887325F;
        }
        if (yaw == lastYaw && pitch == lastPitch) {
            return new float[]{yaw, pitch};
        }
        float f1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f2 = f1 * f1 * f1 * 8.0F;
        int deltaX = (int) ((6.667 * yaw - 6.667 * lastYaw) / f2);
        int deltaY = (int) ((6.667 * pitch - 6.667 * lastPitch) / f2) * -1;
        float f5 = deltaX * f2;
        float f3 = deltaY * f2;
        yaw = (float) ((double) lastYaw + (double) f5 * 0.15D);
        float f4 = (float) ((double) lastPitch - (double) f3 * 0.15D);
        pitch = MathHelper.clamp_float(f4, -90.0F, 90.0F);
        return new float[]{yaw, pitch};
    }

}
