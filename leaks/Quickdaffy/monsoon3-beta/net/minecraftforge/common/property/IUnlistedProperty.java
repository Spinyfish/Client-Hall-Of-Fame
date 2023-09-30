/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.common.property;

public interface IUnlistedProperty<V> {
    public String getName();

    public boolean isValid(V var1);

    public Class<V> getType();

    public String valueToString(V var1);
}

