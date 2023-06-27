package net.minecraft.block.state;

import com.google.common.base.*;
import net.minecraft.util.*;
import net.minecraft.block.*;
import net.minecraft.block.properties.*;
import com.google.common.collect.*;
import java.util.*;

public abstract class BlockStateBase implements IBlockState
{
    private static final Joiner COMMA_JOINER;
    private static final Function MAP_ENTRY_TO_STRING;
    private static final String __OBFID = "CL_00002032";
    private int blockId;
    private int blockStateId;
    private int metadata;
    private ResourceLocation blockLocation;
    
    public BlockStateBase() {
        this.blockId = -1;
        this.blockStateId = -1;
        this.metadata = -1;
        this.blockLocation = null;
    }
    
    public int getBlockId() {
        if (this.blockId < 0) {
            this.blockId = Block.getIdFromBlock(this.getBlock());
        }
        return this.blockId;
    }
    
    public int getBlockStateId() {
        if (this.blockStateId < 0) {
            this.blockStateId = Block.getStateId(this);
        }
        return this.blockStateId;
    }
    
    public int getMetadata() {
        if (this.metadata < 0) {
            this.metadata = this.getBlock().getMetaFromState(this);
        }
        return this.metadata;
    }
    
    public ResourceLocation getBlockLocation() {
        if (this.blockLocation == null) {
            this.blockLocation = Block.blockRegistry.getNameForObject(this.getBlock());
        }
        return this.blockLocation;
    }
    
    @Override
    public IBlockState cycleProperty(final IProperty property) {
        return this.withProperty((IProperty<Comparable>)property, cyclePropertyValue(property.getAllowedValues(), this.getValue((IProperty<T>)property)));
    }
    
    protected static Object cyclePropertyValue(final Collection values, final Object currentValue) {
        final Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(currentValue)) {
                if (iterator.hasNext()) {
                    return iterator.next();
                }
                return values.iterator().next();
            }
        }
        return iterator.next();
    }
    
    @Override
    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(Block.blockRegistry.getNameForObject(this.getBlock()));
        if (!this.getProperties().isEmpty()) {
            stringbuilder.append("[");
            BlockStateBase.COMMA_JOINER.appendTo(stringbuilder, Iterables.transform((Iterable)this.getProperties().entrySet(), BlockStateBase.MAP_ENTRY_TO_STRING));
            stringbuilder.append("]");
        }
        return stringbuilder.toString();
    }
    
    public ImmutableTable<IProperty, Comparable, IBlockState> getPropertyValueTable() {
        return null;
    }
    
    static {
        COMMA_JOINER = Joiner.on(',');
        MAP_ENTRY_TO_STRING = (Function)new Function() {
            private static final String __OBFID = "CL_00002031";
            
            public String apply(final Map.Entry p_apply_1_) {
                if (p_apply_1_ == null) {
                    return "<NULL>";
                }
                final IProperty iproperty = p_apply_1_.getKey();
                return iproperty.getName() + "=" + iproperty.getName((Comparable)p_apply_1_.getValue());
            }
            
            public Object apply(final Object p_apply_1_) {
                return this.apply((Map.Entry)p_apply_1_);
            }
        };
    }
}
