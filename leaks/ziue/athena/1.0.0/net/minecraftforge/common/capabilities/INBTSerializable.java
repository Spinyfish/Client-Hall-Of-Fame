package net.minecraftforge.common.capabilities;

import net.minecraft.nbt.*;

public interface INBTSerializable<T extends NBTBase>
{
    T serializeNBT();
    
    void deserializeNBT(final T p0);
}
