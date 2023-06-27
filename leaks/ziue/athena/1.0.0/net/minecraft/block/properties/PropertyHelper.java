package net.minecraft.block.properties;

import com.google.common.base.*;

public abstract class PropertyHelper<T extends Comparable<T>> implements IProperty<T>
{
    private final Class<T> valueClass;
    private final String name;
    
    protected PropertyHelper(final String name, final Class<T> valueClass) {
        this.valueClass = valueClass;
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public Class<T> getValueClass() {
        return this.valueClass;
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper((Object)this).add("name", (Object)this.name).add("clazz", (Object)this.valueClass).add("values", (Object)this.getAllowedValues()).toString();
    }
    
    @Override
    public boolean equals(final Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            final PropertyHelper propertyhelper = (PropertyHelper)p_equals_1_;
            return this.valueClass.equals(propertyhelper.valueClass) && this.name.equals(propertyhelper.name);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return 31 * this.valueClass.hashCode() + this.name.hashCode();
    }
}
