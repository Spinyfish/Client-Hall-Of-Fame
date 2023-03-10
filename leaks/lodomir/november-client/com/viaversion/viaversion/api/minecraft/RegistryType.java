/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.minecraft;

import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

public enum RegistryType {
    BLOCK("block"),
    ITEM("item"),
    FLUID("fluid"),
    ENTITY("entity_type"),
    GAME_EVENT("game_event");

    private static final Map<String, RegistryType> MAP;
    private static final RegistryType[] VALUES;
    private final String resourceLocation;

    public static RegistryType[] getValues() {
        return VALUES;
    }

    public static @Nullable RegistryType getByKey(String resourceKey) {
        return MAP.get(resourceKey);
    }

    private RegistryType(String resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    @Deprecated
    public String getResourceLocation() {
        return this.resourceLocation;
    }

    public String resourceLocation() {
        return this.resourceLocation;
    }

    static {
        MAP = new HashMap<String, RegistryType>();
        VALUES = RegistryType.values();
        for (RegistryType type : RegistryType.getValues()) {
            MAP.put(type.resourceLocation, type);
        }
    }
}

