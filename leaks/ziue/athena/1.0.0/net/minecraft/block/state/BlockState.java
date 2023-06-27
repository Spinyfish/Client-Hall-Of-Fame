package net.minecraft.block.state;

import net.minecraft.block.properties.*;
import net.minecraft.block.*;
import net.minecraft.util.*;
import com.google.common.base.*;
import java.util.*;
import com.google.common.collect.*;

public class BlockState
{
    private static final Joiner COMMA_JOINER;
    private static final Function<IProperty, String> GET_NAME_FUNC;
    private final Block block;
    private final ImmutableList<IProperty> properties;
    private final ImmutableList<IBlockState> validStates;
    
    public BlockState(final Block blockIn, final IProperty... properties) {
        this.block = blockIn;
        Arrays.sort(properties, new Comparator<IProperty>() {
            @Override
            public int compare(final IProperty p_compare_1_, final IProperty p_compare_2_) {
                return p_compare_1_.getName().compareTo(p_compare_2_.getName());
            }
        });
        this.properties = (ImmutableList<IProperty>)ImmutableList.copyOf((Object[])properties);
        final Map<Map<IProperty, Comparable>, StateImplementation> map = (Map<Map<IProperty, Comparable>, StateImplementation>)Maps.newLinkedHashMap();
        final List<StateImplementation> list = (List<StateImplementation>)Lists.newArrayList();
        for (final List<Comparable> list2 : Cartesian.cartesianProduct((Iterable<? extends Iterable<?>>)this.getAllowedValues())) {
            final Map<IProperty, Comparable> map2 = (Map<IProperty, Comparable>)MapPopulator.createMap((Iterable<IProperty>)this.properties, list2);
            final StateImplementation blockstate$stateimplementation = new StateImplementation(blockIn, ImmutableMap.copyOf((Map)map2));
            map.put(map2, blockstate$stateimplementation);
            list.add(blockstate$stateimplementation);
        }
        for (final StateImplementation blockstate$stateimplementation2 : list) {
            blockstate$stateimplementation2.buildPropertyValueTable(map);
        }
        this.validStates = (ImmutableList<IBlockState>)ImmutableList.copyOf((Collection)list);
    }
    
    public ImmutableList<IBlockState> getValidStates() {
        return this.validStates;
    }
    
    private List<Iterable<Comparable>> getAllowedValues() {
        final List<Iterable<Comparable>> list = (List<Iterable<Comparable>>)Lists.newArrayList();
        for (int i = 0; i < this.properties.size(); ++i) {
            list.add(((IProperty)this.properties.get(i)).getAllowedValues());
        }
        return list;
    }
    
    public IBlockState getBaseState() {
        return (IBlockState)this.validStates.get(0);
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    public Collection<IProperty> getProperties() {
        return (Collection<IProperty>)this.properties;
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper((Object)this).add("block", Block.blockRegistry.getNameForObject(this.block)).add("properties", (Object)Iterables.transform((Iterable)this.properties, (Function)BlockState.GET_NAME_FUNC)).toString();
    }
    
    static {
        COMMA_JOINER = Joiner.on(", ");
        GET_NAME_FUNC = (Function)new Function<IProperty, String>() {
            public String apply(final IProperty p_apply_1_) {
                return (p_apply_1_ == null) ? "<NULL>" : p_apply_1_.getName();
            }
        };
    }
    
    static class StateImplementation extends BlockStateBase
    {
        private final Block block;
        private final ImmutableMap<IProperty, Comparable> properties;
        private ImmutableTable<IProperty, Comparable, IBlockState> propertyValueTable;
        
        private StateImplementation(final Block blockIn, final ImmutableMap<IProperty, Comparable> propertiesIn) {
            this.block = blockIn;
            this.properties = propertiesIn;
        }
        
        @Override
        public Collection<IProperty> getPropertyNames() {
            return (Collection<IProperty>)Collections.unmodifiableCollection((Collection<? extends IProperty>)this.properties.keySet());
        }
        
        @Override
        public <T extends Comparable<T>> T getValue(final IProperty<T> property) {
            if (!this.properties.containsKey((Object)property)) {
                throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.block.getBlockState());
            }
            return property.getValueClass().cast(this.properties.get((Object)property));
        }
        
        @Override
        public <T extends Comparable<T>, V extends T> IBlockState withProperty(final IProperty<T> property, final V value) {
            if (!this.properties.containsKey((Object)property)) {
                throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.block.getBlockState());
            }
            if (!property.getAllowedValues().contains(value)) {
                throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on block " + Block.blockRegistry.getNameForObject(this.block) + ", it is not an allowed value");
            }
            return (this.properties.get((Object)property) == value) ? this : ((IBlockState)this.propertyValueTable.get((Object)property, (Object)value));
        }
        
        @Override
        public ImmutableMap<IProperty, Comparable> getProperties() {
            return this.properties;
        }
        
        @Override
        public Block getBlock() {
            return this.block;
        }
        
        @Override
        public boolean equals(final Object p_equals_1_) {
            return this == p_equals_1_;
        }
        
        @Override
        public int hashCode() {
            return this.properties.hashCode();
        }
        
        public void buildPropertyValueTable(final Map<Map<IProperty, Comparable>, StateImplementation> map) {
            if (this.propertyValueTable != null) {
                throw new IllegalStateException();
            }
            final Table<IProperty, Comparable, IBlockState> table = (Table<IProperty, Comparable, IBlockState>)HashBasedTable.create();
            for (final IProperty<? extends Comparable> iproperty : this.properties.keySet()) {
                for (final Comparable comparable : iproperty.getAllowedValues()) {
                    if (comparable != this.properties.get((Object)iproperty)) {
                        table.put((Object)iproperty, (Object)comparable, (Object)map.get(this.getPropertiesWithValue(iproperty, comparable)));
                    }
                }
            }
            this.propertyValueTable = (ImmutableTable<IProperty, Comparable, IBlockState>)ImmutableTable.copyOf((Table)table);
        }
        
        private Map<IProperty, Comparable> getPropertiesWithValue(final IProperty property, final Comparable value) {
            final Map<IProperty, Comparable> map = (Map<IProperty, Comparable>)Maps.newHashMap((Map)this.properties);
            map.put(property, value);
            return map;
        }
    }
}
