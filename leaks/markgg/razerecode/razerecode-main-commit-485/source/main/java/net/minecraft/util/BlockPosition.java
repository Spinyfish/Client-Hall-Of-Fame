package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import net.minecraft.entity.Entity;

public class BlockPosition extends Vec3i
{
    public static final BlockPosition ORIGIN = new BlockPosition(0, 0, 0);
    private static final int NUM_X_BITS = 1 + MathHelper.calculateLogBaseTwo(MathHelper.roundUpToPowerOfTwo(30000000));
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
    private static final int Y_SHIFT = 0 + NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    public BlockPosition(int x, int y, int z)
    {
        super(x, y, z);
    }

    public BlockPosition(double x, double y, double z)
    {
        super(x, y, z);
    }

    public BlockPosition(Entity source)
    {
        this(source.posX, source.posY, source.posZ);
    }

    public BlockPosition(Vec3 source)
    {
        this(source.xCoord, source.yCoord, source.zCoord);
    }

    public BlockPosition(Vec3i source)
    {
        this(source.getX(), source.getY(), source.getZ());
    }

    public BlockPosition add(double x, double y, double z)
    {
        return x == 0.0D && y == 0.0D && z == 0.0D ? this : new BlockPosition((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
    }

    public BlockPosition add(int x, int y, int z)
    {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPosition(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public BlockPosition add(Vec3i vec)
    {
        return vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0 ? this : new BlockPosition(this.getX() + vec.getX(), this.getY() + vec.getY(), this.getZ() + vec.getZ());
    }

    public BlockPosition subtract(Vec3i vec)
    {
        return vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0 ? this : new BlockPosition(this.getX() - vec.getX(), this.getY() - vec.getY(), this.getZ() - vec.getZ());
    }

    public BlockPosition up()
    {
        return this.up(1);
    }

    public BlockPosition up(int n)
    {
        return this.offset(EnumFacing.UP, n);
    }

    public BlockPosition down()
    {
        return this.down(1);
    }

    public BlockPosition down(int n)
    {
        return this.offset(EnumFacing.DOWN, n);
    }

    public BlockPosition north()
    {
        return this.north(1);
    }

    public BlockPosition north(int n)
    {
        return this.offset(EnumFacing.NORTH, n);
    }

    public BlockPosition south()
    {
        return this.south(1);
    }

    public BlockPosition south(int n)
    {
        return this.offset(EnumFacing.SOUTH, n);
    }

    public BlockPosition west()
    {
        return this.west(1);
    }

    public BlockPosition west(int n)
    {
        return this.offset(EnumFacing.WEST, n);
    }

    public BlockPosition east()
    {
        return this.east(1);
    }

    public BlockPosition east(int n)
    {
        return this.offset(EnumFacing.EAST, n);
    }

    public BlockPosition offset(EnumFacing facing)
    {
        return this.offset(facing, 1);
    }

    public BlockPosition offset(EnumFacing facing, int n)
    {
        return n == 0 ? this : new BlockPosition(this.getX() + facing.getFrontOffsetX() * n, this.getY() + facing.getFrontOffsetY() * n, this.getZ() + facing.getFrontOffsetZ() * n);
    }

    public BlockPosition crossProduct(Vec3i vec)
    {
        return new BlockPosition(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    public long toLong()
    {
        return ((long)this.getX() & X_MASK) << X_SHIFT | ((long)this.getY() & Y_MASK) << Y_SHIFT | ((long)this.getZ() & Z_MASK) << 0;
    }

    public static BlockPosition fromLong(long serialized)
    {
        int i = (int)(serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        int j = (int)(serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        int k = (int)(serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
        return new BlockPosition(i, j, k);
    }

    public static Iterable<BlockPosition> getAllInBox(BlockPosition from, BlockPosition to)
    {
        final BlockPosition blockpos = new BlockPosition(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPosition blockpos1 = new BlockPosition(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<BlockPosition>()
        {
            public Iterator<BlockPosition> iterator()
            {
                return new AbstractIterator<BlockPosition>()
                {
                    private BlockPosition lastReturned = null;
                    protected BlockPosition computeNext()
                    {
                        if (this.lastReturned == null)
                        {
                            this.lastReturned = blockpos;
                            return this.lastReturned;
                        }
                        else if (this.lastReturned.equals(blockpos1))
                        {
                            return (BlockPosition)this.endOfData();
                        }
                        else
                        {
                            int i = this.lastReturned.getX();
                            int j = this.lastReturned.getY();
                            int k = this.lastReturned.getZ();

                            if (i < blockpos1.getX())
                            {
                                ++i;
                            }
                            else if (j < blockpos1.getY())
                            {
                                i = blockpos.getX();
                                ++j;
                            }
                            else if (k < blockpos1.getZ())
                            {
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

    public static Iterable<MutableBlockPosition> getAllInBoxMutable(BlockPosition from, BlockPosition to)
    {
        final BlockPosition blockpos = new BlockPosition(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPosition blockpos1 = new BlockPosition(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<MutableBlockPosition>()
        {
            public Iterator<MutableBlockPosition> iterator()
            {
                return new AbstractIterator<MutableBlockPosition>()
                {
                    private MutableBlockPosition theBlockPos = null;
                    protected MutableBlockPosition computeNext()
                    {
                        if (this.theBlockPos == null)
                        {
                            this.theBlockPos = new MutableBlockPosition(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                            return this.theBlockPos;
                        }
                        else if (this.theBlockPos.equals(blockpos1))
                        {
                            return (MutableBlockPosition)this.endOfData();
                        }
                        else
                        {
                            int i = this.theBlockPos.getX();
                            int j = this.theBlockPos.getY();
                            int k = this.theBlockPos.getZ();

                            if (i < blockpos1.getX())
                            {
                                ++i;
                            }
                            else if (j < blockpos1.getY())
                            {
                                i = blockpos.getX();
                                ++j;
                            }
                            else if (k < blockpos1.getZ())
                            {
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

    public static final class MutableBlockPosition extends BlockPosition
    {
        private int x;
        private int y;
        private int z;

        public MutableBlockPosition()
        {
            this(0, 0, 0);
        }

        public MutableBlockPosition(int x_, int y_, int z_)
        {
            super(0, 0, 0);
            this.x = x_;
            this.y = y_;
            this.z = z_;
        }

        public int getX()
        {
            return this.x;
        }

        public int getY()
        {
            return this.y;
        }

        public int getZ()
        {
            return this.z;
        }

        public MutableBlockPosition set(int xIn, int yIn, int zIn)
        {
            this.x = xIn;
            this.y = yIn;
            this.z = zIn;
            return this;
        }
    }
}
