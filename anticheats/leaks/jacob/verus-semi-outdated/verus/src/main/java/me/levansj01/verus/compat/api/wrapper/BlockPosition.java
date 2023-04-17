package me.levansj01.verus.compat.api.wrapper;

import me.levansj01.verus.util.*;

public class BlockPosition implements IBlockPosition
{
    private int y;
    private int x;
    private int z;
    
    public int diff(final BlockPosition blockPosition) {
        return Math.abs(this.x - blockPosition.x) + Math.abs(this.z - blockPosition.z);
    }
    
    public MutableBlockLocation toMutableBlock() {
        return new MutableBlockLocation(this.x, this.y, this.z);
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public void setX(final int x) {
        this.x = x;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BlockPosition)) {
            return false;
        }
        final BlockPosition blockPosition = (BlockPosition)o;
        return blockPosition.canEqual(this) && this.getX() == blockPosition.getX() && this.getY() == blockPosition.getY() && this.getZ() == blockPosition.getZ();
    }
    
    public boolean nearby(final BlockPosition blockPosition) {
        return this.diff(blockPosition) == 1;
    }
    
    public BlockPosition copy() {
        return new BlockPosition(this.x, this.y, this.z);
    }
    
    @Override
    public int getY() {
        return this.y;
    }
    
    @Override
    public String toString() {
        return "BlockPosition(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ")";
    }
    
    public BlockPosition(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void face(final int n) {
        switch (n) {
            case 0: {
                --this.y;
                break;
            }
            case 1: {
                ++this.y;
                break;
            }
            case 2: {
                --this.z;
                break;
            }
            case 3: {
                ++this.z;
                break;
            }
            case 4: {
                --this.x;
                break;
            }
            case 5: {
                ++this.x;
                break;
            }
        }
    }
    
    @Override
    public int getX() {
        return this.x;
    }
    
    public void setZ(final int z) {
        this.z = z;
    }
    
    @Override
    public int hashCode() {
        final int n = 59 + this.getX();
        final int n2 = 59 + this.getY();
        final int n3 = 59 + this.getZ();
        return 1;
    }
    
    @Override
    public int getZ() {
        return this.z;
    }
    
    public BlockPosition move(final Direction direction, final int n) {
        switch (BlockPosition.BlockPosition.SwitchMap.me.levansj01.verus.compat.api.wrapper.Direction[direction.ordinal()]) {
            case 1: {
                this.y += n;
                break;
            }
            case 2: {
                this.y -= n;
                break;
            }
            case 3: {
                this.z += n;
                break;
            }
            case 4: {
                this.z -= n;
                break;
            }
            case 5: {
                this.x += n;
                break;
            }
            case 6: {
                this.x -= n;
                break;
            }
        }
        return this;
    }
    
    protected boolean canEqual(final Object o) {
        return o instanceof BlockPosition;
    }
}
