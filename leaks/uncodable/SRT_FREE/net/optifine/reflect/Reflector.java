package net.optifine.reflect;

import com.google.common.base.Optional;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.model.ModelBlaze;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.model.ModelEnderMite;
import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelLeashKnot;
import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelRabbit;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.model.ModelWither;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderLeashKnot;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEnchantmentTableRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEnderChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import org.lwjgl.util.vector.Matrix4f;

public class Reflector {
   public static final ReflectorClass BetterFoliageClient = new ReflectorClass("mods.betterfoliage.client.BetterFoliageClient");
   public static final ReflectorClass BlamingTransformer = new ReflectorClass("net.minecraftforge.fml.common.asm.transformers.BlamingTransformer");
   public static final ReflectorMethod BlamingTransformer_onCrash = new ReflectorMethod(BlamingTransformer, "onCrash");
   public static final ReflectorClass CoreModManager = new ReflectorClass("net.minecraftforge.fml.relauncher.CoreModManager");
   public static final ReflectorMethod CoreModManager_onCrash = new ReflectorMethod(CoreModManager, "onCrash");
   public static final ReflectorClass DimensionManager = new ReflectorClass("net.minecraftforge.common.DimensionManager");
   public static final ReflectorMethod DimensionManager_getStaticDimensionIDs = new ReflectorMethod(DimensionManager, "getStaticDimensionIDs");
   public static final ReflectorClass EntityViewRenderEvent_CameraSetup = new ReflectorClass(
      "net.minecraftforge.client.event.EntityViewRenderEvent$CameraSetup"
   );
   public static final ReflectorConstructor EntityViewRenderEvent_CameraSetup_Constructor = new ReflectorConstructor(
      EntityViewRenderEvent_CameraSetup, new Class[]{EntityRenderer.class, Entity.class, Block.class, Double.TYPE, Float.TYPE, Float.TYPE, Float.TYPE}
   );
   public static final ReflectorField EntityViewRenderEvent_CameraSetup_yaw = new ReflectorField(EntityViewRenderEvent_CameraSetup, "yaw");
   public static final ReflectorField EntityViewRenderEvent_CameraSetup_pitch = new ReflectorField(EntityViewRenderEvent_CameraSetup, "pitch");
   public static final ReflectorField EntityViewRenderEvent_CameraSetup_roll = new ReflectorField(EntityViewRenderEvent_CameraSetup, "roll");
   public static final ReflectorClass EntityViewRenderEvent_FogColors = new ReflectorClass("net.minecraftforge.client.event.EntityViewRenderEvent$FogColors");
   public static final ReflectorConstructor EntityViewRenderEvent_FogColors_Constructor = new ReflectorConstructor(
      EntityViewRenderEvent_FogColors, new Class[]{EntityRenderer.class, Entity.class, Block.class, Double.TYPE, Float.TYPE, Float.TYPE, Float.TYPE}
   );
   public static final ReflectorField EntityViewRenderEvent_FogColors_red = new ReflectorField(EntityViewRenderEvent_FogColors, "red");
   public static final ReflectorField EntityViewRenderEvent_FogColors_green = new ReflectorField(EntityViewRenderEvent_FogColors, "green");
   public static final ReflectorField EntityViewRenderEvent_FogColors_blue = new ReflectorField(EntityViewRenderEvent_FogColors, "blue");
   public static final ReflectorClass EventBus = new ReflectorClass("net.minecraftforge.fml.common.eventhandler.EventBus");
   public static final ReflectorMethod EventBus_post = new ReflectorMethod(EventBus, "post");
   public static final ReflectorClass Event_Result = new ReflectorClass("net.minecraftforge.fml.common.eventhandler.Event$Result");
   public static final ReflectorField Event_Result_DENY = new ReflectorField(Event_Result, "DENY");
   public static final ReflectorField Event_Result_ALLOW = new ReflectorField(Event_Result, "ALLOW");
   public static final ReflectorField Event_Result_DEFAULT = new ReflectorField(Event_Result, "DEFAULT");
   public static final ReflectorClass FMLClientHandler = new ReflectorClass("net.minecraftforge.fml.client.FMLClientHandler");
   public static final ReflectorMethod FMLClientHandler_instance = new ReflectorMethod(FMLClientHandler, "instance");
   public static final ReflectorMethod FMLClientHandler_handleLoadingScreen = new ReflectorMethod(FMLClientHandler, "handleLoadingScreen");
   public static final ReflectorMethod FMLClientHandler_isLoading = new ReflectorMethod(FMLClientHandler, "isLoading");
   public static final ReflectorMethod FMLClientHandler_trackBrokenTexture = new ReflectorMethod(FMLClientHandler, "trackBrokenTexture");
   public static final ReflectorMethod FMLClientHandler_trackMissingTexture = new ReflectorMethod(FMLClientHandler, "trackMissingTexture");
   public static final ReflectorClass FMLCommonHandler = new ReflectorClass("net.minecraftforge.fml.common.FMLCommonHandler");
   public static final ReflectorMethod FMLCommonHandler_enhanceCrashReport = new ReflectorMethod(FMLCommonHandler, "enhanceCrashReport");
   public static final ReflectorMethod FMLCommonHandler_getBrandings = new ReflectorMethod(FMLCommonHandler, "getBrandings");
   public static final ReflectorMethod FMLCommonHandler_handleServerAboutToStart = new ReflectorMethod(FMLCommonHandler, "handleServerAboutToStart");
   public static final ReflectorMethod FMLCommonHandler_handleServerStarting = new ReflectorMethod(FMLCommonHandler, "handleServerStarting");
   public static final ReflectorMethod FMLCommonHandler_instance = new ReflectorMethod(FMLCommonHandler, "instance");
   public static final ReflectorClass ForgeBiome = new ReflectorClass(BiomeGenBase.class);
   public static final ReflectorMethod ForgeBiome_getWaterColorMultiplier = new ReflectorMethod(ForgeBiome, "getWaterColorMultiplier");
   public static final ReflectorClass ForgeBlock = new ReflectorClass(Block.class);
   public static final ReflectorMethod ForgeBlock_addDestroyEffects = new ReflectorMethod(ForgeBlock, "addDestroyEffects");
   public static final ReflectorMethod ForgeBlock_canCreatureSpawn = new ReflectorMethod(ForgeBlock, "canCreatureSpawn");
   public static final ReflectorMethod ForgeBlock_canRenderInLayer = new ReflectorMethod(
      ForgeBlock, "canRenderInLayer", new Class[]{EnumWorldBlockLayer.class}
   );
   public static final ReflectorMethod ForgeBlock_getBedDirection = new ReflectorMethod(ForgeBlock, "getBedDirection");
   public static final ReflectorMethod ForgeBlock_getExtendedState = new ReflectorMethod(ForgeBlock, "getExtendedState");
   public static final ReflectorMethod ForgeBlock_hasTileEntity = new ReflectorMethod(ForgeBlock, "hasTileEntity", new Class[]{IBlockState.class});
   public static final ReflectorMethod ForgeBlock_isAir = new ReflectorMethod(ForgeBlock, "isAir");
   public static final ReflectorMethod ForgeBlock_isBed = new ReflectorMethod(ForgeBlock, "isBed");
   public static final ReflectorClass ForgeChunkCache = new ReflectorClass(ChunkCache.class);
   public static final ReflectorMethod ForgeChunkCache_isSideSolid = new ReflectorMethod(ForgeChunkCache, "isSideSolid");
   public static final ReflectorClass ForgeEntity = new ReflectorClass(Entity.class);
   public static final ReflectorMethod ForgeEntity_canRiderInteract = new ReflectorMethod(ForgeEntity, "canRiderInteract");
   public static final ReflectorMethod ForgeEntity_shouldRenderInPass = new ReflectorMethod(ForgeEntity, "shouldRenderInPass");
   public static final ReflectorMethod ForgeEntity_shouldRiderSit = new ReflectorMethod(ForgeEntity, "shouldRiderSit");
   public static final ReflectorClass ForgeEventFactory = new ReflectorClass("net.minecraftforge.event.ForgeEventFactory");
   public static final ReflectorMethod ForgeEventFactory_canEntityDespawn = new ReflectorMethod(ForgeEventFactory, "canEntityDespawn");
   public static final ReflectorMethod ForgeEventFactory_canEntitySpawn = new ReflectorMethod(ForgeEventFactory, "canEntitySpawn");
   public static final ReflectorMethod ForgeEventFactory_doSpecialSpawn = new ReflectorMethod(
      ForgeEventFactory, "doSpecialSpawn", new Class[]{EntityLiving.class, World.class, Float.TYPE, Float.TYPE, Float.TYPE}
   );
   public static final ReflectorMethod ForgeEventFactory_getMaxSpawnPackSize = new ReflectorMethod(ForgeEventFactory, "getMaxSpawnPackSize");
   public static final ReflectorMethod ForgeEventFactory_renderBlockOverlay = new ReflectorMethod(ForgeEventFactory, "renderBlockOverlay");
   public static final ReflectorMethod ForgeEventFactory_renderFireOverlay = new ReflectorMethod(ForgeEventFactory, "renderFireOverlay");
   public static final ReflectorMethod ForgeEventFactory_renderWaterOverlay = new ReflectorMethod(ForgeEventFactory, "renderWaterOverlay");
   public static final ReflectorClass ForgeHooks = new ReflectorClass("net.minecraftforge.common.ForgeHooks");
   public static final ReflectorMethod ForgeHooks_onLivingSetAttackTarget = new ReflectorMethod(ForgeHooks, "onLivingSetAttackTarget");
   public static final ReflectorClass ForgeHooksClient = new ReflectorClass("net.minecraftforge.client.ForgeHooksClient");
   public static final ReflectorMethod ForgeHooksClient_applyTransform = new ReflectorMethod(
      ForgeHooksClient, "applyTransform", new Class[]{Matrix4f.class, Optional.class}
   );
   public static final ReflectorMethod ForgeHooksClient_dispatchRenderLast = new ReflectorMethod(ForgeHooksClient, "dispatchRenderLast");
   public static final ReflectorMethod ForgeHooksClient_drawScreen = new ReflectorMethod(ForgeHooksClient, "drawScreen");
   public static final ReflectorMethod ForgeHooksClient_fillNormal = new ReflectorMethod(ForgeHooksClient, "fillNormal");
   public static final ReflectorMethod ForgeHooksClient_handleCameraTransforms = new ReflectorMethod(ForgeHooksClient, "handleCameraTransforms");
   public static final ReflectorMethod ForgeHooksClient_getArmorTexture = new ReflectorMethod(ForgeHooksClient, "getArmorTexture");
   public static final ReflectorMethod ForgeHooksClient_getFogDensity = new ReflectorMethod(ForgeHooksClient, "getFogDensity");
   public static final ReflectorMethod ForgeHooksClient_getFOVModifier = new ReflectorMethod(ForgeHooksClient, "getFOVModifier");
   public static final ReflectorMethod ForgeHooksClient_getMatrix = new ReflectorMethod(ForgeHooksClient, "getMatrix", new Class[]{ModelRotation.class});
   public static final ReflectorMethod ForgeHooksClient_getOffsetFOV = new ReflectorMethod(ForgeHooksClient, "getOffsetFOV");
   public static final ReflectorMethod ForgeHooksClient_loadEntityShader = new ReflectorMethod(ForgeHooksClient, "loadEntityShader");
   public static final ReflectorMethod ForgeHooksClient_onDrawBlockHighlight = new ReflectorMethod(ForgeHooksClient, "onDrawBlockHighlight");
   public static final ReflectorMethod ForgeHooksClient_onFogRender = new ReflectorMethod(ForgeHooksClient, "onFogRender");
   public static final ReflectorMethod ForgeHooksClient_onTextureStitchedPre = new ReflectorMethod(ForgeHooksClient, "onTextureStitchedPre");
   public static final ReflectorMethod ForgeHooksClient_onTextureStitchedPost = new ReflectorMethod(ForgeHooksClient, "onTextureStitchedPost");
   public static final ReflectorMethod ForgeHooksClient_orientBedCamera = new ReflectorMethod(ForgeHooksClient, "orientBedCamera");
   public static final ReflectorMethod ForgeHooksClient_renderFirstPersonHand = new ReflectorMethod(ForgeHooksClient, "renderFirstPersonHand");
   public static final ReflectorMethod ForgeHooksClient_renderMainMenu = new ReflectorMethod(ForgeHooksClient, "renderMainMenu");
   public static final ReflectorMethod ForgeHooksClient_setRenderLayer = new ReflectorMethod(ForgeHooksClient, "setRenderLayer");
   public static final ReflectorMethod ForgeHooksClient_setRenderPass = new ReflectorMethod(ForgeHooksClient, "setRenderPass");
   public static final ReflectorMethod ForgeHooksClient_transform = new ReflectorMethod(ForgeHooksClient, "transform");
   public static final ReflectorClass ForgeItem = new ReflectorClass(Item.class);
   public static final ReflectorField ForgeItem_delegate = new ReflectorField(ForgeItem, "delegate");
   public static final ReflectorMethod ForgeItem_getDurabilityForDisplay = new ReflectorMethod(ForgeItem, "getDurabilityForDisplay");
   public static final ReflectorMethod ForgeItem_getModel = new ReflectorMethod(ForgeItem, "getModel");
   public static final ReflectorMethod ForgeItem_shouldCauseReequipAnimation = new ReflectorMethod(ForgeItem, "shouldCauseReequipAnimation");
   public static final ReflectorMethod ForgeItem_showDurabilityBar = new ReflectorMethod(ForgeItem, "showDurabilityBar");
   public static final ReflectorClass ForgeModContainer = new ReflectorClass("net.minecraftforge.common.ForgeModContainer");
   public static final ReflectorField ForgeModContainer_forgeLightPipelineEnabled = new ReflectorField(ForgeModContainer, "forgeLightPipelineEnabled");
   public static final ReflectorClass ForgeTileEntity = new ReflectorClass(TileEntity.class);
   public static final ReflectorMethod ForgeTileEntity_canRenderBreaking = new ReflectorMethod(ForgeTileEntity, "canRenderBreaking");
   public static final ReflectorMethod ForgeTileEntity_getRenderBoundingBox = new ReflectorMethod(ForgeTileEntity, "getRenderBoundingBox");
   public static final ReflectorMethod ForgeTileEntity_hasFastRenderer = new ReflectorMethod(ForgeTileEntity, "hasFastRenderer");
   public static final ReflectorMethod ForgeTileEntity_shouldRenderInPass = new ReflectorMethod(ForgeTileEntity, "shouldRenderInPass");
   public static final ReflectorClass ForgeVertexFormatElementEnumUseage = new ReflectorClass(VertexFormatElement.EnumUsage.class);
   public static final ReflectorMethod ForgeVertexFormatElementEnumUseage_preDraw = new ReflectorMethod(ForgeVertexFormatElementEnumUseage, "preDraw");
   public static final ReflectorMethod ForgeVertexFormatElementEnumUseage_postDraw = new ReflectorMethod(ForgeVertexFormatElementEnumUseage, "postDraw");
   public static final ReflectorClass ForgeWorld = new ReflectorClass(World.class);
   public static final ReflectorMethod ForgeWorld_countEntities = new ReflectorMethod(
      ForgeWorld, "countEntities", new Class[]{EnumCreatureType.class, Boolean.TYPE}
   );
   public static final ReflectorClass ForgeWorldProvider = new ReflectorClass(WorldProvider.class);
   public static final ReflectorMethod ForgeWorldProvider_getCloudRenderer = new ReflectorMethod(ForgeWorldProvider, "getCloudRenderer");
   public static final ReflectorMethod ForgeWorldProvider_getSkyRenderer = new ReflectorMethod(ForgeWorldProvider, "getSkyRenderer");
   public static final ReflectorMethod ForgeWorldProvider_getWeatherRenderer = new ReflectorMethod(ForgeWorldProvider, "getWeatherRenderer");
   public static final ReflectorClass GuiModList = new ReflectorClass("net.minecraftforge.fml.client.GuiModList");
   public static final ReflectorConstructor GuiModList_Constructor = new ReflectorConstructor(GuiModList, new Class[]{GuiScreen.class});
   public static final ReflectorClass IColoredBakedQuad = new ReflectorClass("net.minecraftforge.client.model.IColoredBakedQuad");
   public static final ReflectorClass IExtendedBlockState = new ReflectorClass("net.minecraftforge.common.property.IExtendedBlockState");
   public static final ReflectorMethod IExtendedBlockState_getClean = new ReflectorMethod(IExtendedBlockState, "getClean");
   public static final ReflectorClass IModel = new ReflectorClass("net.minecraftforge.client.model.IModel");
   public static final ReflectorMethod IModel_getTextures = new ReflectorMethod(IModel, "getTextures");
   public static final ReflectorClass IRenderHandler = new ReflectorClass("net.minecraftforge.client.IRenderHandler");
   public static final ReflectorMethod IRenderHandler_render = new ReflectorMethod(IRenderHandler, "render");
   public static final ReflectorClass ItemModelMesherForge = new ReflectorClass("net.minecraftforge.client.ItemModelMesherForge");
   public static final ReflectorConstructor ItemModelMesherForge_Constructor = new ReflectorConstructor(ItemModelMesherForge, new Class[]{ModelManager.class});
   public static final ReflectorClass Launch = new ReflectorClass("net.minecraft.launchwrapper.Launch");
   public static final ReflectorField Launch_blackboard = new ReflectorField(Launch, "blackboard");
   public static final ReflectorClass LightUtil = new ReflectorClass("net.minecraftforge.client.model.pipeline.LightUtil");
   public static final ReflectorField LightUtil_itemConsumer = new ReflectorField(LightUtil, "itemConsumer");
   public static final ReflectorMethod LightUtil_putBakedQuad = new ReflectorMethod(LightUtil, "putBakedQuad");
   public static final ReflectorField LightUtil_tessellator = new ReflectorField(LightUtil, "tessellator");
   public static final ReflectorClass Loader = new ReflectorClass("net.minecraftforge.fml.common.Loader");
   public static final ReflectorMethod Loader_getActiveModList = new ReflectorMethod(Loader, "getActiveModList");
   public static final ReflectorMethod Loader_instance = new ReflectorMethod(Loader, "instance");
   public static final ReflectorClass MinecraftForge = new ReflectorClass("net.minecraftforge.common.MinecraftForge");
   public static final ReflectorField MinecraftForge_EVENT_BUS = new ReflectorField(MinecraftForge, "EVENT_BUS");
   public static final ReflectorClass MinecraftForgeClient = new ReflectorClass("net.minecraftforge.client.MinecraftForgeClient");
   public static final ReflectorMethod MinecraftForgeClient_getRenderPass = new ReflectorMethod(MinecraftForgeClient, "getRenderPass");
   public static final ReflectorMethod MinecraftForgeClient_onRebuildChunk = new ReflectorMethod(MinecraftForgeClient, "onRebuildChunk");
   public static final ReflectorClass ModContainer = new ReflectorClass("net.minecraftforge.fml.common.ModContainer");
   public static final ReflectorMethod ModContainer_getModId = new ReflectorMethod(ModContainer, "getModId");
   public static final ReflectorClass ModelLoader = new ReflectorClass("net.minecraftforge.client.model.ModelLoader");
   public static final ReflectorField ModelLoader_stateModels = new ReflectorField(ModelLoader, "stateModels");
   public static final ReflectorMethod ModelLoader_onRegisterItems = new ReflectorMethod(ModelLoader, "onRegisterItems");
   public static final ReflectorMethod ModelLoader_getInventoryVariant = new ReflectorMethod(ModelLoader, "getInventoryVariant");
   public static final ReflectorField ModelLoader_textures = new ReflectorField(ModelLoader, "textures", true);
   public static final ReflectorClass ModelLoader_VanillaLoader = new ReflectorClass("net.minecraftforge.client.model.ModelLoader$VanillaLoader");
   public static final ReflectorField ModelLoader_VanillaLoader_INSTANCE = new ReflectorField(ModelLoader_VanillaLoader, "instance", true);
   public static final ReflectorMethod ModelLoader_VanillaLoader_loadModel = new ReflectorMethod(ModelLoader_VanillaLoader, "loadModel", null, true);
   public static final ReflectorClass RenderBlockOverlayEvent_OverlayType = new ReflectorClass(
      "net.minecraftforge.client.event.RenderBlockOverlayEvent$OverlayType"
   );
   public static final ReflectorField RenderBlockOverlayEvent_OverlayType_BLOCK = new ReflectorField(RenderBlockOverlayEvent_OverlayType, "BLOCK");
   public static final ReflectorClass RenderingRegistry = new ReflectorClass("net.minecraftforge.fml.client.registry.RenderingRegistry");
   public static final ReflectorMethod RenderingRegistry_loadEntityRenderers = new ReflectorMethod(
      RenderingRegistry, "loadEntityRenderers", new Class[]{RenderManager.class, Map.class}
   );
   public static final ReflectorClass RenderItemInFrameEvent = new ReflectorClass("net.minecraftforge.client.event.RenderItemInFrameEvent");
   public static final ReflectorConstructor RenderItemInFrameEvent_Constructor = new ReflectorConstructor(
      RenderItemInFrameEvent, new Class[]{EntityItemFrame.class, RenderItemFrame.class}
   );
   public static final ReflectorClass RenderLivingEvent_Pre = new ReflectorClass("net.minecraftforge.client.event.RenderLivingEvent$Pre");
   public static final ReflectorConstructor RenderLivingEvent_Pre_Constructor = new ReflectorConstructor(
      RenderLivingEvent_Pre, new Class[]{EntityLivingBase.class, RendererLivingEntity.class, Double.TYPE, Double.TYPE, Double.TYPE}
   );
   public static final ReflectorClass RenderLivingEvent_Post = new ReflectorClass("net.minecraftforge.client.event.RenderLivingEvent$Post");
   public static final ReflectorConstructor RenderLivingEvent_Post_Constructor = new ReflectorConstructor(
      RenderLivingEvent_Post, new Class[]{EntityLivingBase.class, RendererLivingEntity.class, Double.TYPE, Double.TYPE, Double.TYPE}
   );
   public static final ReflectorClass RenderLivingEvent_Specials_Pre = new ReflectorClass("net.minecraftforge.client.event.RenderLivingEvent$Specials$Pre");
   public static final ReflectorConstructor RenderLivingEvent_Specials_Pre_Constructor = new ReflectorConstructor(
      RenderLivingEvent_Specials_Pre, new Class[]{EntityLivingBase.class, RendererLivingEntity.class, Double.TYPE, Double.TYPE, Double.TYPE}
   );
   public static final ReflectorClass RenderLivingEvent_Specials_Post = new ReflectorClass("net.minecraftforge.client.event.RenderLivingEvent$Specials$Post");
   public static final ReflectorConstructor RenderLivingEvent_Specials_Post_Constructor = new ReflectorConstructor(
      RenderLivingEvent_Specials_Post, new Class[]{EntityLivingBase.class, RendererLivingEntity.class, Double.TYPE, Double.TYPE, Double.TYPE}
   );
   public static final ReflectorClass SplashScreen = new ReflectorClass("net.minecraftforge.fml.client.SplashProgress");
   public static final ReflectorClass WorldEvent_Load = new ReflectorClass("net.minecraftforge.event.world.WorldEvent$Load");
   public static final ReflectorConstructor WorldEvent_Load_Constructor = new ReflectorConstructor(WorldEvent_Load, new Class[]{World.class});
   public static final ReflectorClass ChunkProviderClient = new ReflectorClass(ChunkProviderClient.class);
   public static final ReflectorField ChunkProviderClient_chunkMapping = new ReflectorField(ChunkProviderClient, LongHashMap.class);
   public static final ReflectorField EntityVillager_careerId = new ReflectorField(
      new FieldLocatorTypes(
         EntityVillager.class,
         new Class[0],
         Integer.TYPE,
         new Class[]{Integer.TYPE, Boolean.TYPE, Boolean.TYPE, InventoryBasic.class},
         "EntityVillager.careerId"
      )
   );
   public static final ReflectorField EntityVillager_careerLevel = new ReflectorField(
      new FieldLocatorTypes(
         EntityVillager.class,
         new Class[]{Integer.TYPE},
         Integer.TYPE,
         new Class[]{Boolean.TYPE, Boolean.TYPE, InventoryBasic.class},
         "EntityVillager.careerLevel"
      )
   );
   public static final ReflectorClass GuiBeacon = new ReflectorClass(GuiBeacon.class);
   public static final ReflectorField GuiBeacon_tileBeacon = new ReflectorField(GuiBeacon, IInventory.class);
   public static final ReflectorClass GuiBrewingStand = new ReflectorClass(GuiBrewingStand.class);
   public static final ReflectorField GuiBrewingStand_tileBrewingStand = new ReflectorField(GuiBrewingStand, IInventory.class);
   public static final ReflectorClass GuiChest = new ReflectorClass(GuiChest.class);
   public static final ReflectorField GuiChest_lowerChestInventory = new ReflectorField(GuiChest, IInventory.class, 1);
   public static final ReflectorClass GuiEnchantment = new ReflectorClass(GuiEnchantment.class);
   public static final ReflectorField GuiEnchantment_nameable = new ReflectorField(GuiEnchantment, IWorldNameable.class);
   public static final ReflectorClass GuiFurnace = new ReflectorClass(GuiFurnace.class);
   public static final ReflectorField GuiFurnace_tileFurnace = new ReflectorField(GuiFurnace, IInventory.class);
   public static final ReflectorClass GuiHopper = new ReflectorClass(GuiHopper.class);
   public static final ReflectorField GuiHopper_hopperInventory = new ReflectorField(GuiHopper, IInventory.class, 1);
   public static final ReflectorClass GuiMainMenu = new ReflectorClass(GuiMainMenu.class);
   public static final ReflectorField GuiMainMenu_splashText = new ReflectorField(GuiMainMenu, String.class);
   public static final ReflectorClass Minecraft = new ReflectorClass(Minecraft.class);
   public static final ReflectorField Minecraft_defaultResourcePack = new ReflectorField(Minecraft, DefaultResourcePack.class);
   public static final ReflectorClass ModelHumanoidHead = new ReflectorClass(ModelHumanoidHead.class);
   public static final ReflectorField ModelHumanoidHead_head = new ReflectorField(ModelHumanoidHead, ModelRenderer.class);
   public static final ReflectorClass ModelBat = new ReflectorClass(ModelBat.class);
   public static final ReflectorFields ModelBat_ModelRenderers = new ReflectorFields(ModelBat, ModelRenderer.class, 6);
   public static final ReflectorClass ModelBlaze = new ReflectorClass(ModelBlaze.class);
   public static final ReflectorField ModelBlaze_blazeHead = new ReflectorField(ModelBlaze, ModelRenderer.class);
   public static final ReflectorField ModelBlaze_blazeSticks = new ReflectorField(ModelBlaze, ModelRenderer[].class);
   public static final ReflectorClass ModelBlock = new ReflectorClass(ModelBlock.class);
   public static final ReflectorField ModelBlock_parentLocation = new ReflectorField(ModelBlock, ResourceLocation.class);
   public static final ReflectorField ModelBlock_textures = new ReflectorField(ModelBlock, Map.class);
   public static final ReflectorClass ModelDragon = new ReflectorClass(ModelDragon.class);
   public static final ReflectorFields ModelDragon_ModelRenderers = new ReflectorFields(ModelDragon, ModelRenderer.class, 12);
   public static final ReflectorClass ModelEnderCrystal = new ReflectorClass(ModelEnderCrystal.class);
   public static final ReflectorFields ModelEnderCrystal_ModelRenderers = new ReflectorFields(ModelEnderCrystal, ModelRenderer.class, 3);
   public static final ReflectorClass RenderEnderCrystal = new ReflectorClass(RenderEnderCrystal.class);
   public static final ReflectorField RenderEnderCrystal_modelEnderCrystal = new ReflectorField(RenderEnderCrystal, ModelBase.class, 0);
   public static final ReflectorClass ModelEnderMite = new ReflectorClass(ModelEnderMite.class);
   public static final ReflectorField ModelEnderMite_bodyParts = new ReflectorField(ModelEnderMite, ModelRenderer[].class);
   public static final ReflectorClass ModelGhast = new ReflectorClass(ModelGhast.class);
   public static final ReflectorField ModelGhast_body = new ReflectorField(ModelGhast, ModelRenderer.class);
   public static final ReflectorField ModelGhast_tentacles = new ReflectorField(ModelGhast, ModelRenderer[].class);
   public static final ReflectorClass ModelGuardian = new ReflectorClass(ModelGuardian.class);
   public static final ReflectorField ModelGuardian_body = new ReflectorField(ModelGuardian, ModelRenderer.class, 0);
   public static final ReflectorField ModelGuardian_eye = new ReflectorField(ModelGuardian, ModelRenderer.class, 1);
   public static final ReflectorField ModelGuardian_spines = new ReflectorField(ModelGuardian, ModelRenderer[].class, 0);
   public static final ReflectorField ModelGuardian_tail = new ReflectorField(ModelGuardian, ModelRenderer[].class, 1);
   public static final ReflectorClass ModelHorse = new ReflectorClass(ModelHorse.class);
   public static final ReflectorFields ModelHorse_ModelRenderers = new ReflectorFields(ModelHorse, ModelRenderer.class, 39);
   public static final ReflectorClass RenderLeashKnot = new ReflectorClass(RenderLeashKnot.class);
   public static final ReflectorField RenderLeashKnot_leashKnotModel = new ReflectorField(RenderLeashKnot, ModelLeashKnot.class);
   public static final ReflectorClass ModelMagmaCube = new ReflectorClass(ModelMagmaCube.class);
   public static final ReflectorField ModelMagmaCube_core = new ReflectorField(ModelMagmaCube, ModelRenderer.class);
   public static final ReflectorField ModelMagmaCube_segments = new ReflectorField(ModelMagmaCube, ModelRenderer[].class);
   public static final ReflectorClass ModelOcelot = new ReflectorClass(ModelOcelot.class);
   public static final ReflectorFields ModelOcelot_ModelRenderers = new ReflectorFields(ModelOcelot, ModelRenderer.class, 8);
   public static final ReflectorClass ModelRabbit = new ReflectorClass(ModelRabbit.class);
   public static final ReflectorFields ModelRabbit_renderers = new ReflectorFields(ModelRabbit, ModelRenderer.class, 12);
   public static final ReflectorClass ModelSilverfish = new ReflectorClass(ModelSilverfish.class);
   public static final ReflectorField ModelSilverfish_bodyParts = new ReflectorField(ModelSilverfish, ModelRenderer[].class, 0);
   public static final ReflectorField ModelSilverfish_wingParts = new ReflectorField(ModelSilverfish, ModelRenderer[].class, 1);
   public static final ReflectorClass ModelSlime = new ReflectorClass(ModelSlime.class);
   public static final ReflectorFields ModelSlime_ModelRenderers = new ReflectorFields(ModelSlime, ModelRenderer.class, 4);
   public static final ReflectorClass ModelSquid = new ReflectorClass(ModelSquid.class);
   public static final ReflectorField ModelSquid_body = new ReflectorField(ModelSquid, ModelRenderer.class);
   public static final ReflectorField ModelSquid_tentacles = new ReflectorField(ModelSquid, ModelRenderer[].class);
   public static final ReflectorClass ModelWitch = new ReflectorClass(ModelWitch.class);
   public static final ReflectorField ModelWitch_mole = new ReflectorField(ModelWitch, ModelRenderer.class, 0);
   public static final ReflectorField ModelWitch_hat = new ReflectorField(ModelWitch, ModelRenderer.class, 1);
   public static final ReflectorClass ModelWither = new ReflectorClass(ModelWither.class);
   public static final ReflectorField ModelWither_bodyParts = new ReflectorField(ModelWither, ModelRenderer[].class, 0);
   public static final ReflectorField ModelWither_heads = new ReflectorField(ModelWither, ModelRenderer[].class, 1);
   public static final ReflectorClass ModelWolf = new ReflectorClass(ModelWolf.class);
   public static final ReflectorField ModelWolf_tail = new ReflectorField(ModelWolf, ModelRenderer.class, 6);
   public static final ReflectorField ModelWolf_mane = new ReflectorField(ModelWolf, ModelRenderer.class, 7);
   public static final ReflectorClass OptiFineClassTransformer = new ReflectorClass("optifine.OptiFineClassTransformer");
   public static final ReflectorField OptiFineClassTransformer_instance = new ReflectorField(OptiFineClassTransformer, "instance");
   public static final ReflectorMethod OptiFineClassTransformer_getOptiFineResource = new ReflectorMethod(OptiFineClassTransformer, "getOptiFineResource");
   public static final ReflectorClass RenderBoat = new ReflectorClass(RenderBoat.class);
   public static final ReflectorField RenderBoat_modelBoat = new ReflectorField(RenderBoat, ModelBase.class);
   public static final ReflectorClass RenderMinecart = new ReflectorClass(RenderMinecart.class);
   public static final ReflectorField RenderMinecart_modelMinecart = new ReflectorField(RenderMinecart, ModelBase.class);
   public static final ReflectorClass RenderWitherSkull = new ReflectorClass(RenderWitherSkull.class);
   public static final ReflectorField RenderWitherSkull_model = new ReflectorField(RenderWitherSkull, ModelSkeletonHead.class);
   public static final ReflectorClass TileEntityBannerRenderer = new ReflectorClass(TileEntityBannerRenderer.class);
   public static final ReflectorField TileEntityBannerRenderer_bannerModel = new ReflectorField(TileEntityBannerRenderer, ModelBanner.class);
   public static final ReflectorClass TileEntityBeacon = new ReflectorClass(TileEntityBeacon.class);
   public static final ReflectorField TileEntityBeacon_customName = new ReflectorField(TileEntityBeacon, String.class);
   public static final ReflectorClass TileEntityBrewingStand = new ReflectorClass(TileEntityBrewingStand.class);
   public static final ReflectorField TileEntityBrewingStand_customName = new ReflectorField(TileEntityBrewingStand, String.class);
   public static final ReflectorClass TileEntityChestRenderer = new ReflectorClass(TileEntityChestRenderer.class);
   public static final ReflectorField TileEntityChestRenderer_simpleChest = new ReflectorField(TileEntityChestRenderer, ModelChest.class, 0);
   public static final ReflectorField TileEntityChestRenderer_largeChest = new ReflectorField(TileEntityChestRenderer, ModelChest.class, 1);
   public static final ReflectorClass TileEntityEnchantmentTable = new ReflectorClass(TileEntityEnchantmentTable.class);
   public static final ReflectorField TileEntityEnchantmentTable_customName = new ReflectorField(TileEntityEnchantmentTable, String.class);
   public static final ReflectorClass TileEntityEnchantmentTableRenderer = new ReflectorClass(TileEntityEnchantmentTableRenderer.class);
   public static final ReflectorField TileEntityEnchantmentTableRenderer_modelBook = new ReflectorField(TileEntityEnchantmentTableRenderer, ModelBook.class);
   public static final ReflectorClass TileEntityEnderChestRenderer = new ReflectorClass(TileEntityEnderChestRenderer.class);
   public static final ReflectorField TileEntityEnderChestRenderer_modelChest = new ReflectorField(TileEntityEnderChestRenderer, ModelChest.class);
   public static final ReflectorClass TileEntityFurnace = new ReflectorClass(TileEntityFurnace.class);
   public static final ReflectorField TileEntityFurnace_customName = new ReflectorField(TileEntityFurnace, String.class);
   public static final ReflectorClass TileEntitySignRenderer = new ReflectorClass(TileEntitySignRenderer.class);
   public static final ReflectorField TileEntitySignRenderer_model = new ReflectorField(TileEntitySignRenderer, ModelSign.class);
   public static final ReflectorClass TileEntitySkullRenderer = new ReflectorClass(TileEntitySkullRenderer.class);
   public static final ReflectorField TileEntitySkullRenderer_humanoidHead = new ReflectorField(TileEntitySkullRenderer, ModelSkeletonHead.class, 1);

