/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.configuration;

import java.util.Map;

public interface ConfigurationProvider {
    public void set(String var1, Object var2);

    public void saveConfig();

    public void reloadConfig();

    public Map<String, Object> getValues();
}

