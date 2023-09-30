package net.optifine;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;

import java.util.Iterator;

public class BlockPositionM extends BlockPosition
{
    private int mx;
    private int my;
    private int mz;
    private int level;
    private BlockPositionM[] facings;
    private boolean needsUpdate;

    public BlockPositionM(int x, int y, int z)
    {
        this(x, y, z, 0);
    }

    public BlockPositionM(double xIn, double yIn, double zIn)
    {
        this(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
    }

    public BlockPositionM(int x, int y, int z, int level)
    {
        super(0, 0, 0);
        this.mx = x;
        this.my = y;
        this.mz = z;
        this.level = level;
    }

    /**
     * Get the X coordinate
     */
    public int getX()
    {
        return this.mx;
    }

    /**
     * Get the Y coordinate
     */
    public int getY()
    {
        return this.my;
    }

    /**
     * Get the Z coordinate
     */
    public int getZ()
    {
        return this.mz;
    }

    public void setXyz(int x, int y, int z)
    {
        this.mx = x;
        this.my = y;
        this.mz = z;
        this.needsUpdate = true;
    }

    public void setXyz(double xIn, double yIn, double zIn)
    {
        this.setXyz(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
    }

    public BlockPositionM set(Vec3i vec)
    {
        this.setXyz(vec.getX(), vec.getY(), vec.getZ());
        return this;
    }

    public BlockPositionM set(int xIn, int yIn, int zIn)
    {
        this.setXyz(xIn, yIn, zIn);
        return this;
    }

    public BlockPosition offsetMutable(EnumFacing facing)
    {
        return this.offset(facing);
    }

    /**
     * Offset this BlockPos 1 block in the given direction
     */
    public BlockPosition offset(EnumFacing facing)
    {
        if (this.level <= 0)
        {
            return super.offset(facing, 1);
        }
        else
        {
            if (this.facings == null)
            {
                this.facings = new BlockPositionM[EnumFacing.VALUES.length];
            }

            if (this.needsUpdate)
            {
                this.update();
            }

            int i = facing.getIndex();
            BlockPositionM blockposm = this.facings[i];

            if (blockposm == null)
            {
                int j = this.mx + facing.getFrontOffsetX();
                int k = this.my + facing.getFrontOffsetY();
                int l = this.mz + facing.getFrontOffsetZ();
                blockposm = new BlockPositionM(j, k, l, this.level - 1);
                this.facings[i] = blockposm;
            }

            return blockposm;
        }
    }

    /**
     * Offsets this BlockPos n blocks in the given direction
     */
    public BlockPosition offset(EnumFacing facing, int n)
    {
        return n == 1 ? this.offset(facing) : super.offset(facing, n);
    }

    private void update()
    {
        for (int i = 0; i < 6; ++i)
        {
            BlockPositionM blockposm = this.facings[i];

            if (blockposm != null)
            {
                EnumFacing enumfacing = EnumFacing.VALUES[i];
                int j = this.mx + enumfacing.getFrontOffsetX();
                int k = this.my + enumfacing.getFrontOffsetY();
                int l = this.mz + enumfacing.getFrontOffsetZ();
                blockposm.setXyz(j, k, l);
            }
        }

        this.needsUpdate = false;
    }

    public BlockPosition toImmutable()
    {
        return new BlockPosition(this.mx, this.my, this.mz);
    }

    public static Iterable getAllInBoxMutable(BlockPosition from, BlockPosition to)
    {
        final BlockPosition blockpos = new BlockPosition(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPosition blockpos1 = new BlockPosition(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable()
        {
            public Iterator iterator()
            {
                return new AbstractIterator()
                {
                    private BlockPositionM theBlockPosM = null;
                    protected BlockPositionM computeNext0()
                    {
                        if (this.theBlockPosM == null)
                        {
                            this.theBlockPosM = new BlockPositionM(blockpos.getX(), blockpos.getY(), blockpos.getZ(), 3);
                            return this.theBlockPosM;
                        }
                        else if (this.theBlockPosM.equals(blockpos1))
                        {
                            return (BlockPositionM)this.endOfData();
                        }
                        else
                        {
                            int i = this.theBlockPosM.getX();
                            int j = this.theBlockPosM.getY();
                            int k = this.theBlockPosM.getZ();

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

                            this.theBlockPosM.setXyz(i, j, k);
                            return this.theBlockPosM;
                        }
                    }
                    protected Object computeNext()
                    {
                        return this.computeNext0();
                    }
                };
            }
        };
    }
}
