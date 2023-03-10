/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 */
package com.viaversion.viaversion.protocols.protocol1_11to1_10;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class BlockEntityRewriter {
    private static BiMap<String, String> oldToNewNames = HashBiMap.create();

    private static void rewrite(String oldName, String newName) {
        oldToNewNames.put((Object)oldName, (Object)("minecraft:" + newName));
    }

    public static BiMap<String, String> inverse() {
        return oldToNewNames.inverse();
    }

    public static String toNewIdentifier(String oldId) {
        String newName = (String)oldToNewNames.get((Object)oldId);
        if (newName != null) {
            return newName;
        }
        return oldId;
    }

    static {
        BlockEntityRewriter.rewrite("Furnace", "furnace");
        BlockEntityRewriter.rewrite("Chest", "chest");
        BlockEntityRewriter.rewrite("EnderChest", "ender_chest");
        BlockEntityRewriter.rewrite("RecordPlayer", "jukebox");
        BlockEntityRewriter.rewrite("Trap", "dispenser");
        BlockEntityRewriter.rewrite("Dropper", "dropper");
        BlockEntityRewriter.rewrite("Sign", "sign");
        BlockEntityRewriter.rewrite("MobSpawner", "mob_spawner");
        BlockEntityRewriter.rewrite("Music", "noteblock");
        BlockEntityRewriter.rewrite("Piston", "piston");
        BlockEntityRewriter.rewrite("Cauldron", "brewing_stand");
        BlockEntityRewriter.rewrite("EnchantTable", "enchanting_table");
        BlockEntityRewriter.rewrite("Airportal", "end_portal");
        BlockEntityRewriter.rewrite("Beacon", "beacon");
        BlockEntityRewriter.rewrite("Skull", "skull");
        BlockEntityRewriter.rewrite("DLDetector", "daylight_detector");
        BlockEntityRewriter.rewrite("Hopper", "hopper");
        BlockEntityRewriter.rewrite("Comparator", "comparator");
        BlockEntityRewriter.rewrite("FlowerPot", "flower_pot");
        BlockEntityRewriter.rewrite("Banner", "banner");
        BlockEntityRewriter.rewrite("Structure", "structure_block");
        BlockEntityRewriter.rewrite("EndGateway", "end_gateway");
        BlockEntityRewriter.rewrite("Control", "command_block");
    }
}

