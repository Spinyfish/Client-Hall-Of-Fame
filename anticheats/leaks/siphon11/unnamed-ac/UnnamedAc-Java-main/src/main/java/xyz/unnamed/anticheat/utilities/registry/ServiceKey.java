package xyz.unnamed.anticheat.utilities.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

public final class ServiceKey<T> {
    private final Class<T> type;
    private final int hashCode;
    private final String name;

    private ServiceKey(Class<T> type, String name) {
        this.type = type;
        this.name = name;
        this.hashCode = Objects.hash(type, name);
    }

    @Nonnull
    public Class<T> getType() {
        return type;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ServiceKey)) {
            return false;

        } else {

            ServiceKey that = (ServiceKey)obj;
            return type == that.type && Objects.equals(name, that.name);
        }
    }

    public int hashCode() {
        return hashCode;
    }

    public String toString() {
        return name == null ? type.getName() : type.getName() + "(" + name + ")";
    }

    @Nonnull
    public static <T> ServiceKey<T> key(@Nonnull Class<T> type) {
        return new ServiceKey(type, null);
    }

    @Nonnull
    public static <T> ServiceKey<T> key(@Nonnull Class<T> type, @Nonnull String name) {
        return new ServiceKey(type, name);
    }
}
