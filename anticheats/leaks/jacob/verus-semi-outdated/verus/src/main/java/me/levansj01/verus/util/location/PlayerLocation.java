package me.levansj01.verus.util.location;


import me.levansj01.verus.util.java.MathHelper;

@Deprecated
public class PlayerLocation implements ILocationGround
{
    private double z;
    private float yaw;
    private double y;
    private double x;
    private int tickTime;
    private long timestamp;
    private Boolean ground;
    private float pitch;
    private boolean teleport;
    
    public PlayerLocation(final long timestamp, final int tickTime, final double x, final double y, final double z, final float yaw, final float pitch, final Boolean ground, final boolean teleport) {
        this.timestamp = timestamp;
        this.tickTime = tickTime;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.ground = ground;
        this.teleport = teleport;
    }
    
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }
    
    public PlayerLocation up() {
        return this.add(0.0, 1.0, 0.0);
    }
    
    public PlayerLocation clone() {
        return new PlayerLocation(this.timestamp, this.tickTime, this.x, this.y, this.z, this.yaw, this.pitch, this.ground, this.teleport);
    }
    
    @Override
    public double getY() {
        return this.y;
    }
    
    public void setTickTime(final int tickTime) {
        this.tickTime = tickTime;
    }
    
    @Override
    public float getYaw() {
        return this.yaw;
    }
    
    @Override
    public float getPitch() {
        return this.pitch;
    }
    
    public void setZ(final double z) {
        this.z = z;
    }
    
    public void setX(final double x) {
        this.x = x;
    }
    
    public void setTeleport(final boolean teleport) {
        this.teleport = teleport;
    }
    
    public void setGround(final Boolean ground) {
        this.ground = ground;
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final PlayerLocation playerLocation = (PlayerLocation)o;
        return this.timestamp == playerLocation.timestamp && this.tickTime == playerLocation.tickTime && Double.compare(playerLocation.x, this.x) == 0 && Double.compare(playerLocation.y, this.y) == 0 && Double.compare(playerLocation.z, this.z) == 0 && Float.compare(playerLocation.yaw, this.yaw) == 0 && Float.compare(playerLocation.pitch, this.pitch) == 0;
    }
    
    public double distanceXZ(final IVector3d vector3d) {
        return MathHelper.sqrt_double(this.distanceXZSquared(vector3d));
    }
    
    @Override
    public double getX() {
        return this.x;
    }
    
    public PlayerLocation add(final double n, final double n2, final double n3) {
        return new PlayerLocation(this.timestamp, this.tickTime, this.x + n, this.y + n2, this.z + n3, this.yaw, this.pitch, this.ground, this.teleport);
    }
    
    public double distanceXZSquared(final IVector3d vector3d) {
        return Math.pow(this.x - vector3d.getX(), 2.0) + Math.pow(this.z - vector3d.getZ(), 2.0);
    }
    
    public Boolean getGround() {
        return this.ground;
    }
    
    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }
    
    @Override
    public int hashCode() {
        final int n = 31 * (int)(this.timestamp ^ this.timestamp >>> 32) + this.tickTime;
        final long doubleToLongBits = Double.doubleToLongBits(this.x);
        final int n2 = 31 * n + (int)(doubleToLongBits ^ doubleToLongBits >>> 32);
        final long doubleToLongBits2 = Double.doubleToLongBits(this.y);
        final int n3 = 31 * n2 + (int)(doubleToLongBits2 ^ doubleToLongBits2 >>> 32);
        final long doubleToLongBits3 = Double.doubleToLongBits(this.z);
        return 31 * (31 * (31 * n3 + (int)(doubleToLongBits3 ^ doubleToLongBits3 >>> 32)) + ((this.yaw != 0.0f) ? Float.floatToIntBits(this.yaw) : 0)) + ((this.pitch != 0.0f) ? Float.floatToIntBits(this.pitch) : 0);
    }
    
    public boolean isTeleport() {
        return this.teleport;
    }
    
    @Override
    public boolean isGround() {
        return this.ground != null && this.ground;
    }
    
    public void setYaw(final float yaw) {
        this.yaw = yaw;
    }
    
    @Override
    public double getZ() {
        return this.z;
    }
    
    public int getTickTime() {
        return this.tickTime;
    }
    
    public void setY(final double y) {
        this.y = y;
    }
}
