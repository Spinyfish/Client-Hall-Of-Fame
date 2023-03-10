/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.util;

import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.viaversion.viaversion.util.CommentStore;
import com.viaversion.viaversion.util.YamlConstructor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

public abstract class Config
implements ConfigurationProvider {
    private static final ThreadLocal<Yaml> YAML = ThreadLocal.withInitial(() -> {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(false);
        options.setIndent(2);
        return new Yaml(new YamlConstructor(), new Representer(), options);
    });
    private final CommentStore commentStore = new CommentStore('.', 2);
    private final File configFile;
    private Map<String, Object> config;

    public Config(File configFile) {
        this.configFile = configFile;
    }

    public abstract URL getDefaultConfigURL();

    public synchronized Map<String, Object> loadConfig(File location) {
        List<String> unsupported = this.getUnsupportedOptions();
        URL jarConfigFile = this.getDefaultConfigURL();
        try {
            this.commentStore.storeComments(jarConfigFile.openStream());
            for (String option : unsupported) {
                List<String> comments = this.commentStore.header(option);
                if (comments == null) continue;
                comments.clear();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Object> config = null;
        if (location.exists()) {
            try (FileInputStream input = new FileInputStream(location);){
                config = (Map)YAML.get().load(input);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (config == null) {
            config = new HashMap<String, Object>();
        }
        Map<String, Object> defaults = config;
        try (InputStream stream = jarConfigFile.openStream();){
            defaults = (Map)YAML.get().load(stream);
            for (String string : unsupported) {
                defaults.remove(string);
            }
            for (Map.Entry entry : config.entrySet()) {
                if (!defaults.containsKey(entry.getKey()) || unsupported.contains(entry.getKey())) continue;
                defaults.put((String)entry.getKey(), entry.getValue());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.handleConfig(defaults);
        this.saveConfig(location, defaults);
        return defaults;
    }

    protected abstract void handleConfig(Map<String, Object> var1);

    public synchronized void saveConfig(File location, Map<String, Object> config) {
        try {
            this.commentStore.writeComments(YAML.get().dump(config), location);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract List<String> getUnsupportedOptions();

    @Override
    public void set(String path, Object value) {
        this.config.put(path, value);
    }

    @Override
    public void saveConfig() {
        this.configFile.getParentFile().mkdirs();
        this.saveConfig(this.configFile, this.config);
    }

    @Override
    public void reloadConfig() {
        this.configFile.getParentFile().mkdirs();
        this.config = new ConcurrentSkipListMap<String, Object>(this.loadConfig(this.configFile));
    }

    @Override
    public Map<String, Object> getValues() {
        return this.config;
    }

    public <T> @Nullable T get(String key, Class<T> clazz, T def) {
        Object o = this.config.get(key);
        if (o != null) {
            return (T)o;
        }
        return def;
    }

    public boolean getBoolean(String key, boolean def) {
        Object o = this.config.get(key);
        if (o != null) {
            return (Boolean)o;
        }
        return def;
    }

    public @Nullable String getString(String key, @Nullable String def) {
        Object o = this.config.get(key);
        if (o != null) {
            return (String)o;
        }
        return def;
    }

    public int getInt(String key, int def) {
        Object o = this.config.get(key);
        if (o != null) {
            if (o instanceof Number) {
                return ((Number)o).intValue();
            }
            return def;
        }
        return def;
    }

    public double getDouble(String key, double def) {
        Object o = this.config.get(key);
        if (o != null) {
            if (o instanceof Number) {
                return ((Number)o).doubleValue();
            }
            return def;
        }
        return def;
    }

    public List<Integer> getIntegerList(String key) {
        Object o = this.config.get(key);
        return o != null ? (List)o : new ArrayList();
    }

    public List<String> getStringList(String key) {
        Object o = this.config.get(key);
        return o != null ? (List)o : new ArrayList();
    }

    public @Nullable JsonElement getSerializedComponent(String key) {
        Object o = this.config.get(key);
        if (o != null && !((String)o).isEmpty()) {
            return GsonComponentSerializer.gson().serializeToTree(LegacyComponentSerializer.legacySection().deserialize((String)o));
        }
        return null;
    }
}

