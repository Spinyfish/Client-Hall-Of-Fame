/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.WrappedBlockData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FireConnectionHandler
extends ConnectionHandler {
    private static final String[] WOOD_TYPES = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"};
    private static final Map<Byte, Integer> connectedBlocks = new HashMap<Byte, Integer>();
    private static final Set<Integer> flammableBlocks = new HashSet<Integer>();

    private static void addWoodTypes(Set<String> set, String suffix) {
        for (String woodType : WOOD_TYPES) {
            set.add("minecraft:" + woodType + suffix);
        }
    }

    static ConnectionData.ConnectorInitAction init() {
        HashSet<String> flammabeIds = new HashSet<String>();
        flammabeIds.add("minecraft:tnt");
        flammabeIds.add("minecraft:vine");
        flammabeIds.add("minecraft:bookshelf");
        flammabeIds.add("minecraft:hay_block");
        flammabeIds.add("minecraft:deadbush");
        FireConnectionHandler.addWoodTypes(flammabeIds, "_slab");
        FireConnectionHandler.addWoodTypes(flammabeIds, "_log");
        FireConnectionHandler.addWoodTypes(flammabeIds, "_planks");
        FireConnectionHandler.addWoodTypes(flammabeIds, "_leaves");
        FireConnectionHandler.addWoodTypes(flammabeIds, "_fence");
        FireConnectionHandler.addWoodTypes(flammabeIds, "_fence_gate");
        FireConnectionHandler.addWoodTypes(flammabeIds, "_stairs");
        FireConnectionHandler connectionHandler = new FireConnectionHandler();
        return blockData -> {
            String key = blockData.getMinecraftKey();
            if (key.contains("_wool") || key.contains("_carpet") || flammabeIds.contains(key)) {
                flammableBlocks.add(blockData.getSavedBlockStateId());
            } else if (key.equals("minecraft:fire")) {
                int id = blockData.getSavedBlockStateId();
                connectedBlocks.put(FireConnectionHandler.getStates(blockData), id);
                ConnectionData.connectionHandlerMap.put(id, (ConnectionHandler)connectionHandler);
            }
        };
    }

    private static byte getStates(WrappedBlockData blockData) {
        byte states = 0;
        if (blockData.getValue("east").equals("true")) {
            states = (byte)(states | 1);
        }
        if (blockData.getValue("north").equals("true")) {
            states = (byte)(states | 2);
        }
        if (blockData.getValue("south").equals("true")) {
            states = (byte)(states | 4);
        }
        if (blockData.getValue("up").equals("true")) {
            states = (byte)(states | 8);
        }
        if (blockData.getValue("west").equals("true")) {
            states = (byte)(states | 0x10);
        }
        return states;
    }

    @Override
    public int connect(UserConnection user, Position position, int blockState) {
        byte states = 0;
        if (flammableBlocks.contains(this.getBlockData(user, position.getRelative(BlockFace.EAST)))) {
            states = (byte)(states | 1);
        }
        if (flammableBlocks.contains(this.getBlockData(user, position.getRelative(BlockFace.NORTH)))) {
            states = (byte)(states | 2);
        }
        if (flammableBlocks.contains(this.getBlockData(user, position.getRelative(BlockFace.SOUTH)))) {
            states = (byte)(states | 4);
        }
        if (flammableBlocks.contains(this.getBlockData(user, position.getRelative(BlockFace.TOP)))) {
            states = (byte)(states | 8);
        }
        if (flammableBlocks.contains(this.getBlockData(user, position.getRelative(BlockFace.WEST)))) {
            states = (byte)(states | 0x10);
        }
        return connectedBlocks.get(states);
    }
}

