/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data;

import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.NamedSoundRewriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class NamedSoundMapping {
    private static final Map<String, String> SOUNDS = new HashMap<String, String>();

    public static String getOldId(String sound1_13) {
        if (sound1_13.startsWith("minecraft:")) {
            sound1_13 = sound1_13.substring(10);
        }
        return SOUNDS.get(sound1_13);
    }

    static {
        try {
            Field field = NamedSoundRewriter.class.getDeclaredField("oldToNew");
            field.setAccessible(true);
            Map sounds = (Map)field.get(null);
            sounds.forEach((sound1_12, sound1_13) -> SOUNDS.put((String)sound1_13, (String)sound1_12));
        }
        catch (IllegalAccessException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }
}

