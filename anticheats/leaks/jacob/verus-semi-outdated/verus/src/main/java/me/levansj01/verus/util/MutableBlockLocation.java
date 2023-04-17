package me.levansj01.verus.util;

import me.levansj01.verus.util.location.Vector3d;
import org.bukkit.block.Block;

public class MutableBlockLocation implements IBlockPosition {
    private int z,
            y,
            x;

    public MutableBlockLocation incrementZ() {
        return this.add(0, 0, 1);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public static MutableBlockLocation from(Vector3d vector3d) {
        return new MutableBlockLocation((int) Math.floor(vector3d.getX()), (int) Math.floor(vector3d.getY()), (int) Math.floor(vector3d.getZ()));
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof MutableBlockLocation)) {
            return false;
        }
        MutableBlockLocation mutableBlockLocation = (MutableBlockLocation) object;
        return mutableBlockLocation.canEqual(this) && this.getX() == mutableBlockLocation.getX() && this.getY() == mutableBlockLocation.getY() && this.getZ() == mutableBlockLocation.getZ();
    }

    @Override
    public int getX() {
        return this.x;
    }

    public MutableBlockLocation add(int n, int n2, int n3) {
        this.x += n;
        this.y += n2;
        this.z += n3;
        return this;
    }

    @Override
    public String toString() {
        return "MutableBlockLocation(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ")";
    }

    @Override
    public int getY() {
        return this.y;
    }

    public MutableBlockLocation incrementY() {
        return this.add(0, 1, 0);
    }

    public MutableBlockLocation andThen(Vector3d vector3d) {
        this.x = (int) Math.floor(vector3d.getX());
        this.y = (int) Math.floor(vector3d.getY());
        this.z = (int) Math.floor(vector3d.getZ());
        return this;
    }

    public MutableBlockLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static MutableBlockLocation from(Block block) {
        return new MutableBlockLocation(block.getX(), block.getY(), block.getZ());
    }

    public MutableBlockLocation andThen(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    public void setY(int y) {
        this.y = y;
    }

    public MutableBlockLocation incrementX() {
        return this.add(1, 0, 0);
    }

    protected boolean canEqual(Object object) {
        return object instanceof MutableBlockLocation;
    }
}
