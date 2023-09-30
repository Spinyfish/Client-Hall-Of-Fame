package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

import java.util.Iterator;

public class BlockPosition extends Vec3i {
    /**
     * The BlockPos with all coordinates 0
     */
    public static final BlockPosition ORIGIN = new BlockPosition(0, 0, 0), NEGATIVE = new BlockPosition(-1, -1, -1);
    private static final int NUM_X_BITS = 1 + MathHelper.calculateLogBaseTwo(MathHelper.roundUpToPowerOfTwo(30000000));
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
    private static final int Y_SHIFT = NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    public BlockPosition(int x, int y, int z) {
        super(x, y, z);
    }

    public BlockPosition(double x, double y, double z) {
        super(x, y, z);
    }

    public BlockPosition(Entity source) {
        this(source.posX, source.posY, source.posZ);
    }

    public BlockPosition(Vec3 source) {
        this(source.xCoord, source.yCoord, source.zCoord);
    }

    public BlockPosition(Vec3i source) {
        this(source.getX(), source.getY(), source.getZ());
    }

    /**
     * Add the given coordinates to the coordinates of this BlockPos
     */
    public BlockPosition add(double x, double y, double z) {
        return x == 0.0D && y == 0.0D && z == 0.0D ? this : new BlockPosition((double) this.getX() + x, (double) this.getY() + y, (double) this.getZ() + z);
    }

    public boolean equalsBlockPos(final BlockPosition blockPosition) {
        return this.getX() == blockPosition.getX() && this.getY() == blockPosition.getY() && this.getZ() == blockPosition.getZ();
    }

