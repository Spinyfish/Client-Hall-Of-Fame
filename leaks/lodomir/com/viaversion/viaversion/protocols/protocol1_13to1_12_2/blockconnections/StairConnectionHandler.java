/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionHandler;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

public class StairConnectionHandler
extends ConnectionHandler {
    private static final Map<Integer, StairData> stairDataMap = new HashMap<Integer, StairData>();
    private static final Map<Short, Integer> connectedBlocks = new HashMap<Short, Integer>();

    static ConnectionData.ConnectorInitAction init() {
        LinkedList<String> baseStairs = new LinkedList<String>();
        baseStairs.add("minecraft:oak_stairs");
        baseStairs.add("minecraft:cobblestone_stairs");
        baseStairs.add("minecraft:brick_stairs");
        baseStairs.add("minecraft:stone_brick_stairs");
        baseStairs.add("minecraft:nether_brick_stairs");
        baseStairs.add("minecraft:sandstone_stairs");
        baseStairs.add("minecraft:spruce_stairs");
        baseStairs.add("minecraft:birch_stairs");
        baseStairs.add("minecraft:jungle_stairs");
        baseStairs.add("minecraft:quartz_stairs");
        baseStairs.add("minecraft:acacia_stairs");
        baseStairs.add("minecraft:dark_oak_stairs");
        baseStairs.add("minecraft:red_sandstone_stairs");
        baseStairs.add("minecraft:purpur_stairs");
        baseStairs.add("minecraft:prismarine_stairs");
        baseStairs.add("minecraft:prismarine_brick_stairs");
        baseStairs.add("minecraft:dark_prismarine_stairs");
        StairConnectionHandler connectionHandler = new StairConnectionHandler();
        return blockData -> {
            byte shape;
            int type = baseStairs.indexOf(blockData.getMinecraftKey());
            if (type == -1) {
                return;
            }
            if (blockData.getValue("waterlogged").equals("true")) {
                return;
            }
            switch (blockData.getValue("shape")) {
                case "straight": {
                    shape = 0;
                    break;
                }
                case "inner_left": {
                    shape = 1;
                    break;
                }
                case "inner_right": {
                    shape = 2;
                    break;
                }
                case "outer_left": {
                    shape = 3;
                    break;
                }
                case "outer_right": {
                    shape = 4;
                    break;
                }
                default: {
                    return;
                }
            }
            StairData stairData = new StairData(blockData.getValue("half").equals("bottom"), shape, (byte)type, BlockFace.valueOf(blockData.getValue("facing").toUpperCase(Locale.ROOT)));
            stairDataMap.put(blockData.getSavedBlockStateId(), stairData);
            connectedBlocks.put(StairConnectionHandler.getStates(stairData), blockData.getSavedBlockStateId());
            ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), (ConnectionHandler)connectionHandler);
        };
    }

    private static short getStates(StairData stairData) {
        short s = 0;
        if (stairData.isBottom()) {
            s = (short)(s | 1);
        }
        s = (short)(s | stairData.getShape() << 1);
        s = (short)(s | stairData.getType() << 4);
        s = (short)(s | stairData.getFacing().ordinal() << 9);
        return s;
    }

    @Override
    public int connect(UserConnection user, Position position, int blockState) {
        StairData stairData = stairDataMap.get(blockState);
        if (stairData == null) {
            return blockState;
        }
        short s = 0;
        if (stairData.isBottom()) {
            s = (short)(s | 1);
        }
        s = (short)(s | this.getShape(user, position, stairData) << 1);
        s = (short)(s | stairData.getType() << 4);
        Integer newBlockState = connectedBlocks.get(s = (short)(s | stairData.getFacing().ordinal() << 9));
        return newBlockState == null ? blockState : newBlockState;
    }

    private int getShape(UserConnection user, Position position, StairData stair) {
        BlockFace facing2;
        BlockFace facing = stair.getFacing();
        StairData relativeStair = stairDataMap.get(this.getBlockData(user, position.getRelative(facing)));
        if (relativeStair != null && relativeStair.isBottom() == stair.isBottom()) {
            facing2 = relativeStair.getFacing();
            if (facing.axis() != facing2.axis() && this.checkOpposite(user, stair, position, facing2.opposite())) {
                return facing2 == this.rotateAntiClockwise(facing) ? 3 : 4;
            }
        }
        if ((relativeStair = stairDataMap.get(this.getBlockData(user, position.getRelative(facing.opposite())))) != null && relativeStair.isBottom() == stair.isBottom()) {
            facing2 = relativeStair.getFacing();
            if (facing.axis() != facing2.axis() && this.checkOpposite(user, stair, position, facing2)) {
                return facing2 == this.rotateAntiClockwise(facing) ? 1 : 2;
            }
        }
        return 0;
    }

    private boolean checkOpposite(UserConnection user, StairData stair, Position position, BlockFace face) {
        StairData relativeStair = stairDataMap.get(this.getBlockData(user, position.getRelative(face)));
        return relativeStair == null || relativeStair.getFacing() != stair.getFacing() || relativeStair.isBottom() != stair.isBottom();
    }

    private BlockFace rotateAntiClockwise(BlockFace face) {
        switch (face) {
            case NORTH: {
                return BlockFace.WEST;
            }
            case SOUTH: {
                return BlockFace.EAST;
            }
            case EAST: {
                return BlockFace.NORTH;
            }
            case WEST: {
                return BlockFace.SOUTH;
            }
        }
        return face;
    }

    private static final class StairData {
        private final boolean bottom;
        private final byte shape;
        private final byte type;
        private final BlockFace facing;

        private StairData(boolean bottom, byte shape, byte type, BlockFace facing) {
            this.bottom = bottom;
            this.shape = shape;
            this.type = type;
            this.facing = facing;
        }

        public boolean isBottom() {
            return this.bottom;
        }

        public byte getShape() {
            return this.shape;
        }

        public byte getType() {
            return this.type;
        }

        public BlockFace getFacing() {
            return this.facing;
        }
    }
}

