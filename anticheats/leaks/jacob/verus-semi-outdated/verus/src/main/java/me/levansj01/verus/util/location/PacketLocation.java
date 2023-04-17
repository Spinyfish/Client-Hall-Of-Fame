package me.levansj01.verus.util.location;

public class PacketLocation extends BasicPacketLocation implements IPacketLocation {
    private final boolean teleport;
    private final boolean pos;
    private final boolean look;
    private final boolean ground;

    public PacketLocation add(final double n, final double n2, final double n3) {
        return new PacketLocation(this.x + n, this.y + n2, this.z + n3, this.yaw, this.pitch, this.ground, true, this.look, this.teleport);
    }

    @Override
    public boolean isGround() {
        return this.ground;
    }

    public PacketLocation(final double n, final double n2, final double n3, final float n4, final float n5, final boolean ground, final boolean pos, final boolean look, final boolean teleport) {
        super(n, n2, n3, n4, n5);
        this.ground = ground;
        this.pos = pos;
        this.look = look;
        this.teleport = teleport;
    }

    @Override
    public boolean isLook() {
        return this.look;
    }

    public PacketLocation up() {
        return this.add(0.0, 1.0, 0.0);
    }

    public boolean isTeleport() {
        return this.teleport;
    }

    @Override
    public boolean isPos() {
        return this.pos;
    }
}
