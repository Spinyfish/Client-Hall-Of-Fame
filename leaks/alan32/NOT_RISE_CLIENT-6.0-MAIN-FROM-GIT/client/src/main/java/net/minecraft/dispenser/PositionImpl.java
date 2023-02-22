package net.minecraft.dispenser;

public class PositionImpl implements IPosition {
    protected final double x;
    protected final double y;
    protected final double z;

    public PositionImpl(final double xCoord, final double yCoord, final double zCoord) {
        this.x = xCoord;
        this.y = yCoord;
        this.z = zCoord;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }
}
