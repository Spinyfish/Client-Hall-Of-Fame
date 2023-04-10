package crispy.util.pathfinder;

import net.minecraft.util.BlockPos;

public class PathPos extends BlockPos {
    private final boolean jumping;

    public PathPos(BlockPos pos) {
        this(pos, false);
    }

    public PathPos(BlockPos pos, boolean jumping) {
        super(pos);
        this.jumping = jumping;
    }

    public boolean isJumping() {
        return jumping;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof PathPos))
            return false;

        PathPos node = (PathPos) obj;
        return getX() == node.getX() && getY() == node.getY()
                && getZ() == node.getZ() && isJumping() == node.isJumping();
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 2 + (isJumping() ? 1 : 0);
    }
}