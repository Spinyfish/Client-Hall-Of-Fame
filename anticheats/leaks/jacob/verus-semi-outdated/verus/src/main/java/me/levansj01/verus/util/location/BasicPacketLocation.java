package me.levansj01.verus.util.location;

public class BasicPacketLocation implements ILocation
{
    protected final double z;
    protected final float yaw;
    protected final double y;
    protected final double x;
    protected final float pitch;
    
    @Override
    public String toString() {
        return "BasicPacketLocation(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ")";
    }
    
    public BasicPacketLocation(final double x, final double y, final double z, final float yaw, final float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    protected boolean canEqual(final Object o) {
        return o instanceof BasicPacketLocation;
    }
    
    @Override
    public float getYaw() {
        return this.yaw;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BasicPacketLocation)) {
            return false;
        }
        final BasicPacketLocation basicPacketLocation = (BasicPacketLocation)o;
        return basicPacketLocation.canEqual(this) && Double.compare(this.getX(), basicPacketLocation.getX()) == 0 && Double.compare(this.getY(), basicPacketLocation.getY()) == 0 && Double.compare(this.getZ(), basicPacketLocation.getZ()) == 0;
    }
    
    @Override
    public float getPitch() {
        return this.pitch;
    }
    
    @Override
    public int hashCode() {
        final long doubleToLongBits = Double.doubleToLongBits(this.getX());
        final int n = 59 + (int)(doubleToLongBits >>> 32 ^ doubleToLongBits);
        final long doubleToLongBits2 = Double.doubleToLongBits(this.getY());
        final int n2 = 59 + (int)(doubleToLongBits2 >>> 32 ^ doubleToLongBits2);
        final long doubleToLongBits3 = Double.doubleToLongBits(this.getZ());
        final int n3 = 59 + (int)(doubleToLongBits3 >>> 32 ^ doubleToLongBits3);
        return 1;
    }
    
    @Override
    public double getZ() {
        return this.z;
    }
    
    @Override
    public double getX() {
        return this.x;
    }
    
    @Override
    public double getY() {
        return this.y;
    }
}
