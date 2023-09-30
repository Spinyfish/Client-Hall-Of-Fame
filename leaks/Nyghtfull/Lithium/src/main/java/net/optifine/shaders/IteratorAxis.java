package net.optifine.shaders;

import net.minecraft.util.BlockPosition;
import net.optifine.BlockPositionM;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteratorAxis implements Iterator<BlockPosition>
{
    private double yDelta;
    private double zDelta;
    private int xStart;
    private int xEnd;
    private double yStart;
    private double yEnd;
    private double zStart;
    private double zEnd;
    private int xNext;
    private double yNext;
    private double zNext;
    private BlockPositionM pos = new BlockPositionM(0, 0, 0);
    private boolean hasNext = false;

    public IteratorAxis(BlockPosition posStart, BlockPosition posEnd, double yDelta, double zDelta)
    {
        this.yDelta = yDelta;
        this.zDelta = zDelta;
        this.xStart = posStart.getX();
        this.xEnd = posEnd.getX();
        this.yStart = (double)posStart.getY();
        this.yEnd = (double)posEnd.getY() - 0.5D;
        this.zStart = (double)posStart.getZ();
        this.zEnd = (double)posEnd.getZ() - 0.5D;
        this.xNext = this.xStart;
        this.yNext = this.yStart;
        this.zNext = this.zStart;
        this.hasNext = this.xNext < this.xEnd && this.yNext < this.yEnd && this.zNext < this.zEnd;
    }

    public boolean hasNext()
    {
        return this.hasNext;
    }

    public BlockPosition next()
    {
        if (!this.hasNext)
        {
            throw new NoSuchElementException();
        }
        else
        {
            this.pos.setXyz((double)this.xNext, this.yNext, this.zNext);
            this.nextPos();
            this.hasNext = this.xNext < this.xEnd && this.yNext < this.yEnd && this.zNext < this.zEnd;
            return this.pos;
        }
    }

    private void nextPos()
    {
        ++this.zNext;

        if (this.zNext >= this.zEnd)
        {
            this.zNext = this.zStart;
            ++this.yNext;

            if (this.yNext >= this.yEnd)
            {
                this.yNext = this.yStart;
                this.yStart += this.yDelta;
                this.yEnd += this.yDelta;
                this.yNext = this.yStart;
                this.zStart += this.zDelta;
                this.zEnd += this.zDelta;
                this.zNext = this.zStart;
                ++this.xNext;

                if (this.xNext >= this.xEnd)
                {
                    ;
                }
            }
        }
    }

    public void remove()
    {
        throw new RuntimeException("Not implemented");
    }

    public static void main(String[] args) throws Exception
    {
        BlockPosition blockpos = new BlockPosition(-2, 10, 20);
        BlockPosition blockpos1 = new BlockPosition(2, 12, 22);
        double d0 = -0.5D;
        double d1 = 0.5D;
        IteratorAxis iteratoraxis = new IteratorAxis(blockpos, blockpos1, d0, d1);
        System.out.println("Start: " + blockpos + ", end: " + blockpos1 + ", yDelta: " + d0 + ", zDelta: " + d1);

        while (iteratoraxis.hasNext())
        {
            BlockPosition blockpos2 = iteratoraxis.next();
            System.out.println("" + blockpos2);
        }
    }
}
