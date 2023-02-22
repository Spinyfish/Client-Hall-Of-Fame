package net.minecraft.src;

import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.BlockPos;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.DynamicLights;
import net.optifine.VersionCheckThread;
import net.optifine.config.GlVersion;
import net.optifine.gui.GuiMessage;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.Shaders;
import net.optifine.util.DisplayModeComparator;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.TextureUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {
    public static final String OF_NAME = "OptiFine";
    public static final String MC_VERSION = "1.8.9";
    public static final String OF_EDITION = "HD_U";
    public static final String OF_RELEASE = "L5";
    public static final String VERSION = "OptiFine_1.8.9_HD_U_L5";
    private static String build = null;
    private static String newRelease = null;
    private static boolean notify64BitJava = false;
    public static String openGlVersion = null;
    public static String openGlRenderer = null;
    public static String openGlVendor = null;
    public static String[] openGlExtensions = null;
    public static GlVersion glVersion = null;
    public static GlVersion glslVersion = null;
    public static int minecraftVersionInt = -1;
    public static boolean fancyFogAvailable = false;
    public static boolean occlusionAvailable = false;
    private static GameSettings gameSettings = null;
    private static final Minecraft minecraft = Minecraft.getMinecraft();
    private static boolean initialized = false;
    private static Thread minecraftThread = null;
    private static DisplayMode desktopDisplayMode = null;
    private static DisplayMode[] displayModes = null;
    private static int antialiasingLevel = 0;
    private static int availableProcessors = 0;
    public static boolean zoomMode = false;
    private static int texturePackClouds = 0;
    public static boolean waterOpacityChanged = false;
    private static boolean fullscreenModeChecked = false;
    private static boolean desktopModeChecked = false;
    private static DefaultResourcePack defaultResourcePackLazy = null;
    public static final Float DEF_ALPHA_FUNC_LEVEL = Float.valueOf(0.1F);
    private static final Logger LOGGER = LogManager.getLogger();
    public static final boolean logDetail = System.getProperty("log.detail", "false").equals("true");
    private static String mcDebugLast = null;
    private static int fpsMinLast = 0;
    public static float renderPartialTicks;

    public static String getVersion() {
        return "OptiFine_1.8.9_HD_U_L5";
    }

    public static String getVersionDebug() {
        final StringBuffer stringbuffer = new StringBuffer(32);

        if (isDynamicLights()) {
            stringbuffer.append("DL: ");
            stringbuffer.append(DynamicLights.getCount());
            stringbuffer.append(", ");
        }

        stringbuffer.append("OptiFine_1.8.9_HD_U_L5");
        final String s = Shaders.getShaderPackName();

        if (s != null) {
            stringbuffer.append(", ");
            stringbuffer.append(s);
        }

        return stringbuffer.toString();
    }

    public static void initGameSettings(final GameSettings p_initGameSettings_0_) {
        if (gameSettings == null) {
            gameSettings = p_initGameSettings_0_;
            desktopDisplayMode = Display.getDesktopDisplayMode();
            updateAvailableProcessors();
            ReflectorForge.putLaunchBlackboard("optifine.ForgeSplashCompatible", Boolean.TRUE);
        }
    }

    public static void initDisplay() {
        checkInitialized();
        antialiasingLevel = gameSettings.ofAaLevel;
        checkDisplaySettings();
        checkDisplayMode();
        minecraftThread = Thread.currentThread();
        updateThreadPriorities();
        Shaders.startup(Minecraft.getMinecraft());
    }

    public static void checkInitialized() {
        if (!initialized) {
            if (Display.isCreated()) {
                initialized = true;
                checkOpenGlCaps();
            }
        }
    }

    private static void checkOpenGlCaps() {
        log("");
        log(getVersion());
        log("Build: " + getBuild());
        log("OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        log("Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        log("VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        log("LWJGL: " + Sys.getVersion());
        openGlVersion = GL11.glGetString(GL11.GL_VERSION);
        openGlRenderer = GL11.glGetString(GL11.GL_RENDERER);
        openGlVendor = GL11.glGetString(GL11.GL_VENDOR);
        log("OpenGL: " + openGlRenderer + ", version " + openGlVersion + ", " + openGlVendor);
        log("OpenGL Version: " + getOpenGlVersionString());

        if (!GLContext.getCapabilities().OpenGL12) {
            log("OpenGL Mipmap levels: Not available (GL12.GL_TEXTURE_MAX_LEVEL)");
        }

        fancyFogAvailable = GLContext.getCapabilities().GL_NV_fog_distance;

        if (!fancyFogAvailable) {
            log("OpenGL Fancy fog: Not available (GL_NV_fog_distance)");
        }

        occlusionAvailable = GLContext.getCapabilities().GL_ARB_occlusion_query;

        if (!occlusionAvailable) {
            log("OpenGL Occlussion culling: Not available (GL_ARB_occlusion_query)");
        }

        final int i = TextureUtils.getGLMaximumTextureSize();
        dbg("Maximum texture size: " + i + "x" + i);
    }

    public static String getBuild() {
        if (build == null) {
            try {
                final InputStream inputstream = Config.class.getResourceAsStream("/buildof.txt");

                if (inputstream == null) {
                    return null;
                }

                build = readLines(inputstream)[0];
            } catch (final Exception exception) {
                warn("" + exception.getClass().getName() + ": " + exception.getMessage());
                build = "";
            }
        }

        return build;
    }

    public static boolean isFancyFogAvailable() {
        return fancyFogAvailable;
    }

    public static boolean isOcclusionAvailable() {
        return occlusionAvailable;
    }

    public static int getMinecraftVersionInt() {
        if (minecraftVersionInt < 0) {
            final String[] astring = tokenize("1.8.9", ".");
            int i = 0;

            if (astring.length > 0) {
                i += 10000 * parseInt(astring[0], 0);
            }

            if (astring.length > 1) {
                i += 100 * parseInt(astring[1], 0);
            }

            if (astring.length > 2) {
                i += 1 * parseInt(astring[2], 0);
            }

            minecraftVersionInt = i;
        }

        return minecraftVersionInt;
    }

    public static String getOpenGlVersionString() {
        final GlVersion glversion = getGlVersion();
        final String s = "" + glversion.getMajor() + "." + glversion.getMinor() + "." + glversion.getRelease();
        return s;
    }

    private static GlVersion getGlVersionLwjgl() {
        return GLContext.getCapabilities().OpenGL44 ? new GlVersion(4, 4) : (GLContext.getCapabilities().OpenGL43 ? new GlVersion(4, 3) : (GLContext.getCapabilities().OpenGL42 ? new GlVersion(4, 2) : (GLContext.getCapabilities().OpenGL41 ? new GlVersion(4, 1) : (GLContext.getCapabilities().OpenGL40 ? new GlVersion(4, 0) : (GLContext.getCapabilities().OpenGL33 ? new GlVersion(3, 3) : (GLContext.getCapabilities().OpenGL32 ? new GlVersion(3, 2) : (GLContext.getCapabilities().OpenGL31 ? new GlVersion(3, 1) : (GLContext.getCapabilities().OpenGL30 ? new GlVersion(3, 0) : (GLContext.getCapabilities().OpenGL21 ? new GlVersion(2, 1) : (GLContext.getCapabilities().OpenGL20 ? new GlVersion(2, 0) : (GLContext.getCapabilities().OpenGL15 ? new GlVersion(1, 5) : (GLContext.getCapabilities().OpenGL14 ? new GlVersion(1, 4) : (GLContext.getCapabilities().OpenGL13 ? new GlVersion(1, 3) : (GLContext.getCapabilities().OpenGL12 ? new GlVersion(1, 2) : (GLContext.getCapabilities().OpenGL11 ? new GlVersion(1, 1) : new GlVersion(1, 0))))))))))))))));
    }

    public static GlVersion getGlVersion() {
        if (glVersion == null) {
            final String s = GL11.glGetString(GL11.GL_VERSION);
            glVersion = parseGlVersion(s, null);

            if (glVersion == null) {
                glVersion = getGlVersionLwjgl();
            }

            if (glVersion == null) {
                glVersion = new GlVersion(1, 0);
            }
        }

        return glVersion;
    }

    public static GlVersion getGlslVersion() {
        if (glslVersion == null) {
            final String s = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
            glslVersion = parseGlVersion(s, null);

            if (glslVersion == null) {
                glslVersion = new GlVersion(1, 10);
            }
        }

        return glslVersion;
    }

    public static GlVersion parseGlVersion(final String p_parseGlVersion_0_, final GlVersion p_parseGlVersion_1_) {
        try {
            if (p_parseGlVersion_0_ == null) {
                return p_parseGlVersion_1_;
            } else {
                final Pattern pattern = Pattern.compile("([0-9]+)\\.([0-9]+)(\\.([0-9]+))?(.+)?");
                final Matcher matcher = pattern.matcher(p_parseGlVersion_0_);

                if (!matcher.matches()) {
                    return p_parseGlVersion_1_;
                } else {
                    final int i = Integer.parseInt(matcher.group(1));
                    final int j = Integer.parseInt(matcher.group(2));
                    final int k = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
                    final String s = matcher.group(5);
                    return new GlVersion(i, j, k, s);
                }
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
            return p_parseGlVersion_1_;
        }
    }

    public static String[] getOpenGlExtensions() {
        if (openGlExtensions == null) {
            openGlExtensions = detectOpenGlExtensions();
        }

        return openGlExtensions;
    }

    private static String[] detectOpenGlExtensions() {
        try {
            final GlVersion glversion = getGlVersion();

            if (glversion.getMajor() >= 3) {
                final int i = GL11.glGetInteger(33309);

                if (i > 0) {
                    final String[] astring = new String[i];

                    for (int j = 0; j < i; ++j) {
                        astring[j] = GL30.glGetStringi(7939, j);
                    }

                    return astring;
                }
            }
        } catch (final Exception exception1) {
            exception1.printStackTrace();
        }

        try {
            final String s = GL11.glGetString(GL11.GL_EXTENSIONS);
            final String[] astring1 = s.split(" ");
            return astring1;
        } catch (final Exception exception) {
            exception.printStackTrace();
            return new String[0];
        }
    }

    public static void updateThreadPriorities() {
        updateAvailableProcessors();
        final int i = 8;

        if (isSingleProcessor()) {
            if (isSmoothWorld()) {
                minecraftThread.setPriority(10);
                setThreadPriority("Server thread", 1);
            } else {
                minecraftThread.setPriority(5);
                setThreadPriority("Server thread", 5);
            }
        } else {
            minecraftThread.setPriority(10);
            setThreadPriority("Server thread", 5);
        }
    }

    private static void setThreadPriority(final String p_setThreadPriority_0_, final int p_setThreadPriority_1_) {
        try {
            final ThreadGroup threadgroup = Thread.currentThread().getThreadGroup();

            if (threadgroup == null) {
                return;
            }

            final int i = (threadgroup.activeCount() + 10) * 2;
            final Thread[] athread = new Thread[i];
            threadgroup.enumerate(athread, false);

            for (int j = 0; j < athread.length; ++j) {
                final Thread thread = athread[j];

                if (thread != null && thread.getName().startsWith(p_setThreadPriority_0_)) {
                    thread.setPriority(p_setThreadPriority_1_);
                }
            }
        } catch (final Throwable throwable) {
            warn(throwable.getClass().getName() + ": " + throwable.getMessage());
        }
    }

    public static boolean isMinecraftThread() {
        return Thread.currentThread() == minecraftThread;
    }

    private static void startVersionCheckThread() {
        final VersionCheckThread versioncheckthread = new VersionCheckThread();
        versioncheckthread.start();
    }

    public static boolean isMipmaps() {
        return gameSettings.mipmapLevels > 0;
    }

    public static int getMipmapLevels() {
        return gameSettings.mipmapLevels;
    }

    public static int getMipmapType() {
        switch (gameSettings.ofMipmapType) {
            case 0:
                return 9986;

            case 1:
                return 9986;

            case 2:
                if (isMultiTexture()) {
                    return 9985;
                }

                return 9986;

            case 3:
                if (isMultiTexture()) {
                    return 9987;
                }

                return 9986;

            default:
                return 9986;
        }
    }

    public static boolean isUseAlphaFunc() {
        final float f = getAlphaFuncLevel();
        return f > DEF_ALPHA_FUNC_LEVEL.floatValue() + 1.0E-5F;
    }

    public static float getAlphaFuncLevel() {
        return DEF_ALPHA_FUNC_LEVEL.floatValue();
    }

    public static boolean isFogFancy() {
        return isFancyFogAvailable() && gameSettings.ofFogType == 2;
    }

    public static boolean isFogFast() {
        return gameSettings.ofFogType == 1;
    }

    public static boolean isFogOff() {
        return gameSettings.ofFogType == 3;
    }

    public static boolean isFogOn() {
        return gameSettings.ofFogType != 3;
    }

    public static float getFogStart() {
        return gameSettings.ofFogStart;
    }

    public static void detail(final String p_detail_0_) {
        if (logDetail) {
            LOGGER.info("[OptiFine] " + p_detail_0_);
        }
    }

    public static void dbg(final String p_dbg_0_) {
        LOGGER.info("[OptiFine] " + p_dbg_0_);
    }

    public static void warn(final String p_warn_0_) {
        LOGGER.warn("[OptiFine] " + p_warn_0_);
    }

    public static void error(final String p_error_0_) {
        LOGGER.error("[OptiFine] " + p_error_0_);
    }

    public static void log(final String p_log_0_) {
        dbg(p_log_0_);
    }

    public static int getUpdatesPerFrame() {
        return gameSettings.ofChunkUpdates;
    }

    public static boolean isDynamicUpdates() {
        return gameSettings.ofChunkUpdatesDynamic;
    }

    public static boolean isRainFancy() {
        return gameSettings.ofRain == 0 ? gameSettings.fancyGraphics : gameSettings.ofRain == 2;
    }

    public static boolean isRainOff() {
        return gameSettings.ofRain == 3;
    }

    public static boolean isCloudsFancy() {
        return gameSettings.ofClouds != 0 ? gameSettings.ofClouds == 2 : (isShaders() && !Shaders.shaderPackClouds.isDefault() ? Shaders.shaderPackClouds.isFancy() : (texturePackClouds != 0 ? texturePackClouds == 2 : gameSettings.fancyGraphics));
    }

    public static boolean isCloudsOff() {
        return gameSettings.ofClouds != 0 ? gameSettings.ofClouds == 3 : (isShaders() && !Shaders.shaderPackClouds.isDefault() ? Shaders.shaderPackClouds.isOff() : (texturePackClouds != 0 && texturePackClouds == 3));
    }

    public static void updateTexturePackClouds() {
        texturePackClouds = 0;
        final IResourceManager iresourcemanager = getResourceManager();

        if (iresourcemanager != null) {
            try {
                final InputStream inputstream = iresourcemanager.getResource(new ResourceLocation("mcpatcher/color.properties")).getInputStream();

                if (inputstream == null) {
                    return;
                }

                final Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                String s = properties.getProperty("clouds");

                if (s == null) {
                    return;
                }

                dbg("Texture pack clouds: " + s);
                s = s.toLowerCase();

                if (s.equals("fast")) {
                    texturePackClouds = 1;
                }

                if (s.equals("fancy")) {
                    texturePackClouds = 2;
                }

                if (s.equals("off")) {
                    texturePackClouds = 3;
                }
            } catch (final Exception var4) {
            }
        }
    }

    public static ModelManager getModelManager() {
        return minecraft.getRenderItem().modelManager;
    }

    public static boolean isTreesFancy() {
        return gameSettings.ofTrees == 0 ? gameSettings.fancyGraphics : gameSettings.ofTrees != 1;
    }

    public static boolean isTreesSmart() {
        return gameSettings.ofTrees == 4;
    }

    public static boolean isCullFacesLeaves() {
        return gameSettings.ofTrees == 0 ? !gameSettings.fancyGraphics : gameSettings.ofTrees == 4;
    }

    public static boolean isDroppedItemsFancy() {
        return gameSettings.ofDroppedItems == 0 ? gameSettings.fancyGraphics : gameSettings.ofDroppedItems == 2;
    }

    public static int limit(final int p_limit_0_, final int p_limit_1_, final int p_limit_2_) {
        return p_limit_0_ < p_limit_1_ ? p_limit_1_ : (p_limit_0_ > p_limit_2_ ? p_limit_2_ : p_limit_0_);
    }

    public static float limit(final float p_limit_0_, final float p_limit_1_, final float p_limit_2_) {
        return p_limit_0_ < p_limit_1_ ? p_limit_1_ : (p_limit_0_ > p_limit_2_ ? p_limit_2_ : p_limit_0_);
    }

    public static double limit(final double p_limit_0_, final double p_limit_2_, final double p_limit_4_) {
        return p_limit_0_ < p_limit_2_ ? p_limit_2_ : (p_limit_0_ > p_limit_4_ ? p_limit_4_ : p_limit_0_);
    }

    public static float limitTo1(final float p_limitTo1_0_) {
        return p_limitTo1_0_ < 0.0F ? 0.0F : (p_limitTo1_0_ > 1.0F ? 1.0F : p_limitTo1_0_);
    }

    public static boolean isAnimatedWater() {
        return gameSettings.ofAnimatedWater != 2;
    }

    public static boolean isGeneratedWater() {
        return gameSettings.ofAnimatedWater == 1;
    }

    public static boolean isAnimatedPortal() {
        return gameSettings.ofAnimatedPortal;
    }

    public static boolean isAnimatedLava() {
        return gameSettings.ofAnimatedLava != 2;
    }

    public static boolean isGeneratedLava() {
        return gameSettings.ofAnimatedLava == 1;
    }

    public static boolean isAnimatedFire() {
        return gameSettings.ofAnimatedFire;
    }

    public static boolean isAnimatedRedstone() {
        return gameSettings.ofAnimatedRedstone;
    }

    public static boolean isAnimatedExplosion() {
        return gameSettings.ofAnimatedExplosion;
    }

    public static boolean isAnimatedFlame() {
        return gameSettings.ofAnimatedFlame;
    }

    public static boolean isAnimatedSmoke() {
        return gameSettings.ofAnimatedSmoke;
    }

    public static boolean isVoidParticles() {
        return gameSettings.ofVoidParticles;
    }

    public static boolean isWaterParticles() {
        return gameSettings.ofWaterParticles;
    }

    public static boolean isRainSplash() {
        return gameSettings.ofRainSplash;
    }

    public static boolean isPortalParticles() {
        return gameSettings.ofPortalParticles;
    }

    public static boolean isPotionParticles() {
        return gameSettings.ofPotionParticles;
    }

    public static boolean isFireworkParticles() {
        return gameSettings.ofFireworkParticles;
    }

    public static float getAmbientOcclusionLevel() {
        return isShaders() && Shaders.aoLevel >= 0.0F ? Shaders.aoLevel : gameSettings.ofAoLevel;
    }

    public static String listToString(final List p_listToString_0_) {
        return listToString(p_listToString_0_, ", ");
    }

    public static String listToString(final List p_listToString_0_, final String p_listToString_1_) {
        if (p_listToString_0_ == null) {
            return "";
        } else {
            final StringBuffer stringbuffer = new StringBuffer(p_listToString_0_.size() * 5);

            for (int i = 0; i < p_listToString_0_.size(); ++i) {
                final Object object = p_listToString_0_.get(i);

                if (i > 0) {
                    stringbuffer.append(p_listToString_1_);
                }

                stringbuffer.append(object);
            }

            return stringbuffer.toString();
        }
    }

    public static String arrayToString(final Object[] p_arrayToString_0_) {
        return arrayToString(p_arrayToString_0_, ", ");
    }

    public static String arrayToString(final Object[] p_arrayToString_0_, final String p_arrayToString_1_) {
        if (p_arrayToString_0_ == null) {
            return "";
        } else {
            final StringBuffer stringbuffer = new StringBuffer(p_arrayToString_0_.length * 5);

            for (int i = 0; i < p_arrayToString_0_.length; ++i) {
                final Object object = p_arrayToString_0_[i];

                if (i > 0) {
                    stringbuffer.append(p_arrayToString_1_);
                }

                stringbuffer.append(object);
            }

            return stringbuffer.toString();
        }
    }

    public static String arrayToString(final int[] p_arrayToString_0_) {
        return arrayToString(p_arrayToString_0_, ", ");
    }

    public static String arrayToString(final int[] p_arrayToString_0_, final String p_arrayToString_1_) {
        if (p_arrayToString_0_ == null) {
            return "";
        } else {
            final StringBuffer stringbuffer = new StringBuffer(p_arrayToString_0_.length * 5);

            for (int i = 0; i < p_arrayToString_0_.length; ++i) {
                final int j = p_arrayToString_0_[i];

                if (i > 0) {
                    stringbuffer.append(p_arrayToString_1_);
                }

                stringbuffer.append(j);
            }

            return stringbuffer.toString();
        }
    }

    public static String arrayToString(final float[] p_arrayToString_0_) {
        return arrayToString(p_arrayToString_0_, ", ");
    }

    public static String arrayToString(final float[] p_arrayToString_0_, final String p_arrayToString_1_) {
        if (p_arrayToString_0_ == null) {
            return "";
        } else {
            final StringBuffer stringbuffer = new StringBuffer(p_arrayToString_0_.length * 5);

            for (int i = 0; i < p_arrayToString_0_.length; ++i) {
                final float f = p_arrayToString_0_[i];

                if (i > 0) {
                    stringbuffer.append(p_arrayToString_1_);
                }

                stringbuffer.append(f);
            }

            return stringbuffer.toString();
        }
    }

    public static Minecraft getMinecraft() {
        return minecraft;
    }

    public static TextureManager getTextureManager() {
        return minecraft.getTextureManager();
    }

    public static IResourceManager getResourceManager() {
        return minecraft.getResourceManager();
    }

    public static InputStream getResourceStream(final ResourceLocation p_getResourceStream_0_) throws IOException {
        return getResourceStream(minecraft.getResourceManager(), p_getResourceStream_0_);
    }

    public static InputStream getResourceStream(final IResourceManager p_getResourceStream_0_, final ResourceLocation p_getResourceStream_1_) throws IOException {
        final IResource iresource = p_getResourceStream_0_.getResource(p_getResourceStream_1_);
        return iresource == null ? null : iresource.getInputStream();
    }

    public static IResource getResource(final ResourceLocation p_getResource_0_) throws IOException {
        return minecraft.getResourceManager().getResource(p_getResource_0_);
    }

    public static boolean hasResource(final ResourceLocation p_hasResource_0_) {
        if (p_hasResource_0_ == null) {
            return false;
        } else {
            final IResourcePack iresourcepack = getDefiningResourcePack(p_hasResource_0_);
            return iresourcepack != null;
        }
    }

    public static boolean hasResource(final IResourceManager p_hasResource_0_, final ResourceLocation p_hasResource_1_) {
        try {
            final IResource iresource = p_hasResource_0_.getResource(p_hasResource_1_);
            return iresource != null;
        } catch (final IOException var3) {
            return false;
        }
    }

    public static IResourcePack[] getResourcePacks() {
        final ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();
        final List list = resourcepackrepository.getRepositoryEntries();
        final List list1 = new ArrayList();

        for (final Object e : list) {
            final ResourcePackRepository.Entry resourcepackrepository$entry = (Entry) e;
            list1.add(resourcepackrepository$entry.getResourcePack());
        }

        if (resourcepackrepository.getResourcePackInstance() != null) {
            list1.add(resourcepackrepository.getResourcePackInstance());
        }

        final IResourcePack[] airesourcepack = (IResourcePack[]) list1.toArray(new IResourcePack[list1.size()]);
        return airesourcepack;
    }

    public static String getResourcePackNames() {
        if (minecraft.getResourcePackRepository() == null) {
            return "";
        } else {
            final IResourcePack[] airesourcepack = getResourcePacks();

            if (airesourcepack.length <= 0) {
                return getDefaultResourcePack().getPackName();
            } else {
                final String[] astring = new String[airesourcepack.length];

                for (int i = 0; i < airesourcepack.length; ++i) {
                    astring[i] = airesourcepack[i].getPackName();
                }

                final String s = arrayToString(astring);
                return s;
            }
        }
    }

    public static DefaultResourcePack getDefaultResourcePack() {
        if (defaultResourcePackLazy == null) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            defaultResourcePackLazy = (DefaultResourcePack) Reflector.getFieldValue(minecraft, Reflector.Minecraft_defaultResourcePack);

            if (defaultResourcePackLazy == null) {
                final ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();

                if (resourcepackrepository != null) {
                    defaultResourcePackLazy = (DefaultResourcePack) resourcepackrepository.rprDefaultResourcePack;
                }
            }
        }

        return defaultResourcePackLazy;
    }

    public static boolean isFromDefaultResourcePack(final ResourceLocation p_isFromDefaultResourcePack_0_) {
        final IResourcePack iresourcepack = getDefiningResourcePack(p_isFromDefaultResourcePack_0_);
        return iresourcepack == getDefaultResourcePack();
    }

    public static IResourcePack getDefiningResourcePack(final ResourceLocation p_getDefiningResourcePack_0_) {
        final ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();
        final IResourcePack iresourcepack = resourcepackrepository.getResourcePackInstance();

        if (iresourcepack != null && iresourcepack.resourceExists(p_getDefiningResourcePack_0_)) {
            return iresourcepack;
        } else {
            final List<ResourcePackRepository.Entry> list = resourcepackrepository.repositoryEntries;

            for (int i = list.size() - 1; i >= 0; --i) {
                final ResourcePackRepository.Entry resourcepackrepository$entry = list.get(i);
                final IResourcePack iresourcepack1 = resourcepackrepository$entry.getResourcePack();

                if (iresourcepack1.resourceExists(p_getDefiningResourcePack_0_)) {
                    return iresourcepack1;
                }
            }

            if (getDefaultResourcePack().resourceExists(p_getDefiningResourcePack_0_)) {
                return getDefaultResourcePack();
            } else {
                return null;
            }
        }
    }

    public static RenderGlobal getRenderGlobal() {
        return minecraft.renderGlobal;
    }

    public static boolean isBetterGrass() {
        return gameSettings.ofBetterGrass != 3;
    }

    public static boolean isBetterGrassFancy() {
        return gameSettings.ofBetterGrass == 2;
    }

    public static boolean isWeatherEnabled() {
        return gameSettings.ofWeather;
    }

    public static boolean isSkyEnabled() {
        return gameSettings.ofSky;
    }

    public static boolean isSunMoonEnabled() {
        return gameSettings.ofSunMoon;
    }

    public static boolean isSunTexture() {
        return isSunMoonEnabled() && (!isShaders() || Shaders.isSun());
    }

    public static boolean isMoonTexture() {
        return isSunMoonEnabled() && (!isShaders() || Shaders.isMoon());
    }

    public static boolean isVignetteEnabled() {
        return (!isShaders() || Shaders.isVignette()) && (gameSettings.ofVignette == 0 ? gameSettings.fancyGraphics : gameSettings.ofVignette == 2);
    }

    public static boolean isStarsEnabled() {
        return gameSettings.ofStars;
    }

    public static void sleep(final long p_sleep_0_) {
        try {
            Thread.sleep(p_sleep_0_);
        } catch (final InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
        }
    }

    public static boolean isTimeDayOnly() {
        return gameSettings.ofTime == 1;
    }

    public static boolean isTimeDefault() {
        return gameSettings.ofTime == 0;
    }

    public static boolean isTimeNightOnly() {
        return gameSettings.ofTime == 2;
    }

    public static boolean isClearWater() {
        return gameSettings.ofClearWater;
    }

    public static int getAnisotropicFilterLevel() {
        return gameSettings.ofAfLevel;
    }

    public static boolean isAnisotropicFiltering() {
        return getAnisotropicFilterLevel() > 1;
    }

    public static int getAntialiasingLevel() {
        return antialiasingLevel;
    }

    public static boolean isAntialiasing() {
        return getAntialiasingLevel() > 0;
    }

    public static boolean isAntialiasingConfigured() {
        return getGameSettings().ofAaLevel > 0;
    }

    public static boolean isMultiTexture() {
        return getAnisotropicFilterLevel() > 1 || getAntialiasingLevel() > 0;
    }

    public static boolean between(final int p_between_0_, final int p_between_1_, final int p_between_2_) {
        return p_between_0_ >= p_between_1_ && p_between_0_ <= p_between_2_;
    }

    public static boolean between(final float p_between_0_, final float p_between_1_, final float p_between_2_) {
        return p_between_0_ >= p_between_1_ && p_between_0_ <= p_between_2_;
    }

    public static boolean isDrippingWaterLava() {
        return gameSettings.ofDrippingWaterLava;
    }

    public static boolean isBetterSnow() {
        return gameSettings.ofBetterSnow;
    }

    public static Dimension getFullscreenDimension() {
        if (desktopDisplayMode == null) {
            return null;
        } else if (gameSettings == null) {
            return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
        } else {
            final String s = gameSettings.ofFullscreenMode;

            if (s.equals("Default")) {
                return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
            } else {
                final String[] astring = tokenize(s, " x");
                return astring.length < 2 ? new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight()) : new Dimension(parseInt(astring[0], -1), parseInt(astring[1], -1));
            }
        }
    }

    public static int parseInt(String p_parseInt_0_, final int p_parseInt_1_) {
        try {
            if (p_parseInt_0_ == null) {
                return p_parseInt_1_;
            } else {
                p_parseInt_0_ = p_parseInt_0_.trim();
                return Integer.parseInt(p_parseInt_0_);
            }
        } catch (final NumberFormatException var3) {
            return p_parseInt_1_;
        }
    }

    public static float parseFloat(String p_parseFloat_0_, final float p_parseFloat_1_) {
        try {
            if (p_parseFloat_0_ == null) {
                return p_parseFloat_1_;
            } else {
                p_parseFloat_0_ = p_parseFloat_0_.trim();
                return Float.parseFloat(p_parseFloat_0_);
            }
        } catch (final NumberFormatException var3) {
            return p_parseFloat_1_;
        }
    }

    public static boolean parseBoolean(String p_parseBoolean_0_, final boolean p_parseBoolean_1_) {
        try {
            if (p_parseBoolean_0_ == null) {
                return p_parseBoolean_1_;
            } else {
                p_parseBoolean_0_ = p_parseBoolean_0_.trim();
                return Boolean.parseBoolean(p_parseBoolean_0_);
            }
        } catch (final NumberFormatException var3) {
            return p_parseBoolean_1_;
        }
    }

    public static Boolean parseBoolean(String p_parseBoolean_0_, final Boolean p_parseBoolean_1_) {
        try {
            if (p_parseBoolean_0_ == null) {
                return p_parseBoolean_1_;
            } else {
                p_parseBoolean_0_ = p_parseBoolean_0_.trim().toLowerCase();
                return p_parseBoolean_0_.equals("true") ? Boolean.TRUE : (p_parseBoolean_0_.equals("false") ? Boolean.FALSE : p_parseBoolean_1_);
            }
        } catch (final NumberFormatException var3) {
            return p_parseBoolean_1_;
        }
    }

    public static String[] tokenize(final String p_tokenize_0_, final String p_tokenize_1_) {
        final StringTokenizer stringtokenizer = new StringTokenizer(p_tokenize_0_, p_tokenize_1_);
        final List list = new ArrayList();

        while (stringtokenizer.hasMoreTokens()) {
            final String s = stringtokenizer.nextToken();
            list.add(s);
        }

        final String[] astring = (String[]) list.toArray(new String[list.size()]);
        return astring;
    }

    public static DisplayMode getDesktopDisplayMode() {
        return desktopDisplayMode;
    }

    public static DisplayMode[] getDisplayModes() {
        if (displayModes == null) {
            try {
                final DisplayMode[] adisplaymode = Display.getAvailableDisplayModes();
                final Set<Dimension> set = getDisplayModeDimensions(adisplaymode);
                final List list = new ArrayList();

                for (final Dimension dimension : set) {
                    final DisplayMode[] adisplaymode1 = getDisplayModes(adisplaymode, dimension);
                    final DisplayMode displaymode = getDisplayMode(adisplaymode1, desktopDisplayMode);

                    if (displaymode != null) {
                        list.add(displaymode);
                    }
                }

                final DisplayMode[] adisplaymode2 = (DisplayMode[]) list.toArray(new DisplayMode[list.size()]);
                Arrays.sort(adisplaymode2, new DisplayModeComparator());
                return adisplaymode2;
            } catch (final Exception exception) {
                exception.printStackTrace();
                displayModes = new DisplayMode[]{desktopDisplayMode};
            }
        }

        return displayModes;
    }

    public static DisplayMode getLargestDisplayMode() {
        final DisplayMode[] adisplaymode = getDisplayModes();

        if (adisplaymode != null && adisplaymode.length >= 1) {
            final DisplayMode displaymode = adisplaymode[adisplaymode.length - 1];
            return desktopDisplayMode.getWidth() > displaymode.getWidth() ? desktopDisplayMode : (desktopDisplayMode.getWidth() == displaymode.getWidth() && desktopDisplayMode.getHeight() > displaymode.getHeight() ? desktopDisplayMode : displaymode);
        } else {
            return desktopDisplayMode;
        }
    }

    private static Set<Dimension> getDisplayModeDimensions(final DisplayMode[] p_getDisplayModeDimensions_0_) {
        final Set<Dimension> set = new HashSet();

        for (int i = 0; i < p_getDisplayModeDimensions_0_.length; ++i) {
            final DisplayMode displaymode = p_getDisplayModeDimensions_0_[i];
            final Dimension dimension = new Dimension(displaymode.getWidth(), displaymode.getHeight());
            set.add(dimension);
        }

        return set;
    }

    private static DisplayMode[] getDisplayModes(final DisplayMode[] p_getDisplayModes_0_, final Dimension p_getDisplayModes_1_) {
        final List list = new ArrayList();

        for (int i = 0; i < p_getDisplayModes_0_.length; ++i) {
            final DisplayMode displaymode = p_getDisplayModes_0_[i];

            if ((double) displaymode.getWidth() == p_getDisplayModes_1_.getWidth() && (double) displaymode.getHeight() == p_getDisplayModes_1_.getHeight()) {
                list.add(displaymode);
            }
        }

        final DisplayMode[] adisplaymode = (DisplayMode[]) list.toArray(new DisplayMode[list.size()]);
        return adisplaymode;
    }

    private static DisplayMode getDisplayMode(final DisplayMode[] p_getDisplayMode_0_, final DisplayMode p_getDisplayMode_1_) {
        if (p_getDisplayMode_1_ != null) {
            for (int i = 0; i < p_getDisplayMode_0_.length; ++i) {
                final DisplayMode displaymode = p_getDisplayMode_0_[i];

                if (displaymode.getBitsPerPixel() == p_getDisplayMode_1_.getBitsPerPixel() && displaymode.getFrequency() == p_getDisplayMode_1_.getFrequency()) {
                    return displaymode;
                }
            }
        }

        if (p_getDisplayMode_0_.length <= 0) {
            return null;
        } else {
            Arrays.sort(p_getDisplayMode_0_, new DisplayModeComparator());
            return p_getDisplayMode_0_[p_getDisplayMode_0_.length - 1];
        }
    }

    public static String[] getDisplayModeNames() {
        final DisplayMode[] adisplaymode = getDisplayModes();
        final String[] astring = new String[adisplaymode.length];

        for (int i = 0; i < adisplaymode.length; ++i) {
            final DisplayMode displaymode = adisplaymode[i];
            final String s = "" + displaymode.getWidth() + "x" + displaymode.getHeight();
            astring[i] = s;
        }

        return astring;
    }

    public static DisplayMode getDisplayMode(final Dimension p_getDisplayMode_0_) throws LWJGLException {
        final DisplayMode[] adisplaymode = getDisplayModes();

        for (int i = 0; i < adisplaymode.length; ++i) {
            final DisplayMode displaymode = adisplaymode[i];

            if (displaymode.getWidth() == p_getDisplayMode_0_.width && displaymode.getHeight() == p_getDisplayMode_0_.height) {
                return displaymode;
            }
        }

        return desktopDisplayMode;
    }

    public static boolean isAnimatedTerrain() {
        return gameSettings.ofAnimatedTerrain;
    }

    public static boolean isAnimatedTextures() {
        return gameSettings.ofAnimatedTextures;
    }

    public static boolean isSwampColors() {
        return gameSettings.ofSwampColors;
    }

    public static boolean isRandomEntities() {
        return gameSettings.ofRandomEntities;
    }

    public static void checkGlError(final String p_checkGlError_0_) {
//        final int i = GlStateManager.glGetError();
//
//        if (i != 0 && GlErrors.isEnabled(i)) {
//            final String s = getGlErrorString(i);
//            final String s1 = String.format("OpenGL error: %s (%s), at: %s", Integer.valueOf(i), s, p_checkGlError_0_);
//            error(s1);
//
//            if (isShowGlErrors() && TimedEvent.isActive("ShowGlError", 10000L)) {
//                final String s2 = I18n.format("of.message.openglError", Integer.valueOf(i), s);
//                minecraft.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(s2));
//            }
//        }
    }

    public static boolean isSmoothBiomes() {
        return gameSettings.ofSmoothBiomes;
    }

    public static boolean isCustomColors() {
        return gameSettings.ofCustomColors;
    }

    public static boolean isCustomSky() {
        return gameSettings.ofCustomSky;
    }

    public static boolean isCustomFonts() {
        return gameSettings.ofCustomFonts;
    }

    public static boolean isShowCapes() {
        return gameSettings.ofShowCapes;
    }

    public static boolean isConnectedTextures() {
        return gameSettings.ofConnectedTextures != 3;
    }

    public static boolean isNaturalTextures() {
        return gameSettings.ofNaturalTextures;
    }

    public static boolean isEmissiveTextures() {
        return gameSettings.ofEmissiveTextures;
    }

    public static boolean isConnectedTexturesFancy() {
        return gameSettings.ofConnectedTextures == 2;
    }

    public static boolean isFastRender() {
        return gameSettings.ofFastRender;
    }

    public static boolean isTranslucentBlocksFancy() {
        return gameSettings.ofTranslucentBlocks == 0 ? gameSettings.fancyGraphics : gameSettings.ofTranslucentBlocks == 2;
    }

    public static boolean isShaders() {
        return Shaders.shaderPackLoaded;
    }

    public static String[] readLines(final File p_readLines_0_) throws IOException {
        final FileInputStream fileinputstream = new FileInputStream(p_readLines_0_);
        return readLines(fileinputstream);
    }

    public static String[] readLines(final InputStream p_readLines_0_) throws IOException {
        final List list = new ArrayList();
        final InputStreamReader inputstreamreader = new InputStreamReader(p_readLines_0_, StandardCharsets.US_ASCII);
        final BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

        while (true) {
            final String s = bufferedreader.readLine();

            if (s == null) {
                final String[] astring = (String[]) list.toArray(new String[list.size()]);
                return astring;
            }

            list.add(s);
        }
    }

    public static String readFile(final File p_readFile_0_) throws IOException {
        final FileInputStream fileinputstream = new FileInputStream(p_readFile_0_);
        return readInputStream(fileinputstream, "ASCII");
    }

    public static String readInputStream(final InputStream p_readInputStream_0_) throws IOException {
        return readInputStream(p_readInputStream_0_, "ASCII");
    }

    public static String readInputStream(final InputStream p_readInputStream_0_, final String p_readInputStream_1_) throws IOException {
        final InputStreamReader inputstreamreader = new InputStreamReader(p_readInputStream_0_, p_readInputStream_1_);
        final BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        final StringBuffer stringbuffer = new StringBuffer();

        while (true) {
            final String s = bufferedreader.readLine();

            if (s == null) {
                return stringbuffer.toString();
            }

            stringbuffer.append(s);
            stringbuffer.append("\n");
        }
    }

    public static byte[] readAll(final InputStream p_readAll_0_) throws IOException {
        final ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        final byte[] abyte = new byte[1024];

        while (true) {
            final int i = p_readAll_0_.read(abyte);

            if (i < 0) {
                p_readAll_0_.close();
                final byte[] abyte1 = bytearrayoutputstream.toByteArray();
                return abyte1;
            }

            bytearrayoutputstream.write(abyte, 0, i);
        }
    }

    public static GameSettings getGameSettings() {
        return gameSettings;
    }

    public static String getNewRelease() {
        return newRelease;
    }

    public static void setNewRelease(final String p_setNewRelease_0_) {
        newRelease = p_setNewRelease_0_;
    }

    public static int compareRelease(final String p_compareRelease_0_, final String p_compareRelease_1_) {
        final String[] astring = splitRelease(p_compareRelease_0_);
        final String[] astring1 = splitRelease(p_compareRelease_1_);
        final String s = astring[0];
        final String s1 = astring1[0];

        if (!s.equals(s1)) {
            return s.compareTo(s1);
        } else {
            final int i = parseInt(astring[1], -1);
            final int j = parseInt(astring1[1], -1);

            if (i != j) {
                return i - j;
            } else {
                final String s2 = astring[2];
                final String s3 = astring1[2];

                if (!s2.equals(s3)) {
                    if (s2.isEmpty()) {
                        return 1;
                    }

                    if (s3.isEmpty()) {
                        return -1;
                    }
                }

                return s2.compareTo(s3);
            }
        }
    }

    private static String[] splitRelease(final String p_splitRelease_0_) {
        if (p_splitRelease_0_ != null && p_splitRelease_0_.length() > 0) {
            final Pattern pattern = Pattern.compile("([A-Z])([0-9]+)(.*)");
            final Matcher matcher = pattern.matcher(p_splitRelease_0_);

            if (!matcher.matches()) {
                return new String[]{"", "", ""};
            } else {
                final String s = normalize(matcher.group(1));
                final String s1 = normalize(matcher.group(2));
                final String s2 = normalize(matcher.group(3));
                return new String[]{s, s1, s2};
            }
        } else {
            return new String[]{"", "", ""};
        }
    }

    public static int intHash(int p_intHash_0_) {
        p_intHash_0_ = p_intHash_0_ ^ 61 ^ p_intHash_0_ >> 16;
        p_intHash_0_ = p_intHash_0_ + (p_intHash_0_ << 3);
        p_intHash_0_ = p_intHash_0_ ^ p_intHash_0_ >> 4;
        p_intHash_0_ = p_intHash_0_ * 668265261;
        p_intHash_0_ = p_intHash_0_ ^ p_intHash_0_ >> 15;
        return p_intHash_0_;
    }

    public static int getRandom(final BlockPos p_getRandom_0_, final int p_getRandom_1_) {
        int i = intHash(p_getRandom_1_ + 37);
        i = intHash(i + p_getRandom_0_.getX());
        i = intHash(i + p_getRandom_0_.getZ());
        i = intHash(i + p_getRandom_0_.getY());
        return i;
    }

    public static int getAvailableProcessors() {
        return availableProcessors;
    }

    public static void updateAvailableProcessors() {
        availableProcessors = Runtime.getRuntime().availableProcessors();
    }

    public static boolean isSingleProcessor() {
        return getAvailableProcessors() <= 1;
    }

    public static boolean isSmoothWorld() {
        return gameSettings.ofSmoothWorld;
    }

    public static boolean isLazyChunkLoading() {
        return gameSettings.ofLazyChunkLoading;
    }

    public static boolean isDynamicFov() {
        return gameSettings.ofDynamicFov;
    }

    public static boolean isAlternateBlocks() {
        return gameSettings.ofAlternateBlocks;
    }

    public static int getChunkViewDistance() {
        if (gameSettings == null) {
            return 10;
        } else {
            final int i = gameSettings.renderDistanceChunks;
            return i;
        }
    }

    public static boolean equals(final Object p_equals_0_, final Object p_equals_1_) {
        return p_equals_0_ == p_equals_1_ || (p_equals_0_ != null && p_equals_0_.equals(p_equals_1_));
    }

    public static boolean equalsOne(final Object p_equalsOne_0_, final Object[] p_equalsOne_1_) {
        if (p_equalsOne_1_ == null) {
            return false;
        } else {
            for (int i = 0; i < p_equalsOne_1_.length; ++i) {
                final Object object = p_equalsOne_1_[i];

                if (equals(p_equalsOne_0_, object)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean equalsOne(final int p_equalsOne_0_, final int[] p_equalsOne_1_) {
        for (int i = 0; i < p_equalsOne_1_.length; ++i) {
            if (p_equalsOne_1_[i] == p_equalsOne_0_) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSameOne(final Object p_isSameOne_0_, final Object[] p_isSameOne_1_) {
        if (p_isSameOne_1_ == null) {
            return false;
        } else {
            for (int i = 0; i < p_isSameOne_1_.length; ++i) {
                final Object object = p_isSameOne_1_[i];

                if (p_isSameOne_0_ == object) {
                    return true;
                }
            }

            return false;
        }
    }

    public static String normalize(final String p_normalize_0_) {
        return p_normalize_0_ == null ? "" : p_normalize_0_;
    }

    public static void checkDisplaySettings() {
        final int i = getAntialiasingLevel();

        if (i > 0) {
            final DisplayMode displaymode = Display.getDisplayMode();
            dbg("FSAA Samples: " + i);

            try {
                Display.destroy();
                Display.setDisplayMode(displaymode);
                Display.create((new PixelFormat()).withDepthBits(24).withSamples(i));
                Display.setResizable(false);
                Display.setResizable(true);
            } catch (final LWJGLException lwjglexception2) {
                warn("Error setting FSAA: " + i + "x");
                lwjglexception2.printStackTrace();

                try {
                    Display.setDisplayMode(displaymode);
                    Display.create((new PixelFormat()).withDepthBits(24));
                    Display.setResizable(false);
                    Display.setResizable(true);
                } catch (final LWJGLException lwjglexception1) {
                    lwjglexception1.printStackTrace();

                    try {
                        Display.setDisplayMode(displaymode);
                        Display.create();
                        Display.setResizable(false);
                        Display.setResizable(true);
                    } catch (final LWJGLException lwjglexception) {
                        lwjglexception.printStackTrace();
                    }
                }
            }

            if (!Minecraft.isRunningOnMac && getDefaultResourcePack() != null) {
                InputStream inputstream = null;
                InputStream inputstream1 = null;

                try {
                    inputstream = getDefaultResourcePack().getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
                    inputstream1 = getDefaultResourcePack().getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));

                    if (inputstream != null && inputstream1 != null) {
                        Display.setIcon(new ByteBuffer[]{readIconImage(inputstream), readIconImage(inputstream1)});
                    }
                } catch (final IOException ioexception) {
                    warn("Error setting window icon: " + ioexception.getClass().getName() + ": " + ioexception.getMessage());
                } finally {
                    IOUtils.closeQuietly(inputstream);
                    IOUtils.closeQuietly(inputstream1);
                }
            }
        }
    }

    private static ByteBuffer readIconImage(final InputStream p_readIconImage_0_) throws IOException {
        final BufferedImage bufferedimage = ImageIO.read(p_readIconImage_0_);
        final int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
        final ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);

        for (final int i : aint) {
            bytebuffer.putInt(i << 8 | i >> 24 & 255);
        }

        bytebuffer.flip();
        return bytebuffer;
    }

    public static void checkDisplayMode() {
        try {
            if (minecraft.isFullScreen()) {
                if (fullscreenModeChecked) {
                    return;
                }

                fullscreenModeChecked = true;
                desktopModeChecked = false;
                final DisplayMode displaymode = Display.getDisplayMode();
                final Dimension dimension = getFullscreenDimension();

                if (dimension == null) {
                    return;
                }

                if (displaymode.getWidth() == dimension.width && displaymode.getHeight() == dimension.height) {
                    return;
                }

                final DisplayMode displaymode1 = getDisplayMode(dimension);

                if (displaymode1 == null) {
                    return;
                }

                Display.setDisplayMode(displaymode1);
                minecraft.displayWidth = Display.getDisplayMode().getWidth();
                minecraft.displayHeight = Display.getDisplayMode().getHeight();

                if (minecraft.displayWidth <= 0) {
                    minecraft.displayWidth = 1;
                }

                if (minecraft.displayHeight <= 0) {
                    minecraft.displayHeight = 1;
                }

                if (minecraft.currentScreen != null) {
                    final ScaledResolution scaledresolution = new ScaledResolution(minecraft);
                    final int i = scaledresolution.getScaledWidth();
                    final int j = scaledresolution.getScaledHeight();
                    minecraft.currentScreen.setWorldAndResolution(minecraft, i, j);
                }

                updateFramebufferSize();
                Display.setFullscreen(true);
                minecraft.gameSettings.updateVSync();
                GlStateManager.enableTexture2D();
            } else {
                if (desktopModeChecked) {
                    return;
                }

                desktopModeChecked = true;
                fullscreenModeChecked = false;
                minecraft.gameSettings.updateVSync();
                Display.update();
                GlStateManager.enableTexture2D();
                Display.setResizable(false);
                Display.setResizable(true);
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
            gameSettings.ofFullscreenMode = "Default";
            gameSettings.saveOfOptions();
        }
    }

    public static void updateFramebufferSize() {
        minecraft.getFramebuffer().createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight);

        if (minecraft.entityRenderer != null) {
            minecraft.entityRenderer.updateShaderGroupSize(minecraft.displayWidth, minecraft.displayHeight);
        }

        minecraft.loadingScreen = new LoadingScreenRenderer(minecraft);
    }

    public static Object[] addObjectToArray(final Object[] p_addObjectToArray_0_, final Object p_addObjectToArray_1_) {
        if (p_addObjectToArray_0_ == null) {
            throw new NullPointerException("The given array is NULL");
        } else {
            final int i = p_addObjectToArray_0_.length;
            final int j = i + 1;
            final Object[] aobject = (Object[]) Array.newInstance(p_addObjectToArray_0_.getClass().getComponentType(), j);
            System.arraycopy(p_addObjectToArray_0_, 0, aobject, 0, i);
            aobject[i] = p_addObjectToArray_1_;
            return aobject;
        }
    }

    public static Object[] addObjectToArray(final Object[] p_addObjectToArray_0_, final Object p_addObjectToArray_1_, final int p_addObjectToArray_2_) {
        final List list = new ArrayList(Arrays.asList(p_addObjectToArray_0_));
        list.add(p_addObjectToArray_2_, p_addObjectToArray_1_);
        final Object[] aobject = (Object[]) Array.newInstance(p_addObjectToArray_0_.getClass().getComponentType(), list.size());
        return list.toArray(aobject);
    }

    public static Object[] addObjectsToArray(final Object[] p_addObjectsToArray_0_, final Object[] p_addObjectsToArray_1_) {
        if (p_addObjectsToArray_0_ == null) {
            throw new NullPointerException("The given array is NULL");
        } else if (p_addObjectsToArray_1_.length == 0) {
            return p_addObjectsToArray_0_;
        } else {
            final int i = p_addObjectsToArray_0_.length;
            final int j = i + p_addObjectsToArray_1_.length;
            final Object[] aobject = (Object[]) Array.newInstance(p_addObjectsToArray_0_.getClass().getComponentType(), j);
            System.arraycopy(p_addObjectsToArray_0_, 0, aobject, 0, i);
            System.arraycopy(p_addObjectsToArray_1_, 0, aobject, i, p_addObjectsToArray_1_.length);
            return aobject;
        }
    }

    public static Object[] removeObjectFromArray(final Object[] p_removeObjectFromArray_0_, final Object p_removeObjectFromArray_1_) {
        final List list = new ArrayList(Arrays.asList(p_removeObjectFromArray_0_));
        list.remove(p_removeObjectFromArray_1_);
        final Object[] aobject = collectionToArray(list, p_removeObjectFromArray_0_.getClass().getComponentType());
        return aobject;
    }

    public static Object[] collectionToArray(final Collection p_collectionToArray_0_, final Class p_collectionToArray_1_) {
        if (p_collectionToArray_0_ == null) {
            return null;
        } else if (p_collectionToArray_1_ == null) {
            return null;
        } else if (p_collectionToArray_1_.isPrimitive()) {
            throw new IllegalArgumentException("Can not make arrays with primitive elements (int, double), element class: " + p_collectionToArray_1_);
        } else {
            final Object[] aobject = (Object[]) Array.newInstance(p_collectionToArray_1_, p_collectionToArray_0_.size());
            return p_collectionToArray_0_.toArray(aobject);
        }
    }

    public static boolean isCustomItems() {
        return gameSettings.ofCustomItems;
    }

    public static void drawFps() {
        final int i = Minecraft.getDebugFPS();
        final String s = getUpdates(minecraft.debug);
        final int j = minecraft.renderGlobal.getCountActiveRenderers();
        final int k = minecraft.renderGlobal.getCountEntitiesRendered();
        final int l = minecraft.renderGlobal.getCountTileEntitiesRendered();
        final String s1 = "" + i + "/" + getFpsMin() + " fps, C: " + j + ", E: " + k + "+" + l + ", U: " + s;
        minecraft.fontRendererObj.drawString(s1, 2, 2, -2039584);
    }

    public static int getFpsMin() {
        if (minecraft.debug == mcDebugLast) {
            return fpsMinLast;
        } else {
            mcDebugLast = minecraft.debug;
            final FrameTimer frametimer = minecraft.func_181539_aj();
            final long[] along = frametimer.func_181746_c();
            final int i = frametimer.func_181750_b();
            final int j = frametimer.func_181749_a();

            if (i == j) {
                return fpsMinLast;
            } else {
                int k = Minecraft.getDebugFPS();

                if (k <= 0) {
                    k = 1;
                }

                final long l = (long) (1.0D / (double) k * 1.0E9D);
                long i1 = l;
                long j1 = 0L;

                for (int k1 = MathHelper.normalizeAngle(i - 1, along.length); k1 != j && (double) j1 < 1.0E9D; k1 = MathHelper.normalizeAngle(k1 - 1, along.length)) {
                    final long l1 = along[k1];

                    if (l1 > i1) {
                        i1 = l1;
                    }

                    j1 += l1;
                }

                final double d0 = (double) i1 / 1.0E9D;
                fpsMinLast = (int) (1.0D / d0);
                return fpsMinLast;
            }
        }
    }

    private static String getUpdates(final String p_getUpdates_0_) {
        final int i = p_getUpdates_0_.indexOf(40);

        if (i < 0) {
            return "";
        } else {
            final int j = p_getUpdates_0_.indexOf(32, i);
            return j < 0 ? "" : p_getUpdates_0_.substring(i + 1, j);
        }
    }

    public static int getBitsOs() {
        final String s = System.getenv("ProgramFiles(X86)");
        return s != null ? 64 : 32;
    }

    public static int getBitsJre() {
        final String[] astring = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

        for (int i = 0; i < astring.length; ++i) {
            final String s = astring[i];
            final String s1 = System.getProperty(s);

            if (s1 != null && s1.contains("64")) {
                return 64;
            }
        }

        return 32;
    }

    public static boolean isNotify64BitJava() {
        return notify64BitJava;
    }

    public static void setNotify64BitJava(final boolean p_setNotify64BitJava_0_) {
        notify64BitJava = p_setNotify64BitJava_0_;
    }

    public static boolean isConnectedModels() {
        return false;
    }

    public static void showGuiMessage(final String p_showGuiMessage_0_, final String p_showGuiMessage_1_) {
        final GuiMessage guimessage = new GuiMessage(minecraft.currentScreen, p_showGuiMessage_0_, p_showGuiMessage_1_);
        minecraft.displayGuiScreen(guimessage);
    }

    public static int[] addIntToArray(final int[] p_addIntToArray_0_, final int p_addIntToArray_1_) {
        return addIntsToArray(p_addIntToArray_0_, new int[]{p_addIntToArray_1_});
    }

    public static int[] addIntsToArray(final int[] p_addIntsToArray_0_, final int[] p_addIntsToArray_1_) {
        if (p_addIntsToArray_0_ != null && p_addIntsToArray_1_ != null) {
            final int i = p_addIntsToArray_0_.length;
            final int j = i + p_addIntsToArray_1_.length;
            final int[] aint = new int[j];
            System.arraycopy(p_addIntsToArray_0_, 0, aint, 0, i);

            for (int k = 0; k < p_addIntsToArray_1_.length; ++k) {
                aint[k + i] = p_addIntsToArray_1_[k];
            }

            return aint;
        } else {
            throw new NullPointerException("The given array is NULL");
        }
    }

    public static DynamicTexture getMojangLogoTexture(final DynamicTexture p_getMojangLogoTexture_0_) {
        try {
            final ResourceLocation resourcelocation = new ResourceLocation("textures/gui/title/mojang.png");
            final InputStream inputstream = getResourceStream(resourcelocation);

            if (inputstream == null) {
                return p_getMojangLogoTexture_0_;
            } else {
                final BufferedImage bufferedimage = ImageIO.read(inputstream);

                if (bufferedimage == null) {
                    return p_getMojangLogoTexture_0_;
                } else {
                    final DynamicTexture dynamictexture = new DynamicTexture(bufferedimage);
                    return dynamictexture;
                }
            }
        } catch (final Exception exception) {
            warn(exception.getClass().getName() + ": " + exception.getMessage());
            return p_getMojangLogoTexture_0_;
        }
    }

    public static void writeFile(final File p_writeFile_0_, final String p_writeFile_1_) throws IOException {
        final FileOutputStream fileoutputstream = new FileOutputStream(p_writeFile_0_);
        final byte[] abyte = p_writeFile_1_.getBytes(StandardCharsets.US_ASCII);
        fileoutputstream.write(abyte);
        fileoutputstream.close();
    }

    public static TextureMap getTextureMap() {
        return getMinecraft().getTextureMapBlocks();
    }

    public static boolean isDynamicLights() {
        return gameSettings.ofDynamicLights != 3;
    }

    public static boolean isDynamicLightsFast() {
        return gameSettings.ofDynamicLights == 1;
    }

    public static boolean isDynamicHandLight() {
        return isDynamicLights() && (!isShaders() || Shaders.isDynamicHandLight());
    }

    public static boolean isCustomEntityModels() {
        return gameSettings.ofCustomEntityModels;
    }

    public static boolean isCustomGuis() {
        return gameSettings.ofCustomGuis;
    }

    public static int getScreenshotSize() {
        return gameSettings.ofScreenshotSize;
    }

    public static int[] toPrimitive(final Integer[] p_toPrimitive_0_) {
        if (p_toPrimitive_0_ == null) {
            return null;
        } else if (p_toPrimitive_0_.length == 0) {
            return new int[0];
        } else {
            final int[] aint = new int[p_toPrimitive_0_.length];

            for (int i = 0; i < aint.length; ++i) {
                aint[i] = p_toPrimitive_0_[i].intValue();
            }

            return aint;
        }
    }

    public static boolean isRenderRegions() {
        return gameSettings.ofRenderRegions;
    }

    public static boolean isVbo() {
        return OpenGlHelper.useVbo();
    }

    public static boolean isSmoothFps() {
        return gameSettings.ofSmoothFps;
    }

    public static boolean openWebLink(final URI p_openWebLink_0_) {
        try {
            Desktop.getDesktop().browse(p_openWebLink_0_);
            return true;
        } catch (final Exception exception) {
            warn("Error opening link: " + p_openWebLink_0_);
            warn(exception.getClass().getName() + ": " + exception.getMessage());
            return false;
        }
    }

    public static boolean isShowGlErrors() {
        return gameSettings.ofShowGlErrors;
    }

    public static String arrayToString(final boolean[] p_arrayToString_0_, final String p_arrayToString_1_) {
        if (p_arrayToString_0_ == null) {
            return "";
        } else {
            final StringBuffer stringbuffer = new StringBuffer(p_arrayToString_0_.length * 5);

            for (int i = 0; i < p_arrayToString_0_.length; ++i) {
                final boolean flag = p_arrayToString_0_[i];

                if (i > 0) {
                    stringbuffer.append(p_arrayToString_1_);
                }

                stringbuffer.append(flag);
            }

            return stringbuffer.toString();
        }
    }

    public static boolean isIntegratedServerRunning() {
        return minecraft.getIntegratedServer() != null && minecraft.isIntegratedServerRunning();
    }

    public static IntBuffer createDirectIntBuffer(final int p_createDirectIntBuffer_0_) {
        return GLAllocation.createDirectByteBuffer(p_createDirectIntBuffer_0_ << 2).asIntBuffer();
    }

    public static String getGlErrorString(final int p_getGlErrorString_0_) {
        switch (p_getGlErrorString_0_) {
            case 0:
                return "No error";

            case 1280:
                return "Invalid enum";

            case 1281:
                return "Invalid value";

            case 1282:
                return "Invalid operation";

            case 1283:
                return "Stack overflow";

            case 1284:
                return "Stack underflow";

            case 1285:
                return "Out of memory";

            case 1286:
                return "Invalid framebuffer operation";

            default:
                return "Unknown";
        }
    }

    public static boolean isTrue(final Boolean p_isTrue_0_) {
        return p_isTrue_0_ != null && p_isTrue_0_.booleanValue();
    }

    public static boolean isQuadsToTriangles() {
        return isShaders() && !Shaders.canRenderQuads();
    }
}
