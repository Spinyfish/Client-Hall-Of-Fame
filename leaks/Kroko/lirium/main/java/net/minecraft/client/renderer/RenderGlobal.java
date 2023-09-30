package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import de.lirium.Client;
import de.lirium.base.chunk.ChunkSystem;
import de.lirium.impl.events.IsOutlineEvent;
import de.lirium.impl.events.RenderGlobalEvent;
import de.lirium.impl.events.ShaderRenderEvent;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import optifine.ChunkUtils;
import optifine.CloudRenderer;
import optifine.Config;
import optifine.CustomColors;
import optifine.CustomSky;
import optifine.DynamicLights;
import optifine.Lagometer;
import optifine.RandomMobs;
import optifine.Reflector;
import optifine.RenderEnv;
import optifine.RenderInfoLazy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersRender;
import shadersmod.client.ShadowUtils;

public class RenderGlobal implements IWorldEventListener, IResourceManagerReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation MOON_PHASES_TEXTURES = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation SUN_TEXTURES = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation CLOUDS_TEXTURES = new ResourceLocation("textures/environment/clouds.png");
    private static final ResourceLocation END_SKY_TEXTURES = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation FORCEFIELD_TEXTURES = new ResourceLocation("textures/misc/forcefield.png");

    /**
     * A reference to the Minecraft object.
     */
    public final Minecraft mc;

    /**
     * The RenderEngine instance used by RenderGlobal
     */
    private final TextureManager renderEngine;
    private final RenderManager renderManager;
    private WorldClient theWorld;
    private Set<RenderChunk> chunksToUpdate = Sets.<RenderChunk>newLinkedHashSet();
    private List<RenderGlobal.ContainerLocalRenderInformation> renderInfos = Lists.<RenderGlobal.ContainerLocalRenderInformation>newArrayListWithCapacity(69696);
    private final Set<TileEntity> setTileEntities = Sets.<TileEntity>newHashSet();
    private ViewFrustum viewFrustum;

    /**
     * The star GL Call list
     */
    private int starGLCallList = -1;

    /**
     * OpenGL sky list
     */
    private int glSkyList = -1;

    /**
     * OpenGL sky list 2
     */
    private int glSkyList2 = -1;
    private final VertexFormat vertexBufferFormat;
    private VertexBuffer starVBO;
    private VertexBuffer skyVBO;
    private VertexBuffer sky2VBO;

    /**
     * counts the cloud render updates. Used with mod to stagger some updates
     */
    private int cloudTickCounter;
    public final Map<Integer, DestroyBlockProgress> damagedBlocks = Maps.<Integer, DestroyBlockProgress>newHashMap();
    private final Map<BlockPos, ISound> mapSoundPositions = Maps.<BlockPos, ISound>newHashMap();
    private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];
    private Framebuffer entityOutlineFramebuffer;

    /**
     * Stores the shader group for the entity_outline shader
     */
    private ShaderGroup entityOutlineShader;
    private double frustumUpdatePosX = Double.MIN_VALUE;
    private double frustumUpdatePosY = Double.MIN_VALUE;
    private double frustumUpdatePosZ = Double.MIN_VALUE;
    private int frustumUpdatePosChunkX = Integer.MIN_VALUE;
    private int frustumUpdatePosChunkY = Integer.MIN_VALUE;
    private int frustumUpdatePosChunkZ = Integer.MIN_VALUE;
    private double lastViewEntityX = Double.MIN_VALUE;
    private double lastViewEntityY = Double.MIN_VALUE;
    private double lastViewEntityZ = Double.MIN_VALUE;
    private double lastViewEntityPitch = Double.MIN_VALUE;
    private double lastViewEntityYaw = Double.MIN_VALUE;
    private ChunkRenderDispatcher renderDispatcher;
    private ChunkRenderContainer renderContainer;
    private int renderDistanceChunks = -1;

    /**
     * Render entities startup counter (init value=2)
     */
    private int renderEntitiesStartupCounter = 2;

    /**
     * Count entities total
     */
    private int countEntitiesTotal;

    /**
     * Count entities rendered
     */
    private int countEntitiesRendered;

    /**
     * Count entities hidden
     */
    private int countEntitiesHidden;
    private boolean debugFixTerrainFrustum;
    private ClippingHelper debugFixedClippingHelper;
    private final Vector4f[] debugTerrainMatrix = new Vector4f[8];
    private final Vector3d debugTerrainFrustumPosition = new Vector3d();
    private boolean vboEnabled;
    IRenderChunkFactory renderChunkFactory;
    private double prevRenderSortX;
    private double prevRenderSortY;
    private double prevRenderSortZ;
    public boolean displayListEntitiesDirty = true;
    private boolean entityOutlinesRendered;
    private final Set<BlockPos> setLightUpdates = Sets.<BlockPos>newHashSet();
    private CloudRenderer cloudRenderer;
    public Entity renderedEntity;
    public Set chunksToResortTransparency = new LinkedHashSet();
    public Set chunksToUpdateForced = new LinkedHashSet();
    private Deque visibilityDeque = new ArrayDeque();
    private List renderInfosEntities = new ArrayList(1024);
    private List renderInfosTileEntities = new ArrayList(1024);
    private List renderInfosNormal = new ArrayList(1024);
    private List renderInfosEntitiesNormal = new ArrayList(1024);
    private List renderInfosTileEntitiesNormal = new ArrayList(1024);
    private List renderInfosShadow = new ArrayList(1024);
    private List renderInfosEntitiesShadow = new ArrayList(1024);
    private List renderInfosTileEntitiesShadow = new ArrayList(1024);
    private int renderDistance = 0;
    private int renderDistanceSq = 0;
    private static final Set SET_ALL_FACINGS = Collections.unmodifiableSet(new HashSet(Arrays.asList(EnumFacing.VALUES)));
    private int countTileEntitiesRendered;
    private IChunkProvider worldChunkProvider = null;
    private Long2ObjectMap<Chunk> worldChunkProviderMap = null;
    private int countLoadedChunksPrev = 0;
    private RenderEnv renderEnv;
    public boolean renderOverlayDamaged;
    public boolean renderOverlayEyes;
    static Deque<RenderGlobal.ContainerLocalRenderInformation> renderInfoCache = new ArrayDeque<RenderGlobal.ContainerLocalRenderInformation>();

    public RenderGlobal(Minecraft mcIn) {
        this.renderEnv = new RenderEnv(this.theWorld, Blocks.AIR.getDefaultState(), new BlockPos(0, 0, 0));
        this.renderOverlayDamaged = false;
        this.renderOverlayEyes = false;
        this.cloudRenderer = new CloudRenderer(mcIn);
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.renderEngine = mcIn.getTextureManager();
        this.renderEngine.bindTexture(FORCEFIELD_TEXTURES);
        GlStateManager.glTexParameteri(3553, 10242, 10497);
        GlStateManager.glTexParameteri(3553, 10243, 10497);
        GlStateManager.bindTexture(0);
        this.updateDestroyBlockIcons();
        this.vboEnabled = OpenGlHelper.useVbo();

        if (this.vboEnabled) {
            this.renderContainer = new VboRenderList();
            this.renderChunkFactory = new VboChunkFactory();
        } else {
            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactory();
        }

        this.vertexBufferFormat = new VertexFormat();
        this.vertexBufferFormat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
        this.generateStars();
        this.generateSky();
        this.generateSky2();
    }

    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.updateDestroyBlockIcons();
    }

    private void updateDestroyBlockIcons() {
        TextureMap texturemap = this.mc.getTextureMapBlocks();

        for (int i = 0; i < this.destroyBlockIcons.length; ++i) {
            this.destroyBlockIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
        }
    }

    /**
     * Creates the entity outline shader to be stored in RenderGlobal.entityOutlineShader
     */
    public void makeEntityOutlineShader() {
        if (OpenGlHelper.shadersSupported) {
            if (ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
                ShaderLinkHelper.setNewStaticShaderLinkHelper();
            }

            ResourceLocation resourcelocation = new ResourceLocation("shaders/post/entity_outline.json");

            try {
                this.entityOutlineShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocation);
                this.entityOutlineShader.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                this.entityOutlineFramebuffer = this.entityOutlineShader.getFramebufferRaw("final");
            } catch (IOException ioexception) {
                LOGGER.warn("Failed to load shader: {}", resourcelocation, ioexception);
                this.entityOutlineShader = null;
                this.entityOutlineFramebuffer = null;
            } catch (JsonSyntaxException jsonsyntaxexception) {
                LOGGER.warn("Failed to load shader: {}", resourcelocation, jsonsyntaxexception);
                this.entityOutlineShader = null;
                this.entityOutlineFramebuffer = null;
            }
        } else {
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
        }
    }

    public void renderEntityOutlineFramebuffer() {
        if (this.isRenderEntityOutlines()) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ZERO);
            this.entityOutlineFramebuffer.framebufferRenderExt(this.mc.displayWidth, this.mc.displayHeight, false);
            GlStateManager.disableBlend();
        }
    }

    public boolean isRenderEntityOutlines() {
        if (!Config.isFastRender() && !Config.isShaders() && !Config.isAntialiasing()) {
            return this.entityOutlineFramebuffer != null && this.entityOutlineShader != null && this.mc.player != null;
        } else {
            return false;
        }
    }

    private void generateSky2() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        if (this.sky2VBO != null) {
            this.sky2VBO.deleteGlBuffers();
        }

        if (this.glSkyList2 >= 0) {
            GLAllocation.deleteDisplayLists(this.glSkyList2);
            this.glSkyList2 = -1;
        }

        if (this.vboEnabled) {
            this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderSky(bufferbuilder, -16.0F, true);
            bufferbuilder.finishDrawing();
            bufferbuilder.reset();
            this.sky2VBO.bufferData(bufferbuilder.getByteBuffer());
        } else {
            this.glSkyList2 = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(this.glSkyList2, 4864);
            this.renderSky(bufferbuilder, -16.0F, true);
            tessellator.draw();
            GlStateManager.glEndList();
        }
    }

    private void generateSky() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        if (this.skyVBO != null) {
            this.skyVBO.deleteGlBuffers();
        }

        if (this.glSkyList >= 0) {
            GLAllocation.deleteDisplayLists(this.glSkyList);
            this.glSkyList = -1;
        }

        if (this.vboEnabled) {
            this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderSky(bufferbuilder, 16.0F, false);
            bufferbuilder.finishDrawing();
            bufferbuilder.reset();
            this.skyVBO.bufferData(bufferbuilder.getByteBuffer());
        } else {
            this.glSkyList = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(this.glSkyList, 4864);
            this.renderSky(bufferbuilder, 16.0F, false);
            tessellator.draw();
            GlStateManager.glEndList();
        }
    }

    private void renderSky(BufferBuilder worldRendererIn, float posY, boolean reverseX) {
        int i = 64;
        int j = 6;
        worldRendererIn.begin(7, DefaultVertexFormats.POSITION);

        for (int k = -384; k <= 384; k += 64) {
            for (int l = -384; l <= 384; l += 64) {
                float f = (float) k;
                float f1 = (float) (k + 64);

                if (reverseX) {
                    f1 = (float) k;
                    f = (float) (k + 64);
                }

                worldRendererIn.pos((double) f, (double) posY, (double) l).endVertex();
                worldRendererIn.pos((double) f1, (double) posY, (double) l).endVertex();
                worldRendererIn.pos((double) f1, (double) posY, (double) (l + 64)).endVertex();
                worldRendererIn.pos((double) f, (double) posY, (double) (l + 64)).endVertex();
            }
        }
    }

    private void generateStars() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        if (this.starVBO != null) {
            this.starVBO.deleteGlBuffers();
        }

        if (this.starGLCallList >= 0) {
            GLAllocation.deleteDisplayLists(this.starGLCallList);
            this.starGLCallList = -1;
        }

        if (this.vboEnabled) {
            this.starVBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderStars(bufferbuilder);
            bufferbuilder.finishDrawing();
            bufferbuilder.reset();
            this.starVBO.bufferData(bufferbuilder.getByteBuffer());
        } else {
            this.starGLCallList = GLAllocation.generateDisplayLists(1);
            GlStateManager.pushMatrix();
            GlStateManager.glNewList(this.starGLCallList, 4864);
            this.renderStars(bufferbuilder);
            tessellator.draw();
            GlStateManager.glEndList();
            GlStateManager.popMatrix();
        }
    }

    private void renderStars(BufferBuilder worldRendererIn) {
        Random random = new Random(10842L);
        worldRendererIn.begin(7, DefaultVertexFormats.POSITION);

        for (int i = 0; i < 1500; ++i) {
            double d0 = (double) (random.nextFloat() * 2.0F - 1.0F);
            double d1 = (double) (random.nextFloat() * 2.0F - 1.0F);
            double d2 = (double) (random.nextFloat() * 2.0F - 1.0F);
            double d3 = (double) (0.15F + random.nextFloat() * 0.1F);
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d4 < 1.0D && d4 > 0.01D) {
                d4 = 1.0D / Math.sqrt(d4);
                d0 = d0 * d4;
                d1 = d1 * d4;
                d2 = d2 * d4;
                double d5 = d0 * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                for (int j = 0; j < 4; ++j) {
                    double d17 = 0.0D;
                    double d18 = (double) ((j & 2) - 1) * d3;
                    double d19 = (double) ((j + 1 & 2) - 1) * d3;
                    double d20 = 0.0D;
                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0D * d13;
                    double d24 = 0.0D * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    worldRendererIn.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
                }
            }
        }
    }

    /**
     * set null to clear
     */
    public void setWorldAndLoadRenderers(@Nullable WorldClient worldClientIn) {
        if (this.theWorld != null) {
            this.theWorld.removeEventListener(this);
        }

        this.frustumUpdatePosX = Double.MIN_VALUE;
        this.frustumUpdatePosY = Double.MIN_VALUE;
        this.frustumUpdatePosZ = Double.MIN_VALUE;
        this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
        this.renderManager.set(worldClientIn);
        this.theWorld = worldClientIn;

        if (Config.isDynamicLights()) {
            DynamicLights.clear();
        }

        if (worldClientIn != null) {
            worldClientIn.addEventListener(this);
            this.loadRenderers();
        } else {
            this.chunksToUpdate.clear();
            this.renderInfos.clear();

            if (this.viewFrustum != null) {
                this.viewFrustum.deleteGlResources();
                this.viewFrustum = null;
            }

            if (this.renderDispatcher != null) {
                this.renderDispatcher.stopWorkerThreads();
            }

            this.renderDispatcher = null;
        }
    }

    /**
     * Loads all the renderers and sets up the basic settings usage
     */
    public void loadRenderers() {
        ChunkSystem.blocks.clear();
        if (this.theWorld != null) {
            if (this.renderDispatcher == null) {
                this.renderDispatcher = new ChunkRenderDispatcher();
            }

            this.displayListEntitiesDirty = true;
            Blocks.LEAVES.setGraphicsLevel(Config.isTreesFancy());
            Blocks.LEAVES2.setGraphicsLevel(Config.isTreesFancy());
            BlockModelRenderer.updateAoLightValue();
            renderInfoCache.clear();

            if (Config.isDynamicLights()) {
                DynamicLights.clear();
            }

            this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
            this.renderDistance = this.renderDistanceChunks * 16;
            this.renderDistanceSq = this.renderDistance * this.renderDistance;
            boolean flag = this.vboEnabled;
            this.vboEnabled = OpenGlHelper.useVbo();

            if (flag && !this.vboEnabled) {
                this.renderContainer = new RenderList();
                this.renderChunkFactory = new ListChunkFactory();
            } else if (!flag && this.vboEnabled) {
                this.renderContainer = new VboRenderList();
                this.renderChunkFactory = new VboChunkFactory();
            }

            if (flag != this.vboEnabled) {
                this.generateStars();
                this.generateSky();
                this.generateSky2();
            }

            if (this.viewFrustum != null) {
                this.viewFrustum.deleteGlResources();
            }

            this.stopChunkUpdates();

            synchronized (this.setTileEntities) {
                this.setTileEntities.clear();
            }

            this.viewFrustum = new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);

            if (this.theWorld != null) {
                Entity entity = this.mc.getRenderViewEntity();

                if (entity != null) {
                    this.viewFrustum.updateChunkPositions(entity.posX, entity.posZ);
                }
            }

            this.renderEntitiesStartupCounter = 2;
        }
    }

    protected void stopChunkUpdates() {
        this.chunksToUpdate.clear();
        this.renderDispatcher.stopChunkUpdates();
    }

    public void createBindEntityOutlineFbs(int width, int height) {
        if (OpenGlHelper.shadersSupported && this.entityOutlineShader != null) {
            this.entityOutlineShader.createBindFramebuffers(width, height);
        }
    }

    public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks) {
        int i = 0;

        if (Reflector.MinecraftForgeClient_getRenderPass.exists()) {
            i = Reflector.callInt(Reflector.MinecraftForgeClient_getRenderPass);
        }

        if (this.renderEntitiesStartupCounter > 0) {
            if (i > 0) {
                return;
            }

            --this.renderEntitiesStartupCounter;
        } else {
            double d0 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double) partialTicks;
            double d1 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double) partialTicks;
            double d2 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double) partialTicks;
            this.theWorld.theProfiler.startSection("prepare");
            TileEntityRendererDispatcher.instance.prepare(this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.objectMouseOver, partialTicks);
            this.renderManager.cacheActiveRenderInfo(this.theWorld, this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks);

            if (i == 0) {
                this.countEntitiesTotal = 0;
                this.countEntitiesRendered = 0;
                this.countEntitiesHidden = 0;
                this.countTileEntitiesRendered = 0;
            }

            Entity entity = this.mc.getRenderViewEntity();
            double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
            double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
            double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
            TileEntityRendererDispatcher.staticPlayerX = d3;
            TileEntityRendererDispatcher.staticPlayerY = d4;
            TileEntityRendererDispatcher.staticPlayerZ = d5;
            this.renderManager.setRenderPosition(d3, d4, d5);
            this.mc.entityRenderer.enableLightmap();
            this.theWorld.theProfiler.endStartSection("global");
            List<Entity> list = this.theWorld.getLoadedEntityList();

            if (i == 0) {
                this.countEntitiesTotal = list.size();
            }

            if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
                GlStateManager.disableFog();
            }

            boolean flag = Reflector.ForgeEntity_shouldRenderInPass.exists();
            boolean flag1 = Reflector.ForgeTileEntity_shouldRenderInPass.exists();

            for (int j = 0; j < this.theWorld.weatherEffects.size(); ++j) {
                Entity entity1 = this.theWorld.weatherEffects.get(j);

                if (!flag || Reflector.callBoolean(entity1, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                    ++this.countEntitiesRendered;

                    if (entity1.isInRangeToRender3d(d0, d1, d2)) {
                        this.renderManager.renderEntityStatic(entity1, partialTicks, false);
                    }
                }
            }

            this.theWorld.theProfiler.endStartSection("entities");
            boolean flag4 = Config.isShaders();

            if (flag4) {
                Shaders.beginEntities();
            }

            boolean flag5 = this.mc.gameSettings.fancyGraphics;
            this.mc.gameSettings.fancyGraphics = Config.isDroppedItemsFancy();
            List<Entity> list1 = Lists.<Entity>newArrayList();
            List<Entity> list2 = Lists.<Entity>newArrayList();
            BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

            Client.INSTANCE.getEventBus().call(new RenderGlobalEvent());

            for (Object renderglobal$containerlocalrenderinformation0 : this.renderInfosEntities) {
                RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation = (RenderGlobal.ContainerLocalRenderInformation) renderglobal$containerlocalrenderinformation0;
                Chunk chunk = renderglobal$containerlocalrenderinformation.renderChunk.getChunk(this.theWorld);
                ClassInheritanceMultiMap<Entity> classinheritancemultimap = chunk.getEntityLists()[renderglobal$containerlocalrenderinformation.renderChunk.getPosition().getY() / 16];

                if (!classinheritancemultimap.isEmpty()) {
                    for (Entity entity2 : classinheritancemultimap) {
                        if (!flag || Reflector.callBoolean(entity2, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                            boolean flag2 = this.renderManager.shouldRender(entity2, camera, d0, d1, d2) || entity2.isRidingOrBeingRiddenBy(this.mc.player);

                            if (flag2) {
                                boolean flag3 = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) this.mc.getRenderViewEntity()).isPlayerSleeping();

                                if ((entity2 != this.mc.getRenderViewEntity() || this.mc.gameSettings.thirdPersonView != 0 || flag3) && (entity2.posY < 0.0D || entity2.posY >= 256.0D || this.theWorld.isBlockLoaded(blockpos$pooledmutableblockpos.setPos(entity2)))) {
                                    ++this.countEntitiesRendered;
                                    this.renderedEntity = entity2;

                                    if (flag4) {
                                        Shaders.nextEntity(entity2);
                                    }
                                    this.renderManager.renderEntityStatic(entity2, partialTicks, false);
                                    this.renderedEntity = null;

                                    if (this.isOutlineActive(entity2, entity, camera)) {
                                        list1.add(entity2);
                                    }

                                    if (this.renderManager.isRenderMultipass(entity2)) {
                                        list2.add(entity2);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            blockpos$pooledmutableblockpos.release();

            if (!list2.isEmpty()) {
                for (Entity entity3 : list2) {
                    if (!flag || Reflector.callBoolean(entity3, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                        if (flag4) {
                            Shaders.nextEntity(entity3);
                        }

                        this.renderManager.renderMultipass(entity3, partialTicks);
                    }
                }
            }
            boolean cleared = false;
            if (i == 0) {
                this.theWorld.theProfiler.endStartSection("other outlines");
                if (!cleared) {
                    this.entityOutlineFramebuffer.framebufferClear();
                    cleared = true;
                }
                GlStateManager.depthFunc(519);
                GlStateManager.disableFog();
                this.entityOutlineFramebuffer.bindFramebuffer(false);
                RenderHelper.disableStandardItemLighting();
                this.renderManager.setRenderOutlines(true);

                Client.INSTANCE.getEventBus().call(new ShaderRenderEvent());

                this.renderManager.setRenderOutlines(false);
                RenderHelper.enableStandardItemLighting();
                GlStateManager.depthMask(false);
                this.entityOutlineShader.loadShaderGroup(partialTicks);
                GlStateManager.enableLighting();
                GlStateManager.depthMask(true);
                GlStateManager.enableFog();
                GlStateManager.enableBlend();
                GlStateManager.enableColorMaterial();
                GlStateManager.depthFunc(515);
                GlStateManager.enableDepth();
                GlStateManager.enableAlpha();
                this.mc.getFramebuffer().bindFramebuffer(false);
            }

            if (i == 0 && this.isRenderEntityOutlines() && (!list1.isEmpty() || this.entityOutlinesRendered)) {
                this.theWorld.theProfiler.endStartSection("entityOutlines");
                if (!cleared) {
                    this.entityOutlineFramebuffer.framebufferClear();
                    cleared = true;
                }
                this.entityOutlinesRendered = !list1.isEmpty();

                if (!list1.isEmpty()) {
                    GlStateManager.depthFunc(519);
                    GlStateManager.disableFog();
                    this.entityOutlineFramebuffer.bindFramebuffer(false);
                    RenderHelper.disableStandardItemLighting();
                    this.renderManager.setRenderOutlines(true);


                    for (int k = 0; k < list1.size(); ++k) {
                        Entity entity4 = list1.get(k);

                        if (!flag || Reflector.callBoolean(entity4, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                            if (flag4) {
                                Shaders.nextEntity(entity4);
                            }

                            this.renderManager.renderEntityStatic(entity4, partialTicks, false);
                        }
                    }

                    this.renderManager.setRenderOutlines(false);
                    RenderHelper.enableStandardItemLighting();
                    GlStateManager.depthMask(false);
                    this.entityOutlineShader.loadShaderGroup(partialTicks);
                    GlStateManager.enableLighting();
                    GlStateManager.depthMask(true);
                    GlStateManager.enableFog();
                    GlStateManager.enableBlend();
                    GlStateManager.enableColorMaterial();
                    GlStateManager.depthFunc(515);
                    GlStateManager.enableDepth();
                    GlStateManager.enableAlpha();
                }

                this.mc.getFramebuffer().bindFramebuffer(false);
            }

            if (!this.isRenderEntityOutlines() && (!list1.isEmpty() || this.entityOutlinesRendered)) {
                this.theWorld.theProfiler.endStartSection("entityOutlines");
                this.entityOutlinesRendered = !list1.isEmpty();

                if (!list1.isEmpty()) {
                    GlStateManager.disableFog();
                    GlStateManager.disableDepth();
                    this.mc.entityRenderer.disableLightmap();
                    RenderHelper.disableStandardItemLighting();
                    this.renderManager.setRenderOutlines(true);


                    for (int l = 0; l < list1.size(); ++l) {
                        Entity entity5 = list1.get(l);

                        if (!flag || Reflector.callBoolean(entity5, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                            if (flag4) {
                                Shaders.nextEntity(entity5);
                            }

                            this.renderManager.renderEntityStatic(entity5, partialTicks, false);
                        }
                    }

                    this.renderManager.setRenderOutlines(false);
                    RenderHelper.enableStandardItemLighting();
                    this.mc.entityRenderer.enableLightmap();
                    GlStateManager.enableDepth();
                    GlStateManager.enableFog();
                }
            }

            this.mc.gameSettings.fancyGraphics = flag5;
            FontRenderer fontrenderer = TileEntityRendererDispatcher.instance.getFontRenderer();

            if (flag4) {
                Shaders.endEntities();
                Shaders.beginBlockEntities();
            }

            this.theWorld.theProfiler.endStartSection("blockentities");
            RenderHelper.enableStandardItemLighting();

            if (Reflector.ForgeTileEntity_hasFastRenderer.exists()) {
                TileEntityRendererDispatcher.instance.preDrawBatch();
            }

            label251:

            for (Object renderglobal$containerlocalrenderinformation10 : this.renderInfosTileEntities) {
                RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1 = (RenderGlobal.ContainerLocalRenderInformation) renderglobal$containerlocalrenderinformation10;
                List<TileEntity> list3 = renderglobal$containerlocalrenderinformation1.renderChunk.getCompiledChunk().getTileEntities();

                if (!list3.isEmpty()) {
                    Iterator iterator = list3.iterator();

                    while (true) {
                        TileEntity tileentity1;

                        while (true) {
                            if (!iterator.hasNext()) {
                                continue label251;
                            }

                            tileentity1 = (TileEntity) iterator.next();

                            if (!flag1) {
                                break;
                            }

                            if (Reflector.callBoolean(tileentity1, Reflector.ForgeTileEntity_shouldRenderInPass, i)) {
                                AxisAlignedBB axisalignedbb = (AxisAlignedBB) Reflector.call(tileentity1, Reflector.ForgeTileEntity_getRenderBoundingBox);

                                if (axisalignedbb == null || camera.isBoundingBoxInFrustum(axisalignedbb)) {
                                    break;
                                }
                            }
                        }

                        Class oclass = tileentity1.getClass();

                        if (oclass == TileEntitySign.class && !Config.zoomMode) {
                            EntityPlayer entityplayer = this.mc.player;
                            double d6 = tileentity1.getDistanceSq(entityplayer.posX, entityplayer.posY, entityplayer.posZ);

                            if (d6 > 256.0D) {
                                fontrenderer.enabled = false;
                            }
                        }

                        if (flag4) {
                            Shaders.nextBlockEntity(tileentity1);
                        }

                        TileEntityRendererDispatcher.instance.renderTileEntity(tileentity1, partialTicks, -1);
                        ++this.countTileEntitiesRendered;
                        fontrenderer.enabled = true;
                    }
                }
            }

            synchronized (this.setTileEntities) {
                for (TileEntity tileentity : this.setTileEntities) {
                    if (!flag1 || Reflector.callBoolean(tileentity, Reflector.ForgeTileEntity_shouldRenderInPass, i)) {
                        if (flag4) {
                            Shaders.nextBlockEntity(tileentity);
                        }

                        TileEntityRendererDispatcher.instance.renderTileEntity(tileentity, partialTicks, -1);
                    }
                }
            }

            if (Reflector.ForgeTileEntity_hasFastRenderer.exists()) {
                TileEntityRendererDispatcher.instance.drawBatch(i);
            }

            this.renderOverlayDamaged = true;
            this.preRenderDamagedBlocks();

            for (DestroyBlockProgress destroyblockprogress : this.damagedBlocks.values()) {
                BlockPos blockpos = destroyblockprogress.getPosition();

                if (this.theWorld.getBlockState(blockpos).getBlock().hasTileEntity()) {
                    TileEntity tileentity2 = this.theWorld.getTileEntity(blockpos);

                    if (tileentity2 instanceof TileEntityChest) {
                        TileEntityChest tileentitychest = (TileEntityChest) tileentity2;

                        if (tileentitychest.adjacentChestXNeg != null) {
                            blockpos = blockpos.offset(EnumFacing.WEST);
                            tileentity2 = this.theWorld.getTileEntity(blockpos);
                        } else if (tileentitychest.adjacentChestZNeg != null) {
                            blockpos = blockpos.offset(EnumFacing.NORTH);
                            tileentity2 = this.theWorld.getTileEntity(blockpos);
                        }
                    }

                    IBlockState iblockstate = this.theWorld.getBlockState(blockpos);

                    if (tileentity2 != null && iblockstate.func_191057_i()) {
                        if (flag4) {
                            Shaders.nextBlockEntity(tileentity2);
                        }

                        TileEntityRendererDispatcher.instance.renderTileEntity(tileentity2, partialTicks, destroyblockprogress.getPartialBlockDamage());
                    }
                }
            }

            this.postRenderDamagedBlocks();
            this.renderOverlayDamaged = false;
            this.mc.entityRenderer.disableLightmap();
            this.mc.mcProfiler.endSection();
        }
    }

    private boolean isOutlineActive(Entity p_184383_1_, Entity p_184383_2_, ICamera p_184383_3_) {
        final IsOutlineEvent isOutlineEvent = new IsOutlineEvent(false, p_184383_1_);
        Client.INSTANCE.getEventBus().call(isOutlineEvent);
        if (isOutlineEvent.isOverrideOutline())
            return true;
        boolean flag = p_184383_2_ instanceof EntityLivingBase && ((EntityLivingBase) p_184383_2_).isPlayerSleeping();

        if (p_184383_1_ == p_184383_2_ && this.mc.gameSettings.thirdPersonView == 0 && !flag) {
            return false;
        } else if (p_184383_1_.isGlowing()) {
            return true;
        } else if (this.mc.player.isSpectator() && this.mc.gameSettings.keyBindSpectatorOutlines.isKeyDown() && p_184383_1_ instanceof EntityPlayer) {
            return p_184383_1_.ignoreFrustumCheck || p_184383_3_.isBoundingBoxInFrustum(p_184383_1_.getEntityBoundingBox()) || p_184383_1_.isRidingOrBeingRiddenBy(this.mc.player);
        } else {
            return false;
        }
    }

    /**
     * Gets the render info for use on the Debug screen
     */
    public String getDebugInfoRenders() {
        int i = this.viewFrustum.renderChunks.length;
        int j = this.getRenderedChunks();
        return String.format("C: %d/%d %sD: %d, L: %d, %s", j, i, this.mc.renderChunksMany ? "(s) " : "", this.renderDistanceChunks, this.setLightUpdates.size(), this.renderDispatcher == null ? "null" : this.renderDispatcher.getDebugInfo());
    }

    protected int getRenderedChunks() {
        int i = 0;

        for (RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos) {
            CompiledChunk compiledchunk = renderglobal$containerlocalrenderinformation.renderChunk.compiledChunk;

            if (compiledchunk != CompiledChunk.DUMMY && !compiledchunk.isEmpty()) {
                ++i;
            }
        }

        return i;
    }

    /**
     * Gets the entities info for use on the Debug screen
     */
    public String getDebugInfoEntities() {
        return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ", B: " + this.countEntitiesHidden + ", " + Config.getVersionDebug();
    }

    public void setupTerrain(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {
        if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks) {
            this.loadRenderers();
        }

        this.theWorld.theProfiler.startSection("camera");
        double d0 = viewEntity.posX - this.frustumUpdatePosX;
        double d1 = viewEntity.posY - this.frustumUpdatePosY;
        double d2 = viewEntity.posZ - this.frustumUpdatePosZ;

        if (this.frustumUpdatePosChunkX != viewEntity.chunkCoordX || this.frustumUpdatePosChunkY != viewEntity.chunkCoordY || this.frustumUpdatePosChunkZ != viewEntity.chunkCoordZ || d0 * d0 + d1 * d1 + d2 * d2 > 16.0D) {
            this.frustumUpdatePosX = viewEntity.posX;
            this.frustumUpdatePosY = viewEntity.posY;
            this.frustumUpdatePosZ = viewEntity.posZ;
            this.frustumUpdatePosChunkX = viewEntity.chunkCoordX;
            this.frustumUpdatePosChunkY = viewEntity.chunkCoordY;
            this.frustumUpdatePosChunkZ = viewEntity.chunkCoordZ;
            this.viewFrustum.updateChunkPositions(viewEntity.posX, viewEntity.posZ);
        }

        if (Config.isDynamicLights()) {
            DynamicLights.update(this);
        }

        this.theWorld.theProfiler.endStartSection("renderlistcamera");
        double d3 = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
        double d4 = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
        double d5 = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
        this.renderContainer.initialize(d3, d4, d5);
        this.theWorld.theProfiler.endStartSection("cull");

        if (this.debugFixedClippingHelper != null) {
            Frustum frustum = new Frustum(this.debugFixedClippingHelper);
            frustum.setPosition(this.debugTerrainFrustumPosition.x, this.debugTerrainFrustumPosition.y, this.debugTerrainFrustumPosition.z);
            camera = frustum;
        }

        this.mc.mcProfiler.endStartSection("culling");
        BlockPos blockpos1 = new BlockPos(d3, d4 + (double) viewEntity.getEyeHeight(), d5);
        RenderChunk renderchunk = this.viewFrustum.getRenderChunk(blockpos1);
        new BlockPos(MathHelper.floor(d3 / 16.0D) * 16, MathHelper.floor(d4 / 16.0D) * 16, MathHelper.floor(d5 / 16.0D) * 16);
        this.displayListEntitiesDirty = this.displayListEntitiesDirty || !this.chunksToUpdate.isEmpty() || viewEntity.posX != this.lastViewEntityX || viewEntity.posY != this.lastViewEntityY || viewEntity.posZ != this.lastViewEntityZ || (double) viewEntity.rotationPitch != this.lastViewEntityPitch || (double) viewEntity.rotationYaw != this.lastViewEntityYaw;
        this.lastViewEntityX = viewEntity.posX;
        this.lastViewEntityY = viewEntity.posY;
        this.lastViewEntityZ = viewEntity.posZ;
        this.lastViewEntityPitch = (double) viewEntity.rotationPitch;
        this.lastViewEntityYaw = (double) viewEntity.rotationYaw;
        boolean flag = this.debugFixedClippingHelper != null;
        this.mc.mcProfiler.endStartSection("update");
        Lagometer.timerVisibility.start();
        int i = this.getCountLoadedChunks();

        if (i != this.countLoadedChunksPrev) {
            this.countLoadedChunksPrev = i;
            this.displayListEntitiesDirty = true;
        }

        if (Shaders.isShadowPass) {
            this.renderInfos = this.renderInfosShadow;
            this.renderInfosEntities = this.renderInfosEntitiesShadow;
            this.renderInfosTileEntities = this.renderInfosTileEntitiesShadow;

            if (!flag && this.displayListEntitiesDirty) {
                this.renderInfos.clear();
                this.renderInfosEntities.clear();
                this.renderInfosTileEntities.clear();
                RenderInfoLazy renderinfolazy = new RenderInfoLazy();
                Iterator<RenderChunk> iterator = ShadowUtils.makeShadowChunkIterator(this.theWorld, partialTicks, viewEntity, this.renderDistanceChunks, this.viewFrustum);

                while (iterator.hasNext()) {
                    RenderChunk renderchunk1 = iterator.next();

                    if (renderchunk1 != null) {
                        renderinfolazy.setRenderChunk(renderchunk1);

                        if (!renderchunk1.compiledChunk.isEmpty() || renderchunk1.isNeedsUpdate()) {
                            this.renderInfos.add(renderinfolazy.getRenderInfo());
                        }

                        BlockPos blockpos = renderchunk1.getPosition();

                        if (ChunkUtils.hasEntities(this.theWorld.getChunkFromBlockCoords(blockpos))) {
                            this.renderInfosEntities.add(renderinfolazy.getRenderInfo());
                        }

                        if (renderchunk1.getCompiledChunk().getTileEntities().size() > 0) {
                            this.renderInfosTileEntities.add(renderinfolazy.getRenderInfo());
                        }
                    }
                }
            }
        } else {
            this.renderInfos = this.renderInfosNormal;
            this.renderInfosEntities = this.renderInfosEntitiesNormal;
            this.renderInfosTileEntities = this.renderInfosTileEntitiesNormal;
        }

        if (!flag && this.displayListEntitiesDirty && !Shaders.isShadowPass) {
            this.displayListEntitiesDirty = false;

            for (RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1 : this.renderInfos) {
                this.freeRenderInformation(renderglobal$containerlocalrenderinformation1);
            }

            this.renderInfos.clear();
            this.renderInfosEntities.clear();
            this.renderInfosTileEntities.clear();
            this.visibilityDeque.clear();
            Deque deque = this.visibilityDeque;
            Entity.setRenderDistanceWeight(MathHelper.clamp((double) this.mc.gameSettings.renderDistanceChunks / 8.0D, 1.0D, 2.5D));
            boolean flag2 = this.mc.renderChunksMany;

            if (renderchunk != null) {
                boolean flag3 = false;
                RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation3 = new RenderGlobal.ContainerLocalRenderInformation(renderchunk, (EnumFacing) null, 0);
                Set set1 = SET_ALL_FACINGS;

                if (set1.size() == 1) {
                    Vector3f vector3f = this.getViewVector(viewEntity, partialTicks);
                    EnumFacing enumfacing = EnumFacing.getFacingFromVector(vector3f.x, vector3f.y, vector3f.z).getOpposite();
                    set1.remove(enumfacing);
                }

                if (set1.isEmpty()) {
                    flag3 = true;
                }

                if (flag3 && !playerSpectator) {
                    this.renderInfos.add(renderglobal$containerlocalrenderinformation3);
                } else {
                    if (playerSpectator && this.theWorld.getBlockState(blockpos1).isOpaqueCube()) {
                        flag2 = false;
                    }

                    renderchunk.setFrameIndex(frameCount);
                    deque.add(renderglobal$containerlocalrenderinformation3);
                }
            } else {
                int i1 = blockpos1.getY() > 0 ? 248 : 8;

                for (int j1 = -this.renderDistanceChunks; j1 <= this.renderDistanceChunks; ++j1) {
                    for (int j = -this.renderDistanceChunks; j <= this.renderDistanceChunks; ++j) {
                        RenderChunk renderchunk2 = this.viewFrustum.getRenderChunk(new BlockPos((j1 << 4) + 8, i1, (j << 4) + 8));

                        if (renderchunk2 != null && camera.isBoundingBoxInFrustum(renderchunk2.boundingBox)) {
                            renderchunk2.setFrameIndex(frameCount);
                            deque.add(new RenderGlobal.ContainerLocalRenderInformation(renderchunk2, (EnumFacing) null, 0));
                        }
                    }
                }
            }

            this.mc.mcProfiler.startSection("iteration");
            EnumFacing[] aenumfacing = EnumFacing.VALUES;
            int k1 = aenumfacing.length;

            while (!deque.isEmpty()) {
                RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation4 = (RenderGlobal.ContainerLocalRenderInformation) deque.poll();
                RenderChunk renderchunk5 = renderglobal$containerlocalrenderinformation4.renderChunk;
                EnumFacing enumfacing2 = renderglobal$containerlocalrenderinformation4.facing;
                boolean flag1 = false;
                CompiledChunk compiledchunk = renderchunk5.compiledChunk;

                if (!compiledchunk.isEmpty() || renderchunk5.isNeedsUpdate()) {
                    this.renderInfos.add(renderglobal$containerlocalrenderinformation4);
                    flag1 = true;
                }

                if (ChunkUtils.hasEntities(renderchunk5.getChunk(this.theWorld))) {
                    this.renderInfosEntities.add(renderglobal$containerlocalrenderinformation4);
                    flag1 = true;
                }

                if (compiledchunk.getTileEntities().size() > 0) {
                    this.renderInfosTileEntities.add(renderglobal$containerlocalrenderinformation4);
                    flag1 = true;
                }

                for (int k = 0; k < k1; ++k) {
                    EnumFacing enumfacing1 = aenumfacing[k];

                    if ((!flag2 || !renderglobal$containerlocalrenderinformation4.hasDirection(enumfacing1.getOpposite())) && (!flag2 || enumfacing2 == null || compiledchunk.isVisible(enumfacing2.getOpposite(), enumfacing1))) {
                        RenderChunk renderchunk3 = this.getRenderChunkOffset(blockpos1, renderchunk5, enumfacing1);

                        if (renderchunk3 != null && renderchunk3.setFrameIndex(frameCount) && camera.isBoundingBoxInFrustum(renderchunk3.boundingBox)) {
                            int l = renderglobal$containerlocalrenderinformation4.setFacing | 1 << enumfacing1.ordinal();
                            RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation = this.allocateRenderInformation(renderchunk3, enumfacing1, l);
                            deque.add(renderglobal$containerlocalrenderinformation);
                        }
                    }
                }

                if (!flag1) {
                    this.freeRenderInformation(renderglobal$containerlocalrenderinformation4);
                }
            }

            this.mc.mcProfiler.endSection();
        }

        this.mc.mcProfiler.endStartSection("captureFrustum");

        if (this.debugFixTerrainFrustum) {
            this.fixTerrainFrustum(d3, d4, d5);
            this.debugFixTerrainFrustum = false;
        }

        Lagometer.timerVisibility.end();

        if (Shaders.isShadowPass) {
            Shaders.mcProfilerEndSection();
        } else {
            this.mc.mcProfiler.endStartSection("rebuildNear");
            Set<RenderChunk> set = this.chunksToUpdate;
            this.chunksToUpdate = Sets.<RenderChunk>newLinkedHashSet();
            Lagometer.timerChunkUpdate.start();

            for (RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation2 : this.renderInfos) {
                RenderChunk renderchunk4 = renderglobal$containerlocalrenderinformation2.renderChunk;
                if (renderchunk4.isNeedsUpdate() || set.contains(renderchunk4)) {
                    this.displayListEntitiesDirty = true;
                    BlockPos blockpos2 = renderchunk4.getPosition();
                    boolean flag4 = blockpos1.distanceSq((double) (blockpos2.getX() + 8), (double) (blockpos2.getY() + 8), (double) (blockpos2.getZ() + 8)) < 768.0D;

                    if (!flag4) {
                        this.chunksToUpdate.add(renderchunk4);
                    } else if (!renderchunk4.isPlayerUpdate()) {
                        this.chunksToUpdateForced.add(renderchunk4);
                    } else {
                        this.mc.mcProfiler.startSection("build near");
                        this.renderDispatcher.updateChunkNow(renderchunk4);
                        renderchunk4.clearNeedsUpdate();
                        this.mc.mcProfiler.endSection();
                    }
                }
            }

            Lagometer.timerChunkUpdate.end();
            this.chunksToUpdate.addAll(set);
            this.mc.mcProfiler.endSection();
        }
    }

    private Set<EnumFacing> getVisibleFacings(BlockPos pos) {
        VisGraph visgraph = new VisGraph();
        BlockPos blockpos = new BlockPos(pos.getX() >> 4 << 4, pos.getY() >> 4 << 4, pos.getZ() >> 4 << 4);
        Chunk chunk = this.theWorld.getChunkFromBlockCoords(blockpos);

        for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos.add(15, 15, 15))) {
            if (chunk.getBlockState(blockpos$mutableblockpos).isOpaqueCube()) {
                visgraph.setOpaqueCube(blockpos$mutableblockpos);
            }
        }
        return visgraph.getVisibleFacings(pos);
    }

    @Nullable

    /**
     * Returns RenderChunk offset from given RenderChunk in given direction, or null if it can't be seen by player at
     * given BlockPos.
     */
    private RenderChunk getRenderChunkOffset(BlockPos playerPos, RenderChunk renderChunkBase, EnumFacing facing) {
        BlockPos blockpos = renderChunkBase.getBlockPosOffset16(facing);

        if (blockpos.getY() >= 0 && blockpos.getY() < 256) {
            int i = playerPos.getX() - blockpos.getX();
            int j = playerPos.getZ() - blockpos.getZ();

            if (Config.isFogOff()) {
                if (Math.abs(i) > this.renderDistance || Math.abs(j) > this.renderDistance) {
                    return null;
                }
            } else {
                int k = i * i + j * j;

                if (k > this.renderDistanceSq) {
                    return null;
                }
            }

            return renderChunkBase.getRenderChunkOffset16(this.viewFrustum, facing);
        } else {
            return null;
        }
    }

    private void fixTerrainFrustum(double x, double y, double z) {
        this.debugFixedClippingHelper = new ClippingHelperImpl();
        ((ClippingHelperImpl) this.debugFixedClippingHelper).init();
        Matrix4f matrix4f = new Matrix4f(this.debugFixedClippingHelper.modelviewMatrix);
        matrix4f.transpose();
        Matrix4f matrix4f1 = new Matrix4f(this.debugFixedClippingHelper.projectionMatrix);
        matrix4f1.transpose();
        Matrix4f matrix4f2 = new Matrix4f();
        Matrix4f.mul(matrix4f1, matrix4f, matrix4f2);
        matrix4f2.invert();
        this.debugTerrainFrustumPosition.x = x;
        this.debugTerrainFrustumPosition.y = y;
        this.debugTerrainFrustumPosition.z = z;
        this.debugTerrainMatrix[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

        for (int i = 0; i < 8; ++i) {
            Matrix4f.transform(matrix4f2, this.debugTerrainMatrix[i], this.debugTerrainMatrix[i]);
            this.debugTerrainMatrix[i].x /= this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].y /= this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].z /= this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].w = 1.0F;
        }
    }

    protected Vector3f getViewVector(Entity entityIn, double partialTicks) {
        float f = (float) ((double) entityIn.prevRotationPitch + (double) (entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks);
        float f1 = (float) ((double) entityIn.prevRotationYaw + (double) (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks);

        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) {
            f += 180.0F;
        }

        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        return new Vector3f(f3 * f4, f5, f2 * f4);
    }

    public int renderBlockLayer(BlockRenderLayer blockLayerIn, double partialTicks, int pass, Entity entityIn) {
        RenderHelper.disableStandardItemLighting();

        if (blockLayerIn == BlockRenderLayer.TRANSLUCENT) {
            this.mc.mcProfiler.startSection("translucent_sort");
            double d0 = entityIn.posX - this.prevRenderSortX;
            double d1 = entityIn.posY - this.prevRenderSortY;
            double d2 = entityIn.posZ - this.prevRenderSortZ;

            if (d0 * d0 + d1 * d1 + d2 * d2 > 1.0D) {
                this.prevRenderSortX = entityIn.posX;
                this.prevRenderSortY = entityIn.posY;
                this.prevRenderSortZ = entityIn.posZ;
                int k = 0;
                this.chunksToResortTransparency.clear();

                for (RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos) {
                    if (renderglobal$containerlocalrenderinformation.renderChunk.compiledChunk.isLayerStarted(blockLayerIn) && k++ < 15) {
                        this.chunksToResortTransparency.add(renderglobal$containerlocalrenderinformation.renderChunk);
                    }
                }
            }

            this.mc.mcProfiler.endSection();
        }

        this.mc.mcProfiler.startSection("filterempty");
        int l = 0;
        boolean flag = blockLayerIn == BlockRenderLayer.TRANSLUCENT;
        int i1 = flag ? this.renderInfos.size() - 1 : 0;
        int i = flag ? -1 : this.renderInfos.size();
        int j1 = flag ? -1 : 1;

        for (int j = i1; j != i; j += j1) {
            RenderChunk renderchunk = (this.renderInfos.get(j)).renderChunk;

            if (!renderchunk.getCompiledChunk().isLayerEmpty(blockLayerIn)) {
                ++l;
                this.renderContainer.addRenderChunk(renderchunk, blockLayerIn);
            }
        }

        if (l == 0) {
            this.mc.mcProfiler.endSection();
            return l;
        } else {
            if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
                GlStateManager.disableFog();
            }

            this.mc.mcProfiler.endStartSection("render_" + blockLayerIn);
            this.renderBlockLayer(blockLayerIn);
            this.mc.mcProfiler.endSection();
            return l;
        }
    }

    private void renderBlockLayer(BlockRenderLayer blockLayerIn) {
        this.mc.entityRenderer.enableLightmap();

        if (OpenGlHelper.useVbo()) {
            GlStateManager.glEnableClientState(32884);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.glEnableClientState(32888);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.glEnableClientState(32888);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.glEnableClientState(32886);
        }

        if (Config.isShaders()) {
            ShadersRender.preRenderChunkLayer(blockLayerIn);
        }

        this.renderContainer.renderChunkLayer(blockLayerIn);

        if (Config.isShaders()) {
            ShadersRender.postRenderChunkLayer(blockLayerIn);
        }

        if (OpenGlHelper.useVbo()) {
            for (VertexFormatElement vertexformatelement : DefaultVertexFormats.BLOCK.getElements()) {
                VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
                int k1 = vertexformatelement.getIndex();

                switch (vertexformatelement$enumusage) {
                    case POSITION:
                        GlStateManager.glDisableClientState(32884);
                        break;

                    case UV:
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + k1);
                        GlStateManager.glDisableClientState(32888);
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                        break;

                    case COLOR:
                        GlStateManager.glDisableClientState(32886);
                        GlStateManager.resetColor();
                }
            }
        }

        this.mc.entityRenderer.disableLightmap();
    }

    private void cleanupDamagedBlocks(Iterator<DestroyBlockProgress> iteratorIn) {
        while (iteratorIn.hasNext()) {
            DestroyBlockProgress destroyblockprogress = iteratorIn.next();
            int k1 = destroyblockprogress.getCreationCloudUpdateTick();

            if (this.cloudTickCounter - k1 > 400) {
                iteratorIn.remove();
            }
        }
    }

    public void updateClouds() {
        if (Config.isShaders() && Keyboard.isKeyDown(61) && Keyboard.isKeyDown(19)) {
            Shaders.uninit();
            Shaders.loadShaderPack();
            Reflector.Minecraft_actionKeyF3.setValue(this.mc, Boolean.TRUE);
        }

        ++this.cloudTickCounter;

        if (this.cloudTickCounter % 20 == 0) {
            this.cleanupDamagedBlocks(this.damagedBlocks.values().iterator());
        }

        if (!this.setLightUpdates.isEmpty() && !this.renderDispatcher.hasNoFreeRenderBuilders() && this.chunksToUpdate.isEmpty()) {
            Iterator<BlockPos> iterator = this.setLightUpdates.iterator();

            while (iterator.hasNext()) {
                BlockPos blockpos = iterator.next();
                iterator.remove();
                int k1 = blockpos.getX();
                int l1 = blockpos.getY();
                int i2 = blockpos.getZ();
                this.markBlocksForUpdate(k1 - 1, l1 - 1, i2 - 1, k1 + 1, l1 + 1, i2 + 1, false);
            }
        }
    }

    private void renderSkyEnd() {
        if (Config.isSkyEnabled()) {
            GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.depthMask(false);
            this.renderEngine.bindTexture(END_SKY_TEXTURES);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();

            for (int k1 = 0; k1 < 6; ++k1) {
                GlStateManager.pushMatrix();

                if (k1 == 1) {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (k1 == 2) {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (k1 == 3) {
                    GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                }

                if (k1 == 4) {
                    GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                }

                if (k1 == 5) {
                    GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                }

                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int l1 = 40;
                int i2 = 40;
                int j2 = 40;

                if (Config.isCustomColors()) {
                    Vec3d vec3d = new Vec3d((double) l1 / 255.0D, (double) i2 / 255.0D, (double) j2 / 255.0D);
                    vec3d = CustomColors.getWorldSkyColor(vec3d, this.theWorld, this.mc.getRenderViewEntity(), 0.0F);
                    l1 = (int) (vec3d.xCoord * 255.0D);
                    i2 = (int) (vec3d.yCoord * 255.0D);
                    j2 = (int) (vec3d.zCoord * 255.0D);
                }

                bufferbuilder.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(l1, i2, j2, 255).endVertex();
                bufferbuilder.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(l1, i2, j2, 255).endVertex();
                bufferbuilder.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(l1, i2, j2, 255).endVertex();
                bufferbuilder.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(l1, i2, j2, 255).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    }

    public void renderSky(float partialTicks, int pass) {
        if (Reflector.ForgeWorldProvider_getSkyRenderer.exists()) {
            WorldProvider worldprovider = this.mc.world.provider;
            Object object = Reflector.call(worldprovider, Reflector.ForgeWorldProvider_getSkyRenderer);

            if (object != null) {
                Reflector.callVoid(object, Reflector.IRenderHandler_render, partialTicks, this.theWorld, this.mc);
                return;
            }
        }

        if (this.mc.world.provider.getDimensionType() == DimensionType.THE_END) {
            this.renderSkyEnd();
        } else if (this.mc.world.provider.isSurfaceWorld()) {
            GlStateManager.disableTexture2D();
            boolean flag1 = Config.isShaders();

            if (flag1) {
                Shaders.disableTexture2D();
            }

            Vec3d vec3d = this.theWorld.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
            vec3d = CustomColors.getSkyColor(vec3d, this.mc.world, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0D, this.mc.getRenderViewEntity().posZ);

            if (flag1) {
                Shaders.setSkyColor(vec3d);
            }

            float f = (float) vec3d.xCoord;
            float f1 = (float) vec3d.yCoord;
            float f2 = (float) vec3d.zCoord;

            if (pass != 2) {
                float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
                float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
                float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
                f = f3;
                f1 = f4;
                f2 = f5;
            }

            GlStateManager.color(f, f1, f2);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            GlStateManager.depthMask(false);
            GlStateManager.enableFog();

            if (flag1) {
                Shaders.enableFog();
            }

            GlStateManager.color(f, f1, f2);

            if (flag1) {
                Shaders.preSkyList();
            }

            if (Config.isSkyEnabled()) {
                if (this.vboEnabled) {
                    this.skyVBO.bindBuffer();
                    GlStateManager.glEnableClientState(32884);
                    GlStateManager.glVertexPointer(3, 5126, 12, 0);
                    this.skyVBO.drawArrays(7);
                    this.skyVBO.unbindBuffer();
                    GlStateManager.glDisableClientState(32884);
                } else {
                    GlStateManager.callList(this.glSkyList);
                }
            }

            GlStateManager.disableFog();

            if (flag1) {
                Shaders.disableFog();
            }

            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.disableStandardItemLighting();
            float[] afloat = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(partialTicks), partialTicks);

            if (afloat != null && Config.isSunMoonEnabled()) {
                GlStateManager.disableTexture2D();

                if (flag1) {
                    Shaders.disableTexture2D();
                }

                GlStateManager.shadeModel(7425);
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(MathHelper.sin(this.theWorld.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                float f6 = afloat[0];
                float f7 = afloat[1];
                float f8 = afloat[2];

                if (pass != 2) {
                    float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
                    float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
                    float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
                    f6 = f9;
                    f7 = f10;
                    f8 = f11;
                }

                bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
                bufferbuilder.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, afloat[3]).endVertex();
                int l1 = 16;

                for (int j2 = 0; j2 <= 16; ++j2) {
                    float f18 = (float) j2 * ((float) Math.PI * 2F) / 16.0F;
                    float f12 = MathHelper.sin(f18);
                    float f13 = MathHelper.cos(f18);
                    bufferbuilder.pos((double) (f12 * 120.0F), (double) (f13 * 120.0F), (double) (-f13 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
                }

                tessellator.draw();
                GlStateManager.popMatrix();
                GlStateManager.shadeModel(7424);
            }

            GlStateManager.enableTexture2D();

            if (flag1) {
                Shaders.enableTexture2D();
            }

            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            float f15 = 1.0F - this.theWorld.getRainStrength(partialTicks);
            GlStateManager.color(1.0F, 1.0F, 1.0F, f15);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            CustomSky.renderSky(this.theWorld, this.renderEngine, partialTicks);

            if (flag1) {
                Shaders.preCelestialRotate();
            }

            GlStateManager.rotate(this.theWorld.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);

            if (flag1) {
                Shaders.postCelestialRotate();
            }

            float f16 = 30.0F;

            if (Config.isSunTexture()) {
                this.renderEngine.bindTexture(SUN_TEXTURES);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos((double) (-f16), 100.0D, (double) (-f16)).tex(0.0D, 0.0D).endVertex();
                bufferbuilder.pos((double) f16, 100.0D, (double) (-f16)).tex(1.0D, 0.0D).endVertex();
                bufferbuilder.pos((double) f16, 100.0D, (double) f16).tex(1.0D, 1.0D).endVertex();
                bufferbuilder.pos((double) (-f16), 100.0D, (double) f16).tex(0.0D, 1.0D).endVertex();
                tessellator.draw();
            }

            f16 = 20.0F;

            if (Config.isMoonTexture()) {
                this.renderEngine.bindTexture(MOON_PHASES_TEXTURES);
                int k1 = this.theWorld.getMoonPhase();
                int i2 = k1 % 4;
                int k2 = k1 / 4 % 2;
                float f19 = (float) (i2 + 0) / 4.0F;
                float f21 = (float) (k2 + 0) / 2.0F;
                float f23 = (float) (i2 + 1) / 4.0F;
                float f14 = (float) (k2 + 1) / 2.0F;
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos((double) (-f16), -100.0D, (double) f16).tex((double) f23, (double) f14).endVertex();
                bufferbuilder.pos((double) f16, -100.0D, (double) f16).tex((double) f19, (double) f14).endVertex();
                bufferbuilder.pos((double) f16, -100.0D, (double) (-f16)).tex((double) f19, (double) f21).endVertex();
                bufferbuilder.pos((double) (-f16), -100.0D, (double) (-f16)).tex((double) f23, (double) f21).endVertex();
                tessellator.draw();
            }

            GlStateManager.disableTexture2D();

            if (flag1) {
                Shaders.disableTexture2D();
            }

            float f17 = this.theWorld.getStarBrightness(partialTicks) * f15;

            if (f17 > 0.0F && Config.isStarsEnabled() && !CustomSky.hasSkyLayers(this.theWorld)) {
                GlStateManager.color(f17, f17, f17, f17);

                if (this.vboEnabled) {
                    this.starVBO.bindBuffer();
                    GlStateManager.glEnableClientState(32884);
                    GlStateManager.glVertexPointer(3, 5126, 12, 0);
                    this.starVBO.drawArrays(7);
                    this.starVBO.unbindBuffer();
                    GlStateManager.glDisableClientState(32884);
                } else {
                    GlStateManager.callList(this.starGLCallList);
                }
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableFog();

            if (flag1) {
                Shaders.enableFog();
            }

            GlStateManager.popMatrix();
            GlStateManager.disableTexture2D();

            if (flag1) {
                Shaders.disableTexture2D();
            }

            GlStateManager.color(0.0F, 0.0F, 0.0F);
            double d3 = this.mc.player.getPositionEyes(partialTicks).yCoord - this.theWorld.getHorizon();

            if (d3 < 0.0D) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, 12.0F, 0.0F);

                if (this.vboEnabled) {
                    this.sky2VBO.bindBuffer();
                    GlStateManager.glEnableClientState(32884);
                    GlStateManager.glVertexPointer(3, 5126, 12, 0);
                    this.sky2VBO.drawArrays(7);
                    this.sky2VBO.unbindBuffer();
                    GlStateManager.glDisableClientState(32884);
                } else {
                    GlStateManager.callList(this.glSkyList2);
                }

                GlStateManager.popMatrix();
                float f20 = 1.0F;
                float f22 = -((float) (d3 + 65.0D));
                float f24 = -1.0F;
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                bufferbuilder.pos(-1.0D, (double) f22, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, (double) f22, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, (double) f22, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, (double) f22, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, (double) f22, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, (double) f22, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, (double) f22, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, (double) f22, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                tessellator.draw();
            }

            if (this.theWorld.provider.isSkyColored()) {
                GlStateManager.color(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
            } else {
                GlStateManager.color(f, f1, f2);
            }

            if (this.mc.gameSettings.renderDistanceChunks <= 4) {
                GlStateManager.color(this.mc.entityRenderer.fogColorRed, this.mc.entityRenderer.fogColorGreen, this.mc.entityRenderer.fogColorBlue);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, -((float) (d3 - 16.0D)), 0.0F);

            if (Config.isSkyEnabled()) {
                GlStateManager.callList(this.glSkyList2);
            }

            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();

            if (flag1) {
                Shaders.enableTexture2D();
            }

            GlStateManager.depthMask(true);
        }
    }

    public void renderClouds(float partialTicks, int pass, double p_180447_3_, double p_180447_5_, double p_180447_7_) {
        if (!Config.isCloudsOff()) {
            if (Reflector.ForgeWorldProvider_getCloudRenderer.exists()) {
                WorldProvider worldprovider = this.mc.world.provider;
                Object object = Reflector.call(worldprovider, Reflector.ForgeWorldProvider_getCloudRenderer);

                if (object != null) {
                    Reflector.callVoid(object, Reflector.IRenderHandler_render, partialTicks, this.theWorld, this.mc);
                    return;
                }
            }

            if (this.mc.world.provider.isSurfaceWorld()) {
                if (Config.isShaders()) {
                    Shaders.beginClouds();
                }

                if (Config.isCloudsFancy()) {
                    this.renderCloudsFancy(partialTicks, pass, p_180447_3_, p_180447_5_, p_180447_7_);
                } else {
                    float f9 = partialTicks;
                    partialTicks = 0.0F;
                    GlStateManager.disableCull();
                    int l2 = 32;
                    int k1 = 8;
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuffer();
                    this.renderEngine.bindTexture(CLOUDS_TEXTURES);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    Vec3d vec3d = this.theWorld.getCloudColour(partialTicks);
                    float f = (float) vec3d.xCoord;
                    float f1 = (float) vec3d.yCoord;
                    float f2 = (float) vec3d.zCoord;
                    this.cloudRenderer.prepareToRender(false, this.cloudTickCounter, f9, vec3d);

                    if (this.cloudRenderer.shouldUpdateGlList()) {
                        this.cloudRenderer.startUpdateGlList();

                        if (pass != 2) {
                            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
                            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
                            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
                            f = f3;
                            f1 = f4;
                            f2 = f5;
                        }

                        float f10 = 4.8828125E-4F;
                        double d5 = (double) ((float) this.cloudTickCounter + partialTicks);
                        double d3 = p_180447_3_ + d5 * 0.029999999329447746D;
                        int l1 = MathHelper.floor(d3 / 2048.0D);
                        int i2 = MathHelper.floor(p_180447_7_ / 2048.0D);
                        d3 = d3 - (double) (l1 * 2048);
                        double d4 = p_180447_7_ - (double) (i2 * 2048);
                        float f6 = this.theWorld.provider.getCloudHeight() - (float) p_180447_5_ + 0.33F;
                        f6 = f6 + this.mc.gameSettings.ofCloudsHeight * 128.0F;
                        float f7 = (float) (d3 * 4.8828125E-4D);
                        float f8 = (float) (d4 * 4.8828125E-4D);
                        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

                        for (int j2 = -256; j2 < 256; j2 += 32) {
                            for (int k2 = -256; k2 < 256; k2 += 32) {
                                bufferbuilder.pos((double) (j2 + 0), (double) f6, (double) (k2 + 32)).tex((double) ((float) (j2 + 0) * 4.8828125E-4F + f7), (double) ((float) (k2 + 32) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                                bufferbuilder.pos((double) (j2 + 32), (double) f6, (double) (k2 + 32)).tex((double) ((float) (j2 + 32) * 4.8828125E-4F + f7), (double) ((float) (k2 + 32) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                                bufferbuilder.pos((double) (j2 + 32), (double) f6, (double) (k2 + 0)).tex((double) ((float) (j2 + 32) * 4.8828125E-4F + f7), (double) ((float) (k2 + 0) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                                bufferbuilder.pos((double) (j2 + 0), (double) f6, (double) (k2 + 0)).tex((double) ((float) (j2 + 0) * 4.8828125E-4F + f7), (double) ((float) (k2 + 0) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                            }
                        }

                        tessellator.draw();
                        this.cloudRenderer.endUpdateGlList();
                    }

                    this.cloudRenderer.renderGlList();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.disableBlend();
                    GlStateManager.enableCull();
                }

                if (Config.isShaders()) {
                    Shaders.endClouds();
                }
            }
        }
    }

    /**
     * Checks if the given position is to be rendered with cloud fog
     */
    public boolean hasCloudFog(double x, double y, double z, float partialTicks) {
        return false;
    }

    private void renderCloudsFancy(float partialTicks, int pass, double p_180445_3_, double p_180445_5_, double p_180445_7_) {
        float f251 = 0.0F;
        GlStateManager.disableCull();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        float f = 12.0F;
        float f1 = 4.0F;
        double d3 = (double) ((float) this.cloudTickCounter + f251);
        double d4 = (p_180445_3_ + d3 * 0.029999999329447746D) / 12.0D;
        double d5 = p_180445_7_ / 12.0D + 0.33000001311302185D;
        float f2 = this.theWorld.provider.getCloudHeight() - (float) p_180445_5_ + 0.33F;
        f2 = f2 + this.mc.gameSettings.ofCloudsHeight * 128.0F;
        int k1 = MathHelper.floor(d4 / 2048.0D);
        int l1 = MathHelper.floor(d5 / 2048.0D);
        d4 = d4 - (double) (k1 * 2048);
        d5 = d5 - (double) (l1 * 2048);
        this.renderEngine.bindTexture(CLOUDS_TEXTURES);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Vec3d vec3d = this.theWorld.getCloudColour(f251);
        float f3 = (float) vec3d.xCoord;
        float f4 = (float) vec3d.yCoord;
        float f5 = (float) vec3d.zCoord;
        this.cloudRenderer.prepareToRender(true, this.cloudTickCounter, partialTicks, vec3d);

        if (pass != 2) {
            float f6 = (f3 * 30.0F + f4 * 59.0F + f5 * 11.0F) / 100.0F;
            float f7 = (f3 * 30.0F + f4 * 70.0F) / 100.0F;
            float f8 = (f3 * 30.0F + f5 * 70.0F) / 100.0F;
            f3 = f6;
            f4 = f7;
            f5 = f8;
        }

        float f26 = f3 * 0.9F;
        float f27 = f4 * 0.9F;
        float f28 = f5 * 0.9F;
        float f9 = f3 * 0.7F;
        float f10 = f4 * 0.7F;
        float f11 = f5 * 0.7F;
        float f12 = f3 * 0.8F;
        float f13 = f4 * 0.8F;
        float f14 = f5 * 0.8F;
        float f15 = 0.00390625F;
        float f16 = (float) MathHelper.floor(d4) * 0.00390625F;
        float f17 = (float) MathHelper.floor(d5) * 0.00390625F;
        float f18 = (float) (d4 - (double) MathHelper.floor(d4));
        float f19 = (float) (d5 - (double) MathHelper.floor(d5));
        int i2 = 8;
        int j2 = 4;
        float f20 = 9.765625E-4F;
        GlStateManager.scale(12.0F, 1.0F, 12.0F);

        for (int k2 = 0; k2 < 2; ++k2) {
            if (k2 == 0) {
                GlStateManager.colorMask(false, false, false, false);
            } else {
                switch (pass) {
                    case 0:
                        GlStateManager.colorMask(false, true, true, true);
                        break;

                    case 1:
                        GlStateManager.colorMask(true, false, false, true);
                        break;

                    case 2:
                        GlStateManager.colorMask(true, true, true, true);
                }
            }

            this.cloudRenderer.renderGlList();
        }

        if (this.cloudRenderer.shouldUpdateGlList()) {
            this.cloudRenderer.startUpdateGlList();

            for (int j3 = -3; j3 <= 4; ++j3) {
                for (int l2 = -3; l2 <= 4; ++l2) {
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                    float f21 = (float) (j3 * 8);
                    float f22 = (float) (l2 * 8);
                    float f23 = f21 - f18;
                    float f24 = f22 - f19;

                    if (f2 > -5.0F) {
                        bufferbuilder.pos((double) (f23 + 0.0F), (double) (f2 + 0.0F), (double) (f24 + 8.0F)).tex((double) ((f21 + 0.0F) * 0.00390625F + f16), (double) ((f22 + 8.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        bufferbuilder.pos((double) (f23 + 8.0F), (double) (f2 + 0.0F), (double) (f24 + 8.0F)).tex((double) ((f21 + 8.0F) * 0.00390625F + f16), (double) ((f22 + 8.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        bufferbuilder.pos((double) (f23 + 8.0F), (double) (f2 + 0.0F), (double) (f24 + 0.0F)).tex((double) ((f21 + 8.0F) * 0.00390625F + f16), (double) ((f22 + 0.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        bufferbuilder.pos((double) (f23 + 0.0F), (double) (f2 + 0.0F), (double) (f24 + 0.0F)).tex((double) ((f21 + 0.0F) * 0.00390625F + f16), (double) ((f22 + 0.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    }

                    if (f2 <= 5.0F) {
                        bufferbuilder.pos((double) (f23 + 0.0F), (double) (f2 + 4.0F - 9.765625E-4F), (double) (f24 + 8.0F)).tex((double) ((f21 + 0.0F) * 0.00390625F + f16), (double) ((f22 + 8.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        bufferbuilder.pos((double) (f23 + 8.0F), (double) (f2 + 4.0F - 9.765625E-4F), (double) (f24 + 8.0F)).tex((double) ((f21 + 8.0F) * 0.00390625F + f16), (double) ((f22 + 8.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        bufferbuilder.pos((double) (f23 + 8.0F), (double) (f2 + 4.0F - 9.765625E-4F), (double) (f24 + 0.0F)).tex((double) ((f21 + 8.0F) * 0.00390625F + f16), (double) ((f22 + 0.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        bufferbuilder.pos((double) (f23 + 0.0F), (double) (f2 + 4.0F - 9.765625E-4F), (double) (f24 + 0.0F)).tex((double) ((f21 + 0.0F) * 0.00390625F + f16), (double) ((f22 + 0.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                    }

                    if (j3 > -1) {
                        for (int i3 = 0; i3 < 8; ++i3) {
                            bufferbuilder.pos((double) (f23 + (float) i3 + 0.0F), (double) (f2 + 0.0F), (double) (f24 + 8.0F)).tex((double) ((f21 + (float) i3 + 0.5F) * 0.00390625F + f16), (double) ((f22 + 8.0F) * 0.00390625F + f17)).color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            bufferbuilder.pos((double) (f23 + (float) i3 + 0.0F), (double) (f2 + 4.0F), (double) (f24 + 8.0F)).tex((double) ((f21 + (float) i3 + 0.5F) * 0.00390625F + f16), (double) ((f22 + 8.0F) * 0.00390625F + f17)).color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            bufferbuilder.pos((double) (f23 + (float) i3 + 0.0F), (double) (f2 + 4.0F), (double) (f24 + 0.0F)).tex((double) ((f21 + (float) i3 + 0.5F) * 0.00390625F + f16), (double) ((f22 + 0.0F) * 0.00390625F + f17)).color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            bufferbuilder.pos((double) (f23 + (float) i3 + 0.0F), (double) (f2 + 0.0F), (double) (f24 + 0.0F)).tex((double) ((f21 + (float) i3 + 0.5F) * 0.00390625F + f16), (double) ((f22 + 0.0F) * 0.00390625F + f17)).color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (j3 <= 1) {
                        for (int k3 = 0; k3 < 8; ++k3) {
                            bufferbuilder.pos((double) (f23 + (float) k3 + 1.0F - 9.765625E-4F), (double) (f2 + 0.0F), (double) (f24 + 8.0F)).tex((double) ((f21 + (float) k3 + 0.5F) * 0.00390625F + f16), (double) ((f22 + 8.0F) * 0.00390625F + f17)).color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            bufferbuilder.pos((double) (f23 + (float) k3 + 1.0F - 9.765625E-4F), (double) (f2 + 4.0F), (double) (f24 + 8.0F)).tex((double) ((f21 + (float) k3 + 0.5F) * 0.00390625F + f16), (double) ((f22 + 8.0F) * 0.00390625F + f17)).color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            bufferbuilder.pos((double) (f23 + (float) k3 + 1.0F - 9.765625E-4F), (double) (f2 + 4.0F), (double) (f24 + 0.0F)).tex((double) ((f21 + (float) k3 + 0.5F) * 0.00390625F + f16), (double) ((f22 + 0.0F) * 0.00390625F + f17)).color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            bufferbuilder.pos((double) (f23 + (float) k3 + 1.0F - 9.765625E-4F), (double) (f2 + 0.0F), (double) (f24 + 0.0F)).tex((double) ((f21 + (float) k3 + 0.5F) * 0.00390625F + f16), (double) ((f22 + 0.0F) * 0.00390625F + f17)).color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (l2 > -1) {
                        for (int l3 = 0; l3 < 8; ++l3) {
                            bufferbuilder.pos((double) (f23 + 0.0F), (double) (f2 + 4.0F), (double) (f24 + (float) l3 + 0.0F)).tex((double) ((f21 + 0.0F) * 0.00390625F + f16), (double) ((f22 + (float) l3 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            bufferbuilder.pos((double) (f23 + 8.0F), (double) (f2 + 4.0F), (double) (f24 + (float) l3 + 0.0F)).tex((double) ((f21 + 8.0F) * 0.00390625F + f16), (double) ((f22 + (float) l3 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            bufferbuilder.pos((double) (f23 + 8.0F), (double) (f2 + 0.0F), (double) (f24 + (float) l3 + 0.0F)).tex((double) ((f21 + 8.0F) * 0.00390625F + f16), (double) ((f22 + (float) l3 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            bufferbuilder.pos((double) (f23 + 0.0F), (double) (f2 + 0.0F), (double) (f24 + (float) l3 + 0.0F)).tex((double) ((f21 + 0.0F) * 0.00390625F + f16), (double) ((f22 + (float) l3 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        }
                    }

                    if (l2 <= 1) {
                        for (int i4 = 0; i4 < 8; ++i4) {
                            bufferbuilder.pos((double) (f23 + 0.0F), (double) (f2 + 4.0F), (double) (f24 + (float) i4 + 1.0F - 9.765625E-4F)).tex((double) ((f21 + 0.0F) * 0.00390625F + f16), (double) ((f22 + (float) i4 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            bufferbuilder.pos((double) (f23 + 8.0F), (double) (f2 + 4.0F), (double) (f24 + (float) i4 + 1.0F - 9.765625E-4F)).tex((double) ((f21 + 8.0F) * 0.00390625F + f16), (double) ((f22 + (float) i4 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            bufferbuilder.pos((double) (f23 + 8.0F), (double) (f2 + 0.0F), (double) (f24 + (float) i4 + 1.0F - 9.765625E-4F)).tex((double) ((f21 + 8.0F) * 0.00390625F + f16), (double) ((f22 + (float) i4 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            bufferbuilder.pos((double) (f23 + 0.0F), (double) (f2 + 0.0F), (double) (f24 + (float) i4 + 1.0F - 9.765625E-4F)).tex((double) ((f21 + 0.0F) * 0.00390625F + f16), (double) ((f22 + (float) i4 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                        }
                    }

                    tessellator.draw();
                }
            }

            this.cloudRenderer.endUpdateGlList();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }

    public void updateChunks(long finishTimeNano) {
        finishTimeNano = (long) ((double) finishTimeNano + 1.0E8D);
        this.displayListEntitiesDirty |= this.renderDispatcher.runChunkUploads(finishTimeNano);

        if (this.chunksToUpdateForced.size() > 0) {
            Iterator iterator = this.chunksToUpdateForced.iterator();

            while (iterator.hasNext()) {
                RenderChunk renderchunk1 = (RenderChunk) iterator.next();

                if (!this.renderDispatcher.updateChunkLater(renderchunk1)) {
                    break;
                }

                renderchunk1.clearNeedsUpdate();
                iterator.remove();
                this.chunksToUpdate.remove(renderchunk1);
                this.chunksToResortTransparency.remove(renderchunk1);
            }
        }

        if (this.chunksToResortTransparency.size() > 0) {
            Iterator iterator2 = this.chunksToResortTransparency.iterator();

            if (iterator2.hasNext()) {
                RenderChunk renderchunk3 = (RenderChunk) iterator2.next();

                if (this.renderDispatcher.updateTransparencyLater(renderchunk3)) {
                    iterator2.remove();
                }
            }
        }

        int l1 = 0;
        int i2 = Config.getUpdatesPerFrame();
        int k1 = i2 * 2;

        if (!this.chunksToUpdate.isEmpty()) {
            Iterator<RenderChunk> iterator1 = this.chunksToUpdate.iterator();

            while (iterator1.hasNext()) {
                RenderChunk renderchunk2 = iterator1.next();
                boolean flag1;

                if (renderchunk2.isNeedsUpdateCustom()) {
                    flag1 = this.renderDispatcher.updateChunkNow(renderchunk2);
                } else {
                    flag1 = this.renderDispatcher.updateChunkLater(renderchunk2);
                }

                if (!flag1) {
                    break;
                }

                renderchunk2.clearNeedsUpdate();
                iterator1.remove();

                if (renderchunk2.getCompiledChunk().isEmpty() && i2 < k1) {
                    ++i2;
                }

                ++l1;

                if (l1 >= i2) {
                    break;
                }
            }
        }
    }

    public void renderWorldBorder(Entity entityIn, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        WorldBorder worldborder = this.theWorld.getWorldBorder();
        double d3 = (double) (this.mc.gameSettings.renderDistanceChunks * 16);

        if (entityIn.posX >= worldborder.maxX() - d3 || entityIn.posX <= worldborder.minX() + d3 || entityIn.posZ >= worldborder.maxZ() - d3 || entityIn.posZ <= worldborder.minZ() + d3) {
            double d4 = 1.0D - worldborder.getClosestDistance(entityIn) / d3;
            d4 = Math.pow(d4, 4.0D);
            double d5 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
            double d6 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
            double d7 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.renderEngine.bindTexture(FORCEFIELD_TEXTURES);
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            int k1 = worldborder.getStatus().getID();
            float f = (float) (k1 >> 16 & 255) / 255.0F;
            float f1 = (float) (k1 >> 8 & 255) / 255.0F;
            float f2 = (float) (k1 & 255) / 255.0F;
            GlStateManager.color(f, f1, f2, (float) d4);
            GlStateManager.doPolygonOffset(-3.0F, -3.0F);
            GlStateManager.enablePolygonOffset();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableAlpha();
            GlStateManager.disableCull();
            float f3 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F;
            float f4 = 0.0F;
            float f5 = 0.0F;
            float f6 = 128.0F;
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.setTranslation(-d5, -d6, -d7);
            double d8 = Math.max((double) MathHelper.floor(d7 - d3), worldborder.minZ());
            double d9 = Math.min((double) MathHelper.ceil(d7 + d3), worldborder.maxZ());

            if (d5 > worldborder.maxX() - d3) {
                float f7 = 0.0F;

                for (double d10 = d8; d10 < d9; f7 += 0.5F) {
                    double d11 = Math.min(1.0D, d9 - d10);
                    float f8 = (float) d11 * 0.5F;
                    bufferbuilder.pos(worldborder.maxX(), 256.0D, d10).tex((double) (f3 + f7), (double) (f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 256.0D, d10 + d11).tex((double) (f3 + f8 + f7), (double) (f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 0.0D, d10 + d11).tex((double) (f3 + f8 + f7), (double) (f3 + 128.0F)).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 0.0D, d10).tex((double) (f3 + f7), (double) (f3 + 128.0F)).endVertex();
                    ++d10;
                }
            }

            if (d5 < worldborder.minX() + d3) {
                float f9 = 0.0F;

                for (double d12 = d8; d12 < d9; f9 += 0.5F) {
                    double d15 = Math.min(1.0D, d9 - d12);
                    float f12 = (float) d15 * 0.5F;
                    bufferbuilder.pos(worldborder.minX(), 256.0D, d12).tex((double) (f3 + f9), (double) (f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 256.0D, d12 + d15).tex((double) (f3 + f12 + f9), (double) (f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 0.0D, d12 + d15).tex((double) (f3 + f12 + f9), (double) (f3 + 128.0F)).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 0.0D, d12).tex((double) (f3 + f9), (double) (f3 + 128.0F)).endVertex();
                    ++d12;
                }
            }

            d8 = Math.max((double) MathHelper.floor(d5 - d3), worldborder.minX());
            d9 = Math.min((double) MathHelper.ceil(d5 + d3), worldborder.maxX());

            if (d7 > worldborder.maxZ() - d3) {
                float f10 = 0.0F;

                for (double d13 = d8; d13 < d9; f10 += 0.5F) {
                    double d16 = Math.min(1.0D, d9 - d13);
                    float f13 = (float) d16 * 0.5F;
                    bufferbuilder.pos(d13, 256.0D, worldborder.maxZ()).tex((double) (f3 + f10), (double) (f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(d13 + d16, 256.0D, worldborder.maxZ()).tex((double) (f3 + f13 + f10), (double) (f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(d13 + d16, 0.0D, worldborder.maxZ()).tex((double) (f3 + f13 + f10), (double) (f3 + 128.0F)).endVertex();
                    bufferbuilder.pos(d13, 0.0D, worldborder.maxZ()).tex((double) (f3 + f10), (double) (f3 + 128.0F)).endVertex();
                    ++d13;
                }
            }

            if (d7 < worldborder.minZ() + d3) {
                float f11 = 0.0F;

                for (double d14 = d8; d14 < d9; f11 += 0.5F) {
                    double d17 = Math.min(1.0D, d9 - d14);
                    float f14 = (float) d17 * 0.5F;
                    bufferbuilder.pos(d14, 256.0D, worldborder.minZ()).tex((double) (f3 + f11), (double) (f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(d14 + d17, 256.0D, worldborder.minZ()).tex((double) (f3 + f14 + f11), (double) (f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(d14 + d17, 0.0D, worldborder.minZ()).tex((double) (f3 + f14 + f11), (double) (f3 + 128.0F)).endVertex();
                    bufferbuilder.pos(d14, 0.0D, worldborder.minZ()).tex((double) (f3 + f11), (double) (f3 + 128.0F)).endVertex();
                    ++d14;
                }
            }

            tessellator.draw();
            bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
            GlStateManager.enableCull();
            GlStateManager.disableAlpha();
            GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.enableAlpha();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
        }
    }

    private void preRenderDamagedBlocks() {
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
        GlStateManager.doPolygonOffset(-3.0F, -3.0F);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();

        if (Config.isShaders()) {
            ShadersRender.beginBlockDamage();
        }
    }

    private void postRenderDamagedBlocks() {
        GlStateManager.disableAlpha();
        GlStateManager.doPolygonOffset(0.0F, 0.0F);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();

        if (Config.isShaders()) {
            ShadersRender.endBlockDamage();
        }
    }

    public void drawBlockDamageTexture(Tessellator tessellatorIn, BufferBuilder worldRendererIn, Entity entityIn, float partialTicks) {
        double d3 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
        double d4 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
        double d5 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;

        if (!this.damagedBlocks.isEmpty()) {
            this.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.preRenderDamagedBlocks();
            worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
            worldRendererIn.setTranslation(-d3, -d4, -d5);
            worldRendererIn.noColor();
            Iterator<DestroyBlockProgress> iterator = this.damagedBlocks.values().iterator();

            while (iterator.hasNext()) {
                DestroyBlockProgress destroyblockprogress = iterator.next();
                BlockPos blockpos = destroyblockprogress.getPosition();
                double d6 = (double) blockpos.getX() - d3;
                double d7 = (double) blockpos.getY() - d4;
                double d8 = (double) blockpos.getZ() - d5;
                Block block = this.theWorld.getBlockState(blockpos).getBlock();
                boolean flag1;

                if (Reflector.ForgeTileEntity_canRenderBreaking.exists()) {
                    boolean flag2 = block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockSign || block instanceof BlockSkull;

                    if (!flag2) {
                        TileEntity tileentity = this.theWorld.getTileEntity(blockpos);

                        if (tileentity != null) {
                            flag2 = Reflector.callBoolean(tileentity, Reflector.ForgeTileEntity_canRenderBreaking);
                        }
                    }

                    flag1 = !flag2;
                } else {
                    flag1 = !(block instanceof BlockChest) && !(block instanceof BlockEnderChest) && !(block instanceof BlockSign) && !(block instanceof BlockSkull);
                }

                if (flag1) {
                    if (d6 * d6 + d7 * d7 + d8 * d8 > 1024.0D) {
                        iterator.remove();
                    } else {
                        IBlockState iblockstate = this.theWorld.getBlockState(blockpos);

                        if (iblockstate.getMaterial() != Material.AIR) {
                            int k1 = destroyblockprogress.getPartialBlockDamage();
                            TextureAtlasSprite textureatlassprite = this.destroyBlockIcons[k1];
                            BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
                            blockrendererdispatcher.renderBlockDamage(iblockstate, blockpos, textureatlassprite, this.theWorld);
                        }
                    }
                }
            }

            tessellatorIn.draw();
            worldRendererIn.setTranslation(0.0D, 0.0D, 0.0D);
            this.postRenderDamagedBlocks();
        }
    }

    /**
     * Draws the selection box for the player.
     */
    public void drawSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks) {
        if (execute == 0 && movingObjectPositionIn.typeOfHit == RayTraceResult.Type.BLOCK) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();

            if (Config.isShaders()) {
                Shaders.disableTexture2D();
            }

            GlStateManager.depthMask(false);
            BlockPos blockpos = movingObjectPositionIn.getBlockPos();
            IBlockState iblockstate = this.theWorld.getBlockState(blockpos);

            if (iblockstate.getMaterial() != Material.AIR && this.theWorld.getWorldBorder().contains(blockpos)) {
                double d3 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
                double d4 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
                double d5 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
                drawSelectionBoundingBox(iblockstate.getSelectedBoundingBox(this.theWorld, blockpos).expandXyz(0.0020000000949949026D).offset(-d3, -d4, -d5), 0.0F, 0.0F, 0.0F, 0.4F);
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();

            if (Config.isShaders()) {
                Shaders.enableTexture2D();
            }

            GlStateManager.disableBlend();
        }
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB box, float red, float green, float blue, float alpha) {
        drawBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
    }

    public static void drawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        drawBoundingBox(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }

    public static void drawBoundingBox(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
    }

    public static void renderFilledBox(AxisAlignedBB p_189696_0_, float p_189696_1_, float p_189696_2_, float p_189696_3_, float p_189696_4_) {
        renderFilledBox(p_189696_0_.minX, p_189696_0_.minY, p_189696_0_.minZ, p_189696_0_.maxX, p_189696_0_.maxY, p_189696_0_.maxZ, p_189696_1_, p_189696_2_, p_189696_3_, p_189696_4_);
    }

    public static void renderFilledBox(double p_189695_0_, double p_189695_2_, double p_189695_4_, double p_189695_6_, double p_189695_8_, double p_189695_10_, float p_189695_12_, float p_189695_13_, float p_189695_14_, float p_189695_15_) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        addChainedFilledBoxVertices(bufferbuilder, p_189695_0_, p_189695_2_, p_189695_4_, p_189695_6_, p_189695_8_, p_189695_10_, p_189695_12_, p_189695_13_, p_189695_14_, p_189695_15_);
        tessellator.draw();
    }

    public static void addChainedFilledBoxVertices(BufferBuilder p_189693_0_, double p_189693_1_, double p_189693_3_, double p_189693_5_, double p_189693_7_, double p_189693_9_, double p_189693_11_, float p_189693_13_, float p_189693_14_, float p_189693_15_, float p_189693_16_) {
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
        p_189693_0_.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(p_189693_13_, p_189693_14_, p_189693_15_, p_189693_16_).endVertex();
    }

    private void markBlocksForUpdate(int p_184385_1_, int p_184385_2_, int p_184385_3_, int p_184385_4_, int p_184385_5_, int p_184385_6_, boolean p_184385_7_) {
        this.viewFrustum.markBlocksForUpdate(p_184385_1_, p_184385_2_, p_184385_3_, p_184385_4_, p_184385_5_, p_184385_6_, p_184385_7_);
    }

    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        int k1 = pos.getX();
        int l1 = pos.getY();
        int i2 = pos.getZ();
        this.markBlocksForUpdate(k1 - 1, l1 - 1, i2 - 1, k1 + 1, l1 + 1, i2 + 1, (flags & 8) != 0);
    }

    public void notifyLightSet(BlockPos pos) {
        this.setLightUpdates.add(pos.toImmutable());
    }

    /**
     * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing.
     */
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1, false);
    }

    public void playRecord(@Nullable SoundEvent soundIn, BlockPos pos) {
        ISound isound = this.mapSoundPositions.get(pos);

        if (isound != null) {
            this.mc.getSoundHandler().stopSound(isound);
            this.mapSoundPositions.remove(pos);
        }

        if (soundIn != null) {
            ItemRecord itemrecord = ItemRecord.getBySound(soundIn);

            if (itemrecord != null) {
                this.mc.ingameGUI.setRecordPlayingMessage(itemrecord.getRecordNameLocal());
            }

            ISound isound1 = PositionedSoundRecord.getRecordSoundRecord(soundIn, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
            this.mapSoundPositions.put(pos, isound1);
            this.mc.getSoundHandler().playSound(isound1);
        }

        this.func_193054_a(this.theWorld, pos, soundIn != null);
    }

    private void func_193054_a(World p_193054_1_, BlockPos p_193054_2_, boolean p_193054_3_) {
        for (EntityLivingBase entitylivingbase : p_193054_1_.getEntitiesWithinAABB(EntityLivingBase.class, (new AxisAlignedBB(p_193054_2_)).expandXyz(3.0D))) {
            entitylivingbase.func_191987_a(p_193054_2_, p_193054_3_);
        }
    }

    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
    }

    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        this.func_190570_a(particleID, ignoreRange, false, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    public void func_190570_a(int p_190570_1_, boolean p_190570_2_, boolean p_190570_3_, final double p_190570_4_, final double p_190570_6_, final double p_190570_8_, double p_190570_10_, double p_190570_12_, double p_190570_14_, int... p_190570_16_) {
        try {
            this.func_190571_b(p_190570_1_, p_190570_2_, p_190570_3_, p_190570_4_, p_190570_6_, p_190570_8_, p_190570_10_, p_190570_12_, p_190570_14_, p_190570_16_);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while adding particle");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being added");
            crashreportcategory.addCrashSection("ID", Integer.valueOf(p_190570_1_));

            if (p_190570_16_ != null) {
                crashreportcategory.addCrashSection("Parameters", p_190570_16_);
            }

            crashreportcategory.setDetail("Position", new ICrashReportDetail<String>() {
                public String call() throws Exception {
                    return CrashReportCategory.getCoordinateInfo(p_190570_4_, p_190570_6_, p_190570_8_);
                }
            });
            throw new ReportedException(crashreport);
        }
    }

    private void spawnParticle(EnumParticleTypes particleIn, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        this.spawnParticle(particleIn.getParticleID(), particleIn.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Nullable
    private Particle spawnEntityFX(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        return this.func_190571_b(particleID, ignoreRange, false, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Nullable
    private Particle func_190571_b(int p_190571_1_, boolean p_190571_2_, boolean p_190571_3_, double p_190571_4_, double p_190571_6_, double p_190571_8_, double p_190571_10_, double p_190571_12_, double p_190571_14_, int... p_190571_16_) {
        Entity entity = this.mc.getRenderViewEntity();

        if (this.mc != null && entity != null && this.mc.effectRenderer != null) {
            int k1 = this.func_190572_a(p_190571_3_);
            double d3 = entity.posX - p_190571_4_;
            double d4 = entity.posY - p_190571_6_;
            double d5 = entity.posZ - p_190571_8_;

            if (p_190571_1_ == EnumParticleTypes.EXPLOSION_HUGE.getParticleID() && !Config.isAnimatedExplosion()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.EXPLOSION_LARGE.getParticleID() && !Config.isAnimatedExplosion()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.EXPLOSION_NORMAL.getParticleID() && !Config.isAnimatedExplosion()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.SUSPENDED.getParticleID() && !Config.isWaterParticles()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.SUSPENDED_DEPTH.getParticleID() && !Config.isVoidParticles()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.SMOKE_NORMAL.getParticleID() && !Config.isAnimatedSmoke()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.SMOKE_LARGE.getParticleID() && !Config.isAnimatedSmoke()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.SPELL_MOB.getParticleID() && !Config.isPotionParticles()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID() && !Config.isPotionParticles()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.SPELL.getParticleID() && !Config.isPotionParticles()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.SPELL_INSTANT.getParticleID() && !Config.isPotionParticles()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.SPELL_WITCH.getParticleID() && !Config.isPotionParticles()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.PORTAL.getParticleID() && !Config.isAnimatedPortal()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.FLAME.getParticleID() && !Config.isAnimatedFlame()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.REDSTONE.getParticleID() && !Config.isAnimatedRedstone()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.DRIP_WATER.getParticleID() && !Config.isDrippingWaterLava()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.DRIP_LAVA.getParticleID() && !Config.isDrippingWaterLava()) {
                return null;
            } else if (p_190571_1_ == EnumParticleTypes.FIREWORKS_SPARK.getParticleID() && !Config.isFireworkParticles()) {
                return null;
            } else {
                if (!p_190571_2_) {
                    double d6 = 1024.0D;

                    if (p_190571_1_ == EnumParticleTypes.CRIT.getParticleID()) {
                        d6 = 38416.0D;
                    }

                    if (d3 * d3 + d4 * d4 + d5 * d5 > d6) {
                        return null;
                    }

                    if (k1 > 1) {
                        return null;
                    }
                }

                Particle particle = this.mc.effectRenderer.spawnEffectParticle(p_190571_1_, p_190571_4_, p_190571_6_, p_190571_8_, p_190571_10_, p_190571_12_, p_190571_14_, p_190571_16_);

                if (p_190571_1_ == EnumParticleTypes.WATER_BUBBLE.getParticleID()) {
                    CustomColors.updateWaterFX(particle, this.theWorld, p_190571_4_, p_190571_6_, p_190571_8_, this.renderEnv);
                }

                if (p_190571_1_ == EnumParticleTypes.WATER_SPLASH.getParticleID()) {
                    CustomColors.updateWaterFX(particle, this.theWorld, p_190571_4_, p_190571_6_, p_190571_8_, this.renderEnv);
                }

                if (p_190571_1_ == EnumParticleTypes.WATER_DROP.getParticleID()) {
                    CustomColors.updateWaterFX(particle, this.theWorld, p_190571_4_, p_190571_6_, p_190571_8_, this.renderEnv);
                }

                if (p_190571_1_ == EnumParticleTypes.TOWN_AURA.getParticleID()) {
                    CustomColors.updateMyceliumFX(particle);
                }

                if (p_190571_1_ == EnumParticleTypes.PORTAL.getParticleID()) {
                    CustomColors.updatePortalFX(particle);
                }

                if (p_190571_1_ == EnumParticleTypes.REDSTONE.getParticleID()) {
                    CustomColors.updateReddustFX(particle, this.theWorld, p_190571_4_, p_190571_6_, p_190571_8_);
                }

                return particle;
            }
        } else {
            return null;
        }
    }

    private int func_190572_a(boolean p_190572_1_) {
        int k1 = this.mc.gameSettings.particleSetting;

        if (p_190572_1_ && k1 == 2 && this.theWorld.rand.nextInt(10) == 0) {
            k1 = 1;
        }

        if (k1 == 1 && this.theWorld.rand.nextInt(3) == 0) {
            k1 = 2;
        }

        return k1;
    }

    /**
     * Called on all IWorldAccesses when an entity is created or loaded. On client worlds, starts downloading any
     * necessary textures. On server worlds, adds the entity to the entity tracker.
     */
    public void onEntityAdded(Entity entityIn) {
        RandomMobs.entityLoaded(entityIn, this.theWorld);

        if (Config.isDynamicLights()) {
            DynamicLights.entityAdded(entityIn, this);
        }
    }

    /**
     * Called on all IWorldAccesses when an entity is unloaded or destroyed. On client worlds, releases any downloaded
     * textures. On server worlds, removes the entity from the entity tracker.
     */
    public void onEntityRemoved(Entity entityIn) {
        if (Config.isDynamicLights()) {
            DynamicLights.entityRemoved(entityIn, this);
        }
    }

    /**
     * Deletes all display lists
     */
    public void deleteAllDisplayLists() {
    }

    public void broadcastSound(int soundID, BlockPos pos, int data) {
        switch (soundID) {
            case 1023:
            case 1028:
            case 1038:
                Entity entity = this.mc.getRenderViewEntity();

                if (entity != null) {
                    double d3 = (double) pos.getX() - entity.posX;
                    double d4 = (double) pos.getY() - entity.posY;
                    double d5 = (double) pos.getZ() - entity.posZ;
                    double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                    double d7 = entity.posX;
                    double d8 = entity.posY;
                    double d9 = entity.posZ;

                    if (d6 > 0.0D) {
                        d7 += d3 / d6 * 2.0D;
                        d8 += d4 / d6 * 2.0D;
                        d9 += d5 / d6 * 2.0D;
                    }

                    if (soundID == 1023) {
                        this.theWorld.playSound(d7, d8, d9, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
                    } else if (soundID == 1038) {
                        this.theWorld.playSound(d7, d8, d9, SoundEvents.field_193782_bq, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
                    } else {
                        this.theWorld.playSound(d7, d8, d9, SoundEvents.ENTITY_ENDERDRAGON_DEATH, SoundCategory.HOSTILE, 5.0F, 1.0F, false);
                    }
                }

            default:
        }
    }

    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
        Random random = this.theWorld.rand;

        switch (type) {
            case 1000:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;

            case 1001:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
                break;

            case 1002:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
                break;

            case 1003:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ENDEREYE_LAUNCH, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
                break;

            case 1004:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_FIREWORK_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
                break;

            case 1005:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1006:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1007:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1008:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1009:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
                break;

            case 1010:
                if (Item.getItemById(data) instanceof ItemRecord) {
                    this.theWorld.playRecord(blockPosIn, ((ItemRecord) Item.getItemById(data)).getSound());
                } else {
                    this.theWorld.playRecord(blockPosIn, (SoundEvent) null);
                }

                break;

            case 1011:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1012:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1013:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1014:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1015:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1016:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1017:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1018:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1019:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1020:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1021:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1022:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1024:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1025:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1026:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1027:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1029:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1030:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1031:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1032:
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4F + 0.8F));
                break;

            case 1033:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;

            case 1034:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;

            case 1035:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;

            case 1036:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1037:
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 2000:
                int k1 = data % 3 - 1;
                int l1 = data / 3 % 3 - 1;
                double d3 = (double) blockPosIn.getX() + (double) k1 * 0.6D + 0.5D;
                double d4 = (double) blockPosIn.getY() + 0.5D;
                double d5 = (double) blockPosIn.getZ() + (double) l1 * 0.6D + 0.5D;

                for (int k2 = 0; k2 < 10; ++k2) {
                    double d18 = random.nextDouble() * 0.2D + 0.01D;
                    double d19 = d3 + (double) k1 * 0.01D + (random.nextDouble() - 0.5D) * (double) l1 * 0.5D;
                    double d20 = d4 + (random.nextDouble() - 0.5D) * 0.5D;
                    double d21 = d5 + (double) l1 * 0.01D + (random.nextDouble() - 0.5D) * (double) k1 * 0.5D;
                    double d22 = (double) k1 * d18 + random.nextGaussian() * 0.01D;
                    double d23 = -0.03D + random.nextGaussian() * 0.01D;
                    double d24 = (double) l1 * d18 + random.nextGaussian() * 0.01D;
                    this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d19, d20, d21, d22, d23, d24);
                }

                return;

            case 2001:
                Block block = Block.getBlockById(data & 4095);

                if (block.getDefaultState().getMaterial() != Material.AIR) {
                    SoundType soundtype = block.getSoundType();

                    if (Reflector.ForgeBlock_getSoundType.exists()) {
                        soundtype = (SoundType) Reflector.call(block, Reflector.ForgeBlock_getSoundType, Block.getStateById(data), this.theWorld, blockPosIn, null);
                    }

                    this.theWorld.playSound(blockPosIn, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F, false);
                }

                this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, block.getStateFromMeta(data >> 12 & 255));
                break;

            case 2002:
            case 2007:
                double d6 = (double) blockPosIn.getX();
                double d7 = (double) blockPosIn.getY();
                double d8 = (double) blockPosIn.getZ();

                for (int i2 = 0; i2 < 8; ++i2) {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d6, d7, d8, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, Item.getIdFromItem(Items.SPLASH_POTION));
                }

                float f5 = (float) (data >> 16 & 255) / 255.0F;
                float f = (float) (data >> 8 & 255) / 255.0F;
                float f1 = (float) (data >> 0 & 255) / 255.0F;
                EnumParticleTypes enumparticletypes = type == 2007 ? EnumParticleTypes.SPELL_INSTANT : EnumParticleTypes.SPELL;

                for (int l2 = 0; l2 < 100; ++l2) {
                    double d10 = random.nextDouble() * 4.0D;
                    double d12 = random.nextDouble() * Math.PI * 2.0D;
                    double d14 = Math.cos(d12) * d10;
                    double d27 = 0.01D + random.nextDouble() * 0.5D;
                    double d29 = Math.sin(d12) * d10;
                    Particle particle1 = this.spawnEntityFX(enumparticletypes.getParticleID(), enumparticletypes.getShouldIgnoreRange(), d6 + d14 * 0.1D, d7 + 0.3D, d8 + d29 * 0.1D, d14, d27, d29);

                    if (particle1 != null) {
                        float f4 = 0.75F + random.nextFloat() * 0.25F;
                        particle1.setRBGColorF(f5 * f4, f * f4, f1 * f4);
                        particle1.multiplyVelocity((float) d10);
                    }
                }

                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 2003:
                double d9 = (double) blockPosIn.getX() + 0.5D;
                double d11 = (double) blockPosIn.getY();
                double d13 = (double) blockPosIn.getZ() + 0.5D;

                for (int j3 = 0; j3 < 8; ++j3) {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d9, d11, d13, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, Item.getIdFromItem(Items.ENDER_EYE));
                }

                for (double d25 = 0.0D; d25 < (Math.PI * 2D); d25 += 0.15707963267948966D) {
                    this.spawnParticle(EnumParticleTypes.PORTAL, d9 + Math.cos(d25) * 5.0D, d11 - 0.4D, d13 + Math.sin(d25) * 5.0D, Math.cos(d25) * -5.0D, 0.0D, Math.sin(d25) * -5.0D);
                    this.spawnParticle(EnumParticleTypes.PORTAL, d9 + Math.cos(d25) * 5.0D, d11 - 0.4D, d13 + Math.sin(d25) * 5.0D, Math.cos(d25) * -7.0D, 0.0D, Math.sin(d25) * -7.0D);
                }

                return;

            case 2004:
                for (int i3 = 0; i3 < 20; ++i3) {
                    double d26 = (double) blockPosIn.getX() + 0.5D + ((double) this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    double d28 = (double) blockPosIn.getY() + 0.5D + ((double) this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    double d30 = (double) blockPosIn.getZ() + 0.5D + ((double) this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    this.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d26, d28, d30, 0.0D, 0.0D, 0.0D, new int[0]);
                    this.theWorld.spawnParticle(EnumParticleTypes.FLAME, d26, d28, d30, 0.0D, 0.0D, 0.0D, new int[0]);
                }

                return;

            case 2005:
                ItemDye.spawnBonemealParticles(this.theWorld, blockPosIn, data);
                break;

            case 2006:
                for (int j2 = 0; j2 < 200; ++j2) {
                    float f2 = random.nextFloat() * 4.0F;
                    float f3 = random.nextFloat() * ((float) Math.PI * 2F);
                    double d15 = (double) (MathHelper.cos(f3) * f2);
                    double d16 = 0.01D + random.nextDouble() * 0.5D;
                    double d17 = (double) (MathHelper.sin(f3) * f2);
                    Particle particle = this.spawnEntityFX(EnumParticleTypes.DRAGON_BREATH.getParticleID(), false, (double) blockPosIn.getX() + d15 * 0.1D, (double) blockPosIn.getY() + 0.3D, (double) blockPosIn.getZ() + d17 * 0.1D, d15, d16, d17);

                    if (particle != null) {
                        particle.multiplyVelocity(f2);
                    }
                }

                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_FIREBALL_EPLD, SoundCategory.HOSTILE, 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 3000:
                this.theWorld.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, true, (double) blockPosIn.getX() + 0.5D, (double) blockPosIn.getY() + 0.5D, (double) blockPosIn.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
                this.theWorld.playSound(blockPosIn, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0F, (1.0F + (this.theWorld.rand.nextFloat() - this.theWorld.rand.nextFloat()) * 0.2F) * 0.7F, false);
                break;

            case 3001:
                this.theWorld.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.HOSTILE, 64.0F, 0.8F + this.theWorld.rand.nextFloat() * 0.3F, false);
        }
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
        if (progress >= 0 && progress < 10) {
            DestroyBlockProgress destroyblockprogress = this.damagedBlocks.get(Integer.valueOf(breakerId));

            if (destroyblockprogress == null || destroyblockprogress.getPosition().getX() != pos.getX() || destroyblockprogress.getPosition().getY() != pos.getY() || destroyblockprogress.getPosition().getZ() != pos.getZ()) {
                destroyblockprogress = new DestroyBlockProgress(breakerId, pos);
                this.damagedBlocks.put(Integer.valueOf(breakerId), destroyblockprogress);
            }

            destroyblockprogress.setPartialBlockDamage(progress);
            destroyblockprogress.setCloudUpdateTick(this.cloudTickCounter);
        } else {
            this.damagedBlocks.remove(Integer.valueOf(breakerId));
        }
    }

    public boolean hasNoChunkUpdates() {
        return this.chunksToUpdate.isEmpty() && this.renderDispatcher.hasChunkUpdates();
    }

    public void setDisplayListEntitiesDirty() {
        this.displayListEntitiesDirty = true;
    }

    public void resetClouds() {
        this.cloudRenderer.reset();
    }

    public int getCountRenderers() {
        return this.viewFrustum.renderChunks.length;
    }

    public int getCountActiveRenderers() {
        return this.renderInfos.size();
    }

    public int getCountEntitiesRendered() {
        return this.countEntitiesRendered;
    }

    public int getCountTileEntitiesRendered() {
        return this.countTileEntitiesRendered;
    }

    public int getCountLoadedChunks() {
        if (this.theWorld == null) {
            return 0;
        } else {
            IChunkProvider ichunkprovider = this.theWorld.getChunkProvider();

            if (ichunkprovider == null) {
                return 0;
            } else {
                if (ichunkprovider != this.worldChunkProvider) {
                    this.worldChunkProvider = ichunkprovider;
                    this.worldChunkProviderMap = (Long2ObjectMap) Reflector.getFieldValue(ichunkprovider, Reflector.ChunkProviderClient_chunkMapping);
                }

                return this.worldChunkProviderMap == null ? 0 : this.worldChunkProviderMap.size();
            }
        }
    }

    public int getCountChunksToUpdate() {
        return this.chunksToUpdate.size();
    }

    public RenderChunk getRenderChunk(BlockPos p_getRenderChunk_1_) {
        return this.viewFrustum.getRenderChunk(p_getRenderChunk_1_);
    }

    public WorldClient getWorld() {
        return this.theWorld;
    }

    public void updateTileEntities(Collection<TileEntity> tileEntitiesToRemove, Collection<TileEntity> tileEntitiesToAdd) {
        synchronized (this.setTileEntities) {
            this.setTileEntities.removeAll(tileEntitiesToRemove);
            this.setTileEntities.addAll(tileEntitiesToAdd);
        }
    }

    private RenderGlobal.ContainerLocalRenderInformation allocateRenderInformation(RenderChunk p_allocateRenderInformation_1_, EnumFacing p_allocateRenderInformation_2_, int p_allocateRenderInformation_3_) {
        RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1;

        if (renderInfoCache.isEmpty()) {
            renderglobal$containerlocalrenderinformation1 = new RenderGlobal.ContainerLocalRenderInformation(p_allocateRenderInformation_1_, p_allocateRenderInformation_2_, p_allocateRenderInformation_3_);
        } else {
            renderglobal$containerlocalrenderinformation1 = renderInfoCache.pollLast();
            renderglobal$containerlocalrenderinformation1.initialize(p_allocateRenderInformation_1_, p_allocateRenderInformation_2_, p_allocateRenderInformation_3_);
        }

        renderglobal$containerlocalrenderinformation1.cacheable = true;
        return renderglobal$containerlocalrenderinformation1;
    }

    private void freeRenderInformation(RenderGlobal.ContainerLocalRenderInformation p_freeRenderInformation_1_) {
        if (p_freeRenderInformation_1_.cacheable) {
            renderInfoCache.add(p_freeRenderInformation_1_);
        }
    }

    public static class ContainerLocalRenderInformation {
        RenderChunk renderChunk;
        EnumFacing facing;
        int setFacing;
        boolean cacheable = false;

        public ContainerLocalRenderInformation(RenderChunk p_i1_1_, EnumFacing p_i1_2_, int p_i1_3_) {
            this.renderChunk = p_i1_1_;
            this.facing = p_i1_2_;
            this.setFacing = p_i1_3_;
        }

        public void setDirection(byte p_189561_1_, EnumFacing p_189561_2_) {
            this.setFacing = this.setFacing | p_189561_1_ | 1 << p_189561_2_.ordinal();
        }

        public boolean hasDirection(EnumFacing p_189560_1_) {
            return (this.setFacing & 1 << p_189560_1_.ordinal()) > 0;
        }

        private void initialize(RenderChunk p_initialize_1_, EnumFacing p_initialize_2_, int p_initialize_3_) {
            this.renderChunk = p_initialize_1_;
            this.facing = p_initialize_2_;
            this.setFacing = p_initialize_3_;
        }
    }
}
