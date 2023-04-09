package tech.dort.dortware.impl.events;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import tech.dort.dortware.api.event.Event;

public class BlockCollisionEvent extends Event {

    private AxisAlignedBB axisAlignedBB;
    private final Block block;
    private int x, y, z;

    public BlockCollisionEvent(AxisAlignedBB axisAlignedBB, Block block, int x, int y, int z) {
        this.axisAlignedBB = axisAlignedBB;
        this.block = block;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public AxisAlignedBB getAxisAlignedBB() {
        return axisAlignedBB;
    }

    public void setAxisAlignedBB(AxisAlignedBB axisAlignedBB) {
        this.axisAlignedBB = axisAlignedBB;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Block getBlock() {
        return block;
    }
}
