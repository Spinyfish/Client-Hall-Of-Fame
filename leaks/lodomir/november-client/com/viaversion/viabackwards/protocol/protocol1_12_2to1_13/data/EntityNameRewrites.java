/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data;

import java.util.HashMap;
import java.util.Map;

public class EntityNameRewrites {
    private static final Map<String, String> ENTITY_NAMES = new HashMap<String, String>();

    private static void reg(String past, String future) {
        ENTITY_NAMES.put("minecraft:" + future, "minecraft:" + past);
    }

    public static String rewrite(String entName) {
        String entityName = ENTITY_NAMES.get(entName);
        if (entityName != null) {
            return entityName;
        }
        entityName = ENTITY_NAMES.get("minecraft:" + entName);
        if (entityName != null) {
            return entityName;
        }
        return entName;
    }

    static {
        EntityNameRewrites.reg("commandblock_minecart", "command_block_minecart");
        EntityNameRewrites.reg("ender_crystal", "end_crystal");
        EntityNameRewrites.reg("evocation_fangs", "evoker_fangs");
        EntityNameRewrites.reg("evocation_illager", "evoker");
        EntityNameRewrites.reg("eye_of_ender_signal", "eye_of_ender");
        EntityNameRewrites.reg("fireworks_rocket", "firework_rocket");
        EntityNameRewrites.reg("illusion_illager", "illusioner");
        EntityNameRewrites.reg("snowman", "snow_golem");
        EntityNameRewrites.reg("villager_golem", "iron_golem");
        EntityNameRewrites.reg("vindication_illager", "vindicator");
        EntityNameRewrites.reg("xp_bottle", "experience_bottle");
        EntityNameRewrites.reg("xp_orb", "experience_orb");
    }
}

