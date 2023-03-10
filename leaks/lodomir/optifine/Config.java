/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.LWJGLException
 *  org.lwjgl.Sys
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.DisplayMode
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GLContext
 *  org.lwjgl.opengl.PixelFormat
 *  org.lwjgl.util.glu.GLU
 */
package optifine;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import optifine.DisplayModeComparator;
import optifine.DynamicLights;
import optifine.GlVersion;
import optifine.GuiMessage;
import optifine.Reflector;
import optifine.ReflectorForge;
import optifine.TextureUtils;
import optifine.VersionCheckThread;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import shadersmod.client.Shaders;

public class Config {
    public static final String OF_NAME = "OptiFine";
    public static final String MC_VERSION = "1.8.8";
    public static final String OF_EDITION = "HD_U";
    public static final String OF_RELEASE = "H8";
    public static final String VERSION = "OptiFine_1.8.8_HD_U_H8";
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
    private static Minecraft minecraft = Minecraft.getMinecraft();
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
    public static final Float DEF_ALPHA_FUNC_LEVEL = Float.valueOf(0.1f);
    private static final Logger LOGGER = LogManager.getLogger();

    public static String getVersion() {
        return VERSION;
    }

    public static String getVersionDebug() {
        StringBuffer stringbuffer = new StringBuffer(32);
        if (Config.isDynamicLights()) {
            stringbuffer.append("DL: ");
            stringbuffer.append(String.valueOf(DynamicLights.getCount()));
            stringbuffer.append(", ");
        }
        stringbuffer.append(VERSION);
        String s = Shaders.getShaderPackName();
        if (s != null) {
            stringbuffer.append(", ");
            stringbuffer.append(s);
        }
        return stringbuffer.toString();
    }

    public static void initGameSettings(GameSettings p_initGameSettings_0_) {
        if (gameSettings == null) {
            gameSettings = p_initGameSettings_0_;
            desktopDisplayMode = Display.getDesktopDisplayMode();
            Config.updateAvailableProcessors();
            ReflectorForge.putLaunchBlackboard("optifine.ForgeSplashCompatible", Boolean.TRUE);
        }
    }

    public static void initDisplay() {
        Config.checkInitialized();
        antialiasingLevel = Config.gameSettings.ofAaLevel;
        Config.checkDisplaySettings();
        Config.checkDisplayMode();
        minecraftThread = Thread.currentThread();
        Config.updateThreadPriorities();
        Shaders.startup(Minecraft.getMinecraft());
    }

    public static void checkInitialized() {
        if (!initialized && Display.isCreated()) {
            initialized = true;
            Config.checkOpenGlCaps();
            Config.startVersionCheckThread();
        }
    }

