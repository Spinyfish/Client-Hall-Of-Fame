package club.mineman.antigamingchair.util;

import club.mineman.antigamingchair.location.CustomLocation;
import net.minecraft.server.v1_8_R3.MathHelper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MathUtil {
    public static CustomLocation getLocationInFrontOfPlayer(final Player player, final double distance) {
        return new CustomLocation(0.0, 0.0, 0.0, 0.0f, 0.0f);
    }

    public static CustomLocation getLocationInFrontOfLocation(final double x, final double y, final double z, final float yaw, final float pitch, final double distance) {
        return new CustomLocation(0.0, 0.0, 0.0, 0.0f, 0.0f);
    }

    public static boolean isMouseOverEntity(final Player player) {
        return rayTrace(player, 6.0) != null;
    }

    public static Entity rayTrace(final Player player, final double distance) {
        final CustomLocation playerLocation = CustomLocation.fromBukkitLocation(player.getLocation());
        Entity currentTarget = null;
        float lowestFov = Float.MAX_VALUE;
        for (final Entity entity : player.getNearbyEntities(distance, distance, distance)) {
            final CustomLocation entityLocation = CustomLocation.fromBukkitLocation(entity.getLocation());
            final float fov = getRotationFromPosition(playerLocation, entityLocation)[0] - playerLocation.getYaw();
            final double groundDistance = playerLocation.getGroundDistanceTo(entityLocation);
            if (lowestFov < fov && fov < groundDistance + 2.0) {
                currentTarget = entity;
                lowestFov = fov;
            }
        }
        return currentTarget;
    }

    public static float[] getRotationFromPosition(final CustomLocation playerLocation, final CustomLocation targetLocation) {
        final double xDiff = targetLocation.getX() - playerLocation.getX();
        final double zDiff = targetLocation.getZ() - playerLocation.getZ();
        final double yDiff = targetLocation.getY() - (playerLocation.getY() + 0.4);
        final double dist = (double) MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
        final float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float) (-(Math.atan2(yDiff, dist) * 180.0 / 3.141592653589793));
        return new float[]{yaw, pitch};
    }

    public static double getDistanceBetweenAngles(final float angle1, final float angle2) {
        float distance = Math.abs(angle1 - angle2) % 360.0f;
        if (distance > 180.0f) {
            distance = 360.0f - distance;
        }
        return distance;
    }

    public static int pingFormula(final long ping) {
        return (int) Math.ceil((double) ping / 50.0);
    }
}
