package net.minecraft.block.state.pattern;

import com.google.common.base.*;
import net.minecraft.block.state.*;
import net.minecraft.block.properties.*;
import com.google.common.collect.*;
import net.minecraft.block.*;
import java.util.*;

public class BlockStateHelper implements Predicate<IBlockState>
{
    private final BlockState blockstate;
    private final Map<IProperty, Predicate> propertyPredicates;
    
    private BlockStateHelper(final BlockState blockStateIn) {
        this.propertyPredicates = (Map<IProperty, Predicate>)Maps.newHashMap();
        this.blockstate = blockStateIn;
    }
    
    public static BlockStateHelper forBlock(final Block blockIn) {
        return new BlockStateHelper(blockIn.getBlockState());
    }
    
    public boolean apply(final IBlockState p_apply_1_) {
        if (p_apply_1_ != null && p_apply_1_.getBlock().equals(this.blockstate.getBlock())) {
            for (final Map.Entry<IProperty, Predicate> entry : this.propertyPredicates.entrySet()) {
                final Object object = p_apply_1_.getValue(entry.getKey());
                if (!entry.getValue().apply(object)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public <V extends Comparable<V>> BlockStateHelper where(final IProperty<V> property, final Predicate<? extends V> is) {
        if (!this.blockstate.getProperties().contains(property)) {
            throw new IllegalArgumentException(this.blockstate + " cannot support property " + property);
        }
        this.propertyPredicates.put(property, is);
        return this;
    }
}
