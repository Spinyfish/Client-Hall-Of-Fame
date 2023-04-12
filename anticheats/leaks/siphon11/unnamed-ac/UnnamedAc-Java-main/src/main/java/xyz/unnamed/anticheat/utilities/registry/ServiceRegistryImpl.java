package xyz.unnamed.anticheat.utilities.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

public class ServiceRegistryImpl implements ServiceRegistry {
    private final Map<ServiceKey<?>, Provider<?>> registry = new ConcurrentHashMap();

    public ServiceRegistryImpl() {
    }

    @Nonnull
    public Set<ServiceKey<?>> keySet() {
        return registry.keySet();
    }

    @Nonnull
    public Set<Map.Entry<ServiceKey<?>, Provider<?>>> entrySet() {
        return registry.entrySet();
    }

    @Nullable
    public <T> T getOrNull(@Nonnull ServiceKey<T> key) {
        Provider<T> provider = (Provider<T>) registry.get(key);
        return provider == null ? null : provider.get();
    }

    @Nullable
    public <T> T put(@Nonnull ServiceKey<T> key, T service) {
        return put(key, singleton(service));
    }

    @Nullable
    public <T> T put(@Nonnull ServiceKey<T> key, Provider<T> service) {
        return (T) registry.put(key, service);
    }

    @Nullable
    public <T> T putIfAbsent(@Nonnull ServiceKey<T> type, T service) {
        return putIfAbsent(type, singleton(service));
    }

    @Nullable
    public <T> T putIfAbsent(@Nonnull ServiceKey<T> key, Provider<T> service) {
        return (T) registry.putIfAbsent(key, service);
    }

    private static <T> Provider<T> singleton(T service) {
        return () -> service;
    }

}
