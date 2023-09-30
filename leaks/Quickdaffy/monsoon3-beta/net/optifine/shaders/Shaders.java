/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  org.apache.commons.io.IOUtils
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.ARBGeometryShader4
 *  org.lwjgl.opengl.ARBShaderObjects
 *  org.lwjgl.opengl.ARBVertexShader
 *  org.lwjgl.opengl.ContextCapabilities
 *  org.lwjgl.opengl.EXTFramebufferObject
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GLContext
 *  org.lwjgl.util.glu.GLU
 *  org.lwjgl.util.vector.Vector4f
 */
package net.optifine.shaders;

import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.optifine.CustomBlockLayers;
import net.optifine.CustomColors;
import net.optifine.GlErrors;
import net.optifine.Lang;
import net.optifine.config.ConnectedParser;
import net.optifine.expr.IExpressionBool;
import net.optifine.reflect.Reflector;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.BlockAliases;
import net.optifine.shaders.CustomTexture;
import net.optifine.shaders.CustomTextureLocation;
import net.optifine.shaders.CustomTextureRaw;
import net.optifine.shaders.EntityAliases;
import net.optifine.shaders.FlipTextures;
import net.optifine.shaders.HFNoiseTexture;
import net.optifine.shaders.ICustomTexture;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.ItemAliases;
import net.optifine.shaders.Program;
import net.optifine.shaders.ProgramStack;
import net.optifine.shaders.ProgramStage;
import net.optifine.shaders.Programs;
import net.optifine.shaders.SMCLog;
import net.optifine.shaders.SMath;
import net.optifine.shaders.ShaderPackDefault;
import net.optifine.shaders.ShaderPackFolder;
import net.optifine.shaders.ShaderPackNone;
import net.optifine.shaders.ShaderPackZip;
import net.optifine.shaders.ShaderUtils;
import net.optifine.shaders.ShadersRender;
import net.optifine.shaders.ShadersTex;
import net.optifine.shaders.SimpleShaderTexture;
import net.optifine.shaders.config.EnumShaderOption;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.shaders.config.MacroState;
import net.optifine.shaders.config.PropertyDefaultFastFancyOff;
import net.optifine.shaders.config.PropertyDefaultTrueFalse;
import net.optifine.shaders.config.RenderScale;
import net.optifine.shaders.config.ScreenShaderOptions;
import net.optifine.shaders.config.ShaderLine;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionProfile;
import net.optifine.shaders.config.ShaderOptionRest;
import net.optifine.shaders.config.ShaderPackParser;
import net.optifine.shaders.config.ShaderParser;
import net.optifine.shaders.config.ShaderProfile;
import net.optifine.shaders.uniform.CustomUniforms;
import net.optifine.shaders.uniform.ShaderUniform1f;
import net.optifine.shaders.uniform.ShaderUniform1i;
import net.optifine.shaders.uniform.ShaderUniform2i;
import net.optifine.shaders.uniform.ShaderUniform3f;
import net.optifine.shaders.uniform.ShaderUniform4f;
import net.optifine.shaders.uniform.ShaderUniform4i;
import net.optifine.shaders.uniform.ShaderUniformM4;
import net.optifine.shaders.uniform.ShaderUniforms;
import net.optifine.shaders.uniform.Smoother;
import net.optifine.texture.InternalFormat;
import net.optifine.texture.PixelFormat;
import net.optifine.texture.PixelType;
import net.optifine.texture.TextureType;
import net.optifine.util.EntityUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;
import net.optifine.util.TimedEvent;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBGeometryShader4;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector4f;

public class Shaders {
    static Minecraft mc;
    static EntityRenderer entityRenderer;
    public static boolean isInitializedOnce;
    public static boolean isShaderPackInitialized;
    public static ContextCapabilities capabilities;
    public static String glVersionString;
    public static String glVendorString;
    public static String glRendererString;
    public static boolean hasGlGenMipmap;
    public static int countResetDisplayLists;
    private static int renderDisplayWidth;
    private static int renderDisplayHeight;
    public static int renderWidth;
    public static int renderHeight;
    public static boolean isRenderingWorld;
    public static boolean isRenderingSky;
    public static boolean isCompositeRendered;
    public static boolean isRenderingDfb;
    public static boolean isShadowPass;
    public static boolean isEntitiesGlowing;
    public static boolean isSleeping;
    private static boolean isRenderingFirstPersonHand;
    private static boolean isHandRenderedMain;
    private static boolean isHandRenderedOff;
    private static boolean skipRenderHandMain;
    private static boolean skipRenderHandOff;
    public static boolean renderItemKeepDepthMask;
    public static boolean itemToRenderMainTranslucent;
    public static boolean itemToRenderOffTranslucent;
    static float[] sunPosition;
    static float[] moonPosition;
    static float[] shadowLightPosition;
    static float[] upPosition;
    static float[] shadowLightPositionVector;
    static float[] upPosModelView;
    static float[] sunPosModelView;
    static float[] moonPosModelView;
    private static float[] tempMat;
    static float clearColorR;
    static float clearColorG;
    static float clearColorB;
    static float skyColorR;
    static float skyColorG;
    static float skyColorB;
    static long worldTime;
    static long lastWorldTime;
    static long diffWorldTime;
    static float celestialAngle;
    static float sunAngle;
    static float shadowAngle;
    static int moonPhase;
    static long systemTime;
    static long lastSystemTime;
    static long diffSystemTime;
    static int frameCounter;
    static float frameTime;
    static float frameTimeCounter;
    static int systemTimeInt32;
    static float rainStrength;
    static float wetness;
    public static float wetnessHalfLife;
    public static float drynessHalfLife;
    public static float eyeBrightnessHalflife;
    static boolean usewetness;
    static int isEyeInWater;
    static int eyeBrightness;
    static float eyeBrightnessFadeX;
    static float eyeBrightnessFadeY;
    static float eyePosY;
    static float centerDepth;
    static float centerDepthSmooth;
    static float centerDepthSmoothHalflife;
    static boolean centerDepthSmoothEnabled;
    static int superSamplingLevel;
    static float nightVision;
    static float blindness;
    static boolean lightmapEnabled;
    static boolean fogEnabled;
    public static int entityAttrib;
    public static int midTexCoordAttrib;
    public static int tangentAttrib;
    public static boolean useEntityAttrib;
    public static boolean useMidTexCoordAttrib;
    public static boolean useTangentAttrib;
    public static boolean progUseEntityAttrib;
    public static boolean progUseMidTexCoordAttrib;
    public static boolean progUseTangentAttrib;
    private static boolean progArbGeometryShader4;
    private static int progMaxVerticesOut;
    private static boolean hasGeometryShaders;
    public static int atlasSizeX;
    public static int atlasSizeY;
    private static ShaderUniforms shaderUniforms;
    public static ShaderUniform4f uniform_entityColor;
    public static ShaderUniform1i uniform_entityId;
    public static ShaderUniform1i uniform_blockEntityId;
    public static ShaderUniform1i uniform_texture;
    public static ShaderUniform1i uniform_lightmap;
    public static ShaderUniform1i uniform_normals;
    public static ShaderUniform1i uniform_specular;
    public static ShaderUniform1i uniform_shadow;
    public static ShaderUniform1i uniform_watershadow;
    public static ShaderUniform1i uniform_shadowtex0;
    public static ShaderUniform1i uniform_shadowtex1;
    public static ShaderUniform1i uniform_depthtex0;
    public static ShaderUniform1i uniform_depthtex1;
    public static ShaderUniform1i uniform_shadowcolor;
    public static ShaderUniform1i uniform_shadowcolor0;
    public static ShaderUniform1i uniform_shadowcolor1;
    public static ShaderUniform1i uniform_noisetex;
    public static ShaderUniform1i uniform_gcolor;
    public static ShaderUniform1i uniform_gdepth;
    public static ShaderUniform1i uniform_gnormal;
    public static ShaderUniform1i uniform_composite;
    public static ShaderUniform1i uniform_gaux1;
    public static ShaderUniform1i uniform_gaux2;
    public static ShaderUniform1i uniform_gaux3;
    public static ShaderUniform1i uniform_gaux4;
    public static ShaderUniform1i uniform_colortex0;
    public static ShaderUniform1i uniform_colortex1;
    public static ShaderUniform1i uniform_colortex2;
    public static ShaderUniform1i uniform_colortex3;
    public static ShaderUniform1i uniform_colortex4;
    public static ShaderUniform1i uniform_colortex5;
    public static ShaderUniform1i uniform_colortex6;
    public static ShaderUniform1i uniform_colortex7;
    public static ShaderUniform1i uniform_gdepthtex;
    public static ShaderUniform1i uniform_depthtex2;
    public static ShaderUniform1i uniform_tex;
    public static ShaderUniform1i uniform_heldItemId;
    public static ShaderUniform1i uniform_heldBlockLightValue;
    public static ShaderUniform1i uniform_heldItemId2;
    public static ShaderUniform1i uniform_heldBlockLightValue2;
    public static ShaderUniform1i uniform_fogMode;
    public static ShaderUniform1f uniform_fogDensity;
    public static ShaderUniform3f uniform_fogColor;
    public static ShaderUniform3f uniform_skyColor;
    public static ShaderUniform1i uniform_worldTime;
    public static ShaderUniform1i uniform_worldDay;
    public static ShaderUniform1i uniform_moonPhase;
    public static ShaderUniform1i uniform_frameCounter;
    public static ShaderUniform1f uniform_frameTime;
    public static ShaderUniform1f uniform_frameTimeCounter;
    public static ShaderUniform1f uniform_sunAngle;
    public static ShaderUniform1f uniform_shadowAngle;
    public static ShaderUniform1f uniform_rainStrength;
    public static ShaderUniform1f uniform_aspectRatio;
    public static ShaderUniform1f uniform_viewWidth;
    public static ShaderUniform1f uniform_viewHeight;
    public static ShaderUniform1f uniform_near;
    public static ShaderUniform1f uniform_far;
    public static ShaderUniform3f uniform_sunPosition;
    public static ShaderUniform3f uniform_moonPosition;
    public static ShaderUniform3f uniform_shadowLightPosition;
    public static ShaderUniform3f uniform_upPosition;
    public static ShaderUniform3f uniform_previousCameraPosition;
    public static ShaderUniform3f uniform_cameraPosition;
    public static ShaderUniformM4 uniform_gbufferModelView;
    public static ShaderUniformM4 uniform_gbufferModelViewInverse;
    public static ShaderUniformM4 uniform_gbufferPreviousProjection;
    public static ShaderUniformM4 uniform_gbufferProjection;
    public static ShaderUniformM4 uniform_gbufferProjectionInverse;
    public static ShaderUniformM4 uniform_gbufferPreviousModelView;
    public static ShaderUniformM4 uniform_shadowProjection;
    public static ShaderUniformM4 uniform_shadowProjectionInverse;
    public static ShaderUniformM4 uniform_shadowModelView;
    public static ShaderUniformM4 uniform_shadowModelViewInverse;
    public static ShaderUniform1f uniform_wetness;
    public static ShaderUniform1f uniform_eyeAltitude;
    public static ShaderUniform2i uniform_eyeBrightness;
    public static ShaderUniform2i uniform_eyeBrightnessSmooth;
    public static ShaderUniform2i uniform_terrainTextureSize;
    public static ShaderUniform1i uniform_terrainIconSize;
    public static ShaderUniform1i uniform_isEyeInWater;
    public static ShaderUniform1f uniform_nightVision;
    public static ShaderUniform1f uniform_blindness;
    public static ShaderUniform1f uniform_screenBrightness;
    public static ShaderUniform1i uniform_hideGUI;
    public static ShaderUniform1f uniform_centerDepthSmooth;
    public static ShaderUniform2i uniform_atlasSize;
    public static ShaderUniform4i uniform_blendFunc;
    public static ShaderUniform1i uniform_instanceId;
    static double previousCameraPositionX;
    static double previousCameraPositionY;
    static double previousCameraPositionZ;
    static double cameraPositionX;
    static double cameraPositionY;
    static double cameraPositionZ;
    static int cameraOffsetX;
    static int cameraOffsetZ;
    static int shadowPassInterval;
    public static boolean needResizeShadow;
    static int shadowMapWidth;
    static int shadowMapHeight;
    static int spShadowMapWidth;
    static int spShadowMapHeight;
    static float shadowMapFOV;
    static float shadowMapHalfPlane;
    static boolean shadowMapIsOrtho;
    static float shadowDistanceRenderMul;
    static int shadowPassCounter;
    static int preShadowPassThirdPersonView;
    public static boolean shouldSkipDefaultShadow;
    static boolean waterShadowEnabled;
    static final int MaxDrawBuffers = 8;
    static final int MaxColorBuffers = 8;
    static final int MaxDepthBuffers = 3;
    static final int MaxShadowColorBuffers = 8;
    static final int MaxShadowDepthBuffers = 2;
    static int usedColorBuffers;
    static int usedDepthBuffers;
    static int usedShadowColorBuffers;
    static int usedShadowDepthBuffers;
    static int usedColorAttachs;
    static int usedDrawBuffers;
    static int dfb;
    static int sfb;
    private static int[] gbuffersFormat;
    public static boolean[] gbuffersClear;
    public static Vector4f[] gbuffersClearColor;
    private static Programs programs;
    public static final Program ProgramNone;
    public static final Program ProgramShadow;
    public static final Program ProgramShadowSolid;
    public static final Program ProgramShadowCutout;
    public static final Program ProgramBasic;
    public static final Program ProgramTextured;
    public static final Program ProgramTexturedLit;
    public static final Program ProgramSkyBasic;
    public static final Program ProgramSkyTextured;
    public static final Program ProgramClouds;
    public static final Program ProgramTerrain;
    public static final Program ProgramTerrainSolid;
    public static final Program ProgramTerrainCutoutMip;
    public static final Program ProgramTerrainCutout;
    public static final Program ProgramDamagedBlock;
    public static final Program ProgramBlock;
    public static final Program ProgramBeaconBeam;
    public static final Program ProgramItem;
    public static final Program ProgramEntities;
    public static final Program ProgramEntitiesGlowing;
    public static final Program ProgramArmorGlint;
    public static final Program ProgramSpiderEyes;
    public static final Program ProgramHand;
    public static final Program ProgramWeather;
    public static final Program ProgramDeferredPre;
    public static final Program[] ProgramsDeferred;
    public static final Program ProgramDeferred;
    public static final Program ProgramWater;
    public static final Program ProgramHandWater;
    public static final Program ProgramCompositePre;
    public static final Program[] ProgramsComposite;
    public static final Program ProgramComposite;
    public static final Program ProgramFinal;
    public static final int ProgramCount;
    public static final Program[] ProgramsAll;
    public static Program activeProgram;
    public static int activeProgramID;
    private static ProgramStack programStackLeash;
    private static boolean hasDeferredPrograms;
    static IntBuffer activeDrawBuffers;
    private static int activeCompositeMipmapSetting;
    public static Properties loadedShaders;
    public static Properties shadersConfig;
    public static ITextureObject defaultTexture;
    public static boolean[] shadowHardwareFilteringEnabled;
    public static boolean[] shadowMipmapEnabled;
    public static boolean[] shadowFilterNearest;
    public static boolean[] shadowColorMipmapEnabled;
    public static boolean[] shadowColorFilterNearest;
    public static boolean configTweakBlockDamage;
    public static boolean configCloudShadow;
    public static float configHandDepthMul;
    public static float configRenderResMul;
    public static float configShadowResMul;
    public static int configTexMinFilB;
    public static int configTexMinFilN;
    public static int configTexMinFilS;
    public static int configTexMagFilB;
    public static int configTexMagFilN;
    public static int configTexMagFilS;
    public static boolean configShadowClipFrustrum;
    public static boolean configNormalMap;
    public static boolean configSpecularMap;
    public static PropertyDefaultTrueFalse configOldLighting;
    public static PropertyDefaultTrueFalse configOldHandLight;
    public static int configAntialiasingLevel;
    public static final int texMinFilRange = 3;
    public static final int texMagFilRange = 2;
    public static final String[] texMinFilDesc;
    public static final String[] texMagFilDesc;
    public static final int[] texMinFilValue;
    public static final int[] texMagFilValue;
    private static IShaderPack shaderPack;
    public static boolean shaderPackLoaded;
    public static String currentShaderName;
    public static final String SHADER_PACK_NAME_NONE = "OFF";
    public static final String SHADER_PACK_NAME_DEFAULT = "(internal)";
    public static final String SHADER_PACKS_DIR_NAME = "shaderpacks";
    public static final String OPTIONS_FILE_NAME = "optionsshaders.txt";
    public static final File shaderPacksDir;
    static File configFile;
    private static ShaderOption[] shaderPackOptions;
    private static Set<String> shaderPackOptionSliders;
    static ShaderProfile[] shaderPackProfiles;
    static Map<String, ScreenShaderOptions> shaderPackGuiScreens;
    static Map<String, IExpressionBool> shaderPackProgramConditions;
    public static final String PATH_SHADERS_PROPERTIES = "/shaders/shaders.properties";
    public static PropertyDefaultFastFancyOff shaderPackClouds;
    public static PropertyDefaultTrueFalse shaderPackOldLighting;
    public static PropertyDefaultTrueFalse shaderPackOldHandLight;
    public static PropertyDefaultTrueFalse shaderPackDynamicHandLight;
    public static PropertyDefaultTrueFalse shaderPackShadowTranslucent;
    public static PropertyDefaultTrueFalse shaderPackUnderwaterOverlay;
    public static PropertyDefaultTrueFalse shaderPackSun;
    public static PropertyDefaultTrueFalse shaderPackMoon;
    public static PropertyDefaultTrueFalse shaderPackVignette;
    public static PropertyDefaultTrueFalse shaderPackBackFaceSolid;
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutout;
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutoutMipped;
    public static PropertyDefaultTrueFalse shaderPackBackFaceTranslucent;
    public static PropertyDefaultTrueFalse shaderPackRainDepth;
    public static PropertyDefaultTrueFalse shaderPackBeaconBeamDepth;
    public static PropertyDefaultTrueFalse shaderPackSeparateAo;
    public static PropertyDefaultTrueFalse shaderPackFrustumCulling;
    private static Map<String, String> shaderPackResources;
    private static World currentWorld;
    private static List<Integer> shaderPackDimensions;
    private static ICustomTexture[] customTexturesGbuffers;
    private static ICustomTexture[] customTexturesComposite;
    private static ICustomTexture[] customTexturesDeferred;
    private static String noiseTexturePath;
    private static CustomUniforms customUniforms;
    private static final int STAGE_GBUFFERS = 0;
    private static final int STAGE_COMPOSITE = 1;
    private static final int STAGE_DEFERRED = 2;
    private static final String[] STAGE_NAMES;
    public static final boolean enableShadersOption = true;
    private static final boolean enableShadersDebug = true;
    public static final boolean saveFinalShaders;
    public static float blockLightLevel05;
    public static float blockLightLevel06;
    public static float blockLightLevel08;
    public static float aoLevel;
    public static float sunPathRotation;
    public static float shadowAngleInterval;
    public static int fogMode;
    public static float fogDensity;
    public static float fogColorR;
    public static float fogColorG;
    public static float fogColorB;
    public static float shadowIntervalSize;
    public static int terrainIconSize;
    public static int[] terrainTextureSize;
    private static ICustomTexture noiseTexture;
    private static boolean noiseTextureEnabled;
    private static int noiseTextureResolution;
    static final int[] colorTextureImageUnit;
    private static final int bigBufferSize;
    private static final ByteBuffer bigBuffer;
    static final float[] faProjection;
    static final float[] faProjectionInverse;
    static final float[] faModelView;
    static final float[] faModelViewInverse;
    static final float[] faShadowProjection;
    static final float[] faShadowProjectionInverse;
    static final float[] faShadowModelView;
    static final float[] faShadowModelViewInverse;
    static final FloatBuffer projection;
    static final FloatBuffer projectionInverse;
    static final FloatBuffer modelView;
    static final FloatBuffer modelViewInverse;
    static final FloatBuffer shadowProjection;
    static final FloatBuffer shadowProjectionInverse;
    static final FloatBuffer shadowModelView;
    static final FloatBuffer shadowModelViewInverse;
    static final FloatBuffer previousProjection;
    static final FloatBuffer previousModelView;
    static final FloatBuffer tempMatrixDirectBuffer;
    static final FloatBuffer tempDirectFloatBuffer;
    static final IntBuffer dfbColorTextures;
    static final IntBuffer dfbDepthTextures;
    static final IntBuffer sfbColorTextures;
    static final IntBuffer sfbDepthTextures;
    static final IntBuffer dfbDrawBuffers;
    static final IntBuffer sfbDrawBuffers;
    static final IntBuffer drawBuffersNone;
    static final IntBuffer drawBuffersColorAtt0;
    static final FlipTextures dfbColorTexturesFlip;
    static Map<Block, Integer> mapBlockToEntityData;
    private static final String[] formatNames;
    private static final int[] formatIds;
    private static final Pattern patternLoadEntityDataMap;
    public static int[] entityData;
    public static int entityDataIndex;

    private static ByteBuffer nextByteBuffer(int size) {
        ByteBuffer bytebuffer = bigBuffer;
        int i = bytebuffer.limit();
        bytebuffer.position(i).limit(i + size);
        return bytebuffer.slice();
    }

    public static IntBuffer nextIntBuffer(int size) {
        ByteBuffer bytebuffer = bigBuffer;
        int i = bytebuffer.limit();
        bytebuffer.position(i).limit(i + size * 4);
        return bytebuffer.asIntBuffer();
    }

    private static FloatBuffer nextFloatBuffer(int size) {
        ByteBuffer bytebuffer = bigBuffer;
        int i = bytebuffer.limit();
        bytebuffer.position(i).limit(i + size * 4);
        return bytebuffer.asFloatBuffer();
    }

    private static IntBuffer[] nextIntBufferArray(int count, int size) {
        IntBuffer[] aintbuffer = new IntBuffer[count];
        for (int i = 0; i < count; ++i) {
            aintbuffer[i] = Shaders.nextIntBuffer(size);
        }
        return aintbuffer;
    }

