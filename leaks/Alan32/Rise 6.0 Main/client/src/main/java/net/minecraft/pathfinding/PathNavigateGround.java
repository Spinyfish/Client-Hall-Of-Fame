package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public class PathNavigateGround extends PathNavigate {
    protected WalkNodeProcessor nodeProcessor;
    private boolean shouldAvoidSun;

    public PathNavigateGround(final EntityLiving entitylivingIn, final World worldIn) {
        super(entitylivingIn, worldIn);
    }

    protected PathFinder getPathFinder() {
        this.nodeProcessor = new WalkNodeProcessor();
        this.nodeProcessor.setEnterDoors(true);
        return new PathFinder(this.nodeProcessor);
    }

    /**
     * If on ground or swimming and can swim
     */
    protected boolean canNavigate() {
        return this.theEntity.onGround || this.getCanSwim() && this.isInLiquid() || this.theEntity.isRiding() && this.theEntity instanceof EntityZombie && this.theEntity.ridingEntity instanceof EntityChicken;
    }

    protected Vec3 getEntityPosition() {
        return new Vec3(this.theEntity.posX, this.getPathablePosY(), this.theEntity.posZ);
    }

    /**
     * Gets the safe pathing Y position for the entity depending on if it can path swim or not
     */
    private int getPathablePosY() {
        if (this.theEntity.isInWater() && this.getCanSwim()) {
            int i = (int) this.theEntity.getEntityBoundingBox().minY;
            Block block = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.theEntity.posX), i, MathHelper.floor_double(this.theEntity.posZ))).getBlock();
            int j = 0;

            while (block == Blocks.flowing_water || block == Blocks.water) {
                ++i;
                block = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.theEntity.posX), i, MathHelper.floor_double(this.theEntity.posZ))).getBlock();
                ++j;

                if (j > 16) {
                    return (int) this.theEntity.getEntityBoundingBox().minY;
                }
            }

            return i;
        } else {
            return (int) (this.theEntity.getEntityBoundingBox().minY + 0.5D);
        }
    }

    /**
     * Trims path data from the end to the first sun covered block
     */
    protected void removeSunnyPath() {
        super.removeSunnyPath();

        if (this.shouldAvoidSun) {
            if (this.worldObj.canSeeSky(new BlockPos(MathHelper.floor_double(this.theEntity.posX), (int) (this.theEntity.getEntityBoundingBox().minY + 0.5D), MathHelper.floor_double(this.theEntity.posZ)))) {
                return;
            }

            for (int i = 0; i < this.currentPath.getCurrentPathLength(); ++i) {
                final PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);

                if (this.worldObj.canSeeSky(new BlockPos(pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord))) {
                    this.currentPath.setCurrentPathLength(i - 1);
                    return;
                }
            }
        }
    }

    /**
     * Returns true when an entity of specified size could safely walk in a straight line between the two points. Args:
     * pos1, pos2, entityXSize, entityYSize, entityZSize
     */
    protected boolean isDirectPathBetweenPoints(final Vec3 posVec31, final Vec3 posVec32, int sizeX, final int sizeY, int sizeZ) {
        int i = MathHelper.floor_double(posVec31.xCoord);
        int j = MathHelper.floor_double(posVec31.zCoord);
        double d0 = posVec32.xCoord - posVec31.xCoord;
        double d1 = posVec32.zCoord - posVec31.zCoord;
        final double d2 = d0 * d0 + d1 * d1;

        if (d2 < 1.0E-8D) {
            return false;
        } else {
            final double d3 = 1.0D / Math.sqrt(d2);
            d0 = d0 * d3;
            d1 = d1 * d3;
            sizeX = sizeX + 2;
            sizeZ = sizeZ + 2;

            if (!this.isSafeToStandAt(i, (int) posVec31.yCoord, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
                return false;
            } else {
                sizeX = sizeX - 2;
                sizeZ = sizeZ - 2;
                final double d4 = 1.0D / Math.abs(d0);
                final double d5 = 1.0D / Math.abs(d1);
                double d6 = (double) (i * 1) - posVec31.xCoord;
                double d7 = (double) (j * 1) - posVec31.zCoord;

                if (d0 >= 0.0D) {
                    ++d6;
                }

                if (d1 >= 0.0D) {
                    ++d7;
                }

                d6 = d6 / d0;
                d7 = d7 / d1;
                final int k = d0 < 0.0D ? -1 : 1;
                final int l = d1 < 0.0D ? -1 : 1;
                final int i1 = MathHelper.floor_double(posVec32.xCoord);
                final int j1 = MathHelper.floor_double(posVec32.zCoord);
                int k1 = i1 - i;
                int l1 = j1 - j;

                while (k1 * k > 0 || l1 * l > 0) {
                    if (d6 < d7) {
                        d6 += d4;
                        i += k;
                        k1 = i1 - i;
                    } else {
                        d7 += d5;
                        j += l;
                        l1 = j1 - j;
                    }

                    if (!this.isSafeToStandAt(i, (int) posVec31.yCoord, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    /**
     * Returns true when an entity could stand at a position, including solid blocks under the entire entity.
     */
    private boolean isSafeToStandAt(final int x, final int y, final int z, final int sizeX, final int sizeY, final int sizeZ, final Vec3 vec31, final double p_179683_8_, final double p_179683_10_) {
        final int i = x - sizeX / 2;
        final int j = z - sizeZ / 2;

        if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_)) {
            return false;
        } else {
            for (int k = i; k < i + sizeX; ++k) {
                for (int l = j; l < j + sizeZ; ++l) {
                    final double d0 = (double) k + 0.5D - vec31.xCoord;
                    final double d1 = (double) l + 0.5D - vec31.zCoord;

                    if (d0 * p_179683_8_ + d1 * p_179683_10_ >= 0.0D) {
                        final Block block = this.worldObj.getBlockState(new BlockPos(k, y - 1, l)).getBlock();
                        final Material material = block.getMaterial();

                        if (material == Material.air) {
                            return false;
                        }

                        if (material == Material.water && !this.theEntity.isInWater()) {
                            return false;
                        }

                        if (material == Material.lava) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    /**
     * Returns true if an entity does not collide with any solid blocks at the position.
     */
    private boolean isPositionClear(final int p_179692_1_, final int p_179692_2_, final int p_179692_3_, final int p_179692_4_, final int p_179692_5_, final int p_179692_6_, final Vec3 p_179692_7_, final double p_179692_8_, final double p_179692_10_) {
        for (final BlockPos blockpos : BlockPos.getAllInBox(new BlockPos(p_179692_1_, p_179692_2_, p_179692_3_), new BlockPos(p_179692_1_ + p_179692_4_ - 1, p_179692_2_ + p_179692_5_ - 1, p_179692_3_ + p_179692_6_ - 1))) {
            final double d0 = (double) blockpos.getX() + 0.5D - p_179692_7_.xCoord;
            final double d1 = (double) blockpos.getZ() + 0.5D - p_179692_7_.zCoord;

            if (d0 * p_179692_8_ + d1 * p_179692_10_ >= 0.0D) {
                final Block block = this.worldObj.getBlockState(blockpos).getBlock();

                if (!block.isPassable(this.worldObj, blockpos)) {
                    return false;
                }
            }
        }

        return true;
    }

    public void setAvoidsWater(final boolean avoidsWater) {
        this.nodeProcessor.setAvoidsWater(avoidsWater);
    }

    public boolean getAvoidsWater() {
        return this.nodeProcessor.getAvoidsWater();
    }

    public void setBreakDoors(final boolean canBreakDoors) {
        this.nodeProcessor.setBreakDoors(canBreakDoors);
    }

    public void setEnterDoors(final boolean par1) {
        this.nodeProcessor.setEnterDoors(par1);
    }

    public boolean getEnterDoors() {
        return this.nodeProcessor.getEnterDoors();
    }

    public void setCanSwim(final boolean canSwim) {
        this.nodeProcessor.setCanSwim(canSwim);
    }

    public boolean getCanSwim() {
        return this.nodeProcessor.getCanSwim();
    }

    public void setAvoidSun(final boolean par1) {
        this.shouldAvoidSun = par1;
    }
}
