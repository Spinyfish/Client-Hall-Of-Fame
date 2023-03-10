/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data;

import java.util.HashMap;
import java.util.Map;

public class EntityNameRewriter {
    private static final Map<String, String> entityNames = new HashMap<String, String>();

    private static void reg(String past, String future) {
        entityNames.put("minecraft:" + past, "minecraft:" + future);
    }

    public static String rewrite(String entName) {
        String entityName = entityNames.get(entName);
        if (entityName != null) {
            return entityName;
        }
        entityName = entityNames.get("minecraft:" + entName);
        if (entityName != null) {
            return entityName;
        }
        return entName;
    }

    static {
        EntityNameRewriter.reg("commandblock_minecart", "command_block_minecart");
        EntityNameRewriter.reg("ender_crystal", "end_crystal");
        EntityNameRewriter.reg("evocation_fangs", "evoker_fangs");
        EntityNameRewriter.reg("evocation_illager", "evoker");
        EntityNameRewriter.reg("eye_of_ender_signal", "eye_of_ender");
        EntityNameRewriter.reg("fireworks_rocket", "firework_rocket");
        EntityNameRewriter.reg("illusion_illager", "illusioner");
        EntityNameRewriter.reg("snowman", "snow_golem");
        EntityNameRewriter.reg("villager_golem", "iron_golem");
        EntityNameRewriter.reg("vindication_illager", "vindicator");
        EntityNameRewriter.reg("xp_bottle", "experience_bottle");
        EntityNameRewriter.reg("xp_orb", "experience_orb");
    }
}