   public static void callVoid(ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         if (method == null) {
            return;
         }

         method.invoke(null, params);
      } catch (Throwable var3) {
         handleException(var3, null, refMethod, params);
      }
   }

   public static boolean callBoolean(ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         return method == null ? false : method.invoke(null, params);
      } catch (Throwable var3) {
         handleException(var3, null, refMethod, params);
         return false;
      }
   }

   public static int callInt(ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         return method == null ? 0 : method.invoke(null, params);
      } catch (Throwable var3) {
         handleException(var3, null, refMethod, params);
         return 0;
      }
   }

   public static float callFloat(ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         return method == null ? 0.0F : method.invoke(null, params);
      } catch (Throwable var3) {
         handleException(var3, null, refMethod, params);
         return 0.0F;
      }
   }

   public static String callString(ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         return method == null ? null : (String)method.invoke(null, params);
      } catch (Throwable var3) {
         handleException(var3, null, refMethod, params);
         return null;
      }
   }

   public static Object call(ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         return method == null ? null : method.invoke(null, params);
      } catch (Throwable var3) {
         handleException(var3, null, refMethod, params);
         return null;
      }
   }

   public static void callVoid(Object obj, ReflectorMethod refMethod, Object... params) {
      try {
         if (obj == null) {
            return;
         }

         Method method = refMethod.getTargetMethod();
         if (method == null) {
            return;
         }

         method.invoke(obj, params);
      } catch (Throwable var4) {
         handleException(var4, obj, refMethod, params);
      }
   }

   public static boolean callBoolean(Object obj, ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         return method == null ? false : method.invoke(obj, params);
      } catch (Throwable var4) {
         handleException(var4, obj, refMethod, params);
         return false;
      }
   }

   public static int callInt(Object obj, ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         return method == null ? 0 : method.invoke(obj, params);
      } catch (Throwable var4) {
         handleException(var4, obj, refMethod, params);
         return 0;
      }
   }

   public static double callDouble(Object obj, ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         return method == null ? 0.0 : method.invoke(obj, params);
      } catch (Throwable var4) {
         handleException(var4, obj, refMethod, params);
         return 0.0;
      }
   }

   public static String callString(Object obj, ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         return method == null ? null : (String)method.invoke(obj, params);
      } catch (Throwable var4) {
         handleException(var4, obj, refMethod, params);
         return null;
      }
   }

   public static Object call(Object obj, ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         return method == null ? null : method.invoke(obj, params);
      } catch (Throwable var4) {
         handleException(var4, obj, refMethod, params);
         return null;
      }
   }

   public static Object getFieldValue(ReflectorField refField) {
      return getFieldValue(null, refField);
   }

   public static Object getFieldValue(Object obj, ReflectorField refField) {
      try {
         Field field = refField.getTargetField();
         return field == null ? null : field.get(obj);
      } catch (Throwable var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public static boolean getFieldValueBoolean(Object obj, ReflectorField refField, boolean def) {
      try {
         Field field = refField.getTargetField();
         return field == null ? def : field.getBoolean(obj);
      } catch (Throwable var4) {
         var4.printStackTrace();
         return def;
      }
   }

   public static Object getFieldValue(Object obj, ReflectorFields refFields, int index) {
      ReflectorField reflectorfield = refFields.getReflectorField(index);
      return reflectorfield == null ? null : getFieldValue(obj, reflectorfield);
   }

   public static float getFieldValueFloat(Object obj, ReflectorField refField, float def) {
      try {
         Field field = refField.getTargetField();
         return field == null ? def : field.getFloat(obj);
      } catch (Throwable var4) {
         var4.printStackTrace();
         return def;
      }
   }

   public static int getFieldValueInt(Object obj, ReflectorField refField, int def) {
      try {
         Field field = refField.getTargetField();
         return field == null ? def : field.getInt(obj);
      } catch (Throwable var4) {
         var4.printStackTrace();
         return def;
      }
   }

   public static void setFieldValue(ReflectorField refField, Object value) {
      setFieldValue(null, refField, value);
   }

   public static void setFieldValue(Object obj, ReflectorField refField, Object value) {
      try {
         Field field = refField.getTargetField();
         if (field != null) {
            field.set(obj, value);
         }
      } catch (Throwable var4) {
         var4.printStackTrace();
      }
   }

   public static void setFieldValueInt(Object obj, ReflectorField refField, int value) {
      try {
         Field field = refField.getTargetField();
         if (field != null) {
            field.setInt(obj, value);
         }
      } catch (Throwable var4) {
         var4.printStackTrace();
      }
   }

   public static boolean postForgeBusEvent(ReflectorConstructor constr, Object... params) {
      Object object = newInstance(constr, params);
      return postForgeBusEvent(object);
   }

   public static boolean postForgeBusEvent(Object event) {
      if (event == null) {
         return false;
      } else {
         Object object = getFieldValue(MinecraftForge_EVENT_BUS);
         if (object == null) {
            return false;
         } else {
            Object object1 = call(object, EventBus_post, event);
            return !(object1 instanceof Boolean) ? false : (Boolean)object1;
         }
      }
   }

   public static Object newInstance(ReflectorConstructor constr, Object... params) {
      Constructor constructor = constr.getTargetConstructor();
      if (constructor == null) {
         return null;
      } else {
         try {
            return constructor.newInstance(params);
         } catch (Throwable var4) {
            handleException(var4, constr, params);
            return null;
         }
      }
   }

   public static boolean matchesTypes(Class[] pTypes, Class[] cTypes) {
      if (pTypes.length != cTypes.length) {
         return false;
      } else {
         for(int i = 0; i < cTypes.length; ++i) {
            Class oclass = pTypes[i];
            Class oclass1 = cTypes[i];
            if (oclass != oclass1) {
               return false;
            }
         }

         return true;
      }
   }

   private static void handleException(Throwable e, Object obj, ReflectorMethod refMethod, Object[] params) {
      if (e instanceof InvocationTargetException) {
         Throwable throwable = e.getCause();
         if (throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
         }

         e.printStackTrace();
      } else {
         if (e instanceof IllegalArgumentException) {
            Config.warn("*** IllegalArgumentException ***");
            Config.warn("Method: " + refMethod.getTargetMethod());
            Config.warn("Object: " + obj);
            Config.warn("Parameter classes: " + Config.arrayToString(getClasses(params)));
            Config.warn("Parameters: " + Config.arrayToString(params));
         }

         Config.warn("*** Exception outside of method ***");
         Config.warn("Method deactivated: " + refMethod.getTargetMethod());
         refMethod.deactivate();
         e.printStackTrace();
      }
   }

   private static void handleException(Throwable e, ReflectorConstructor refConstr, Object[] params) {
      if (!(e instanceof InvocationTargetException)) {
         if (e instanceof IllegalArgumentException) {
            Config.warn("*** IllegalArgumentException ***");
            Config.warn("Constructor: " + refConstr.getTargetConstructor());
            Config.warn("Parameter classes: " + Config.arrayToString(getClasses(params)));
            Config.warn("Parameters: " + Config.arrayToString(params));
         }

         Config.warn("*** Exception outside of constructor ***");
         Config.warn("Constructor deactivated: " + refConstr.getTargetConstructor());
         refConstr.deactivate();
      }

      e.printStackTrace();
   }

   private static Object[] getClasses(Object[] objs) {
      if (objs == null) {
         return new Class[0];
      } else {
         Class[] aclass = new Class[objs.length];

         for(int i = 0; i < aclass.length; ++i) {
            Object object = objs[i];
            if (object != null) {
               aclass[i] = object.getClass();
            }
         }

         return aclass;
      }
   }
}
