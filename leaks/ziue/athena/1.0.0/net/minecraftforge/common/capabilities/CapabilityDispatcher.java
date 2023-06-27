package net.minecraftforge.common.capabilities;

import net.minecraft.nbt.*;
import com.google.common.collect.*;
import java.util.*;
import net.minecraft.util.*;

public final class CapabilityDispatcher implements INBTSerializable<NBTTagCompound>, ICapabilityProvider
{
    private ICapabilityProvider[] caps;
    private INBTSerializable<NBTBase>[] writers;
    private String[] names;
    
    public CapabilityDispatcher(final Map<ResourceLocation, ICapabilityProvider> list) {
        this(list, null);
    }
    
    public CapabilityDispatcher(final Map<ResourceLocation, ICapabilityProvider> list, final ICapabilityProvider parent) {
        final List<ICapabilityProvider> lstCaps = (List<ICapabilityProvider>)Lists.newArrayList();
        final List<INBTSerializable<NBTBase>> lstWriters = (List<INBTSerializable<NBTBase>>)Lists.newArrayList();
        final List<String> lstNames = (List<String>)Lists.newArrayList();
        if (parent != null) {
            lstCaps.add(parent);
            if (parent instanceof INBTSerializable) {
                lstWriters.add((INBTSerializable<NBTBase>)parent);
                lstNames.add("Parent");
            }
        }
        for (final Map.Entry<ResourceLocation, ICapabilityProvider> entry : list.entrySet()) {
            final ICapabilityProvider prov = entry.getValue();
            lstCaps.add(prov);
            if (prov instanceof INBTSerializable) {
                lstWriters.add((INBTSerializable<NBTBase>)prov);
                lstNames.add(entry.getKey().toString());
            }
        }
        this.caps = lstCaps.toArray(new ICapabilityProvider[lstCaps.size()]);
        this.writers = lstWriters.toArray(new INBTSerializable[lstWriters.size()]);
        this.names = lstNames.toArray(new String[lstNames.size()]);
    }
    
    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
        for (final ICapabilityProvider cap : this.caps) {
            if (cap.hasCapability(capability, facing)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
        for (final ICapabilityProvider cap : this.caps) {
            final T ret = cap.getCapability(capability, facing);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }
    
    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound nbt = new NBTTagCompound();
        for (int x = 0; x < this.writers.length; ++x) {
            nbt.setTag(this.names[x], this.writers[x].serializeNBT());
        }
        return nbt;
    }
    
    @Override
    public void deserializeNBT(final NBTTagCompound nbt) {
        for (int x = 0; x < this.writers.length; ++x) {
            if (nbt.hasKey(this.names[x])) {
                this.writers[x].deserializeNBT(nbt.getTag(this.names[x]));
            }
        }
    }
}
