package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableTable;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public abstract class BlockStateBase implements IBlockState {
    private static final Joiner COMMA_JOINER = Joiner.on(',');
    private static final Function<Entry<IProperty, Comparable>, String> MAP_ENTRY_TO_STRING = p_apply_1_ -> {
        if (p_apply_1_ == null) {
            return "<NULL>";
        } else {
            final IProperty iproperty = p_apply_1_.getKey();
            return iproperty.getName() + "=" + iproperty.getName(p_apply_1_.getValue());
        }
    };
    private int blockId = -1;
    private int blockStateId = -1;
    private int metadata = -1;
    private ResourceLocation blockLocation = null;

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

    public ImmutableTable<IProperty<?>, Comparable<?>, IBlockState> getPropertyValueTable() {
        return null;
    }

    public <T extends Comparable<T>> IBlockState cycleProperty(final IProperty<T> property) {
        return this.withProperty(property, cyclePropertyValue(property.getAllowedValues(), this.getValue(property)));
    }

    protected static <T> T cyclePropertyValue(final Collection<T> values, final T currentValue) {
        final Iterator<T> iterator = values.iterator();

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

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(Block.blockRegistry.getNameForObject(this.getBlock()));

        if (!this.getProperties().isEmpty()) {
            stringbuilder.append("[");
            COMMA_JOINER.appendTo(stringbuilder, this.getProperties().entrySet().stream().map(MAP_ENTRY_TO_STRING::apply).collect(Collectors.toList()));
            stringbuilder.append("]");
        }

        return stringbuilder.toString();
    }
}
