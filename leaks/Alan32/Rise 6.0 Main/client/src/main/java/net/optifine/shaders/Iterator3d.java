package net.optifine.shaders;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.optifine.BlockPosM;

import java.util.Iterator;

public class Iterator3d implements Iterator<BlockPos> {
    private final IteratorAxis iteratorAxis;
    private final BlockPosM blockPos = new BlockPosM(0, 0, 0);
    private int axis = 0;
    private final int kX;
    private final int kY;
    private final int kZ;
    private static final int AXIS_X = 0;
    private static final int AXIS_Y = 1;
    private static final int AXIS_Z = 2;

    public Iterator3d(BlockPos posStart, BlockPos posEnd, final int width, final int height) {
        final boolean flag = posStart.getX() > posEnd.getX();
        final boolean flag1 = posStart.getY() > posEnd.getY();
        final boolean flag2 = posStart.getZ() > posEnd.getZ();
        posStart = this.reverseCoord(posStart, flag, flag1, flag2);
        posEnd = this.reverseCoord(posEnd, flag, flag1, flag2);
        this.kX = flag ? -1 : 1;
        this.kY = flag1 ? -1 : 1;
        this.kZ = flag2 ? -1 : 1;
        final Vec3 vec3 = new Vec3(posEnd.getX() - posStart.getX(), posEnd.getY() - posStart.getY(), posEnd.getZ() - posStart.getZ());
        final Vec3 vec31 = vec3.normalize();
        final Vec3 vec32 = new Vec3(1.0D, 0.0D, 0.0D);
        final double d0 = vec31.dotProduct(vec32);
        final double d1 = Math.abs(d0);
        final Vec3 vec33 = new Vec3(0.0D, 1.0D, 0.0D);
        final double d2 = vec31.dotProduct(vec33);
        final double d3 = Math.abs(d2);
        final Vec3 vec34 = new Vec3(0.0D, 0.0D, 1.0D);
        final double d4 = vec31.dotProduct(vec34);
        final double d5 = Math.abs(d4);

        if (d5 >= d3 && d5 >= d1) {
            this.axis = 2;
            final BlockPos blockpos3 = new BlockPos(posStart.getZ(), posStart.getY() - width, posStart.getX() - height);
            final BlockPos blockpos5 = new BlockPos(posEnd.getZ(), posStart.getY() + width + 1, posStart.getX() + height + 1);
            final int k = posEnd.getZ() - posStart.getZ();
            final double d9 = (double) (posEnd.getY() - posStart.getY()) / (1.0D * (double) k);
            final double d11 = (double) (posEnd.getX() - posStart.getX()) / (1.0D * (double) k);
            this.iteratorAxis = new IteratorAxis(blockpos3, blockpos5, d9, d11);
        } else if (d3 >= d1 && d3 >= d5) {
            this.axis = 1;
            final BlockPos blockpos2 = new BlockPos(posStart.getY(), posStart.getX() - width, posStart.getZ() - height);
            final BlockPos blockpos4 = new BlockPos(posEnd.getY(), posStart.getX() + width + 1, posStart.getZ() + height + 1);
            final int j = posEnd.getY() - posStart.getY();
            final double d8 = (double) (posEnd.getX() - posStart.getX()) / (1.0D * (double) j);
            final double d10 = (double) (posEnd.getZ() - posStart.getZ()) / (1.0D * (double) j);
            this.iteratorAxis = new IteratorAxis(blockpos2, blockpos4, d8, d10);
        } else {
            this.axis = 0;
            final BlockPos blockpos = new BlockPos(posStart.getX(), posStart.getY() - width, posStart.getZ() - height);
            final BlockPos blockpos1 = new BlockPos(posEnd.getX(), posStart.getY() + width + 1, posStart.getZ() + height + 1);
            final int i = posEnd.getX() - posStart.getX();
            final double d6 = (double) (posEnd.getY() - posStart.getY()) / (1.0D * (double) i);
            final double d7 = (double) (posEnd.getZ() - posStart.getZ()) / (1.0D * (double) i);
            this.iteratorAxis = new IteratorAxis(blockpos, blockpos1, d6, d7);
        }
    }

    private BlockPos reverseCoord(BlockPos pos, final boolean revX, final boolean revY, final boolean revZ) {
        if (revX) {
            pos = new BlockPos(-pos.getX(), pos.getY(), pos.getZ());
        }

        if (revY) {
            pos = new BlockPos(pos.getX(), -pos.getY(), pos.getZ());
        }

        if (revZ) {
            pos = new BlockPos(pos.getX(), pos.getY(), -pos.getZ());
        }

        return pos;
    }

    public boolean hasNext() {
        return this.iteratorAxis.hasNext();
    }

    public BlockPos next() {
        final BlockPos blockpos = this.iteratorAxis.next();

        switch (this.axis) {
            case 0:
                this.blockPos.setXyz(blockpos.getX() * this.kX, blockpos.getY() * this.kY, blockpos.getZ() * this.kZ);
                return this.blockPos;

            case 1:
                this.blockPos.setXyz(blockpos.getY() * this.kX, blockpos.getX() * this.kY, blockpos.getZ() * this.kZ);
                return this.blockPos;

            case 2:
                this.blockPos.setXyz(blockpos.getZ() * this.kX, blockpos.getY() * this.kY, blockpos.getX() * this.kZ);
                return this.blockPos;

            default:
                this.blockPos.setXyz(blockpos.getX() * this.kX, blockpos.getY() * this.kY, blockpos.getZ() * this.kZ);
                return this.blockPos;
        }
    }

    public void remove() {
        throw new RuntimeException("Not supported");
    }

    public static void main(final String[] args) {
        final BlockPos blockpos = new BlockPos(10, 20, 30);
        final BlockPos blockpos1 = new BlockPos(30, 40, 20);
        final Iterator3d iterator3d = new Iterator3d(blockpos, blockpos1, 1, 1);

        while (iterator3d.hasNext()) {
            final BlockPos blockpos2 = iterator3d.next();
            System.out.println("" + blockpos2);
        }
    }
}
