package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModifiableAttributeInstance implements IAttributeInstance, java.io.Serializable{
    /**
     * The BaseAttributeMap this attributeInstance can be found in
     */
    private final BaseAttributeMap attributeMap;

    /**
     * The Attribute this is an instance of
     */
    private final IAttribute genericAttribute;
    private final Map<Integer, Set<AttributeModifier>> mapByOperation = Maps.newHashMap();
    private final Map<String, Set<AttributeModifier>> mapByName = Maps.newHashMap();
    private final Map<UUID, AttributeModifier> mapByUUID = Maps.newHashMap();
    private double baseValue;
    private boolean needsUpdate = true;
    private double cachedValue;

    public ModifiableAttributeInstance(final BaseAttributeMap attributeMapIn, final IAttribute genericAttributeIn) {
        this.attributeMap = attributeMapIn;
        this.genericAttribute = genericAttributeIn;
        this.baseValue = genericAttributeIn.getDefaultValue();

        for (int i = 0; i < 3; ++i) {
            this.mapByOperation.put(Integer.valueOf(i), Sets.newHashSet());
        }
    }

    /**
     * Get the Attribute this is an instance of
     */
    public IAttribute getAttribute() {
        return this.genericAttribute;
    }

    public double getBaseValue() {
        return this.baseValue;
    }

    public void setBaseValue(final double baseValue) {
        if (baseValue != this.getBaseValue()) {
            this.baseValue = baseValue;
            this.flagForUpdate();
        }
    }

    public Collection<AttributeModifier> getModifiersByOperation(final int operation) {
        return this.mapByOperation.get(Integer.valueOf(operation));
    }

    public Collection<AttributeModifier> func_111122_c() {
        final Set<AttributeModifier> set = Sets.newHashSet();

        for (int i = 0; i < 3; ++i) {
            set.addAll(this.getModifiersByOperation(i));
        }

        return set;
    }

    /**
     * Returns attribute modifier, if any, by the given UUID
     */
    public AttributeModifier getModifier(final UUID uuid) {
        return this.mapByUUID.get(uuid);
    }

    public boolean hasModifier(final AttributeModifier modifier) {
        return this.mapByUUID.get(modifier.getID()) != null;
    }

    public void applyModifier(final AttributeModifier modifier) {
        if (this.getModifier(modifier.getID()) != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        } else {
            Set<AttributeModifier> set = this.mapByName.get(modifier.getName());

            if (set == null) {
                set = Sets.newHashSet();
                this.mapByName.put(modifier.getName(), set);
            }

            this.mapByOperation.get(Integer.valueOf(modifier.getOperation())).add(modifier);
            set.add(modifier);
            this.mapByUUID.put(modifier.getID(), modifier);
            this.flagForUpdate();
        }
    }

    protected void flagForUpdate() {
        this.needsUpdate = true;
        this.attributeMap.func_180794_a(this);
    }

    public void removeModifier(final AttributeModifier modifier) {
        for (int i = 0; i < 3; ++i) {
            final Set<AttributeModifier> set = this.mapByOperation.get(Integer.valueOf(i));
            set.remove(modifier);
        }

        final Set<AttributeModifier> set1 = this.mapByName.get(modifier.getName());

        if (set1 != null) {
            set1.remove(modifier);

            if (set1.isEmpty()) {
                this.mapByName.remove(modifier.getName());
            }
        }

        this.mapByUUID.remove(modifier.getID());
        this.flagForUpdate();
    }

    public void removeAllModifiers() {
        final Collection<AttributeModifier> collection = this.func_111122_c();

        if (collection != null) {
            for (final AttributeModifier attributemodifier : Lists.newArrayList(collection)) {
                this.removeModifier(attributemodifier);
            }
        }
    }

    public double getAttributeValue() {
        if (this.needsUpdate) {
            this.cachedValue = this.computeValue();
            this.needsUpdate = false;
        }

        return this.cachedValue;
    }

    private double computeValue() {
        double d0 = this.getBaseValue();

        for (final AttributeModifier attributemodifier : this.func_180375_b(0)) {
            d0 += attributemodifier.getAmount();
        }

        double d1 = d0;

        for (final AttributeModifier attributemodifier1 : this.func_180375_b(1)) {
            d1 += d0 * attributemodifier1.getAmount();
        }

        for (final AttributeModifier attributemodifier2 : this.func_180375_b(2)) {
            d1 *= 1.0D + attributemodifier2.getAmount();
        }

        return this.genericAttribute.clampValue(d1);
    }

    @SneakyThrows // fix the annoying CME when not on a server
    private Collection<AttributeModifier> func_180375_b(final int p_180375_1_) {
        final Set<AttributeModifier> set = Sets.newHashSet(this.getModifiersByOperation(p_180375_1_));

        for (IAttribute iattribute = this.genericAttribute.func_180372_d(); iattribute != null; iattribute = iattribute.func_180372_d()) {
            final IAttributeInstance iattributeinstance = this.attributeMap.getAttributeInstance(iattribute);

            if (iattributeinstance != null) {
                set.addAll(iattributeinstance.getModifiersByOperation(p_180375_1_));
            }
        }

        return set;
    }
}
