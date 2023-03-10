/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Optional;

public class SpawnEggRewriter {
    private static final BiMap<String, Integer> spawnEggs = HashBiMap.create();

    private static void registerSpawnEgg(String key) {
        spawnEggs.put((Object)key, (Object)spawnEggs.size());
    }

    public static int getSpawnEggId(String entityIdentifier) {
        if (!spawnEggs.containsKey((Object)entityIdentifier)) {
            return -1;
        }
        return 0x17F0000 | (Integer)spawnEggs.get((Object)entityIdentifier) & 0xFFFF;
    }

    public static Optional<String> getEntityId(int spawnEggId) {
        if (spawnEggId >> 16 != 383) {
            return Optional.empty();
        }
        return Optional.ofNullable((String)spawnEggs.inverse().get((Object)(spawnEggId & 0xFFFF)));
    }

    static {
        SpawnEggRewriter.registerSpawnEgg("minecraft:bat");
        SpawnEggRewriter.registerSpawnEgg("minecraft:blaze");
        SpawnEggRewriter.registerSpawnEgg("minecraft:cave_spider");
        SpawnEggRewriter.registerSpawnEgg("minecraft:chicken");
        SpawnEggRewriter.registerSpawnEgg("minecraft:cow");
        SpawnEggRewriter.registerSpawnEgg("minecraft:creeper");
        SpawnEggRewriter.registerSpawnEgg("minecraft:donkey");
        SpawnEggRewriter.registerSpawnEgg("minecraft:elder_guardian");
        SpawnEggRewriter.registerSpawnEgg("minecraft:enderman");
        SpawnEggRewriter.registerSpawnEgg("minecraft:endermite");
        SpawnEggRewriter.registerSpawnEgg("minecraft:evocation_illager");
        SpawnEggRewriter.registerSpawnEgg("minecraft:ghast");
        SpawnEggRewriter.registerSpawnEgg("minecraft:guardian");
        SpawnEggRewriter.registerSpawnEgg("minecraft:horse");
        SpawnEggRewriter.registerSpawnEgg("minecraft:husk");
        SpawnEggRewriter.registerSpawnEgg("minecraft:llama");
        SpawnEggRewriter.registerSpawnEgg("minecraft:magma_cube");
        SpawnEggRewriter.registerSpawnEgg("minecraft:mooshroom");
        SpawnEggRewriter.registerSpawnEgg("minecraft:mule");
        SpawnEggRewriter.registerSpawnEgg("minecraft:ocelot");
        SpawnEggRewriter.registerSpawnEgg("minecraft:parrot");
        SpawnEggRewriter.registerSpawnEgg("minecraft:pig");
        SpawnEggRewriter.registerSpawnEgg("minecraft:polar_bear");
        SpawnEggRewriter.registerSpawnEgg("minecraft:rabbit");
        SpawnEggRewriter.registerSpawnEgg("minecraft:sheep");
        SpawnEggRewriter.registerSpawnEgg("minecraft:shulker");
        SpawnEggRewriter.registerSpawnEgg("minecraft:silverfish");
        SpawnEggRewriter.registerSpawnEgg("minecraft:skeleton");
        SpawnEggRewriter.registerSpawnEgg("minecraft:skeleton_horse");
        SpawnEggRewriter.registerSpawnEgg("minecraft:slime");
        SpawnEggRewriter.registerSpawnEgg("minecraft:spider");
        SpawnEggRewriter.registerSpawnEgg("minecraft:squid");
        SpawnEggRewriter.registerSpawnEgg("minecraft:stray");
        SpawnEggRewriter.registerSpawnEgg("minecraft:vex");
        SpawnEggRewriter.registerSpawnEgg("minecraft:villager");
        SpawnEggRewriter.registerSpawnEgg("minecraft:vindication_illager");
        SpawnEggRewriter.registerSpawnEgg("minecraft:witch");
        SpawnEggRewriter.registerSpawnEgg("minecraft:wither_skeleton");
        SpawnEggRewriter.registerSpawnEgg("minecraft:wolf");
        SpawnEggRewriter.registerSpawnEgg("minecraft:zombie");
        SpawnEggRewriter.registerSpawnEgg("minecraft:zombie_horse");
        SpawnEggRewriter.registerSpawnEgg("minecraft:zombie_pigman");
        SpawnEggRewriter.registerSpawnEgg("minecraft:zombie_villager");
    }
}

