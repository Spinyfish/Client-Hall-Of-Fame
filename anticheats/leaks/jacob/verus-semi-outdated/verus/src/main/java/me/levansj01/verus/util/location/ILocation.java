package me.levansj01.verus.util.location;

import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.MutableBlockLocation;

public interface ILocation extends IVector3d {
    default boolean matches(ILocation location) {
        return this.sameXYZ(location) && this.sameYawPitch(location);
    }

    default Vector3d getDirection() {
        double cos = Math.cos(Math.toRadians(this.getPitch()));
        return new Vector3d(-cos * Math.sin(Math.toRadians(this.getYaw())), -Math.sin(Math.toRadians(this.getPitch())), cos * Math.cos(Math.toRadians((double)this.getYaw())));
    }

    default BasicPacketLocation pos(ILocation location) {
        return new BasicPacketLocation(location.getX(), location.getY(), location.getZ(), this.getYaw(), this.getPitch());
    }

    default Vector3d toEyeVector(double value) {
        return new Vector3d(this.getX(), this.getY() + value, this.getZ());
    }

    default boolean sameXYZ(ILocation location) {
        return this.sameXZ(location) && this.getY() == location.getY();
    }

    default MutableBlockLocation toBlock() {
        return new MutableBlockLocation((int)Math.floor(this.getX()), (int)Math.floor(this.getY()), (int)Math.floor(this.getZ()));
    }

    default Cuboid to(ILocation location, int value) {
        return this.distanceSquared(location) > (double)value ? new Cuboid(this) : new Cuboid(Math.min(this.getX(), location.getX()), Math.max(this.getX(), location.getX()), Math.min(this.getY(), location.getY()), Math.max(this.getY(), location.getY()), Math.min(this.getZ(), location.getZ()), Math.max(this.getZ(), location.getZ()));
    }

    default BasicPacketLocation look(ILocation location) {
        return new BasicPacketLocation(this.getX(), this.getY(), this.getZ(), location.getYaw(), location.getPitch());
    }

    default double distanceSquared(ILocation location) {
        return Math.pow(this.getX() - location.getX(), 2.0D) + Math.pow(this.getY() - location.getY(), 2.0D) + Math.pow(this.getZ() - location.getZ(), 2.0D);
    }

    default boolean sameXZ(ILocation location) {
        return this.getX() == location.getX() && this.getZ() == location.getZ();
    }

    default Vector3d toVector() {
        return new Vector3d(this.getX(), this.getY(), this.getZ());
    }

    float getPitch();

    default boolean sameYawPitch(ILocation location) {
        return this.getYaw() == location.getYaw() && this.getPitch() == location.getPitch();
    }

    /** @deprecated */
    @Deprecated
    default Vector3d toEyeVector(boolean bool) {
        return this.toEyeVector(bool ? 1.5399999618530273D : 1.6200000047683716D);
    }

    default double distance(ILocation location) {
        return Math.sqrt(this.distanceSquared(location));
    }

    default BasicPacketLocation withY(double var1) {
        return new BasicPacketLocation(this.getX(), var1, this.getZ(), this.getYaw(), this.getPitch());
    }

    default ILocation[] interpolate(ILocation location, int value) {
        ILocation[] var3 = new ILocation[value + 1];
        byte var4 = 0;
        int var13 = var4 + 1;
        var3[var4] = this;
        for(float f = (location.getPitch() - this.getPitch()) / (float)value; var13 < value; ++var13) {
            var3[var13] = new BasicPacketLocation(this.getX() + (location.getX() - this.getX()) / (double)value * (double)var13, this.getY() + (location.getY() - this.getY()) / (double)value * (double)var13, this.getZ() + (location.getZ() - this.getZ()) / (double)value * (double)var13, this.getYaw() + (location.getYaw() - this.getYaw()) / (float)value * (float)var13, this.getPitch() + f * (float)var13);
        }
        var3[value] = location;
        return var3;
    }
    float getYaw();
}
