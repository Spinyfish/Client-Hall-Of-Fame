package net.minecraft.tileentity;

import net.minecraft.world.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;

public abstract class TileEntityLockable extends TileEntity implements IInteractionObject, ILockableContainer
{
    private LockCode code;
    
    public TileEntityLockable() {
        this.code = LockCode.EMPTY_CODE;
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.code = LockCode.fromNBT(compound);
    }
    
    @Override
    public void writeToNBT(final NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (this.code != null) {
            this.code.toNBT(compound);
        }
    }
    
    @Override
    public boolean isLocked() {
        return this.code != null && !this.code.isEmpty();
    }
    
    @Override
    public LockCode getLockCode() {
        return this.code;
    }
    
    @Override
    public void setLockCode(final LockCode code) {
        this.code = code;
    }
    
    @Override
    public IChatComponent getDisplayName() {
        return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName(), new Object[0]);
    }
}
