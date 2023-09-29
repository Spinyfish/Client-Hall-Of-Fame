package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.*;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.Cartesian;
import net.minecraft.util.MapPopulator;

import java.util.*;
import java.util.stream.Collectors;

public class BlockState {
    private static final Joiner COMMA_JOINER = Joiner.on(", ");
    private static final Function<IProperty, String> GET_NAME_FUNC = p_apply_1_ -> p_apply_1_ == null ? "<NULL>" : p_apply_1_.getName();
    private final Block block;
    private final ImmutableList<IProperty> properties;
    private final ImmutableList<IBlockState> validStates;

    public BlockState(final Block blockIn, final IProperty... properties) {
        this.block = blockIn;
        Arrays.sort(properties, Comparator.comparing(IProperty::getName));
        this.properties = ImmutableList.copyOf(properties);
        final Map<Map<IProperty, Comparable>, BlockState.StateImplementation> map = Maps.newLinkedHashMap();
        final List<BlockState.StateImplementation> list = Lists.newArrayList();

        for (final List<Comparable> list1 : Cartesian.cartesianProduct(this.getAllowedValues())) {
            final Map<IProperty, Comparable> map1 = MapPopulator.createMap(this.properties, list1);
            final BlockState.StateImplementation blockstate$stateimplementation = new BlockState.StateImplementation(blockIn, ImmutableMap.copyOf(map1));
            map.put(map1, blockstate$stateimplementation);
            list.add(blockstate$stateimplementation);
        }

        for (final BlockState.StateImplementation blockstate$stateimplementation1 : list) {
            blockstate$stateimplementation1.buildPropertyValueTable(map);
        }

        this.validStates = ImmutableList.copyOf(list);
    }

    public ImmutableList<IBlockState> getValidStates() {
        return this.validStates;
    }

    private List<Iterable<Comparable>> getAllowedValues() {
        final List<Iterable<Comparable>> list = Lists.newArrayList();

        for (int i = 0; i < this.properties.size(); ++i) {
            list.add(this.properties.get(i).getAllowedValues());
        }

        return list;
    }

    public IBlockState getBaseState() {
        return this.validStates.get(0);
    }

    public Block getBlock() {
        return this.block;
    }

    public Collection<IProperty> getProperties() {
        return this.properties;
    }

    public String toString() {
        return Objects.toStringHelper(this).add("block", Block.blockRegistry.getNameForObject(this.block)).add("properties", this.properties.stream().map(GET_NAME_FUNC::apply).collect(Collectors.toList())).toString();
    }

    static class StateImplementation extends BlockStateBase {
        private final Block block;
        private final ImmutableMap<IProperty, Comparable> properties;
        private ImmutableTable<IProperty, Comparable, IBlockState> propertyValueTable;

        private StateImplementation(final Block blockIn, final ImmutableMap<IProperty, Comparable> propertiesIn) {
            this.block = blockIn;
            this.properties = propertiesIn;
        }

        public Collection<IProperty> getPropertyNames() {
            return Collections.unmodifiableCollection(this.properties.keySet());
        }

        public <T extends Comparable<T>> T getValue(final IProperty<T> property) {
            if (!this.properties.containsKey(property)) {
                throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.block.getBlockState());
            } else {
                return property.getValueClass().cast(this.properties.get(property));
            }
        }

        public <T extends Comparable<T>, V extends T> IBlockState withProperty(final IProperty<T> property, final V value) {
            if (!this.properties.containsKey(property)) {
                throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.block.getBlockState());
            } else if (!property.getAllowedValues().contains(value)) {
                throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on block " + Block.blockRegistry.getNameForObject(this.block) + ", it is not an allowed value");
            } else {
                return this.properties.get(property) == value ? this : this.propertyValueTable.get(property, value);
            }
        }

        public ImmutableMap<IProperty, Comparable> getProperties() {
            return this.properties;
        }

        public Block getBlock() {
            return this.block;
        }

        public boolean equals(final Object p_equals_1_) {
            return this == p_equals_1_;
        }

        public int hashCode() {
            return this.properties.hashCode();
        }

        public void buildPropertyValueTable(final Map<Map<IProperty, Comparable>, BlockState.StateImplementation> map) {
            if (this.propertyValueTable != null) {
                throw new IllegalStateException();
            } else {
                final Table<IProperty, Comparable, IBlockState> table = HashBasedTable.create();

                for (final IProperty iproperty : this.properties.keySet()) {
                    for (final Object comparable : iproperty.getAllowedValues()) {
                        if (comparable != this.properties.get(iproperty)) {
                            table.put(iproperty, (Comparable) comparable, map.get(this.setPropertyValue(iproperty, (Comparable) comparable)));
                        }
                    }
                }

                this.propertyValueTable = ImmutableTable.copyOf(table);
            }
        }

        private Map<IProperty, Comparable> setPropertyValue(final IProperty property, final Comparable value) {
            final Map<IProperty, Comparable> map = Maps.newHashMap(this.properties);
            map.put(property, value);
            return map;
        }
    }
}
