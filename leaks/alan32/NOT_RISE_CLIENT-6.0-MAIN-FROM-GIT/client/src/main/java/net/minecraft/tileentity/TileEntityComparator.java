package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityComparator extends TileEntity {
    private int outputSignal;

    public void writeToNBT(final NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("OutputSignal", this.outputSignal);
    }

    public void readFromNBT(final NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.outputSignal = compound.getInteger("OutputSignal");
    }

    public int getOutputSignal() {
        return this.outputSignal;
    }

    public void setOutputSignal(final int p_145995_1_) {
        this.outputSignal = p_145995_1_;
    }
}
