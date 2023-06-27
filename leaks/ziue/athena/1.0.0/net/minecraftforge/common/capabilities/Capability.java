package net.minecraftforge.common.capabilities;

import java.util.concurrent.*;
import com.google.common.base.*;
import net.minecraft.util.*;
import net.minecraft.nbt.*;

public class Capability<T>
{
    private final String name;
    private final IStorage<T> storage;
    private final Callable<? extends T> factory;
    
    public String getName() {
        return this.name;
    }
    
    public IStorage<T> getStorage() {
        return this.storage;
    }
    
    public T getDefaultInstance() {
        try {
            return (T)this.factory.call();
        }
        catch (Exception e) {
            Throwables.propagate((Throwable)e);
            return null;
        }
    }
    
    public Capability(final String name, final IStorage<T> iStorage, final Callable<? extends T> factory) {
        this.name = name;
        this.storage = iStorage;
        this.factory = factory;
    }
    
    public interface IStorage<T>
    {
        NBTBase writeNBT(final Capability<T> p0, final T p1, final EnumFacing p2);
        
        void readNBT(final Capability<T> p0, final T p1, final EnumFacing p2, final NBTBase p3);
    }
}
