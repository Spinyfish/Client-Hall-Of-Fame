package net.minecraft.util;

import net.minecraft.entity.Entity;

public class MovingObjectPosition
{
    private BlockPosition blockPosition;

    /** What type of ray trace hit was this? 0 = block, 1 = entity */
    public MovingObjectPosition.MovingObjectType typeOfHit;
    public EnumFacing sideHit;

    /** The vector position of the hit */
    public Vec3 hitVec;

    /** The hit entity */
    public Entity entityHit;

    public MovingObjectPosition(Vec3 hitVecIn, EnumFacing facing, BlockPosition blockPositionIn)
    {
        this(MovingObjectPosition.MovingObjectType.BLOCK, hitVecIn, facing, blockPositionIn);
    }

    public MovingObjectPosition(Vec3 p_i45552_1_, EnumFacing facing)
    {
        this(MovingObjectPosition.MovingObjectType.BLOCK, p_i45552_1_, facing, BlockPosition.ORIGIN);
    }

    public MovingObjectPosition(Entity entityIn)
    {
        this(entityIn, new Vec3(entityIn.posX, entityIn.posY, entityIn.posZ));
    }

    public MovingObjectPosition(MovingObjectPosition.MovingObjectType typeOfHitIn, Vec3 hitVecIn, EnumFacing sideHitIn, BlockPosition blockPositionIn)
    {
        this.typeOfHit = typeOfHitIn;
        this.blockPosition = blockPositionIn;
        this.sideHit = sideHitIn;
        this.hitVec = new Vec3(hitVecIn.xCoord, hitVecIn.yCoord, hitVecIn.zCoord);
    }

    public MovingObjectPosition(Entity entityHitIn, Vec3 hitVecIn)
    {
        this.typeOfHit = MovingObjectPosition.MovingObjectType.ENTITY;
        this.entityHit = entityHitIn;
        this.hitVec = hitVecIn;
    }

    public BlockPosition getBlockPos()
    {
        return this.blockPosition;
    }

    public String toString()
    {
        return "HitResult{type=" + this.typeOfHit + ", blockpos=" + this.blockPosition + ", f=" + this.sideHit + ", pos=" + this.hitVec + ", entity=" + this.entityHit + '}';
    }

    public static enum MovingObjectType
    {
        MISS,
        BLOCK,
        ENTITY;
    }
}
