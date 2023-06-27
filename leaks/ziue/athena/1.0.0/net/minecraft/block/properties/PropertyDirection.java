package net.minecraft.block.properties;

import net.minecraft.util.*;
import java.util.*;
import com.google.common.base.*;
import com.google.common.collect.*;

public class PropertyDirection extends PropertyEnum<EnumFacing>
{
    protected PropertyDirection(final String name, final Collection<EnumFacing> values) {
        super(name, EnumFacing.class, values);
    }
    
    public static PropertyDirection create(final String name) {
        return create(name, (Predicate<EnumFacing>)Predicates.alwaysTrue());
    }
    
    public static PropertyDirection create(final String name, final Predicate<EnumFacing> filter) {
        return create(name, Collections2.filter((Collection)Lists.newArrayList((Object[])EnumFacing.values()), (Predicate)filter));
    }
    
    public static PropertyDirection create(final String name, final Collection<EnumFacing> values) {
        return new PropertyDirection(name, values);
    }
}
