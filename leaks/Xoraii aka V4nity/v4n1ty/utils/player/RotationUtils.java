package v4n1ty.utils.player;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.Minecraft;

public class RotationUtils
{
    public static float yaw;
    public static float renderYawOffset;
    public static float pitch;
    public static boolean RotationInUse;
    static Minecraft mc;

    public static float[] lookAtPosBed(final double x, final double y, final double z) {
        double dirx = mc.thePlayer.posX - x;
        double diry = mc.thePlayer.posY - y;
        double dirz = mc.thePlayer.posZ - z;
        final double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        float yaw = (float)Math.atan2(dirz, dirx);
        float pitch = (float)((float)Math.asin(diry) + 0.3);
        pitch = (float)(pitch * 180.0 / 3.141592653589793);
        yaw = (float)(yaw * 180.0 / 3.141592653589793);
        yaw += 90.0;
        final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f3 = f2 * f2 * f2 * 1.2f;
        yaw -= yaw % f3;
        pitch -= pitch % (f3 * f2);
        return new float[] { yaw, pitch };
    }

    public static void setRotation(final float y, float p) {
        if (p > 90.0f) {
            p = 90.0f;
        }
        else if (p < -90.0f) {
            p = -90.0f;
        }
        yaw = y;
        pitch = p;
        RotationInUse = true;
    }

    public static boolean setYaw(final float y, final float speed) {
        setRotation(yaw, pitch);
        if (speed >= 360.0f) {
            yaw = y;
            return true;
        }
        if (isInRange(yaw, y, speed) || speed >= 360.0f) {
            yaw = y;
            return true;
        }
        if (getRotation(yaw, y) < 0) {
            yaw -= speed;
        }
        else {
            yaw += speed;
        }
        return false;
    }

    public static int getRotation(double before, float after) {
        while (before > 360.0) {
            before -= 360.0;
        }
        while (before < 0.0) {
            before += 360.0;
        }
        while (after > 360.0f) {
            after -= 360.0f;
        }
        while (after < 0.0f) {
            after += 360.0f;
        }
        if (before > after) {
            if (before - after > 180.0) {
                return 1;
            }
            return -1;
        }
        else {
            if (after - before > 180.0) {
                return -1;
            }
            return 1;
        }
    }

    public static boolean isInRange(double before, float after, final float max) {
        while (before > 360.0) {
            before -= 360.0;
        }
        while (before < 0.0) {
            before += 360.0;
        }
        while (after > 360.0f) {
            after -= 360.0f;
        }
        while (after < 0.0f) {
            after += 360.0f;
        }
        if (before > after) {
            return (before - after > 180.0 && 360.0 - before - after <= max) || before - after <= max;
        }
        return (after - before > 180.0 && 360.0f - after - before <= max) || after - before <= max;
    }

    public static boolean setPitch(float p, final float speed) {
        if (p > 90.0f) {
            p = 90.0f;
        }
        else if (p < -90.0f) {
            p = -90.0f;
        }
        if (Math.abs(pitch - p) <= speed || speed >= 360.0f) {
            pitch = p;
            return false;
        }
        if (p < pitch) {
            pitch -= speed;
        }
        else {
            pitch += speed;
        }
        return true;
    }

    static {
        mc = Minecraft.getMinecraft();
    }
}