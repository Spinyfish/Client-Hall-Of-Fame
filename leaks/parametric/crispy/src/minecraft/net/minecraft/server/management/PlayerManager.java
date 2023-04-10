package net.minecraft.server.management;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerManager
{
    private static final Logger pmLogger = LogManager.getLogger();
    private final WorldServer theWorldServer;

    /** players in the current instance */
    private final List players = Lists.newArrayList();

    /** the hash of all playerInstances created */
    private final LongHashMap playerInstances = new LongHashMap();

    /** the playerInstances(chunks) that need to be updated */
    private final List playerInstancesToUpdate = Lists.newArrayList();

    /** This field is using when chunk should be processed (every 8000 ticks) */
    private final List playerInstanceList = Lists.newArrayList();

    /**
     * Number of chunks the server sends to the client. Valid 3<=x<=15. In server.properties.
     */
    private int playerViewRadius;

    /** time what is using to check if InhabitedTime should be calculated */
    private long previousTotalWorldTime;

    /** x, z direction vectors: east, south, west, north */
    private final int[][] xzDirectionsConst = new int[][] {{1, 0}, {0, 1}, { -1, 0}, {0, -1}};

    public PlayerManager(WorldServer serverWorld)
    {
        this.theWorldServer = serverWorld;
        this.setPlayerViewRadius(serverWorld.getMinecraftServer().getConfigurationManager().getViewDistance());
    }

    /**
     * Returns the WorldServer associated with this PlayerManager
     */
    public WorldServer getWorldServer()
    {
        return this.theWorldServer;
    }

    /**
     * updates all the player instances that need to be updated
     */
    public void updatePlayerInstances()
    {
        long var1 = this.theWorldServer.getTotalWorldTime();
        int var3;
        PlayerManager.PlayerInstance var4;

        if (var1 - this.previousTotalWorldTime > 8000L)
        {
            this.previousTotalWorldTime = var1;

            for (var3 = 0; var3 < this.playerInstanceList.size(); ++var3)
            {
                var4 = (PlayerManager.PlayerInstance)this.playerInstanceList.get(var3);
                var4.onUpdate();
                var4.processChunk();
            }
        }
        else
        {
            for (var3 = 0; var3 < this.playerInstancesToUpdate.size(); ++var3)
            {
                var4 = (PlayerManager.PlayerInstance)this.playerInstancesToUpdate.get(var3);
                var4.onUpdate();
            }
        }

        this.playerInstancesToUpdate.clear();

        if (this.players.isEmpty())
        {
            WorldProvider var5 = this.theWorldServer.provider;

            if (!var5.canRespawnHere())
            {
                this.theWorldServer.theChunkProviderServer.unloadAllChunks();
            }
        }
    }

    public boolean hasPlayerInstance(int chunkX, int chunkZ)
    {
        long var3 = (long)chunkX + 2147483647L | (long)chunkZ + 2147483647L << 32;
        return this.playerInstances.getValueByKey(var3) != null;
    }

    /**
     * passi n the chunk x and y and a flag as to whether or not the instance should be made if it doesnt exist
     *  
     * @param chunkX The chunk X coordinate
     * @param chunkZ The chunk Z coordinate
     * @param createIfAbsent If the player instance should be created if it doesn't exist
     */
    private PlayerManager.PlayerInstance getPlayerInstance(int chunkX, int chunkZ, boolean createIfAbsent)
    {
        long var4 = (long)chunkX + 2147483647L | (long)chunkZ + 2147483647L << 32;
        PlayerManager.PlayerInstance var6 = (PlayerManager.PlayerInstance)this.playerInstances.getValueByKey(var4);

        if (var6 == null && createIfAbsent)
        {
            var6 = new PlayerManager.PlayerInstance(chunkX, chunkZ);
            this.playerInstances.add(var4, var6);
            this.playerInstanceList.add(var6);
        }

        return var6;
    }

    public void markBlockForUpdate(BlockPos pos)
    {
        int var2 = pos.getX() >> 4;
        int var3 = pos.getZ() >> 4;
        PlayerManager.PlayerInstance var4 = this.getPlayerInstance(var2, var3, false);

        if (var4 != null)
        {
            var4.flagChunkForUpdate(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
        }
    }

    /**
     * Adds an EntityPlayerMP to the PlayerManager and to all player instances within player visibility
     *  
     * @param player The player to add
     */
    public void addPlayer(EntityPlayerMP player)
    {
        int var2 = (int)player.posX >> 4;
        int var3 = (int)player.posZ >> 4;
        player.managedPosX = player.posX;
        player.managedPosZ = player.posZ;

        for (int var4 = var2 - this.playerViewRadius; var4 <= var2 + this.playerViewRadius; ++var4)
        {
            for (int var5 = var3 - this.playerViewRadius; var5 <= var3 + this.playerViewRadius; ++var5)
            {
                this.getPlayerInstance(var4, var5, true).addPlayer(player);
            }
        }

        this.players.add(player);
        this.filterChunkLoadQueue(player);
    }

    /**
     * Removes all chunks from the given player's chunk load queue that are not in viewing range of the player.
     */
    public void filterChunkLoadQueue(EntityPlayerMP player)
    {
        ArrayList var2 = Lists.newArrayList(player.loadedChunks);
        int var3 = 0;
        int var4 = this.playerViewRadius;
        int var5 = (int)player.posX >> 4;
        int var6 = (int)player.posZ >> 4;
        int var7 = 0;
        int var8 = 0;
        ChunkCoordIntPair var9 = this.getPlayerInstance(var5, var6, true).chunkCoords;
        player.loadedChunks.clear();

        if (var2.contains(var9))
        {
            player.loadedChunks.add(var9);
        }

        int var10;

        for (var10 = 1; var10 <= var4 * 2; ++var10)
        {
            for (int var11 = 0; var11 < 2; ++var11)
            {
                int[] var12 = this.xzDirectionsConst[var3++ % 4];

                for (int var13 = 0; var13 < var10; ++var13)
                {
                    var7 += var12[0];
                    var8 += var12[1];
                    var9 = this.getPlayerInstance(var5 + var7, var6 + var8, true).chunkCoords;

                    if (var2.contains(var9))
                    {
                        player.loadedChunks.add(var9);
                    }
                }
            }
        }

        var3 %= 4;

        for (var10 = 0; var10 < var4 * 2; ++var10)
        {
            var7 += this.xzDirectionsConst[var3][0];
            var8 += this.xzDirectionsConst[var3][1];
            var9 = this.getPlayerInstance(var5 + var7, var6 + var8, true).chunkCoords;

            if (var2.contains(var9))
            {
                player.loadedChunks.add(var9);
            }
        }
    }

    /**
     * Removes an EntityPlayerMP from the PlayerManager.
     *  
     * @param player The player to remove
     */
    public void removePlayer(EntityPlayerMP player)
    {
        int var2 = (int)player.managedPosX >> 4;
        int var3 = (int)player.managedPosZ >> 4;

        for (int var4 = var2 - this.playerViewRadius; var4 <= var2 + this.playerViewRadius; ++var4)
        {
            for (int var5 = var3 - this.playerViewRadius; var5 <= var3 + this.playerViewRadius; ++var5)
            {
                PlayerManager.PlayerInstance var6 = this.getPlayerInstance(var4, var5, false);

                if (var6 != null)
                {
                    var6.removePlayer(player);
                }
            }
        }

        this.players.remove(player);
    }

    /**
     * Determine if two rectangles centered at the given points overlap for the provided radius. Arguments: x1, z1, x2,
     * z2, radius.
     *  
     * @param x1 The first X coordinate
     * @param z1 The first Z coordinate
     * @param x2 The second X coordinate
     * @param z2 The second Z coordinate
     * @param radius The radius
     */
    private boolean overlaps(int x1, int z1, int x2, int z2, int radius)
    {
        int var6 = x1 - x2;
        int var7 = z1 - z2;
        return var6 >= -radius && var6 <= radius ? var7 >= -radius && var7 <= radius : false;
    }

    /**
     * update chunks around a player being moved by server logic (e.g. cart, boat)
     *  
     * @param player The player to update chunks around
     */
    public void updateMountedMovingPlayer(EntityPlayerMP player)
    {
        int var2 = (int)player.posX >> 4;
        int var3 = (int)player.posZ >> 4;
        double var4 = player.managedPosX - player.posX;
        double var6 = player.managedPosZ - player.posZ;
        double var8 = var4 * var4 + var6 * var6;

        if (var8 >= 64.0D)
        {
            int var10 = (int)player.managedPosX >> 4;
            int var11 = (int)player.managedPosZ >> 4;
            int var12 = this.playerViewRadius;
            int var13 = var2 - var10;
            int var14 = var3 - var11;

            if (var13 != 0 || var14 != 0)
            {
                for (int var15 = var2 - var12; var15 <= var2 + var12; ++var15)
                {
                    for (int var16 = var3 - var12; var16 <= var3 + var12; ++var16)
                    {
                        if (!this.overlaps(var15, var16, var10, var11, var12))
                        {
                            this.getPlayerInstance(var15, var16, true).addPlayer(player);
                        }

                        if (!this.overlaps(var15 - var13, var16 - var14, var2, var3, var12))
                        {
                            PlayerManager.PlayerInstance var17 = this.getPlayerInstance(var15 - var13, var16 - var14, false);

                            if (var17 != null)
                            {
                                var17.removePlayer(player);
                            }
                        }
                    }
                }

                this.filterChunkLoadQueue(player);
                player.managedPosX = player.posX;
                player.managedPosZ = player.posZ;
            }
        }
    }

    public boolean isPlayerWatchingChunk(EntityPlayerMP player, int chunkX, int chunkZ)
    {
        PlayerManager.PlayerInstance var4 = this.getPlayerInstance(chunkX, chunkZ, false);
        return var4 != null && var4.playersWatchingChunk.contains(player) && !player.loadedChunks.contains(var4.chunkCoords);
    }

    public void setPlayerViewRadius(int radius)
    {
        radius = MathHelper.clamp_int(radius, 3, 32);

        if (radius != this.playerViewRadius)
        {
            int var2 = radius - this.playerViewRadius;
            ArrayList var3 = Lists.newArrayList(this.players);
            Iterator var4 = var3.iterator();

            while (var4.hasNext())
            {
                EntityPlayerMP var5 = (EntityPlayerMP)var4.next();
                int var6 = (int)var5.posX >> 4;
                int var7 = (int)var5.posZ >> 4;
                int var8;
                int var9;

                if (var2 > 0)
                {
                    for (var8 = var6 - radius; var8 <= var6 + radius; ++var8)
                    {
                        for (var9 = var7 - radius; var9 <= var7 + radius; ++var9)
                        {
                            PlayerManager.PlayerInstance var10 = this.getPlayerInstance(var8, var9, true);

                            if (!var10.playersWatchingChunk.contains(var5))
                            {
                                var10.addPlayer(var5);
                            }
                        }
                    }
                }
                else
                {
                    for (var8 = var6 - this.playerViewRadius; var8 <= var6 + this.playerViewRadius; ++var8)
                    {
                        for (var9 = var7 - this.playerViewRadius; var9 <= var7 + this.playerViewRadius; ++var9)
                        {
                            if (!this.overlaps(var8, var9, var6, var7, radius))
                            {
                                this.getPlayerInstance(var8, var9, true).removePlayer(var5);
                            }
                        }
                    }
                }
            }

            this.playerViewRadius = radius;
        }
    }

    /**
     * Get the furthest viewable block given player's view distance
     */
    public static int getFurthestViewableBlock(int distance)
    {
        return distance * 16 - 16;
    }

    class PlayerInstance
    {
        private final List playersWatchingChunk = Lists.newArrayList();
        private final ChunkCoordIntPair chunkCoords;
        private short[] locationOfBlockChange = new short[64];
        private int numBlocksToUpdate;
        private int flagsYAreasToUpdate;
        private long previousWorldTime;

        public PlayerInstance(int chunkX, int chunkZ)
        {
            this.chunkCoords = new ChunkCoordIntPair(chunkX, chunkZ);
            PlayerManager.this.getWorldServer().theChunkProviderServer.loadChunk(chunkX, chunkZ);
        }

        public void addPlayer(EntityPlayerMP player)
        {
            if (this.playersWatchingChunk.contains(player))
            {
                PlayerManager.pmLogger.debug("Failed to add player. {} already is in chunk {}, {}", new Object[] {player, Integer.valueOf(this.chunkCoords.chunkXPos), Integer.valueOf(this.chunkCoords.chunkZPos)});
            }
            else
            {
                if (this.playersWatchingChunk.isEmpty())
                {
                    this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
                }

                this.playersWatchingChunk.add(player);
                player.loadedChunks.add(this.chunkCoords);
            }
        }

        public void removePlayer(EntityPlayerMP player)
        {
            if (this.playersWatchingChunk.contains(player))
            {
                Chunk var2 = PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);

                if (var2.isPopulated())
                {
                    player.playerNetServerHandler.sendPacket(new S21PacketChunkData(var2, true, 0));
                }

                this.playersWatchingChunk.remove(player);
                player.loadedChunks.remove(this.chunkCoords);

                if (this.playersWatchingChunk.isEmpty())
                {
                    long var3 = (long)this.chunkCoords.chunkXPos + 2147483647L | (long)this.chunkCoords.chunkZPos + 2147483647L << 32;
                    this.increaseInhabitedTime(var2);
                    PlayerManager.this.playerInstances.remove(var3);
                    PlayerManager.this.playerInstanceList.remove(this);

                    if (this.numBlocksToUpdate > 0)
                    {
                        PlayerManager.this.playerInstancesToUpdate.remove(this);
                    }

                    PlayerManager.this.getWorldServer().theChunkProviderServer.dropChunk(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
                }
            }
        }

        public void processChunk()
        {
            this.increaseInhabitedTime(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos));
        }

        private void increaseInhabitedTime(Chunk theChunk)
        {
            theChunk.setInhabitedTime(theChunk.getInhabitedTime() + PlayerManager.this.theWorldServer.getTotalWorldTime() - this.previousWorldTime);
            this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
        }

        public void flagChunkForUpdate(int x, int y, int z)
        {
            if (this.numBlocksToUpdate == 0)
            {
                PlayerManager.this.playerInstancesToUpdate.add(this);
            }

            this.flagsYAreasToUpdate |= 1 << (y >> 4);

            if (this.numBlocksToUpdate < 64)
            {
                short var4 = (short)(x << 12 | z << 8 | y);

                for (int var5 = 0; var5 < this.numBlocksToUpdate; ++var5)
                {
                    if (this.locationOfBlockChange[var5] == var4)
                    {
                        return;
                    }
                }

                this.locationOfBlockChange[this.numBlocksToUpdate++] = var4;
            }
        }

        public void sendToAllPlayersWatchingChunk(Packet thePacket)
        {
            for (int var2 = 0; var2 < this.playersWatchingChunk.size(); ++var2)
            {
                EntityPlayerMP var3 = (EntityPlayerMP)this.playersWatchingChunk.get(var2);

                if (!var3.loadedChunks.contains(this.chunkCoords))
                {
                    var3.playerNetServerHandler.sendPacket(thePacket);
                }
            }
        }

        public void onUpdate()
        {
            if (this.numBlocksToUpdate != 0)
            {
                int var1;
                int var2;
                int var3;

                if (this.numBlocksToUpdate == 1)
                {
                    var1 = (this.locationOfBlockChange[0] >> 12 & 15) + this.chunkCoords.chunkXPos * 16;
                    var2 = this.locationOfBlockChange[0] & 255;
                    var3 = (this.locationOfBlockChange[0] >> 8 & 15) + this.chunkCoords.chunkZPos * 16;
                    BlockPos var4 = new BlockPos(var1, var2, var3);
                    this.sendToAllPlayersWatchingChunk(new S23PacketBlockChange(PlayerManager.this.theWorldServer, var4));

                    if (PlayerManager.this.theWorldServer.getBlockState(var4).getBlock().hasTileEntity())
                    {
                        this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(var4));
                    }
                }
                else
                {
                    int var7;

                    if (this.numBlocksToUpdate == 64)
                    {
                        var1 = this.chunkCoords.chunkXPos * 16;
                        var2 = this.chunkCoords.chunkZPos * 16;
                        this.sendToAllPlayersWatchingChunk(new S21PacketChunkData(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos), false, this.flagsYAreasToUpdate));

                        for (var3 = 0; var3 < 16; ++var3)
                        {
                            if ((this.flagsYAreasToUpdate & 1 << var3) != 0)
                            {
                                var7 = var3 << 4;
                                List var5 = PlayerManager.this.theWorldServer.getTileEntitiesIn(var1, var7, var2, var1 + 16, var7 + 16, var2 + 16);

                                for (int var6 = 0; var6 < var5.size(); ++var6)
                                {
                                    this.sendTileToAllPlayersWatchingChunk((TileEntity)var5.get(var6));
                                }
                            }
                        }
                    }
                    else
                    {
                        this.sendToAllPlayersWatchingChunk(new S22PacketMultiBlockChange(this.numBlocksToUpdate, this.locationOfBlockChange, PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos)));

                        for (var1 = 0; var1 < this.numBlocksToUpdate; ++var1)
                        {
                            var2 = (this.locationOfBlockChange[var1] >> 12 & 15) + this.chunkCoords.chunkXPos * 16;
                            var3 = this.locationOfBlockChange[var1] & 255;
                            var7 = (this.locationOfBlockChange[var1] >> 8 & 15) + this.chunkCoords.chunkZPos * 16;
                            BlockPos var8 = new BlockPos(var2, var3, var7);

                            if (PlayerManager.this.theWorldServer.getBlockState(var8).getBlock().hasTileEntity())
                            {
                                this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(var8));
                            }
                        }
                    }
                }

                this.numBlocksToUpdate = 0;
                this.flagsYAreasToUpdate = 0;
            }
        }

        private void sendTileToAllPlayersWatchingChunk(TileEntity theTileEntity)
        {
            if (theTileEntity != null)
            {
                Packet var2 = theTileEntity.getDescriptionPacket();

                if (var2 != null)
                {
                    this.sendToAllPlayersWatchingChunk(var2);
                }
            }
        }
    }
}
