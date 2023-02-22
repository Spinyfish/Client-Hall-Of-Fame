package net.minecraft.world.pathfinder;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class SwimNodeProcessor extends NodeProcessor {
    public void initProcessor(final IBlockAccess iblockaccessIn, final Entity entityIn) {
        super.initProcessor(iblockaccessIn, entityIn);
    }

    /**
     * This method is called when all nodes have been processed and PathEntity is created.
     * {@link net.minecraft.world.pathfinder.WalkNodeProcessor WalkNodeProcessor} uses this to change its field {@link
     * net.minecraft.world.pathfinder.WalkNodeProcessor#avoidsWater avoidsWater}
     */
    public void postProcess() {
        super.postProcess();
    }

    /**
     * Returns given entity's position as PathPoint
     */
    public PathPoint getPathPointTo(final Entity entityIn) {
        return this.openPoint(MathHelper.floor_double(entityIn.getEntityBoundingBox().minX), MathHelper.floor_double(entityIn.getEntityBoundingBox().minY + 0.5D), MathHelper.floor_double(entityIn.getEntityBoundingBox().minZ));
    }

    /**
     * Returns PathPoint for given coordinates
     *
     * @param entityIn entity which size will be used to center position
     * @param x        target x coordinate
     * @param y        target y coordinate
     * @param target   z coordinate
     */
    public PathPoint getPathPointToCoords(final Entity entityIn, final double x, final double y, final double target) {
        return this.openPoint(MathHelper.floor_double(x - (double) (entityIn.width / 2.0F)), MathHelper.floor_double(y + 0.5D), MathHelper.floor_double(target - (double) (entityIn.width / 2.0F)));
    }

    public int findPathOptions(final PathPoint[] pathOptions, final Entity entityIn, final PathPoint currentPoint, final PathPoint targetPoint, final float maxDistance) {
        int i = 0;

        for (final EnumFacing enumfacing : EnumFacing.values()) {
            final PathPoint pathpoint = this.getSafePoint(entityIn, currentPoint.xCoord + enumfacing.getFrontOffsetX(), currentPoint.yCoord + enumfacing.getFrontOffsetY(), currentPoint.zCoord + enumfacing.getFrontOffsetZ());

            if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint;
            }
        }

        return i;
    }

    /**
     * Returns a point that the entity can safely move to
     */
    private PathPoint getSafePoint(final Entity entityIn, final int x, final int y, final int z) {
        final int i = this.func_176186_b(entityIn, x, y, z);
        return i == -1 ? this.openPoint(x, y, z) : null;
    }

    private int func_176186_b(final Entity entityIn, final int x, final int y, final int z) {
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int i = x; i < x + this.entitySizeX; ++i) {
            for (int j = y; j < y + this.entitySizeY; ++j) {
                for (int k = z; k < z + this.entitySizeZ; ++k) {
                    final Block block = this.blockaccess.getBlockState(blockpos$mutableblockpos.func_181079_c(i, j, k)).getBlock();

                    if (block.getMaterial() != Material.water) {
                        return 0;
                    }
                }
            }
        }

        return -1;
    }
}
