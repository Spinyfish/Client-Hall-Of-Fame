package xyz.unnamed.anticheat.utilities.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;


/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

public interface ServiceRegistry {

    @Nonnull
    Set<ServiceKey<?>> keySet();

    @Nonnull
    Set<Map.Entry<ServiceKey<?>, Provider<?>>> entrySet();

    @Nonnull
    default <T> T get(@Nonnull ServiceKey<T> key) {
        T service = getOrNull(key);
        if (service == null) {
            throw new NullPointerException("Service not found: " + key);
        } else {
            return service;
        }
    }

    @Nonnull
    default <T> T get(@Nonnull Class<T> type) {
        return get(ServiceKey.key(type));
    }

    @Nullable
    <T> T getOrNull(@Nonnull ServiceKey<T> key);

    @Nullable
    default <T> T getOrNull(@Nonnull Class<T> type) {
        return getOrNull(ServiceKey.key(type));
    }

    @Nullable
    default <T> T put(@Nonnull Class<T> type, Provider<T> service) {
        return put(ServiceKey.key(type), service);
    }

    @Nullable
    <T> T put(@Nonnull ServiceKey<T> key, Provider<T> service);

    @Nullable
    default <T> T put(@Nonnull Class<T> type, T service) {
        return put(ServiceKey.key(type), service);
    }

    @Nullable
    <T> T put(@Nonnull ServiceKey<T> key, T service);

    @Nullable
    <T> T putIfAbsent(@Nonnull ServiceKey<T> key, T service);

    @Nullable
    default <T> T putIfAbsent(@Nonnull Class<T> type, T service) {
        return putIfAbsent(ServiceKey.key(type), service);
    }

    @Nullable
    <T> T putIfAbsent(@Nonnull ServiceKey<T> key, Provider<T> service);

    @Nullable
    default <T> T putIfAbsent(@Nonnull Class<T> type, Provider<T> service) {
        return putIfAbsent(ServiceKey.key(type), service);
    }

}
