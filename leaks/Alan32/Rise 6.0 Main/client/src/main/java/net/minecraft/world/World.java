package net.minecraft.world;

import com.alan.clients.util.RayCastUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class World implements IBlockAccess, InstanceAccess, java.io.Serializable {
    private int field_181546_a = 63;

    /**
     * boolean; if true updates scheduled by scheduleBlockUpdate happen immediately
     */
    protected boolean scheduledUpdatesAreImmediate;
    public final ConcurrentLinkedQueue<Entity> loadedEntityList = new ConcurrentLinkedQueue<>();
    protected final List<Entity> unloadedEntityList = Lists.newArrayList();
    public final List<TileEntity> loadedTileEntityList = Lists.newArrayList();
    public final List<TileEntity> tickableTileEntities = Lists.newArrayList();
    private final List<TileEntity> addedTileEntityList = Lists.newArrayList();
    private final List<TileEntity> tileEntitiesToBeRemoved = Lists.newArrayList();
    public final List<EntityPlayer> playerEntities = Lists.newArrayList();
    public final List<Entity> weatherEffects = Lists.newArrayList();
    protected final IntHashMap<Entity> entitiesById = new IntHashMap();
    private final long cloudColour = 16777215L;

    /**
     * How much light is subtracted from full daylight
     */
    private int skylightSubtracted;

    /**
     * Contains the current Linear Congruential Generator seed for block updates. Used with an A value of 3 and a C
     * value of 0x3c6ef35f, producing a highly planar series of values ill-suited for choosing random blocks in a
     * 16x128x16 field.
     */
    protected int updateLCG = (new Random()).nextInt();

    /**
     * magic number used to generate fast random numbers for 3d distribution within a chunk
     */
    protected final int DIST_HASH_MAGIC = 1013904223;
    protected float prevRainingStrength;
    protected float rainingStrength;
    protected float prevThunderingStrength;
    protected float thunderingStrength;

    /**
     * Set to 2 whenever a lightning bolt is generated in SSP. Decrements if > 0 in updateWeather(). Value appears to be
     * unused.
     */
    private int lastLightningBolt;

    /**
     * RNG for World.
     */
    public final Random rand = new Random();

    /**
     * The WorldProvider instance that World uses.
     */
    public final WorldProvider provider;
    protected List<IWorldAccess> worldAccesses = Lists.newArrayList();

    /**
     * Handles chunk operations and caching
     */
    protected IChunkProvider chunkProvider;
    protected final ISaveHandler saveHandler;

    /**
     * holds information about a world (size on disk, time, spawn point, seed, ...)
     */
    protected WorldInfo worldInfo;

    /**
     * if set, this flag forces a request to load a chunk to load the chunk rather than defaulting to the world's
     * chunkprovider's dummy if possible
     */
    protected boolean findingSpawnPoint;
    protected MapStorage mapStorage;
    protected VillageCollection villageCollectionObj;
    public final Profiler theProfiler;
    private final Calendar theCalendar = Calendar.getInstance();
    protected Scoreboard worldScoreboard = new Scoreboard();

    /**
     * True if the world is a 'slave' client; changes will not be saved or propagated from this world. For example,
     * server worlds have this set to false, client worlds have this set to true.
     */
    public final boolean isRemote;
    protected Set<ChunkCoordIntPair> activeChunkSet = Sets.newHashSet();

    /**
     * number of ticks until the next random ambients play
     */
    private int ambientTickCountdown;

    /**
     * indicates if enemies are spawned or not
     */
    protected boolean spawnHostileMobs;

    /**
     * A flag indicating whether we should spawn peaceful mobs.
     */
    protected boolean spawnPeacefulMobs;
    private boolean processingLoadedTiles;
    private final WorldBorder worldBorder;

    /**
     * is a temporary list of blocks and light values used when updating light levels. Holds up to 32x32x32 blocks (the
     * maximum influence of a light source.) Every element is a packed bit value: 0000000000LLLLzzzzzzyyyyyyxxxxxx. The
     * 4-bit L is a light level used when darkening blocks. 6-bit numbers x, y and z represent the block's offset from
     * the original block, plus 32 (i.e. value of 31 would mean a -1 offset
     */
    int[] lightUpdateBlockList;

    protected World(final ISaveHandler saveHandlerIn, final WorldInfo info, final WorldProvider providerIn, final Profiler profilerIn, final boolean client) {
        this.ambientTickCountdown = this.rand.nextInt(12000);
        this.spawnHostileMobs = true;
        this.spawnPeacefulMobs = true;
        this.lightUpdateBlockList = new int[32768];
        this.saveHandler = saveHandlerIn;
        this.theProfiler = profilerIn;
        this.worldInfo = info;
        this.provider = providerIn;
        this.isRemote = client;
        this.worldBorder = providerIn.getWorldBorder();
    }

    public World init() {
        return this;
    }

    public BiomeGenBase getBiomeGenForCoords(final BlockPos pos) {
        if (this.isBlockLoaded(pos)) {
            final Chunk chunk = this.getChunkFromBlockCoords(pos);

            try {
                return chunk.getBiome(pos, this.provider.getWorldChunkManager());
            } catch (final Throwable throwable) {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting biome");
                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Coordinates of biome request");
                crashreportcategory.addCrashSectionCallable("Location", new Callable<String>() {
                    public String call() throws Exception {
                        return CrashReportCategory.getCoordinateInfo(pos);
                    }
                });
                throw new ReportedException(crashreport);
            }
        } else {
            return this.provider.getWorldChunkManager().getBiomeGenerator(pos, BiomeGenBase.plains);
        }
    }

    public WorldChunkManager getWorldChunkManager() {
        return this.provider.getWorldChunkManager();
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected abstract IChunkProvider createChunkProvider();

    public void initialize(final WorldSettings settings) {
        this.worldInfo.setServerInitialized(true);
    }

    /**
     * Sets a new spawn location by finding an uncovered block at a random (x,z) location in the chunk.
     */
    public void setInitialSpawnLocation() {
        this.setSpawnPoint(new BlockPos(8, 64, 8));
    }

    public Block getGroundAboveSeaLevel(final BlockPos pos) {
        BlockPos blockpos;

        for (blockpos = new BlockPos(pos.getX(), this.func_181545_F(), pos.getZ()); !this.isAirBlock(blockpos.up()); blockpos = blockpos.up()) {
        }

        return this.getBlockState(blockpos).getBlock();
    }

    /**
     * Check if the given BlockPos has valid coordinates
     */
    private boolean isValid(final BlockPos pos) {
        return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000 && pos.getY() >= 0 && pos.getY() < 256;
    }

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
     * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
     *
     * @param pos The position of the block being checked.
     */
    public boolean isAirBlock(final BlockPos pos) {
        return this.getBlockState(pos).getBlock().getMaterial() == Material.air;
    }

    public boolean isBlockLoaded(final BlockPos pos) {
        return this.isBlockLoaded(pos, true);
    }

    public boolean isBlockLoaded(final BlockPos pos, final boolean allowEmpty) {
        return this.isValid(pos) && this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, allowEmpty);
    }

    public boolean isAreaLoaded(final BlockPos center, final int radius) {
        return this.isAreaLoaded(center, radius, true);
    }

    public boolean isAreaLoaded(final BlockPos center, final int radius, final boolean allowEmpty) {
        return this.isAreaLoaded(center.getX() - radius, center.getY() - radius, center.getZ() - radius, center.getX() + radius, center.getY() + radius, center.getZ() + radius, allowEmpty);
    }

    public boolean isAreaLoaded(final BlockPos from, final BlockPos to) {
        return this.isAreaLoaded(from, to, true);
    }

    public boolean isAreaLoaded(final BlockPos from, final BlockPos to, final boolean allowEmpty) {
        return this.isAreaLoaded(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(), allowEmpty);
    }

    public boolean isAreaLoaded(final StructureBoundingBox box) {
        return this.isAreaLoaded(box, true);
    }

    public boolean isAreaLoaded(final StructureBoundingBox box, final boolean allowEmpty) {
        return this.isAreaLoaded(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, allowEmpty);
    }

    private boolean isAreaLoaded(int xStart, final int yStart, int zStart, int xEnd, final int yEnd, int zEnd, final boolean allowEmpty) {
        if (yEnd >= 0 && yStart < 256) {
            xStart = xStart >> 4;
            zStart = zStart >> 4;
            xEnd = xEnd >> 4;
            zEnd = zEnd >> 4;

            for (int i = xStart; i <= xEnd; ++i) {
                for (int j = zStart; j <= zEnd; ++j) {
                    if (!this.isChunkLoaded(i, j, allowEmpty)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean isChunkLoaded(final int x, final int z, final boolean allowEmpty) {
        return this.chunkProvider.chunkExists(x, z) && (allowEmpty || !this.chunkProvider.provideChunk(x, z).isEmpty());
    }

    public Chunk getChunkFromBlockCoords(final BlockPos pos) {
        return this.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4);
    }

    /**
     * Returns back a chunk looked up by chunk coordinates Args: x, y
     *
     * @param chunkX Chunk X Coordinate
     * @param chunkZ Chunk Z Coordinate
     */
    public Chunk getChunkFromChunkCoords(final int chunkX, final int chunkZ) {
        return this.chunkProvider.provideChunk(chunkX, chunkZ);
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
        if (!this.isValid(pos)) {
            return false;
        } else if (!this.isRemote && this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
            return false;
        } else {
            final Chunk chunk = this.getChunkFromBlockCoords(pos);
            final Block block = newState.getBlock();
            final IBlockState iblockstate = chunk.setBlockState(pos, newState);

            if (iblockstate == null) {
                return false;
            } else {
                final Block block1 = iblockstate.getBlock();

                if (block.getLightOpacity() != block1.getLightOpacity() || block.getLightValue() != block1.getLightValue()) {
                    this.theProfiler.startSection("checkLight");
                    this.checkLight(pos);
                    this.theProfiler.endSection();
                }

                if ((flags & 2) != 0 && (!this.isRemote || (flags & 4) == 0) && chunk.isPopulated()) {
                    this.markBlockForUpdate(pos);
                }

                if (!this.isRemote && (flags & 1) != 0) {
                    this.notifyNeighborsRespectDebug(pos, iblockstate.getBlock());

                    if (block.hasComparatorInputOverride()) {
                        this.updateComparatorOutputLevel(pos, block);
                    }
                }

                return true;
            }
        }
    }

    public boolean setBlockToAir(final BlockPos pos) {
        return this.setBlockState(pos, Blocks.air.getDefaultState(), 3);
    }

    /**
     * Sets a block to air, but also plays the sound and particles and can spawn drops
     */
    public boolean destroyBlock(final BlockPos pos, final boolean dropBlock) {
        final IBlockState iblockstate = this.getBlockState(pos);
        final Block block = iblockstate.getBlock();

        if (block.getMaterial() == Material.air) {
            return false;
        } else {
            this.playAuxSFX(2001, pos, Block.getStateId(iblockstate));

            if (dropBlock) {
                block.dropBlockAsItem(this, pos, iblockstate, 0);
            }

            return this.setBlockState(pos, Blocks.air.getDefaultState(), 3);
        }
    }

    /**
     * Convenience method to update the block on both the client and server
     */
    public boolean setBlockState(final BlockPos pos, final IBlockState state) {
        return this.setBlockState(pos, state, 3);
    }

    public void markBlockForUpdate(final BlockPos pos) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            this.worldAccesses.get(i).markBlockForUpdate(pos);
        }
    }

    public void notifyNeighborsRespectDebug(final BlockPos pos, final Block blockType) {
        if (this.worldInfo.getTerrainType() != WorldType.DEBUG_WORLD) {
            this.notifyNeighborsOfStateChange(pos, blockType);
        }
    }

    /**
     * marks a vertical line of blocks as dirty
     */
    public void markBlocksDirtyVertical(final int x1, final int z1, int x2, int z2) {
        if (x2 > z2) {
            final int i = z2;
            z2 = x2;
            x2 = i;
        }

        if (!this.provider.getHasNoSky()) {
            for (int j = x2; j <= z2; ++j) {
                this.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x1, j, z1));
            }
        }

        this.markBlockRangeForRenderUpdate(x1, x2, z1, x1, z2, z1);
    }

    public void markBlockRangeForRenderUpdate(final BlockPos rangeMin, final BlockPos rangeMax) {
        this.markBlockRangeForRenderUpdate(rangeMin.getX(), rangeMin.getY(), rangeMin.getZ(), rangeMax.getX(), rangeMax.getY(), rangeMax.getZ());
    }

    public void markBlockRangeForRenderUpdate(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            this.worldAccesses.get(i).markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
        }
    }

    public void notifyNeighborsOfStateChange(final BlockPos pos, final Block blockType) {
        this.notifyBlockOfStateChange(pos.west(), blockType);
        this.notifyBlockOfStateChange(pos.east(), blockType);
        this.notifyBlockOfStateChange(pos.down(), blockType);
        this.notifyBlockOfStateChange(pos.up(), blockType);
        this.notifyBlockOfStateChange(pos.north(), blockType);
        this.notifyBlockOfStateChange(pos.south(), blockType);
    }

    public void notifyNeighborsOfStateExcept(final BlockPos pos, final Block blockType, final EnumFacing skipSide) {
        if (skipSide != EnumFacing.WEST) {
            this.notifyBlockOfStateChange(pos.west(), blockType);
        }

        if (skipSide != EnumFacing.EAST) {
            this.notifyBlockOfStateChange(pos.east(), blockType);
        }

        if (skipSide != EnumFacing.DOWN) {
            this.notifyBlockOfStateChange(pos.down(), blockType);
        }

        if (skipSide != EnumFacing.UP) {
            this.notifyBlockOfStateChange(pos.up(), blockType);
        }

        if (skipSide != EnumFacing.NORTH) {
            this.notifyBlockOfStateChange(pos.north(), blockType);
        }

        if (skipSide != EnumFacing.SOUTH) {
            this.notifyBlockOfStateChange(pos.south(), blockType);
        }
    }

    public void notifyBlockOfStateChange(final BlockPos pos, final Block blockIn) {
        if (!this.isRemote) {
            final IBlockState iblockstate = this.getBlockState(pos);

            try {
                iblockstate.getBlock().onNeighborBlockChange(this, pos, iblockstate, blockIn);
            } catch (final Throwable throwable) {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
                crashreportcategory.addCrashSectionCallable("Source block type", new Callable<String>() {
                    public String call() throws Exception {
                        try {
                            return String.format("ID #%d (%s // %s)", Integer.valueOf(Block.getIdFromBlock(blockIn)), blockIn.getUnlocalizedName(), blockIn.getClass().getCanonicalName());
                        } catch (final Throwable var2) {
                            return "ID #" + Block.getIdFromBlock(blockIn);
                        }
                    }
                });
                CrashReportCategory.addBlockInfo(crashreportcategory, pos, iblockstate);
                throw new ReportedException(crashreport);
            }
        }
    }

    public boolean isBlockTickPending(final BlockPos pos, final Block blockType) {
        return false;
    }

    public boolean canSeeSky(final BlockPos pos) {
        return this.getChunkFromBlockCoords(pos).canSeeSky(pos);
    }

    public boolean canBlockSeeSky(final BlockPos pos) {
        if (pos.getY() >= this.func_181545_F()) {
            return this.canSeeSky(pos);
        } else {
            BlockPos blockpos = new BlockPos(pos.getX(), this.func_181545_F(), pos.getZ());

            if (!this.canSeeSky(blockpos)) {
                return false;
            } else {
                for (blockpos = blockpos.down(); blockpos.getY() > pos.getY(); blockpos = blockpos.down()) {
                    final Block block = this.getBlockState(blockpos).getBlock();

                    if (block.getLightOpacity() > 0 && !block.getMaterial().isLiquid()) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public int getLight(BlockPos pos) {
        if (pos.getY() < 0) {
            return 0;
        } else {
            if (pos.getY() >= 256) {
                pos = new BlockPos(pos.getX(), 255, pos.getZ());
            }

            return this.getChunkFromBlockCoords(pos).getLightSubtracted(pos, 0);
        }
    }

    public int getLightFromNeighbors(final BlockPos pos) {
        return this.getLight(pos, true);
    }

    public int getLight(BlockPos pos, final boolean checkNeighbors) {
        if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000) {
            if (checkNeighbors && this.getBlockState(pos).getBlock().getUseNeighborBrightness()) {
                int i1 = this.getLight(pos.up(), false);
                final int i = this.getLight(pos.east(), false);
                final int j = this.getLight(pos.west(), false);
                final int k = this.getLight(pos.south(), false);
                final int l = this.getLight(pos.north(), false);

                if (i > i1) {
                    i1 = i;
                }

                if (j > i1) {
                    i1 = j;
                }

                if (k > i1) {
                    i1 = k;
                }

                if (l > i1) {
                    i1 = l;
                }

                return i1;
            } else if (pos.getY() < 0) {
                return 0;
            } else {
                if (pos.getY() >= 256) {
                    pos = new BlockPos(pos.getX(), 255, pos.getZ());
                }

                final Chunk chunk = this.getChunkFromBlockCoords(pos);
                return chunk.getLightSubtracted(pos, this.skylightSubtracted);
            }
        } else {
            return 15;
        }
    }

    /**
     * Returns the position at this x, z coordinate in the chunk with y set to the value from the height map.
     */
    public BlockPos getHeight(final BlockPos pos) {
        final int i;

        if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000) {
            if (this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, true)) {
                i = this.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4).getHeightValue(pos.getX() & 15, pos.getZ() & 15);
            } else {
                i = 0;
            }
        } else {
            i = this.func_181545_F() + 1;
        }

        return new BlockPos(pos.getX(), i, pos.getZ());
    }

    /**
     * Gets the lowest height of the chunk where sunlight directly reaches
     */
    public int getChunksLowestHorizon(final int x, final int z) {
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            if (!this.isChunkLoaded(x >> 4, z >> 4, true)) {
                return 0;
            } else {
                final Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
                return chunk.getLowestHeight();
            }
        } else {
            return this.func_181545_F() + 1;
        }
    }

    public int getLightFromNeighborsFor(final EnumSkyBlock type, BlockPos pos) {
        if (this.provider.getHasNoSky() && type == EnumSkyBlock.SKY) {
            return 0;
        } else {
            if (pos.getY() < 0) {
                pos = new BlockPos(pos.getX(), 0, pos.getZ());
            }

            if (!this.isValid(pos)) {
                return type.defaultLightValue;
            } else if (!this.isBlockLoaded(pos)) {
                return type.defaultLightValue;
            } else if (this.getBlockState(pos).getBlock().getUseNeighborBrightness()) {
                int i1 = this.getLightFor(type, pos.up());
                final int i = this.getLightFor(type, pos.east());
                final int j = this.getLightFor(type, pos.west());
                final int k = this.getLightFor(type, pos.south());
                final int l = this.getLightFor(type, pos.north());

                if (i > i1) {
                    i1 = i;
                }

                if (j > i1) {
                    i1 = j;
                }

                if (k > i1) {
                    i1 = k;
                }

                if (l > i1) {
                    i1 = l;
                }

                return i1;
            } else {
                final Chunk chunk = this.getChunkFromBlockCoords(pos);
                return chunk.getLightFor(type, pos);
            }
        }
    }

    public int getLightFor(final EnumSkyBlock type, BlockPos pos) {
        if (pos.getY() < 0) {
            pos = new BlockPos(pos.getX(), 0, pos.getZ());
        }

        if (!this.isValid(pos)) {
            return type.defaultLightValue;
        } else if (!this.isBlockLoaded(pos)) {
            return type.defaultLightValue;
        } else {
            final Chunk chunk = this.getChunkFromBlockCoords(pos);
            return chunk.getLightFor(type, pos);
        }
    }

    public void setLightFor(final EnumSkyBlock type, final BlockPos pos, final int lightValue) {
        if (this.isValid(pos)) {
            if (this.isBlockLoaded(pos)) {
                final Chunk chunk = this.getChunkFromBlockCoords(pos);
                chunk.setLightFor(type, pos, lightValue);
                this.notifyLightSet(pos);
            }
        }
    }

    public void notifyLightSet(final BlockPos pos) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            this.worldAccesses.get(i).notifyLightSet(pos);
        }
    }

    public int getCombinedLight(final BlockPos pos, final int lightValue) {
        final int i = this.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos);
        int j = this.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos);

        if (j < lightValue) {
            j = lightValue;
        }

        return i << 20 | j << 4;
    }

    public float getLightBrightness(final BlockPos pos) {
        return this.provider.getLightBrightnessTable()[this.getLightFromNeighbors(pos)];
    }

    public IBlockState getBlockState(final BlockPos pos) {
        if (!this.isValid(pos)) {
            return Blocks.air.getDefaultState();
        } else {
            final Chunk chunk = this.getChunkFromBlockCoords(pos);
            return chunk.getBlockState(pos);
        }
    }

    /**
     * Checks whether its daytime by seeing if the light subtracted from the skylight is less than 4
     */
    public boolean isDaytime() {
        return this.skylightSubtracted < 4;
    }

    /**
     * ray traces all blocks, including non-collideable ones
     */
    public MovingObjectPosition rayTraceBlocks(final Vec3 p_72933_1_, final Vec3 p_72933_2_) {
        return this.rayTraceBlocks(p_72933_1_, p_72933_2_, false, false, false);
    }

    public MovingObjectPosition rayTraceBlocks(final Vec3 start, final Vec3 end, final boolean stopOnLiquid) {
        return this.rayTraceBlocks(start, end, stopOnLiquid, false, false);
    }

    /**
     * Performs a raycast against all blocks in the world. Args : Vec1, Vec2, stopOnLiquid,
     * ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock
     */
    public MovingObjectPosition rayTraceBlocks(Vec3 vec31, final Vec3 vec32, final boolean stopOnLiquid, final boolean ignoreBlockWithoutBoundingBox, final boolean returnLastUncollidableBlock) {
        if (!Double.isNaN(vec31.xCoord) && !Double.isNaN(vec31.yCoord) && !Double.isNaN(vec31.zCoord)) {
            if (!Double.isNaN(vec32.xCoord) && !Double.isNaN(vec32.yCoord) && !Double.isNaN(vec32.zCoord)) {
                final int i = MathHelper.floor_double(vec32.xCoord);
                final int j = MathHelper.floor_double(vec32.yCoord);
                final int k = MathHelper.floor_double(vec32.zCoord);
                int l = MathHelper.floor_double(vec31.xCoord);
                int i1 = MathHelper.floor_double(vec31.yCoord);
                int j1 = MathHelper.floor_double(vec31.zCoord);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                final IBlockState iblockstate = this.getBlockState(blockpos);
                final Block block = iblockstate.getBlock();

                if ((!ignoreBlockWithoutBoundingBox || block.getCollisionBoundingBox(this, blockpos, iblockstate) != null) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
                    final MovingObjectPosition movingobjectposition = block.collisionRayTrace(this, blockpos, vec31, vec32);

                    if (movingobjectposition != null) {
                        return movingobjectposition;
                    }
                }

                MovingObjectPosition movingobjectposition2 = null;
                int k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord)) {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k) {
                        return returnLastUncollidableBlock ? movingobjectposition2 : null;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l) {
                        d0 = (double) l + 1.0D;
                    } else if (i < l) {
                        d0 = (double) l + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (j > i1) {
                        d1 = (double) i1 + 1.0D;
                    } else if (j < i1) {
                        d1 = (double) i1 + 0.0D;
                    } else {
                        flag = false;
                    }

                    if (k > j1) {
                        d2 = (double) j1 + 1.0D;
                    } else if (k < j1) {
                        d2 = (double) j1 + 0.0D;
                    } else {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    final double d6 = vec32.xCoord - vec31.xCoord;
                    final double d7 = vec32.yCoord - vec31.yCoord;
                    final double d8 = vec32.zCoord - vec31.zCoord;

                    if (flag2) {
                        d3 = (d0 - vec31.xCoord) / d6;
                    }

                    if (flag) {
                        d4 = (d1 - vec31.yCoord) / d7;
                    }

                    if (flag1) {
                        d5 = (d2 - vec31.zCoord) / d8;
                    }

                    if (d3 == -0.0D) {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D) {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D) {
                        d5 = -1.0E-4D;
                    }

                    final EnumFacing enumfacing;

                    if (d3 < d4 && d3 < d5) {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3(d0, vec31.yCoord + d7 * d3, vec31.zCoord + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3(vec31.xCoord + d6 * d4, d1, vec31.zCoord + d8 * d4);
                    } else {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3(vec31.xCoord + d6 * d5, vec31.yCoord + d7 * d5, d2);
                    }

                    l = MathHelper.floor_double(vec31.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor_double(vec31.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor_double(vec31.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    final IBlockState iblockstate1 = this.getBlockState(blockpos);
                    final Block block1 = iblockstate1.getBlock();

                    if (!ignoreBlockWithoutBoundingBox || block1.getCollisionBoundingBox(this, blockpos, iblockstate1) != null) {
                        if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
                            final MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(this, blockpos, vec31, vec32);

                            if (movingobjectposition1 != null) {
                                return movingobjectposition1;
                            }
                        } else {
                            movingobjectposition2 = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec31, enumfacing, blockpos);
                        }
                    }
                }

                return returnLastUncollidableBlock ? movingobjectposition2 : null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Plays a sound at the entity's position. Args: entity, sound, volume (relative to 1.0), and frequency (or pitch,
     * also relative to 1.0).
     */
    public void playSoundAtEntity(final Entity entityIn, final String name, final float volume, final float pitch) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            this.worldAccesses.get(i).playSound(name, entityIn.posX, entityIn.posY, entityIn.posZ, volume, pitch);
        }
    }

    /**
     * Plays sound to all near players except the player reference given
     */
    public void playSoundToNearExcept(final EntityPlayer player, final String name, final float volume, final float pitch) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            this.worldAccesses.get(i).playSoundToNearExcept(player, name, player.posX, player.posY, player.posZ, volume, pitch);
        }
    }

    /**
     * Play a sound effect. Many many parameters for this function. Not sure what they do, but a classic call is :
     * (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 'random.door_open', 1.0F, world.rand.nextFloat() * 0.1F +
     * 0.9F with i,j,k position of the block.
     */
    public void playSoundEffect(final double x, final double y, final double z, final String soundName, final float volume, final float pitch) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            this.worldAccesses.get(i).playSound(soundName, x, y, z, volume, pitch);
        }
    }

    /**
     * par8 is loudness, all pars passed to minecraftInstance.sndManager.playSound
     */
    public void playSound(final double x, final double y, final double z, final String soundName, final float volume, final float pitch, final boolean distanceDelay) {
    }

    public void playRecord(final BlockPos pos, final String name) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            this.worldAccesses.get(i).playRecord(name, pos);
        }
    }

    public void spawnParticle(final EnumParticleTypes particleType, final double xCoord, final double yCoord, final double zCoord, final double xOffset, final double yOffset, final double zOffset, final int... p_175688_14_) {
        this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, p_175688_14_);
    }

    public void spawnParticle(final EnumParticleTypes particleType, final boolean p_175682_2_, final double xCoord, final double yCoord, final double zCoord, final double xOffset, final double yOffset, final double zOffset, final int... p_175682_15_) {
        this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange() | p_175682_2_, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, p_175682_15_);
    }

    private void spawnParticle(final int particleID, final boolean p_175720_2_, final double xCood, final double yCoord, final double zCoord, final double xOffset, final double yOffset, final double zOffset, final int... p_175720_15_) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            this.worldAccesses.get(i).spawnParticle(particleID, p_175720_2_, xCood, yCoord, zCoord, xOffset, yOffset, zOffset, p_175720_15_);
        }
    }

    /**
     * adds a lightning bolt to the list of lightning bolts in this world.
     */
    public boolean addWeatherEffect(final Entity entityIn) {
        this.weatherEffects.add(entityIn);
        return true;
    }

    /**
     * Called when an entity is spawned in the world. This includes players.
     */
    public boolean spawnEntityInWorld(final Entity entityIn) {
        final int i = MathHelper.floor_double(entityIn.posX / 16.0D);
        final int j = MathHelper.floor_double(entityIn.posZ / 16.0D);
        boolean flag = entityIn.forceSpawn;

        if (entityIn instanceof EntityPlayer) {
            flag = true;
        }

        if (!flag && !this.isChunkLoaded(i, j, true)) {
            return false;
        } else {
            if (entityIn instanceof EntityPlayer) {
                final EntityPlayer entityplayer = (EntityPlayer) entityIn;
                this.playerEntities.add(entityplayer);
                this.updateAllPlayersSleepingFlag();
            }

            this.getChunkFromChunkCoords(i, j).addEntity(entityIn);
            this.loadedEntityList.add(entityIn);
            this.onEntityAdded(entityIn);
            return true;
        }
    }

    protected void onEntityAdded(final Entity entityIn) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            this.worldAccesses.get(i).onEntityAdded(entityIn);
        }
    }

    protected void onEntityRemoved(final Entity entityIn) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            this.worldAccesses.get(i).onEntityRemoved(entityIn);
        }
    }

    /**
     * Schedule the entity for removal during the next tick. Marks the entity dead in anticipation.
     */
    public void removeEntity(final Entity entityIn) {
        if (entityIn.riddenByEntity != null) {
            entityIn.riddenByEntity.mountEntity(null);
        }

        if (entityIn.ridingEntity != null) {
            entityIn.mountEntity(null);
        }

        entityIn.setDead();

        if (entityIn instanceof EntityPlayer) {
            this.playerEntities.remove(entityIn);
            this.updateAllPlayersSleepingFlag();
            this.onEntityRemoved(entityIn);
        }
    }

    /**
     * Do NOT use this method to remove normal entities- use normal removeEntity
     */
    public void removePlayerEntityDangerously(final Entity entityIn) {
        entityIn.setDead();

        if (entityIn instanceof EntityPlayer) {
            this.playerEntities.remove(entityIn);
            this.updateAllPlayersSleepingFlag();
        }

        final int i = entityIn.chunkCoordX;
        final int j = entityIn.chunkCoordZ;

        if (entityIn.addedToChunk && this.isChunkLoaded(i, j, true)) {
            this.getChunkFromChunkCoords(i, j).removeEntity(entityIn);
        }

        this.loadedEntityList.remove(entityIn);
        this.onEntityRemoved(entityIn);
    }

    /**
     * Adds a IWorldAccess to the list of worldAccesses
     */
    public void addWorldAccess(final IWorldAccess worldAccess) {
        this.worldAccesses.add(worldAccess);
    }

    /**
     * Removes a worldAccess from the worldAccesses object
     */
    public void removeWorldAccess(final IWorldAccess worldAccess) {
        this.worldAccesses.remove(worldAccess);
    }

    public List<AxisAlignedBB> getCollidingBoundingBoxes(final Entity entityIn, final AxisAlignedBB bb) {
        final List<AxisAlignedBB> list = Lists.newArrayList();
        final int i = MathHelper.floor_double(bb.minX);
        final int j = MathHelper.floor_double(bb.maxX + 1.0D);
        final int k = MathHelper.floor_double(bb.minY);
        final int l = MathHelper.floor_double(bb.maxY + 1.0D);
        final int i1 = MathHelper.floor_double(bb.minZ);
        final int j1 = MathHelper.floor_double(bb.maxZ + 1.0D);
        final WorldBorder worldborder = this.getWorldBorder();
        final boolean flag = entityIn.isOutsideBorder();
        final boolean flag1 = this.isInsideBorder(worldborder, entityIn);
        final IBlockState iblockstate = Blocks.stone.getDefaultState();
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = i1; l1 < j1; ++l1) {
                if (this.isBlockLoaded(blockpos$mutableblockpos.func_181079_c(k1, 64, l1))) {
                    for (int i2 = k - 1; i2 < l; ++i2) {
                        blockpos$mutableblockpos.func_181079_c(k1, i2, l1);

                        if (flag && flag1) {
                            entityIn.setOutsideBorder(false);
                        } else if (!flag && !flag1) {
                            entityIn.setOutsideBorder(true);
                        }

                        IBlockState iblockstate1 = iblockstate;

                        if (worldborder.contains(blockpos$mutableblockpos) || !flag1) {
                            iblockstate1 = this.getBlockState(blockpos$mutableblockpos);
                        }

                        iblockstate1.getBlock().addCollisionBoxesToList(this, blockpos$mutableblockpos, iblockstate1, bb, list, entityIn);
                    }
                }
            }
        }

        final double d0 = 0.25D;

        if (entityIn == mc.thePlayer) {
            final List<Entity> list1 = this.getEntitiesWithinAABBExcludingEntity(entityIn, bb.expand(d0, d0, d0));

            for (int j2 = 0; j2 < list1.size(); ++j2) {
                if (entityIn.riddenByEntity != list1 && entityIn.ridingEntity != list1) {
                    AxisAlignedBB axisalignedbb = list1.get(j2).getCollisionBoundingBox();

                    if (axisalignedbb != null && axisalignedbb.intersectsWith(bb)) {
                        list.add(axisalignedbb);
                    }

                    axisalignedbb = entityIn.getCollisionBox(list1.get(j2));

                    if (axisalignedbb != null && axisalignedbb.intersectsWith(bb)) {
                        list.add(axisalignedbb);
                    }
                }
            }
        }

        return list;
    }

    public boolean isInsideBorder(final WorldBorder worldBorderIn, final Entity entityIn) {
        double d0 = worldBorderIn.minX();
        double d1 = worldBorderIn.minZ();
        double d2 = worldBorderIn.maxX();
        double d3 = worldBorderIn.maxZ();

        if (entityIn.isOutsideBorder()) {
            ++d0;
            ++d1;
            --d2;
            --d3;
        } else {
            --d0;
            --d1;
            ++d2;
            ++d3;
        }

        return entityIn.posX > d0 && entityIn.posX < d2 && entityIn.posZ > d1 && entityIn.posZ < d3;
    }

    public List<AxisAlignedBB> func_147461_a(final AxisAlignedBB bb) {
        final List<AxisAlignedBB> list = Lists.newArrayList();
        final int i = MathHelper.floor_double(bb.minX);
        final int j = MathHelper.floor_double(bb.maxX + 1.0D);
        final int k = MathHelper.floor_double(bb.minY);
        final int l = MathHelper.floor_double(bb.maxY + 1.0D);
        final int i1 = MathHelper.floor_double(bb.minZ);
        final int j1 = MathHelper.floor_double(bb.maxZ + 1.0D);
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = i1; l1 < j1; ++l1) {
                if (this.isBlockLoaded(blockpos$mutableblockpos.func_181079_c(k1, 64, l1))) {
                    for (int i2 = k - 1; i2 < l; ++i2) {
                        blockpos$mutableblockpos.func_181079_c(k1, i2, l1);
                        final IBlockState iblockstate;

                        if (k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000) {
                            iblockstate = this.getBlockState(blockpos$mutableblockpos);
                        } else {
                            iblockstate = Blocks.bedrock.getDefaultState();
                        }

                        iblockstate.getBlock().addCollisionBoxesToList(this, blockpos$mutableblockpos, iblockstate, bb, list, null);
                    }
                }
            }
        }

        return list;
    }

    /**
     * Returns the amount of skylight subtracted for the current time
     */
    public int calculateSkylightSubtracted(final float p_72967_1_) {
        final float f = this.getCelestialAngle(p_72967_1_);
        float f1 = 1.0F - (MathHelper.cos(f * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        f1 = 1.0F - f1;
        f1 = (float) ((double) f1 * (1.0D - (double) (this.getRainStrength(p_72967_1_) * 5.0F) / 16.0D));
        f1 = (float) ((double) f1 * (1.0D - (double) (this.getThunderStrength(p_72967_1_) * 5.0F) / 16.0D));
        f1 = 1.0F - f1;
        return (int) (f1 * 11.0F);
    }

    /**
     * Returns the sun brightness - checks time of day, rain and thunder
     */
    public float getSunBrightness(final float p_72971_1_) {
        final float f = this.getCelestialAngle(p_72971_1_);
        float f1 = 1.0F - (MathHelper.cos(f * (float) Math.PI * 2.0F) * 2.0F + 0.2F);
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        f1 = 1.0F - f1;
        f1 = (float) ((double) f1 * (1.0D - (double) (this.getRainStrength(p_72971_1_) * 5.0F) / 16.0D));
        f1 = (float) ((double) f1 * (1.0D - (double) (this.getThunderStrength(p_72971_1_) * 5.0F) / 16.0D));
        return f1 * 0.8F + 0.2F;
    }

    /**
     * Calculates the color for the skybox
     */
    public Vec3 getSkyColor(final Entity entityIn, final float partialTicks) {
        final float f = this.getCelestialAngle(partialTicks);
        float f1 = MathHelper.cos(f * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        final int i = MathHelper.floor_double(entityIn.posX);
        final int j = MathHelper.floor_double(entityIn.posY + entityIn.getEyeHeight());
        final int k = MathHelper.floor_double(entityIn.posZ);
        final BlockPos blockpos = new BlockPos(i, j, k);
        final BiomeGenBase biomegenbase = this.getBiomeGenForCoords(blockpos);
        final float f2 = biomegenbase.getFloatTemperature(blockpos);
        final int l = biomegenbase.getSkyColorByTemp(f2);
        float red = (float) (l >> 16 & 255) / 255.0F;
        float green = (float) (l >> 8 & 255) / 255.0F;
        float blue = (float) (l & 255) / 255.0F;
        red = red * f1;
        green = green * f1;
        blue = blue * f1;
        final float rainStrength = this.getRainStrength(partialTicks);

        if (rainStrength > 0.0F) {
            final float f7 = (red * 0.3F + green * 0.59F + blue * 0.11F) * 0.6F;
            final float f8 = 1.0F - rainStrength * 0.75F;
            red = red * f8 + f7 * (1.0F - f8);
            green = green * f8 + f7 * (1.0F - f8);
            blue = blue * f8 + f7 * (1.0F - f8);
        }

        final float thunderStrength = this.getThunderStrength(partialTicks);

        if (thunderStrength > 0.0F) {
            final float f11 = (red * 0.3F + green * 0.59F + blue * 0.11F) * 0.2F;
            final float f9 = 1.0F - thunderStrength * 0.75F;
            red = red * f9 + f11 * (1.0F - f9);
            green = green * f9 + f11 * (1.0F - f9);
            blue = blue * f9 + f11 * (1.0F - f9);
        }

        if (this.lastLightningBolt > 0) {
            float f12 = (float) this.lastLightningBolt - partialTicks;

            if (f12 > 1.0F) {
                f12 = 1.0F;
            }

            f12 = f12 * 0.45F;
            red = red * (1.0F - f12) + 0.8F * f12;
            green = green * (1.0F - f12) + 0.8F * f12;
            blue = blue * (1.0F - f12) + 1.0F * f12;
        }

        return new Vec3(red, green, blue);
    }

    /**
     * calls calculateCelestialAngle
     */
    public float getCelestialAngle(final float partialTicks) {
        return this.provider.calculateCelestialAngle(this.worldInfo.getWorldTime(), partialTicks);
    }

    public int getMoonPhase() {
        return this.provider.getMoonPhase(this.worldInfo.getWorldTime());
    }

    /**
     * gets the current fullness of the moon expressed as a float between 1.0 and 0.0, in steps of .25
     */
    public float getCurrentMoonPhaseFactor() {
        return WorldProvider.moonPhaseFactors[this.provider.getMoonPhase(this.worldInfo.getWorldTime())];
    }

    /**
     * Return getCelestialAngle()*2*PI
     */
    public float getCelestialAngleRadians(final float partialTicks) {
        final float f = this.getCelestialAngle(partialTicks);
        return f * (float) Math.PI * 2.0F;
    }

    public Vec3 getCloudColour(final float partialTicks) {
        final float f = this.getCelestialAngle(partialTicks);
        float f1 = MathHelper.cos(f * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        float f2 = (float) (this.cloudColour >> 16 & 255L) / 255.0F;
        float f3 = (float) (this.cloudColour >> 8 & 255L) / 255.0F;
        float f4 = (float) (this.cloudColour & 255L) / 255.0F;
        final float f5 = this.getRainStrength(partialTicks);

        if (f5 > 0.0F) {
            final float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
            final float f7 = 1.0F - f5 * 0.95F;
            f2 = f2 * f7 + f6 * (1.0F - f7);
            f3 = f3 * f7 + f6 * (1.0F - f7);
            f4 = f4 * f7 + f6 * (1.0F - f7);
        }

        f2 = f2 * (f1 * 0.9F + 0.1F);
        f3 = f3 * (f1 * 0.9F + 0.1F);
        f4 = f4 * (f1 * 0.85F + 0.15F);
        final float f9 = this.getThunderStrength(partialTicks);

        if (f9 > 0.0F) {
            final float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
            final float f8 = 1.0F - f9 * 0.95F;
            f2 = f2 * f8 + f10 * (1.0F - f8);
            f3 = f3 * f8 + f10 * (1.0F - f8);
            f4 = f4 * f8 + f10 * (1.0F - f8);
        }

        return new Vec3(f2, f3, f4);
    }

    /**
     * Returns vector(ish) with R/G/B for fog
     */
    public Vec3 getFogColor(final float partialTicks) {
        final float f = this.getCelestialAngle(partialTicks);
        return this.provider.getFogColor(f, partialTicks);
    }

    public BlockPos getPrecipitationHeight(final BlockPos pos) {
        return this.getChunkFromBlockCoords(pos).getPrecipitationHeight(pos);
    }

    /**
     * Finds the highest block on the x and z coordinate that is solid or liquid, and returns its y coord.
     *
     * @param pos The object containing the x and z coordinates to check at
     */
    public BlockPos getTopSolidOrLiquidBlock(final BlockPos pos) {
        final Chunk chunk = this.getChunkFromBlockCoords(pos);
        BlockPos blockpos;
        BlockPos blockpos1;

        for (blockpos = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ()); blockpos.getY() >= 0; blockpos = blockpos1) {
            blockpos1 = blockpos.down();
            final Material material = chunk.getBlock(blockpos1).getMaterial();

            if (material.blocksMovement() && material != Material.leaves) {
                break;
            }
        }

        return blockpos;
    }

    /**
     * How bright are stars in the sky
     */
    public float getStarBrightness(final float partialTicks) {
        final float f = this.getCelestialAngle(partialTicks);
        float f1 = 1.0F - (MathHelper.cos(f * (float) Math.PI * 2.0F) * 2.0F + 0.25F);
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        return f1 * f1 * 0.5F;
    }

    public void scheduleUpdate(final BlockPos pos, final Block blockIn, final int delay) {
    }

    public void updateBlockTick(final BlockPos pos, final Block blockIn, final int delay, final int priority) {
    }

    public void scheduleBlockUpdate(final BlockPos pos, final Block blockIn, final int delay, final int priority) {
    }

    /**
     * Updates (and cleans up) entities and tile entities
     */
    public void updateEntities() {
        this.theProfiler.startSection("entities");
        this.theProfiler.startSection("global");


        threadPool.execute(() -> {
            for (int i = 0; i < this.weatherEffects.size(); ++i) {
                final Entity entity = this.weatherEffects.get(i);

                try {
                    ++entity.ticksExisted;
                    ++entity.ticksSinceVelocity;
                    ++entity.ticksSincePlayerVelocity;
                    ++entity.ticksSinceTeleport;
                    entity.onUpdate();
                } catch (final Throwable throwable2) {
                    final CrashReport crashreport = CrashReport.makeCrashReport(throwable2, "Ticking entity");
                    final CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being ticked");

                    if (entity == null) {
                        crashreportcategory.addCrashSection("Entity", "~~NULL~~");
                    } else {
                        entity.addEntityCrashInfo(crashreportcategory);
                    }

                    throw new ReportedException(crashreport);
                }

                if (entity.isDead) {
                    this.weatherEffects.remove(i--);
                }
            }
        });

        this.theProfiler.endStartSection("remove");
        this.loadedEntityList.removeAll(this.unloadedEntityList);

        for (final Entity entity1 : this.unloadedEntityList) {
            if (entity1 != null) {
                final int j = entity1.chunkCoordX;
                final int l1 = entity1.chunkCoordZ;

                if (entity1.addedToChunk && this.isChunkLoaded(j, l1, true)) {
                    this.getChunkFromChunkCoords(j, l1).removeEntity(entity1);
                }
            }
        }

        for (Entity entity : this.unloadedEntityList) {
            this.onEntityRemoved(entity);
        }

        this.unloadedEntityList.clear();
        this.theProfiler.endStartSection("regular");

        for (final Entity entity2 : this.loadedEntityList) {
            if (entity2 == null || mc.thePlayer == null) continue;

            if (mc.thePlayer == entity2 || (entity2.threadDistance < 16 * 16 && entity2.inView)) {
                update(entity2);
            } else {
                threadPool.execute(() -> update(entity2));
            }

            if (entity2.isDead) {
                final int k1 = entity2.chunkCoordX;
                final int i2 = entity2.chunkCoordZ;

                if (entity2.addedToChunk && this.isChunkLoaded(k1, i2, true)) {
                    this.getChunkFromChunkCoords(k1, i2).removeEntity(entity2);
                }

                this.loadedEntityList.remove(entity2);
                this.onEntityRemoved(entity2);
            }
        }

        this.theProfiler.endStartSection("blockEntities");
        this.processingLoadedTiles = true;
        final Iterator<TileEntity> iterator = this.tickableTileEntities.iterator();

        threadPool.execute(() -> {
            for (final Entity entity2 : this.loadedEntityList) {
                if (entity2 == null || mc.thePlayer == null) {
                    continue;
                }

                if (entity2 instanceof EntityItem) {
                    entity2.inView = true;
                    continue;
                }

                if (entity2 == mc.thePlayer || entity2.isInvisible()) {
                    entity2.inView = false;
                    continue;
                }

                entity2.inView = RayCastUtil.inView(entity2);
                entity2.threadDistance = mc.thePlayer.getDistanceSqToEntity(entity2);

                if (mc.gameSettings.thirdPersonView != 0) {
                    entity2.inView = true;
                }

                if (entity2.inView) {
                    entity2.outOfViewTicks = 0;
                } else {
                    entity2.outOfViewTicks++;
                }
            }
        });

        while (iterator.hasNext()) {
            final TileEntity tileentity = iterator.next();

            if (!tileentity.isInvalid() && tileentity.hasWorldObj()) {
                final BlockPos blockpos = tileentity.getPos();

                if (this.isBlockLoaded(blockpos) && this.worldBorder.contains(blockpos)) {
                    try {
                        ((ITickable) tileentity).update();
                    } catch (final Throwable throwable) {
                        final CrashReport crashreport2 = CrashReport.makeCrashReport(throwable, "Ticking block entity");
                        final CrashReportCategory crashreportcategory1 = crashreport2.makeCategory("Block entity being ticked");
                        tileentity.addInfoToCrashReport(crashreportcategory1);
                        throw new ReportedException(crashreport2);
                    }
                }
            }

            if (tileentity.isInvalid()) {
                iterator.remove();
                this.loadedTileEntityList.remove(tileentity);

                if (this.isBlockLoaded(tileentity.getPos())) {
                    this.getChunkFromBlockCoords(tileentity.getPos()).removeTileEntity(tileentity.getPos());
                }
            }
        }

//        threadPool.execute(() -> {
        this.processingLoadedTiles = false;

        if (!this.tileEntitiesToBeRemoved.isEmpty()) {
            this.tickableTileEntities.removeAll(this.tileEntitiesToBeRemoved);
            this.loadedTileEntityList.removeAll(this.tileEntitiesToBeRemoved);
            this.tileEntitiesToBeRemoved.clear();
        }

        this.theProfiler.endStartSection("pendingBlockEntities");

        if (!this.addedTileEntityList.isEmpty()) {
            for (final TileEntity tileentity1 : this.addedTileEntityList) {
                if (!tileentity1.isInvalid()) {
                    if (!this.loadedTileEntityList.contains(tileentity1)) {
                        this.addTileEntity(tileentity1);
                    }

                    if (this.isBlockLoaded(tileentity1.getPos())) {
                        this.getChunkFromBlockCoords(tileentity1.getPos()).addTileEntity(tileentity1.getPos(), tileentity1);
                    }

                    this.markBlockForUpdate(tileentity1.getPos());
                }
            }

            this.addedTileEntityList.clear();
        }
//        });


        this.theProfiler.endSection();
        this.theProfiler.endSection();
    }

    public boolean addTileEntity(final TileEntity tile) {
        final boolean flag = this.loadedTileEntityList.add(tile);

        if (flag && tile instanceof ITickable) {
            this.tickableTileEntities.add(tile);
        }

        return flag;
    }

    public void addTileEntities(final Collection<TileEntity> tileEntityCollection) {
        if (this.processingLoadedTiles) {
            this.addedTileEntityList.addAll(tileEntityCollection);
        } else {
            for (final TileEntity tileentity : tileEntityCollection) {
                this.loadedTileEntityList.add(tileentity);

                if (tileentity instanceof ITickable) {
                    this.tickableTileEntities.add(tileentity);
                }
            }
        }
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded. Args: entity
     */
    public void updateEntity(final Entity ent) {
        this.updateEntityWithOptionalForce(ent, true);
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
     * Args: entity, forceUpdate
     */
    public void updateEntityWithOptionalForce(final Entity entityIn, final boolean forceUpdate) {
        final int i = MathHelper.floor_double(entityIn.posX);
        final int j = MathHelper.floor_double(entityIn.posZ);
        final int k = 32;

        if (!forceUpdate || this.isAreaLoaded(i - k, 0, j - k, i + k, 0, j + k, true)) {
            entityIn.lastTickPosX = entityIn.posX;
            entityIn.lastTickPosY = entityIn.posY;
            entityIn.lastTickPosZ = entityIn.posZ;
            entityIn.prevRotationYaw = entityIn.rotationYaw;
            entityIn.prevRotationPitch = entityIn.rotationPitch;

            if (forceUpdate && entityIn.addedToChunk) {
                ++entityIn.ticksSinceVelocity;
                ++entityIn.ticksExisted;
                ++entityIn.ticksSincePlayerVelocity;
                ++entityIn.ticksSinceTeleport;

                if (entityIn.timer.finished(60)) {
                    entityIn.render = true;
                    entityIn.timer.reset();
                }

                if (entityIn.timerName.finished(60)) {
                    entityIn.renderNameTag = true;
                    entityIn.timerName.reset();
                }

                if (entityIn.ridingEntity != null) {
                    entityIn.updateRidden();
                } else {
                    entityIn.onUpdate();
                }
            }

            this.theProfiler.startSection("chunkCheck");

            if (Double.isNaN(entityIn.posX) || Double.isInfinite(entityIn.posX)) {
                entityIn.posX = entityIn.lastTickPosX;
            }

            if (Double.isNaN(entityIn.posY) || Double.isInfinite(entityIn.posY)) {
                entityIn.posY = entityIn.lastTickPosY;
            }

            if (Double.isNaN(entityIn.posZ) || Double.isInfinite(entityIn.posZ)) {
                entityIn.posZ = entityIn.lastTickPosZ;
            }

            if (Double.isNaN(entityIn.rotationPitch) || Double.isInfinite(entityIn.rotationPitch)) {
                entityIn.rotationPitch = entityIn.prevRotationPitch;
            }

            if (Double.isNaN(entityIn.rotationYaw) || Double.isInfinite(entityIn.rotationYaw)) {
                entityIn.rotationYaw = entityIn.prevRotationYaw;
            }

            final int l = MathHelper.floor_double(entityIn.posX / 16.0D);
            final int i1 = MathHelper.floor_double(entityIn.posY / 16.0D);
            final int j1 = MathHelper.floor_double(entityIn.posZ / 16.0D);

            if (!entityIn.addedToChunk || entityIn.chunkCoordX != l || entityIn.chunkCoordY != i1 || entityIn.chunkCoordZ != j1) {
                if (entityIn.addedToChunk && this.isChunkLoaded(entityIn.chunkCoordX, entityIn.chunkCoordZ, true)) {
                    this.getChunkFromChunkCoords(entityIn.chunkCoordX, entityIn.chunkCoordZ).removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
                }

                if (this.isChunkLoaded(l, j1, true)) {
                    entityIn.addedToChunk = true;
                    this.getChunkFromChunkCoords(l, j1).addEntity(entityIn);
                } else {
                    entityIn.addedToChunk = false;
                }
            }

            this.theProfiler.endSection();

            if (forceUpdate && entityIn.addedToChunk && entityIn.riddenByEntity != null) {
                if (!entityIn.riddenByEntity.isDead && entityIn.riddenByEntity.ridingEntity == entityIn) {
                    this.updateEntity(entityIn.riddenByEntity);
                } else {
                    entityIn.riddenByEntity.ridingEntity = null;
                    entityIn.riddenByEntity = null;
                }
            }
        }
    }

    /**
     * Returns true if there are no solid, live entities in the specified AxisAlignedBB
     */
    public boolean checkNoEntityCollision(final AxisAlignedBB bb) {
        return this.checkNoEntityCollision(bb, null);
    }

    /**
     * Returns true if there are no solid, live entities in the specified AxisAlignedBB, excluding the given entity
     */
    public boolean checkNoEntityCollision(final AxisAlignedBB bb, final Entity entityIn) {
        final List<Entity> list = this.getEntitiesWithinAABBExcludingEntity(null, bb);

        for (int i = 0; i < list.size(); ++i) {
            final Entity entity = list.get(i);

            if (!entity.isDead && entity.preventEntitySpawning && entity != entityIn && (entityIn == null || entityIn.ridingEntity != entity && entityIn.riddenByEntity != entity)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if there are any blocks in the region constrained by an AxisAlignedBB
     */
    public boolean checkBlockCollision(final AxisAlignedBB bb) {
        final int i = MathHelper.floor_double(bb.minX);
        final int j = MathHelper.floor_double(bb.maxX);
        final int k = MathHelper.floor_double(bb.minY);
        final int l = MathHelper.floor_double(bb.maxY);
        final int i1 = MathHelper.floor_double(bb.minZ);
        final int j1 = MathHelper.floor_double(bb.maxZ);
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 <= j; ++k1) {
            for (int l1 = k; l1 <= l; ++l1) {
                for (int i2 = i1; i2 <= j1; ++i2) {
                    final Block block = this.getBlockState(blockpos$mutableblockpos.func_181079_c(k1, l1, i2)).getBlock();

                    if (block.getMaterial() != Material.air) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns if any of the blocks within the aabb are liquids. Args: aabb
     */
    public boolean isAnyLiquid(final AxisAlignedBB bb) {
        final int i = MathHelper.floor_double(bb.minX);
        final int j = MathHelper.floor_double(bb.maxX);
        final int k = MathHelper.floor_double(bb.minY);
        final int l = MathHelper.floor_double(bb.maxY);
        final int i1 = MathHelper.floor_double(bb.minZ);
        final int j1 = MathHelper.floor_double(bb.maxZ);
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 <= j; ++k1) {
            for (int l1 = k; l1 <= l; ++l1) {
                for (int i2 = i1; i2 <= j1; ++i2) {
                    final Block block = this.getBlockState(blockpos$mutableblockpos.func_181079_c(k1, l1, i2)).getBlock();

                    if (block.getMaterial().isLiquid()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isFlammableWithin(final AxisAlignedBB bb) {
        final int i = MathHelper.floor_double(bb.minX);
        final int j = MathHelper.floor_double(bb.maxX + 1.0D);
        final int k = MathHelper.floor_double(bb.minY);
        final int l = MathHelper.floor_double(bb.maxY + 1.0D);
        final int i1 = MathHelper.floor_double(bb.minZ);
        final int j1 = MathHelper.floor_double(bb.maxZ + 1.0D);

        if (this.isAreaLoaded(i, k, i1, j, l, j1, true)) {
            final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        final Block block = this.getBlockState(blockpos$mutableblockpos.func_181079_c(k1, l1, i2)).getBlock();

                        if (block == Blocks.fire || block == Blocks.flowing_lava || block == Blocks.lava) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * handles the acceleration of an object whilst in water. Not sure if it is used elsewhere.
     */
    public boolean handleMaterialAcceleration(final AxisAlignedBB bb, final Material materialIn, final Entity entityIn) {
        final int i = MathHelper.floor_double(bb.minX);
        final int j = MathHelper.floor_double(bb.maxX + 1.0D);
        final int k = MathHelper.floor_double(bb.minY);
        final int l = MathHelper.floor_double(bb.maxY + 1.0D);
        final int i1 = MathHelper.floor_double(bb.minZ);
        final int j1 = MathHelper.floor_double(bb.maxZ + 1.0D);

        if (!this.isAreaLoaded(i, k, i1, j, l, j1, true)) {
            return false;
        } else {
            boolean flag = false;
            Vec3 vec3 = new Vec3(0.0D, 0.0D, 0.0D);
            final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        blockpos$mutableblockpos.func_181079_c(k1, l1, i2);
                        final IBlockState iblockstate = this.getBlockState(blockpos$mutableblockpos);
                        final Block block = iblockstate.getBlock();

                        if (block.getMaterial() == materialIn) {
                            final double d0 = (float) (l1 + 1) - BlockLiquid.getLiquidHeightPercent(iblockstate.getValue(BlockLiquid.LEVEL).intValue());

                            if ((double) l >= d0) {
                                flag = true;
                                vec3 = block.modifyAcceleration(this, blockpos$mutableblockpos, entityIn, vec3);
                            }
                        }
                    }
                }
            }

            if (vec3.lengthVector() > 0.0D && entityIn.isPushedByWater()) {
                vec3 = vec3.normalize();
                final double d1 = 0.014D;
                entityIn.motionX += vec3.xCoord * d1;
                entityIn.motionY += vec3.yCoord * d1;
                entityIn.motionZ += vec3.zCoord * d1;
            }

            return flag;
        }
    }

    /**
     * Returns true if the given bounding box contains the given material
     */
    public boolean isMaterialInBB(final AxisAlignedBB bb, final Material materialIn) {
        final int i = MathHelper.floor_double(bb.minX);
        final int j = MathHelper.floor_double(bb.maxX + 1.0D);
        final int k = MathHelper.floor_double(bb.minY);
        final int l = MathHelper.floor_double(bb.maxY + 1.0D);
        final int i1 = MathHelper.floor_double(bb.minZ);
        final int j1 = MathHelper.floor_double(bb.maxZ + 1.0D);
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    if (this.getBlockState(blockpos$mutableblockpos.func_181079_c(k1, l1, i2)).getBlock().getMaterial() == materialIn) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * checks if the given AABB is in the material given. Used while swimming.
     */
    public boolean isAABBInMaterial(final AxisAlignedBB bb, final Material materialIn) {
        final int i = MathHelper.floor_double(bb.minX);
        final int j = MathHelper.floor_double(bb.maxX + 1.0D);
        final int k = MathHelper.floor_double(bb.minY);
        final int l = MathHelper.floor_double(bb.maxY + 1.0D);
        final int i1 = MathHelper.floor_double(bb.minZ);
        final int j1 = MathHelper.floor_double(bb.maxZ + 1.0D);
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    final IBlockState iblockstate = this.getBlockState(blockpos$mutableblockpos.func_181079_c(k1, l1, i2));
                    final Block block = iblockstate.getBlock();

                    if (block.getMaterial() == materialIn) {
                        final int j2 = iblockstate.getValue(BlockLiquid.LEVEL).intValue();
                        double d0 = l1 + 1;

                        if (j2 < 8) {
                            d0 = (double) (l1 + 1) - (double) j2 / 8.0D;
                        }

                        if (d0 >= bb.minY) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Creates an explosion. Args: entity, x, y, z, strength
     */
    public Explosion createExplosion(final Entity entityIn, final double x, final double y, final double z, final float strength, final boolean isSmoking) {
        return this.newExplosion(entityIn, x, y, z, strength, false, isSmoking);
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    public Explosion newExplosion(final Entity entityIn, final double x, final double y, final double z, final float strength, final boolean isFlaming, final boolean isSmoking) {
        final Explosion explosion = new Explosion(this, entityIn, x, y, z, strength, isFlaming, isSmoking);
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        return explosion;
    }

    /**
     * Gets the percentage of real blocks within within a bounding box, along a specified vector.
     */
    public float getBlockDensity(final Vec3 vec, final AxisAlignedBB bb) {
        final double d0 = 1.0D / ((bb.maxX - bb.minX) * 2.0D + 1.0D);
        final double d1 = 1.0D / ((bb.maxY - bb.minY) * 2.0D + 1.0D);
        final double d2 = 1.0D / ((bb.maxZ - bb.minZ) * 2.0D + 1.0D);
        final double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        final double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;

        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D) {
            int i = 0;
            int j = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0)) {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
                        final double d5 = bb.minX + (bb.maxX - bb.minX) * (double) f;
                        final double d6 = bb.minY + (bb.maxY - bb.minY) * (double) f1;
                        final double d7 = bb.minZ + (bb.maxZ - bb.minZ) * (double) f2;

                        if (this.rayTraceBlocks(new Vec3(d5 + d3, d6, d7 + d4), vec) == null) {
                            ++i;
                        }

                        ++j;
                    }
                }
            }

            return (float) i / (float) j;
        } else {
            return 0.0F;
        }
    }

    /**
     * Attempts to extinguish a fire
     *
     * @param player The player putting out the fire
     * @param pos    The coordinates of the fire
     * @param side   The side from which the player is putting out the fire from
     */
    public boolean extinguishFire(final EntityPlayer player, BlockPos pos, final EnumFacing side) {
        pos = pos.offset(side);

        if (this.getBlockState(pos).getBlock() == Blocks.fire) {
            this.playAuxSFXAtEntity(player, 1004, pos, 0);
            this.setBlockToAir(pos);
            return true;
        } else {
            return false;
        }
    }

    /**
     * This string is 'All: (number of loaded entities)' Viewable by press ing F3
     */
    public String getDebugLoadedEntities() {
        return "All: " + this.loadedEntityList.size();
    }

    /**
     * Returns the name of the current chunk provider, by calling chunkprovider.makeString()
     */
    public String getProviderName() {
        return this.chunkProvider.makeString();
    }

    public TileEntity getTileEntity(final BlockPos pos) {
        if (!this.isValid(pos)) {
            return null;
        } else {
            TileEntity tileentity = null;

            if (this.processingLoadedTiles) {
                for (int i = 0; i < this.addedTileEntityList.size(); ++i) {
                    final TileEntity tileentity1 = this.addedTileEntityList.get(i);

                    if (!tileentity1.isInvalid() && tileentity1.getPos().equals(pos)) {
                        tileentity = tileentity1;
                        break;
                    }
                }
            }

            if (tileentity == null) {
                tileentity = this.getChunkFromBlockCoords(pos).getTileEntity(pos, Chunk.EnumCreateEntityType.IMMEDIATE);
            }

            if (tileentity == null) {
                for (int j = 0; j < this.addedTileEntityList.size(); ++j) {
                    final TileEntity tileentity2 = this.addedTileEntityList.get(j);

                    if (!tileentity2.isInvalid() && tileentity2.getPos().equals(pos)) {
                        tileentity = tileentity2;
                        break;
                    }
                }
            }

            return tileentity;
        }
    }

    public void setTileEntity(final BlockPos pos, final TileEntity tileEntityIn) {
        if (tileEntityIn != null && !tileEntityIn.isInvalid()) {
            if (this.processingLoadedTiles) {
                tileEntityIn.setPos(pos);
                final Iterator<TileEntity> iterator = this.addedTileEntityList.iterator();

                while (iterator.hasNext()) {
                    try {
                        final TileEntity tileentity = iterator.next();

                        if (tileentity.getPos().equals(pos)) {
                            tileentity.invalidate();
                            iterator.remove();
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

                this.addedTileEntityList.add(tileEntityIn);
            } else {
                this.addTileEntity(tileEntityIn);
                this.getChunkFromBlockCoords(pos).addTileEntity(pos, tileEntityIn);
            }
        }
    }

    public void removeTileEntity(final BlockPos pos) {
        final TileEntity tileentity = this.getTileEntity(pos);

        if (tileentity != null && this.processingLoadedTiles) {
            tileentity.invalidate();
            this.addedTileEntityList.remove(tileentity);
        } else {
            if (tileentity != null) {
                this.addedTileEntityList.remove(tileentity);
                this.loadedTileEntityList.remove(tileentity);
                this.tickableTileEntities.remove(tileentity);
            }

            this.getChunkFromBlockCoords(pos).removeTileEntity(pos);
        }
    }

    /**
     * Adds the specified TileEntity to the pending removal list.
     */
    public void markTileEntityForRemoval(final TileEntity tileEntityIn) {
        this.tileEntitiesToBeRemoved.add(tileEntityIn);
    }

    public boolean isBlockFullCube(final BlockPos pos) {
        final IBlockState iblockstate = this.getBlockState(pos);
        final AxisAlignedBB axisalignedbb = iblockstate.getBlock().getCollisionBoundingBox(this, pos, iblockstate);
        return axisalignedbb != null && axisalignedbb.getAverageEdgeLength() >= 1.0D;
    }

    public static boolean doesBlockHaveSolidTopSurface(final IBlockAccess blockAccess, final BlockPos pos) {
        final IBlockState iblockstate = blockAccess.getBlockState(pos);
        final Block block = iblockstate.getBlock();
        return block.getMaterial().isOpaque() && block.isFullCube() || (block instanceof BlockStairs ? iblockstate.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP : (block instanceof BlockSlab ? iblockstate.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP : (block instanceof BlockHopper || (block instanceof BlockSnow && iblockstate.getValue(BlockSnow.LAYERS).intValue() == 7))));
    }

    /**
     * Checks if a block's material is opaque, and that it takes up a full cube
     */
    public boolean isBlockNormalCube(final BlockPos pos, final boolean _default) {
        if (!this.isValid(pos)) {
            return _default;
        } else {
            final Chunk chunk = this.chunkProvider.provideChunk(pos);

            if (chunk.isEmpty()) {
                return _default;
            } else {
                final Block block = this.getBlockState(pos).getBlock();
                return block.getMaterial().isOpaque() && block.isFullCube();
            }
        }
    }

    /**
     * Called on construction of the World class to setup the initial skylight values
     */
    public void calculateInitialSkylight() {
        final int i = this.calculateSkylightSubtracted(1.0F);

        if (i != this.skylightSubtracted) {
            this.skylightSubtracted = i;
        }
    }

    /**
     * first boolean for hostile mobs and second for peaceful mobs
     */
    public void setAllowedSpawnTypes(final boolean hostile, final boolean peaceful) {
        this.spawnHostileMobs = hostile;
        this.spawnPeacefulMobs = peaceful;
    }

    /**
     * Runs a single tick for the world
     */
    public void tick() {
        this.updateWeather();
    }

    /**
     * Called from World constructor to set rainingStrength and thunderingStrength
     */
    protected void calculateInitialWeather() {
        if (this.worldInfo.isRaining()) {
            this.rainingStrength = 1.0F;

            if (this.worldInfo.isThundering()) {
                this.thunderingStrength = 1.0F;
            }
        }
    }

    /**
     * Updates all weather states.
     */
    protected void updateWeather() {
        if (!this.provider.getHasNoSky()) {
            if (!this.isRemote) {
                int i = this.worldInfo.getCleanWeatherTime();

                if (i > 0) {
                    --i;
                    this.worldInfo.setCleanWeatherTime(i);
                    this.worldInfo.setThunderTime(this.worldInfo.isThundering() ? 1 : 2);
                    this.worldInfo.setRainTime(this.worldInfo.isRaining() ? 1 : 2);
                }

                int j = this.worldInfo.getThunderTime();

                if (j <= 0) {
                    if (this.worldInfo.isThundering()) {
                        this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
                    } else {
                        this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
                    }
                } else {
                    --j;
                    this.worldInfo.setThunderTime(j);

                    if (j <= 0) {
                        this.worldInfo.setThundering(!this.worldInfo.isThundering());
                    }
                }

                this.prevThunderingStrength = this.thunderingStrength;

                if (this.worldInfo.isThundering()) {
                    this.thunderingStrength = (float) ((double) this.thunderingStrength + 0.01D);
                } else {
                    this.thunderingStrength = (float) ((double) this.thunderingStrength - 0.01D);
                }

                this.thunderingStrength = MathHelper.clamp_float(this.thunderingStrength, 0.0F, 1.0F);
                int k = this.worldInfo.getRainTime();

                if (k <= 0) {
                    if (this.worldInfo.isRaining()) {
                        this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
                    } else {
                        this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
                    }
                } else {
                    --k;
                    this.worldInfo.setRainTime(k);

                    if (k <= 0) {
                        this.worldInfo.setRaining(!this.worldInfo.isRaining());
                    }
                }

                this.prevRainingStrength = this.rainingStrength;

                if (this.worldInfo.isRaining()) {
                    this.rainingStrength = (float) ((double) this.rainingStrength + 0.01D);
                } else {
                    this.rainingStrength = (float) ((double) this.rainingStrength - 0.01D);
                }

                this.rainingStrength = MathHelper.clamp_float(this.rainingStrength, 0.0F, 1.0F);
            }
        }
    }

    protected void setActivePlayerChunksAndCheckLight() {
        this.activeChunkSet.clear();
        this.theProfiler.startSection("buildList");

        for (int i = 0; i < this.playerEntities.size(); ++i) {
            final EntityPlayer entityplayer = this.playerEntities.get(i);
            final int j = MathHelper.floor_double(entityplayer.posX / 16.0D);
            final int k = MathHelper.floor_double(entityplayer.posZ / 16.0D);
            final int l = this.getRenderDistanceChunks();

            for (int i1 = -l; i1 <= l; ++i1) {
                for (int j1 = -l; j1 <= l; ++j1) {
                    this.activeChunkSet.add(new ChunkCoordIntPair(i1 + j, j1 + k));
                }
            }
        }

        this.theProfiler.endSection();

        if (this.ambientTickCountdown > 0) {
            --this.ambientTickCountdown;
        }

        this.theProfiler.startSection("playerCheckLight");

        if (!this.playerEntities.isEmpty()) {
            final int k1 = this.rand.nextInt(this.playerEntities.size());
            final EntityPlayer entityplayer1 = this.playerEntities.get(k1);
            final int l1 = MathHelper.floor_double(entityplayer1.posX) + this.rand.nextInt(11) - 5;
            final int i2 = MathHelper.floor_double(entityplayer1.posY) + this.rand.nextInt(11) - 5;
            final int j2 = MathHelper.floor_double(entityplayer1.posZ) + this.rand.nextInt(11) - 5;
            this.checkLight(new BlockPos(l1, i2, j2));
        }

        this.theProfiler.endSection();
    }

    protected abstract int getRenderDistanceChunks();

    protected void playMoodSoundAndCheckLight(final int p_147467_1_, final int p_147467_2_, final Chunk chunkIn) {
        this.theProfiler.endStartSection("moodSound");

        if (this.ambientTickCountdown == 0 && !this.isRemote) {
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            final int i = this.updateLCG >> 2;
            int j = i & 15;
            int k = i >> 8 & 15;
            final int l = i >> 16 & 255;
            final BlockPos blockpos = new BlockPos(j, l, k);
            final Block block = chunkIn.getBlock(blockpos);
            j = j + p_147467_1_;
            k = k + p_147467_2_;

            if (block.getMaterial() == Material.air && this.getLight(blockpos) <= this.rand.nextInt(8) && this.getLightFor(EnumSkyBlock.SKY, blockpos) <= 0) {
                final EntityPlayer entityplayer = this.getClosestPlayer((double) j + 0.5D, (double) l + 0.5D, (double) k + 0.5D, 8.0D);

                if (entityplayer != null && entityplayer.getDistanceSq((double) j + 0.5D, (double) l + 0.5D, (double) k + 0.5D) > 4.0D) {
                    this.playSoundEffect((double) j + 0.5D, (double) l + 0.5D, (double) k + 0.5D, "ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
                    this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
                }
            }
        }

        this.theProfiler.endStartSection("checkLight");
        chunkIn.enqueueRelightChecks();
    }

    protected void updateBlocks() {
        this.setActivePlayerChunksAndCheckLight();
    }

    public void forceBlockUpdateTick(final Block blockType, final BlockPos pos, final Random random) {
        this.scheduledUpdatesAreImmediate = true;
        blockType.updateTick(this, pos, this.getBlockState(pos), random);
        this.scheduledUpdatesAreImmediate = false;
    }

    public boolean canBlockFreezeWater(final BlockPos pos) {
        return this.canBlockFreeze(pos, false);
    }

    public boolean canBlockFreezeNoWater(final BlockPos pos) {
        return this.canBlockFreeze(pos, true);
    }

    /**
     * Checks to see if a given block is both water and cold enough to freeze.
     *
     * @param pos        The block coordinates
     * @param noWaterAdj If true, this method will only return true if there is a non-water block immediately adjacent
     *                   to the specified block
     */
    public boolean canBlockFreeze(final BlockPos pos, final boolean noWaterAdj) {
        final BiomeGenBase biomegenbase = this.getBiomeGenForCoords(pos);
        final float f = biomegenbase.getFloatTemperature(pos);

        if (f > 0.15F) {
            return false;
        } else {
            if (pos.getY() >= 0 && pos.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
                final IBlockState iblockstate = this.getBlockState(pos);
                final Block block = iblockstate.getBlock();

                if ((block == Blocks.water || block == Blocks.flowing_water) && iblockstate.getValue(BlockLiquid.LEVEL).intValue() == 0) {
                    if (!noWaterAdj) {
                        return true;
                    }

                    final boolean flag = this.isWater(pos.west()) && this.isWater(pos.east()) && this.isWater(pos.north()) && this.isWater(pos.south());

                    return !flag;
                }
            }

            return false;
        }
    }

    private boolean isWater(final BlockPos pos) {
        return this.getBlockState(pos).getBlock().getMaterial() == Material.water;
    }

    /**
     * Checks to see if a given block can accumulate snow from it snowing
     *
     * @param pos        The block coordinates
     * @param checkLight If false, checking for the correct light values will be skipped
     */
    public boolean canSnowAt(final BlockPos pos, final boolean checkLight) {
        final BiomeGenBase biomegenbase = this.getBiomeGenForCoords(pos);
        final float f = biomegenbase.getFloatTemperature(pos);

        if (f > 0.15F) {
            return false;
        } else if (!checkLight) {
            return true;
        } else {
            if (pos.getY() >= 0 && pos.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
                final Block block = this.getBlockState(pos).getBlock();

                return block.getMaterial() == Material.air && Blocks.snow_layer.canPlaceBlockAt(this, pos);
            }

            return false;
        }
    }

    public boolean checkLight(final BlockPos pos) {
        boolean flag = false;

        if (!this.provider.getHasNoSky()) {
            flag |= this.checkLightFor(EnumSkyBlock.SKY, pos);
        }

        flag = flag | this.checkLightFor(EnumSkyBlock.BLOCK, pos);
        return flag;
    }

    /**
     * gets the light level at the supplied position
     */
    private int getRawLight(final BlockPos pos, final EnumSkyBlock lightType) {
        if (lightType == EnumSkyBlock.SKY && this.canSeeSky(pos)) {
            return 15;
        } else {
            final Block block = this.getBlockState(pos).getBlock();
            int i = lightType == EnumSkyBlock.SKY ? 0 : block.getLightValue();
            int j = block.getLightOpacity();

            if (j >= 15 && block.getLightValue() > 0) {
                j = 1;
            }

            if (j < 1) {
                j = 1;
            }

            if (j >= 15) {
                return 0;
            } else if (i >= 14) {
                return i;
            } else {
                for (final EnumFacing enumfacing : EnumFacing.values()) {
                    final BlockPos blockpos = pos.offset(enumfacing);
                    final int k = this.getLightFor(lightType, blockpos) - j;

                    if (k > i) {
                        i = k;
                    }

                    if (i >= 14) {
                        return i;
                    }
                }

                return i;
            }
        }
    }

    public boolean checkLightFor(final EnumSkyBlock lightType, final BlockPos pos) {
        if (!this.isAreaLoaded(pos, 17, false)) {
            return false;
        } else {
            int i = 0;
            int j = 0;
            this.theProfiler.startSection("getBrightness");
            final int k = this.getLightFor(lightType, pos);
            final int l = this.getRawLight(pos, lightType);
            final int i1 = pos.getX();
            final int j1 = pos.getY();
            final int k1 = pos.getZ();

            if (l > k) {
                this.lightUpdateBlockList[j++] = 133152;
            } else if (l < k) {
                this.lightUpdateBlockList[j++] = 133152 | k << 18;

                while (i < j) {
                    final int l1 = this.lightUpdateBlockList[i++];
                    final int i2 = (l1 & 63) - 32 + i1;
                    final int j2 = (l1 >> 6 & 63) - 32 + j1;
                    final int k2 = (l1 >> 12 & 63) - 32 + k1;
                    final int l2 = l1 >> 18 & 15;
                    final BlockPos blockpos = new BlockPos(i2, j2, k2);
                    int i3 = this.getLightFor(lightType, blockpos);

                    if (i3 == l2) {
                        this.setLightFor(lightType, blockpos, 0);

                        if (l2 > 0) {
                            final int j3 = MathHelper.abs_int(i2 - i1);
                            final int k3 = MathHelper.abs_int(j2 - j1);
                            final int l3 = MathHelper.abs_int(k2 - k1);

                            if (j3 + k3 + l3 < 17) {
                                final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                                for (final EnumFacing enumfacing : EnumFacing.values()) {
                                    final int i4 = i2 + enumfacing.getFrontOffsetX();
                                    final int j4 = j2 + enumfacing.getFrontOffsetY();
                                    final int k4 = k2 + enumfacing.getFrontOffsetZ();
                                    blockpos$mutableblockpos.func_181079_c(i4, j4, k4);
                                    final int l4 = Math.max(1, this.getBlockState(blockpos$mutableblockpos).getBlock().getLightOpacity());
                                    i3 = this.getLightFor(lightType, blockpos$mutableblockpos);

                                    if (i3 == l2 - l4 && j < this.lightUpdateBlockList.length) {
                                        this.lightUpdateBlockList[j++] = i4 - i1 + 32 | j4 - j1 + 32 << 6 | k4 - k1 + 32 << 12 | l2 - l4 << 18;
                                    }
                                }
                            }
                        }
                    }
                }

                i = 0;
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("checkedPosition < toCheckCount");

            while (i < j) {
                final int i5 = this.lightUpdateBlockList[i++];
                final int j5 = (i5 & 63) - 32 + i1;
                final int k5 = (i5 >> 6 & 63) - 32 + j1;
                final int l5 = (i5 >> 12 & 63) - 32 + k1;
                final BlockPos blockpos1 = new BlockPos(j5, k5, l5);
                final int i6 = this.getLightFor(lightType, blockpos1);
                final int j6 = this.getRawLight(blockpos1, lightType);

                if (j6 != i6) {
                    this.setLightFor(lightType, blockpos1, j6);

                    if (j6 > i6) {
                        final int k6 = Math.abs(j5 - i1);
                        final int l6 = Math.abs(k5 - j1);
                        final int i7 = Math.abs(l5 - k1);
                        final boolean flag = j < this.lightUpdateBlockList.length - 6;

                        if (k6 + l6 + i7 < 17 && flag) {
                            if (this.getLightFor(lightType, blockpos1.west()) < j6) {
                                this.lightUpdateBlockList[j++] = j5 - 1 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                            }

                            if (this.getLightFor(lightType, blockpos1.east()) < j6) {
                                this.lightUpdateBlockList[j++] = j5 + 1 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                            }

                            if (this.getLightFor(lightType, blockpos1.down()) < j6) {
                                this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - 1 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                            }

                            if (this.getLightFor(lightType, blockpos1.up()) < j6) {
                                this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 + 1 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                            }

                            if (this.getLightFor(lightType, blockpos1.north()) < j6) {
                                this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - 1 - k1 + 32 << 12);
                            }

                            if (this.getLightFor(lightType, blockpos1.south()) < j6) {
                                this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 + 1 - k1 + 32 << 12);
                            }
                        }
                    }
                }
            }

            this.theProfiler.endSection();
            return true;
        }
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    public boolean tickUpdates(final boolean p_72955_1_) {
        return false;
    }

    public List<NextTickListEntry> getPendingBlockUpdates(final Chunk chunkIn, final boolean p_72920_2_) {
        return null;
    }

    public List<NextTickListEntry> func_175712_a(final StructureBoundingBox structureBB, final boolean p_175712_2_) {
        return null;
    }

    public List<Entity> getEntitiesWithinAABBExcludingEntity(final Entity entityIn, final AxisAlignedBB bb) {
        return this.getEntitiesInAABBexcluding(entityIn, bb, EntitySelectors.NOT_SPECTATING);
    }

    public List<Entity> getEntitiesInAABBexcluding(final Entity entityIn, final AxisAlignedBB boundingBox, final Predicate<? super Entity> predicate) {
        final List<Entity> list = Lists.newArrayList();
        final int i = MathHelper.floor_double((boundingBox.minX - 2.0D) / 16.0D);
        final int j = MathHelper.floor_double((boundingBox.maxX + 2.0D) / 16.0D);
        final int k = MathHelper.floor_double((boundingBox.minZ - 2.0D) / 16.0D);
        final int l = MathHelper.floor_double((boundingBox.maxZ + 2.0D) / 16.0D);

        try {
            for (int i1 = i; i1 <= j; ++i1) {
                for (int j1 = k; j1 <= l; ++j1) {
                    if (this.isChunkLoaded(i1, j1, true)) {
                        this.getChunkFromChunkCoords(i1, j1).getEntitiesWithinAABBForEntity(entityIn, boundingBox, list, predicate);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return list;
        }

        return list;
    }

    public <T extends Entity> List<T> getEntities(final Class<? extends T> entityType, final Predicate<? super T> filter) {
        final List<T> list = Lists.newArrayList();

        for (final Entity entity : this.loadedEntityList) {
            if (entityType.isAssignableFrom(entity.getClass()) && filter.apply((T) entity)) {
                list.add((T) entity);
            }
        }

        return list;
    }

    public <T extends Entity> List<T> getPlayers(final Class<? extends T> playerType, final Predicate<? super T> filter) {
        final List<T> list = Lists.newArrayList();

        for (final Entity entity : this.playerEntities) {
            if (playerType.isAssignableFrom(entity.getClass()) && filter.apply((T) entity)) {
                list.add((T) entity);
            }
        }

        return list;
    }

    public <T extends Entity> List<T> getEntitiesWithinAABB(final Class<? extends T> classEntity, final AxisAlignedBB bb) {
        return this.getEntitiesWithinAABB(classEntity, bb, EntitySelectors.NOT_SPECTATING);
    }

    public <T extends Entity> List<T> getEntitiesWithinAABB(final Class<? extends T> clazz, final AxisAlignedBB aabb, final Predicate<? super T> filter) {
        final int i = MathHelper.floor_double((aabb.minX - 2.0D) / 16.0D);
        final int j = MathHelper.floor_double((aabb.maxX + 2.0D) / 16.0D);
        final int k = MathHelper.floor_double((aabb.minZ - 2.0D) / 16.0D);
        final int l = MathHelper.floor_double((aabb.maxZ + 2.0D) / 16.0D);
        final List<T> list = Lists.newArrayList();

        for (int i1 = i; i1 <= j; ++i1) {
            for (int j1 = k; j1 <= l; ++j1) {
                if (this.isChunkLoaded(i1, j1, true)) {
                    this.getChunkFromChunkCoords(i1, j1).getEntitiesOfTypeWithinAAAB(clazz, aabb, list, filter);
                }
            }
        }

        return list;
    }

    public <T extends Entity> T findNearestEntityWithinAABB(final Class<? extends T> entityType, final AxisAlignedBB aabb, final T closestTo) {
        final List<T> list = this.getEntitiesWithinAABB(entityType, aabb);
        T t = null;
        double d0 = Double.MAX_VALUE;

        for (int i = 0; i < list.size(); ++i) {
            final T t1 = list.get(i);

            if (t1 != closestTo && EntitySelectors.NOT_SPECTATING.apply(t1)) {
                final double d1 = closestTo.getDistanceSqToEntity(t1);

                if (d1 <= d0) {
                    t = t1;
                    d0 = d1;
                }
            }
        }

        return t;
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    public Entity getEntityByID(final int id) {
        return this.entitiesById.lookup(id);
    }

    public ConcurrentLinkedQueue<Entity> getLoadedEntityList() {
        return this.loadedEntityList;
    }

    public void markChunkDirty(final BlockPos pos, final TileEntity unusedTileEntity) {
        if (this.isBlockLoaded(pos)) {
            this.getChunkFromBlockCoords(pos).setChunkModified();
        }
    }

    /**
     * Counts how many entities of an entity class exist in the world. Args: entityClass
     */
    public int countEntities(final Class<?> entityType) {
        int i = 0;

        for (final Entity entity : this.loadedEntityList) {
            if ((!(entity instanceof EntityLiving) || !((EntityLiving) entity).isNoDespawnRequired()) && entityType.isAssignableFrom(entity.getClass())) {
                ++i;
            }
        }

        return i;
    }

    public void loadEntities(final Collection<Entity> entityCollection) {
        this.loadedEntityList.addAll(entityCollection);

        for (final Entity entity : entityCollection) {
            this.onEntityAdded(entity);
        }
    }

    public void unloadEntities(final Collection<Entity> entityCollection) {
        this.unloadedEntityList.addAll(entityCollection);
    }

    public boolean canBlockBePlaced(final Block blockIn, final BlockPos pos, final boolean p_175716_3_, final EnumFacing side, final Entity entityIn, final ItemStack itemStackIn) {
        final Block block = this.getBlockState(pos).getBlock();
        final AxisAlignedBB axisalignedbb = p_175716_3_ ? null : blockIn.getCollisionBoundingBox(this, pos, blockIn.getDefaultState());
        return (axisalignedbb == null || Minecraft.getMinecraft().thePlayer.noClip || this.checkNoEntityCollision(axisalignedbb, entityIn)) && (block.getMaterial() == Material.circuits && blockIn == Blocks.anvil || block.getMaterial().isReplaceable() && blockIn.canReplace(this, pos, side, itemStackIn));
    }

    public int func_181545_F() {
        return this.field_181546_a;
    }

    public void func_181544_b(final int p_181544_1_) {
        this.field_181546_a = p_181544_1_;
    }

    public int getStrongPower(final BlockPos pos, final EnumFacing direction) {
        final IBlockState iblockstate = this.getBlockState(pos);
        return iblockstate.getBlock().isProvidingStrongPower(this, pos, iblockstate, direction);
    }

    public WorldType getWorldType() {
        return this.worldInfo.getTerrainType();
    }

    /**
     * Returns the single highest strong power out of all directions using getStrongPower(BlockPos, EnumFacing)
     */
    public int getStrongPower(final BlockPos pos) {
        int i = 0;
        i = Math.max(i, this.getStrongPower(pos.down(), EnumFacing.DOWN));

        if (i >= 15) {
            return i;
        } else {
            i = Math.max(i, this.getStrongPower(pos.up(), EnumFacing.UP));

            if (i >= 15) {
                return i;
            } else {
                i = Math.max(i, this.getStrongPower(pos.north(), EnumFacing.NORTH));

                if (i >= 15) {
                    return i;
                } else {
                    i = Math.max(i, this.getStrongPower(pos.south(), EnumFacing.SOUTH));

                    if (i >= 15) {
                        return i;
                    } else {
                        i = Math.max(i, this.getStrongPower(pos.west(), EnumFacing.WEST));

                        if (i >= 15) {
                            return i;
                        } else {
                            i = Math.max(i, this.getStrongPower(pos.east(), EnumFacing.EAST));
                            return i;
                        }
                    }
                }
            }
        }
    }

    public boolean isSidePowered(final BlockPos pos, final EnumFacing side) {
        return this.getRedstonePower(pos, side) > 0;
    }

    public int getRedstonePower(final BlockPos pos, final EnumFacing facing) {
        final IBlockState iblockstate = this.getBlockState(pos);
        final Block block = iblockstate.getBlock();
        return block.isNormalCube() ? this.getStrongPower(pos) : block.isProvidingWeakPower(this, pos, iblockstate, facing);
    }

    public boolean isBlockPowered(final BlockPos pos) {
        return this.getRedstonePower(pos.down(), EnumFacing.DOWN) > 0 || (this.getRedstonePower(pos.up(), EnumFacing.UP) > 0 || (this.getRedstonePower(pos.north(), EnumFacing.NORTH) > 0 || (this.getRedstonePower(pos.south(), EnumFacing.SOUTH) > 0 || (this.getRedstonePower(pos.west(), EnumFacing.WEST) > 0 || this.getRedstonePower(pos.east(), EnumFacing.EAST) > 0))));
    }

    /**
     * Checks if the specified block or its neighbors are powered by a neighboring block. Used by blocks like TNT and
     * Doors.
     */
    public int isBlockIndirectlyGettingPowered(final BlockPos pos) {
        int i = 0;

        for (final EnumFacing enumfacing : EnumFacing.values()) {
            final int j = this.getRedstonePower(pos.offset(enumfacing), enumfacing);

            if (j >= 15) {
                return 15;
            }

            if (j > i) {
                i = j;
            }
        }

        return i;
    }

    /**
     * Gets the closest player to the entity within the specified distance (if distance is less than 0 then ignored).
     * Args: entity, dist
     */
    public EntityPlayer getClosestPlayerToEntity(final Entity entityIn, final double distance) {
        return this.getClosestPlayer(entityIn.posX, entityIn.posY, entityIn.posZ, distance);
    }

    /**
     * Gets the closest player to the point within the specified distance (distance can be set to less than 0 to not
     * limit the distance). Args: x, y, z, dist
     */
    public EntityPlayer getClosestPlayer(final double x, final double y, final double z, final double distance) {
        double d0 = -1.0D;
        EntityPlayer entityplayer = null;

        for (int i = 0; i < this.playerEntities.size(); ++i) {
            final EntityPlayer entityplayer1 = this.playerEntities.get(i);

            if (EntitySelectors.NOT_SPECTATING.apply(entityplayer1)) {
                final double d1 = entityplayer1.getDistanceSq(x, y, z);

                if ((distance < 0.0D || d1 < distance * distance) && (d0 == -1.0D || d1 < d0)) {
                    d0 = d1;
                    entityplayer = entityplayer1;
                }
            }
        }

        return entityplayer;
    }

    public boolean isAnyPlayerWithinRangeAt(final double x, final double y, final double z, final double range) {
        for (int i = 0; i < this.playerEntities.size(); ++i) {
            final EntityPlayer entityplayer = this.playerEntities.get(i);

            if (EntitySelectors.NOT_SPECTATING.apply(entityplayer)) {
                final double d0 = entityplayer.getDistanceSq(x, y, z);

                if (range < 0.0D || d0 < range * range) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Find a player by name in this world.
     */
    public EntityPlayer getPlayerEntityByName(final String name) {
        for (int i = 0; i < this.playerEntities.size(); ++i) {
            final EntityPlayer entityplayer = this.playerEntities.get(i);

            if (name.equals(entityplayer.getCommandSenderName())) {
                return entityplayer;
            }
        }

        return null;
    }

    public EntityPlayer getPlayerEntityByUUID(final UUID uuid) {
        for (int i = 0; i < this.playerEntities.size(); ++i) {
            final EntityPlayer entityplayer = this.playerEntities.get(i);

            if (uuid.equals(entityplayer.getUniqueID())) {
                return entityplayer;
            }
        }

        return null;
    }

    /**
     * If on MP, sends a quitting packet.
     */
    public void sendQuittingDisconnectingPacket() {
    }

    /**
     * Checks whether the session lock file was modified by another process
     */
    public void checkSessionLock() throws MinecraftException {
        this.saveHandler.checkSessionLock();
    }

    public void setTotalWorldTime(final long worldTime) {
        this.worldInfo.setWorldTotalTime(worldTime);
    }

    /**
     * gets the random world seed
     */
    public long getSeed() {
        return this.worldInfo.getSeed();
    }

    public long getTotalWorldTime() {
        return this.worldInfo.getWorldTotalTime();
    }

    public long getWorldTime() {
        return this.worldInfo.getWorldTime();
    }

    /**
     * Sets the world time.
     */
    public void setWorldTime(final long time) {
        this.worldInfo.setWorldTime(time);
    }

    /**
     * Gets the spawn point in the world
     */
    public BlockPos getSpawnPoint() {
        BlockPos blockpos = new BlockPos(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());

        if (!this.getWorldBorder().contains(blockpos)) {
            blockpos = this.getHeight(new BlockPos(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
        }

        return blockpos;
    }

    public void setSpawnPoint(final BlockPos pos) {
        this.worldInfo.setSpawn(pos);
    }

    /**
     * spwans an entity and loads surrounding chunks
     */
    public void joinEntityInSurroundings(final Entity entityIn) {
        final int i = MathHelper.floor_double(entityIn.posX / 16.0D);
        final int j = MathHelper.floor_double(entityIn.posZ / 16.0D);
        final int k = 2;

        for (int l = i - k; l <= i + k; ++l) {
            for (int i1 = j - k; i1 <= j + k; ++i1) {
                this.getChunkFromChunkCoords(l, i1);
            }
        }

        if (!this.loadedEntityList.contains(entityIn)) {
            this.loadedEntityList.add(entityIn);
        }
    }

    public boolean isBlockModifiable(final EntityPlayer player, final BlockPos pos) {
        return true;
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    public void setEntityState(final Entity entityIn, final byte state) {
    }

    /**
     * gets the world's chunk provider
     */
    public IChunkProvider getChunkProvider() {
        return this.chunkProvider;
    }

    public void addBlockEvent(final BlockPos pos, final Block blockIn, final int eventID, final int eventParam) {
        blockIn.onBlockEventReceived(this, pos, this.getBlockState(pos), eventID, eventParam);
    }

    /**
     * Returns this world's current save handler
     */
    public ISaveHandler getSaveHandler() {
        return this.saveHandler;
    }

    /**
     * Returns the world's WorldInfo object
     */
    public WorldInfo getWorldInfo() {
        return this.worldInfo;
    }

    /**
     * Gets the GameRules instance.
     */
    public GameRules getGameRules() {
        return this.worldInfo.getGameRulesInstance();
    }

    /**
     * Updates the flag that indicates whether or not all players in the world are sleeping.
     */
    public void updateAllPlayersSleepingFlag() {
    }

    public float getThunderStrength(final float delta) {
        return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * delta) * this.getRainStrength(delta);
    }

    /**
     * Sets the strength of the thunder.
     */
    public void setThunderStrength(final float strength) {
        this.prevThunderingStrength = strength;
        this.thunderingStrength = strength;
    }

    /**
     * Returns rain strength.
     */
    public float getRainStrength(final float delta) {
        return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * delta;
    }

    /**
     * Sets the strength of the rain.
     */
    public void setRainStrength(final float strength) {
        this.prevRainingStrength = strength;
        this.rainingStrength = strength;
    }

    /**
     * Returns true if the current thunder strength (weighted with the rain strength) is greater than 0.9
     */
    public boolean isThundering() {
        return (double) this.getThunderStrength(1.0F) > 0.9D;
    }

    /**
     * Returns true if the current rain strength is greater than 0.2
     */
    public boolean isRaining() {
        return (double) this.getRainStrength(1.0F) > 0.2D;
    }

    public boolean canLightningStrike(final BlockPos strikePosition) {
        if (!this.isRaining()) {
            return false;
        } else if (!this.canSeeSky(strikePosition)) {
            return false;
        } else if (this.getPrecipitationHeight(strikePosition).getY() > strikePosition.getY()) {
            return false;
        } else {
            final BiomeGenBase biomegenbase = this.getBiomeGenForCoords(strikePosition);
            return !biomegenbase.getEnableSnow() && (!this.canSnowAt(strikePosition, false) && biomegenbase.canSpawnLightningBolt());
        }
    }

    public boolean isBlockinHighHumidity(final BlockPos pos) {
        final BiomeGenBase biomegenbase = this.getBiomeGenForCoords(pos);
        return biomegenbase.isHighHumidity();
    }

    public MapStorage getMapStorage() {
        return this.mapStorage;
    }

    /**
     * Assigns the given String id to the given MapDataBase using the MapStorage, removing any existing ones of the same
     * id.
     */
    public void setItemData(final String dataID, final WorldSavedData worldSavedDataIn) {
        this.mapStorage.setData(dataID, worldSavedDataIn);
    }

    /**
     * Loads an existing MapDataBase corresponding to the given String id from disk using the MapStorage, instantiating
     * the given Class, or returns null if none such file exists. args: Class to instantiate, String dataid
     */
    public WorldSavedData loadItemData(final Class<? extends WorldSavedData> clazz, final String dataID) {
        return this.mapStorage.loadData(clazz, dataID);
    }

    /**
     * Returns an unique new data id from the MapStorage for the given prefix and saves the idCounts map to the
     * 'idcounts' file.
     */
    public int getUniqueDataId(final String key) {
        return this.mapStorage.getUniqueDataId(key);
    }

    public void playBroadcastSound(final int p_175669_1_, final BlockPos pos, final int p_175669_3_) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            this.worldAccesses.get(i).broadcastSound(p_175669_1_, pos, p_175669_3_);
        }
    }

    public void playAuxSFX(final int p_175718_1_, final BlockPos pos, final int p_175718_3_) {
        this.playAuxSFXAtEntity(null, p_175718_1_, pos, p_175718_3_);
    }

    public void playAuxSFXAtEntity(final EntityPlayer player, final int sfxType, final BlockPos pos, final int p_180498_4_) {
        try {
            for (int i = 0; i < this.worldAccesses.size(); ++i) {
                this.worldAccesses.get(i).playAuxSFX(player, sfxType, pos, p_180498_4_);
            }
        } catch (final Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Playing level event");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Level event being played");
            crashreportcategory.addCrashSection("Block coordinates", CrashReportCategory.getCoordinateInfo(pos));
            crashreportcategory.addCrashSection("Event source", player);
            crashreportcategory.addCrashSection("Event type", Integer.valueOf(sfxType));
            crashreportcategory.addCrashSection("Event data", Integer.valueOf(p_180498_4_));
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Returns maximum world height.
     */
    public int getHeight() {
        return 256;
    }

    /**
     * Returns current world height.
     */
    public int getActualHeight() {
        return this.provider.getHasNoSky() ? 128 : 256;
    }

    /**
     * puts the World Random seed to a specific state dependant on the inputs
     */
    public Random setRandomSeed(final int p_72843_1_, final int p_72843_2_, final int p_72843_3_) {
        final long i = (long) p_72843_1_ * 341873128712L + (long) p_72843_2_ * 132897987541L + this.getWorldInfo().getSeed() + (long) p_72843_3_;
        this.rand.setSeed(i);
        return this.rand;
    }

    public BlockPos getStrongholdPos(final String name, final BlockPos pos) {
        return this.getChunkProvider().getStrongholdGen(this, name, pos);
    }

    /**
     * set by !chunk.getAreLevelsEmpty
     */
    public boolean extendedLevelsInChunkCache() {
        return false;
    }

    /**
     * Returns horizon height for use in rendering the sky.
     */
    public double getHorizon() {
        return 0.0D;
    }

    /**
     * Adds some basic stats of the world to the given crash report.
     */
    public CrashReportCategory addWorldInfoToCrashReport(final CrashReport report) {
        final CrashReportCategory crashreportcategory = report.makeCategoryDepth("Affected level", 1);
        crashreportcategory.addCrashSection("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
        crashreportcategory.addCrashSectionCallable("All players", new Callable<String>() {
            public String call() {
                return World.this.playerEntities.size() + " total; " + World.this.playerEntities;
            }
        });
        crashreportcategory.addCrashSectionCallable("Chunk stats", new Callable<String>() {
            public String call() {
                return World.this.chunkProvider.makeString();
            }
        });

        try {
            this.worldInfo.addToCrashReport(crashreportcategory);
        } catch (final Throwable throwable) {
            crashreportcategory.addCrashSectionThrowable("Level Data Unobtainable", throwable);
        }

        return crashreportcategory;
    }

    public void sendBlockBreakProgress(final int breakerId, final BlockPos pos, final int progress) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            final IWorldAccess iworldaccess = this.worldAccesses.get(i);
            iworldaccess.sendBlockBreakProgress(breakerId, pos, progress);
        }
    }

    /**
     * returns a calendar object containing the current date
     */
    public Calendar getCurrentDate() {
        if (this.getTotalWorldTime() % 600L == 0L) {
            this.theCalendar.setTimeInMillis(MinecraftServer.getCurrentTimeMillis());
        }

        return this.theCalendar;
    }

    public void makeFireworks(final double x, final double y, final double z, final double motionX, final double motionY, final double motionZ, final NBTTagCompound compund) {
    }

    public Scoreboard getScoreboard() {
        return this.worldScoreboard;
    }

    public void updateComparatorOutputLevel(final BlockPos pos, final Block blockIn) {
        for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.offset(enumfacing);

            if (this.isBlockLoaded(blockpos)) {
                IBlockState iblockstate = this.getBlockState(blockpos);

                if (Blocks.unpowered_comparator.isAssociated(iblockstate.getBlock())) {
                    iblockstate.getBlock().onNeighborBlockChange(this, blockpos, iblockstate, blockIn);
                } else if (iblockstate.getBlock().isNormalCube()) {
                    blockpos = blockpos.offset(enumfacing);
                    iblockstate = this.getBlockState(blockpos);

                    if (Blocks.unpowered_comparator.isAssociated(iblockstate.getBlock())) {
                        iblockstate.getBlock().onNeighborBlockChange(this, blockpos, iblockstate, blockIn);
                    }
                }
            }
        }
    }

    public DifficultyInstance getDifficultyForLocation(final BlockPos pos) {
        long i = 0L;
        float f = 0.0F;

        if (this.isBlockLoaded(pos)) {
            f = this.getCurrentMoonPhaseFactor();
            i = this.getChunkFromBlockCoords(pos).getInhabitedTime();
        }

        return new DifficultyInstance(this.getDifficulty(), this.getWorldTime(), i, f);
    }

    public EnumDifficulty getDifficulty() {
        return this.getWorldInfo().getDifficulty();
    }

    public int getSkylightSubtracted() {
        return this.skylightSubtracted;
    }

    public void setSkylightSubtracted(final int newSkylightSubtracted) {
        this.skylightSubtracted = newSkylightSubtracted;
    }

    public int getLastLightningBolt() {
        return this.lastLightningBolt;
    }

    public void setLastLightningBolt(final int lastLightningBoltIn) {
        this.lastLightningBolt = lastLightningBoltIn;
    }

    public boolean isFindingSpawnPoint() {
        return this.findingSpawnPoint;
    }

    public VillageCollection getVillageCollection() {
        return this.villageCollectionObj;
    }

    public WorldBorder getWorldBorder() {
        return this.worldBorder;
    }

    /**
     * Returns true if the chunk is located near the spawn point
     */
    public boolean isSpawnChunk(final int x, final int z) {
        final BlockPos blockpos = this.getSpawnPoint();
        final int i = x * 16 + 8 - blockpos.getX();
        final int j = z * 16 + 8 - blockpos.getZ();
        final int k = 128;
        return i >= -k && i <= k && j >= -k && j <= k;
    }

    public void update(Entity entity) {

        if (entity.ridingEntity != null) {
            if (!entity.ridingEntity.isDead && entity.ridingEntity.riddenByEntity == entity) {
                return;
            }

            entity.ridingEntity.riddenByEntity = null;
            entity.ridingEntity = null;
        }

        this.theProfiler.startSection("tick");

        if (!entity.isDead) {
            try {
                this.updateEntity(entity);
            } catch (final Throwable throwable1) {
//                final CrashReport crashreport1 = CrashReport.makeCrashReport(throwable1, "Ticking entity");
//                final CrashReportCategory crashreportcategory2 = crashreport1.makeCategory("Entity being ticked");
//                entity.addEntityCrashInfo(crashreportcategory2);
//                throw new ReportedException(crashreport1);
            }
        }

    }
}
