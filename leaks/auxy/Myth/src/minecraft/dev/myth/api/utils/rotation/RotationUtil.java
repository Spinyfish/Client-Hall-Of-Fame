/**
 * @project Myth
 * @author CodeMan
 * @at 04.01.23, 15:27
 */
package dev.myth.api.utils.rotation;

import dev.myth.api.interfaces.IMethods;
import dev.myth.events.MoveFlyingEvent;
import dev.myth.events.UpdateEvent;
import lombok.experimental.UtilityClass;
import net.minecraft.util.*;

@UtilityClass
public class RotationUtil implements IMethods {

    public float[] getRotationsToVector(Vec3 to, Vec3 from) {
        double d0 = to.xCoord - from.xCoord;
        double d1 = to.yCoord - from.yCoord;
        double d2 = to.zCoord - from.zCoord;
        double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float f = (float) (MathHelper.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
        float f1 = (float) (-(MathHelper.atan2(d1, d3) * 180.0D / Math.PI));
        return new float[] {f, f1};
    }

    public float[] getRotationsToBlock(BlockPos block, EnumFacing face) {
        double x = block.getX() + 0.5 + (double) face.getFrontOffsetX() / 2;
        double z = block.getZ() + 0.5 + (double) face.getFrontOffsetZ() / 2;
        double y = block.getY() + 0.5 + (double) face.getFrontOffsetY() / 2;
        double dy = MC.thePlayer.posY + MC.thePlayer.getEyeHeight() - y;
        double dx = x - MC.thePlayer.posX;
        double dz = z - MC.thePlayer.posZ;
        double d3 = MathHelper.sqrt_double(dx * dx + dz * dz);
        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (Math.atan2(dy, d3) * 180.0D / Math.PI);
        return new float[] {yaw, pitch};
    }

    public Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }

    public MovingObjectPosition rayTrace(double blockReachDistance, float yaw, float pitch) {
        Vec3 vec3 = MC.thePlayer.getPositionEyes(1F);
        Vec3 vec31 = getVectorForRotation(pitch, yaw);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
        return MC.thePlayer.worldObj.rayTraceBlocks(vec3, vec32, false, false, true);
    }

    public void doRotation(UpdateEvent event, float[] rots, float speed, boolean silent) {
        float[] prevRots = {event.getLastYaw(), event.getLastPitch()};

        float[] limitedRots = {
                limitAngle(prevRots[0], rots[0], speed),
                limitAngle(prevRots[1], rots[1], speed)
        };

        limitedRots[0] = MathHelper.wrapAngleTo180_float(limitedRots[0] - prevRots[0]) + prevRots[0];
        limitedRots[1] = MathHelper.wrapAngleTo180_float(limitedRots[1] - prevRots[1]) + prevRots[1];

        limitedRots = applyGCD(limitedRots, prevRots);

        if(!silent) {
            MC.thePlayer.rotationYaw = limitedRots[0];
            MC.thePlayer.rotationPitch = limitedRots[1];
        }
        event.setYaw(limitedRots[0]);
        event.setPitch(limitedRots[1]);
    }

    // beschissener gcd fix
    public float[] applyGCD(float[] rots, float[] prevRots) {
        float[] deltaRots = {
                rots[0] - prevRots[0],
                rots[1] - prevRots[1]
        };

        float f = MC.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 1.2F;

        rots[0] -= deltaRots[0] % f1;
        rots[1] -= deltaRots[1] % f1;

        return rots;
    }

    public float limitAngle(float prev, float target, float speed) {
        float dif = MathHelper.wrapAngleTo180_float(target - prev);
        if (dif > speed) dif = speed;
        if (dif < -speed) dif = -speed;
        return prev + dif;
    }

    public float[] getSilentMovementValues(float yaw) {
        float diff = MathHelper.wrapAngleTo180_float(MC.thePlayer.rotationYaw - yaw);
        float f = MathHelper.sin(diff * ((float) Math.PI / 180F));
        float f1 = MathHelper.cos(diff * ((float) Math.PI / 180F));
        float multiplier = 1f;
        if (MC.thePlayer.isSneaking() || MC.thePlayer.isUsingItem()) multiplier = 10;
        float forward = (float) (Math.round((MC.thePlayer.moveForward * (double) f1 + MC.thePlayer.moveStrafing * (double) f) * multiplier)) / multiplier;
        float strafe = (float) (Math.round((MC.thePlayer.moveStrafing * (double) f1 - MC.thePlayer.moveForward * (double) f) * multiplier)) / multiplier;
        forward *= 0.98;
        strafe *= 0.98;
        return new float[] { forward, strafe };
    }
}
