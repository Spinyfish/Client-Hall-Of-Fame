package net.minecraft.client.multiplayer;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFirework;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.CustomGuis;
import net.optifine.DynamicLights;
import net.optifine.override.PlayerControllerOF;
import net.optifine.reflect.Reflector;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

public class WorldClient extends World {
    /**
     * The packets that need to be sent to the server.
     */
    private final NetHandlerPlayClient sendQueue;

    /**
     * The ChunkProviderClient instance
     */
    private ChunkProviderClient clientChunkProvider;
    private final Set<Entity> entityList = Sets.newHashSet();
    private final Set<Entity> entitySpawnQueue = Sets.newHashSet();
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Set<ChunkCoordIntPair> previousActiveChunkSet = Sets.newHashSet();
    private boolean playerUpdate = false;

    public WorldClient(final NetHandlerPlayClient p_i45063_1_, final WorldSettings p_i45063_2_, final int p_i45063_3_, final EnumDifficulty p_i45063_4_, final Profiler p_i45063_5_) {
        super(new SaveHandlerMP(), new WorldInfo(p_i45063_2_, "MpServer"), WorldProvider.getProviderForDimension(p_i45063_3_), p_i45063_5_, true);
        this.sendQueue = p_i45063_1_;
        this.getWorldInfo().setDifficulty(p_i45063_4_);
        this.provider.registerWorld(this);
        this.setSpawnPoint(new BlockPos(8, 64, 8));
        this.chunkProvider = this.createChunkProvider();
        this.mapStorage = new SaveDataMemoryStorage();
        this.calculateInitialSkylight();
        this.calculateInitialWeather();
        Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, this);

