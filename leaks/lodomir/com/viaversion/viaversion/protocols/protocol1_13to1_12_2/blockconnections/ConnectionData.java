/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord1_8;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.BasicFenceConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.BlockData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ChestConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ChorusPlantConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.DoorConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.FireConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.FlowerConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.GlassConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.MelonConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.NetherFenceConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.PumpkinConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.RedstoneConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.SnowyGrassConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.StairConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.TripwireConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.VineConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.WallConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.WrappedBlockData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers.BlockConnectionProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers.PacketBlockConnectionProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConnectionData {
    private static final BlockChangeRecord1_8[] EMPTY_RECORDS = new BlockChangeRecord1_8[0];
    public static BlockConnectionProvider blockConnectionProvider;
    static Int2ObjectMap<String> idToKey;
    static Object2IntMap<String> keyToId;
    static Int2ObjectMap<ConnectionHandler> connectionHandlerMap;
    static Int2ObjectMap<BlockData> blockConnectionData;
    static IntSet occludingStates;

    public static void update(UserConnection user, Position position) {
        for (BlockFace face : BlockFace.values()) {
            Position pos = position.getRelative(face);
            int blockState = blockConnectionProvider.getBlockData(user, pos.x(), pos.y(), pos.z());
            ConnectionHandler handler = (ConnectionHandler)connectionHandlerMap.get(blockState);
            if (handler == null) continue;
            int newBlockState = handler.connect(user, pos, blockState);
            PacketWrapper blockUpdatePacket = PacketWrapper.create(ClientboundPackets1_13.BLOCK_CHANGE, null, user);
            blockUpdatePacket.write(Type.POSITION, pos);
            blockUpdatePacket.write(Type.VAR_INT, newBlockState);
            try {
                blockUpdatePacket.send(Protocol1_13To1_12_2.class);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void updateChunkSectionNeighbours(UserConnection user, int chunkX, int chunkZ, int chunkSectionY) {
        for (int chunkDeltaX = -1; chunkDeltaX <= 1; ++chunkDeltaX) {
            for (int chunkDeltaZ = -1; chunkDeltaZ <= 1; ++chunkDeltaZ) {
                int blockY;
                if (Math.abs(chunkDeltaX) + Math.abs(chunkDeltaZ) == 0) continue;
                ArrayList<BlockChangeRecord1_8> updates = new ArrayList<BlockChangeRecord1_8>();
                if (Math.abs(chunkDeltaX) + Math.abs(chunkDeltaZ) == 2) {
                    for (blockY = chunkSectionY * 16; blockY < chunkSectionY * 16 + 16; ++blockY) {
                        int blockPosX = chunkDeltaX == 1 ? 0 : 15;
                        int blockPosZ = chunkDeltaZ == 1 ? 0 : 15;
                        ConnectionData.updateBlock(user, new Position((chunkX + chunkDeltaX << 4) + blockPosX, (short)blockY, (chunkZ + chunkDeltaZ << 4) + blockPosZ), updates);
                    }
                } else {
                    for (blockY = chunkSectionY * 16; blockY < chunkSectionY * 16 + 16; ++blockY) {
                        int zEnd;
                        int zStart;
                        int xEnd;
                        int xStart;
                        if (chunkDeltaX == 1) {
                            xStart = 0;
                            xEnd = 2;
                            zStart = 0;
                            zEnd = 16;
                        } else if (chunkDeltaX == -1) {
                            xStart = 14;
                            xEnd = 16;
                            zStart = 0;
                            zEnd = 16;
                        } else if (chunkDeltaZ == 1) {
                            xStart = 0;
                            xEnd = 16;
                            zStart = 0;
                            zEnd = 2;
                        } else {
                            xStart = 0;
                            xEnd = 16;
                            zStart = 14;
                            zEnd = 16;
                        }
                        for (int blockX = xStart; blockX < xEnd; ++blockX) {
                            for (int blockZ = zStart; blockZ < zEnd; ++blockZ) {
                                ConnectionData.updateBlock(user, new Position((chunkX + chunkDeltaX << 4) + blockX, (short)blockY, (chunkZ + chunkDeltaZ << 4) + blockZ), updates);
                            }
                        }
                    }
                }
                if (updates.isEmpty()) continue;
                PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_13.MULTI_BLOCK_CHANGE, null, user);
                wrapper.write(Type.INT, chunkX + chunkDeltaX);
                wrapper.write(Type.INT, chunkZ + chunkDeltaZ);
                wrapper.write(Type.BLOCK_CHANGE_RECORD_ARRAY, (BlockChangeRecord[])updates.toArray(EMPTY_RECORDS));
                try {
                    wrapper.send(Protocol1_13To1_12_2.class);
                    continue;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void updateBlock(UserConnection user, Position pos, List<BlockChangeRecord1_8> records) {
        int blockState = blockConnectionProvider.getBlockData(user, pos.x(), pos.y(), pos.z());
        ConnectionHandler handler = ConnectionData.getConnectionHandler(blockState);
        if (handler == null) {
            return;
        }
        int newBlockState = handler.connect(user, pos, blockState);
        records.add(new BlockChangeRecord1_8(pos.x() & 0xF, pos.y(), pos.z() & 0xF, newBlockState));
    }

    public static void updateBlockStorage(UserConnection userConnection, int x, int y, int z, int blockState) {
        if (!ConnectionData.needStoreBlocks()) {
            return;
        }
        if (ConnectionData.isWelcome(blockState)) {
            blockConnectionProvider.storeBlock(userConnection, x, y, z, blockState);
        } else {
            blockConnectionProvider.removeBlock(userConnection, x, y, z);
        }
    }

    public static void clearBlockStorage(UserConnection connection) {
        if (!ConnectionData.needStoreBlocks()) {
            return;
        }
        blockConnectionProvider.clearStorage(connection);
    }

    public static boolean needStoreBlocks() {
        return blockConnectionProvider.storesBlocks();
    }

    public static void connectBlocks(UserConnection user, Chunk chunk) {
        long xOff = chunk.getX() << 4;
        long zOff = chunk.getZ() << 4;
        for (int i = 0; i < chunk.getSections().length; ++i) {
            ChunkSection section = chunk.getSections()[i];
            if (section == null) continue;
            boolean willConnect = false;
            for (int p = 0; p < section.getPaletteSize(); ++p) {
                int id = section.getPaletteEntry(p);
                if (!ConnectionData.connects(id)) continue;
                willConnect = true;
                break;
            }
            if (!willConnect) continue;
            long yOff = i << 4;
            for (int y = 0; y < 16; ++y) {
                for (int z = 0; z < 16; ++z) {
                    for (int x = 0; x < 16; ++x) {
                        int block = section.getFlatBlock(x, y, z);
                        ConnectionHandler handler = ConnectionData.getConnectionHandler(block);
                        if (handler == null) continue;
                        block = handler.connect(user, new Position((int)(xOff + (long)x), (short)(yOff + (long)y), (int)(zOff + (long)z)), block);
                        section.setFlatBlock(x, y, z, block);
                    }
                }
            }
        }
    }

    public static void init() {
        if (!Via.getConfig().isServersideBlockConnections()) {
            return;
        }
        Via.getPlatform().getLogger().info("Loading block connection mappings ...");
        JsonObject mapping1_13 = MappingDataLoader.loadData("mapping-1.13.json", true);
        JsonObject blocks1_13 = mapping1_13.getAsJsonObject("blockstates");
        for (Map.Entry<String, JsonElement> entry : blocks1_13.entrySet()) {
            int n = Integer.parseInt(entry.getKey());
            String key = entry.getValue().getAsString();
            idToKey.put(n, key);
            keyToId.put(key, n);
        }
        connectionHandlerMap = new Int2ObjectOpenHashMap<ConnectionHandler>(3650, 0.99f);
        if (!Via.getConfig().isReduceBlockStorageMemory()) {
            blockConnectionData = new Int2ObjectOpenHashMap<BlockData>(1146, 0.99f);
            JsonObject mappingBlockConnections = MappingDataLoader.loadData("blockConnections.json");
            for (Map.Entry<String, JsonElement> entry : mappingBlockConnections.entrySet()) {
                int id = keyToId.get(entry.getKey());
                BlockData blockData = new BlockData();
                for (Map.Entry<String, JsonElement> type : entry.getValue().getAsJsonObject().entrySet()) {
                    String name = type.getKey();
                    JsonObject object = type.getValue().getAsJsonObject();
                    boolean[] data = new boolean[6];
                    for (BlockFace value : BlockFace.values()) {
                        String face = value.toString().toLowerCase(Locale.ROOT);
                        if (!object.has(face)) continue;
                        data[value.ordinal()] = object.getAsJsonPrimitive(face).getAsBoolean();
                    }
                    blockData.put(name, data);
                }
                if (entry.getKey().contains("stairs")) {
                    blockData.put("allFalseIfStairPre1_12", new boolean[6]);
                }
                blockConnectionData.put(id, blockData);
            }
        }
        JsonObject blockData = MappingDataLoader.loadData("blockData.json");
        JsonArray jsonArray = blockData.getAsJsonArray("occluding");
        for (JsonElement jsonElement : jsonArray) {
            occludingStates.add((int)keyToId.get(jsonElement.getAsString()));
        }
        ArrayList<ConnectorInitAction> arrayList = new ArrayList<ConnectorInitAction>();
        arrayList.add(PumpkinConnectionHandler.init());
        arrayList.addAll(BasicFenceConnectionHandler.init());
        arrayList.add(NetherFenceConnectionHandler.init());
        arrayList.addAll(WallConnectionHandler.init());
        arrayList.add(MelonConnectionHandler.init());
        arrayList.addAll(GlassConnectionHandler.init());
        arrayList.add(ChestConnectionHandler.init());
        arrayList.add(DoorConnectionHandler.init());
        arrayList.add(RedstoneConnectionHandler.init());
        arrayList.add(StairConnectionHandler.init());
        arrayList.add(FlowerConnectionHandler.init());
        arrayList.addAll(ChorusPlantConnectionHandler.init());
        arrayList.add(TripwireConnectionHandler.init());
        arrayList.add(SnowyGrassConnectionHandler.init());
        arrayList.add(FireConnectionHandler.init());
        if (Via.getConfig().isVineClimbFix()) {
            arrayList.add(VineConnectionHandler.init());
        }
        for (String key : keyToId.keySet()) {
            WrappedBlockData wrappedBlockData = WrappedBlockData.fromString(key);
            for (ConnectorInitAction action : arrayList) {
                action.check(wrappedBlockData);
            }
        }
        if (Via.getConfig().getBlockConnectionMethod().equalsIgnoreCase("packet")) {
            blockConnectionProvider = new PacketBlockConnectionProvider();
            Via.getManager().getProviders().register(BlockConnectionProvider.class, blockConnectionProvider);
        }
    }

    public static boolean isWelcome(int blockState) {
        return blockConnectionData.containsKey(blockState) || connectionHandlerMap.containsKey(blockState);
    }

    public static boolean connects(int blockState) {
        return connectionHandlerMap.containsKey(blockState);
    }

    public static int connect(UserConnection user, Position position, int blockState) {
        ConnectionHandler handler = (ConnectionHandler)connectionHandlerMap.get(blockState);
        return handler != null ? handler.connect(user, position, blockState) : blockState;
    }

    public static ConnectionHandler getConnectionHandler(int blockstate) {
        return (ConnectionHandler)connectionHandlerMap.get(blockstate);
    }

    public static int getId(String key) {
        return keyToId.getOrDefault((Object)key, -1);
    }

    public static String getKey(int id) {
        return (String)idToKey.get(id);
    }

    static {
        idToKey = new Int2ObjectOpenHashMap<String>(8582, 0.99f);
        keyToId = new Object2IntOpenHashMap<String>(8582, 0.99f);
        connectionHandlerMap = new Int2ObjectOpenHashMap<ConnectionHandler>(1);
        blockConnectionData = new Int2ObjectOpenHashMap<BlockData>(1);
        occludingStates = new IntOpenHashSet(377, 0.99f);
    }

    @FunctionalInterface
    static interface ConnectorInitAction {
        public void check(WrappedBlockData var1);
    }
}