    /**
     * Add the given coordinates to the coordinates of this BlockPos
     */
    public BlockPosition add(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPosition(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    /**
     * Add the given Vector to this BlockPos
     */
    public BlockPosition add(Vec3i vec) {
        return vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0 ? this : new BlockPosition(this.getX() + vec.getX(), this.getY() + vec.getY(), this.getZ() + vec.getZ());
    }

    /**
     * Subtract the given Vector from this BlockPos
     */
    public BlockPosition subtract(Vec3i vec) {
        return vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0 ? this : new BlockPosition(this.getX() - vec.getX(), this.getY() - vec.getY(), this.getZ() - vec.getZ());
    }

    /**
     * Offset this BlockPos 1 block up
     */
    public BlockPosition up() {
        return new BlockPosition(this.getX(), this.getY() + 1, this.getZ());
    }

    /**
     * Offset this BlockPos n blocks up
     */
    public BlockPosition up(int n) {
        return n == 0 ? this : new BlockPosition(this.getX(), this.getY() + n, this.getZ());
    }

    /**
     * Offset this BlockPos 1 block down
     */
    public BlockPosition down() {
        return new BlockPosition(this.getX(), this.getY() - 1, this.getZ());
    }

    /**
     * Offset this BlockPos n blocks down
     */
    public BlockPosition down(int n) {
        return n == 0 ? this : new BlockPosition(this.getX(), this.getY() - n, this.getZ());
    }

    /**
     * Offset this BlockPos 1 block in northern direction
     */
    public BlockPosition north() {
        return new BlockPosition(this.getX(), this.getY(), this.getZ() - 1);
    }

    /**
     * Offset this BlockPos n blocks in northern direction
     */
    public BlockPosition north(int n) {
        return n == 0 ? this : new BlockPosition(this.getX(), this.getY(), this.getZ() - n);
    }

    /**
     * Offset this BlockPos 1 block in southern direction
     */
    public BlockPosition south() {
        return new BlockPosition(this.getX(), this.getY(), this.getZ() + 1);
    }

    /**
     * Offset this BlockPos n blocks in southern direction
     */
    public BlockPosition south(int n) {
        return n == 0 ? (BlockPosition) (Object) this : new BlockPosition(this.getX(), this.getY(), this.getZ() + n);
    }

    /**
     * Offset this BlockPos 1 block in western direction
     */
    public BlockPosition west() {
        return new BlockPosition(this.getX() - 1, this.getY(), this.getZ());
    }

    /**
     * Offset this BlockPos n blocks in western direction
     */
    public BlockPosition west(int n) {
        return n == 0 ? (BlockPosition) (Object) this : new BlockPosition(this.getX() - n, this.getY(), this.getZ());
    }

    /**
     * Offset this BlockPos 1 block in eastern direction
     */
    public BlockPosition east() {
        return new BlockPosition(this.getX() + 1, this.getY(), this.getZ());
    }

    /**
     * Offset this BlockPos n blocks in eastern direction
     */
    public BlockPosition east(int n) {
        return n == 0 ? this : new BlockPosition(this.getX() + n, this.getY(), this.getZ());
    }

    /**
     * Offset this BlockPos 1 block in the given direction
     */
    public BlockPosition offset(EnumFacing facing) {
        return new BlockPosition(this.getX() + facing.getFrontOffsetX(), this.getY() + facing.getFrontOffsetY(), this.getZ() + facing.getFrontOffsetZ());
    }

    /**
     * Offsets this BlockPos n blocks in the given direction
     */
    public BlockPosition offset(EnumFacing facing, int n) {
        return n == 0 ? this : new BlockPosition(this.getX() + facing.getFrontOffsetX() * n, this.getY() + facing.getFrontOffsetY() * n, this.getZ() + facing.getFrontOffsetZ() * n);
    }

    /**
     * Calculate the cross product of this and the given Vector
     */
    public BlockPosition crossProduct(Vec3i vec) {
        return new BlockPosition(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    /**
     * Serialize this BlockPos into a long value
     */
    public long toLong() {
        return ((long) this.getX() & X_MASK) << X_SHIFT | ((long) this.getY() & Y_MASK) << Y_SHIFT | ((long) this.getZ() & Z_MASK);
    }

    public Block getBlock() {
        return Minecraft.getMinecraft().theWorld.getBlockState(this).getBlock();
    }

    /**
     * Create a BlockPos from a serialized long value (created by toLong)
     */
    public static BlockPosition fromLong(long serialized) {
        int i = (int) (serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        int j = (int) (serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        int k = (int) (serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
        return new BlockPosition(i, j, k);
    }

    public static Iterable<BlockPosition> getAllInBox(BlockPosition from, BlockPosition to) {
        final BlockPosition blockpos = new BlockPosition(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPosition blockpos1 = new BlockPosition(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<BlockPosition>() {
            public Iterator<BlockPosition> iterator() {
                return new AbstractIterator<BlockPosition>() {
                    private BlockPosition lastReturned = null;

                    protected BlockPosition computeNext() {
                        if (this.lastReturned == null) {
                            this.lastReturned = blockpos;
                            return this.lastReturned;
                        } else if (this.lastReturned.equals(blockpos1)) {
                            return this.endOfData();
                        } else {
                            int i = this.lastReturned.getX();
                            int j = this.lastReturned.getY();
                            int k = this.lastReturned.getZ();

                            if (i < blockpos1.getX()) {
                                ++i;
                            } else if (j < blockpos1.getY()) {
                                i = blockpos.getX();
                                ++j;
                            } else if (k < blockpos1.getZ()) {
                                i = blockpos.getX();
                                j = blockpos.getY();
                                ++k;
                            }

                            this.lastReturned = new BlockPosition(i, j, k);
                            return this.lastReturned;
                        }
                    }
                };
            }
        };
    }

    public static Iterable<MutableBlockPosition> getAllInBoxMutable(BlockPosition from, BlockPosition to) {
        final BlockPosition blockpos = new BlockPosition(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPosition blockpos1 = new BlockPosition(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<MutableBlockPosition>() {
            public Iterator<MutableBlockPosition> iterator() {
                return new AbstractIterator<MutableBlockPosition>() {
                    private MutableBlockPosition theBlockPos = null;

                    protected MutableBlockPosition computeNext() {
                        if (this.theBlockPos == null) {
                            this.theBlockPos = new MutableBlockPosition(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                            return this.theBlockPos;
                        } else if (this.theBlockPos.equals(blockpos1)) {
                            return this.endOfData();
                        } else {
                            int i = this.theBlockPos.getX();
                            int j = this.theBlockPos.getY();
                            int k = this.theBlockPos.getZ();

                            if (i < blockpos1.getX()) {
                                ++i;
                            } else if (j < blockpos1.getY()) {
                                i = blockpos.getX();
                                ++j;
                            } else if (k < blockpos1.getZ()) {
                                i = blockpos.getX();
                                j = blockpos.getY();
                                ++k;
                            }

                            this.theBlockPos.x = i;
                            this.theBlockPos.y = j;
                            this.theBlockPos.z = k;
                            return this.theBlockPos;
                        }
                    }
                };
            }
        };
    }

    public static final class MutableBlockPosition extends BlockPosition {
        private int x;
        private int y;
        private int z;

        public MutableBlockPosition() {
            this(0, 0, 0);
        }

        public MutableBlockPosition(int x_, int y_, int z_) {
            super(0, 0, 0);
            this.x = x_;
            this.y = y_;
            this.z = z_;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getZ() {
            return this.z;
        }

        public MutableBlockPosition set(int xIn, int yIn, int zIn) {
            this.x = xIn;
            this.y = yIn;
            this.z = zIn;
            return this;
        }
    }
}
