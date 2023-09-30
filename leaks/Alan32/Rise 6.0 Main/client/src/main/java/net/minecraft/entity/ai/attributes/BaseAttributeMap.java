package net.minecraft.entity.ai.attributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.server.management.LowerStringMap;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public abstract class BaseAttributeMap {
    protected final Map<IAttribute, IAttributeInstance> attributes = Maps.newHashMap();
    protected final Map<String, IAttributeInstance> attributesByName = new LowerStringMap();
    protected final Multimap<IAttribute, IAttribute> field_180377_c = HashMultimap.create();

    public IAttributeInstance getAttributeInstance(final IAttribute attribute) {
        return this.attributes.get(attribute);
    }

    public IAttributeInstance getAttributeInstanceByName(final String attributeName) {
        return this.attributesByName.get(attributeName);
    }

    /**
     * Registers an attribute with this AttributeMap, returns a modifiable AttributeInstance associated with this map
     */
    public IAttributeInstance registerAttribute(final IAttribute attribute) {
        if (this.attributesByName.containsKey(attribute.getAttributeUnlocalizedName())) {
            throw new IllegalArgumentException("Attribute is already registered!");
        } else {
            final IAttributeInstance iattributeinstance = this.func_180376_c(attribute);
            this.attributesByName.put(attribute.getAttributeUnlocalizedName(), iattributeinstance);
            this.attributes.put(attribute, iattributeinstance);

            for (IAttribute iattribute = attribute.func_180372_d(); iattribute != null; iattribute = iattribute.func_180372_d()) {
                this.field_180377_c.put(iattribute, attribute);
            }

            return iattributeinstance;
        }
    }

    protected abstract IAttributeInstance func_180376_c(IAttribute p_180376_1_);

    public Collection<IAttributeInstance> getAllAttributes() {
        return this.attributesByName.values();
    }

    public void func_180794_a(final IAttributeInstance p_180794_1_) {
    }

    public void removeAttributeModifiers(final Multimap<String, AttributeModifier> p_111148_1_) {
        for (final Entry<String, AttributeModifier> entry : p_111148_1_.entries()) {
            final IAttributeInstance iattributeinstance = this.getAttributeInstanceByName(entry.getKey());

            if (iattributeinstance != null) {
                iattributeinstance.removeModifier(entry.getValue());
            }
        }
    }

    public void applyAttributeModifiers(final Multimap<String, AttributeModifier> p_111147_1_) {
        for (final Entry<String, AttributeModifier> entry : p_111147_1_.entries()) {
            final IAttributeInstance iattributeinstance = this.getAttributeInstanceByName(entry.getKey());

            if (iattributeinstance != null) {
                iattributeinstance.removeModifier(entry.getValue());
                iattributeinstance.applyModifier(entry.getValue());
            }
        }
    }
}
