/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.data;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonIOException;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import com.viaversion.viaversion.util.GsonUtil;
import com.viaversion.viaversion.util.Int2IntBiMap;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MappingDataLoader {
    private static final Map<String, JsonObject> MAPPINGS_CACHE = new ConcurrentHashMap<String, JsonObject>();
    private static boolean cacheJsonMappings;

    public static boolean isCacheJsonMappings() {
        return cacheJsonMappings;
    }

    public static void enableMappingsCache() {
        cacheJsonMappings = true;
    }

    public static Map<String, JsonObject> getMappingsCache() {
        return MAPPINGS_CACHE;
    }

    public static @Nullable JsonObject loadFromDataDir(String name) {
        block9: {
            JsonObject jsonObject;
            File file = new File(Via.getPlatform().getDataFolder(), name);
            if (!file.exists()) {
                return MappingDataLoader.loadData(name);
            }
            FileReader reader = new FileReader(file);
            try {
                jsonObject = GsonUtil.getGson().fromJson((Reader)reader, JsonObject.class);
            }
            catch (Throwable throwable) {
                try {
                    try {
                        reader.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                catch (JsonSyntaxException e) {
                    Via.getPlatform().getLogger().warning(name + " is badly formatted!");
                    e.printStackTrace();
                    break block9;
                }
                catch (JsonIOException | IOException e) {
                    e.printStackTrace();
                }
            }
            reader.close();
            return jsonObject;
        }
        return null;
    }

    public static @Nullable JsonObject loadData(String name) {
        return MappingDataLoader.loadData(name, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static @Nullable JsonObject loadData(String name, boolean cacheIfEnabled) {
        JsonObject cached;
        if (cacheJsonMappings && (cached = MAPPINGS_CACHE.get(name)) != null) {
            return cached;
        }
        InputStream stream = MappingDataLoader.getResource(name);
        if (stream == null) {
            return null;
        }
        InputStreamReader reader = new InputStreamReader(stream);
        try {
            JsonObject object = GsonUtil.getGson().fromJson((Reader)reader, JsonObject.class);
            if (cacheIfEnabled && cacheJsonMappings) {
                MAPPINGS_CACHE.put(name, object);
            }
            JsonObject jsonObject = object;
            return jsonObject;
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException iOException) {}
        }
    }

    public static void mapIdentifiers(Int2IntBiMap output, JsonObject oldIdentifiers, JsonObject newIdentifiers, @Nullable JsonObject diffIdentifiers, boolean warnOnMissing) {
        Object2IntMap<String> newIdentifierMap = MappingDataLoader.indexedObjectToMap(newIdentifiers);
        for (Map.Entry<String, JsonElement> entry : oldIdentifiers.entrySet()) {
            int value = MappingDataLoader.mapIdentifierEntry(entry, newIdentifierMap, diffIdentifiers, warnOnMissing);
            if (value == -1) continue;
            output.put(Integer.parseInt(entry.getKey()), value);
        }
    }

    @Deprecated
    public static void mapIdentifiers(int[] output, JsonObject oldIdentifiers, JsonObject newIdentifiers) {
        MappingDataLoader.mapIdentifiers(output, oldIdentifiers, newIdentifiers, null);
    }

    public static void mapIdentifiers(int[] output, JsonObject oldIdentifiers, JsonObject newIdentifiers, @Nullable JsonObject diffIdentifiers, boolean warnOnMissing) {
        Object2IntMap<String> newIdentifierMap = MappingDataLoader.indexedObjectToMap(newIdentifiers);
        for (Map.Entry<String, JsonElement> entry : oldIdentifiers.entrySet()) {
            int value = MappingDataLoader.mapIdentifierEntry(entry, newIdentifierMap, diffIdentifiers, warnOnMissing);
            if (value == -1) continue;
            output[Integer.parseInt((String)entry.getKey())] = value;
        }
    }

    public static void mapIdentifiers(int[] output, JsonObject oldIdentifiers, JsonObject newIdentifiers, @Nullable JsonObject diffIdentifiers) {
        MappingDataLoader.mapIdentifiers(output, oldIdentifiers, newIdentifiers, diffIdentifiers, true);
    }

    private static int mapIdentifierEntry(Map.Entry<String, JsonElement> entry, Object2IntMap newIdentifierMap, @Nullable JsonObject diffIdentifiers, boolean warnOnMissing) {
        int value = newIdentifierMap.getInt(entry.getValue().getAsString());
        if (value == -1) {
            JsonElement diffElement;
            if (diffIdentifiers != null && (diffElement = diffIdentifiers.get(entry.getKey())) != null) {
                value = newIdentifierMap.getInt(diffElement.getAsString());
            }
            if (value == -1) {
                if (warnOnMissing && !Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                    Via.getPlatform().getLogger().warning("No key for " + entry.getValue() + " :( ");
                }
                return -1;
            }
        }
        return value;
    }

    @Deprecated
    public static void mapIdentifiers(int[] output, JsonArray oldIdentifiers, JsonArray newIdentifiers, boolean warnOnMissing) {
        MappingDataLoader.mapIdentifiers(output, oldIdentifiers, newIdentifiers, null, warnOnMissing);
    }

    public static void mapIdentifiers(int[] output, JsonArray oldIdentifiers, JsonArray newIdentifiers, @Nullable JsonObject diffIdentifiers, boolean warnOnMissing) {
        Object2IntMap<String> newIdentifierMap = MappingDataLoader.arrayToMap(newIdentifiers);
        for (int i = 0; i < oldIdentifiers.size(); ++i) {
            JsonElement oldIdentifier = oldIdentifiers.get(i);
            int mappedId = newIdentifierMap.getInt(oldIdentifier.getAsString());
            if (mappedId == -1) {
                JsonElement diffElement;
                if (diffIdentifiers != null && (diffElement = diffIdentifiers.get(oldIdentifier.getAsString())) != null) {
                    String mappedName = diffElement.getAsString();
                    if (mappedName.isEmpty()) continue;
                    mappedId = newIdentifierMap.getInt(mappedName);
                }
                if (mappedId == -1) {
                    if ((!warnOnMissing || Via.getConfig().isSuppressConversionWarnings()) && !Via.getManager().isDebug()) continue;
                    Via.getPlatform().getLogger().warning("No key for " + oldIdentifier + " :( ");
                    continue;
                }
            }
            output[i] = mappedId;
        }
    }

    public static Object2IntMap<String> indexedObjectToMap(JsonObject object) {
        Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<String>(object.size(), 0.99f);
        map.defaultReturnValue(-1);
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            map.put(entry.getValue().getAsString(), Integer.parseInt(entry.getKey()));
        }
        return map;
    }

    public static Object2IntMap<String> arrayToMap(JsonArray array) {
        Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<String>(array.size(), 0.99f);
        map.defaultReturnValue(-1);
        for (int i = 0; i < array.size(); ++i) {
            map.put(array.get(i).getAsString(), i);
        }
        return map;
    }

    public static @Nullable InputStream getResource(String name) {
        return MappingDataLoader.class.getClassLoader().getResourceAsStream("assets/viaversion/data/" + name);
    }
}