        if (this.mc.playerController != null && this.mc.playerController.getClass() == PlayerControllerMP.class) {
            this.mc.playerController = new PlayerControllerOF(this.mc, p_i45063_1_);
            CustomGuis.setPlayerControllerOF((PlayerControllerOF) this.mc.playerController);
        }
    }

    /**
     * Runs a single tick for the world
     */
    public void tick() {
        super.tick();
        this.setTotalWorldTime(this.getTotalWorldTime() + 1L);

        if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
            this.setWorldTime(this.getWorldTime() + 1L);
        }

        this.theProfiler.startSection("reEntryProcessing");

        for (int i = 0; i < 10 && !this.entitySpawnQueue.isEmpty(); ++i) {
            final Entity entity = this.entitySpawnQueue.iterator().next();
            this.entitySpawnQueue.remove(entity);

            if (!this.loadedEntityList.contains(entity)) {
                this.spawnEntityInWorld(entity);
            }
        }

        this.theProfiler.endStartSection("chunkCache");
        this.clientChunkProvider.unloadQueuedChunks();
        this.theProfiler.endStartSection("blocks");
        this.updateBlocks();
        this.theProfiler.endSection();
    }

    /**
     * Invalidates an AABB region of blocks from the receive queue, in the event that the block has been modified
     * client-side in the intervening 80 receive ticks.
     */
    public void invalidateBlockReceiveRegion(final int p_73031_1_, final int p_73031_2_, final int p_73031_3_, final int p_73031_4_, final int p_73031_5_, final int p_73031_6_) {
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected IChunkProvider createChunkProvider() {
        this.clientChunkProvider = new ChunkProviderClient(this);
        return this.clientChunkProvider;
    }

    protected void updateBlocks() {
        super.updateBlocks();
        this.previousActiveChunkSet.retainAll(this.activeChunkSet);

        if (this.previousActiveChunkSet.size() == this.activeChunkSet.size()) {
            this.previousActiveChunkSet.clear();
        }

        int i = 0;

        for (final ChunkCoordIntPair chunkcoordintpair : this.activeChunkSet) {
            if (!this.previousActiveChunkSet.contains(chunkcoordintpair)) {
                final int j = chunkcoordintpair.chunkXPos * 16;
                final int k = chunkcoordintpair.chunkZPos * 16;
                this.theProfiler.startSection("getChunk");
                final Chunk chunk = this.getChunkFromChunkCoords(chunkcoordintpair.chunkXPos, chunkcoordintpair.chunkZPos);
                this.playMoodSoundAndCheckLight(j, k, chunk);
                this.theProfiler.endSection();
                this.previousActiveChunkSet.add(chunkcoordintpair);
                ++i;

                if (i >= 10) {
                    return;
                }
            }
        }
    }

    public void doPreChunk(final int p_73025_1_, final int p_73025_2_, final boolean p_73025_3_) {
        if (p_73025_3_) {
            this.clientChunkProvider.loadChunk(p_73025_1_, p_73025_2_);
        } else {
            this.clientChunkProvider.unloadChunk(p_73025_1_, p_73025_2_);
        }

        if (!p_73025_3_) {
            this.markBlockRangeForRenderUpdate(p_73025_1_ * 16, 0, p_73025_2_ * 16, p_73025_1_ * 16 + 15, 256, p_73025_2_ * 16 + 15);
        }
    }

    /**
     * Called when an entity is spawned in the world. This includes players.
     */
    public boolean spawnEntityInWorld(final Entity entityIn) {
        final boolean flag = super.spawnEntityInWorld(entityIn);
        this.entityList.add(entityIn);

        if (!flag) {
            this.entitySpawnQueue.add(entityIn);
        } else if (entityIn instanceof EntityMinecart) {
            this.mc.getSoundHandler().playSound(new MovingSoundMinecart((EntityMinecart) entityIn));
        }

        return flag;
    }

    /**
     * Schedule the entity for removal during the next tick. Marks the entity dead in anticipation.
     */
    public void removeEntity(final Entity entityIn) {
        super.removeEntity(entityIn);
        this.entityList.remove(entityIn);
    }

    protected void onEntityAdded(final Entity entityIn) {
        super.onEntityAdded(entityIn);

        this.entitySpawnQueue.remove(entityIn);
    }

    protected void onEntityRemoved(final Entity entityIn) {
        super.onEntityRemoved(entityIn);
        boolean flag = false;

        if (this.entityList.contains(entityIn)) {
            if (entityIn.isEntityAlive()) {
                this.entitySpawnQueue.add(entityIn);
                flag = true;
            } else {
                this.entityList.remove(entityIn);
            }
        }
    }

    /**
     * Add an ID to Entity mapping to entityHashSet
     */
    public void addEntityToWorld(final int p_73027_1_, final Entity p_73027_2_) {
        final Entity entity = this.getEntityByID(p_73027_1_);

        if (entity != null) {
            this.removeEntity(entity);
        }

        this.entityList.add(p_73027_2_);
        p_73027_2_.setEntityId(p_73027_1_);

        if (!this.spawnEntityInWorld(p_73027_2_)) {
            this.entitySpawnQueue.add(p_73027_2_);
        }

        this.entitiesById.addKey(p_73027_1_, p_73027_2_);
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    public Entity getEntityByID(final int id) {
        return id == this.mc.thePlayer.getEntityId() ? this.mc.thePlayer : super.getEntityByID(id);
    }

    public Entity removeEntityFromWorld(final int p_73028_1_) {
        final Entity entity = this.entitiesById.removeObject(p_73028_1_);

        if (entity != null) {
            this.entityList.remove(entity);
            this.removeEntity(entity);
        }

        return entity;
    }

    public boolean invalidateRegionAndSetBlock(final BlockPos p_180503_1_, final IBlockState p_180503_2_) {
        final int i = p_180503_1_.getX();
        final int j = p_180503_1_.getY();
        final int k = p_180503_1_.getZ();
        this.invalidateBlockReceiveRegion(i, j, k, i, j, k);
        return super.setBlockState(p_180503_1_, p_180503_2_, 3);
    }

    /**
     * If on MP, sends a quitting packet.
     */
    public void sendQuittingDisconnectingPacket() {
        this.sendQueue.getNetworkManager().closeChannel(new ChatComponentText("Quitting"));
    }

    /**
     * Updates all weather states.
     */
    protected void updateWeather() {
    }

    protected int getRenderDistanceChunks() {
        return this.mc.gameSettings.renderDistanceChunks;
    }

    public void doVoidFogParticles(final int p_73029_1_, final int p_73029_2_, final int p_73029_3_) {
        final int i = 16;
        final Random random = new Random();
        final ItemStack itemstack = this.mc.thePlayer.getHeldItem();
        final boolean flag = this.mc.playerController.getCurrentGameType() == WorldSettings.GameType.CREATIVE && itemstack != null && Block.getBlockFromItem(itemstack.getItem()) == Blocks.barrier;
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int j = 0; j < 1000; ++j) {
            final int k = p_73029_1_ + this.rand.nextInt(i) - this.rand.nextInt(i);
            final int l = p_73029_2_ + this.rand.nextInt(i) - this.rand.nextInt(i);
            final int i1 = p_73029_3_ + this.rand.nextInt(i) - this.rand.nextInt(i);
            blockpos$mutableblockpos.func_181079_c(k, l, i1);
            final IBlockState iblockstate = this.getBlockState(blockpos$mutableblockpos);
            iblockstate.getBlock().randomDisplayTick(this, blockpos$mutableblockpos, iblockstate, random);

            if (flag && iblockstate.getBlock() == Blocks.barrier) {
                this.spawnParticle(EnumParticleTypes.BARRIER, (float) k + 0.5F, (float) l + 0.5F, (float) i1 + 0.5F, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    /**
     * also releases skins.
     */
    public void removeAllEntities() {
        this.loadedEntityList.removeAll(this.unloadedEntityList);

        for (int i = 0; i < this.unloadedEntityList.size(); ++i) {
            final Entity entity = this.unloadedEntityList.get(i);
            final int j = entity.chunkCoordX;
            final int k = entity.chunkCoordZ;

            if (entity.addedToChunk && this.isChunkLoaded(j, k, true)) {
                this.getChunkFromChunkCoords(j, k).removeEntity(entity);
            }
        }

        for (int l = 0; l < this.unloadedEntityList.size(); ++l) {
            this.onEntityRemoved(this.unloadedEntityList.get(l));
        }

        this.unloadedEntityList.clear();

        for (Entity entity1 : this.loadedEntityList) {

            if (entity1.ridingEntity != null) {
                if (!entity1.ridingEntity.isDead && entity1.ridingEntity.riddenByEntity == entity1) {
                    continue;
                }

                entity1.ridingEntity.riddenByEntity = null;
                entity1.ridingEntity = null;
            }

            if (entity1.isDead) {
                final int j1 = entity1.chunkCoordX;
                final int k1 = entity1.chunkCoordZ;

                if (entity1.addedToChunk && this.isChunkLoaded(j1, k1, true)) {
                    this.getChunkFromChunkCoords(j1, k1).removeEntity(entity1);
                }

                this.loadedEntityList.remove(entity1);
                this.onEntityRemoved(entity1);
            }
        }
    }

    /**
     * Adds some basic stats of the world to the given crash report.
     */
    public CrashReportCategory addWorldInfoToCrashReport(final CrashReport report) {
        final CrashReportCategory crashreportcategory = super.addWorldInfoToCrashReport(report);
        crashreportcategory.addCrashSectionCallable("Forced entities", new Callable<String>() {
            public String call() {
                return WorldClient.this.entityList.size() + " total; " + WorldClient.this.entityList;
            }
        });
        crashreportcategory.addCrashSectionCallable("Retry entities", new Callable<String>() {
            public String call() {
                return WorldClient.this.entitySpawnQueue.size() + " total; " + WorldClient.this.entitySpawnQueue;
            }
        });
        crashreportcategory.addCrashSectionCallable("Server brand", new Callable<String>() {
            public String call() throws Exception {
                return WorldClient.this.mc.thePlayer.getClientBrand();
            }
        });
        crashreportcategory.addCrashSectionCallable("Server type", new Callable<String>() {
            public String call() throws Exception {
                return WorldClient.this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
            }
        });
        return crashreportcategory;
    }

    /**
     * Plays a sound at the specified position.
     */
    public void playSoundAtPos(final BlockPos p_175731_1_, final String p_175731_2_, final float p_175731_3_, final float p_175731_4_, final boolean p_175731_5_) {
        this.playSound((double) p_175731_1_.getX() + 0.5D, (double) p_175731_1_.getY() + 0.5D, (double) p_175731_1_.getZ() + 0.5D, p_175731_2_, p_175731_3_, p_175731_4_, p_175731_5_);
    }

    /**
     * par8 is loudness, all pars passed to minecraftInstance.sndManager.playSound
     */
    public void playSound(final double x, final double y, final double z, final String soundName, final float volume, final float pitch, final boolean distanceDelay) {
        final double d0 = this.mc.getRenderViewEntity().getDistanceSq(x, y, z);
        final PositionedSoundRecord positionedsoundrecord = new PositionedSoundRecord(new ResourceLocation(soundName), volume, pitch, (float) x, (float) y, (float) z);

        if (distanceDelay && d0 > 100.0D) {
            final double d1 = Math.sqrt(d0) / 40.0D;
            this.mc.getSoundHandler().playDelayedSound(positionedsoundrecord, (int) (d1 * 20.0D));
        } else {
            this.mc.getSoundHandler().playSound(positionedsoundrecord);
        }
    }

    public void makeFireworks(final double x, final double y, final double z, final double motionX, final double motionY, final double motionZ, final NBTTagCompound compund) {
        this.mc.effectRenderer.addEffect(new EntityFirework.StarterFX(this, x, y, z, motionX, motionY, motionZ, this.mc.effectRenderer, compund));
    }

    public void setWorldScoreboard(final Scoreboard p_96443_1_) {
        this.worldScoreboard = p_96443_1_;
    }

    /**
     * Sets the world time.
     */
    public void setWorldTime(long time) {
        if (time < 0L) {
            time = -time;
            this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
        } else {
            this.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
        }

        super.setWorldTime(time);
    }

    public int getCombinedLight(final BlockPos pos, final int lightValue) {
        int i = super.getCombinedLight(pos, lightValue);

        if (Config.isDynamicLights()) {
            i = DynamicLights.getCombinedLight(pos, i);
        }

        return i;
    }

    /**
     * Sets the block state at a given location. Flag 1 will cause a block update. Flag 2 will send the change to
     * clients (you almost always want this). Flag 4 prevents the block from being re-rendered, if this is a client
     * world. Flags can be added together.
     *
     * @param flags Flag 1 will cause a block update. Flag 2 will send the change to clients (you almost always want
     *              this). Flag 4 prevents the block from being re-rendered, if this is a client world. Flags can be added together.
     */
    public boolean setBlockState(final BlockPos pos, final IBlockState newState, final int flags) {
        this.playerUpdate = this.isPlayerActing();
        final boolean flag = super.setBlockState(pos, newState, flags);
        this.playerUpdate = false;
        return flag;
    }

    private boolean isPlayerActing() {
        if (this.mc.playerController instanceof PlayerControllerOF) {
            final PlayerControllerOF playercontrollerof = (PlayerControllerOF) this.mc.playerController;
            return playercontrollerof.isActing();
        } else {
            return false;
        }
    }

    public boolean isPlayerUpdate() {
        return this.playerUpdate;
    }
}
