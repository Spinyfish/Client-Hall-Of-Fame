/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.management;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.optifine.ChunkPosComparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerManager {
    private static final Logger pmLogger = LogManager.getLogger();
    private final WorldServer theWorldServer;
    private final List<EntityPlayerMP> players = Lists.newArrayList();
    private final LongHashMap<PlayerInstance> playerInstances = new LongHashMap();
    private final List<PlayerInstance> playerInstancesToUpdate = Lists.newArrayList();
    private final List<PlayerInstance> playerInstanceList = Lists.newArrayList();
    private int playerViewRadius;
    private long previousTotalWorldTime;
    private final int[][] xzDirectionsConst = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    private final Map<EntityPlayerMP, Set<ChunkCoordIntPair>> mapPlayerPendingEntries = new HashMap<EntityPlayerMP, Set<ChunkCoordIntPair>>();

    public PlayerManager(WorldServer serverWorld) {
        this.theWorldServer = serverWorld;
        this.setPlayerViewRadius(serverWorld.getMinecraftServer().getConfigurationManager().getViewDistance());
    }

    public WorldServer getWorldServer() {
        return this.theWorldServer;
    }

    public void updatePlayerInstances() {
        WorldProvider worldprovider;
        Set<Map.Entry<EntityPlayerMP, Set<ChunkCoordIntPair>>> set = this.mapPlayerPendingEntries.entrySet();
        Iterator<Map.Entry<EntityPlayerMP, Set<ChunkCoordIntPair>>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<EntityPlayerMP, Set<ChunkCoordIntPair>> entry = iterator.next();
            Set<ChunkCoordIntPair> set1 = entry.getValue();
            if (set1.isEmpty()) continue;
            EntityPlayerMP entityplayermp = entry.getKey();
            if (entityplayermp.worldObj != this.theWorldServer) {
                iterator.remove();
                continue;
            }
            int i = this.playerViewRadius / 3 + 1;
            if (!Config.isLazyChunkLoading()) {
                i = this.playerViewRadius * 2 + 1;
            }
            for (ChunkCoordIntPair chunkcoordintpair : this.getNearest(set1, entityplayermp, i)) {
                PlayerInstance playermanager$playerinstance = this.getPlayerInstance(chunkcoordintpair.chunkXPos, chunkcoordintpair.chunkZPos, true);
                playermanager$playerinstance.addPlayer(entityplayermp);
                set1.remove(chunkcoordintpair);
            }
        }
        long j = this.theWorldServer.getTotalWorldTime();
        if (j - this.previousTotalWorldTime > 8000L) {
            this.previousTotalWorldTime = j;
            for (int k = 0; k < this.playerInstanceList.size(); ++k) {
                PlayerInstance playermanager$playerinstance1 = this.playerInstanceList.get(k);
                playermanager$playerinstance1.onUpdate();
                playermanager$playerinstance1.processChunk();
            }
        } else {
            for (int l = 0; l < this.playerInstancesToUpdate.size(); ++l) {
                PlayerInstance playermanager$playerinstance2 = this.playerInstancesToUpdate.get(l);
                playermanager$playerinstance2.onUpdate();
            }
        }
        this.playerInstancesToUpdate.clear();
        if (this.players.isEmpty() && !(worldprovider = this.theWorldServer.provider).canRespawnHere()) {
            this.theWorldServer.theChunkProviderServer.unloadAllChunks();
        }
    }

    public boolean hasPlayerInstance(int chunkX, int chunkZ) {
        long i = (long)chunkX + Integer.MAX_VALUE | (long)chunkZ + Integer.MAX_VALUE << 32;
        return this.playerInstances.getValueByKey(i) != null;
    }

    private PlayerInstance getPlayerInstance(int chunkX, int chunkZ, boolean createIfAbsent) {
        long i = (long)chunkX + Integer.MAX_VALUE | (long)chunkZ + Integer.MAX_VALUE << 32;
        PlayerInstance playermanager$playerinstance = this.playerInstances.getValueByKey(i);
        if (playermanager$playerinstance == null && createIfAbsent) {
            playermanager$playerinstance = new PlayerInstance(chunkX, chunkZ);
            this.playerInstances.add(i, playermanager$playerinstance);
            this.playerInstanceList.add(playermanager$playerinstance);
        }
        return playermanager$playerinstance;
    }

    public void markBlockForUpdate(BlockPos pos) {
        int j;
        int i = pos.getX() >> 4;
        PlayerInstance playermanager$playerinstance = this.getPlayerInstance(i, j = pos.getZ() >> 4, false);
        if (playermanager$playerinstance != null) {
            playermanager$playerinstance.flagChunkForUpdate(pos.getX() & 0xF, pos.getY(), pos.getZ() & 0xF);
        }
    }

    public void addPlayer(EntityPlayerMP player) {
        int i = (int)player.posX >> 4;
        int j = (int)player.posZ >> 4;
        player.managedPosX = player.posX;
        player.managedPosZ = player.posZ;
        int k = Math.min(this.playerViewRadius, 8);
        int l = i - k;
        int i1 = i + k;
        int j1 = j - k;
        int k1 = j + k;
        Set<ChunkCoordIntPair> set = this.getPendingEntriesSafe(player);
        for (int l1 = i - this.playerViewRadius; l1 <= i + this.playerViewRadius; ++l1) {
            for (int i2 = j - this.playerViewRadius; i2 <= j + this.playerViewRadius; ++i2) {
                if (l1 >= l && l1 <= i1 && i2 >= j1 && i2 <= k1) {
                    this.getPlayerInstance(l1, i2, true).addPlayer(player);
                    continue;
                }
                set.add(new ChunkCoordIntPair(l1, i2));
            }
        }
        this.players.add(player);
        this.filterChunkLoadQueue(player);
    }

    public void filterChunkLoadQueue(EntityPlayerMP player) {
        ArrayList list = Lists.newArrayList(player.loadedChunks);
        int i = 0;
        int j = this.playerViewRadius;
        int k = (int)player.posX >> 4;
        int l = (int)player.posZ >> 4;
        int i1 = 0;
        int j1 = 0;
        ChunkCoordIntPair chunkcoordintpair = this.getPlayerInstance(k, l, true).chunkCoords;
        player.loadedChunks.clear();
        if (list.contains(chunkcoordintpair)) {
            player.loadedChunks.add(chunkcoordintpair);
        }
        for (int k1 = 1; k1 <= j * 2; ++k1) {
            for (int l1 = 0; l1 < 2; ++l1) {
                int[] aint = this.xzDirectionsConst[i++ % 4];
                for (int i2 = 0; i2 < k1; ++i2) {
                    chunkcoordintpair = this.getPlayerInstance(k + (i1 += aint[0]), l + (j1 += aint[1]), true).chunkCoords;
                    if (!list.contains(chunkcoordintpair)) continue;
                    player.loadedChunks.add(chunkcoordintpair);
                }
            }
        }
        i %= 4;
        for (int j2 = 0; j2 < j * 2; ++j2) {
            chunkcoordintpair = this.getPlayerInstance(k + (i1 += this.xzDirectionsConst[i][0]), l + (j1 += this.xzDirectionsConst[i][1]), true).chunkCoords;
            if (!list.contains(chunkcoordintpair)) continue;
            player.loadedChunks.add(chunkcoordintpair);
        }
    }

    public void removePlayer(EntityPlayerMP player) {
        this.mapPlayerPendingEntries.remove(player);
        int i = (int)player.managedPosX >> 4;
        int j = (int)player.managedPosZ >> 4;
        for (int k = i - this.playerViewRadius; k <= i + this.playerViewRadius; ++k) {
            for (int l = j - this.playerViewRadius; l <= j + this.playerViewRadius; ++l) {
                PlayerInstance playermanager$playerinstance = this.getPlayerInstance(k, l, false);
                if (playermanager$playerinstance == null) continue;
                playermanager$playerinstance.removePlayer(player);
            }
        }
        this.players.remove(player);
    }

    private boolean overlaps(int x1, int z1, int x2, int z2, int radius) {
        int i = x1 - x2;
        int j = z1 - z2;
        return i >= -radius && i <= radius ? j >= -radius && j <= radius : false;
    }

    public void updateMountedMovingPlayer(EntityPlayerMP player) {
        int i = (int)player.posX >> 4;
        int j = (int)player.posZ >> 4;
        double d0 = player.managedPosX - player.posX;
        double d1 = player.managedPosZ - player.posZ;
        double d2 = d0 * d0 + d1 * d1;
        if (d2 >= 64.0) {
            int k = (int)player.managedPosX >> 4;
            int l = (int)player.managedPosZ >> 4;
            int i1 = this.playerViewRadius;
            int j1 = i - k;
            int k1 = j - l;
            if (j1 != 0 || k1 != 0) {
                Set<ChunkCoordIntPair> set = this.getPendingEntriesSafe(player);
                for (int l1 = i - i1; l1 <= i + i1; ++l1) {
                    for (int i2 = j - i1; i2 <= j + i1; ++i2) {
                        if (!this.overlaps(l1, i2, k, l, i1)) {
                            if (Config.isLazyChunkLoading()) {
                                set.add(new ChunkCoordIntPair(l1, i2));
                            } else {
                                this.getPlayerInstance(l1, i2, true).addPlayer(player);
                            }
                        }
                        if (this.overlaps(l1 - j1, i2 - k1, i, j, i1)) continue;
                        set.remove(new ChunkCoordIntPair(l1 - j1, i2 - k1));
                        PlayerInstance playermanager$playerinstance = this.getPlayerInstance(l1 - j1, i2 - k1, false);
                        if (playermanager$playerinstance == null) continue;
                        playermanager$playerinstance.removePlayer(player);
                    }
                }
                this.filterChunkLoadQueue(player);
                player.managedPosX = player.posX;
                player.managedPosZ = player.posZ;
            }
        }
    }

    public boolean isPlayerWatchingChunk(EntityPlayerMP player, int chunkX, int chunkZ) {
        PlayerInstance playermanager$playerinstance = this.getPlayerInstance(chunkX, chunkZ, false);
        return playermanager$playerinstance != null && playermanager$playerinstance.playersWatchingChunk.contains(player) && !player.loadedChunks.contains(playermanager$playerinstance.chunkCoords);
    }

    public void setPlayerViewRadius(int radius) {
        if ((radius = MathHelper.clamp_int(radius, 3, 64)) != this.playerViewRadius) {
            int i = radius - this.playerViewRadius;
            for (EntityPlayerMP entityplayermp : Lists.newArrayList(this.players)) {
                int j = (int)entityplayermp.posX >> 4;
                int k = (int)entityplayermp.posZ >> 4;
                Set<ChunkCoordIntPair> set = this.getPendingEntriesSafe(entityplayermp);
                if (i > 0) {
                    for (int j1 = j - radius; j1 <= j + radius; ++j1) {
                        for (int k1 = k - radius; k1 <= k + radius; ++k1) {
                            if (Config.isLazyChunkLoading()) {
                                set.add(new ChunkCoordIntPair(j1, k1));
                                continue;
                            }
                            PlayerInstance playermanager$playerinstance1 = this.getPlayerInstance(j1, k1, true);
                            if (playermanager$playerinstance1.playersWatchingChunk.contains(entityplayermp)) continue;
                            playermanager$playerinstance1.addPlayer(entityplayermp);
                        }
                    }
                    continue;
                }
                for (int l = j - this.playerViewRadius; l <= j + this.playerViewRadius; ++l) {
                    for (int i1 = k - this.playerViewRadius; i1 <= k + this.playerViewRadius; ++i1) {
                        if (this.overlaps(l, i1, j, k, radius)) continue;
                        set.remove(new ChunkCoordIntPair(l, i1));
                        PlayerInstance playermanager$playerinstance = this.getPlayerInstance(l, i1, true);
                        if (playermanager$playerinstance == null) continue;
                        playermanager$playerinstance.removePlayer(entityplayermp);
                    }
                }
            }
            this.playerViewRadius = radius;
        }
    }

    public static int getFurthestViewableBlock(int distance) {
        return distance * 16 - 16;
    }

    private PriorityQueue<ChunkCoordIntPair> getNearest(Set<ChunkCoordIntPair> p_getNearest_1_, EntityPlayerMP p_getNearest_2_, int p_getNearest_3_) {
        float f;
        for (f = p_getNearest_2_.rotationYaw + 90.0f; f <= -180.0f; f += 360.0f) {
        }
        while (f > 180.0f) {
            f -= 360.0f;
        }
        double d0 = (double)f * (Math.PI / 180);
        double d1 = p_getNearest_2_.rotationPitch;
        double d2 = d1 * (Math.PI / 180);
        ChunkPosComparator chunkposcomparator = new ChunkPosComparator(p_getNearest_2_.chunkCoordX, p_getNearest_2_.chunkCoordZ, d0, d2);
        Comparator<ChunkCoordIntPair> comparator = Collections.reverseOrder(chunkposcomparator);
        PriorityQueue<ChunkCoordIntPair> priorityqueue = new PriorityQueue<ChunkCoordIntPair>(p_getNearest_3_, comparator);
        for (ChunkCoordIntPair chunkcoordintpair : p_getNearest_1_) {
            if (priorityqueue.size() < p_getNearest_3_) {
                priorityqueue.add(chunkcoordintpair);
                continue;
            }
            ChunkCoordIntPair chunkcoordintpair1 = priorityqueue.peek();
            if (chunkposcomparator.compare(chunkcoordintpair, chunkcoordintpair1) >= 0) continue;
            priorityqueue.remove();
            priorityqueue.add(chunkcoordintpair);
        }
        return priorityqueue;
    }

    private Set<ChunkCoordIntPair> getPendingEntriesSafe(EntityPlayerMP p_getPendingEntriesSafe_1_) {
        Set<ChunkCoordIntPair> set = this.mapPlayerPendingEntries.get(p_getPendingEntriesSafe_1_);
        if (set != null) {
            return set;
        }
        int i = Math.min(this.playerViewRadius, 8);
        int j = this.playerViewRadius * 2 + 1;
        int k = i * 2 + 1;
        int l = j * j - k * k;
        l = Math.max(l, 16);
        HashSet<ChunkCoordIntPair> hashset = new HashSet<ChunkCoordIntPair>(l);
        this.mapPlayerPendingEntries.put(p_getPendingEntriesSafe_1_, hashset);
        return hashset;
    }

    class PlayerInstance {
        private final List<EntityPlayerMP> playersWatchingChunk = Lists.newArrayList();
        private final ChunkCoordIntPair chunkCoords;
        private short[] locationOfBlockChange = new short[64];
        private int numBlocksToUpdate;
        private int flagsYAreasToUpdate;
        private long previousWorldTime;

        public PlayerInstance(int chunkX, int chunkZ) {
            this.chunkCoords = new ChunkCoordIntPair(chunkX, chunkZ);
            PlayerManager.this.getWorldServer().theChunkProviderServer.loadChunk(chunkX, chunkZ);
        }

        public void addPlayer(EntityPlayerMP player) {
            if (this.playersWatchingChunk.contains(player)) {
                pmLogger.debug("Failed to add player. {} already is in chunk {}, {}", new Object[]{player, this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos});
            } else {
                if (this.playersWatchingChunk.isEmpty()) {
                    this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
                }
                this.playersWatchingChunk.add(player);
                player.loadedChunks.add(this.chunkCoords);
            }
        }

        public void removePlayer(EntityPlayerMP player) {
            if (this.playersWatchingChunk.contains(player)) {
                Chunk chunk = PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
                if (chunk.isPopulated()) {
                    player.playerNetServerHandler.sendPacket(new S21PacketChunkData(chunk, true, 0));
                }
                this.playersWatchingChunk.remove(player);
                player.loadedChunks.remove(this.chunkCoords);
                if (this.playersWatchingChunk.isEmpty()) {
                    long i = (long)this.chunkCoords.chunkXPos + Integer.MAX_VALUE | (long)this.chunkCoords.chunkZPos + Integer.MAX_VALUE << 32;
                    this.increaseInhabitedTime(chunk);
                    PlayerManager.this.playerInstances.remove(i);
                    PlayerManager.this.playerInstanceList.remove(this);
                    if (this.numBlocksToUpdate > 0) {
                        PlayerManager.this.playerInstancesToUpdate.remove(this);
                    }
                    PlayerManager.this.getWorldServer().theChunkProviderServer.dropChunk(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
                }
            }
        }

        public void processChunk() {
            this.increaseInhabitedTime(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos));
        }

        private void increaseInhabitedTime(Chunk theChunk) {
            theChunk.setInhabitedTime(theChunk.getInhabitedTime() + PlayerManager.this.theWorldServer.getTotalWorldTime() - this.previousWorldTime);
            this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
        }

        public void flagChunkForUpdate(int x, int y, int z) {
            if (this.numBlocksToUpdate == 0) {
                PlayerManager.this.playerInstancesToUpdate.add(this);
            }
            this.flagsYAreasToUpdate |= 1 << (y >> 4);
            if (this.numBlocksToUpdate < 64) {
                short short1 = (short)(x << 12 | z << 8 | y);
                for (int i = 0; i < this.numBlocksToUpdate; ++i) {
                    if (this.locationOfBlockChange[i] != short1) continue;
                    return;
                }
                this.locationOfBlockChange[this.numBlocksToUpdate++] = short1;
            }
        }

        public void sendToAllPlayersWatchingChunk(Packet thePacket) {
            for (int i = 0; i < this.playersWatchingChunk.size(); ++i) {
                EntityPlayerMP entityplayermp = this.playersWatchingChunk.get(i);
                if (entityplayermp.loadedChunks.contains(this.chunkCoords)) continue;
                entityplayermp.playerNetServerHandler.sendPacket(thePacket);
            }
        }

        public void onUpdate() {
            if (this.numBlocksToUpdate != 0) {
                if (this.numBlocksToUpdate == 1) {
                    int k1 = (this.locationOfBlockChange[0] >> 12 & 0xF) + this.chunkCoords.chunkXPos * 16;
                    int i2 = this.locationOfBlockChange[0] & 0xFF;
                    int k2 = (this.locationOfBlockChange[0] >> 8 & 0xF) + this.chunkCoords.chunkZPos * 16;
                    BlockPos blockpos = new BlockPos(k1, i2, k2);
                    this.sendToAllPlayersWatchingChunk(new S23PacketBlockChange(PlayerManager.this.theWorldServer, blockpos));
                    if (PlayerManager.this.theWorldServer.getBlockState(blockpos).getBlock().hasTileEntity()) {
                        this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(blockpos));
                    }
                } else if (this.numBlocksToUpdate != 64) {
                    this.sendToAllPlayersWatchingChunk(new S22PacketMultiBlockChange(this.numBlocksToUpdate, this.locationOfBlockChange, PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos)));
                    for (int j1 = 0; j1 < this.numBlocksToUpdate; ++j1) {
                        int l1 = (this.locationOfBlockChange[j1] >> 12 & 0xF) + this.chunkCoords.chunkXPos * 16;
                        int j2 = this.locationOfBlockChange[j1] & 0xFF;
                        int l2 = (this.locationOfBlockChange[j1] >> 8 & 0xF) + this.chunkCoords.chunkZPos * 16;
                        BlockPos blockpos1 = new BlockPos(l1, j2, l2);
                        if (!PlayerManager.this.theWorldServer.getBlockState(blockpos1).getBlock().hasTileEntity()) continue;
                        this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(blockpos1));
                    }
                } else {
                    int i = this.chunkCoords.chunkXPos * 16;
                    int j = this.chunkCoords.chunkZPos * 16;
                    this.sendToAllPlayersWatchingChunk(new S21PacketChunkData(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos), false, this.flagsYAreasToUpdate));
                    for (int k = 0; k < 16; ++k) {
                        if ((this.flagsYAreasToUpdate & 1 << k) == 0) continue;
                        int l = k << 4;
                        List<TileEntity> list = PlayerManager.this.theWorldServer.getTileEntitiesIn(i, l, j, i + 16, l + 16, j + 16);
                        for (int i1 = 0; i1 < list.size(); ++i1) {
                            this.sendTileToAllPlayersWatchingChunk(list.get(i1));
                        }
                    }
                }
                this.numBlocksToUpdate = 0;
                this.flagsYAreasToUpdate = 0;
            }
        }

        private void sendTileToAllPlayersWatchingChunk(TileEntity theTileEntity) {
            Packet packet;
            if (theTileEntity != null && (packet = theTileEntity.getDescriptionPacket()) != null) {
                this.sendToAllPlayersWatchingChunk(packet);
            }
        }
    }
}