    public static void loadConfig() {
        SMCLog.info("Load shaders configuration.");
        try {
            if (!shaderPacksDir.exists()) {
                shaderPacksDir.mkdir();
            }
        }
        catch (Exception var8) {
            SMCLog.severe("Failed to open the shaderpacks directory: " + shaderPacksDir);
        }
        shadersConfig = new PropertiesOrdered();
        shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "");
        if (configFile.exists()) {
            try {
                FileReader filereader = new FileReader(configFile);
                shadersConfig.load(filereader);
                filereader.close();
            }
            catch (Exception filereader) {
                // empty catch block
            }
        }
        if (!configFile.exists()) {
            try {
                Shaders.storeConfig();
            }
            catch (Exception filereader) {
                // empty catch block
            }
        }
        EnumShaderOption[] aenumshaderoption = EnumShaderOption.values();
        for (int i = 0; i < aenumshaderoption.length; ++i) {
            EnumShaderOption enumshaderoption = aenumshaderoption[i];
            String s = enumshaderoption.getPropertyKey();
            String s1 = enumshaderoption.getValueDefault();
            String s2 = shadersConfig.getProperty(s, s1);
            Shaders.setEnumShaderOption(enumshaderoption, s2);
        }
        Shaders.loadShaderPack();
    }

    private static void setEnumShaderOption(EnumShaderOption eso, String str) {
        if (str == null) {
            str = eso.getValueDefault();
        }
        switch (eso) {
            case ANTIALIASING: {
                configAntialiasingLevel = Config.parseInt(str, 0);
                break;
            }
            case NORMAL_MAP: {
                configNormalMap = Config.parseBoolean(str, true);
                break;
            }
            case SPECULAR_MAP: {
                configSpecularMap = Config.parseBoolean(str, true);
                break;
            }
            case RENDER_RES_MUL: {
                configRenderResMul = Config.parseFloat(str, 1.0f);
                break;
            }
            case SHADOW_RES_MUL: {
                configShadowResMul = Config.parseFloat(str, 1.0f);
                break;
            }
            case HAND_DEPTH_MUL: {
                configHandDepthMul = Config.parseFloat(str, 0.125f);
                break;
            }
            case CLOUD_SHADOW: {
                configCloudShadow = Config.parseBoolean(str, true);
                break;
            }
            case OLD_HAND_LIGHT: {
                configOldHandLight.setPropertyValue(str);
                break;
            }
            case OLD_LIGHTING: {
                configOldLighting.setPropertyValue(str);
                break;
            }
            case SHADER_PACK: {
                currentShaderName = str;
                break;
            }
            case TWEAK_BLOCK_DAMAGE: {
                configTweakBlockDamage = Config.parseBoolean(str, true);
                break;
            }
            case SHADOW_CLIP_FRUSTRUM: {
                configShadowClipFrustrum = Config.parseBoolean(str, true);
                break;
            }
            case TEX_MIN_FIL_B: {
                configTexMinFilB = Config.parseInt(str, 0);
                break;
            }
            case TEX_MIN_FIL_N: {
                configTexMinFilN = Config.parseInt(str, 0);
                break;
            }
            case TEX_MIN_FIL_S: {
                configTexMinFilS = Config.parseInt(str, 0);
                break;
            }
            case TEX_MAG_FIL_B: {
                configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            case TEX_MAG_FIL_N: {
                configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            case TEX_MAG_FIL_S: {
                configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown option: " + (Object)((Object)eso));
            }
        }
    }

    public static void storeConfig() {
        SMCLog.info("Save shaders configuration.");
        if (shadersConfig == null) {
            shadersConfig = new PropertiesOrdered();
        }
        EnumShaderOption[] aenumshaderoption = EnumShaderOption.values();
        for (int i = 0; i < aenumshaderoption.length; ++i) {
            EnumShaderOption enumshaderoption = aenumshaderoption[i];
            String s = enumshaderoption.getPropertyKey();
            String s1 = Shaders.getEnumShaderOption(enumshaderoption);
            shadersConfig.setProperty(s, s1);
        }
        try {
            FileWriter filewriter = new FileWriter(configFile);
            shadersConfig.store(filewriter, (String)null);
            filewriter.close();
        }
        catch (Exception exception) {
            SMCLog.severe("Error saving configuration: " + exception.getClass().getName() + ": " + exception.getMessage());
        }
    }

    public static String getEnumShaderOption(EnumShaderOption eso) {
        switch (eso) {
            case ANTIALIASING: {
                return Integer.toString(configAntialiasingLevel);
            }
            case NORMAL_MAP: {
                return Boolean.toString(configNormalMap);
            }
            case SPECULAR_MAP: {
                return Boolean.toString(configSpecularMap);
            }
            case RENDER_RES_MUL: {
                return Float.toString(configRenderResMul);
            }
            case SHADOW_RES_MUL: {
                return Float.toString(configShadowResMul);
            }
            case HAND_DEPTH_MUL: {
                return Float.toString(configHandDepthMul);
            }
            case CLOUD_SHADOW: {
                return Boolean.toString(configCloudShadow);
            }
            case OLD_HAND_LIGHT: {
                return configOldHandLight.getPropertyValue();
            }
            case OLD_LIGHTING: {
                return configOldLighting.getPropertyValue();
            }
            case SHADER_PACK: {
                return currentShaderName;
            }
            case TWEAK_BLOCK_DAMAGE: {
                return Boolean.toString(configTweakBlockDamage);
            }
            case SHADOW_CLIP_FRUSTRUM: {
                return Boolean.toString(configShadowClipFrustrum);
            }
            case TEX_MIN_FIL_B: {
                return Integer.toString(configTexMinFilB);
            }
            case TEX_MIN_FIL_N: {
                return Integer.toString(configTexMinFilN);
            }
            case TEX_MIN_FIL_S: {
                return Integer.toString(configTexMinFilS);
            }
            case TEX_MAG_FIL_B: {
                return Integer.toString(configTexMagFilB);
            }
            case TEX_MAG_FIL_N: {
                return Integer.toString(configTexMagFilB);
            }
            case TEX_MAG_FIL_S: {
                return Integer.toString(configTexMagFilB);
            }
        }
        throw new IllegalArgumentException("Unknown option: " + (Object)((Object)eso));
    }

    public static void setShaderPack(String par1name) {
        currentShaderName = par1name;
        shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), par1name);
        Shaders.loadShaderPack();
    }

    public static void loadShaderPack() {
        boolean flag4;
        boolean flag = shaderPackLoaded;
        boolean flag1 = Shaders.isOldLighting();
        if (Shaders.mc.renderGlobal != null) {
            Shaders.mc.renderGlobal.pauseChunkUpdates();
        }
        shaderPackLoaded = false;
        if (shaderPack != null) {
            shaderPack.close();
            shaderPack = null;
            shaderPackResources.clear();
            shaderPackDimensions.clear();
            shaderPackOptions = null;
            shaderPackOptionSliders = null;
            shaderPackProfiles = null;
            shaderPackGuiScreens = null;
            shaderPackProgramConditions.clear();
            shaderPackClouds.resetValue();
            shaderPackOldHandLight.resetValue();
            shaderPackDynamicHandLight.resetValue();
            shaderPackOldLighting.resetValue();
            Shaders.resetCustomTextures();
            noiseTexturePath = null;
        }
        boolean flag2 = false;
        if (Config.isAntialiasing()) {
            SMCLog.info("Shaders can not be loaded, Antialiasing is enabled: " + Config.getAntialiasingLevel() + "x");
            flag2 = true;
        }
        if (Config.isAnisotropicFiltering()) {
            SMCLog.info("Shaders can not be loaded, Anisotropic Filtering is enabled: " + Config.getAnisotropicFilterLevel() + "x");
            flag2 = true;
        }
        if (Config.isFastRender()) {
            SMCLog.info("Shaders can not be loaded, Fast Render is enabled.");
            flag2 = true;
        }
        String s = shadersConfig.getProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), SHADER_PACK_NAME_DEFAULT);
        if (!flag2) {
            shaderPack = Shaders.getShaderPack(s);
            boolean bl = shaderPackLoaded = shaderPack != null;
        }
        if (shaderPackLoaded) {
            SMCLog.info("Loaded shaderpack: " + Shaders.getShaderPackName());
        } else {
            SMCLog.info("No shaderpack loaded.");
            shaderPack = new ShaderPackNone();
        }
        if (saveFinalShaders) {
            Shaders.clearDirectory(new File(shaderPacksDir, "debug"));
        }
        Shaders.loadShaderPackResources();
        Shaders.loadShaderPackDimensions();
        shaderPackOptions = Shaders.loadShaderPackOptions();
        Shaders.loadShaderPackProperties();
        boolean flag3 = shaderPackLoaded != flag;
        boolean bl = flag4 = Shaders.isOldLighting() != flag1;
        if (flag3 || flag4) {
            DefaultVertexFormats.updateVertexFormats();
            if (Reflector.LightUtil.exists()) {
                Reflector.LightUtil_itemConsumer.setValue(null);
                Reflector.LightUtil_tessellator.setValue(null);
            }
            Shaders.updateBlockLightLevel();
        }
        if (mc.getResourcePackRepository() != null) {
            CustomBlockLayers.update();
        }
        if (Shaders.mc.renderGlobal != null) {
            Shaders.mc.renderGlobal.resumeChunkUpdates();
        }
        if ((flag3 || flag4) && mc.getResourceManager() != null) {
            mc.scheduleResourcesRefresh();
        }
    }

    public static IShaderPack getShaderPack(String name) {
        if (name == null) {
            return null;
        }
        if (!(name = name.trim()).isEmpty() && !name.equals(SHADER_PACK_NAME_NONE)) {
            if (name.equals(SHADER_PACK_NAME_DEFAULT)) {
                return new ShaderPackDefault();
            }
            try {
                File file1 = new File(shaderPacksDir, name);
                return file1.isDirectory() ? new ShaderPackFolder(name, file1) : (file1.isFile() && name.toLowerCase().endsWith(".zip") ? new ShaderPackZip(name, file1) : null);
            }
            catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static IShaderPack getShaderPack() {
        return shaderPack;
    }

    private static void loadShaderPackDimensions() {
        shaderPackDimensions.clear();
        for (int i = -128; i <= 128; ++i) {
            String s = "/shaders/world" + i;
            if (!shaderPack.hasDirectory(s)) continue;
            shaderPackDimensions.add(i);
        }
        if (shaderPackDimensions.size() > 0) {
            Integer[] ainteger = shaderPackDimensions.toArray(new Integer[shaderPackDimensions.size()]);
            Config.dbg("[Shaders] Worlds: " + Config.arrayToString((Object[])ainteger));
        }
    }

    private static void loadShaderPackProperties() {
        shaderPackClouds.resetValue();
        shaderPackOldHandLight.resetValue();
        shaderPackDynamicHandLight.resetValue();
        shaderPackOldLighting.resetValue();
        shaderPackShadowTranslucent.resetValue();
        shaderPackUnderwaterOverlay.resetValue();
        shaderPackSun.resetValue();
        shaderPackMoon.resetValue();
        shaderPackVignette.resetValue();
        shaderPackBackFaceSolid.resetValue();
        shaderPackBackFaceCutout.resetValue();
        shaderPackBackFaceCutoutMipped.resetValue();
        shaderPackBackFaceTranslucent.resetValue();
        shaderPackRainDepth.resetValue();
        shaderPackBeaconBeamDepth.resetValue();
        shaderPackSeparateAo.resetValue();
        shaderPackFrustumCulling.resetValue();
        BlockAliases.reset();
        ItemAliases.reset();
        EntityAliases.reset();
        customUniforms = null;
        for (int i = 0; i < ProgramsAll.length; ++i) {
            Program program = ProgramsAll[i];
            program.resetProperties();
        }
        if (shaderPack != null) {
            BlockAliases.update(shaderPack);
            ItemAliases.update(shaderPack);
            EntityAliases.update(shaderPack);
            String s = PATH_SHADERS_PROPERTIES;
            try {
                InputStream inputstream = shaderPack.getResourceAsStream(s);
                if (inputstream == null) {
                    return;
                }
                inputstream = MacroProcessor.process(inputstream, s);
                PropertiesOrdered properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                shaderPackClouds.loadFrom(properties);
                shaderPackOldHandLight.loadFrom(properties);
                shaderPackDynamicHandLight.loadFrom(properties);
                shaderPackOldLighting.loadFrom(properties);
                shaderPackShadowTranslucent.loadFrom(properties);
                shaderPackUnderwaterOverlay.loadFrom(properties);
                shaderPackSun.loadFrom(properties);
                shaderPackVignette.loadFrom(properties);
                shaderPackMoon.loadFrom(properties);
                shaderPackBackFaceSolid.loadFrom(properties);
                shaderPackBackFaceCutout.loadFrom(properties);
                shaderPackBackFaceCutoutMipped.loadFrom(properties);
                shaderPackBackFaceTranslucent.loadFrom(properties);
                shaderPackRainDepth.loadFrom(properties);
                shaderPackBeaconBeamDepth.loadFrom(properties);
                shaderPackSeparateAo.loadFrom(properties);
                shaderPackFrustumCulling.loadFrom(properties);
                shaderPackOptionSliders = ShaderPackParser.parseOptionSliders(properties, shaderPackOptions);
                shaderPackProfiles = ShaderPackParser.parseProfiles(properties, shaderPackOptions);
                shaderPackGuiScreens = ShaderPackParser.parseGuiScreens(properties, shaderPackProfiles, shaderPackOptions);
                shaderPackProgramConditions = ShaderPackParser.parseProgramConditions(properties, shaderPackOptions);
                customTexturesGbuffers = Shaders.loadCustomTextures(properties, 0);
                customTexturesComposite = Shaders.loadCustomTextures(properties, 1);
                customTexturesDeferred = Shaders.loadCustomTextures(properties, 2);
                noiseTexturePath = properties.getProperty("texture.noise");
                if (noiseTexturePath != null) {
                    noiseTextureEnabled = true;
                }
                customUniforms = ShaderPackParser.parseCustomUniforms(properties);
                ShaderPackParser.parseAlphaStates(properties);
                ShaderPackParser.parseBlendStates(properties);
                ShaderPackParser.parseRenderScales(properties);
                ShaderPackParser.parseBuffersFlip(properties);
            }
            catch (IOException var3) {
                Config.warn("[Shaders] Error reading: " + s);
            }
        }
    }

    private static ICustomTexture[] loadCustomTextures(Properties props, int stage) {
        String s = "texture." + STAGE_NAMES[stage] + ".";
        Set<Object> set = props.keySet();
        ArrayList<ICustomTexture> list = new ArrayList<ICustomTexture>();
        for (Object e : set) {
            String s1 = (String)e;
            if (!s1.startsWith(s)) continue;
            String s2 = StrUtils.removePrefix(s1, s);
            s2 = StrUtils.removeSuffix(s2, new String[]{".0", ".1", ".2", ".3", ".4", ".5", ".6", ".7", ".8", ".9"});
            String s3 = props.getProperty(s1).trim();
            int i = Shaders.getTextureIndex(stage, s2);
            if (i < 0) {
                SMCLog.warning("Invalid texture name: " + s1);
                continue;
            }
            ICustomTexture icustomtexture = Shaders.loadCustomTexture(i, s3);
            if (icustomtexture == null) continue;
            SMCLog.info("Custom texture: " + s1 + " = " + s3);
            list.add(icustomtexture);
        }
        if (list.size() <= 0) {
            return null;
        }
        ICustomTexture[] aicustomtexture = list.toArray(new ICustomTexture[list.size()]);
        return aicustomtexture;
    }

    private static ICustomTexture loadCustomTexture(int textureUnit, String path) {
        if (path == null) {
            return null;
        }
        return (path = path.trim()).indexOf(58) >= 0 ? Shaders.loadCustomTextureLocation(textureUnit, path) : (path.indexOf(32) >= 0 ? Shaders.loadCustomTextureRaw(textureUnit, path) : Shaders.loadCustomTextureShaders(textureUnit, path));
    }

    private static ICustomTexture loadCustomTextureLocation(int textureUnit, String path) {
        String s = path.trim();
        int i = 0;
        if (s.startsWith("minecraft:textures/")) {
            if ((s = StrUtils.addSuffixCheck(s, ".png")).endsWith("_n.png")) {
                s = StrUtils.replaceSuffix(s, "_n.png", ".png");
                i = 1;
            } else if (s.endsWith("_s.png")) {
                s = StrUtils.replaceSuffix(s, "_s.png", ".png");
                i = 2;
            }
        }
        ResourceLocation resourcelocation = new ResourceLocation(s);
        CustomTextureLocation customtexturelocation = new CustomTextureLocation(textureUnit, resourcelocation, i);
        return customtexturelocation;
    }

    private static ICustomTexture loadCustomTextureRaw(int textureUnit, String line) {
        ConnectedParser connectedparser = new ConnectedParser("Shaders");
        String[] astring = Config.tokenize(line, " ");
        ArrayDeque<String> deque = new ArrayDeque<String>(Arrays.asList(astring));
        String s = (String)deque.poll();
        TextureType texturetype = (TextureType)connectedparser.parseEnum((String)deque.poll(), TextureType.values(), "texture type");
        if (texturetype == null) {
            SMCLog.warning("Invalid raw texture type: " + line);
            return null;
        }
        InternalFormat internalformat = (InternalFormat)connectedparser.parseEnum((String)deque.poll(), InternalFormat.values(), "internal format");
        if (internalformat == null) {
            SMCLog.warning("Invalid raw texture internal format: " + line);
            return null;
        }
        int i = 0;
        int j = 0;
        int k = 0;
        switch (texturetype) {
            case TEXTURE_1D: {
                i = connectedparser.parseInt((String)deque.poll(), -1);
                break;
            }
            case TEXTURE_2D: {
                i = connectedparser.parseInt((String)deque.poll(), -1);
                j = connectedparser.parseInt((String)deque.poll(), -1);
                break;
            }
            case TEXTURE_3D: {
                i = connectedparser.parseInt((String)deque.poll(), -1);
                j = connectedparser.parseInt((String)deque.poll(), -1);
                k = connectedparser.parseInt((String)deque.poll(), -1);
                break;
            }
            case TEXTURE_RECTANGLE: {
                i = connectedparser.parseInt((String)deque.poll(), -1);
                j = connectedparser.parseInt((String)deque.poll(), -1);
                break;
            }
            default: {
                SMCLog.warning("Invalid raw texture type: " + (Object)((Object)texturetype));
                return null;
            }
        }
        if (i >= 0 && j >= 0 && k >= 0) {
            PixelFormat pixelformat = (PixelFormat)connectedparser.parseEnum((String)deque.poll(), PixelFormat.values(), "pixel format");
            if (pixelformat == null) {
                SMCLog.warning("Invalid raw texture pixel format: " + line);
                return null;
            }
            PixelType pixeltype = (PixelType)connectedparser.parseEnum((String)deque.poll(), PixelType.values(), "pixel type");
            if (pixeltype == null) {
                SMCLog.warning("Invalid raw texture pixel type: " + line);
                return null;
            }
            if (!deque.isEmpty()) {
                SMCLog.warning("Invalid raw texture, too many parameters: " + line);
                return null;
            }
            return Shaders.loadCustomTextureRaw(textureUnit, line, s, texturetype, internalformat, i, j, k, pixelformat, pixeltype);
        }
        SMCLog.warning("Invalid raw texture size: " + line);
        return null;
    }

    private static ICustomTexture loadCustomTextureRaw(int textureUnit, String line, String path, TextureType type, InternalFormat internalFormat, int width, int height, int depth, PixelFormat pixelFormat, PixelType pixelType) {
        try {
            String s = "shaders/" + StrUtils.removePrefix(path, "/");
            InputStream inputstream = shaderPack.getResourceAsStream(s);
            if (inputstream == null) {
                SMCLog.warning("Raw texture not found: " + path);
                return null;
            }
            byte[] abyte = Config.readAll(inputstream);
            IOUtils.closeQuietly((InputStream)inputstream);
            ByteBuffer bytebuffer = GLAllocation.createDirectByteBuffer(abyte.length);
            bytebuffer.put(abyte);
            bytebuffer.flip();
            CustomTextureRaw customtextureraw = new CustomTextureRaw(type, internalFormat, width, height, depth, pixelFormat, pixelType, bytebuffer, textureUnit);
            return customtextureraw;
        }
        catch (IOException ioexception) {
            SMCLog.warning("Error loading raw texture: " + path);
            SMCLog.warning("" + ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return null;
        }
    }

    private static ICustomTexture loadCustomTextureShaders(int textureUnit, String path) {
        if ((path = path.trim()).indexOf(46) < 0) {
            path = path + ".png";
        }
        try {
            String s = "shaders/" + StrUtils.removePrefix(path, "/");
            InputStream inputstream = shaderPack.getResourceAsStream(s);
            if (inputstream == null) {
                SMCLog.warning("Texture not found: " + path);
                return null;
            }
            IOUtils.closeQuietly((InputStream)inputstream);
            SimpleShaderTexture simpleshadertexture = new SimpleShaderTexture(s);
            simpleshadertexture.loadTexture(mc.getResourceManager());
            CustomTexture customtexture = new CustomTexture(textureUnit, s, simpleshadertexture);
            return customtexture;
        }
        catch (IOException ioexception) {
            SMCLog.warning("Error loading texture: " + path);
            SMCLog.warning("" + ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return null;
        }
    }

    private static int getTextureIndex(int stage, String name) {
        if (stage == 0) {
            if (name.equals("texture")) {
                return 0;
            }
            if (name.equals("lightmap")) {
                return 1;
            }
            if (name.equals("normals")) {
                return 2;
            }
            if (name.equals("specular")) {
                return 3;
            }
            if (name.equals("shadowtex0") || name.equals("watershadow")) {
                return 4;
            }
            if (name.equals("shadow")) {
                return waterShadowEnabled ? 5 : 4;
            }
            if (name.equals("shadowtex1")) {
                return 5;
            }
            if (name.equals("depthtex0")) {
                return 6;
            }
            if (name.equals("gaux1")) {
                return 7;
            }
            if (name.equals("gaux2")) {
                return 8;
            }
            if (name.equals("gaux3")) {
                return 9;
            }
            if (name.equals("gaux4")) {
                return 10;
            }
            if (name.equals("depthtex1")) {
                return 12;
            }
            if (name.equals("shadowcolor0") || name.equals("shadowcolor")) {
                return 13;
            }
            if (name.equals("shadowcolor1")) {
                return 14;
            }
            if (name.equals("noisetex")) {
                return 15;
            }
        }
        if (stage == 1 || stage == 2) {
            if (name.equals("colortex0") || name.equals("colortex0")) {
                return 0;
            }
            if (name.equals("colortex1") || name.equals("gdepth")) {
                return 1;
            }
            if (name.equals("colortex2") || name.equals("gnormal")) {
                return 2;
            }
            if (name.equals("colortex3") || name.equals("composite")) {
                return 3;
            }
            if (name.equals("shadowtex0") || name.equals("watershadow")) {
                return 4;
            }
            if (name.equals("shadow")) {
                return waterShadowEnabled ? 5 : 4;
            }
            if (name.equals("shadowtex1")) {
                return 5;
            }
            if (name.equals("depthtex0") || name.equals("gdepthtex")) {
                return 6;
            }
            if (name.equals("colortex4") || name.equals("gaux1")) {
                return 7;
            }
            if (name.equals("colortex5") || name.equals("gaux2")) {
                return 8;
            }
            if (name.equals("colortex6") || name.equals("gaux3")) {
                return 9;
            }
            if (name.equals("colortex7") || name.equals("gaux4")) {
                return 10;
            }
            if (name.equals("depthtex1")) {
                return 11;
            }
            if (name.equals("depthtex2")) {
                return 12;
            }
            if (name.equals("shadowcolor0") || name.equals("shadowcolor")) {
                return 13;
            }
            if (name.equals("shadowcolor1")) {
                return 14;
            }
            if (name.equals("noisetex")) {
                return 15;
            }
        }
        return -1;
    }

    private static void bindCustomTextures(ICustomTexture[] cts) {
        if (cts != null) {
            for (int i = 0; i < cts.length; ++i) {
                ICustomTexture icustomtexture = cts[i];
                GlStateManager.setActiveTexture(33984 + icustomtexture.getTextureUnit());
                int j = icustomtexture.getTextureId();
                int k = icustomtexture.getTarget();
                if (k == 3553) {
                    GlStateManager.bindTexture(j);
                    continue;
                }
                GL11.glBindTexture((int)k, (int)j);
            }
        }
    }

    private static void resetCustomTextures() {
        Shaders.deleteCustomTextures(customTexturesGbuffers);
        Shaders.deleteCustomTextures(customTexturesComposite);
        Shaders.deleteCustomTextures(customTexturesDeferred);
        customTexturesGbuffers = null;
        customTexturesComposite = null;
        customTexturesDeferred = null;
    }

    private static void deleteCustomTextures(ICustomTexture[] cts) {
        if (cts != null) {
            for (int i = 0; i < cts.length; ++i) {
                ICustomTexture icustomtexture = cts[i];
                icustomtexture.deleteTexture();
            }
        }
    }

    public static ShaderOption[] getShaderPackOptions(String screenName) {
        Object[] ashaderoption = (ShaderOption[])shaderPackOptions.clone();
        if (shaderPackGuiScreens == null) {
            if (shaderPackProfiles != null) {
                ShaderOptionProfile shaderoptionprofile = new ShaderOptionProfile(shaderPackProfiles, (ShaderOption[])ashaderoption);
                ashaderoption = (ShaderOption[])Config.addObjectToArray(ashaderoption, shaderoptionprofile, 0);
            }
            ashaderoption = Shaders.getVisibleOptions((ShaderOption[])ashaderoption);
            return ashaderoption;
        }
        String s = screenName != null ? "screen." + screenName : "screen";
        ScreenShaderOptions screenshaderoptions = shaderPackGuiScreens.get(s);
        if (screenshaderoptions == null) {
            return new ShaderOption[0];
        }
        ShaderOption[] ashaderoption1 = screenshaderoptions.getShaderOptions();
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        for (int i = 0; i < ashaderoption1.length; ++i) {
            ShaderOption shaderoption = ashaderoption1[i];
            if (shaderoption == null) {
                list.add(null);
                continue;
            }
            if (shaderoption instanceof ShaderOptionRest) {
                ShaderOption[] ashaderoption2 = Shaders.getShaderOptionsRest(shaderPackGuiScreens, (ShaderOption[])ashaderoption);
                list.addAll(Arrays.asList(ashaderoption2));
                continue;
            }
            list.add(shaderoption);
        }
        ShaderOption[] ashaderoption3 = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption3;
    }

    public static int getShaderPackColumns(String screenName, int def) {
        String s;
        String string = s = screenName != null ? "screen." + screenName : "screen";
        if (shaderPackGuiScreens == null) {
            return def;
        }
        ScreenShaderOptions screenshaderoptions = shaderPackGuiScreens.get(s);
        return screenshaderoptions == null ? def : screenshaderoptions.getColumns();
    }

    private static ShaderOption[] getShaderOptionsRest(Map<String, ScreenShaderOptions> mapScreens, ShaderOption[] ops) {
        HashSet<String> set = new HashSet<String>();
        for (String s : mapScreens.keySet()) {
            ScreenShaderOptions screenshaderoptions = mapScreens.get(s);
            ShaderOption[] ashaderoption = screenshaderoptions.getShaderOptions();
            for (int i = 0; i < ashaderoption.length; ++i) {
                ShaderOption shaderoption = ashaderoption[i];
                if (shaderoption == null) continue;
                set.add(shaderoption.getName());
            }
        }
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        for (int j = 0; j < ops.length; ++j) {
            String s1;
            ShaderOption shaderoption1 = ops[j];
            if (!shaderoption1.isVisible() || set.contains(s1 = shaderoption1.getName())) continue;
            list.add(shaderoption1);
        }
        ShaderOption[] ashaderoption1 = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption1;
    }

    public static ShaderOption getShaderOption(String name) {
        return ShaderUtils.getShaderOption(name, shaderPackOptions);
    }

    public static ShaderOption[] getShaderPackOptions() {
        return shaderPackOptions;
    }

    public static boolean isShaderPackOptionSlider(String name) {
        return shaderPackOptionSliders == null ? false : shaderPackOptionSliders.contains(name);
    }

    private static ShaderOption[] getVisibleOptions(ShaderOption[] ops) {
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        for (int i = 0; i < ops.length; ++i) {
            ShaderOption shaderoption = ops[i];
            if (!shaderoption.isVisible()) continue;
            list.add(shaderoption);
        }
        ShaderOption[] ashaderoption = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption;
    }

    public static void saveShaderPackOptions() {
        Shaders.saveShaderPackOptions(shaderPackOptions, shaderPack);
    }

    private static void saveShaderPackOptions(ShaderOption[] sos, IShaderPack sp) {
        PropertiesOrdered properties = new PropertiesOrdered();
        if (shaderPackOptions != null) {
            for (int i = 0; i < sos.length; ++i) {
                ShaderOption shaderoption = sos[i];
                if (!shaderoption.isChanged() || !shaderoption.isEnabled()) continue;
                properties.setProperty(shaderoption.getName(), shaderoption.getValue());
            }
        }
        try {
            Shaders.saveOptionProperties(sp, properties);
        }
        catch (IOException ioexception) {
            Config.warn("[Shaders] Error saving configuration for " + shaderPack.getName());
            ioexception.printStackTrace();
        }
    }

    private static void saveOptionProperties(IShaderPack sp, Properties props) throws IOException {
        String s = "shaderpacks/" + sp.getName() + ".txt";
        File file1 = new File(Minecraft.getMinecraft().mcDataDir, s);
        if (props.isEmpty()) {
            file1.delete();
        } else {
            FileOutputStream fileoutputstream = new FileOutputStream(file1);
            props.store(fileoutputstream, (String)null);
            fileoutputstream.flush();
            fileoutputstream.close();
        }
    }

    private static ShaderOption[] loadShaderPackOptions() {
        try {
            String[] astring = programs.getProgramNames();
            ShaderOption[] ashaderoption = ShaderPackParser.parseShaderPackOptions(shaderPack, astring, shaderPackDimensions);
            Properties properties = Shaders.loadOptionProperties(shaderPack);
            for (int i = 0; i < ashaderoption.length; ++i) {
                ShaderOption shaderoption = ashaderoption[i];
                String s = properties.getProperty(shaderoption.getName());
                if (s == null) continue;
                shaderoption.resetValue();
                if (shaderoption.setValue(s)) continue;
                Config.warn("[Shaders] Invalid value, option: " + shaderoption.getName() + ", value: " + s);
            }
            return ashaderoption;
        }
        catch (IOException ioexception) {
            Config.warn("[Shaders] Error reading configuration for " + shaderPack.getName());
            ioexception.printStackTrace();
            return null;
        }
    }

    private static Properties loadOptionProperties(IShaderPack sp) throws IOException {
        PropertiesOrdered properties = new PropertiesOrdered();
        String s = "shaderpacks/" + sp.getName() + ".txt";
        File file1 = new File(Minecraft.getMinecraft().mcDataDir, s);
        if (file1.exists() && file1.isFile() && file1.canRead()) {
            FileInputStream fileinputstream = new FileInputStream(file1);
            properties.load(fileinputstream);
            fileinputstream.close();
            return properties;
        }
        return properties;
    }

    public static ShaderOption[] getChangedOptions(ShaderOption[] ops) {
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        for (int i = 0; i < ops.length; ++i) {
            ShaderOption shaderoption = ops[i];
            if (!shaderoption.isEnabled() || !shaderoption.isChanged()) continue;
            list.add(shaderoption);
        }
        ShaderOption[] ashaderoption = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption;
    }

    private static String applyOptions(String line, ShaderOption[] ops) {
        if (ops != null && ops.length > 0) {
            for (int i = 0; i < ops.length; ++i) {
                ShaderOption shaderoption = ops[i];
                if (!shaderoption.matchesLine(line)) continue;
                line = shaderoption.getSourceLine();
                break;
            }
            return line;
        }
        return line;
    }

    public static ArrayList listOfShaders() {
        ArrayList<String> arraylist = new ArrayList<String>();
        arraylist.add(SHADER_PACK_NAME_NONE);
        arraylist.add(SHADER_PACK_NAME_DEFAULT);
        int i = arraylist.size();
        try {
            if (!shaderPacksDir.exists()) {
                shaderPacksDir.mkdir();
            }
            File[] afile = shaderPacksDir.listFiles();
            for (int j = 0; j < afile.length; ++j) {
                File file1 = afile[j];
                String s = file1.getName();
                if (file1.isDirectory()) {
                    File file2;
                    if (s.equals("debug") || !(file2 = new File(file1, "shaders")).exists() || !file2.isDirectory()) continue;
                    arraylist.add(s);
                    continue;
                }
                if (!file1.isFile() || !s.toLowerCase().endsWith(".zip")) continue;
                arraylist.add(s);
            }
        }
        catch (Exception afile) {
            // empty catch block
        }
        List list = arraylist.subList(i, arraylist.size());
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return arraylist;
    }

    public static int checkFramebufferStatus(String location) {
        int i = EXTFramebufferObject.glCheckFramebufferStatusEXT((int)36160);
        if (i != 36053) {
            System.err.format("FramebufferStatus 0x%04X at %s\n", i, location);
        }
        return i;
    }

    public static int checkGLError(String location) {
        int i = GlStateManager.glGetError();
        if (i != 0 && GlErrors.isEnabled(i)) {
            String s = Config.getGlErrorString(i);
            String s1 = Shaders.getErrorInfo(i, location);
            String s2 = String.format("OpenGL error: %s (%s)%s, at: %s", i, s, s1, location);
            SMCLog.severe(s2);
            if (Config.isShowGlErrors() && TimedEvent.isActive("ShowGlErrorShaders", 10000L)) {
                String s3 = I18n.format("of.message.openglError", i, s);
                Shaders.printChat(s3);
            }
        }
        return i;
    }

    private static String getErrorInfo(int errorCode, String location) {
        String s2;
        StringBuilder stringbuilder = new StringBuilder();
        if (errorCode == 1286) {
            int i = EXTFramebufferObject.glCheckFramebufferStatusEXT((int)36160);
            String s = Shaders.getFramebufferStatusText(i);
            String s1 = ", fbStatus: " + i + " (" + s + ")";
            stringbuilder.append(s1);
        }
        if ((s2 = activeProgram.getName()).isEmpty()) {
            s2 = "none";
        }
        stringbuilder.append(", program: " + s2);
        Program program = Shaders.getProgramById(activeProgramID);
        if (program != activeProgram) {
            String s3 = program.getName();
            if (s3.isEmpty()) {
                s3 = "none";
            }
            stringbuilder.append(" (" + s3 + ")");
        }
        if (location.equals("setDrawBuffers")) {
            stringbuilder.append(", drawBuffers: " + activeProgram.getDrawBufSettings());
        }
        return stringbuilder.toString();
    }

    private static Program getProgramById(int programID) {
        for (int i = 0; i < ProgramsAll.length; ++i) {
            Program program = ProgramsAll[i];
            if (program.getId() != programID) continue;
            return program;
        }
        return ProgramNone;
    }

    private static String getFramebufferStatusText(int fbStatusCode) {
        switch (fbStatusCode) {
            case 33305: {
                return "Undefined";
            }
            case 36053: {
                return "Complete";
            }
            case 36054: {
                return "Incomplete attachment";
            }
            case 36055: {
                return "Incomplete missing attachment";
            }
            case 36059: {
                return "Incomplete draw buffer";
            }
            case 36060: {
                return "Incomplete read buffer";
            }
            case 36061: {
                return "Unsupported";
            }
            case 36182: {
                return "Incomplete multisample";
            }
            case 36264: {
                return "Incomplete layer targets";
            }
        }
        return "Unknown";
    }

    private static void printChat(String str) {
        Shaders.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
    }

    private static void printChatAndLogError(String str) {
        SMCLog.severe(str);
        Shaders.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
    }

    public static void printIntBuffer(String title, IntBuffer buf) {
        StringBuilder stringbuilder = new StringBuilder(128);
        stringbuilder.append(title).append(" [pos ").append(buf.position()).append(" lim ").append(buf.limit()).append(" cap ").append(buf.capacity()).append(" :");
        int i = buf.limit();
        for (int j = 0; j < i; ++j) {
            stringbuilder.append(" ").append(buf.get(j));
        }
        stringbuilder.append("]");
        SMCLog.info(stringbuilder.toString());
    }

    public static void startup(Minecraft mcs) {
        Shaders.checkShadersModInstalled();
        mc = mcs;
        capabilities = GLContext.getCapabilities();
        glVersionString = GL11.glGetString((int)7938);
        glVendorString = GL11.glGetString((int)7936);
        glRendererString = GL11.glGetString((int)7937);
        SMCLog.info("OpenGL Version: " + glVersionString);
        SMCLog.info("Vendor:  " + glVendorString);
        SMCLog.info("Renderer: " + glRendererString);
        SMCLog.info("Capabilities: " + (Shaders.capabilities.OpenGL20 ? " 2.0 " : " - ") + (Shaders.capabilities.OpenGL21 ? " 2.1 " : " - ") + (Shaders.capabilities.OpenGL30 ? " 3.0 " : " - ") + (Shaders.capabilities.OpenGL32 ? " 3.2 " : " - ") + (Shaders.capabilities.OpenGL40 ? " 4.0 " : " - "));
        SMCLog.info("GL_MAX_DRAW_BUFFERS: " + GL11.glGetInteger((int)34852));
        SMCLog.info("GL_MAX_COLOR_ATTACHMENTS_EXT: " + GL11.glGetInteger((int)36063));
        SMCLog.info("GL_MAX_TEXTURE_IMAGE_UNITS: " + GL11.glGetInteger((int)34930));
        hasGlGenMipmap = Shaders.capabilities.OpenGL30;
        Shaders.loadConfig();
    }

    public static void updateBlockLightLevel() {
        if (Shaders.isOldLighting()) {
            blockLightLevel05 = 0.5f;
            blockLightLevel06 = 0.6f;
            blockLightLevel08 = 0.8f;
        } else {
            blockLightLevel05 = 1.0f;
            blockLightLevel06 = 1.0f;
            blockLightLevel08 = 1.0f;
        }
    }

    public static boolean isOldHandLight() {
        return !configOldHandLight.isDefault() ? configOldHandLight.isTrue() : (!shaderPackOldHandLight.isDefault() ? shaderPackOldHandLight.isTrue() : true);
    }

    public static boolean isDynamicHandLight() {
        return !shaderPackDynamicHandLight.isDefault() ? shaderPackDynamicHandLight.isTrue() : true;
    }

    public static boolean isOldLighting() {
        return !configOldLighting.isDefault() ? configOldLighting.isTrue() : (!shaderPackOldLighting.isDefault() ? shaderPackOldLighting.isTrue() : true);
    }

    public static boolean isRenderShadowTranslucent() {
        return !shaderPackShadowTranslucent.isFalse();
    }

    public static boolean isUnderwaterOverlay() {
        return !shaderPackUnderwaterOverlay.isFalse();
    }

    public static boolean isSun() {
        return !shaderPackSun.isFalse();
    }

    public static boolean isMoon() {
        return !shaderPackMoon.isFalse();
    }

    public static boolean isVignette() {
        return !shaderPackVignette.isFalse();
    }

    public static boolean isRenderBackFace(EnumWorldBlockLayer blockLayerIn) {
        switch (blockLayerIn) {
            case SOLID: {
                return shaderPackBackFaceSolid.isTrue();
            }
            case CUTOUT: {
                return shaderPackBackFaceCutout.isTrue();
            }
            case CUTOUT_MIPPED: {
                return shaderPackBackFaceCutoutMipped.isTrue();
            }
            case TRANSLUCENT: {
                return shaderPackBackFaceTranslucent.isTrue();
            }
        }
        return false;
    }

    public static boolean isRainDepth() {
        return shaderPackRainDepth.isTrue();
    }

    public static boolean isBeaconBeamDepth() {
        return shaderPackBeaconBeamDepth.isTrue();
    }

    public static boolean isSeparateAo() {
        return shaderPackSeparateAo.isTrue();
    }

    public static boolean isFrustumCulling() {
        return !shaderPackFrustumCulling.isFalse();
    }

    public static void init() {
        boolean flag;
        if (!isInitializedOnce) {
            isInitializedOnce = true;
            flag = true;
        } else {
            flag = false;
        }
        if (!isShaderPackInitialized) {
            int i;
            Shaders.checkGLError("Shaders.init pre");
            if (Shaders.getShaderPackName() != null) {
                // empty if block
            }
            if (!Shaders.capabilities.OpenGL20) {
                Shaders.printChatAndLogError("No OpenGL 2.0");
            }
            if (!Shaders.capabilities.GL_EXT_framebuffer_object) {
                Shaders.printChatAndLogError("No EXT_framebuffer_object");
            }
            dfbDrawBuffers.position(0).limit(8);
            dfbColorTextures.position(0).limit(16);
            dfbDepthTextures.position(0).limit(3);
            sfbDrawBuffers.position(0).limit(8);
            sfbDepthTextures.position(0).limit(2);
            sfbColorTextures.position(0).limit(8);
            usedColorBuffers = 4;
            usedDepthBuffers = 1;
            usedShadowColorBuffers = 0;
            usedShadowDepthBuffers = 0;
            usedColorAttachs = 1;
            usedDrawBuffers = 1;
            Arrays.fill(gbuffersFormat, 6408);
            Arrays.fill(gbuffersClear, true);
            Arrays.fill(gbuffersClearColor, null);
            Arrays.fill(shadowHardwareFilteringEnabled, false);
            Arrays.fill(shadowMipmapEnabled, false);
            Arrays.fill(shadowFilterNearest, false);
            Arrays.fill(shadowColorMipmapEnabled, false);
            Arrays.fill(shadowColorFilterNearest, false);
            centerDepthSmoothEnabled = false;
            noiseTextureEnabled = false;
            sunPathRotation = 0.0f;
            shadowIntervalSize = 2.0f;
            shadowMapWidth = 1024;
            shadowMapHeight = 1024;
            spShadowMapWidth = 1024;
            spShadowMapHeight = 1024;
            shadowMapFOV = 90.0f;
            shadowMapHalfPlane = 160.0f;
            shadowMapIsOrtho = true;
            shadowDistanceRenderMul = -1.0f;
            aoLevel = -1.0f;
            useEntityAttrib = false;
            useMidTexCoordAttrib = false;
            useTangentAttrib = false;
            waterShadowEnabled = false;
            hasGeometryShaders = false;
            Shaders.updateBlockLightLevel();
            Smoother.resetValues();
            shaderUniforms.reset();
            if (customUniforms != null) {
                customUniforms.reset();
            }
            ShaderProfile shaderprofile = ShaderUtils.detectProfile(shaderPackProfiles, shaderPackOptions, false);
            String s = "";
            if (currentWorld != null && shaderPackDimensions.contains(i = Shaders.currentWorld.provider.getDimensionId())) {
                s = "world" + i + "/";
            }
            for (int k = 0; k < ProgramsAll.length; ++k) {
                Program program = ProgramsAll[k];
                program.resetId();
                program.resetConfiguration();
                if (program.getProgramStage() == ProgramStage.NONE) continue;
                String s1 = program.getName();
                String s2 = s + s1;
                boolean flag1 = true;
                if (shaderPackProgramConditions.containsKey(s2)) {
                    boolean bl = flag1 = flag1 && shaderPackProgramConditions.get(s2).eval();
                }
                if (shaderprofile != null) {
                    boolean bl = flag1 = flag1 && !shaderprofile.isProgramDisabled(s2);
                }
                if (!flag1) {
                    SMCLog.info("Program disabled: " + s2);
                    s1 = "<disabled>";
                    s2 = s + s1;
                }
                String s3 = "/shaders/" + s2;
                String s4 = s3 + ".vsh";
                String s5 = s3 + ".gsh";
                String s6 = s3 + ".fsh";
                Shaders.setupProgram(program, s4, s5, s6);
                int j = program.getId();
                if (j > 0) {
                    SMCLog.info("Program loaded: " + s2);
                }
                Shaders.initDrawBuffers(program);
                Shaders.updateToggleBuffers(program);
            }
            hasDeferredPrograms = false;
            for (int l = 0; l < ProgramsDeferred.length; ++l) {
                if (ProgramsDeferred[l].getId() == 0) continue;
                hasDeferredPrograms = true;
                break;
            }
            usedColorAttachs = usedColorBuffers;
            shadowPassInterval = usedShadowDepthBuffers > 0 ? 1 : 0;
            shouldSkipDefaultShadow = usedShadowDepthBuffers > 0;
            SMCLog.info("usedColorBuffers: " + usedColorBuffers);
            SMCLog.info("usedDepthBuffers: " + usedDepthBuffers);
            SMCLog.info("usedShadowColorBuffers: " + usedShadowColorBuffers);
            SMCLog.info("usedShadowDepthBuffers: " + usedShadowDepthBuffers);
            SMCLog.info("usedColorAttachs: " + usedColorAttachs);
            SMCLog.info("usedDrawBuffers: " + usedDrawBuffers);
            dfbDrawBuffers.position(0).limit(usedDrawBuffers);
            dfbColorTextures.position(0).limit(usedColorBuffers * 2);
            dfbColorTexturesFlip.reset();
            for (int i1 = 0; i1 < usedDrawBuffers; ++i1) {
                dfbDrawBuffers.put(i1, 36064 + i1);
            }
            int j1 = GL11.glGetInteger((int)34852);
            if (usedDrawBuffers > j1) {
                Shaders.printChatAndLogError("[Shaders] Error: Not enough draw buffers, needed: " + usedDrawBuffers + ", available: " + j1);
            }
            sfbDrawBuffers.position(0).limit(usedShadowColorBuffers);
            for (int k1 = 0; k1 < usedShadowColorBuffers; ++k1) {
                sfbDrawBuffers.put(k1, 36064 + k1);
            }
            for (int l1 = 0; l1 < ProgramsAll.length; ++l1) {
                Program program1;
                Program program2;
                for (program2 = program1 = ProgramsAll[l1]; program2.getId() == 0 && program2.getProgramBackup() != program2; program2 = program2.getProgramBackup()) {
                }
                if (program2 == program1 || program1 == ProgramShadow) continue;
                program1.copyFrom(program2);
            }
            Shaders.resize();
            Shaders.resizeShadow();
            if (noiseTextureEnabled) {
                Shaders.setupNoiseTexture();
            }
            if (defaultTexture == null) {
                defaultTexture = ShadersTex.createDefaultTexture();
            }
            GlStateManager.pushMatrix();
            GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
            Shaders.preCelestialRotate();
            Shaders.postCelestialRotate();
            GlStateManager.popMatrix();
            isShaderPackInitialized = true;
            Shaders.loadEntityDataMap();
            Shaders.resetDisplayLists();
            if (!flag) {
                // empty if block
            }
            Shaders.checkGLError("Shaders.init");
        }
    }

    private static void initDrawBuffers(Program p) {
        int i = GL11.glGetInteger((int)34852);
        Arrays.fill(p.getToggleColorTextures(), false);
        if (p == ProgramFinal) {
            p.setDrawBuffers(null);
        } else if (p.getId() == 0) {
            if (p == ProgramShadow) {
                p.setDrawBuffers(drawBuffersNone);
            } else {
                p.setDrawBuffers(drawBuffersColorAtt0);
            }
        } else {
            String s = p.getDrawBufSettings();
            if (s == null) {
                if (p != ProgramShadow && p != ProgramShadowSolid && p != ProgramShadowCutout) {
                    p.setDrawBuffers(dfbDrawBuffers);
                    usedDrawBuffers = usedColorBuffers;
                    Arrays.fill(p.getToggleColorTextures(), 0, usedColorBuffers, true);
                } else {
                    p.setDrawBuffers(sfbDrawBuffers);
                }
            } else {
                IntBuffer intbuffer = p.getDrawBuffersBuffer();
                int j = s.length();
                usedDrawBuffers = Math.max(usedDrawBuffers, j);
                j = Math.min(j, i);
                p.setDrawBuffers(intbuffer);
                intbuffer.limit(j);
                for (int k = 0; k < j; ++k) {
                    int l = Shaders.getDrawBuffer(p, s, k);
                    intbuffer.put(k, l);
                }
            }
        }
    }

    private static int getDrawBuffer(Program p, String str, int ic) {
        int i = 0;
        if (ic >= str.length()) {
            return i;
        }
        int j = str.charAt(ic) - 48;
        if (p == ProgramShadow) {
            if (j >= 0 && j <= 1) {
                i = j + 36064;
                usedShadowColorBuffers = Math.max(usedShadowColorBuffers, j);
            }
            return i;
        }
        if (j >= 0 && j <= 7) {
            p.getToggleColorTextures()[j] = true;
            i = j + 36064;
            usedColorAttachs = Math.max(usedColorAttachs, j);
            usedColorBuffers = Math.max(usedColorBuffers, j);
        }
        return i;
    }

    private static void updateToggleBuffers(Program p) {
        boolean[] aboolean = p.getToggleColorTextures();
        Boolean[] aboolean1 = p.getBuffersFlip();
        for (int i = 0; i < aboolean1.length; ++i) {
            Boolean obool = aboolean1[i];
            if (obool == null) continue;
            aboolean[i] = obool;
        }
    }

    public static void resetDisplayLists() {
        SMCLog.info("Reset model renderers");
        ++countResetDisplayLists;
        SMCLog.info("Reset world renderers");
        Shaders.mc.renderGlobal.loadRenderers();
    }

    private static void setupProgram(Program program, String vShaderPath, String gShaderPath, String fShaderPath) {
        Shaders.checkGLError("pre setupProgram");
        int i = ARBShaderObjects.glCreateProgramObjectARB();
        Shaders.checkGLError("create");
        if (i != 0) {
            progUseEntityAttrib = false;
            progUseMidTexCoordAttrib = false;
            progUseTangentAttrib = false;
            int j = Shaders.createVertShader(program, vShaderPath);
            int k = Shaders.createGeomShader(program, gShaderPath);
            int l = Shaders.createFragShader(program, fShaderPath);
            Shaders.checkGLError("create");
            if (j == 0 && k == 0 && l == 0) {
                ARBShaderObjects.glDeleteObjectARB((int)i);
                i = 0;
                program.resetId();
            } else {
                if (j != 0) {
                    ARBShaderObjects.glAttachObjectARB((int)i, (int)j);
                    Shaders.checkGLError("attach");
                }
                if (k != 0) {
                    ARBShaderObjects.glAttachObjectARB((int)i, (int)k);
                    Shaders.checkGLError("attach");
                    if (progArbGeometryShader4) {
                        ARBGeometryShader4.glProgramParameteriARB((int)i, (int)36315, (int)4);
                        ARBGeometryShader4.glProgramParameteriARB((int)i, (int)36316, (int)5);
                        ARBGeometryShader4.glProgramParameteriARB((int)i, (int)36314, (int)progMaxVerticesOut);
                        Shaders.checkGLError("arbGeometryShader4");
                    }
                    hasGeometryShaders = true;
                }
                if (l != 0) {
                    ARBShaderObjects.glAttachObjectARB((int)i, (int)l);
                    Shaders.checkGLError("attach");
                }
                if (progUseEntityAttrib) {
                    ARBVertexShader.glBindAttribLocationARB((int)i, (int)entityAttrib, (CharSequence)"mc_Entity");
                    Shaders.checkGLError("mc_Entity");
                }
                if (progUseMidTexCoordAttrib) {
                    ARBVertexShader.glBindAttribLocationARB((int)i, (int)midTexCoordAttrib, (CharSequence)"mc_midTexCoord");
                    Shaders.checkGLError("mc_midTexCoord");
                }
                if (progUseTangentAttrib) {
                    ARBVertexShader.glBindAttribLocationARB((int)i, (int)tangentAttrib, (CharSequence)"at_tangent");
                    Shaders.checkGLError("at_tangent");
                }
                ARBShaderObjects.glLinkProgramARB((int)i);
                if (GL20.glGetProgrami((int)i, (int)35714) != 1) {
                    SMCLog.severe("Error linking program: " + i + " (" + program.getName() + ")");
                }
                Shaders.printLogInfo(i, program.getName());
                if (j != 0) {
                    ARBShaderObjects.glDetachObjectARB((int)i, (int)j);
                    ARBShaderObjects.glDeleteObjectARB((int)j);
                }
                if (k != 0) {
                    ARBShaderObjects.glDetachObjectARB((int)i, (int)k);
                    ARBShaderObjects.glDeleteObjectARB((int)k);
                }
                if (l != 0) {
                    ARBShaderObjects.glDetachObjectARB((int)i, (int)l);
                    ARBShaderObjects.glDeleteObjectARB((int)l);
                }
                program.setId(i);
                program.setRef(i);
                Shaders.useProgram(program);
                ARBShaderObjects.glValidateProgramARB((int)i);
                Shaders.useProgram(ProgramNone);
                Shaders.printLogInfo(i, program.getName());
                int i1 = GL20.glGetProgrami((int)i, (int)35715);
                if (i1 != 1) {
                    String s = "\"";
                    Shaders.printChatAndLogError("[Shaders] Error: Invalid program " + s + program.getName() + s);
                    ARBShaderObjects.glDeleteObjectARB((int)i);
                    i = 0;
                    program.resetId();
                }
            }
        }
    }

    private static int createVertShader(Program program, String filename) {
        int i = ARBShaderObjects.glCreateShaderObjectARB((int)35633);
        if (i == 0) {
            return 0;
        }
        StringBuilder stringbuilder = new StringBuilder(131072);
        BufferedReader bufferedreader = null;
        try {
            bufferedreader = new BufferedReader(Shaders.getShaderReader(filename));
        }
        catch (Exception var10) {
            ARBShaderObjects.glDeleteObjectARB((int)i);
            return 0;
        }
        ShaderOption[] ashaderoption = Shaders.getChangedOptions(shaderPackOptions);
        ArrayList<String> list = new ArrayList<String>();
        if (bufferedreader != null) {
            try {
                bufferedreader = ShaderPackParser.resolveIncludes(bufferedreader, filename, shaderPack, 0, list, 0);
                MacroState macrostate = new MacroState();
                while (true) {
                    ShaderLine shaderline;
                    String s;
                    if ((s = bufferedreader.readLine()) == null) {
                        bufferedreader.close();
                        break;
                    }
                    s = Shaders.applyOptions(s, ashaderoption);
                    stringbuilder.append(s).append('\n');
                    if (!macrostate.processLine(s) || (shaderline = ShaderParser.parseLine(s)) == null) continue;
                    if (shaderline.isAttribute("mc_Entity")) {
                        useEntityAttrib = true;
                        progUseEntityAttrib = true;
                    } else if (shaderline.isAttribute("mc_midTexCoord")) {
                        useMidTexCoordAttrib = true;
                        progUseMidTexCoordAttrib = true;
                    } else if (shaderline.isAttribute("at_tangent")) {
                        useTangentAttrib = true;
                        progUseTangentAttrib = true;
                    }
                    if (!shaderline.isConstInt("countInstances")) continue;
                    program.setCountInstances(shaderline.getValueInt());
                    SMCLog.info("countInstances: " + program.getCountInstances());
                }
            }
            catch (Exception exception) {
                SMCLog.severe("Couldn't read " + filename + "!");
                exception.printStackTrace();
                ARBShaderObjects.glDeleteObjectARB((int)i);
                return 0;
            }
        }
        if (saveFinalShaders) {
            Shaders.saveShader(filename, stringbuilder.toString());
        }
        ARBShaderObjects.glShaderSourceARB((int)i, (CharSequence)stringbuilder);
        ARBShaderObjects.glCompileShaderARB((int)i);
        if (GL20.glGetShaderi((int)i, (int)35713) != 1) {
            SMCLog.severe("Error compiling vertex shader: " + filename);
        }
        Shaders.printShaderLogInfo(i, filename, list);
        return i;
    }

    private static int createGeomShader(Program program, String filename) {
        int i = ARBShaderObjects.glCreateShaderObjectARB((int)36313);
        if (i == 0) {
            return 0;
        }
        StringBuilder stringbuilder = new StringBuilder(131072);
        BufferedReader bufferedreader = null;
        try {
            bufferedreader = new BufferedReader(Shaders.getShaderReader(filename));
        }
        catch (Exception var11) {
            ARBShaderObjects.glDeleteObjectARB((int)i);
            return 0;
        }
        ShaderOption[] ashaderoption = Shaders.getChangedOptions(shaderPackOptions);
        ArrayList<String> list = new ArrayList<String>();
        progArbGeometryShader4 = false;
        progMaxVerticesOut = 3;
        if (bufferedreader != null) {
            try {
                bufferedreader = ShaderPackParser.resolveIncludes(bufferedreader, filename, shaderPack, 0, list, 0);
                MacroState macrostate = new MacroState();
                while (true) {
                    String s1;
                    ShaderLine shaderline;
                    String s;
                    if ((s = bufferedreader.readLine()) == null) {
                        bufferedreader.close();
                        break;
                    }
                    s = Shaders.applyOptions(s, ashaderoption);
                    stringbuilder.append(s).append('\n');
                    if (!macrostate.processLine(s) || (shaderline = ShaderParser.parseLine(s)) == null) continue;
                    if (shaderline.isExtension("GL_ARB_geometry_shader4") && ((s1 = Config.normalize(shaderline.getValue())).equals("enable") || s1.equals("require") || s1.equals("warn"))) {
                        progArbGeometryShader4 = true;
                    }
                    if (!shaderline.isConstInt("maxVerticesOut")) continue;
                    progMaxVerticesOut = shaderline.getValueInt();
                }
            }
            catch (Exception exception) {
                SMCLog.severe("Couldn't read " + filename + "!");
                exception.printStackTrace();
                ARBShaderObjects.glDeleteObjectARB((int)i);
                return 0;
            }
        }
        if (saveFinalShaders) {
            Shaders.saveShader(filename, stringbuilder.toString());
        }
        ARBShaderObjects.glShaderSourceARB((int)i, (CharSequence)stringbuilder);
        ARBShaderObjects.glCompileShaderARB((int)i);
        if (GL20.glGetShaderi((int)i, (int)35713) != 1) {
            SMCLog.severe("Error compiling geometry shader: " + filename);
        }
        Shaders.printShaderLogInfo(i, filename, list);
        return i;
    }

    private static int createFragShader(Program program, String filename) {
        int i = ARBShaderObjects.glCreateShaderObjectARB((int)35632);
        if (i == 0) {
            return 0;
        }
        StringBuilder stringbuilder = new StringBuilder(131072);
        BufferedReader bufferedreader = null;
        try {
            bufferedreader = new BufferedReader(Shaders.getShaderReader(filename));
        }
        catch (Exception var14) {
            ARBShaderObjects.glDeleteObjectARB((int)i);
            return 0;
        }
        ShaderOption[] ashaderoption = Shaders.getChangedOptions(shaderPackOptions);
        ArrayList<String> list = new ArrayList<String>();
        if (bufferedreader != null) {
            try {
                bufferedreader = ShaderPackParser.resolveIncludes(bufferedreader, filename, shaderPack, 0, list, 0);
                MacroState macrostate = new MacroState();
                while (true) {
                    ShaderLine shaderline;
                    String s;
                    if ((s = bufferedreader.readLine()) == null) {
                        bufferedreader.close();
                        break;
                    }
                    s = Shaders.applyOptions(s, ashaderoption);
                    stringbuilder.append(s).append('\n');
                    if (!macrostate.processLine(s) || (shaderline = ShaderParser.parseLine(s)) == null) continue;
                    if (shaderline.isUniform()) {
                        String s6 = shaderline.getName();
                        int l1 = ShaderParser.getShadowDepthIndex(s6);
                        if (l1 >= 0) {
                            usedShadowDepthBuffers = Math.max(usedShadowDepthBuffers, l1 + 1);
                            continue;
                        }
                        l1 = ShaderParser.getShadowColorIndex(s6);
                        if (l1 >= 0) {
                            usedShadowColorBuffers = Math.max(usedShadowColorBuffers, l1 + 1);
                            continue;
                        }
                        l1 = ShaderParser.getDepthIndex(s6);
                        if (l1 >= 0) {
                            usedDepthBuffers = Math.max(usedDepthBuffers, l1 + 1);
                            continue;
                        }
                        if (s6.equals("gdepth") && gbuffersFormat[1] == 6408) {
                            Shaders.gbuffersFormat[1] = 34836;
                            continue;
                        }
                        l1 = ShaderParser.getColorIndex(s6);
                        if (l1 >= 0) {
                            usedColorBuffers = Math.max(usedColorBuffers, l1 + 1);
                            continue;
                        }
                        if (!s6.equals("centerDepthSmooth")) continue;
                        centerDepthSmoothEnabled = true;
                        continue;
                    }
                    if (!shaderline.isConstInt("shadowMapResolution") && !shaderline.isProperty("SHADOWRES")) {
                        if (!shaderline.isConstFloat("shadowMapFov") && !shaderline.isProperty("SHADOWFOV")) {
                            if (!shaderline.isConstFloat("shadowDistance") && !shaderline.isProperty("SHADOWHPL")) {
                                if (shaderline.isConstFloat("shadowDistanceRenderMul")) {
                                    shadowDistanceRenderMul = shaderline.getValueFloat();
                                    SMCLog.info("Shadow distance render mul: " + shadowDistanceRenderMul);
                                    continue;
                                }
                                if (shaderline.isConstFloat("shadowIntervalSize")) {
                                    shadowIntervalSize = shaderline.getValueFloat();
                                    SMCLog.info("Shadow map interval size: " + shadowIntervalSize);
                                    continue;
                                }
                                if (shaderline.isConstBool("generateShadowMipmap", true)) {
                                    Arrays.fill(shadowMipmapEnabled, true);
                                    SMCLog.info("Generate shadow mipmap");
                                    continue;
                                }
                                if (shaderline.isConstBool("generateShadowColorMipmap", true)) {
                                    Arrays.fill(shadowColorMipmapEnabled, true);
                                    SMCLog.info("Generate shadow color mipmap");
                                    continue;
                                }
                                if (shaderline.isConstBool("shadowHardwareFiltering", true)) {
                                    Arrays.fill(shadowHardwareFilteringEnabled, true);
                                    SMCLog.info("Hardware shadow filtering enabled.");
                                    continue;
                                }
                                if (shaderline.isConstBool("shadowHardwareFiltering0", true)) {
                                    Shaders.shadowHardwareFilteringEnabled[0] = true;
                                    SMCLog.info("shadowHardwareFiltering0");
                                    continue;
                                }
                                if (shaderline.isConstBool("shadowHardwareFiltering1", true)) {
                                    Shaders.shadowHardwareFilteringEnabled[1] = true;
                                    SMCLog.info("shadowHardwareFiltering1");
                                    continue;
                                }
                                if (shaderline.isConstBool("shadowtex0Mipmap", "shadowtexMipmap", true)) {
                                    Shaders.shadowMipmapEnabled[0] = true;
                                    SMCLog.info("shadowtex0Mipmap");
                                    continue;
                                }
                                if (shaderline.isConstBool("shadowtex1Mipmap", true)) {
                                    Shaders.shadowMipmapEnabled[1] = true;
                                    SMCLog.info("shadowtex1Mipmap");
                                    continue;
                                }
                                if (shaderline.isConstBool("shadowcolor0Mipmap", "shadowColor0Mipmap", true)) {
                                    Shaders.shadowColorMipmapEnabled[0] = true;
                                    SMCLog.info("shadowcolor0Mipmap");
                                    continue;
                                }
                                if (shaderline.isConstBool("shadowcolor1Mipmap", "shadowColor1Mipmap", true)) {
                                    Shaders.shadowColorMipmapEnabled[1] = true;
                                    SMCLog.info("shadowcolor1Mipmap");
                                    continue;
                                }
                                if (shaderline.isConstBool("shadowtex0Nearest", "shadowtexNearest", "shadow0MinMagNearest", true)) {
                                    Shaders.shadowFilterNearest[0] = true;
                                    SMCLog.info("shadowtex0Nearest");
                                    continue;
                                }
                                if (shaderline.isConstBool("shadowtex1Nearest", "shadow1MinMagNearest", true)) {
                                    Shaders.shadowFilterNearest[1] = true;
                                    SMCLog.info("shadowtex1Nearest");
                                    continue;
                                }
                                if (shaderline.isConstBool("shadowcolor0Nearest", "shadowColor0Nearest", "shadowColor0MinMagNearest", true)) {
                                    Shaders.shadowColorFilterNearest[0] = true;
                                    SMCLog.info("shadowcolor0Nearest");
                                    continue;
                                }
                                if (shaderline.isConstBool("shadowcolor1Nearest", "shadowColor1Nearest", "shadowColor1MinMagNearest", true)) {
                                    Shaders.shadowColorFilterNearest[1] = true;
                                    SMCLog.info("shadowcolor1Nearest");
                                    continue;
                                }
                                if (!shaderline.isConstFloat("wetnessHalflife") && !shaderline.isProperty("WETNESSHL")) {
                                    if (!shaderline.isConstFloat("drynessHalflife") && !shaderline.isProperty("DRYNESSHL")) {
                                        if (shaderline.isConstFloat("eyeBrightnessHalflife")) {
                                            eyeBrightnessHalflife = shaderline.getValueFloat();
                                            SMCLog.info("Eye brightness halflife: " + eyeBrightnessHalflife);
                                            continue;
                                        }
                                        if (shaderline.isConstFloat("centerDepthHalflife")) {
                                            centerDepthSmoothHalflife = shaderline.getValueFloat();
                                            SMCLog.info("Center depth halflife: " + centerDepthSmoothHalflife);
                                            continue;
                                        }
                                        if (shaderline.isConstFloat("sunPathRotation")) {
                                            sunPathRotation = shaderline.getValueFloat();
                                            SMCLog.info("Sun path rotation: " + sunPathRotation);
                                            continue;
                                        }
                                        if (shaderline.isConstFloat("ambientOcclusionLevel")) {
                                            aoLevel = Config.limit(shaderline.getValueFloat(), 0.0f, 1.0f);
                                            SMCLog.info("AO Level: " + aoLevel);
                                            continue;
                                        }
                                        if (shaderline.isConstInt("superSamplingLevel")) {
                                            int i1 = shaderline.getValueInt();
                                            if (i1 > 1) {
                                                SMCLog.info("Super sampling level: " + i1 + "x");
                                                superSamplingLevel = i1;
                                                continue;
                                            }
                                            superSamplingLevel = 1;
                                            continue;
                                        }
                                        if (shaderline.isConstInt("noiseTextureResolution")) {
                                            noiseTextureResolution = shaderline.getValueInt();
                                            noiseTextureEnabled = true;
                                            SMCLog.info("Noise texture enabled");
                                            SMCLog.info("Noise texture resolution: " + noiseTextureResolution);
                                            continue;
                                        }
                                        if (shaderline.isConstIntSuffix("Format")) {
                                            String s5 = StrUtils.removeSuffix(shaderline.getName(), "Format");
                                            String s7 = shaderline.getValue();
                                            int i2 = Shaders.getBufferIndexFromString(s5);
                                            int l = Shaders.getTextureFormatFromString(s7);
                                            if (i2 < 0 || l == 0) continue;
                                            Shaders.gbuffersFormat[i2] = l;
                                            SMCLog.info("%s format: %s", s5, s7);
                                            continue;
                                        }
                                        if (shaderline.isConstBoolSuffix("Clear", false)) {
                                            String s4;
                                            int k1;
                                            if (!ShaderParser.isComposite(filename) && !ShaderParser.isDeferred(filename) || (k1 = Shaders.getBufferIndexFromString(s4 = StrUtils.removeSuffix(shaderline.getName(), "Clear"))) < 0) continue;
                                            Shaders.gbuffersClear[k1] = false;
                                            SMCLog.info("%s clear disabled", s4);
                                            continue;
                                        }
                                        if (shaderline.isConstVec4Suffix("ClearColor")) {
                                            String s3;
                                            int j1;
                                            if (!ShaderParser.isComposite(filename) && !ShaderParser.isDeferred(filename) || (j1 = Shaders.getBufferIndexFromString(s3 = StrUtils.removeSuffix(shaderline.getName(), "ClearColor"))) < 0) continue;
                                            Vector4f vector4f = shaderline.getValueVec4();
                                            if (vector4f != null) {
                                                Shaders.gbuffersClearColor[j1] = vector4f;
                                                SMCLog.info("%s clear color: %s %s %s %s", s3, Float.valueOf(vector4f.getX()), Float.valueOf(vector4f.getY()), Float.valueOf(vector4f.getZ()), Float.valueOf(vector4f.getW()));
                                                continue;
                                            }
                                            SMCLog.warning("Invalid color value: " + shaderline.getValue());
                                            continue;
                                        }
                                        if (shaderline.isProperty("GAUX4FORMAT", "RGBA32F")) {
                                            Shaders.gbuffersFormat[7] = 34836;
                                            SMCLog.info("gaux4 format : RGB32AF");
                                            continue;
                                        }
                                        if (shaderline.isProperty("GAUX4FORMAT", "RGB32F")) {
                                            Shaders.gbuffersFormat[7] = 34837;
                                            SMCLog.info("gaux4 format : RGB32F");
                                            continue;
                                        }
                                        if (shaderline.isProperty("GAUX4FORMAT", "RGB16")) {
                                            Shaders.gbuffersFormat[7] = 32852;
                                            SMCLog.info("gaux4 format : RGB16");
                                            continue;
                                        }
                                        if (shaderline.isConstBoolSuffix("MipmapEnabled", true)) {
                                            String s2;
                                            int j;
                                            if (!ShaderParser.isComposite(filename) && !ShaderParser.isDeferred(filename) && !ShaderParser.isFinal(filename) || (j = Shaders.getBufferIndexFromString(s2 = StrUtils.removeSuffix(shaderline.getName(), "MipmapEnabled"))) < 0) continue;
                                            int k = program.getCompositeMipmapSetting();
                                            program.setCompositeMipmapSetting(k |= 1 << j);
                                            SMCLog.info("%s mipmap enabled", s2);
                                            continue;
                                        }
                                        if (!shaderline.isProperty("DRAWBUFFERS")) continue;
                                        String s1 = shaderline.getValue();
                                        if (ShaderParser.isValidDrawBuffers(s1)) {
                                            program.setDrawBufSettings(s1);
                                            continue;
                                        }
                                        SMCLog.warning("Invalid draw buffers: " + s1);
                                        continue;
                                    }
                                    drynessHalfLife = shaderline.getValueFloat();
                                    SMCLog.info("Dryness halflife: " + drynessHalfLife);
                                    continue;
                                }
                                wetnessHalfLife = shaderline.getValueFloat();
                                SMCLog.info("Wetness halflife: " + wetnessHalfLife);
                                continue;
                            }
                            shadowMapHalfPlane = shaderline.getValueFloat();
                            shadowMapIsOrtho = true;
                            SMCLog.info("Shadow map distance: " + shadowMapHalfPlane);
                            continue;
                        }
                        shadowMapFOV = shaderline.getValueFloat();
                        shadowMapIsOrtho = false;
                        SMCLog.info("Shadow map field of view: " + shadowMapFOV);
                        continue;
                    }
                    spShadowMapWidth = spShadowMapHeight = shaderline.getValueInt();
                    shadowMapWidth = shadowMapHeight = Math.round((float)spShadowMapWidth * configShadowResMul);
                    SMCLog.info("Shadow map resolution: " + spShadowMapWidth);
                }
            }
            catch (Exception exception) {
                SMCLog.severe("Couldn't read " + filename + "!");
                exception.printStackTrace();
                ARBShaderObjects.glDeleteObjectARB((int)i);
                return 0;
            }
        }
        if (saveFinalShaders) {
            Shaders.saveShader(filename, stringbuilder.toString());
        }
        ARBShaderObjects.glShaderSourceARB((int)i, (CharSequence)stringbuilder);
        ARBShaderObjects.glCompileShaderARB((int)i);
        if (GL20.glGetShaderi((int)i, (int)35713) != 1) {
            SMCLog.severe("Error compiling fragment shader: " + filename);
        }
        Shaders.printShaderLogInfo(i, filename, list);
        return i;
    }

    private static Reader getShaderReader(String filename) {
        return new InputStreamReader(shaderPack.getResourceAsStream(filename));
    }

    public static void saveShader(String filename, String code) {
        try {
            File file1 = new File(shaderPacksDir, "debug/" + filename);
            file1.getParentFile().mkdirs();
            Config.writeFile(file1, code);
        }
        catch (IOException ioexception) {
            Config.warn("Error saving: " + filename);
            ioexception.printStackTrace();
        }
    }

    private static void clearDirectory(File dir) {
        File[] afile;
        if (dir.exists() && dir.isDirectory() && (afile = dir.listFiles()) != null) {
            for (int i = 0; i < afile.length; ++i) {
                File file1 = afile[i];
                if (file1.isDirectory()) {
                    Shaders.clearDirectory(file1);
                }
                file1.delete();
            }
        }
    }

    private static boolean printLogInfo(int obj, String name) {
        IntBuffer intbuffer = BufferUtils.createIntBuffer((int)1);
        ARBShaderObjects.glGetObjectParameterARB((int)obj, (int)35716, (IntBuffer)intbuffer);
        int i = intbuffer.get();
        if (i > 1) {
            ByteBuffer bytebuffer = BufferUtils.createByteBuffer((int)i);
            intbuffer.flip();
            ARBShaderObjects.glGetInfoLogARB((int)obj, (IntBuffer)intbuffer, (ByteBuffer)bytebuffer);
            byte[] abyte = new byte[i];
            bytebuffer.get(abyte);
            if (abyte[i - 1] == 0) {
                abyte[i - 1] = 10;
            }
            String s = new String(abyte, Charsets.US_ASCII);
            s = StrUtils.trim(s, " \n\r\t");
            SMCLog.info("Info log: " + name + "\n" + s);
            return false;
        }
        return true;
    }

    private static boolean printShaderLogInfo(int shader, String name, List<String> listFiles) {
        IntBuffer intbuffer = BufferUtils.createIntBuffer((int)1);
        int i = GL20.glGetShaderi((int)shader, (int)35716);
        if (i <= 1) {
            return true;
        }
        for (int j = 0; j < listFiles.size(); ++j) {
            String s = listFiles.get(j);
            SMCLog.info("File: " + (j + 1) + " = " + s);
        }
        String s1 = GL20.glGetShaderInfoLog((int)shader, (int)i);
        s1 = StrUtils.trim(s1, " \n\r\t");
        SMCLog.info("Shader info log: " + name + "\n" + s1);
        return false;
    }

    public static void setDrawBuffers(IntBuffer drawBuffers) {
        if (drawBuffers == null) {
            drawBuffers = drawBuffersNone;
        }
        if (activeDrawBuffers != drawBuffers) {
            activeDrawBuffers = drawBuffers;
            GL20.glDrawBuffers((IntBuffer)drawBuffers);
            Shaders.checkGLError("setDrawBuffers");
        }
    }

    public static void useProgram(Program program) {
        Shaders.checkGLError("pre-useProgram");
        if (isShadowPass) {
            program = ProgramShadow;
        } else if (isEntitiesGlowing) {
            program = ProgramEntitiesGlowing;
        }
        if (activeProgram != program) {
            int i;
            Shaders.updateAlphaBlend(activeProgram, program);
            activeProgram = program;
            activeProgramID = i = program.getId();
            ARBShaderObjects.glUseProgramObjectARB((int)i);
            if (Shaders.checkGLError("useProgram") != 0) {
                program.setId(0);
                activeProgramID = i = program.getId();
                ARBShaderObjects.glUseProgramObjectARB((int)i);
            }
            shaderUniforms.setProgram(i);
            if (customUniforms != null) {
                customUniforms.setProgram(i);
            }
            if (i != 0) {
                IntBuffer intbuffer = program.getDrawBuffers();
                if (isRenderingDfb) {
                    Shaders.setDrawBuffers(intbuffer);
                }
                activeCompositeMipmapSetting = program.getCompositeMipmapSetting();
                switch (program.getProgramStage()) {
                    case GBUFFERS: {
                        Shaders.setProgramUniform1i(uniform_texture, 0);
                        Shaders.setProgramUniform1i(uniform_lightmap, 1);
                        Shaders.setProgramUniform1i(uniform_normals, 2);
                        Shaders.setProgramUniform1i(uniform_specular, 3);
                        Shaders.setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                        Shaders.setProgramUniform1i(uniform_watershadow, 4);
                        Shaders.setProgramUniform1i(uniform_shadowtex0, 4);
                        Shaders.setProgramUniform1i(uniform_shadowtex1, 5);
                        Shaders.setProgramUniform1i(uniform_depthtex0, 6);
                        if (customTexturesGbuffers != null || hasDeferredPrograms) {
                            Shaders.setProgramUniform1i(uniform_gaux1, 7);
                            Shaders.setProgramUniform1i(uniform_gaux2, 8);
                            Shaders.setProgramUniform1i(uniform_gaux3, 9);
                            Shaders.setProgramUniform1i(uniform_gaux4, 10);
                        }
                        Shaders.setProgramUniform1i(uniform_depthtex1, 11);
                        Shaders.setProgramUniform1i(uniform_shadowcolor, 13);
                        Shaders.setProgramUniform1i(uniform_shadowcolor0, 13);
                        Shaders.setProgramUniform1i(uniform_shadowcolor1, 14);
                        Shaders.setProgramUniform1i(uniform_noisetex, 15);
                        break;
                    }
                    case DEFERRED: 
                    case COMPOSITE: {
                        Shaders.setProgramUniform1i(uniform_gcolor, 0);
                        Shaders.setProgramUniform1i(uniform_gdepth, 1);
                        Shaders.setProgramUniform1i(uniform_gnormal, 2);
                        Shaders.setProgramUniform1i(uniform_composite, 3);
                        Shaders.setProgramUniform1i(uniform_gaux1, 7);
                        Shaders.setProgramUniform1i(uniform_gaux2, 8);
                        Shaders.setProgramUniform1i(uniform_gaux3, 9);
                        Shaders.setProgramUniform1i(uniform_gaux4, 10);
                        Shaders.setProgramUniform1i(uniform_colortex0, 0);
                        Shaders.setProgramUniform1i(uniform_colortex1, 1);
                        Shaders.setProgramUniform1i(uniform_colortex2, 2);
                        Shaders.setProgramUniform1i(uniform_colortex3, 3);
                        Shaders.setProgramUniform1i(uniform_colortex4, 7);
                        Shaders.setProgramUniform1i(uniform_colortex5, 8);
                        Shaders.setProgramUniform1i(uniform_colortex6, 9);
                        Shaders.setProgramUniform1i(uniform_colortex7, 10);
                        Shaders.setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                        Shaders.setProgramUniform1i(uniform_watershadow, 4);
                        Shaders.setProgramUniform1i(uniform_shadowtex0, 4);
                        Shaders.setProgramUniform1i(uniform_shadowtex1, 5);
                        Shaders.setProgramUniform1i(uniform_gdepthtex, 6);
                        Shaders.setProgramUniform1i(uniform_depthtex0, 6);
                        Shaders.setProgramUniform1i(uniform_depthtex1, 11);
                        Shaders.setProgramUniform1i(uniform_depthtex2, 12);
                        Shaders.setProgramUniform1i(uniform_shadowcolor, 13);
                        Shaders.setProgramUniform1i(uniform_shadowcolor0, 13);
                        Shaders.setProgramUniform1i(uniform_shadowcolor1, 14);
                        Shaders.setProgramUniform1i(uniform_noisetex, 15);
                        break;
                    }
                    case SHADOW: {
                        Shaders.setProgramUniform1i(uniform_tex, 0);
                        Shaders.setProgramUniform1i(uniform_texture, 0);
                        Shaders.setProgramUniform1i(uniform_lightmap, 1);
                        Shaders.setProgramUniform1i(uniform_normals, 2);
                        Shaders.setProgramUniform1i(uniform_specular, 3);
                        Shaders.setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                        Shaders.setProgramUniform1i(uniform_watershadow, 4);
                        Shaders.setProgramUniform1i(uniform_shadowtex0, 4);
                        Shaders.setProgramUniform1i(uniform_shadowtex1, 5);
                        if (customTexturesGbuffers != null) {
                            Shaders.setProgramUniform1i(uniform_gaux1, 7);
                            Shaders.setProgramUniform1i(uniform_gaux2, 8);
                            Shaders.setProgramUniform1i(uniform_gaux3, 9);
                            Shaders.setProgramUniform1i(uniform_gaux4, 10);
                        }
                        Shaders.setProgramUniform1i(uniform_shadowcolor, 13);
                        Shaders.setProgramUniform1i(uniform_shadowcolor0, 13);
                        Shaders.setProgramUniform1i(uniform_shadowcolor1, 14);
                        Shaders.setProgramUniform1i(uniform_noisetex, 15);
                    }
                }
                ItemStack itemstack = Shaders.mc.thePlayer != null ? Shaders.mc.thePlayer.getHeldItem() : null;
                Item item = itemstack != null ? itemstack.getItem() : null;
                int j = -1;
                Block block = null;
                if (item != null) {
                    j = Item.itemRegistry.getIDForObject(item);
                    block = Block.blockRegistry.getObjectById(j);
                    j = ItemAliases.getItemAliasId(j);
                }
                int k = block != null ? block.getLightValue() : 0;
                Shaders.setProgramUniform1i(uniform_heldItemId, j);
                Shaders.setProgramUniform1i(uniform_heldBlockLightValue, k);
                Shaders.setProgramUniform1i(uniform_fogMode, fogEnabled ? fogMode : 0);
                Shaders.setProgramUniform1f(uniform_fogDensity, fogEnabled ? fogDensity : 0.0f);
                Shaders.setProgramUniform3f(uniform_fogColor, fogColorR, fogColorG, fogColorB);
                Shaders.setProgramUniform3f(uniform_skyColor, skyColorR, skyColorG, skyColorB);
                Shaders.setProgramUniform1i(uniform_worldTime, (int)(worldTime % 24000L));
                Shaders.setProgramUniform1i(uniform_worldDay, (int)(worldTime / 24000L));
                Shaders.setProgramUniform1i(uniform_moonPhase, moonPhase);
                Shaders.setProgramUniform1i(uniform_frameCounter, frameCounter);
                Shaders.setProgramUniform1f(uniform_frameTime, frameTime);
                Shaders.setProgramUniform1f(uniform_frameTimeCounter, frameTimeCounter);
                Shaders.setProgramUniform1f(uniform_sunAngle, sunAngle);
                Shaders.setProgramUniform1f(uniform_shadowAngle, shadowAngle);
                Shaders.setProgramUniform1f(uniform_rainStrength, rainStrength);
                Shaders.setProgramUniform1f(uniform_aspectRatio, (float)renderWidth / (float)renderHeight);
                Shaders.setProgramUniform1f(uniform_viewWidth, renderWidth);
                Shaders.setProgramUniform1f(uniform_viewHeight, renderHeight);
                Shaders.setProgramUniform1f(uniform_near, 0.05f);
                Shaders.setProgramUniform1f(uniform_far, Shaders.mc.gameSettings.renderDistanceChunks * 16);
                Shaders.setProgramUniform3f(uniform_sunPosition, sunPosition[0], sunPosition[1], sunPosition[2]);
                Shaders.setProgramUniform3f(uniform_moonPosition, moonPosition[0], moonPosition[1], moonPosition[2]);
                Shaders.setProgramUniform3f(uniform_shadowLightPosition, shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
                Shaders.setProgramUniform3f(uniform_upPosition, upPosition[0], upPosition[1], upPosition[2]);
                Shaders.setProgramUniform3f(uniform_previousCameraPosition, (float)previousCameraPositionX, (float)previousCameraPositionY, (float)previousCameraPositionZ);
                Shaders.setProgramUniform3f(uniform_cameraPosition, (float)cameraPositionX, (float)cameraPositionY, (float)cameraPositionZ);
                Shaders.setProgramUniformMatrix4ARB(uniform_gbufferModelView, false, modelView);
                Shaders.setProgramUniformMatrix4ARB(uniform_gbufferModelViewInverse, false, modelViewInverse);
                Shaders.setProgramUniformMatrix4ARB(uniform_gbufferPreviousProjection, false, previousProjection);
                Shaders.setProgramUniformMatrix4ARB(uniform_gbufferProjection, false, projection);
                Shaders.setProgramUniformMatrix4ARB(uniform_gbufferProjectionInverse, false, projectionInverse);
                Shaders.setProgramUniformMatrix4ARB(uniform_gbufferPreviousModelView, false, previousModelView);
                if (usedShadowDepthBuffers > 0) {
                    Shaders.setProgramUniformMatrix4ARB(uniform_shadowProjection, false, shadowProjection);
                    Shaders.setProgramUniformMatrix4ARB(uniform_shadowProjectionInverse, false, shadowProjectionInverse);
                    Shaders.setProgramUniformMatrix4ARB(uniform_shadowModelView, false, shadowModelView);
                    Shaders.setProgramUniformMatrix4ARB(uniform_shadowModelViewInverse, false, shadowModelViewInverse);
                }
                Shaders.setProgramUniform1f(uniform_wetness, wetness);
                Shaders.setProgramUniform1f(uniform_eyeAltitude, eyePosY);
                Shaders.setProgramUniform2i(uniform_eyeBrightness, eyeBrightness & 0xFFFF, eyeBrightness >> 16);
                Shaders.setProgramUniform2i(uniform_eyeBrightnessSmooth, Math.round(eyeBrightnessFadeX), Math.round(eyeBrightnessFadeY));
                Shaders.setProgramUniform2i(uniform_terrainTextureSize, terrainTextureSize[0], terrainTextureSize[1]);
                Shaders.setProgramUniform1i(uniform_terrainIconSize, terrainIconSize);
                Shaders.setProgramUniform1i(uniform_isEyeInWater, isEyeInWater);
                Shaders.setProgramUniform1f(uniform_nightVision, nightVision);
                Shaders.setProgramUniform1f(uniform_blindness, blindness);
                Shaders.setProgramUniform1f(uniform_screenBrightness, Shaders.mc.gameSettings.gammaSetting);
                Shaders.setProgramUniform1i(uniform_hideGUI, Shaders.mc.gameSettings.hideGUI ? 1 : 0);
                Shaders.setProgramUniform1f(uniform_centerDepthSmooth, centerDepthSmooth);
                Shaders.setProgramUniform2i(uniform_atlasSize, atlasSizeX, atlasSizeY);
                if (customUniforms != null) {
                    customUniforms.update();
                }
                Shaders.checkGLError("end useProgram");
            }
        }
    }

    private static void updateAlphaBlend(Program programOld, Program programNew) {
        GlBlendState glblendstate;
        GlAlphaState glalphastate;
        if (programOld.getAlphaState() != null) {
            GlStateManager.unlockAlpha();
        }
        if (programOld.getBlendState() != null) {
            GlStateManager.unlockBlend();
        }
        if ((glalphastate = programNew.getAlphaState()) != null) {
            GlStateManager.lockAlpha(glalphastate);
        }
        if ((glblendstate = programNew.getBlendState()) != null) {
            GlStateManager.lockBlend(glblendstate);
        }
    }

    private static void setProgramUniform1i(ShaderUniform1i su, int value) {
        su.setValue(value);
    }

    private static void setProgramUniform2i(ShaderUniform2i su, int i0, int i1) {
        su.setValue(i0, i1);
    }

    private static void setProgramUniform1f(ShaderUniform1f su, float value) {
        su.setValue(value);
    }

    private static void setProgramUniform3f(ShaderUniform3f su, float f0, float f1, float f2) {
        su.setValue(f0, f1, f2);
    }

    private static void setProgramUniformMatrix4ARB(ShaderUniformM4 su, boolean transpose, FloatBuffer matrix) {
        su.setValue(transpose, matrix);
    }

    public static int getBufferIndexFromString(String name) {
        return !name.equals("colortex0") && !name.equals("gcolor") ? (!name.equals("colortex1") && !name.equals("gdepth") ? (!name.equals("colortex2") && !name.equals("gnormal") ? (!name.equals("colortex3") && !name.equals("composite") ? (!name.equals("colortex4") && !name.equals("gaux1") ? (!name.equals("colortex5") && !name.equals("gaux2") ? (!name.equals("colortex6") && !name.equals("gaux3") ? (!name.equals("colortex7") && !name.equals("gaux4") ? -1 : 7) : 6) : 5) : 4) : 3) : 2) : 1) : 0;
    }

    private static int getTextureFormatFromString(String par) {
        par = par.trim();
        for (int i = 0; i < formatNames.length; ++i) {
            String s = formatNames[i];
            if (!par.equals(s)) continue;
            return formatIds[i];
        }
        return 0;
    }

    private static void setupNoiseTexture() {
        if (noiseTexture == null && noiseTexturePath != null) {
            noiseTexture = Shaders.loadCustomTexture(15, noiseTexturePath);
        }
        if (noiseTexture == null) {
            noiseTexture = new HFNoiseTexture(noiseTextureResolution, noiseTextureResolution);
        }
    }

    private static void loadEntityDataMap() {
        mapBlockToEntityData = new IdentityHashMap<Block, Integer>(300);
        if (mapBlockToEntityData.isEmpty()) {
            for (ResourceLocation resourcelocation : Block.blockRegistry.getKeys()) {
                Block block = Block.blockRegistry.getObject(resourcelocation);
                int i = Block.blockRegistry.getIDForObject(block);
                mapBlockToEntityData.put(block, i);
            }
        }
        BufferedReader bufferedreader = null;
        try {
            bufferedreader = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream("/mc_Entity_x.txt")));
        }
        catch (Exception resourcelocation) {
            // empty catch block
        }
        if (bufferedreader != null) {
            try {
                String s1;
                while ((s1 = bufferedreader.readLine()) != null) {
                    Matcher matcher = patternLoadEntityDataMap.matcher(s1);
                    if (matcher.matches()) {
                        String s2 = matcher.group(1);
                        String s = matcher.group(2);
                        int j = Integer.parseInt(s);
                        Block block1 = Block.getBlockFromName(s2);
                        if (block1 != null) {
                            mapBlockToEntityData.put(block1, j);
                            continue;
                        }
                        SMCLog.warning("Unknown block name %s", s2);
                        continue;
                    }
                    SMCLog.warning("unmatched %s\n", s1);
                }
            }
            catch (Exception var9) {
                SMCLog.warning("Error parsing mc_Entity_x.txt");
            }
        }
        if (bufferedreader != null) {
            try {
                bufferedreader.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private static IntBuffer fillIntBufferZero(IntBuffer buf) {
        int i = buf.limit();
        for (int j = buf.position(); j < i; ++j) {
            buf.put(j, 0);
        }
        return buf;
    }

    public static void uninit() {
        if (isShaderPackInitialized) {
            Shaders.checkGLError("Shaders.uninit pre");
            for (int i = 0; i < ProgramsAll.length; ++i) {
                Program program = ProgramsAll[i];
                if (program.getRef() != 0) {
                    ARBShaderObjects.glDeleteObjectARB((int)program.getRef());
                    Shaders.checkGLError("del programRef");
                }
                program.setRef(0);
                program.setId(0);
                program.setDrawBufSettings(null);
                program.setDrawBuffers(null);
                program.setCompositeMipmapSetting(0);
            }
            hasDeferredPrograms = false;
            if (dfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT((int)dfb);
                dfb = 0;
                Shaders.checkGLError("del dfb");
            }
            if (sfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT((int)sfb);
                sfb = 0;
                Shaders.checkGLError("del sfb");
            }
            if (dfbDepthTextures != null) {
                GlStateManager.deleteTextures(dfbDepthTextures);
                Shaders.fillIntBufferZero(dfbDepthTextures);
                Shaders.checkGLError("del dfbDepthTextures");
            }
            if (dfbColorTextures != null) {
                GlStateManager.deleteTextures(dfbColorTextures);
                Shaders.fillIntBufferZero(dfbColorTextures);
                Shaders.checkGLError("del dfbTextures");
            }
            if (sfbDepthTextures != null) {
                GlStateManager.deleteTextures(sfbDepthTextures);
                Shaders.fillIntBufferZero(sfbDepthTextures);
                Shaders.checkGLError("del shadow depth");
            }
            if (sfbColorTextures != null) {
                GlStateManager.deleteTextures(sfbColorTextures);
                Shaders.fillIntBufferZero(sfbColorTextures);
                Shaders.checkGLError("del shadow color");
            }
            if (dfbDrawBuffers != null) {
                Shaders.fillIntBufferZero(dfbDrawBuffers);
            }
            if (noiseTexture != null) {
                noiseTexture.deleteTexture();
                noiseTexture = null;
            }
            SMCLog.info("Uninit");
            shadowPassInterval = 0;
            shouldSkipDefaultShadow = false;
            isShaderPackInitialized = false;
            Shaders.checkGLError("Shaders.uninit");
        }
    }

    public static void scheduleResize() {
        renderDisplayHeight = 0;
    }

    public static void scheduleResizeShadow() {
        needResizeShadow = true;
    }

    private static void resize() {
        renderDisplayWidth = Shaders.mc.displayWidth;
        renderDisplayHeight = Shaders.mc.displayHeight;
        renderWidth = Math.round((float)renderDisplayWidth * configRenderResMul);
        renderHeight = Math.round((float)renderDisplayHeight * configRenderResMul);
        Shaders.setupFrameBuffer();
    }

    private static void resizeShadow() {
        needResizeShadow = false;
        shadowMapWidth = Math.round((float)spShadowMapWidth * configShadowResMul);
        shadowMapHeight = Math.round((float)spShadowMapHeight * configShadowResMul);
        Shaders.setupShadowFrameBuffer();
    }

    private static void setupFrameBuffer() {
        if (dfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT((int)dfb);
            GlStateManager.deleteTextures(dfbDepthTextures);
            GlStateManager.deleteTextures(dfbColorTextures);
        }
        dfb = EXTFramebufferObject.glGenFramebuffersEXT();
        GL11.glGenTextures((IntBuffer)((IntBuffer)dfbDepthTextures.clear().limit(usedDepthBuffers)));
        GL11.glGenTextures((IntBuffer)((IntBuffer)dfbColorTextures.clear().limit(16)));
        dfbDepthTextures.position(0);
        dfbColorTextures.position(0);
        EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)dfb);
        GL20.glDrawBuffers((int)0);
        GL11.glReadBuffer((int)0);
        for (int i = 0; i < usedDepthBuffers; ++i) {
            GlStateManager.bindTexture(dfbDepthTextures.get(i));
            GL11.glTexParameteri((int)3553, (int)10242, (int)33071);
            GL11.glTexParameteri((int)3553, (int)10243, (int)33071);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
            GL11.glTexParameteri((int)3553, (int)34891, (int)6409);
            GL11.glTexImage2D((int)3553, (int)0, (int)6402, (int)renderWidth, (int)renderHeight, (int)0, (int)6402, (int)5126, (FloatBuffer)null);
        }
        EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)36096, (int)3553, (int)dfbDepthTextures.get(0), (int)0);
        GL20.glDrawBuffers((IntBuffer)dfbDrawBuffers);
        GL11.glReadBuffer((int)0);
        Shaders.checkGLError("FT d");
        for (int k = 0; k < usedColorBuffers; ++k) {
            GlStateManager.bindTexture(dfbColorTexturesFlip.getA(k));
            GL11.glTexParameteri((int)3553, (int)10242, (int)33071);
            GL11.glTexParameteri((int)3553, (int)10243, (int)33071);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
            GL11.glTexImage2D((int)3553, (int)0, (int)gbuffersFormat[k], (int)renderWidth, (int)renderHeight, (int)0, (int)Shaders.getPixelFormat(gbuffersFormat[k]), (int)33639, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + k), (int)3553, (int)dfbColorTexturesFlip.getA(k), (int)0);
            Shaders.checkGLError("FT c");
        }
        for (int l = 0; l < usedColorBuffers; ++l) {
            GlStateManager.bindTexture(dfbColorTexturesFlip.getB(l));
            GL11.glTexParameteri((int)3553, (int)10242, (int)33071);
            GL11.glTexParameteri((int)3553, (int)10243, (int)33071);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
            GL11.glTexImage2D((int)3553, (int)0, (int)gbuffersFormat[l], (int)renderWidth, (int)renderHeight, (int)0, (int)Shaders.getPixelFormat(gbuffersFormat[l]), (int)33639, (ByteBuffer)null);
            Shaders.checkGLError("FT ca");
        }
        int i1 = EXTFramebufferObject.glCheckFramebufferStatusEXT((int)36160);
        if (i1 == 36058) {
            Shaders.printChatAndLogError("[Shaders] Error: Failed framebuffer incomplete formats");
            for (int j = 0; j < usedColorBuffers; ++j) {
                GlStateManager.bindTexture(dfbColorTexturesFlip.getA(j));
                GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)renderWidth, (int)renderHeight, (int)0, (int)32993, (int)33639, (ByteBuffer)null);
                EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + j), (int)3553, (int)dfbColorTexturesFlip.getA(j), (int)0);
                Shaders.checkGLError("FT c");
            }
            i1 = EXTFramebufferObject.glCheckFramebufferStatusEXT((int)36160);
            if (i1 == 36053) {
                SMCLog.info("complete");
            }
        }
        GlStateManager.bindTexture(0);
        if (i1 != 36053) {
            Shaders.printChatAndLogError("[Shaders] Error: Failed creating framebuffer! (Status " + i1 + ")");
        } else {
            SMCLog.info("Framebuffer created.");
        }
    }

    private static int getPixelFormat(int internalFormat) {
        switch (internalFormat) {
            case 33333: 
            case 33334: 
            case 33339: 
            case 33340: 
            case 36208: 
            case 36209: 
            case 36226: 
            case 36227: {
                return 36251;
            }
        }
        return 32993;
    }

    private static void setupShadowFrameBuffer() {
        if (usedShadowDepthBuffers != 0) {
            int l;
            if (sfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT((int)sfb);
                GlStateManager.deleteTextures(sfbDepthTextures);
                GlStateManager.deleteTextures(sfbColorTextures);
            }
            sfb = EXTFramebufferObject.glGenFramebuffersEXT();
            EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)sfb);
            GL11.glDrawBuffer((int)0);
            GL11.glReadBuffer((int)0);
            GL11.glGenTextures((IntBuffer)((IntBuffer)sfbDepthTextures.clear().limit(usedShadowDepthBuffers)));
            GL11.glGenTextures((IntBuffer)((IntBuffer)sfbColorTextures.clear().limit(usedShadowColorBuffers)));
            sfbDepthTextures.position(0);
            sfbColorTextures.position(0);
            for (int i = 0; i < usedShadowDepthBuffers; ++i) {
                GlStateManager.bindTexture(sfbDepthTextures.get(i));
                GL11.glTexParameterf((int)3553, (int)10242, (float)33071.0f);
                GL11.glTexParameterf((int)3553, (int)10243, (float)33071.0f);
                int j = shadowFilterNearest[i] ? 9728 : 9729;
                GL11.glTexParameteri((int)3553, (int)10241, (int)j);
                GL11.glTexParameteri((int)3553, (int)10240, (int)j);
                if (shadowHardwareFilteringEnabled[i]) {
                    GL11.glTexParameteri((int)3553, (int)34892, (int)34894);
                }
                GL11.glTexImage2D((int)3553, (int)0, (int)6402, (int)shadowMapWidth, (int)shadowMapHeight, (int)0, (int)6402, (int)5126, (FloatBuffer)null);
            }
            EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)36096, (int)3553, (int)sfbDepthTextures.get(0), (int)0);
            Shaders.checkGLError("FT sd");
            for (int k = 0; k < usedShadowColorBuffers; ++k) {
                GlStateManager.bindTexture(sfbColorTextures.get(k));
                GL11.glTexParameterf((int)3553, (int)10242, (float)33071.0f);
                GL11.glTexParameterf((int)3553, (int)10243, (float)33071.0f);
                int i1 = shadowColorFilterNearest[k] ? 9728 : 9729;
                GL11.glTexParameteri((int)3553, (int)10241, (int)i1);
                GL11.glTexParameteri((int)3553, (int)10240, (int)i1);
                GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)shadowMapWidth, (int)shadowMapHeight, (int)0, (int)32993, (int)33639, (ByteBuffer)null);
                EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + k), (int)3553, (int)sfbColorTextures.get(k), (int)0);
                Shaders.checkGLError("FT sc");
            }
            GlStateManager.bindTexture(0);
            if (usedShadowColorBuffers > 0) {
                GL20.glDrawBuffers((IntBuffer)sfbDrawBuffers);
            }
            if ((l = EXTFramebufferObject.glCheckFramebufferStatusEXT((int)36160)) != 36053) {
                Shaders.printChatAndLogError("[Shaders] Error: Failed creating shadow framebuffer! (Status " + l + ")");
            } else {
                SMCLog.info("Shadow framebuffer created.");
            }
        }
    }

    public static void beginRender(Minecraft minecraft, float partialTicks, long finishTimeNano) {
        block13: {
            Shaders.checkGLError("pre beginRender");
            Shaders.checkWorldChanged(Shaders.mc.theWorld);
            mc = minecraft;
            Shaders.mc.mcProfiler.startSection("init");
            entityRenderer = Shaders.mc.entityRenderer;
            if (!isShaderPackInitialized) {
                try {
                    Shaders.init();
                }
                catch (IllegalStateException illegalstateexception) {
                    if (!Config.normalize(illegalstateexception.getMessage()).equals("Function is not supported")) break block13;
                    Shaders.printChatAndLogError("[Shaders] Error: " + illegalstateexception.getMessage());
                    illegalstateexception.printStackTrace();
                    Shaders.setShaderPack(SHADER_PACK_NAME_NONE);
                    return;
                }
            }
        }
        if (Shaders.mc.displayWidth != renderDisplayWidth || Shaders.mc.displayHeight != renderDisplayHeight) {
            Shaders.resize();
        }
        if (needResizeShadow) {
            Shaders.resizeShadow();
        }
        if ((diffWorldTime = ((worldTime = Shaders.mc.theWorld.getWorldTime()) - lastWorldTime) % 24000L) < 0L) {
            diffWorldTime += 24000L;
        }
        lastWorldTime = worldTime;
        moonPhase = Shaders.mc.theWorld.getMoonPhase();
        if (++frameCounter >= 720720) {
            frameCounter = 0;
        }
        systemTime = System.currentTimeMillis();
        if (lastSystemTime == 0L) {
            lastSystemTime = systemTime;
        }
        diffSystemTime = systemTime - lastSystemTime;
        lastSystemTime = systemTime;
        frameTime = (float)diffSystemTime / 1000.0f;
        frameTimeCounter += frameTime;
        frameTimeCounter %= 3600.0f;
        rainStrength = minecraft.theWorld.getRainStrength(partialTicks);
        float f = (float)diffSystemTime * 0.01f;
        float f1 = (float)Math.exp(Math.log(0.5) * (double)f / (double)(wetness < rainStrength ? drynessHalfLife : wetnessHalfLife));
        wetness = wetness * f1 + rainStrength * (1.0f - f1);
        Entity entity = mc.getRenderViewEntity();
        if (entity != null) {
            isSleeping = entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping();
            eyePosY = (float)entity.posY * partialTicks + (float)entity.lastTickPosY * (1.0f - partialTicks);
            eyeBrightness = entity.getBrightnessForRender(partialTicks);
            f1 = (float)diffSystemTime * 0.01f;
            float f2 = (float)Math.exp(Math.log(0.5) * (double)f1 / (double)eyeBrightnessHalflife);
            eyeBrightnessFadeX = eyeBrightnessFadeX * f2 + (float)(eyeBrightness & 0xFFFF) * (1.0f - f2);
            eyeBrightnessFadeY = eyeBrightnessFadeY * f2 + (float)(eyeBrightness >> 16) * (1.0f - f2);
            Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(Shaders.mc.theWorld, entity, partialTicks);
            Material material = block.getMaterial();
            isEyeInWater = material == Material.water ? 1 : (material == Material.lava ? 2 : 0);
            if (Shaders.mc.thePlayer != null) {
                nightVision = 0.0f;
                if (Shaders.mc.thePlayer.isPotionActive(Potion.nightVision)) {
                    nightVision = Config.getMinecraft().entityRenderer.getNightVisionBrightness(Shaders.mc.thePlayer, partialTicks);
                }
                blindness = 0.0f;
                if (Shaders.mc.thePlayer.isPotionActive(Potion.blindness)) {
                    int i = Shaders.mc.thePlayer.getActivePotionEffect(Potion.blindness).getDuration();
                    blindness = Config.limit((float)i / 20.0f, 0.0f, 1.0f);
                }
            }
            Vec3 vec3 = Shaders.mc.theWorld.getSkyColor(entity, partialTicks);
            vec3 = CustomColors.getWorldSkyColor(vec3, currentWorld, entity, partialTicks);
            skyColorR = (float)vec3.x;
            skyColorG = (float)vec3.y;
            skyColorB = (float)vec3.z;
        }
        isRenderingWorld = true;
        isCompositeRendered = false;
        isShadowPass = false;
        isHandRenderedMain = false;
        isHandRenderedOff = false;
        skipRenderHandMain = false;
        skipRenderHandOff = false;
        Shaders.bindGbuffersTextures();
        previousCameraPositionX = cameraPositionX;
        previousCameraPositionY = cameraPositionY;
        previousCameraPositionZ = cameraPositionZ;
        previousProjection.position(0);
        projection.position(0);
        previousProjection.put(projection);
        previousProjection.position(0);
        projection.position(0);
        previousModelView.position(0);
        modelView.position(0);
        previousModelView.put(modelView);
        previousModelView.position(0);
        modelView.position(0);
        Shaders.checkGLError("beginRender");
        ShadersRender.renderShadowMap(entityRenderer, 0, partialTicks, finishTimeNano);
        Shaders.mc.mcProfiler.endSection();
        EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)dfb);
        for (int j = 0; j < usedColorBuffers; ++j) {
            EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + j), (int)3553, (int)dfbColorTexturesFlip.getA(j), (int)0);
        }
        Shaders.checkGLError("end beginRender");
    }

    private static void bindGbuffersTextures() {
        if (usedShadowDepthBuffers >= 1) {
            GlStateManager.setActiveTexture(33988);
            GlStateManager.bindTexture(sfbDepthTextures.get(0));
            if (usedShadowDepthBuffers >= 2) {
                GlStateManager.setActiveTexture(33989);
                GlStateManager.bindTexture(sfbDepthTextures.get(1));
            }
        }
        GlStateManager.setActiveTexture(33984);
        for (int i = 0; i < usedColorBuffers; ++i) {
            GlStateManager.bindTexture(dfbColorTexturesFlip.getA(i));
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
            GlStateManager.bindTexture(dfbColorTexturesFlip.getB(i));
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
        }
        GlStateManager.bindTexture(0);
        for (int j = 0; j < 4 && 4 + j < usedColorBuffers; ++j) {
            GlStateManager.setActiveTexture(33991 + j);
            GlStateManager.bindTexture(dfbColorTexturesFlip.getA(4 + j));
        }
        GlStateManager.setActiveTexture(33990);
        GlStateManager.bindTexture(dfbDepthTextures.get(0));
        if (usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            GlStateManager.bindTexture(dfbDepthTextures.get(1));
            if (usedDepthBuffers >= 3) {
                GlStateManager.setActiveTexture(33996);
                GlStateManager.bindTexture(dfbDepthTextures.get(2));
            }
        }
        for (int k = 0; k < usedShadowColorBuffers; ++k) {
            GlStateManager.setActiveTexture(33997 + k);
            GlStateManager.bindTexture(sfbColorTextures.get(k));
        }
        if (noiseTextureEnabled) {
            GlStateManager.setActiveTexture(33984 + noiseTexture.getTextureUnit());
            GlStateManager.bindTexture(noiseTexture.getTextureId());
        }
        Shaders.bindCustomTextures(customTexturesGbuffers);
        GlStateManager.setActiveTexture(33984);
    }

    public static void checkWorldChanged(World world) {
        if (currentWorld != world) {
            World oldworld = currentWorld;
            currentWorld = world;
            Shaders.setCameraOffset(mc.getRenderViewEntity());
            int i = Shaders.getDimensionId(oldworld);
            int j = Shaders.getDimensionId(world);
            if (j != i) {
                boolean flag = shaderPackDimensions.contains(i);
                boolean flag1 = shaderPackDimensions.contains(j);
                if (flag || flag1) {
                    Shaders.uninit();
                }
            }
            Smoother.resetValues();
        }
    }

    private static int getDimensionId(World world) {
        return world == null ? Integer.MIN_VALUE : world.provider.getDimensionId();
    }

    public static void beginRenderPass(int pass, float partialTicks, long finishTimeNano) {
        if (!isShadowPass) {
            EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)dfb);
            GL11.glViewport((int)0, (int)0, (int)renderWidth, (int)renderHeight);
            activeDrawBuffers = null;
            ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
            Shaders.useProgram(ProgramTextured);
            Shaders.checkGLError("end beginRenderPass");
        }
    }

    public static void setViewport(int vx, int vy, int vw, int vh) {
        GlStateManager.colorMask(true, true, true, true);
        if (isShadowPass) {
            GL11.glViewport((int)0, (int)0, (int)shadowMapWidth, (int)shadowMapHeight);
        } else {
            GL11.glViewport((int)0, (int)0, (int)renderWidth, (int)renderHeight);
            EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)dfb);
            isRenderingDfb = true;
            GlStateManager.enableCull();
            GlStateManager.enableDepth();
            Shaders.setDrawBuffers(drawBuffersNone);
            Shaders.useProgram(ProgramTextured);
            Shaders.checkGLError("beginRenderPass");
        }
    }

    public static void setFogMode(int value) {
        fogMode = value;
        if (fogEnabled) {
            Shaders.setProgramUniform1i(uniform_fogMode, value);
        }
    }

    public static void setFogColor(float r, float g, float b) {
        fogColorR = r;
        fogColorG = g;
        fogColorB = b;
        Shaders.setProgramUniform3f(uniform_fogColor, fogColorR, fogColorG, fogColorB);
    }

    public static void setClearColor(float red, float green, float blue, float alpha) {
        GlStateManager.clearColor(red, green, blue, alpha);
        clearColorR = red;
        clearColorG = green;
        clearColorB = blue;
    }

    public static void clearRenderBuffer() {
        if (isShadowPass) {
            Shaders.checkGLError("shadow clear pre");
            EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)36096, (int)3553, (int)sfbDepthTextures.get(0), (int)0);
            GL11.glClearColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL20.glDrawBuffers((IntBuffer)ProgramShadow.getDrawBuffers());
            Shaders.checkFramebufferStatus("shadow clear");
            GL11.glClear((int)16640);
            Shaders.checkGLError("shadow clear");
        } else {
            Shaders.checkGLError("clear pre");
            if (gbuffersClear[0]) {
                Vector4f vector4f = gbuffersClearColor[0];
                if (vector4f != null) {
                    GL11.glClearColor((float)vector4f.getX(), (float)vector4f.getY(), (float)vector4f.getZ(), (float)vector4f.getW());
                }
                if (dfbColorTexturesFlip.isChanged(0)) {
                    EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)36064, (int)3553, (int)dfbColorTexturesFlip.getB(0), (int)0);
                    GL20.glDrawBuffers((int)36064);
                    GL11.glClear((int)16384);
                    EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)36064, (int)3553, (int)dfbColorTexturesFlip.getA(0), (int)0);
                }
                GL20.glDrawBuffers((int)36064);
                GL11.glClear((int)16384);
            }
            if (gbuffersClear[1]) {
                GL11.glClearColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                Vector4f vector4f2 = gbuffersClearColor[1];
                if (vector4f2 != null) {
                    GL11.glClearColor((float)vector4f2.getX(), (float)vector4f2.getY(), (float)vector4f2.getZ(), (float)vector4f2.getW());
                }
                if (dfbColorTexturesFlip.isChanged(1)) {
                    EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)36065, (int)3553, (int)dfbColorTexturesFlip.getB(1), (int)0);
                    GL20.glDrawBuffers((int)36065);
                    GL11.glClear((int)16384);
                    EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)36065, (int)3553, (int)dfbColorTexturesFlip.getA(1), (int)0);
                }
                GL20.glDrawBuffers((int)36065);
                GL11.glClear((int)16384);
            }
            for (int i = 2; i < usedColorBuffers; ++i) {
                if (!gbuffersClear[i]) continue;
                GL11.glClearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
                Vector4f vector4f1 = gbuffersClearColor[i];
                if (vector4f1 != null) {
                    GL11.glClearColor((float)vector4f1.getX(), (float)vector4f1.getY(), (float)vector4f1.getZ(), (float)vector4f1.getW());
                }
                if (dfbColorTexturesFlip.isChanged(i)) {
                    EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + i), (int)3553, (int)dfbColorTexturesFlip.getB(i), (int)0);
                    GL20.glDrawBuffers((int)(36064 + i));
                    GL11.glClear((int)16384);
                    EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + i), (int)3553, (int)dfbColorTexturesFlip.getA(i), (int)0);
                }
                GL20.glDrawBuffers((int)(36064 + i));
                GL11.glClear((int)16384);
            }
            Shaders.setDrawBuffers(dfbDrawBuffers);
            Shaders.checkFramebufferStatus("clear");
            Shaders.checkGLError("clear");
        }
    }

    public static void setCamera(float partialTicks) {
        Entity entity = mc.getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        Shaders.updateCameraOffset(entity);
        cameraPositionX = d0 - (double)cameraOffsetX;
        cameraPositionY = d1;
        cameraPositionZ = d2 - (double)cameraOffsetZ;
        GL11.glGetFloat((int)2983, (FloatBuffer)((FloatBuffer)projection.position(0)));
        SMath.invertMat4FBFA((FloatBuffer)projectionInverse.position(0), (FloatBuffer)projection.position(0), faProjectionInverse, faProjection);
        projection.position(0);
        projectionInverse.position(0);
        GL11.glGetFloat((int)2982, (FloatBuffer)((FloatBuffer)modelView.position(0)));
        SMath.invertMat4FBFA((FloatBuffer)modelViewInverse.position(0), (FloatBuffer)modelView.position(0), faModelViewInverse, faModelView);
        modelView.position(0);
        modelViewInverse.position(0);
        Shaders.checkGLError("setCamera");
    }

    private static void updateCameraOffset(Entity viewEntity) {
        double d0 = Math.abs(cameraPositionX - previousCameraPositionX);
        double d1 = Math.abs(cameraPositionZ - previousCameraPositionZ);
        double d2 = Math.abs(cameraPositionX);
        double d3 = Math.abs(cameraPositionZ);
        if (d0 > 1000.0 || d1 > 1000.0 || d2 > 1000000.0 || d3 > 1000000.0) {
            Shaders.setCameraOffset(viewEntity);
        }
    }

    private static void setCameraOffset(Entity viewEntity) {
        if (viewEntity == null) {
            cameraOffsetX = 0;
            cameraOffsetZ = 0;
        } else {
            cameraOffsetX = (int)viewEntity.posX / 1000 * 1000;
            cameraOffsetZ = (int)viewEntity.posZ / 1000 * 1000;
        }
    }

    public static void setCameraShadow(float partialTicks) {
        float f1;
        Entity entity = mc.getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        Shaders.updateCameraOffset(entity);
        cameraPositionX = d0 - (double)cameraOffsetX;
        cameraPositionY = d1;
        cameraPositionZ = d2 - (double)cameraOffsetZ;
        GL11.glGetFloat((int)2983, (FloatBuffer)((FloatBuffer)projection.position(0)));
        SMath.invertMat4FBFA((FloatBuffer)projectionInverse.position(0), (FloatBuffer)projection.position(0), faProjectionInverse, faProjection);
        projection.position(0);
        projectionInverse.position(0);
        GL11.glGetFloat((int)2982, (FloatBuffer)((FloatBuffer)modelView.position(0)));
        SMath.invertMat4FBFA((FloatBuffer)modelViewInverse.position(0), (FloatBuffer)modelView.position(0), faModelViewInverse, faModelView);
        modelView.position(0);
        modelViewInverse.position(0);
        GL11.glViewport((int)0, (int)0, (int)shadowMapWidth, (int)shadowMapHeight);
        GL11.glMatrixMode((int)5889);
        GL11.glLoadIdentity();
        if (shadowMapIsOrtho) {
            GL11.glOrtho((double)(-shadowMapHalfPlane), (double)shadowMapHalfPlane, (double)(-shadowMapHalfPlane), (double)shadowMapHalfPlane, (double)0.05f, (double)256.0);
        } else {
            GLU.gluPerspective((float)shadowMapFOV, (float)((float)shadowMapWidth / (float)shadowMapHeight), (float)0.05f, (float)256.0f);
        }
        GL11.glMatrixMode((int)5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-100.0f);
        GL11.glRotatef((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        celestialAngle = Shaders.mc.theWorld.getCelestialAngle(partialTicks);
        sunAngle = celestialAngle < 0.75f ? celestialAngle + 0.25f : celestialAngle - 0.75f;
        float f = celestialAngle * -360.0f;
        float f2 = f1 = shadowAngleInterval > 0.0f ? f % shadowAngleInterval - shadowAngleInterval * 0.5f : 0.0f;
        if ((double)sunAngle <= 0.5) {
            GL11.glRotatef((float)(f - f1), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)sunPathRotation, (float)1.0f, (float)0.0f, (float)0.0f);
            shadowAngle = sunAngle;
        } else {
            GL11.glRotatef((float)(f + 180.0f - f1), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)sunPathRotation, (float)1.0f, (float)0.0f, (float)0.0f);
            shadowAngle = sunAngle - 0.5f;
        }
        if (shadowMapIsOrtho) {
            float f22 = shadowIntervalSize;
            float f3 = f22 / 2.0f;
            GL11.glTranslatef((float)((float)d0 % f22 - f3), (float)((float)d1 % f22 - f3), (float)((float)d2 % f22 - f3));
        }
        float f9 = sunAngle * ((float)Math.PI * 2);
        float f10 = (float)Math.cos(f9);
        float f4 = (float)Math.sin(f9);
        float f5 = sunPathRotation * ((float)Math.PI * 2);
        float f6 = f10;
        float f7 = f4 * (float)Math.cos(f5);
        float f8 = f4 * (float)Math.sin(f5);
        if ((double)sunAngle > 0.5) {
            f6 = -f10;
            f7 = -f7;
            f8 = -f8;
        }
        Shaders.shadowLightPositionVector[0] = f6;
        Shaders.shadowLightPositionVector[1] = f7;
        Shaders.shadowLightPositionVector[2] = f8;
        Shaders.shadowLightPositionVector[3] = 0.0f;
        GL11.glGetFloat((int)2983, (FloatBuffer)((FloatBuffer)shadowProjection.position(0)));
        SMath.invertMat4FBFA((FloatBuffer)shadowProjectionInverse.position(0), (FloatBuffer)shadowProjection.position(0), faShadowProjectionInverse, faShadowProjection);
        shadowProjection.position(0);
        shadowProjectionInverse.position(0);
        GL11.glGetFloat((int)2982, (FloatBuffer)((FloatBuffer)shadowModelView.position(0)));
        SMath.invertMat4FBFA((FloatBuffer)shadowModelViewInverse.position(0), (FloatBuffer)shadowModelView.position(0), faShadowModelViewInverse, faShadowModelView);
        shadowModelView.position(0);
        shadowModelViewInverse.position(0);
        Shaders.setProgramUniformMatrix4ARB(uniform_gbufferProjection, false, projection);
        Shaders.setProgramUniformMatrix4ARB(uniform_gbufferProjectionInverse, false, projectionInverse);
        Shaders.setProgramUniformMatrix4ARB(uniform_gbufferPreviousProjection, false, previousProjection);
        Shaders.setProgramUniformMatrix4ARB(uniform_gbufferModelView, false, modelView);
        Shaders.setProgramUniformMatrix4ARB(uniform_gbufferModelViewInverse, false, modelViewInverse);
        Shaders.setProgramUniformMatrix4ARB(uniform_gbufferPreviousModelView, false, previousModelView);
        Shaders.setProgramUniformMatrix4ARB(uniform_shadowProjection, false, shadowProjection);
        Shaders.setProgramUniformMatrix4ARB(uniform_shadowProjectionInverse, false, shadowProjectionInverse);
        Shaders.setProgramUniformMatrix4ARB(uniform_shadowModelView, false, shadowModelView);
        Shaders.setProgramUniformMatrix4ARB(uniform_shadowModelViewInverse, false, shadowModelViewInverse);
        Shaders.mc.gameSettings.thirdPersonView = 1;
        Shaders.checkGLError("setCamera");
    }

    public static void preCelestialRotate() {
        GL11.glRotatef((float)(sunPathRotation * 1.0f), (float)0.0f, (float)0.0f, (float)1.0f);
        Shaders.checkGLError("preCelestialRotate");
    }

    public static void postCelestialRotate() {
        FloatBuffer floatbuffer = tempMatrixDirectBuffer;
        floatbuffer.clear();
        GL11.glGetFloat((int)2982, (FloatBuffer)floatbuffer);
        floatbuffer.get(tempMat, 0, 16);
        SMath.multiplyMat4xVec4(sunPosition, tempMat, sunPosModelView);
        SMath.multiplyMat4xVec4(moonPosition, tempMat, moonPosModelView);
        System.arraycopy(shadowAngle == sunAngle ? sunPosition : moonPosition, 0, shadowLightPosition, 0, 3);
        Shaders.setProgramUniform3f(uniform_sunPosition, sunPosition[0], sunPosition[1], sunPosition[2]);
        Shaders.setProgramUniform3f(uniform_moonPosition, moonPosition[0], moonPosition[1], moonPosition[2]);
        Shaders.setProgramUniform3f(uniform_shadowLightPosition, shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
        if (customUniforms != null) {
            customUniforms.update();
        }
        Shaders.checkGLError("postCelestialRotate");
    }

    public static void setUpPosition() {
        FloatBuffer floatbuffer = tempMatrixDirectBuffer;
        floatbuffer.clear();
        GL11.glGetFloat((int)2982, (FloatBuffer)floatbuffer);
        floatbuffer.get(tempMat, 0, 16);
        SMath.multiplyMat4xVec4(upPosition, tempMat, upPosModelView);
        Shaders.setProgramUniform3f(uniform_upPosition, upPosition[0], upPosition[1], upPosition[2]);
        if (customUniforms != null) {
            customUniforms.update();
        }
    }

    public static void genCompositeMipmap() {
        if (hasGlGenMipmap) {
            for (int i = 0; i < usedColorBuffers; ++i) {
                if ((activeCompositeMipmapSetting & 1 << i) == 0) continue;
                GlStateManager.setActiveTexture(33984 + colorTextureImageUnit[i]);
                GL11.glTexParameteri((int)3553, (int)10241, (int)9987);
                GL30.glGenerateMipmap((int)3553);
            }
            GlStateManager.setActiveTexture(33984);
        }
    }

    public static void drawComposite() {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Shaders.drawCompositeQuad();
        int i = activeProgram.getCountInstances();
        if (i > 1) {
            for (int j = 1; j < i; ++j) {
                uniform_instanceId.setValue(j);
                Shaders.drawCompositeQuad();
            }
            uniform_instanceId.setValue(0);
        }
    }

    private static void drawCompositeQuad() {
        if (!Shaders.canRenderQuads()) {
            GL11.glBegin((int)5);
            GL11.glTexCoord2f((float)0.0f, (float)0.0f);
            GL11.glVertex3f((float)0.0f, (float)0.0f, (float)0.0f);
            GL11.glTexCoord2f((float)1.0f, (float)0.0f);
            GL11.glVertex3f((float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glTexCoord2f((float)0.0f, (float)1.0f);
            GL11.glVertex3f((float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTexCoord2f((float)1.0f, (float)1.0f);
            GL11.glVertex3f((float)1.0f, (float)1.0f, (float)0.0f);
            GL11.glEnd();
        } else {
            GL11.glBegin((int)7);
            GL11.glTexCoord2f((float)0.0f, (float)0.0f);
            GL11.glVertex3f((float)0.0f, (float)0.0f, (float)0.0f);
            GL11.glTexCoord2f((float)1.0f, (float)0.0f);
            GL11.glVertex3f((float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glTexCoord2f((float)1.0f, (float)1.0f);
            GL11.glVertex3f((float)1.0f, (float)1.0f, (float)0.0f);
            GL11.glTexCoord2f((float)0.0f, (float)1.0f);
            GL11.glVertex3f((float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glEnd();
        }
    }

    public static void renderDeferred() {
        if (!isShadowPass) {
            boolean flag = Shaders.checkBufferFlip(ProgramDeferredPre);
            if (hasDeferredPrograms) {
                Shaders.checkGLError("pre-render Deferred");
                Shaders.renderComposites(ProgramsDeferred, false);
                flag = true;
            }
            if (flag) {
                Shaders.bindGbuffersTextures();
                for (int i = 0; i < usedColorBuffers; ++i) {
                    EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + i), (int)3553, (int)dfbColorTexturesFlip.getA(i), (int)0);
                }
                if (ProgramWater.getDrawBuffers() != null) {
                    Shaders.setDrawBuffers(ProgramWater.getDrawBuffers());
                } else {
                    Shaders.setDrawBuffers(dfbDrawBuffers);
                }
                GlStateManager.setActiveTexture(33984);
                mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            }
        }
    }

    public static void renderCompositeFinal() {
        if (!isShadowPass) {
            Shaders.checkBufferFlip(ProgramCompositePre);
            Shaders.checkGLError("pre-render CompositeFinal");
            Shaders.renderComposites(ProgramsComposite, true);
        }
    }

    private static boolean checkBufferFlip(Program program) {
        boolean flag = false;
        Boolean[] aboolean = program.getBuffersFlip();
        for (int i = 0; i < usedColorBuffers; ++i) {
            if (!Config.isTrue(aboolean[i])) continue;
            dfbColorTexturesFlip.flip(i);
            flag = true;
        }
        return flag;
    }

    private static void renderComposites(Program[] ps, boolean renderFinal) {
        if (!isShadowPass) {
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode((int)5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho((double)0.0, (double)1.0, (double)0.0, (double)1.0, (double)0.0, (double)1.0);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.enableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(519);
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();
            if (usedShadowDepthBuffers >= 1) {
                GlStateManager.setActiveTexture(33988);
                GlStateManager.bindTexture(sfbDepthTextures.get(0));
                if (usedShadowDepthBuffers >= 2) {
                    GlStateManager.setActiveTexture(33989);
                    GlStateManager.bindTexture(sfbDepthTextures.get(1));
                }
            }
            for (int i = 0; i < usedColorBuffers; ++i) {
                GlStateManager.setActiveTexture(33984 + colorTextureImageUnit[i]);
                GlStateManager.bindTexture(dfbColorTexturesFlip.getA(i));
            }
            GlStateManager.setActiveTexture(33990);
            GlStateManager.bindTexture(dfbDepthTextures.get(0));
            if (usedDepthBuffers >= 2) {
                GlStateManager.setActiveTexture(33995);
                GlStateManager.bindTexture(dfbDepthTextures.get(1));
                if (usedDepthBuffers >= 3) {
                    GlStateManager.setActiveTexture(33996);
                    GlStateManager.bindTexture(dfbDepthTextures.get(2));
                }
            }
            for (int k = 0; k < usedShadowColorBuffers; ++k) {
                GlStateManager.setActiveTexture(33997 + k);
                GlStateManager.bindTexture(sfbColorTextures.get(k));
            }
            if (noiseTextureEnabled) {
                GlStateManager.setActiveTexture(33984 + noiseTexture.getTextureUnit());
                GlStateManager.bindTexture(noiseTexture.getTextureId());
            }
            if (renderFinal) {
                Shaders.bindCustomTextures(customTexturesComposite);
            } else {
                Shaders.bindCustomTextures(customTexturesDeferred);
            }
            GlStateManager.setActiveTexture(33984);
            for (int l = 0; l < usedColorBuffers; ++l) {
                EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + l), (int)3553, (int)dfbColorTexturesFlip.getB(l), (int)0);
            }
            EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)36096, (int)3553, (int)dfbDepthTextures.get(0), (int)0);
            GL20.glDrawBuffers((IntBuffer)dfbDrawBuffers);
            Shaders.checkGLError("pre-composite");
            for (int i1 = 0; i1 < ps.length; ++i1) {
                Program program = ps[i1];
                if (program.getId() == 0) continue;
                Shaders.useProgram(program);
                Shaders.checkGLError(program.getName());
                if (activeCompositeMipmapSetting != 0) {
                    Shaders.genCompositeMipmap();
                }
                Shaders.preDrawComposite();
                Shaders.drawComposite();
                Shaders.postDrawComposite();
                for (int j = 0; j < usedColorBuffers; ++j) {
                    if (!program.getToggleColorTextures()[j]) continue;
                    dfbColorTexturesFlip.flip(j);
                    GlStateManager.setActiveTexture(33984 + colorTextureImageUnit[j]);
                    GlStateManager.bindTexture(dfbColorTexturesFlip.getA(j));
                    EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)(36064 + j), (int)3553, (int)dfbColorTexturesFlip.getB(j), (int)0);
                }
                GlStateManager.setActiveTexture(33984);
            }
            Shaders.checkGLError("composite");
            if (renderFinal) {
                Shaders.renderFinal();
                isCompositeRendered = true;
            }
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            GL11.glPopMatrix();
            GL11.glMatrixMode((int)5888);
            GL11.glPopMatrix();
            Shaders.useProgram(ProgramNone);
        }
    }

    private static void preDrawComposite() {
        RenderScale renderscale = activeProgram.getRenderScale();
        if (renderscale != null) {
            int i = (int)((float)renderWidth * renderscale.getOffsetX());
            int j = (int)((float)renderHeight * renderscale.getOffsetY());
            int k = (int)((float)renderWidth * renderscale.getScale());
            int l = (int)((float)renderHeight * renderscale.getScale());
            GL11.glViewport((int)i, (int)j, (int)k, (int)l);
        }
    }

    private static void postDrawComposite() {
        RenderScale renderscale = activeProgram.getRenderScale();
        if (renderscale != null) {
            GL11.glViewport((int)0, (int)0, (int)renderWidth, (int)renderHeight);
        }
    }

    private static void renderFinal() {
        isRenderingDfb = false;
        mc.getFramebuffer().bindFramebuffer(true);
        OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, 3553, Shaders.mc.getFramebuffer().framebufferTexture, 0);
        GL11.glViewport((int)0, (int)0, (int)Shaders.mc.displayWidth, (int)Shaders.mc.displayHeight);
        if (EntityRenderer.anaglyphEnable) {
            boolean flag = EntityRenderer.anaglyphField != 0;
            GlStateManager.colorMask(flag, !flag, !flag, true);
        }
        GlStateManager.depthMask(true);
        GL11.glClearColor((float)clearColorR, (float)clearColorG, (float)clearColorB, (float)1.0f);
        GL11.glClear((int)16640);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        Shaders.checkGLError("pre-final");
        Shaders.useProgram(ProgramFinal);
        Shaders.checkGLError("final");
        if (activeCompositeMipmapSetting != 0) {
            Shaders.genCompositeMipmap();
        }
        Shaders.drawComposite();
        Shaders.checkGLError("renderCompositeFinal");
    }

    public static void endRender() {
        if (isShadowPass) {
            Shaders.checkGLError("shadow endRender");
        } else {
            if (!isCompositeRendered) {
                Shaders.renderCompositeFinal();
            }
            isRenderingWorld = false;
            GlStateManager.colorMask(true, true, true, true);
            Shaders.useProgram(ProgramNone);
            RenderHelper.disableStandardItemLighting();
            Shaders.checkGLError("endRender end");
        }
    }

    public static void beginSky() {
        isRenderingSky = true;
        fogEnabled = true;
        Shaders.setDrawBuffers(dfbDrawBuffers);
        Shaders.useProgram(ProgramSkyTextured);
        Shaders.pushEntity(-2, 0);
    }

    public static void setSkyColor(Vec3 v3color) {
        skyColorR = (float)v3color.x;
        skyColorG = (float)v3color.y;
        skyColorB = (float)v3color.z;
        Shaders.setProgramUniform3f(uniform_skyColor, skyColorR, skyColorG, skyColorB);
    }

    public static void drawHorizon() {
        WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
        float f = Shaders.mc.gameSettings.renderDistanceChunks * 16;
        double d0 = (double)f * 0.9238;
        double d1 = (double)f * 0.3826;
        double d2 = -d1;
        double d3 = -d0;
        double d4 = 16.0;
        double d5 = -cameraPositionY;
        worldrenderer.begin(7, DefaultVertexFormats.field_181705_e);
        worldrenderer.pos(d2, d5, d3).end();
        worldrenderer.pos(d2, d4, d3).end();
        worldrenderer.pos(d3, d4, d2).end();
        worldrenderer.pos(d3, d5, d2).end();
        worldrenderer.pos(d3, d5, d2).end();
        worldrenderer.pos(d3, d4, d2).end();
        worldrenderer.pos(d3, d4, d1).end();
        worldrenderer.pos(d3, d5, d1).end();
        worldrenderer.pos(d3, d5, d1).end();
        worldrenderer.pos(d3, d4, d1).end();
        worldrenderer.pos(d2, d4, d0).end();
        worldrenderer.pos(d2, d5, d0).end();
        worldrenderer.pos(d2, d5, d0).end();
        worldrenderer.pos(d2, d4, d0).end();
        worldrenderer.pos(d1, d4, d0).end();
        worldrenderer.pos(d1, d5, d0).end();
        worldrenderer.pos(d1, d5, d0).end();
        worldrenderer.pos(d1, d4, d0).end();
        worldrenderer.pos(d0, d4, d1).end();
        worldrenderer.pos(d0, d5, d1).end();
        worldrenderer.pos(d0, d5, d1).end();
        worldrenderer.pos(d0, d4, d1).end();
        worldrenderer.pos(d0, d4, d2).end();
        worldrenderer.pos(d0, d5, d2).end();
        worldrenderer.pos(d0, d5, d2).end();
        worldrenderer.pos(d0, d4, d2).end();
        worldrenderer.pos(d1, d4, d3).end();
        worldrenderer.pos(d1, d5, d3).end();
        worldrenderer.pos(d1, d5, d3).end();
        worldrenderer.pos(d1, d4, d3).end();
        worldrenderer.pos(d2, d4, d3).end();
        worldrenderer.pos(d2, d5, d3).end();
        Tessellator.getInstance().draw();
    }

    public static void preSkyList() {
        Shaders.setUpPosition();
        GL11.glColor3f((float)fogColorR, (float)fogColorG, (float)fogColorB);
        Shaders.drawHorizon();
        GL11.glColor3f((float)skyColorR, (float)skyColorG, (float)skyColorB);
    }

    public static void endSky() {
        isRenderingSky = false;
        Shaders.setDrawBuffers(dfbDrawBuffers);
        Shaders.useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
        Shaders.popEntity();
    }

    public static void beginUpdateChunks() {
        Shaders.checkGLError("beginUpdateChunks1");
        Shaders.checkFramebufferStatus("beginUpdateChunks1");
        if (!isShadowPass) {
            Shaders.useProgram(ProgramTerrain);
        }
        Shaders.checkGLError("beginUpdateChunks2");
        Shaders.checkFramebufferStatus("beginUpdateChunks2");
    }

    public static void endUpdateChunks() {
        Shaders.checkGLError("endUpdateChunks1");
        Shaders.checkFramebufferStatus("endUpdateChunks1");
        if (!isShadowPass) {
            Shaders.useProgram(ProgramTerrain);
        }
        Shaders.checkGLError("endUpdateChunks2");
        Shaders.checkFramebufferStatus("endUpdateChunks2");
    }

    public static boolean shouldRenderClouds(GameSettings gs) {
        if (!shaderPackLoaded) {
            return true;
        }
        Shaders.checkGLError("shouldRenderClouds");
        return isShadowPass ? configCloudShadow : gs.clouds > 0;
    }

    public static void beginClouds() {
        fogEnabled = true;
        Shaders.pushEntity(-3, 0);
        Shaders.useProgram(ProgramClouds);
    }

    public static void endClouds() {
        Shaders.disableFog();
        Shaders.popEntity();
        Shaders.useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
    }

    public static void beginEntities() {
        if (isRenderingWorld) {
            Shaders.useProgram(ProgramEntities);
        }
    }

    public static void nextEntity(Entity entity) {
        if (isRenderingWorld) {
            Shaders.useProgram(ProgramEntities);
            Shaders.setEntityId(entity);
        }
    }

    public static void setEntityId(Entity entity) {
        if (uniform_entityId.isDefined()) {
            int i = EntityUtils.getEntityIdByClass(entity);
            int j = EntityAliases.getEntityAliasId(i);
            if (j >= 0) {
                i = j;
            }
            uniform_entityId.setValue(i);
        }
    }

    public static void beginSpiderEyes() {
        if (isRenderingWorld && ProgramSpiderEyes.getId() != ProgramNone.getId()) {
            Shaders.useProgram(ProgramSpiderEyes);
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.0f);
            GlStateManager.blendFunc(770, 771);
        }
    }

    public static void endSpiderEyes() {
        if (isRenderingWorld && ProgramSpiderEyes.getId() != ProgramNone.getId()) {
            Shaders.useProgram(ProgramEntities);
            GlStateManager.disableAlpha();
        }
    }

    public static void endEntities() {
        if (isRenderingWorld) {
            Shaders.setEntityId(null);
            Shaders.useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
        }
    }

    public static void beginEntitiesGlowing() {
        if (isRenderingWorld) {
            isEntitiesGlowing = true;
        }
    }

    public static void endEntitiesGlowing() {
        if (isRenderingWorld) {
            isEntitiesGlowing = false;
        }
    }

    public static void setEntityColor(float r, float g, float b, float a) {
        if (isRenderingWorld && !isShadowPass) {
            uniform_entityColor.setValue(r, g, b, a);
        }
    }

    public static void beginLivingDamage() {
        if (isRenderingWorld) {
            ShadersTex.bindTexture(defaultTexture);
            if (!isShadowPass) {
                Shaders.setDrawBuffers(drawBuffersColorAtt0);
            }
        }
    }

    public static void endLivingDamage() {
        if (isRenderingWorld && !isShadowPass) {
            Shaders.setDrawBuffers(ProgramEntities.getDrawBuffers());
        }
    }

    public static void beginBlockEntities() {
        if (isRenderingWorld) {
            Shaders.checkGLError("beginBlockEntities");
            Shaders.useProgram(ProgramBlock);
        }
    }

    public static void nextBlockEntity(TileEntity tileEntity) {
        if (isRenderingWorld) {
            Shaders.checkGLError("nextBlockEntity");
            Shaders.useProgram(ProgramBlock);
            Shaders.setBlockEntityId(tileEntity);
        }
    }

    public static void setBlockEntityId(TileEntity tileEntity) {
        if (uniform_blockEntityId.isDefined()) {
            int i = Shaders.getBlockEntityId(tileEntity);
            uniform_blockEntityId.setValue(i);
        }
    }

    private static int getBlockEntityId(TileEntity tileEntity) {
        int j;
        if (tileEntity == null) {
            return -1;
        }
        Block block = tileEntity.getBlockType();
        if (block == null) {
            return 0;
        }
        int i = Block.getIdFromBlock(block);
        int k = BlockAliases.getBlockAliasId(i, j = tileEntity.getBlockMetadata());
        if (k >= 0) {
            i = k;
        }
        return i;
    }

    public static void endBlockEntities() {
        if (isRenderingWorld) {
            Shaders.checkGLError("endBlockEntities");
            Shaders.setBlockEntityId(null);
            Shaders.useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
            ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
        }
    }

    public static void beginLitParticles() {
        Shaders.useProgram(ProgramTexturedLit);
    }

    public static void beginParticles() {
        Shaders.useProgram(ProgramTextured);
    }

    public static void endParticles() {
        Shaders.useProgram(ProgramTexturedLit);
    }

    public static void readCenterDepth() {
        if (!isShadowPass && centerDepthSmoothEnabled) {
            tempDirectFloatBuffer.clear();
            GL11.glReadPixels((int)(renderWidth / 2), (int)(renderHeight / 2), (int)1, (int)1, (int)6402, (int)5126, (FloatBuffer)tempDirectFloatBuffer);
            centerDepth = tempDirectFloatBuffer.get(0);
            float f = (float)diffSystemTime * 0.01f;
            float f1 = (float)Math.exp(Math.log(0.5) * (double)f / (double)centerDepthSmoothHalflife);
            centerDepthSmooth = centerDepthSmooth * f1 + centerDepth * (1.0f - f1);
        }
    }

    public static void beginWeather() {
        if (!isShadowPass) {
            if (usedDepthBuffers >= 3) {
                GlStateManager.setActiveTexture(33996);
                GL11.glCopyTexSubImage2D((int)3553, (int)0, (int)0, (int)0, (int)0, (int)0, (int)renderWidth, (int)renderHeight);
                GlStateManager.setActiveTexture(33984);
            }
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.enableAlpha();
            Shaders.useProgram(ProgramWeather);
        }
    }

    public static void endWeather() {
        GlStateManager.disableBlend();
        Shaders.useProgram(ProgramTexturedLit);
    }

    public static void preWater() {
        if (usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            Shaders.checkGLError("pre copy depth");
            GL11.glCopyTexSubImage2D((int)3553, (int)0, (int)0, (int)0, (int)0, (int)0, (int)renderWidth, (int)renderHeight);
            Shaders.checkGLError("copy depth");
            GlStateManager.setActiveTexture(33984);
        }
        ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
    }

    public static void beginWater() {
        if (isRenderingWorld) {
            if (!isShadowPass) {
                Shaders.renderDeferred();
                Shaders.useProgram(ProgramWater);
                GlStateManager.enableBlend();
                GlStateManager.depthMask(true);
            } else {
                GlStateManager.depthMask(true);
            }
        }
    }

    public static void endWater() {
        if (isRenderingWorld) {
            if (isShadowPass) {
                // empty if block
            }
            Shaders.useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
        }
    }

    public static void applyHandDepth() {
        if ((double)configHandDepthMul != 1.0) {
            GL11.glScaled((double)1.0, (double)1.0, (double)configHandDepthMul);
        }
    }

    public static void beginHand(boolean translucent) {
        GL11.glMatrixMode((int)5888);
        GL11.glPushMatrix();
        GL11.glMatrixMode((int)5889);
        GL11.glPushMatrix();
        GL11.glMatrixMode((int)5888);
        if (translucent) {
            Shaders.useProgram(ProgramHandWater);
        } else {
            Shaders.useProgram(ProgramHand);
        }
        Shaders.checkGLError("beginHand");
        Shaders.checkFramebufferStatus("beginHand");
    }

    public static void endHand() {
        Shaders.checkGLError("pre endHand");
        Shaders.checkFramebufferStatus("pre endHand");
        GL11.glMatrixMode((int)5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode((int)5888);
        GL11.glPopMatrix();
        GlStateManager.blendFunc(770, 771);
        Shaders.checkGLError("endHand");
    }

    public static void beginFPOverlay() {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }

    public static void endFPOverlay() {
    }

    public static void glEnableWrapper(int cap) {
        GL11.glEnable((int)cap);
        if (cap == 3553) {
            Shaders.enableTexture2D();
        } else if (cap == 2912) {
            Shaders.enableFog();
        }
    }

    public static void glDisableWrapper(int cap) {
        GL11.glDisable((int)cap);
        if (cap == 3553) {
            Shaders.disableTexture2D();
        } else if (cap == 2912) {
            Shaders.disableFog();
        }
    }

    public static void sglEnableT2D(int cap) {
        GL11.glEnable((int)cap);
        Shaders.enableTexture2D();
    }

    public static void sglDisableT2D(int cap) {
        GL11.glDisable((int)cap);
        Shaders.disableTexture2D();
    }

    public static void sglEnableFog(int cap) {
        GL11.glEnable((int)cap);
        Shaders.enableFog();
    }

    public static void sglDisableFog(int cap) {
        GL11.glDisable((int)cap);
        Shaders.disableFog();
    }

    public static void enableTexture2D() {
        if (isRenderingSky) {
            Shaders.useProgram(ProgramSkyTextured);
        } else if (activeProgram == ProgramBasic) {
            Shaders.useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
        }
    }

    public static void disableTexture2D() {
        if (isRenderingSky) {
            Shaders.useProgram(ProgramSkyBasic);
        } else if (activeProgram == ProgramTextured || activeProgram == ProgramTexturedLit) {
            Shaders.useProgram(ProgramBasic);
        }
    }

    public static void beginLeash() {
        programStackLeash.push(activeProgram);
        Shaders.useProgram(ProgramBasic);
    }

    public static void endLeash() {
        Shaders.useProgram(programStackLeash.pop());
    }

    public static void enableFog() {
        fogEnabled = true;
        Shaders.setProgramUniform1i(uniform_fogMode, fogMode);
        Shaders.setProgramUniform1f(uniform_fogDensity, fogDensity);
    }

    public static void disableFog() {
        fogEnabled = false;
        Shaders.setProgramUniform1i(uniform_fogMode, 0);
    }

    public static void setFogDensity(float value) {
        fogDensity = value;
        if (fogEnabled) {
            Shaders.setProgramUniform1f(uniform_fogDensity, value);
        }
    }

    public static void sglFogi(int pname, int param) {
        GL11.glFogi((int)pname, (int)param);
        if (pname == 2917) {
            fogMode = param;
            if (fogEnabled) {
                Shaders.setProgramUniform1i(uniform_fogMode, fogMode);
            }
        }
    }

    public static void enableLightmap() {
        lightmapEnabled = true;
        if (activeProgram == ProgramTextured) {
            Shaders.useProgram(ProgramTexturedLit);
        }
    }

    public static void disableLightmap() {
        lightmapEnabled = false;
        if (activeProgram == ProgramTexturedLit) {
            Shaders.useProgram(ProgramTextured);
        }
    }

    public static int getEntityData() {
        return entityData[entityDataIndex * 2];
    }

    public static int getEntityData2() {
        return entityData[entityDataIndex * 2 + 1];
    }

    public static int setEntityData1(int data1) {
        Shaders.entityData[Shaders.entityDataIndex * 2] = entityData[entityDataIndex * 2] & 0xFFFF | data1 << 16;
        return data1;
    }

    public static int setEntityData2(int data2) {
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = entityData[entityDataIndex * 2 + 1] & 0xFFFF0000 | data2 & 0xFFFF;
        return data2;
    }

    public static void pushEntity(int data0, int data1) {
        Shaders.entityData[++Shaders.entityDataIndex * 2] = data0 & 0xFFFF | data1 << 16;
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }

    public static void pushEntity(int data0) {
        Shaders.entityData[++Shaders.entityDataIndex * 2] = data0 & 0xFFFF;
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }

    public static void pushEntity(Block block) {
        int i = block.getRenderType();
        Shaders.entityData[++Shaders.entityDataIndex * 2] = Block.blockRegistry.getIDForObject(block) & 0xFFFF | i << 16;
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }

    public static void popEntity() {
        Shaders.entityData[Shaders.entityDataIndex * 2] = 0;
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
        --entityDataIndex;
    }

    public static void mcProfilerEndSection() {
        Shaders.mc.mcProfiler.endSection();
    }

    public static String getShaderPackName() {
        return shaderPack == null ? null : (shaderPack instanceof ShaderPackNone ? null : shaderPack.getName());
    }

    public static InputStream getShaderPackResourceStream(String path) {
        return shaderPack == null ? null : shaderPack.getResourceAsStream(path);
    }

    public static void nextAntialiasingLevel() {
        configAntialiasingLevel += 2;
        if ((configAntialiasingLevel = configAntialiasingLevel / 2 * 2) > 4) {
            configAntialiasingLevel = 0;
        }
        configAntialiasingLevel = Config.limit(configAntialiasingLevel, 0, 4);
    }

    public static void checkShadersModInstalled() {
        try {
            Class<?> clazz = Class.forName("shadersmod.transform.SMCClassTransformer");
        }
        catch (Throwable var1) {
            return;
        }
        throw new RuntimeException("Shaders Mod detected. Please remove it, OptiFine has built-in support for shaders.");
    }

    public static void resourcesReloaded() {
        Shaders.loadShaderPackResources();
        if (shaderPackLoaded) {
            BlockAliases.resourcesReloaded();
            ItemAliases.resourcesReloaded();
            EntityAliases.resourcesReloaded();
        }
    }

    private static void loadShaderPackResources() {
        shaderPackResources = new HashMap<String, String>();
        if (shaderPackLoaded) {
            ArrayList<String> list = new ArrayList<String>();
            String s = "/shaders/lang/";
            String s1 = "en_US";
            String s2 = ".lang";
            list.add(s + s1 + s2);
            if (!Config.getGameSettings().language.equals(s1)) {
                list.add(s + Config.getGameSettings().language + s2);
            }
            try {
                for (String s3 : list) {
                    InputStream inputstream = shaderPack.getResourceAsStream(s3);
                    if (inputstream == null) continue;
                    PropertiesOrdered properties = new PropertiesOrdered();
                    Lang.loadLocaleData(inputstream, properties);
                    inputstream.close();
                    for (Object o : ((Properties)properties).keySet()) {
                        String s4 = (String)o;
                        String s5 = properties.getProperty(s4);
                        shaderPackResources.put(s4, s5);
                    }
                }
            }
            catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }
    }

    public static String translate(String key, String def) {
        String s = shaderPackResources.get(key);
        return s == null ? def : s;
    }

    public static boolean isProgramPath(String path) {
        Program program;
        if (path == null) {
            return false;
        }
        if (path.length() <= 0) {
            return false;
        }
        int i = path.lastIndexOf("/");
        if (i >= 0) {
            path = path.substring(i + 1);
        }
        return (program = Shaders.getProgram(path)) != null;
    }

    public static Program getProgram(String name) {
        return programs.getProgram(name);
    }

    public static void setItemToRenderMain(ItemStack itemToRenderMain) {
        itemToRenderMainTranslucent = Shaders.isTranslucentBlock(itemToRenderMain);
    }

    public static void setItemToRenderOff(ItemStack itemToRenderOff) {
        itemToRenderOffTranslucent = Shaders.isTranslucentBlock(itemToRenderOff);
    }

    public static boolean isItemToRenderMainTranslucent() {
        return itemToRenderMainTranslucent;
    }

    public static boolean isItemToRenderOffTranslucent() {
        return itemToRenderOffTranslucent;
    }

    public static boolean isBothHandsRendered() {
        return isHandRenderedMain && isHandRenderedOff;
    }

    private static boolean isTranslucentBlock(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (item == null) {
            return false;
        }
        if (!(item instanceof ItemBlock)) {
            return false;
        }
        ItemBlock itemblock = (ItemBlock)item;
        Block block = itemblock.getBlock();
        if (block == null) {
            return false;
        }
        EnumWorldBlockLayer enumworldblocklayer = block.getBlockLayer();
        return enumworldblocklayer == EnumWorldBlockLayer.TRANSLUCENT;
    }

    public static boolean isSkipRenderHand() {
        return skipRenderHandMain;
    }

    public static boolean isRenderBothHands() {
        return !skipRenderHandMain && !skipRenderHandOff;
    }

    public static void setSkipRenderHands(boolean skipMain, boolean skipOff) {
        skipRenderHandMain = skipMain;
        skipRenderHandOff = skipOff;
    }

    public static void setHandsRendered(boolean handMain, boolean handOff) {
        isHandRenderedMain = handMain;
        isHandRenderedOff = handOff;
    }

    public static boolean isHandRenderedMain() {
        return isHandRenderedMain;
    }

    public static boolean isHandRenderedOff() {
        return isHandRenderedOff;
    }

    public static float getShadowRenderDistance() {
        return shadowDistanceRenderMul < 0.0f ? -1.0f : shadowMapHalfPlane * shadowDistanceRenderMul;
    }

    public static void setRenderingFirstPersonHand(boolean flag) {
        isRenderingFirstPersonHand = flag;
    }

    public static boolean isRenderingFirstPersonHand() {
        return isRenderingFirstPersonHand;
    }

    public static void beginBeacon() {
        if (isRenderingWorld) {
            Shaders.useProgram(ProgramBeaconBeam);
        }
    }

    public static void endBeacon() {
        if (isRenderingWorld) {
            Shaders.useProgram(ProgramBlock);
        }
    }

    public static World getCurrentWorld() {
        return currentWorld;
    }

    public static BlockPos getCameraPosition() {
        return new BlockPos(cameraPositionX, cameraPositionY, cameraPositionZ);
    }

    public static boolean isCustomUniforms() {
        return customUniforms != null;
    }

    public static boolean canRenderQuads() {
        return hasGeometryShaders ? Shaders.capabilities.GL_NV_geometry_shader4 : true;
    }

    static {
        isInitializedOnce = false;
        isShaderPackInitialized = false;
        hasGlGenMipmap = false;
        countResetDisplayLists = 0;
        renderDisplayWidth = 0;
        renderDisplayHeight = 0;
        renderWidth = 0;
        renderHeight = 0;
        isRenderingWorld = false;
        isRenderingSky = false;
        isCompositeRendered = false;
        isRenderingDfb = false;
        isShadowPass = false;
        isEntitiesGlowing = false;
        renderItemKeepDepthMask = false;
        itemToRenderMainTranslucent = false;
        itemToRenderOffTranslucent = false;
        sunPosition = new float[4];
        moonPosition = new float[4];
        shadowLightPosition = new float[4];
        upPosition = new float[4];
        shadowLightPositionVector = new float[4];
        upPosModelView = new float[]{0.0f, 100.0f, 0.0f, 0.0f};
        sunPosModelView = new float[]{0.0f, 100.0f, 0.0f, 0.0f};
        moonPosModelView = new float[]{0.0f, -100.0f, 0.0f, 0.0f};
        tempMat = new float[16];
        worldTime = 0L;
        lastWorldTime = 0L;
        diffWorldTime = 0L;
        celestialAngle = 0.0f;
        sunAngle = 0.0f;
        shadowAngle = 0.0f;
        moonPhase = 0;
        systemTime = 0L;
        lastSystemTime = 0L;
        diffSystemTime = 0L;
        frameCounter = 0;
        frameTime = 0.0f;
        frameTimeCounter = 0.0f;
        systemTimeInt32 = 0;
        rainStrength = 0.0f;
        wetness = 0.0f;
        wetnessHalfLife = 600.0f;
        drynessHalfLife = 200.0f;
        eyeBrightnessHalflife = 10.0f;
        usewetness = false;
        isEyeInWater = 0;
        eyeBrightness = 0;
        eyeBrightnessFadeX = 0.0f;
        eyeBrightnessFadeY = 0.0f;
        eyePosY = 0.0f;
        centerDepth = 0.0f;
        centerDepthSmooth = 0.0f;
        centerDepthSmoothHalflife = 1.0f;
        centerDepthSmoothEnabled = false;
        superSamplingLevel = 1;
        nightVision = 0.0f;
        blindness = 0.0f;
        lightmapEnabled = false;
        fogEnabled = true;
        entityAttrib = 10;
        midTexCoordAttrib = 11;
        tangentAttrib = 12;
        useEntityAttrib = false;
        useMidTexCoordAttrib = false;
        useTangentAttrib = false;
        progUseEntityAttrib = false;
        progUseMidTexCoordAttrib = false;
        progUseTangentAttrib = false;
        progArbGeometryShader4 = false;
        progMaxVerticesOut = 3;
        hasGeometryShaders = false;
        atlasSizeX = 0;
        atlasSizeY = 0;
        shaderUniforms = new ShaderUniforms();
        uniform_entityColor = shaderUniforms.make4f("entityColor");
        uniform_entityId = shaderUniforms.make1i("entityId");
        uniform_blockEntityId = shaderUniforms.make1i("blockEntityId");
        uniform_texture = shaderUniforms.make1i("texture");
        uniform_lightmap = shaderUniforms.make1i("lightmap");
        uniform_normals = shaderUniforms.make1i("normals");
        uniform_specular = shaderUniforms.make1i("specular");
        uniform_shadow = shaderUniforms.make1i("shadow");
        uniform_watershadow = shaderUniforms.make1i("watershadow");
        uniform_shadowtex0 = shaderUniforms.make1i("shadowtex0");
        uniform_shadowtex1 = shaderUniforms.make1i("shadowtex1");
        uniform_depthtex0 = shaderUniforms.make1i("depthtex0");
        uniform_depthtex1 = shaderUniforms.make1i("depthtex1");
        uniform_shadowcolor = shaderUniforms.make1i("shadowcolor");
        uniform_shadowcolor0 = shaderUniforms.make1i("shadowcolor0");
        uniform_shadowcolor1 = shaderUniforms.make1i("shadowcolor1");
        uniform_noisetex = shaderUniforms.make1i("noisetex");
        uniform_gcolor = shaderUniforms.make1i("gcolor");
        uniform_gdepth = shaderUniforms.make1i("gdepth");
        uniform_gnormal = shaderUniforms.make1i("gnormal");
        uniform_composite = shaderUniforms.make1i("composite");
        uniform_gaux1 = shaderUniforms.make1i("gaux1");
        uniform_gaux2 = shaderUniforms.make1i("gaux2");
        uniform_gaux3 = shaderUniforms.make1i("gaux3");
        uniform_gaux4 = shaderUniforms.make1i("gaux4");
        uniform_colortex0 = shaderUniforms.make1i("colortex0");
        uniform_colortex1 = shaderUniforms.make1i("colortex1");
        uniform_colortex2 = shaderUniforms.make1i("colortex2");
        uniform_colortex3 = shaderUniforms.make1i("colortex3");
        uniform_colortex4 = shaderUniforms.make1i("colortex4");
        uniform_colortex5 = shaderUniforms.make1i("colortex5");
        uniform_colortex6 = shaderUniforms.make1i("colortex6");
        uniform_colortex7 = shaderUniforms.make1i("colortex7");
        uniform_gdepthtex = shaderUniforms.make1i("gdepthtex");
        uniform_depthtex2 = shaderUniforms.make1i("depthtex2");
        uniform_tex = shaderUniforms.make1i("tex");
        uniform_heldItemId = shaderUniforms.make1i("heldItemId");
        uniform_heldBlockLightValue = shaderUniforms.make1i("heldBlockLightValue");
        uniform_heldItemId2 = shaderUniforms.make1i("heldItemId2");
        uniform_heldBlockLightValue2 = shaderUniforms.make1i("heldBlockLightValue2");
        uniform_fogMode = shaderUniforms.make1i("fogMode");
        uniform_fogDensity = shaderUniforms.make1f("fogDensity");
        uniform_fogColor = shaderUniforms.make3f("fogColor");
        uniform_skyColor = shaderUniforms.make3f("skyColor");
        uniform_worldTime = shaderUniforms.make1i("worldTime");
        uniform_worldDay = shaderUniforms.make1i("worldDay");
        uniform_moonPhase = shaderUniforms.make1i("moonPhase");
        uniform_frameCounter = shaderUniforms.make1i("frameCounter");
        uniform_frameTime = shaderUniforms.make1f("frameTime");
        uniform_frameTimeCounter = shaderUniforms.make1f("frameTimeCounter");
        uniform_sunAngle = shaderUniforms.make1f("sunAngle");
        uniform_shadowAngle = shaderUniforms.make1f("shadowAngle");
        uniform_rainStrength = shaderUniforms.make1f("rainStrength");
        uniform_aspectRatio = shaderUniforms.make1f("aspectRatio");
        uniform_viewWidth = shaderUniforms.make1f("viewWidth");
        uniform_viewHeight = shaderUniforms.make1f("viewHeight");
        uniform_near = shaderUniforms.make1f("near");
        uniform_far = shaderUniforms.make1f("far");
        uniform_sunPosition = shaderUniforms.make3f("sunPosition");
        uniform_moonPosition = shaderUniforms.make3f("moonPosition");
        uniform_shadowLightPosition = shaderUniforms.make3f("shadowLightPosition");
        uniform_upPosition = shaderUniforms.make3f("upPosition");
        uniform_previousCameraPosition = shaderUniforms.make3f("previousCameraPosition");
        uniform_cameraPosition = shaderUniforms.make3f("cameraPosition");
        uniform_gbufferModelView = shaderUniforms.makeM4("gbufferModelView");
        uniform_gbufferModelViewInverse = shaderUniforms.makeM4("gbufferModelViewInverse");
        uniform_gbufferPreviousProjection = shaderUniforms.makeM4("gbufferPreviousProjection");
        uniform_gbufferProjection = shaderUniforms.makeM4("gbufferProjection");
        uniform_gbufferProjectionInverse = shaderUniforms.makeM4("gbufferProjectionInverse");
        uniform_gbufferPreviousModelView = shaderUniforms.makeM4("gbufferPreviousModelView");
        uniform_shadowProjection = shaderUniforms.makeM4("shadowProjection");
        uniform_shadowProjectionInverse = shaderUniforms.makeM4("shadowProjectionInverse");
        uniform_shadowModelView = shaderUniforms.makeM4("shadowModelView");
        uniform_shadowModelViewInverse = shaderUniforms.makeM4("shadowModelViewInverse");
        uniform_wetness = shaderUniforms.make1f("wetness");
        uniform_eyeAltitude = shaderUniforms.make1f("eyeAltitude");
        uniform_eyeBrightness = shaderUniforms.make2i("eyeBrightness");
        uniform_eyeBrightnessSmooth = shaderUniforms.make2i("eyeBrightnessSmooth");
        uniform_terrainTextureSize = shaderUniforms.make2i("terrainTextureSize");
        uniform_terrainIconSize = shaderUniforms.make1i("terrainIconSize");
        uniform_isEyeInWater = shaderUniforms.make1i("isEyeInWater");
        uniform_nightVision = shaderUniforms.make1f("nightVision");
        uniform_blindness = shaderUniforms.make1f("blindness");
        uniform_screenBrightness = shaderUniforms.make1f("screenBrightness");
        uniform_hideGUI = shaderUniforms.make1i("hideGUI");
        uniform_centerDepthSmooth = shaderUniforms.make1f("centerDepthSmooth");
        uniform_atlasSize = shaderUniforms.make2i("atlasSize");
        uniform_blendFunc = shaderUniforms.make4i("blendFunc");
        uniform_instanceId = shaderUniforms.make1i("instanceId");
        shadowPassInterval = 0;
        needResizeShadow = false;
        shadowMapWidth = 1024;
        shadowMapHeight = 1024;
        spShadowMapWidth = 1024;
        spShadowMapHeight = 1024;
        shadowMapFOV = 90.0f;
        shadowMapHalfPlane = 160.0f;
        shadowMapIsOrtho = true;
        shadowDistanceRenderMul = -1.0f;
        shadowPassCounter = 0;
        shouldSkipDefaultShadow = false;
        waterShadowEnabled = false;
        usedColorBuffers = 0;
        usedDepthBuffers = 0;
        usedShadowColorBuffers = 0;
        usedShadowDepthBuffers = 0;
        usedColorAttachs = 0;
        usedDrawBuffers = 0;
        dfb = 0;
        sfb = 0;
        gbuffersFormat = new int[8];
        gbuffersClear = new boolean[8];
        gbuffersClearColor = new Vector4f[8];
        programs = new Programs();
        ProgramNone = programs.getProgramNone();
        ProgramShadow = programs.makeShadow("shadow", ProgramNone);
        ProgramShadowSolid = programs.makeShadow("shadow_solid", ProgramShadow);
        ProgramShadowCutout = programs.makeShadow("shadow_cutout", ProgramShadow);
        ProgramBasic = programs.makeGbuffers("gbuffers_basic", ProgramNone);
        ProgramTextured = programs.makeGbuffers("gbuffers_textured", ProgramBasic);
        ProgramTexturedLit = programs.makeGbuffers("gbuffers_textured_lit", ProgramTextured);
        ProgramSkyBasic = programs.makeGbuffers("gbuffers_skybasic", ProgramBasic);
        ProgramSkyTextured = programs.makeGbuffers("gbuffers_skytextured", ProgramTextured);
        ProgramClouds = programs.makeGbuffers("gbuffers_clouds", ProgramTextured);
        ProgramTerrain = programs.makeGbuffers("gbuffers_terrain", ProgramTexturedLit);
        ProgramTerrainSolid = programs.makeGbuffers("gbuffers_terrain_solid", ProgramTerrain);
        ProgramTerrainCutoutMip = programs.makeGbuffers("gbuffers_terrain_cutout_mip", ProgramTerrain);
        ProgramTerrainCutout = programs.makeGbuffers("gbuffers_terrain_cutout", ProgramTerrain);
        ProgramDamagedBlock = programs.makeGbuffers("gbuffers_damagedblock", ProgramTerrain);
        ProgramBlock = programs.makeGbuffers("gbuffers_block", ProgramTerrain);
        ProgramBeaconBeam = programs.makeGbuffers("gbuffers_beaconbeam", ProgramTextured);
        ProgramItem = programs.makeGbuffers("gbuffers_item", ProgramTexturedLit);
        ProgramEntities = programs.makeGbuffers("gbuffers_entities", ProgramTexturedLit);
        ProgramEntitiesGlowing = programs.makeGbuffers("gbuffers_entities_glowing", ProgramEntities);
        ProgramArmorGlint = programs.makeGbuffers("gbuffers_armor_glint", ProgramTextured);
        ProgramSpiderEyes = programs.makeGbuffers("gbuffers_spidereyes", ProgramTextured);
        ProgramHand = programs.makeGbuffers("gbuffers_hand", ProgramTexturedLit);
        ProgramWeather = programs.makeGbuffers("gbuffers_weather", ProgramTexturedLit);
        ProgramDeferredPre = programs.makeVirtual("deferred_pre");
        ProgramsDeferred = programs.makeDeferreds("deferred", 16);
        ProgramDeferred = ProgramsDeferred[0];
        ProgramWater = programs.makeGbuffers("gbuffers_water", ProgramTerrain);
        ProgramHandWater = programs.makeGbuffers("gbuffers_hand_water", ProgramHand);
        ProgramCompositePre = programs.makeVirtual("composite_pre");
        ProgramsComposite = programs.makeComposites("composite", 16);
        ProgramComposite = ProgramsComposite[0];
        ProgramFinal = programs.makeComposite("final");
        ProgramCount = programs.getCount();
        ProgramsAll = programs.getPrograms();
        activeProgram = ProgramNone;
        activeProgramID = 0;
        programStackLeash = new ProgramStack();
        hasDeferredPrograms = false;
        activeDrawBuffers = null;
        activeCompositeMipmapSetting = 0;
        loadedShaders = null;
        shadersConfig = null;
        defaultTexture = null;
        shadowHardwareFilteringEnabled = new boolean[2];
        shadowMipmapEnabled = new boolean[2];
        shadowFilterNearest = new boolean[2];
        shadowColorMipmapEnabled = new boolean[8];
        shadowColorFilterNearest = new boolean[8];
        configTweakBlockDamage = false;
        configCloudShadow = false;
        configHandDepthMul = 0.125f;
        configRenderResMul = 1.0f;
        configShadowResMul = 1.0f;
        configTexMinFilB = 0;
        configTexMinFilN = 0;
        configTexMinFilS = 0;
        configTexMagFilB = 0;
        configTexMagFilN = 0;
        configTexMagFilS = 0;
        configShadowClipFrustrum = true;
        configNormalMap = true;
        configSpecularMap = true;
        configOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
        configOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
        configAntialiasingLevel = 0;
        texMinFilDesc = new String[]{"Nearest", "Nearest-Nearest", "Nearest-Linear"};
        texMagFilDesc = new String[]{"Nearest", "Linear"};
        texMinFilValue = new int[]{9728, 9984, 9986};
        texMagFilValue = new int[]{9728, 9729};
        shaderPack = null;
        shaderPackLoaded = false;
        shaderPackOptions = null;
        shaderPackOptionSliders = null;
        shaderPackProfiles = null;
        shaderPackGuiScreens = null;
        shaderPackProgramConditions = new HashMap<String, IExpressionBool>();
        shaderPackClouds = new PropertyDefaultFastFancyOff("clouds", "Clouds", 0);
        shaderPackOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
        shaderPackOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
        shaderPackDynamicHandLight = new PropertyDefaultTrueFalse("dynamicHandLight", "Dynamic Hand Light", 0);
        shaderPackShadowTranslucent = new PropertyDefaultTrueFalse("shadowTranslucent", "Shadow Translucent", 0);
        shaderPackUnderwaterOverlay = new PropertyDefaultTrueFalse("underwaterOverlay", "Underwater Overlay", 0);
        shaderPackSun = new PropertyDefaultTrueFalse("sun", "Sun", 0);
        shaderPackMoon = new PropertyDefaultTrueFalse("moon", "Moon", 0);
        shaderPackVignette = new PropertyDefaultTrueFalse("vignette", "Vignette", 0);
        shaderPackBackFaceSolid = new PropertyDefaultTrueFalse("backFace.solid", "Back-face Solid", 0);
        shaderPackBackFaceCutout = new PropertyDefaultTrueFalse("backFace.cutout", "Back-face Cutout", 0);
        shaderPackBackFaceCutoutMipped = new PropertyDefaultTrueFalse("backFace.cutoutMipped", "Back-face Cutout Mipped", 0);
        shaderPackBackFaceTranslucent = new PropertyDefaultTrueFalse("backFace.translucent", "Back-face Translucent", 0);
        shaderPackRainDepth = new PropertyDefaultTrueFalse("rain.depth", "Rain Depth", 0);
        shaderPackBeaconBeamDepth = new PropertyDefaultTrueFalse("beacon.beam.depth", "Rain Depth", 0);
        shaderPackSeparateAo = new PropertyDefaultTrueFalse("separateAo", "Separate AO", 0);
        shaderPackFrustumCulling = new PropertyDefaultTrueFalse("frustum.culling", "Frustum Culling", 0);
        shaderPackResources = new HashMap<String, String>();
        currentWorld = null;
        shaderPackDimensions = new ArrayList<Integer>();
        customTexturesGbuffers = null;
        customTexturesComposite = null;
        customTexturesDeferred = null;
        noiseTexturePath = null;
        customUniforms = null;
        STAGE_NAMES = new String[]{"gbuffers", "composite", "deferred"};
        saveFinalShaders = System.getProperty("shaders.debug.save", "false").equals("true");
        blockLightLevel05 = 0.5f;
        blockLightLevel06 = 0.6f;
        blockLightLevel08 = 0.8f;
        aoLevel = -1.0f;
        sunPathRotation = 0.0f;
        shadowAngleInterval = 0.0f;
        fogMode = 0;
        fogDensity = 0.0f;
        shadowIntervalSize = 2.0f;
        terrainIconSize = 16;
        terrainTextureSize = new int[2];
        noiseTextureEnabled = false;
        noiseTextureResolution = 256;
        colorTextureImageUnit = new int[]{0, 1, 2, 3, 7, 8, 9, 10};
        bigBufferSize = (285 + 8 * ProgramCount) * 4;
        bigBuffer = (ByteBuffer)BufferUtils.createByteBuffer((int)bigBufferSize).limit(0);
        faProjection = new float[16];
        faProjectionInverse = new float[16];
        faModelView = new float[16];
        faModelViewInverse = new float[16];
        faShadowProjection = new float[16];
        faShadowProjectionInverse = new float[16];
        faShadowModelView = new float[16];
        faShadowModelViewInverse = new float[16];
        projection = Shaders.nextFloatBuffer(16);
        projectionInverse = Shaders.nextFloatBuffer(16);
        modelView = Shaders.nextFloatBuffer(16);
        modelViewInverse = Shaders.nextFloatBuffer(16);
        shadowProjection = Shaders.nextFloatBuffer(16);
        shadowProjectionInverse = Shaders.nextFloatBuffer(16);
        shadowModelView = Shaders.nextFloatBuffer(16);
        shadowModelViewInverse = Shaders.nextFloatBuffer(16);
        previousProjection = Shaders.nextFloatBuffer(16);
        previousModelView = Shaders.nextFloatBuffer(16);
        tempMatrixDirectBuffer = Shaders.nextFloatBuffer(16);
        tempDirectFloatBuffer = Shaders.nextFloatBuffer(16);
        dfbColorTextures = Shaders.nextIntBuffer(16);
        dfbDepthTextures = Shaders.nextIntBuffer(3);
        sfbColorTextures = Shaders.nextIntBuffer(8);
        sfbDepthTextures = Shaders.nextIntBuffer(2);
        dfbDrawBuffers = Shaders.nextIntBuffer(8);
        sfbDrawBuffers = Shaders.nextIntBuffer(8);
        drawBuffersNone = (IntBuffer)Shaders.nextIntBuffer(8).limit(0);
        drawBuffersColorAtt0 = (IntBuffer)Shaders.nextIntBuffer(8).put(36064).position(0).limit(1);
        dfbColorTexturesFlip = new FlipTextures(dfbColorTextures, 8);
        formatNames = new String[]{"R8", "RG8", "RGB8", "RGBA8", "R8_SNORM", "RG8_SNORM", "RGB8_SNORM", "RGBA8_SNORM", "R16", "RG16", "RGB16", "RGBA16", "R16_SNORM", "RG16_SNORM", "RGB16_SNORM", "RGBA16_SNORM", "R16F", "RG16F", "RGB16F", "RGBA16F", "R32F", "RG32F", "RGB32F", "RGBA32F", "R32I", "RG32I", "RGB32I", "RGBA32I", "R32UI", "RG32UI", "RGB32UI", "RGBA32UI", "R3_G3_B2", "RGB5_A1", "RGB10_A2", "R11F_G11F_B10F", "RGB9_E5"};
        formatIds = new int[]{33321, 33323, 32849, 32856, 36756, 36757, 36758, 36759, 33322, 33324, 32852, 32859, 36760, 36761, 36762, 36763, 33325, 33327, 34843, 34842, 33326, 33328, 34837, 34836, 33333, 33339, 36227, 36226, 33334, 33340, 36209, 36208, 10768, 32855, 32857, 35898, 35901};
        patternLoadEntityDataMap = Pattern.compile("\\s*([\\w:]+)\\s*=\\s*([-]?\\d+)\\s*");
        entityData = new int[32];
        entityDataIndex = 0;
        shaderPacksDir = new File(Minecraft.getMinecraft().mcDataDir, SHADER_PACKS_DIR_NAME);
        configFile = new File(Minecraft.getMinecraft().mcDataDir, OPTIONS_FILE_NAME);
    }
}

