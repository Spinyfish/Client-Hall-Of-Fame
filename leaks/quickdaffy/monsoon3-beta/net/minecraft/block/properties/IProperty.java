/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.properties;

import java.util.Collection;

public interface IProperty<T extends Comparable<T>> {
    public String getName();

    public Collection<T> getAllowedValues();

    public Class<T> getValueClass();

    public String getName(T var1);
}

