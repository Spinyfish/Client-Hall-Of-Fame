package club.mineman.antigamingchair.location;

import club.mineman.antigamingchair.AntiGamingChair;
import org.bukkit.Location;
import org.bukkit.World;

public class CustomLocation {
    private final long timestamp;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public CustomLocation(final double x, final double y, final double z, final float yaw, final float pitch) {
        this.timestamp = System.currentTimeMillis();
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static CustomLocation fromBukkitLocation(final Location location) {
        return new CustomLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public Location toBukkitLocation() {
        return new Location(AntiGamingChair.getInstance().getServer().getWorlds().get(0), this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public double getGroundDistanceTo(final CustomLocation location) {
        return Math.sqrt(Math.pow(this.x - location.x, 2.0) + Math.pow(this.z - location.z, 2.0));
    }

    public double getDistanceTo(final CustomLocation location) {
        return Math.sqrt(Math.pow(this.x - location.x, 2.0) + Math.pow(this.y - location.y, 2.0) + Math.pow(this.z - location.z, 2.0));
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public double getX() {
        return this.x;
    }

    public void setX(final double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(final double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(final double z) {
        this.z = z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(final float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }
}
