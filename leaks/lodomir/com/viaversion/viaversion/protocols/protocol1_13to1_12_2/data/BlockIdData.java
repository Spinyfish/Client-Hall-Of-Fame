/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ObjectArrays
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data;

import com.google.common.collect.ObjectArrays;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.MappingData;
import com.viaversion.viaversion.util.GsonUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class BlockIdData {
    public static final String[] PREVIOUS = new String[0];
    public static Map<String, String[]> blockIdMapping;
    public static Map<String, String[]> fallbackReverseMapping;
    public static Int2ObjectMap<String> numberIdToString;

    public static void init() {
        InputStream stream = MappingData.class.getClassLoader().getResourceAsStream("assets/viaversion/data/blockIds1.12to1.13.json");
        try (InputStreamReader reader = new InputStreamReader(stream);){
            Map map = (Map)GsonUtil.getGson().fromJson((Reader)reader, new TypeToken<Map<String, String[]>>(){}.getType());
            blockIdMapping = new HashMap<String, String[]>(map);
            fallbackReverseMapping = new HashMap<String, String[]>();
            for (Map.Entry<String, String[]> entry : blockIdMapping.entrySet()) {
                for (String val : entry.getValue()) {
                    Object[] previous = fallbackReverseMapping.get(val);
                    if (previous == null) {
                        previous = PREVIOUS;
                    }
                    fallbackReverseMapping.put(val, (String[])ObjectArrays.concat((Object[])previous, (Object)entry.getKey()));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        InputStream blockS = MappingData.class.getClassLoader().getResourceAsStream("assets/viaversion/data/blockNumberToString1.12.json");
        try (InputStreamReader blockR = new InputStreamReader(blockS);){
            Map map = (Map)GsonUtil.getGson().fromJson((Reader)blockR, new TypeToken<Map<Integer, String>>(){}.getType());
            numberIdToString = new Int2ObjectOpenHashMap<String>(map);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