    private static void checkOpenGlCaps() {
        Config.log("");
        Config.log(Config.getVersion());
        Config.log("Build: " + Config.getBuild());
        Config.log("OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        Config.log("Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        Config.log("VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        Config.log("LWJGL: " + Sys.getVersion());
        openGlVersion = GL11.glGetString((int)7938);
        openGlRenderer = GL11.glGetString((int)7937);
        openGlVendor = GL11.glGetString((int)7936);
        Config.log("OpenGL: " + openGlRenderer + ", version " + openGlVersion + ", " + openGlVendor);
        Config.log("OpenGL Version: " + Config.getOpenGlVersionString());
        if (!GLContext.getCapabilities().OpenGL12) {
            Config.log("OpenGL Mipmap levels: Not available (GL12.GL_TEXTURE_MAX_LEVEL)");
        }
        if (!(fancyFogAvailable = GLContext.getCapabilities().GL_NV_fog_distance)) {
            Config.log("OpenGL Fancy fog: Not available (GL_NV_fog_distance)");
        }
        if (!(occlusionAvailable = GLContext.getCapabilities().GL_ARB_occlusion_query)) {
            Config.log("OpenGL Occlussion culling: Not available (GL_ARB_occlusion_query)");
        }
        int i = TextureUtils.getGLMaximumTextureSize();
        Config.dbg("Maximum texture size: " + i + "x" + i);
    }

    private static String getBuild() {
        try {
            InputStream inputstream = Config.class.getResourceAsStream("/buildof.txt");
            if (inputstream == null) {
                return null;
            }
            String s = Config.readLines(inputstream)[0];
            return s;
        }
        catch (Exception exception) {
            Config.warn("" + exception.getClass().getName() + ": " + exception.getMessage());
            return null;
        }
    }

    public static boolean isFancyFogAvailable() {
        return fancyFogAvailable;
    }

    public static boolean isOcclusionAvailable() {
        return occlusionAvailable;
    }

    public static int getMinecraftVersionInt() {
        if (minecraftVersionInt < 0) {
            String[] astring = Config.tokenize(MC_VERSION, ".");
            int i = 0;
            if (astring.length > 0) {
                i += 10000 * Config.parseInt(astring[0], 0);
            }
            if (astring.length > 1) {
                i += 100 * Config.parseInt(astring[1], 0);
            }
            if (astring.length > 2) {
                i += 1 * Config.parseInt(astring[2], 0);
            }
            minecraftVersionInt = i;
        }
        return minecraftVersionInt;
    }

    public static String getOpenGlVersionString() {
        GlVersion glversion = Config.getGlVersion();
        String s = "" + glversion.getMajor() + "." + glversion.getMinor() + "." + glversion.getRelease();
        return s;
    }

    private static GlVersion getGlVersionLwjgl() {
        return GLContext.getCapabilities().OpenGL44 ? new GlVersion(4, 4) : (GLContext.getCapabilities().OpenGL43 ? new GlVersion(4, 3) : (GLContext.getCapabilities().OpenGL42 ? new GlVersion(4, 2) : (GLContext.getCapabilities().OpenGL41 ? new GlVersion(4, 1) : (GLContext.getCapabilities().OpenGL40 ? new GlVersion(4, 0) : (GLContext.getCapabilities().OpenGL33 ? new GlVersion(3, 3) : (GLContext.getCapabilities().OpenGL32 ? new GlVersion(3, 2) : (GLContext.getCapabilities().OpenGL31 ? new GlVersion(3, 1) : (GLContext.getCapabilities().OpenGL30 ? new GlVersion(3, 0) : (GLContext.getCapabilities().OpenGL21 ? new GlVersion(2, 1) : (GLContext.getCapabilities().OpenGL20 ? new GlVersion(2, 0) : (GLContext.getCapabilities().OpenGL15 ? new GlVersion(1, 5) : (GLContext.getCapabilities().OpenGL14 ? new GlVersion(1, 4) : (GLContext.getCapabilities().OpenGL13 ? new GlVersion(1, 3) : (GLContext.getCapabilities().OpenGL12 ? new GlVersion(1, 2) : (GLContext.getCapabilities().OpenGL11 ? new GlVersion(1, 1) : new GlVersion(1, 0))))))))))))))));
    }

    public static GlVersion getGlVersion() {
        if (glVersion == null) {
            String s = GL11.glGetString((int)7938);
            glVersion = Config.parseGlVersion(s, null);
            if (glVersion == null) {
                glVersion = Config.getGlVersionLwjgl();
            }
            if (glVersion == null) {
                glVersion = new GlVersion(1, 0);
            }
        }
        return glVersion;
    }

    public static GlVersion getGlslVersion() {
        String s;
        if (glslVersion == null && (glslVersion = Config.parseGlVersion(s = GL11.glGetString((int)35724), null)) == null) {
            glslVersion = new GlVersion(1, 10);
        }
        return glslVersion;
    }

    public static GlVersion parseGlVersion(String p_parseGlVersion_0_, GlVersion p_parseGlVersion_1_) {
        try {
            if (p_parseGlVersion_0_ == null) {
                return p_parseGlVersion_1_;
            }
            Pattern pattern = Pattern.compile("([0-9]+)\\.([0-9]+)(\\.([0-9]+))?(.+)?");
            Matcher matcher = pattern.matcher(p_parseGlVersion_0_);
            if (!matcher.matches()) {
                return p_parseGlVersion_1_;
            }
            int i = Integer.parseInt(matcher.group(1));
            int j = Integer.parseInt(matcher.group(2));
            int k = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
            String s = matcher.group(5);
            return new GlVersion(i, j, k, s);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return p_parseGlVersion_1_;
        }
    }

    public static String[] getOpenGlExtensions() {
        if (openGlExtensions == null) {
            openGlExtensions = Config.detectOpenGlExtensions();
        }
        return openGlExtensions;
    }

    private static String[] detectOpenGlExtensions() {
        try {
            int i;
            GlVersion glversion = Config.getGlVersion();
            if (glversion.getMajor() >= 3 && (i = GL11.glGetInteger((int)33309)) > 0) {
                String[] astring = new String[i];
                for (int j = 0; j < i; ++j) {
                    astring[j] = GL30.glGetStringi((int)7939, (int)j);
                }
                return astring;
            }
        }
        catch (Exception exception1) {
            exception1.printStackTrace();
        }
        try {
            String s = GL11.glGetString((int)7939);
            String[] astring1 = s.split(" ");
            return astring1;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return new String[0];
        }
    }

    public static void updateThreadPriorities() {
        Config.updateAvailableProcessors();
        int i = 8;
        if (Config.isSingleProcessor()) {
            if (Config.isSmoothWorld()) {
                minecraftThread.setPriority(10);
                Config.setThreadPriority("Server thread", 1);
            } else {
                minecraftThread.setPriority(5);
                Config.setThreadPriority("Server thread", 5);
            }
        } else {
            minecraftThread.setPriority(10);
            Config.setThreadPriority("Server thread", 5);
        }
    }

    private static void setThreadPriority(String p_setThreadPriority_0_, int p_setThreadPriority_1_) {
        try {
            ThreadGroup threadgroup = Thread.currentThread().getThreadGroup();
            if (threadgroup == null) {
                return;
            }
            int i = (threadgroup.activeCount() + 10) * 2;
            Thread[] athread = new Thread[i];
            threadgroup.enumerate(athread, false);
            for (int j = 0; j < athread.length; ++j) {
                Thread thread = athread[j];
                if (thread == null || !thread.getName().startsWith(p_setThreadPriority_0_)) continue;
                thread.setPriority(p_setThreadPriority_1_);
            }
        }
        catch (Throwable throwable) {
            Config.warn(throwable.getClass().getName() + ": " + throwable.getMessage());
        }
    }

    public static boolean isMinecraftThread() {
        return Thread.currentThread() == minecraftThread;
    }

    private static void startVersionCheckThread() {
        VersionCheckThread versioncheckthread = new VersionCheckThread();
        versioncheckthread.start();
    }

    public static boolean isMipmaps() {
        return Config.gameSettings.mipmapLevels > 0;
    }

    public static int getMipmapLevels() {
        return Config.gameSettings.mipmapLevels;
    }

    public static int getMipmapType() {
        switch (Config.gameSettings.ofMipmapType) {
            case 0: {
                return 9986;
            }
            case 1: {
                return 9986;
            }
            case 2: {
                if (Config.isMultiTexture()) {
                    return 9985;
                }
                return 9986;
            }
            case 3: {
                if (Config.isMultiTexture()) {
                    return 9987;
                }
                return 9986;
            }
        }
        return 9986;
    }

    public static boolean isUseAlphaFunc() {
        float f = Config.getAlphaFuncLevel();
        return f > DEF_ALPHA_FUNC_LEVEL.floatValue() + 1.0E-5f;
    }

    public static float getAlphaFuncLevel() {
        return DEF_ALPHA_FUNC_LEVEL.floatValue();
    }

    public static boolean isFogFancy() {
        return !Config.isFancyFogAvailable() ? false : Config.gameSettings.ofFogType == 2;
    }

    public static boolean isFogFast() {
        return Config.gameSettings.ofFogType == 1;
    }

    public static boolean isFogOff() {
        return Config.gameSettings.ofFogType == 3;
    }

    public static float getFogStart() {
        return Config.gameSettings.ofFogStart;
    }

    public static void dbg(String p_dbg_0_) {
        LOGGER.info("[OptiFine] " + p_dbg_0_);
    }

    public static void warn(String p_warn_0_) {
        LOGGER.warn("[OptiFine] " + p_warn_0_);
    }

    public static void error(String p_error_0_) {
        LOGGER.error("[OptiFine] " + p_error_0_);
    }

    public static void log(String p_log_0_) {
        Config.dbg(p_log_0_);
    }

    public static int getUpdatesPerFrame() {
        return Config.gameSettings.ofChunkUpdates;
    }

    public static boolean isDynamicUpdates() {
        return Config.gameSettings.ofChunkUpdatesDynamic;
    }

    public static boolean isRainFancy() {
        return Config.gameSettings.ofRain == 0 ? Config.gameSettings.fancyGraphics : Config.gameSettings.ofRain == 2;
    }

    public static boolean isRainOff() {
        return Config.gameSettings.ofRain == 3;
    }

    public static boolean isCloudsFancy() {
        return Config.gameSettings.ofClouds != 0 ? Config.gameSettings.ofClouds == 2 : (Config.isShaders() && !Shaders.shaderPackClouds.isDefault() ? Shaders.shaderPackClouds.isFancy() : (texturePackClouds != 0 ? texturePackClouds == 2 : Config.gameSettings.fancyGraphics));
    }

    public static boolean isCloudsOff() {
        return Config.gameSettings.ofClouds != 0 ? Config.gameSettings.ofClouds == 3 : (Config.isShaders() && !Shaders.shaderPackClouds.isDefault() ? Shaders.shaderPackClouds.isOff() : (texturePackClouds != 0 ? texturePackClouds == 3 : false));
    }

    public static void updateTexturePackClouds() {
        texturePackClouds = 0;
        IResourceManager iresourcemanager = Config.getResourceManager();
        if (iresourcemanager != null) {
            try {
                InputStream inputstream = iresourcemanager.getResource(new ResourceLocation("mcpatcher/color.properties")).getInputStream();
                if (inputstream == null) {
                    return;
                }
                Properties properties = new Properties();
                properties.load(inputstream);
                inputstream.close();
                String s = properties.getProperty("clouds");
                if (s == null) {
                    return;
                }
                Config.dbg("Texture pack clouds: " + s);
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
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public static ModelManager getModelManager() {
        return Config.minecraft.getRenderItem().modelManager;
    }

    public static boolean isTreesFancy() {
        return Config.gameSettings.ofTrees == 0 ? Config.gameSettings.fancyGraphics : Config.gameSettings.ofTrees != 1;
    }

    public static boolean isTreesSmart() {
        return Config.gameSettings.ofTrees == 4;
    }

    public static boolean isCullFacesLeaves() {
        return Config.gameSettings.ofTrees == 0 ? !Config.gameSettings.fancyGraphics : Config.gameSettings.ofTrees == 4;
    }

    public static boolean isDroppedItemsFancy() {
        return Config.gameSettings.ofDroppedItems == 0 ? Config.gameSettings.fancyGraphics : Config.gameSettings.ofDroppedItems == 2;
    }

    public static int limit(int p_limit_0_, int p_limit_1_, int p_limit_2_) {
        return p_limit_0_ < p_limit_1_ ? p_limit_1_ : (p_limit_0_ > p_limit_2_ ? p_limit_2_ : p_limit_0_);
    }

    public static float limit(float p_limit_0_, float p_limit_1_, float p_limit_2_) {
        return p_limit_0_ < p_limit_1_ ? p_limit_1_ : (p_limit_0_ > p_limit_2_ ? p_limit_2_ : p_limit_0_);
    }

    public static double limit(double p_limit_0_, double p_limit_2_, double p_limit_4_) {
        return p_limit_0_ < p_limit_2_ ? p_limit_2_ : (p_limit_0_ > p_limit_4_ ? p_limit_4_ : p_limit_0_);
    }

    public static float limitTo1(float p_limitTo1_0_) {
        return p_limitTo1_0_ < 0.0f ? 0.0f : (p_limitTo1_0_ > 1.0f ? 1.0f : p_limitTo1_0_);
    }

    public static boolean isAnimatedWater() {
        return Config.gameSettings.ofAnimatedWater != 2;
    }

    public static boolean isGeneratedWater() {
        return Config.gameSettings.ofAnimatedWater == 1;
    }

    public static boolean isAnimatedPortal() {
        return Config.gameSettings.ofAnimatedPortal;
    }

    public static boolean isAnimatedLava() {
        return Config.gameSettings.ofAnimatedLava != 2;
    }

    public static boolean isGeneratedLava() {
        return Config.gameSettings.ofAnimatedLava == 1;
    }

    public static boolean isAnimatedFire() {
        return Config.gameSettings.ofAnimatedFire;
    }

    public static boolean isAnimatedRedstone() {
        return Config.gameSettings.ofAnimatedRedstone;
    }

    public static boolean isAnimatedExplosion() {
        return Config.gameSettings.ofAnimatedExplosion;
    }

    public static boolean isAnimatedFlame() {
        return Config.gameSettings.ofAnimatedFlame;
    }

    public static boolean isAnimatedSmoke() {
        return Config.gameSettings.ofAnimatedSmoke;
    }

    public static boolean isVoidParticles() {
        return Config.gameSettings.ofVoidParticles;
    }

    public static boolean isWaterParticles() {
        return Config.gameSettings.ofWaterParticles;
    }

    public static boolean isRainSplash() {
        return Config.gameSettings.ofRainSplash;
    }

    public static boolean isPortalParticles() {
        return Config.gameSettings.ofPortalParticles;
    }

    public static boolean isPotionParticles() {
        return Config.gameSettings.ofPotionParticles;
    }

    public static boolean isFireworkParticles() {
        return Config.gameSettings.ofFireworkParticles;
    }

    public static float getAmbientOcclusionLevel() {
        return Config.isShaders() && Shaders.aoLevel >= 0.0f ? Shaders.aoLevel : Config.gameSettings.ofAoLevel;
    }

    public static String arrayToString(Object[] p_arrayToString_0_) {
        if (p_arrayToString_0_ == null) {
            return "";
        }
        StringBuffer stringbuffer = new StringBuffer(p_arrayToString_0_.length * 5);
        for (int i = 0; i < p_arrayToString_0_.length; ++i) {
            Object object = p_arrayToString_0_[i];
            if (i > 0) {
                stringbuffer.append(", ");
            }
            stringbuffer.append(String.valueOf(object));
        }
        return stringbuffer.toString();
    }

    public static String arrayToString(int[] p_arrayToString_0_) {
        if (p_arrayToString_0_ == null) {
            return "";
        }
        StringBuffer stringbuffer = new StringBuffer(p_arrayToString_0_.length * 5);
        for (int i = 0; i < p_arrayToString_0_.length; ++i) {
            int j = p_arrayToString_0_[i];
            if (i > 0) {
                stringbuffer.append(", ");
            }
            stringbuffer.append(String.valueOf(j));
        }
        return stringbuffer.toString();
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

    public static InputStream getResourceStream(ResourceLocation p_getResourceStream_0_) throws IOException {
        return Config.getResourceStream(minecraft.getResourceManager(), p_getResourceStream_0_);
    }

    public static InputStream getResourceStream(IResourceManager p_getResourceStream_0_, ResourceLocation p_getResourceStream_1_) throws IOException {
        IResource iresource = p_getResourceStream_0_.getResource(p_getResourceStream_1_);
        return iresource == null ? null : iresource.getInputStream();
    }

    public static IResource getResource(ResourceLocation p_getResource_0_) throws IOException {
        return minecraft.getResourceManager().getResource(p_getResource_0_);
    }

    public static boolean hasResource(ResourceLocation p_hasResource_0_) {
        IResourcePack iresourcepack = Config.getDefiningResourcePack(p_hasResource_0_);
        return iresourcepack != null;
    }

    public static boolean hasResource(IResourceManager p_hasResource_0_, ResourceLocation p_hasResource_1_) {
        try {
            IResource iresource = p_hasResource_0_.getResource(p_hasResource_1_);
            return iresource != null;
        }
        catch (IOException var3) {
            return false;
        }
    }

    public static IResourcePack[] getResourcePacks() {
        ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();
        List<ResourcePackRepository.Entry> list = resourcepackrepository.getRepositoryEntries();
        ArrayList<IResourcePack> list1 = new ArrayList<IResourcePack>();
        for (ResourcePackRepository.Entry resourcepackrepository$entry : list) {
            list1.add(resourcepackrepository$entry.getResourcePack());
        }
        if (resourcepackrepository.getResourcePackInstance() != null) {
            list1.add(resourcepackrepository.getResourcePackInstance());
        }
        IResourcePack[] airesourcepack = list1.toArray(new IResourcePack[list1.size()]);
        return airesourcepack;
    }

    public static String getResourcePackNames() {
        if (minecraft.getResourcePackRepository() == null) {
            return "";
        }
        IResourcePack[] airesourcepack = Config.getResourcePacks();
        if (airesourcepack.length <= 0) {
            return Config.getDefaultResourcePack().getPackName();
        }
        String[] astring = new String[airesourcepack.length];
        for (int i = 0; i < airesourcepack.length; ++i) {
            astring[i] = airesourcepack[i].getPackName();
        }
        String s = Config.arrayToString(astring);
        return s;
    }

    public static DefaultResourcePack getDefaultResourcePack() {
        ResourcePackRepository resourcepackrepository;
        Minecraft minecraft;
        if (defaultResourcePackLazy == null && (defaultResourcePackLazy = (DefaultResourcePack)Reflector.getFieldValue(minecraft = Minecraft.getMinecraft(), Reflector.Minecraft_defaultResourcePack)) == null && (resourcepackrepository = minecraft.getResourcePackRepository()) != null) {
            defaultResourcePackLazy = (DefaultResourcePack)resourcepackrepository.rprDefaultResourcePack;
        }
        return defaultResourcePackLazy;
    }

    public static boolean isFromDefaultResourcePack(ResourceLocation p_isFromDefaultResourcePack_0_) {
        IResourcePack iresourcepack = Config.getDefiningResourcePack(p_isFromDefaultResourcePack_0_);
        return iresourcepack == Config.getDefaultResourcePack();
    }

    public static IResourcePack getDefiningResourcePack(ResourceLocation p_getDefiningResourcePack_0_) {
        ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();
        IResourcePack iresourcepack = resourcepackrepository.getResourcePackInstance();
        if (iresourcepack != null && iresourcepack.resourceExists(p_getDefiningResourcePack_0_)) {
            return iresourcepack;
        }
        List list = (List)Reflector.getFieldValue(resourcepackrepository, Reflector.ResourcePackRepository_repositoryEntries);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; --i) {
                ResourcePackRepository.Entry resourcepackrepository$entry = (ResourcePackRepository.Entry)list.get(i);
                IResourcePack iresourcepack1 = resourcepackrepository$entry.getResourcePack();
                if (!iresourcepack1.resourceExists(p_getDefiningResourcePack_0_)) continue;
                return iresourcepack1;
            }
        }
        return Config.getDefaultResourcePack().resourceExists(p_getDefiningResourcePack_0_) ? Config.getDefaultResourcePack() : null;
    }

    public static RenderGlobal getRenderGlobal() {
        return Config.minecraft.renderGlobal;
    }

    public static boolean isBetterGrass() {
        return Config.gameSettings.ofBetterGrass != 3;
    }

    public static boolean isBetterGrassFancy() {
        return Config.gameSettings.ofBetterGrass == 2;
    }

    public static boolean isWeatherEnabled() {
        return Config.gameSettings.ofWeather;
    }

    public static boolean isSkyEnabled() {
        return Config.gameSettings.ofSky;
    }

    public static boolean isSunMoonEnabled() {
        return Config.gameSettings.ofSunMoon;
    }

    public static boolean isSunTexture() {
        return !Config.isSunMoonEnabled() ? false : !Config.isShaders() || Shaders.isSun();
    }

    public static boolean isMoonTexture() {
        return !Config.isSunMoonEnabled() ? false : !Config.isShaders() || Shaders.isMoon();
    }

    public static boolean isVignetteEnabled() {
        return Config.isShaders() && !Shaders.isVignette() ? false : (Config.gameSettings.ofVignette == 0 ? Config.gameSettings.fancyGraphics : Config.gameSettings.ofVignette == 2);
    }

    public static boolean isStarsEnabled() {
        return Config.gameSettings.ofStars;
    }

    public static void sleep(long p_sleep_0_) {
        try {
            Thread.sleep(p_sleep_0_);
        }
        catch (InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
        }
    }

    public static boolean isTimeDayOnly() {
        return Config.gameSettings.ofTime == 1;
    }

    public static boolean isTimeDefault() {
        return Config.gameSettings.ofTime == 0;
    }

    public static boolean isTimeNightOnly() {
        return Config.gameSettings.ofTime == 2;
    }

    public static boolean isClearWater() {
        return Config.gameSettings.ofClearWater;
    }

    public static int getAnisotropicFilterLevel() {
        return Config.gameSettings.ofAfLevel;
    }

    public static boolean isAnisotropicFiltering() {
        return Config.getAnisotropicFilterLevel() > 1;
    }

    public static int getAntialiasingLevel() {
        return antialiasingLevel;
    }

    public static boolean isAntialiasing() {
        return Config.getAntialiasingLevel() > 0;
    }

    public static boolean isAntialiasingConfigured() {
        return Config.getGameSettings().ofAaLevel > 0;
    }

    public static boolean isMultiTexture() {
        return Config.getAnisotropicFilterLevel() > 1 ? true : Config.getAntialiasingLevel() > 0;
    }

    public static boolean between(int p_between_0_, int p_between_1_, int p_between_2_) {
        return p_between_0_ >= p_between_1_ && p_between_0_ <= p_between_2_;
    }

    public static boolean isDrippingWaterLava() {
        return Config.gameSettings.ofDrippingWaterLava;
    }

    public static boolean isBetterSnow() {
        return Config.gameSettings.ofBetterSnow;
    }

    public static Dimension getFullscreenDimension() {
        if (desktopDisplayMode == null) {
            return null;
        }
        if (gameSettings == null) {
            return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
        }
        String s = Config.gameSettings.ofFullscreenMode;
        if (s.equals("Default")) {
            return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
        }
        String[] astring = Config.tokenize(s, " x");
        return astring.length < 2 ? new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight()) : new Dimension(Config.parseInt(astring[0], -1), Config.parseInt(astring[1], -1));
    }

    public static int parseInt(String p_parseInt_0_, int p_parseInt_1_) {
        try {
            if (p_parseInt_0_ == null) {
                return p_parseInt_1_;
            }
            p_parseInt_0_ = p_parseInt_0_.trim();
            return Integer.parseInt(p_parseInt_0_);
        }
        catch (NumberFormatException var3) {
            return p_parseInt_1_;
        }
    }

    public static float parseFloat(String p_parseFloat_0_, float p_parseFloat_1_) {
        try {
            if (p_parseFloat_0_ == null) {
                return p_parseFloat_1_;
            }
            p_parseFloat_0_ = p_parseFloat_0_.trim();
            return Float.parseFloat(p_parseFloat_0_);
        }
        catch (NumberFormatException var3) {
            return p_parseFloat_1_;
        }
    }

    public static boolean parseBoolean(String p_parseBoolean_0_, boolean p_parseBoolean_1_) {
        try {
            if (p_parseBoolean_0_ == null) {
                return p_parseBoolean_1_;
            }
            p_parseBoolean_0_ = p_parseBoolean_0_.trim();
            return Boolean.parseBoolean(p_parseBoolean_0_);
        }
        catch (NumberFormatException var3) {
            return p_parseBoolean_1_;
        }
    }

    public static String[] tokenize(String p_tokenize_0_, String p_tokenize_1_) {
        StringTokenizer stringtokenizer = new StringTokenizer(p_tokenize_0_, p_tokenize_1_);
        ArrayList<String> list = new ArrayList<String>();
        while (stringtokenizer.hasMoreTokens()) {
            String s = stringtokenizer.nextToken();
            list.add(s);
        }
        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }

    public static DisplayMode getDesktopDisplayMode() {
        return desktopDisplayMode;
    }

    public static DisplayMode[] getDisplayModes() {
        if (displayModes == null) {
            try {
                DisplayMode[] adisplaymode = Display.getAvailableDisplayModes();
                Set<Dimension> set = Config.getDisplayModeDimensions(adisplaymode);
                ArrayList<DisplayMode> list = new ArrayList<DisplayMode>();
                for (Dimension dimension : set) {
                    DisplayMode[] adisplaymode1 = Config.getDisplayModes(adisplaymode, dimension);
                    DisplayMode displaymode = Config.getDisplayMode(adisplaymode1, desktopDisplayMode);
                    if (displaymode == null) continue;
                    list.add(displaymode);
                }
                DisplayMode[] adisplaymode2 = list.toArray(new DisplayMode[list.size()]);
                Arrays.sort(adisplaymode2, new DisplayModeComparator());
                return adisplaymode2;
            }
            catch (Exception exception) {
                exception.printStackTrace();
                displayModes = new DisplayMode[]{desktopDisplayMode};
            }
        }
        return displayModes;
    }

    public static DisplayMode getLargestDisplayMode() {
        DisplayMode[] adisplaymode = Config.getDisplayModes();
        if (adisplaymode != null && adisplaymode.length >= 1) {
            DisplayMode displaymode = adisplaymode[adisplaymode.length - 1];
            return desktopDisplayMode.getWidth() > displaymode.getWidth() ? desktopDisplayMode : (desktopDisplayMode.getWidth() == displaymode.getWidth() && desktopDisplayMode.getHeight() > displaymode.getHeight() ? desktopDisplayMode : displaymode);
        }
        return desktopDisplayMode;
    }

    private static Set<Dimension> getDisplayModeDimensions(DisplayMode[] p_getDisplayModeDimensions_0_) {
        HashSet<Dimension> set = new HashSet<Dimension>();
        for (int i = 0; i < p_getDisplayModeDimensions_0_.length; ++i) {
            DisplayMode displaymode = p_getDisplayModeDimensions_0_[i];
            Dimension dimension = new Dimension(displaymode.getWidth(), displaymode.getHeight());
            set.add(dimension);
        }
        return set;
    }

    private static DisplayMode[] getDisplayModes(DisplayMode[] p_getDisplayModes_0_, Dimension p_getDisplayModes_1_) {
        ArrayList<DisplayMode> list = new ArrayList<DisplayMode>();
        for (int i = 0; i < p_getDisplayModes_0_.length; ++i) {
            DisplayMode displaymode = p_getDisplayModes_0_[i];
            if ((double)displaymode.getWidth() != p_getDisplayModes_1_.getWidth() || (double)displaymode.getHeight() != p_getDisplayModes_1_.getHeight()) continue;
            list.add(displaymode);
        }
        DisplayMode[] adisplaymode = list.toArray(new DisplayMode[list.size()]);
        return adisplaymode;
    }

    private static DisplayMode getDisplayMode(DisplayMode[] p_getDisplayMode_0_, DisplayMode p_getDisplayMode_1_) {
        if (p_getDisplayMode_1_ != null) {
            for (int i = 0; i < p_getDisplayMode_0_.length; ++i) {
                DisplayMode displaymode = p_getDisplayMode_0_[i];
                if (displaymode.getBitsPerPixel() != p_getDisplayMode_1_.getBitsPerPixel() || displaymode.getFrequency() != p_getDisplayMode_1_.getFrequency()) continue;
                return displaymode;
            }
        }
        if (p_getDisplayMode_0_.length <= 0) {
            return null;
        }
        Arrays.sort(p_getDisplayMode_0_, new DisplayModeComparator());
        return p_getDisplayMode_0_[p_getDisplayMode_0_.length - 1];
    }

    public static String[] getDisplayModeNames() {
        DisplayMode[] adisplaymode = Config.getDisplayModes();
        String[] astring = new String[adisplaymode.length];
        for (int i = 0; i < adisplaymode.length; ++i) {
            String s;
            DisplayMode displaymode = adisplaymode[i];
            astring[i] = s = "" + displaymode.getWidth() + "x" + displaymode.getHeight();
        }
        return astring;
    }

    public static DisplayMode getDisplayMode(Dimension p_getDisplayMode_0_) throws LWJGLException {
        DisplayMode[] adisplaymode = Config.getDisplayModes();
        for (int i = 0; i < adisplaymode.length; ++i) {
            DisplayMode displaymode = adisplaymode[i];
            if (displaymode.getWidth() != p_getDisplayMode_0_.width || displaymode.getHeight() != p_getDisplayMode_0_.height) continue;
            return displaymode;
        }
        return desktopDisplayMode;
    }

    public static boolean isAnimatedTerrain() {
        return Config.gameSettings.ofAnimatedTerrain;
    }

    public static boolean isAnimatedTextures() {
        return Config.gameSettings.ofAnimatedTextures;
    }

    public static boolean isSwampColors() {
        return Config.gameSettings.ofSwampColors;
    }

    public static boolean isRandomMobs() {
        return Config.gameSettings.ofRandomMobs;
    }

    public static void checkGlError(String p_checkGlError_0_) {
        int i = GL11.glGetError();
        if (i != 0) {
            String s = GLU.gluErrorString((int)i);
            Config.error("OpenGlError: " + i + " (" + s + "), at: " + p_checkGlError_0_);
        }
    }

    public static boolean isSmoothBiomes() {
        return Config.gameSettings.ofSmoothBiomes;
    }

    public static boolean isCustomColors() {
        return Config.gameSettings.ofCustomColors;
    }

    public static boolean isCustomSky() {
        return Config.gameSettings.ofCustomSky;
    }

    public static boolean isCustomFonts() {
        return Config.gameSettings.ofCustomFonts;
    }

    public static boolean isShowCapes() {
        return Config.gameSettings.ofShowCapes;
    }

    public static boolean isConnectedTextures() {
        return Config.gameSettings.ofConnectedTextures != 3;
    }

    public static boolean isNaturalTextures() {
        return Config.gameSettings.ofNaturalTextures;
    }

    public static boolean isConnectedTexturesFancy() {
        return Config.gameSettings.ofConnectedTextures == 2;
    }

    public static boolean isFastRender() {
        return Config.gameSettings.ofFastRender;
    }

    public static boolean isTranslucentBlocksFancy() {
        return Config.gameSettings.ofTranslucentBlocks == 0 ? Config.gameSettings.fancyGraphics : Config.gameSettings.ofTranslucentBlocks == 2;
    }

    public static boolean isShaders() {
        return Shaders.shaderPackLoaded;
    }

    public static String[] readLines(File p_readLines_0_) throws IOException {
        FileInputStream fileinputstream = new FileInputStream(p_readLines_0_);
        return Config.readLines(fileinputstream);
    }

    public static String[] readLines(InputStream p_readLines_0_) throws IOException {
        ArrayList<String> list = new ArrayList<String>();
        InputStreamReader inputstreamreader = new InputStreamReader(p_readLines_0_, "ASCII");
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        while (true) {
            String s;
            if ((s = bufferedreader.readLine()) == null) {
                String[] astring = list.toArray(new String[list.size()]);
                return astring;
            }
            list.add(s);
        }
    }

    public static String readFile(File p_readFile_0_) throws IOException {
        FileInputStream fileinputstream = new FileInputStream(p_readFile_0_);
        return Config.readInputStream(fileinputstream, "ASCII");
    }

    public static String readInputStream(InputStream p_readInputStream_0_) throws IOException {
        return Config.readInputStream(p_readInputStream_0_, "ASCII");
    }

    public static String readInputStream(InputStream p_readInputStream_0_, String p_readInputStream_1_) throws IOException {
        InputStreamReader inputstreamreader = new InputStreamReader(p_readInputStream_0_, p_readInputStream_1_);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        StringBuffer stringbuffer = new StringBuffer();
        String s;
        while ((s = bufferedreader.readLine()) != null) {
            stringbuffer.append(s);
            stringbuffer.append("\n");
        }
        return stringbuffer.toString();
    }

    public static byte[] readAll(InputStream p_readAll_0_) throws IOException {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        byte[] abyte = new byte[1024];
        while (true) {
            int i;
            if ((i = p_readAll_0_.read(abyte)) < 0) {
                p_readAll_0_.close();
                byte[] abyte1 = bytearrayoutputstream.toByteArray();
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

    public static void setNewRelease(String p_setNewRelease_0_) {
        newRelease = p_setNewRelease_0_;
    }

    public static int compareRelease(String p_compareRelease_0_, String p_compareRelease_1_) {
        int j;
        String[] astring1;
        String s1;
        String[] astring = Config.splitRelease(p_compareRelease_0_);
        String s = astring[0];
        if (!s.equals(s1 = (astring1 = Config.splitRelease(p_compareRelease_1_))[0])) {
            return s.compareTo(s1);
        }
        int i = Config.parseInt(astring[1], -1);
        if (i != (j = Config.parseInt(astring1[1], -1))) {
            return i - j;
        }
        String s2 = astring[2];
        String s3 = astring1[2];
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

    private static String[] splitRelease(String p_splitRelease_0_) {
        if (p_splitRelease_0_ != null && p_splitRelease_0_.length() > 0) {
            Pattern pattern = Pattern.compile("([A-Z])([0-9]+)(.*)");
            Matcher matcher = pattern.matcher(p_splitRelease_0_);
            if (!matcher.matches()) {
                return new String[]{"", "", ""};
            }
            String s = Config.normalize(matcher.group(1));
            String s1 = Config.normalize(matcher.group(2));
            String s2 = Config.normalize(matcher.group(3));
            return new String[]{s, s1, s2};
        }
        return new String[]{"", "", ""};
    }

    public static int intHash(int p_intHash_0_) {
        p_intHash_0_ = p_intHash_0_ ^ 0x3D ^ p_intHash_0_ >> 16;
        p_intHash_0_ += p_intHash_0_ << 3;
        p_intHash_0_ ^= p_intHash_0_ >> 4;
        p_intHash_0_ *= 668265261;
        p_intHash_0_ ^= p_intHash_0_ >> 15;
        return p_intHash_0_;
    }

    public static int getRandom(BlockPos p_getRandom_0_, int p_getRandom_1_) {
        int i = Config.intHash(p_getRandom_1_ + 37);
        i = Config.intHash(i + p_getRandom_0_.getX());
        i = Config.intHash(i + p_getRandom_0_.getZ());
        i = Config.intHash(i + p_getRandom_0_.getY());
        return i;
    }

    public static WorldServer getWorldServer() {
        WorldClient world = Config.minecraft.theWorld;
        if (world == null) {
            return null;
        }
        if (!minecraft.isIntegratedServerRunning()) {
            return null;
        }
        IntegratedServer integratedserver = minecraft.getIntegratedServer();
        if (integratedserver == null) {
            return null;
        }
        WorldProvider worldprovider = world.provider;
        if (worldprovider == null) {
            return null;
        }
        int i = worldprovider.getDimensionId();
        try {
            WorldServer worldserver = integratedserver.worldServerForDimension(i);
            return worldserver;
        }
        catch (NullPointerException var5) {
            return null;
        }
    }

    public static int getAvailableProcessors() {
        return availableProcessors;
    }

    public static void updateAvailableProcessors() {
        availableProcessors = Runtime.getRuntime().availableProcessors();
    }

    public static boolean isSingleProcessor() {
        return Config.getAvailableProcessors() <= 1;
    }

    public static boolean isSmoothWorld() {
        return Config.gameSettings.ofSmoothWorld;
    }

    public static boolean isLazyChunkLoading() {
        return !Config.isSingleProcessor() ? false : Config.gameSettings.ofLazyChunkLoading;
    }

    public static boolean isDynamicFov() {
        return Config.gameSettings.ofDynamicFov;
    }

    public static boolean isAlternateBlocks() {
        return Config.gameSettings.allowBlockAlternatives;
    }

    public static int getChunkViewDistance() {
        if (gameSettings == null) {
            return 10;
        }
        int i = Config.gameSettings.renderDistanceChunks;
        return i;
    }

    public static boolean equals(Object p_equals_0_, Object p_equals_1_) {
        return p_equals_0_ == p_equals_1_ ? true : (p_equals_0_ == null ? false : p_equals_0_.equals(p_equals_1_));
    }

    public static boolean equalsOne(Object p_equalsOne_0_, Object[] p_equalsOne_1_) {
        if (p_equalsOne_1_ == null) {
            return false;
        }
        for (int i = 0; i < p_equalsOne_1_.length; ++i) {
            Object object = p_equalsOne_1_[i];
            if (!Config.equals(p_equalsOne_0_, object)) continue;
            return true;
        }
        return false;
    }

    public static boolean isSameOne(Object p_isSameOne_0_, Object[] p_isSameOne_1_) {
        if (p_isSameOne_1_ == null) {
            return false;
        }
        for (int i = 0; i < p_isSameOne_1_.length; ++i) {
            Object object = p_isSameOne_1_[i];
            if (p_isSameOne_0_ != object) continue;
            return true;
        }
        return false;
    }

    public static String normalize(String p_normalize_0_) {
        return p_normalize_0_ == null ? "" : p_normalize_0_;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void checkDisplaySettings() {
        int i = Config.getAntialiasingLevel();
        if (i > 0) {
            DisplayMode displaymode = Display.getDisplayMode();
            Config.dbg("FSAA Samples: " + i);
            try {
                Display.destroy();
                Display.setDisplayMode((DisplayMode)displaymode);
                Display.create((PixelFormat)new PixelFormat().withDepthBits(24).withSamples(i));
                Display.setResizable((boolean)false);
                Display.setResizable((boolean)true);
            }
            catch (LWJGLException lwjglexception2) {
                Config.warn("Error setting FSAA: " + i + "x");
                lwjglexception2.printStackTrace();
                try {
                    Display.setDisplayMode((DisplayMode)displaymode);
                    Display.create((PixelFormat)new PixelFormat().withDepthBits(24));
                    Display.setResizable((boolean)false);
                    Display.setResizable((boolean)true);
                }
                catch (LWJGLException lwjglexception1) {
                    lwjglexception1.printStackTrace();
                    try {
                        Display.setDisplayMode((DisplayMode)displaymode);
                        Display.create();
                        Display.setResizable((boolean)false);
                        Display.setResizable((boolean)true);
                    }
                    catch (LWJGLException lwjglexception) {
                        lwjglexception.printStackTrace();
                    }
                }
            }
            if (!Minecraft.isRunningOnMac && Config.getDefaultResourcePack() != null) {
                InputStream inputstream1;
                InputStream inputstream;
                block12: {
                    inputstream = null;
                    inputstream1 = null;
                    try {
                        inputstream = Config.getDefaultResourcePack().getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
                        inputstream1 = Config.getDefaultResourcePack().getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
                        if (inputstream == null || inputstream1 == null) break block12;
                        Display.setIcon((ByteBuffer[])new ByteBuffer[]{Config.readIconImage(inputstream), Config.readIconImage(inputstream1)});
                    }
                    catch (IOException ioexception) {
                        try {
                            Config.warn("Error setting window icon: " + ioexception.getClass().getName() + ": " + ioexception.getMessage());
                        }
                        catch (Throwable throwable) {
                            IOUtils.closeQuietly(inputstream);
                            IOUtils.closeQuietly(inputstream1);
                            throw throwable;
                        }
                        IOUtils.closeQuietly((InputStream)inputstream);
                        IOUtils.closeQuietly((InputStream)inputstream1);
                    }
                }
                IOUtils.closeQuietly((InputStream)inputstream);
                IOUtils.closeQuietly((InputStream)inputstream1);
            }
        }
    }

    private static ByteBuffer readIconImage(InputStream p_readIconImage_0_) throws IOException {
        BufferedImage bufferedimage = ImageIO.read(p_readIconImage_0_);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);
        for (int i : aint) {
            bytebuffer.putInt(i << 8 | i >> 24 & 0xFF);
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
                DisplayMode displaymode = Display.getDisplayMode();
                Dimension dimension = Config.getFullscreenDimension();
                if (dimension == null) {
                    return;
                }
                if (displaymode.getWidth() == dimension.width && displaymode.getHeight() == dimension.height) {
                    return;
                }
                DisplayMode displaymode1 = Config.getDisplayMode(dimension);
                if (displaymode1 == null) {
                    return;
                }
                Display.setDisplayMode((DisplayMode)displaymode1);
                Config.minecraft.displayWidth = Display.getDisplayMode().getWidth();
                Config.minecraft.displayHeight = Display.getDisplayMode().getHeight();
                if (Config.minecraft.displayWidth <= 0) {
                    Config.minecraft.displayWidth = 1;
                }
                if (Config.minecraft.displayHeight <= 0) {
                    Config.minecraft.displayHeight = 1;
                }
                if (Config.minecraft.currentScreen != null) {
                    ScaledResolution scaledresolution = new ScaledResolution(minecraft);
                    int i = scaledresolution.getScaledWidth();
                    int j = scaledresolution.getScaledHeight();
                    Config.minecraft.currentScreen.setWorldAndResolution(minecraft, i, j);
                }
                Config.minecraft.loadingScreen = new LoadingScreenRenderer(minecraft);
                Config.updateFramebufferSize();
                Display.setFullscreen((boolean)true);
                Config.minecraft.gameSettings.updateVSync();
                GlStateManager.enableTexture2D();
            } else {
                if (desktopModeChecked) {
                    return;
                }
                desktopModeChecked = true;
                fullscreenModeChecked = false;
                Config.minecraft.gameSettings.updateVSync();
                Display.update();
                GlStateManager.enableTexture2D();
                Display.setResizable((boolean)false);
                Display.setResizable((boolean)true);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            Config.gameSettings.ofFullscreenMode = "Default";
            gameSettings.saveOfOptions();
        }
    }

    public static void updateFramebufferSize() {
        minecraft.getFramebuffer().createBindFramebuffer(Config.minecraft.displayWidth, Config.minecraft.displayHeight);
        if (Config.minecraft.entityRenderer != null) {
            Config.minecraft.entityRenderer.updateShaderGroupSize(Config.minecraft.displayWidth, Config.minecraft.displayHeight);
        }
    }

    public static Object[] addObjectToArray(Object[] p_addObjectToArray_0_, Object p_addObjectToArray_1_) {
        if (p_addObjectToArray_0_ == null) {
            throw new NullPointerException("The given array is NULL");
        }
        int i = p_addObjectToArray_0_.length;
        int j = i + 1;
        Object[] aobject = (Object[])Array.newInstance(p_addObjectToArray_0_.getClass().getComponentType(), j);
        System.arraycopy(p_addObjectToArray_0_, 0, aobject, 0, i);
        aobject[i] = p_addObjectToArray_1_;
        return aobject;
    }

    public static Object[] addObjectToArray(Object[] p_addObjectToArray_0_, Object p_addObjectToArray_1_, int p_addObjectToArray_2_) {
        ArrayList<Object> list = new ArrayList<Object>(Arrays.asList(p_addObjectToArray_0_));
        list.add(p_addObjectToArray_2_, p_addObjectToArray_1_);
        Object[] aobject = (Object[])Array.newInstance(p_addObjectToArray_0_.getClass().getComponentType(), list.size());
        return list.toArray(aobject);
    }

    public static Object[] addObjectsToArray(Object[] p_addObjectsToArray_0_, Object[] p_addObjectsToArray_1_) {
        if (p_addObjectsToArray_0_ == null) {
            throw new NullPointerException("The given array is NULL");
        }
        if (p_addObjectsToArray_1_.length == 0) {
            return p_addObjectsToArray_0_;
        }
        int i = p_addObjectsToArray_0_.length;
        int j = i + p_addObjectsToArray_1_.length;
        Object[] aobject = (Object[])Array.newInstance(p_addObjectsToArray_0_.getClass().getComponentType(), j);
        System.arraycopy(p_addObjectsToArray_0_, 0, aobject, 0, i);
        System.arraycopy(p_addObjectsToArray_1_, 0, aobject, i, p_addObjectsToArray_1_.length);
        return aobject;
    }

    public static boolean isCustomItems() {
        return Config.gameSettings.ofCustomItems;
    }

    public static void drawFps() {
        int i = Minecraft.getDebugFPS();
        String s = Config.getUpdates(Config.minecraft.debug);
        int j = Config.minecraft.renderGlobal.getCountActiveRenderers();
        int k = Config.minecraft.renderGlobal.getCountEntitiesRendered();
        int l = Config.minecraft.renderGlobal.getCountTileEntitiesRendered();
        String s1 = "" + i + " fps, C: " + j + ", E: " + k + "+" + l + ", U: " + s;
        Config.minecraft.fontRendererObj.drawString(s1, 2, 2, -2039584);
    }

    private static String getUpdates(String p_getUpdates_0_) {
        int i = p_getUpdates_0_.indexOf(40);
        if (i < 0) {
            return "";
        }
        int j = p_getUpdates_0_.indexOf(32, i);
        return j < 0 ? "" : p_getUpdates_0_.substring(i + 1, j);
    }

    public static int getBitsOs() {
        String s = System.getenv("ProgramFiles(X86)");
        return s != null ? 64 : 32;
    }

    public static int getBitsJre() {
        String[] astring = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            String s1 = System.getProperty(s);
            if (s1 == null || !s1.contains("64")) continue;
            return 64;
        }
        return 32;
    }

    public static boolean isNotify64BitJava() {
        return notify64BitJava;
    }

    public static void setNotify64BitJava(boolean p_setNotify64BitJava_0_) {
        notify64BitJava = p_setNotify64BitJava_0_;
    }

    public static boolean isConnectedModels() {
        return false;
    }

    public static void showGuiMessage(String p_showGuiMessage_0_, String p_showGuiMessage_1_) {
        GuiMessage guimessage = new GuiMessage(Config.minecraft.currentScreen, p_showGuiMessage_0_, p_showGuiMessage_1_);
        minecraft.displayGuiScreen(guimessage);
    }

    public static int[] addIntToArray(int[] p_addIntToArray_0_, int p_addIntToArray_1_) {
        return Config.addIntsToArray(p_addIntToArray_0_, new int[]{p_addIntToArray_1_});
    }

    public static int[] addIntsToArray(int[] p_addIntsToArray_0_, int[] p_addIntsToArray_1_) {
        if (p_addIntsToArray_0_ != null && p_addIntsToArray_1_ != null) {
            int i = p_addIntsToArray_0_.length;
            int j = i + p_addIntsToArray_1_.length;
            int[] aint = new int[j];
            System.arraycopy(p_addIntsToArray_0_, 0, aint, 0, i);
            for (int k = 0; k < p_addIntsToArray_1_.length; ++k) {
                aint[k + i] = p_addIntsToArray_1_[k];
            }
            return aint;
        }
        throw new NullPointerException("The given array is NULL");
    }

    public static DynamicTexture getMojangLogoTexture(DynamicTexture p_getMojangLogoTexture_0_) {
        try {
            ResourceLocation resourcelocation = new ResourceLocation("textures/gui/title/mojang.png");
            InputStream inputstream = Config.getResourceStream(resourcelocation);
            if (inputstream == null) {
                return p_getMojangLogoTexture_0_;
            }
            BufferedImage bufferedimage = ImageIO.read(inputstream);
            if (bufferedimage == null) {
                return p_getMojangLogoTexture_0_;
            }
            DynamicTexture dynamictexture = new DynamicTexture(bufferedimage);
            return dynamictexture;
        }
        catch (Exception exception) {
            Config.warn(exception.getClass().getName() + ": " + exception.getMessage());
            return p_getMojangLogoTexture_0_;
        }
    }

    public static void writeFile(File p_writeFile_0_, String p_writeFile_1_) throws IOException {
        FileOutputStream fileoutputstream = new FileOutputStream(p_writeFile_0_);
        byte[] abyte = p_writeFile_1_.getBytes("ASCII");
        fileoutputstream.write(abyte);
        fileoutputstream.close();
    }

    public static TextureMap getTextureMap() {
        return Config.getMinecraft().getTextureMapBlocks();
    }

    public static boolean isDynamicLights() {
        return Config.gameSettings.ofDynamicLights != 3;
    }

    public static boolean isDynamicLightsFast() {
        return Config.gameSettings.ofDynamicLights == 1;
    }

    public static boolean isDynamicHandLight() {
        return !Config.isDynamicLights() ? false : (Config.isShaders() ? Shaders.isDynamicHandLight() : true);
    }
}

