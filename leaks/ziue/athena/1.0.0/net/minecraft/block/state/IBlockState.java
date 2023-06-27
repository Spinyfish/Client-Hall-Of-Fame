package net.minecraft.block.state;

import java.util.*;
import net.minecraft.block.properties.*;
import com.google.common.collect.*;
import net.minecraft.block.*;

public interface IBlockState
{
    Collection<IProperty> getPropertyNames();
    
     <T extends Comparable<T>> T getValue(final IProperty<T> p0);
    
     <T extends Comparable<T>, V extends T> IBlockState withProperty(final IProperty<T> p0, final V p1);
    
     <T extends Comparable<T>> IBlockState cycleProperty(final IProperty<T> p0);
    
    ImmutableMap<IProperty, Comparable> getProperties();
    
    Block getBlock();
}
