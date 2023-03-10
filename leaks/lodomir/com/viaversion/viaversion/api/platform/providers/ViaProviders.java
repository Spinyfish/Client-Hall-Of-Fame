/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.platform.providers;

import com.viaversion.viaversion.api.platform.providers.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ViaProviders {
    private final Map<Class<? extends Provider>, Provider> providers = new HashMap<Class<? extends Provider>, Provider>();
    private final List<Class<? extends Provider>> lonelyProviders = new ArrayList<Class<? extends Provider>>();

    public void require(Class<? extends Provider> provider) {
        this.lonelyProviders.add(provider);
    }

    public <T extends Provider> void register(Class<T> provider, T value) {
        this.providers.put(provider, value);
    }

    public <T extends Provider> void use(Class<T> provider, T value) {
        this.lonelyProviders.remove(provider);
        this.providers.put(provider, value);
    }

    public <T extends Provider> @Nullable T get(Class<T> provider) {
        Provider rawProvider = this.providers.get(provider);
        if (rawProvider != null) {
            return (T)rawProvider;
        }
        if (this.lonelyProviders.contains(provider)) {
            throw new IllegalStateException("There was no provider for " + provider + ", one is required!");
        }
        return null;
    }
}

