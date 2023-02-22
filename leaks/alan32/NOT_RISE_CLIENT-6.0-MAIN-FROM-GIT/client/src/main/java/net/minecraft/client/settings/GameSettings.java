package net.minecraft.client.settings;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.optifine.*;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import net.optifine.util.KeyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameSettings {
    private static final Logger logger = LogManager.getLogger();
    private static final Gson gson = new Gson();
    private static final ParameterizedType typeListString = new ParameterizedType() {
        public Type[] getActualTypeArguments() {
            return new Type[]{String.class};
        }

        public Type getRawType() {
            return List.class;
        }

        public Type getOwnerType() {
            return null;
        }
    };

    /**
     * GUI scale values
     */
    private static final String[] GUISCALES = new String[]{"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
    private static final String[] PARTICLES = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
    private static final String[] AMBIENT_OCCLUSIONS = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
    private static final String[] STREAM_COMPRESSIONS = new String[]{"options.stream.compression.low", "options.stream.compression.medium", "options.stream.compression.high"};
    private static final String[] STREAM_CHAT_MODES = new String[]{"options.stream.chat.enabled.streaming", "options.stream.chat.enabled.always", "options.stream.chat.enabled.never"};
    private static final String[] STREAM_CHAT_FILTER_MODES = new String[]{"options.stream.chat.userFilter.all", "options.stream.chat.userFilter.subs", "options.stream.chat.userFilter.mods"};
    private static final String[] STREAM_MIC_MODES = new String[]{"options.stream.mic_toggle.mute", "options.stream.mic_toggle.talk"};
    private static final String[] field_181149_aW = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
    public float mouseSensitivity = 0.5F;
    public boolean invertMouse;
    public int renderDistanceChunks = -1;
    public boolean viewBobbing = true;
    public boolean anaglyph;
    public boolean fboEnable = true;
    public int limitFramerate = 120;

    /**
     * Clouds flag
     */
    public int clouds = 2;
    public boolean fancyGraphics = true;

    /**
     * Smooth Lighting
     */
    public int ambientOcclusion = 2;
    public List<String> resourcePacks = Lists.newArrayList();
    public List<String> field_183018_l = Lists.newArrayList();
    public EntityPlayer.EnumChatVisibility chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
    public boolean chatColours = true;
    public boolean chatLinks = true;
    public boolean chatLinksPrompt = true;
    public float chatOpacity = 1.0F;
    public boolean snooperEnabled = true;
    public boolean fullScreen;
    public boolean enableVsync = true;
    public boolean useVbo = false;
    public boolean allowBlockAlternatives = true;
    public boolean reducedDebugInfo = false;
    public boolean hideServerAddress;

    /**
     * Whether to show advanced information on item tooltips, toggled by F3+H
     */
    public boolean advancedItemTooltips;

    /**
     * Whether to pause when the game loses focus, toggled by F3+P
     */
    public boolean pauseOnLostFocus = true;
    private final Set<EnumPlayerModelParts> setModelParts = Sets.newHashSet(EnumPlayerModelParts.values());
    public boolean touchscreen;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips = true;
    public float chatScale = 1.0F;
    public float chatWidth = 1.0F;
    public float chatHeightUnfocused = 0.44366196F;
    public float chatHeightFocused = 1.0F;
    public boolean showInventoryAchievementHint = true;
    public int mipmapLevels = 4;
    private final Map<SoundCategory, Float> mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
    public float streamBytesPerPixel = 0.5F;
    public float streamMicVolume = 1.0F;
    public float streamGameVolume = 1.0F;
    public float streamKbps = 0.5412844F;
    public float streamFps = 0.31690142F;
    public int streamCompression = 1;
    public boolean streamSendMetadata = true;
    public String streamPreferredServer = "";
    public int streamChatEnabled = 0;
    public int streamChatUserFilter = 0;
    public int streamMicToggleBehavior = 0;
    public boolean field_181150_U = true;
    public boolean field_181151_V = true;
    public boolean field_183509_X = true;
    public KeyBinding keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
    public KeyBinding keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
    public KeyBinding keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
    public KeyBinding keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
    public KeyBinding keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
    public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
    public KeyBinding keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.movement");
    public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
    public KeyBinding keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
    public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
    public KeyBinding keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
    public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
    public KeyBinding keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
    public KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
    public KeyBinding keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
    public KeyBinding keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
    public KeyBinding keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
    public KeyBinding keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
    public KeyBinding keyBindFullscreen = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
    public KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
    public KeyBinding keyBindStreamStartStop = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
    public KeyBinding keyBindStreamPauseUnpause = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
    public KeyBinding keyBindStreamCommercials = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
    public KeyBinding keyBindStreamToggleMic = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
    public KeyBinding[] keyBindsHotbar = new KeyBinding[]{new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")};
    public KeyBinding[] keyBindings;
    protected Minecraft mc;
    private File optionsFile;
    public EnumDifficulty difficulty;
    public boolean hideGUI;
    public int thirdPersonView;

    /**
     * true if debug info should be displayed instead of version
     */
    public boolean showDebugInfo;
    public boolean showDebugProfilerChart;
    public boolean field_181657_aC;

    /**
     * The lastServer string.
     */
    public String lastServer;

    /**
     * Smooth Camera Toggle
     */
    public boolean smoothCamera;
    public boolean debugCamEnable;
    public float fovSetting;
    public float gammaSetting;
    public float saturation;

    /**
     * GUI scale
     */
    public int guiScale;

    /**
     * Determines amount of particles. 0 = All, 1 = Decreased, 2 = Minimal
     */
    public int particleSetting;

    /**
     * Game settings language
     */
    public String language;
    public boolean forceUnicodeFont;
    public int ofFogType = 1;
    public float ofFogStart = 0.8F;
    public int ofMipmapType = 0;
    public boolean ofOcclusionFancy = false;
    public boolean ofSmoothFps = false;
    public boolean ofSmoothWorld = Config.isSingleProcessor();
    public boolean ofLazyChunkLoading = Config.isSingleProcessor();
    public boolean ofRenderRegions = false;
    public boolean ofSmartAnimations = false;
    public float ofAoLevel = 1.0F;
    public int ofAaLevel = 0;
    public int ofAfLevel = 1;
    public int ofClouds = 0;
    public float ofCloudsHeight = 0.0F;
    public int ofTrees = 0;
    public int ofRain = 0;
    public int ofDroppedItems = 0;
    public int ofBetterGrass = 3;
    public int ofAutoSaveTicks = 4000;
    public boolean ofLagometer = false;
    public boolean ofProfiler = false;
    public boolean ofShowFps = false;
    public boolean ofWeather = true;
    public boolean ofSky = true;
    public boolean ofStars = true;
    public boolean ofSunMoon = true;
    public int ofVignette = 0;
    public int ofChunkUpdates = 1;
    public boolean ofChunkUpdatesDynamic = false;
    public int ofTime = 0;
    public boolean ofClearWater = false;
    public boolean ofBetterSnow = false;
    public String ofFullscreenMode = "Default";
    public boolean ofSwampColors = true;
    public boolean ofRandomEntities = true;
    public boolean ofSmoothBiomes = true;
    public boolean ofCustomFonts = true;
    public boolean ofCustomColors = true;
    public boolean ofCustomSky = true;
    public boolean ofShowCapes = true;
    public int ofConnectedTextures = 2;
    public boolean ofCustomItems = true;
    public boolean ofNaturalTextures = false;
    public boolean ofEmissiveTextures = true;
    public boolean ofFastMath = false;
    public boolean ofFastRender = false;
    public int ofTranslucentBlocks = 0;
    public boolean ofDynamicFov = true;
    public boolean ofAlternateBlocks = true;
    public int ofDynamicLights = 3;
    public boolean ofCustomEntityModels = true;
    public boolean ofCustomGuis = true;
    public boolean ofShowGlErrors = true;
    public int ofScreenshotSize = 1;
    public int ofAnimatedWater = 0;
    public int ofAnimatedLava = 0;
    public boolean ofAnimatedFire = true;
    public boolean ofAnimatedPortal = true;
    public boolean ofAnimatedRedstone = true;
    public boolean ofAnimatedExplosion = true;
    public boolean ofAnimatedFlame = true;
    public boolean ofAnimatedSmoke = true;
    public boolean ofVoidParticles = true;
    public boolean ofWaterParticles = true;
    public boolean ofRainSplash = true;
    public boolean ofPortalParticles = true;
    public boolean ofPotionParticles = true;
    public boolean ofFireworkParticles = true;
    public boolean ofDrippingWaterLava = true;
    public boolean ofAnimatedTerrain = true;
    public boolean ofAnimatedTextures = true;
    public static final int DEFAULT = 0;
    public static final int FAST = 1;
    public static final int FANCY = 2;
    public static final int OFF = 3;
    public static final int SMART = 4;
    public static final int ANIM_ON = 0;
    public static final int ANIM_GENERATED = 1;
    public static final int ANIM_OFF = 2;
    public static final String DEFAULT_STR = "Default";
    private static final int[] OF_TREES_VALUES = new int[]{0, 1, 4, 2};
    private static final int[] OF_DYNAMIC_LIGHTS = new int[]{3, 1, 2};
    private static final String[] KEYS_DYNAMIC_LIGHTS = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
    public KeyBinding ofKeyBindZoom;
    private File optionsFileOF;

    public GameSettings(final Minecraft mcIn, final File p_i46326_2_) {
        this.keyBindings = ArrayUtils.addAll(new KeyBinding[]{this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindStreamStartStop, this.keyBindStreamPauseUnpause, this.keyBindStreamCommercials, this.keyBindStreamToggleMic, this.keyBindFullscreen, this.keyBindSpectatorOutlines}, this.keyBindsHotbar);
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.fovSetting = 70.0F;
        this.language = "en_US";
        this.forceUnicodeFont = false;
        this.mc = mcIn;
        this.optionsFile = new File(p_i46326_2_, "options.txt");

        if (mcIn.isJava64bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
            GameSettings.Options.RENDER_DISTANCE.setValueMax(32.0F);
            final long i = 1000000L;

            if (Runtime.getRuntime().maxMemory() >= 1500L * i) {
                GameSettings.Options.RENDER_DISTANCE.setValueMax(48.0F);
            }

            if (Runtime.getRuntime().maxMemory() >= 2500L * i) {
                GameSettings.Options.RENDER_DISTANCE.setValueMax(64.0F);
            }
        } else {
            GameSettings.Options.RENDER_DISTANCE.setValueMax(16.0F);
        }

        this.renderDistanceChunks = mcIn.isJava64bit() ? 12 : 8;
        this.optionsFileOF = new File(p_i46326_2_, "optionsof.txt");
        this.limitFramerate = (int) GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
        this.ofKeyBindZoom = new KeyBinding("of.key.zoom", 46, "key.categories.misc");
        this.keyBindings = ArrayUtils.add(this.keyBindings, this.ofKeyBindZoom);
        KeyUtils.fixKeyConflicts(this.keyBindings, new KeyBinding[]{this.ofKeyBindZoom});
        this.renderDistanceChunks = 8;
        this.loadOptions();
        Config.initGameSettings(this);
    }

    public GameSettings() {
        this.keyBindings = ArrayUtils.addAll(new KeyBinding[]{this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindStreamStartStop, this.keyBindStreamPauseUnpause, this.keyBindStreamCommercials, this.keyBindStreamToggleMic, this.keyBindFullscreen, this.keyBindSpectatorOutlines}, this.keyBindsHotbar);
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.fovSetting = 70.0F;
        this.language = "en_US";
        this.forceUnicodeFont = false;
    }

    /**
     * Represents a key or mouse button as a string. Args: key
     */
    public static String getKeyDisplayString(final int p_74298_0_) {
        return p_74298_0_ < 0 ? I18n.format("key.mouseButton", p_74298_0_ + 101) : (p_74298_0_ < 256 ? Keyboard.getKeyName(p_74298_0_) : String.format("%c", (char) (p_74298_0_ - 256)).toUpperCase());
    }

    /**
     * Returns whether the specified key binding is currently being pressed.
     */
    public static boolean isKeyDown(final KeyBinding p_100015_0_) {
        return p_100015_0_.getKeyCode() != 0 && (p_100015_0_.getKeyCode() < 0 ? Mouse.isButtonDown(p_100015_0_.getKeyCode() + 100) : Keyboard.isKeyDown(p_100015_0_.getKeyCode()));
    }

    /**
     * Sets a key binding and then saves all settings.
     */
    public void setOptionKeyBinding(final KeyBinding p_151440_1_, final int p_151440_2_) {
        p_151440_1_.setKeyCode(p_151440_2_);
        this.saveOptions();
    }

    /**
     * If the specified option is controlled by a slider (float value), this will set the float value.
     */
    public void setOptionFloatValue(final GameSettings.Options p_74304_1_, final float p_74304_2_) {
        this.setOptionFloatValueOF(p_74304_1_, p_74304_2_);

        switch (p_74304_1_) {
            case SENSITIVITY:
                this.mouseSensitivity = p_74304_2_;
                break;

            case FOV:
                this.fovSetting = p_74304_2_;
                break;

            case GAMMA:
                this.gammaSetting = p_74304_2_;
                break;

            case FRAMERATE_LIMIT:
                this.limitFramerate = (int) p_74304_2_;
                this.enableVsync = false;

                if (this.limitFramerate <= 0) {
                    this.limitFramerate = (int) GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                    this.enableVsync = true;
                }

                this.updateVSync();
                break;

            case CHAT_OPACITY:
                this.chatOpacity = p_74304_2_;
                this.mc.ingameGUI.getChatGUI().refreshChat();
                break;

            case CHAT_HEIGHT_FOCUSED:
                this.chatHeightFocused = p_74304_2_;
                this.mc.ingameGUI.getChatGUI().refreshChat();
                break;

            case CHAT_HEIGHT_UNFOCUSED:
                this.chatHeightUnfocused = p_74304_2_;
                this.mc.ingameGUI.getChatGUI().refreshChat();
                break;

            case CHAT_WIDTH:
                this.chatWidth = p_74304_2_;
                this.mc.ingameGUI.getChatGUI().refreshChat();
                break;

            case CHAT_SCALE:
                this.chatScale = p_74304_2_;
                this.mc.ingameGUI.getChatGUI().refreshChat();
                break;

            case MIPMAP_LEVELS:
                final int i = this.mipmapLevels;
                this.mipmapLevels = (int) p_74304_2_;

                if ((float) i != p_74304_2_) {
                    this.mc.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
                    this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                    this.mc.getTextureMapBlocks().setBlurMipmapDirect(false, this.mipmapLevels > 0);
                    this.mc.scheduleResourcesRefresh();
                }
                break;

            case BLOCK_ALTERNATIVES:
                this.allowBlockAlternatives = !this.allowBlockAlternatives;
                this.mc.renderGlobal.loadRenderers();
                break;

            case RENDER_DISTANCE:
                this.renderDistanceChunks = (int) p_74304_2_;
                this.mc.renderGlobal.setDisplayListEntitiesDirty();
                break;
        }
    }

    /**
     * For non-float options. Toggles the option on/off, or cycles through the list i.e. render distances.
     */
    public void setOptionValue(final GameSettings.Options p_74306_1_, final int p_74306_2_) {
        this.setOptionValueOF(p_74306_1_);

        switch (p_74306_1_) {
            case INVERT_MOUSE:
                this.invertMouse = !this.invertMouse;
                break;

            case GUI_SCALE:
                this.guiScale += p_74306_2_;

                if (GuiScreen.isShiftKeyDown()) {
                    this.guiScale = 0;
                }

                final DisplayMode displaymode = Config.getLargestDisplayMode();
                final int i = displaymode.getWidth() / 320;
                final int j = displaymode.getHeight() / 240;
                final int k = Math.min(i, j);

                if (this.guiScale < 0) {
                    this.guiScale = k - 1;
                }

                if (this.mc.isUnicode() && this.guiScale % 2 != 0) {
                    this.guiScale += p_74306_2_;
                }

                if (this.guiScale < 0 || this.guiScale >= k) {
                    this.guiScale = 0;
                }
                break;

            case PARTICLES:
                this.particleSetting = (this.particleSetting + p_74306_2_) % 3;
                break;

            case VIEW_BOBBING:
                this.viewBobbing = !this.viewBobbing;
                break;

            case RENDER_CLOUDS:
                this.clouds = (this.clouds + p_74306_2_) % 3;
                break;

            case FORCE_UNICODE_FONT:
                this.forceUnicodeFont = !this.forceUnicodeFont;
                this.mc.fontRendererObj.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
                break;

            case FBO_ENABLE:
                this.fboEnable = !this.fboEnable;
                break;

            case ANAGLYPH:
                if (!this.anaglyph && Config.isShaders()) {
                    Config.showGuiMessage(Lang.get("of.message.an.shaders1"), Lang.get("of.message.an.shaders2"));
                    return;
                }

                this.anaglyph = !this.anaglyph;
                this.mc.refreshResources();
                break;

            case GRAPHICS:
                this.fancyGraphics = !this.fancyGraphics;
                this.updateRenderClouds();
                this.mc.renderGlobal.loadRenderers();
                break;

            case AMBIENT_OCCLUSION:
                this.ambientOcclusion = (this.ambientOcclusion + p_74306_2_) % 3;
                this.mc.renderGlobal.loadRenderers();
                break;

            case CHAT_VISIBILITY:
                this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + p_74306_2_) % 3);
                break;

            case CHAT_COLOR:
                this.chatColours = !this.chatColours;
                break;

            case CHAT_LINKS:
                this.chatLinks = !this.chatLinks;
                break;

            case CHAT_LINKS_PROMPT:
                this.chatLinksPrompt = !this.chatLinksPrompt;
                break;

            case SNOOPER_ENABLED:
                this.snooperEnabled = !this.snooperEnabled;
                break;

            case TOUCHSCREEN:
                this.touchscreen = !this.touchscreen;
                break;

            case USE_FULLSCREEN:
                this.fullScreen = !this.fullScreen;

                if (this.mc.isFullScreen() != this.fullScreen) {
                    this.mc.toggleFullscreen();
                }
                break;

            case ENABLE_VSYNC:
                this.enableVsync = !this.enableVsync;
                Display.setVSyncEnabled(this.enableVsync);
                break;

            case USE_VBO:
                this.useVbo = !this.useVbo;
                this.mc.renderGlobal.loadRenderers();
                break;

            case BLOCK_ALTERNATIVES:
                this.allowBlockAlternatives = !this.allowBlockAlternatives;
                this.mc.renderGlobal.loadRenderers();
                break;

            case REDUCED_DEBUG_INFO:
                this.reducedDebugInfo = !this.reducedDebugInfo;
                break;

            case ENTITY_SHADOWS:
                this.field_181151_V = !this.field_181151_V;
                break;

            case REALMS_NOTIFICATIONS:
                this.field_183509_X = !this.field_183509_X;
                break;
        }

        this.saveOptions();
    }

    public float getOptionFloatValue(final GameSettings.Options p_74296_1_) {
        final float f = this.getOptionFloatValueOF(p_74296_1_);
        return f != Float.MAX_VALUE ? f : (p_74296_1_ == GameSettings.Options.FOV ? this.fovSetting : (p_74296_1_ == GameSettings.Options.GAMMA ? this.gammaSetting : (p_74296_1_ == GameSettings.Options.SATURATION ? this.saturation : (p_74296_1_ == GameSettings.Options.SENSITIVITY ? this.mouseSensitivity : (p_74296_1_ == GameSettings.Options.CHAT_OPACITY ? this.chatOpacity : (p_74296_1_ == GameSettings.Options.CHAT_HEIGHT_FOCUSED ? this.chatHeightFocused : (p_74296_1_ == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED ? this.chatHeightUnfocused : (p_74296_1_ == GameSettings.Options.CHAT_SCALE ? this.chatScale : (p_74296_1_ == GameSettings.Options.CHAT_WIDTH ? this.chatWidth : (p_74296_1_ == GameSettings.Options.FRAMERATE_LIMIT ? (float) this.limitFramerate : (p_74296_1_ == GameSettings.Options.MIPMAP_LEVELS ? (float) this.mipmapLevels : (p_74296_1_ == GameSettings.Options.RENDER_DISTANCE ? (float) this.renderDistanceChunks : 0.0F))))))))))));
    }

    public boolean getOptionOrdinalValue(final GameSettings.Options p_74308_1_) {
        switch (p_74308_1_) {
            case INVERT_MOUSE:
                return this.invertMouse;

            case VIEW_BOBBING:
                return this.viewBobbing;

            case ANAGLYPH:
                return this.anaglyph;

            case FBO_ENABLE:
                return this.fboEnable;

            case CHAT_COLOR:
                return this.chatColours;

            case CHAT_LINKS:
                return this.chatLinks;

            case CHAT_LINKS_PROMPT:
                return this.chatLinksPrompt;

            case SNOOPER_ENABLED:
                return this.snooperEnabled;

            case USE_FULLSCREEN:
                return this.fullScreen;

            case ENABLE_VSYNC:
                return this.enableVsync;

            case USE_VBO:
                return this.useVbo;

            case TOUCHSCREEN:
                return this.touchscreen;

            case FORCE_UNICODE_FONT:
                return this.forceUnicodeFont;

            case BLOCK_ALTERNATIVES:
                return this.allowBlockAlternatives;

            case REDUCED_DEBUG_INFO:
                return this.reducedDebugInfo;

            case ENTITY_SHADOWS:
                return this.field_181151_V;

            case REALMS_NOTIFICATIONS:
                return this.field_183509_X;

            default:
                return false;
        }
    }

    /**
     * Returns the translation of the given index in the given String array. If the index is smaller than 0 or greater
     * than/equal to the length of the String array, it is changed to 0.
     */
    private static String getTranslation(final String[] p_74299_0_, int p_74299_1_) {
        if (p_74299_1_ < 0 || p_74299_1_ >= p_74299_0_.length) {
            p_74299_1_ = 0;
        }

        return I18n.format(p_74299_0_[p_74299_1_]);
    }

    /**
     * Gets a key binding.
     */
    public String getKeyBinding(final GameSettings.Options p_74297_1_) {
        final String s = this.getKeyBindingOF(p_74297_1_);

        if (s != null) {
            return s;
        } else {
            final String s1 = I18n.format(p_74297_1_.getEnumString()) + ": ";

            if (p_74297_1_.getEnumFloat()) {
                final float f1 = this.getOptionFloatValue(p_74297_1_);
                final float f = p_74297_1_.normalizeValue(f1);
                return p_74297_1_ == GameSettings.Options.MIPMAP_LEVELS && (double) f1 >= 4.0D ? s1 + Lang.get("of.general.max") : (p_74297_1_ == GameSettings.Options.SENSITIVITY ? (f == 0.0F ? s1 + I18n.format("options.sensitivity.min") : (f == 1.0F ? s1 + I18n.format("options.sensitivity.max") : s1 + (int) (f * 200.0F) + "%")) : (p_74297_1_ == GameSettings.Options.FOV ? (f1 == 70.0F ? s1 + I18n.format("options.fov.min") : (f1 == 110.0F ? s1 + I18n.format("options.fov.max") : s1 + (int) f1)) : (p_74297_1_ == GameSettings.Options.FRAMERATE_LIMIT ? (f1 == p_74297_1_.valueMax ? s1 + I18n.format("options.framerateLimit.max") : s1 + (int) f1 + " fps") : (p_74297_1_ == GameSettings.Options.RENDER_CLOUDS ? (f1 == p_74297_1_.valueMin ? s1 + I18n.format("options.cloudHeight.min") : s1 + ((int) f1 + 128)) : (p_74297_1_ == GameSettings.Options.GAMMA ? (f == 0.0F ? s1 + I18n.format("options.gamma.min") : (f == 1.0F ? s1 + I18n.format("options.gamma.max") : s1 + "+" + (int) (f * 100.0F) + "%")) : (p_74297_1_ == GameSettings.Options.SATURATION ? s1 + (int) (f * 400.0F) + "%" : (p_74297_1_ == GameSettings.Options.CHAT_OPACITY ? s1 + (int) (f * 90.0F + 10.0F) + "%" : (p_74297_1_ == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED ? s1 + GuiNewChat.calculateChatboxHeight(f) + "px" : (p_74297_1_ == GameSettings.Options.CHAT_HEIGHT_FOCUSED ? s1 + GuiNewChat.calculateChatboxHeight(f) + "px" : (p_74297_1_ == GameSettings.Options.CHAT_WIDTH ? s1 + GuiNewChat.calculateChatboxWidth(f) + "px" : (p_74297_1_ == GameSettings.Options.RENDER_DISTANCE ? s1 + (int) f1 + " chunks" : (p_74297_1_ == GameSettings.Options.MIPMAP_LEVELS ? (f1 == 0.0F ? s1 + I18n.format("options.off") : s1 + (int) f1) : (f == 0.0F ? s1 + I18n.format("options.off") : s1 + (int) (f * 100.0F) + "%")))))))))))));
            } else if (p_74297_1_.getEnumBoolean()) {
                final boolean flag = this.getOptionOrdinalValue(p_74297_1_);
                return flag ? s1 + I18n.format("options.on") : s1 + I18n.format("options.off");
            } else {
                switch (p_74297_1_) {
                    case GUI_SCALE:
                        return this.guiScale >= GUISCALES.length ? s1 + this.guiScale + "x" : s1 + getTranslation(GUISCALES, this.guiScale);

                    case CHAT_VISIBILITY:
                        return s1 + I18n.format(this.chatVisibility.getResourceKey());

                    case PARTICLES:
                        return s1 + getTranslation(PARTICLES, this.particleSetting);

                    case AMBIENT_OCCLUSION:
                        return s1 + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);

                    case RENDER_CLOUDS:
                        return s1 + getTranslation(field_181149_aW, this.clouds);

                    case GRAPHICS:
                        if (this.fancyGraphics) {
                            return s1 + I18n.format("options.graphics.fancy");
                        } else {
                            return s1 + I18n.format("options.graphics.fast");
                        }

                    default:
                        return s1;
                }
            }
        }
    }

    /**
     * Loads the options from the options file. It appears that this has replaced the previous 'loadOptions'
     */
    public void loadOptions() {
        FileInputStream fileinputstream = null;
        label2:
        {
            try {
                if (this.optionsFile.exists()) {
                    final BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(fileinputstream = new FileInputStream(this.optionsFile)));
                    String s;
                    this.mapSoundLevels.clear();

                    while ((s = bufferedreader.readLine()) != null) {
                        try {
                            final String[] astring = s.split(":");

                            switch (astring[0]) {
                                case "mouseSensitivity":
                                    this.mouseSensitivity = this.parseFloat(astring[1]);
                                    break;

                                case "fov":
                                    this.fovSetting = this.parseFloat(astring[1]) * 40.0F + 70.0F;
                                    break;

                                case "gamma":
                                    this.gammaSetting = this.parseFloat(astring[1]);
                                    break;

                                case "saturation":
                                    this.saturation = this.parseFloat(astring[1]);
                                    break;

                                case "invertYMouse":
                                    this.invertMouse = astring[1].equals("true");
                                    break;

                                case "renderDistance":
                                    this.renderDistanceChunks = Integer.parseInt(astring[1]);
                                    break;

                                case "guiScale":
                                    this.guiScale = Integer.parseInt(astring[1]);
                                    break;

                                case "particles":
                                    this.particleSetting = Integer.parseInt(astring[1]);
                                    break;

                                case "bobView":
                                    this.viewBobbing = astring[1].equals("true");
                                    break;

                                case "anaglyph3d":
                                    this.anaglyph = astring[1].equals("true");
                                    break;

                                case "maxFps":
                                    this.limitFramerate = Integer.parseInt(astring[1]);

                                    if (this.enableVsync) {
                                        this.limitFramerate = (int) GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                                    }

                                    if (this.limitFramerate <= 0) {
                                        this.limitFramerate = (int) GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                                    }
                                    break;

                                case "fboEnable":
                                    this.fboEnable = astring[1].equals("true");
                                    break;

                                case "difficulty":
                                    this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(astring[1]));
                                    break;

                                case "fancyGraphics":
                                    this.fancyGraphics = astring[1].equals("true");
                                    this.updateRenderClouds();
                                    break;

                                case "ao":
                                    switch (astring[1]) {
                                        case "true":
                                            this.ambientOcclusion = 2;
                                            break;

                                        case "false":
                                            this.ambientOcclusion = 0;
                                            break;

                                        default:
                                            this.ambientOcclusion = Integer.parseInt(astring[1]);
                                            break;
                                    }
                                    break;

                                case "renderClouds":
                                    switch (astring[1]) {
                                        case "true":
                                            this.clouds = 2;
                                            break;

                                        case "false":
                                            this.clouds = 0;
                                            break;

                                        case "fast":
                                            this.clouds = 1;
                                            break;
                                    }
                                    break;

                                case "resourcePacks":
                                    this.resourcePacks = gson.fromJson(s.substring(s.indexOf(58) + 1), typeListString);

                                    if (this.resourcePacks == null) {
                                        this.resourcePacks = Lists.newArrayList();
                                    }
                                    break;

                                case "incompatibleResourcePacks":
                                    this.field_183018_l = gson.fromJson(s.substring(s.indexOf(58) + 1), typeListString);

                                    if (this.field_183018_l == null) {
                                        this.field_183018_l = Lists.newArrayList();
                                    }
                                    break;

                                case "lastServer":
                                    if (astring.length >= 2)
                                        this.lastServer = s.substring(s.indexOf(58) + 1);
                                    break;

                                case "lang":
                                    if (astring.length >= 2)
                                        this.language = astring[1];
                                    break;

                                case "chatVisibility":
                                    this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(astring[1]));
                                    break;

                                case "chatColors":
                                    this.chatColours = astring[1].equals("true");
                                    break;

                                case "chatLinks":
                                    this.chatLinks = astring[1].equals("true");
                                    break;

                                case "chatLinksPrompt":
                                    this.chatLinksPrompt = astring[1].equals("true");
                                    break;

                                case "chatOpacity":
                                    this.chatOpacity = this.parseFloat(astring[1]);
                                    break;

                                case "snooperEnabled":
                                    this.snooperEnabled = astring[1].equals("true");
                                    break;

                                case "fullscreen":
                                    this.fullScreen = astring[1].equals("true");
                                    break;

                                case "enableVsync":
                                    this.enableVsync = astring[1].equals("true");

                                    if (this.enableVsync) {
                                        this.limitFramerate = (int) GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                                    }

                                    this.updateVSync();
                                    break;

                                case "useVbo":
                                    this.useVbo = astring[1].equals("true");
                                    break;

                                case "hideServerAddress":
                                    this.hideServerAddress = astring[1].equals("true");
                                    break;

                                case "advancedItemTooltips":
                                    this.advancedItemTooltips = astring[1].equals("true");
                                    break;

                                case "pauseOnLostFocus":
                                    this.pauseOnLostFocus = astring[1].equals("true");
                                    break;

                                case "touchscreen":
                                    this.touchscreen = astring[1].equals("true");
                                    break;

                                case "overrideHeight":
                                    this.overrideHeight = Integer.parseInt(astring[1]);
                                    break;

                                case "overrideWidth":
                                    this.overrideWidth = Integer.parseInt(astring[1]);
                                    break;

                                case "heldItemTooltips":
                                    this.heldItemTooltips = astring[1].equals("true");
                                    break;

                                case "chatHeightFocused":
                                    this.chatHeightFocused = this.parseFloat(astring[1]);
                                    break;

                                case "chatHeightUnfocused":
                                    this.chatHeightUnfocused = this.parseFloat(astring[1]);
                                    break;

                                case "chatScale":
                                    this.chatScale = this.parseFloat(astring[1]);
                                    break;

                                case "chatWidth":
                                    this.chatWidth = this.parseFloat(astring[1]);
                                    break;

                                case "showInventoryAchievementHint":
                                    this.showInventoryAchievementHint = astring[1].equals("true");
                                    break;

                                case "mipmapLevels":
                                    this.mipmapLevels = Integer.parseInt(astring[1]);
                                    break;

                                case "streamBytesPerPixel":
                                    this.streamBytesPerPixel = this.parseFloat(astring[1]);
                                    break;

                                case "streamMicVolume":
                                    this.streamMicVolume = this.parseFloat(astring[1]);
                                    break;

                                case "streamSystemVolume":
                                    this.streamGameVolume = this.parseFloat(astring[1]);
                                    break;

                                case "streamKbps":
                                    this.streamKbps = this.parseFloat(astring[1]);
                                    break;

                                case "streamFps":
                                    this.streamFps = this.parseFloat(astring[1]);
                                    break;

                                case "streamCompression":
                                    this.streamCompression = Integer.parseInt(astring[1]);
                                    break;

                                case "streamSendMetadata":
                                    this.streamSendMetadata = astring[1].equals("true");
                                    break;

                                case "streamPreferredServer":
                                    if (astring.length >= 2)
                                        this.streamPreferredServer = s.substring(s.indexOf(58) + 1);
                                    break;

                                case "streamChatEnabled":
                                    this.streamChatEnabled = Integer.parseInt(astring[1]);
                                    break;

                                case "streamChatUserFilter":
                                    this.streamChatUserFilter = Integer.parseInt(astring[1]);
                                    break;

                                case "streamMicToggleBehavior":
                                    this.streamMicToggleBehavior = Integer.parseInt(astring[1]);
                                    break;

                                case "forceUnicodeFont":
                                    this.forceUnicodeFont = astring[1].equals("true");
                                    break;

                                case "allowBlockAlternatives":
                                    this.allowBlockAlternatives = astring[1].equals("true");
                                    break;

                                case "reducedDebugInfo":
                                    this.reducedDebugInfo = astring[1].equals("true");
                                    break;

                                case "useNativeTransport":
                                    this.field_181150_U = astring[1].equals("true");
                                    break;

                                case "entityShadows":
                                    this.field_181151_V = astring[1].equals("true");
                                    break;

                                case "realmsNotifications":
                                    this.field_183509_X = astring[1].equals("true");
                                    break;
                            }

                            for (final KeyBinding keybinding : this.keyBindings) {
                                if (astring[0].equals("key_" + keybinding.getKeyDescription())) {
                                    keybinding.setKeyCode(Integer.parseInt(astring[1]));
                                }
                            }

                            for (final SoundCategory soundcategory : SoundCategory.values()) {
                                if (astring[0].equals("soundCategory_" + soundcategory.getCategoryName())) {
                                    this.mapSoundLevels.put(soundcategory, this.parseFloat(astring[1]));
                                }
                            }

                            for (final EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values()) {
                                if (astring[0].equals("modelPart_" + enumplayermodelparts.getPartName())) {
                                    this.setModelPartEnabled(enumplayermodelparts, astring[1].equals("true"));
                                }
                            }
                        } catch (final Exception exception) {
                            logger.warn("Skipping bad option: " + s);
                            exception.printStackTrace();
                        }
                    }

                    KeyBinding.resetKeyBindingArrayAndHash();
                    bufferedreader.close();
                    break label2;
                }
            } catch (final Exception exception1) {
                logger.error("Failed to load options", exception1);
                break label2;
            } finally {
                IOUtils.closeQuietly(fileinputstream);
            }

            return;
        }
        this.loadOfOptions();
    }

    /**
     * Parses a string into a float.
     */
    private float parseFloat(final String p_74305_1_) {
        return p_74305_1_.equals("true") ? 1.0F : (p_74305_1_.equals("false") ? 0.0F : Float.parseFloat(p_74305_1_));
    }

    /**
     * Saves the options to the options file.
     */
    public void saveOptions() {
        if (Reflector.FMLClientHandler.exists()) {
            final Object object = Reflector.call(Reflector.FMLClientHandler_instance);

            if (object != null && Reflector.callBoolean(object, Reflector.FMLClientHandler_isLoading)) {
                return;
            }
        }

        try {
            final PrintWriter printwriter = new PrintWriter(new FileWriter(this.optionsFile));
            printwriter.println("invertYMouse:" + this.invertMouse);
            printwriter.println("mouseSensitivity:" + this.mouseSensitivity);
            printwriter.println("fov:" + (this.fovSetting - 70.0F) / 40.0F);
            printwriter.println("gamma:" + this.gammaSetting);
            printwriter.println("saturation:" + this.saturation);
            printwriter.println("renderDistance:" + this.renderDistanceChunks);
            printwriter.println("guiScale:" + this.guiScale);
            printwriter.println("particles:" + this.particleSetting);
            printwriter.println("bobView:" + this.viewBobbing);
            printwriter.println("anaglyph3d:" + this.anaglyph);
            printwriter.println("maxFps:" + this.limitFramerate);
            printwriter.println("fboEnable:" + this.fboEnable);
            printwriter.println("difficulty:" + this.difficulty.getDifficultyId());
            printwriter.println("fancyGraphics:" + this.fancyGraphics);
            printwriter.println("ao:" + this.ambientOcclusion);

            switch (this.clouds) {
                case 0:
                    printwriter.println("renderClouds:false");
                    break;

                case 1:
                    printwriter.println("renderClouds:fast");
                    break;

                case 2:
                    printwriter.println("renderClouds:true");
            }

            printwriter.println("resourcePacks:" + gson.toJson(this.resourcePacks));
            printwriter.println("incompatibleResourcePacks:" + gson.toJson(this.field_183018_l));
            printwriter.println("lastServer:" + this.lastServer);
            printwriter.println("lang:" + this.language);
            printwriter.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
            printwriter.println("chatColors:" + this.chatColours);
            printwriter.println("chatLinks:" + this.chatLinks);
            printwriter.println("chatLinksPrompt:" + this.chatLinksPrompt);
            printwriter.println("chatOpacity:" + this.chatOpacity);
            printwriter.println("snooperEnabled:" + this.snooperEnabled);
            printwriter.println("fullscreen:" + this.fullScreen);
            printwriter.println("enableVsync:" + this.enableVsync);
            printwriter.println("useVbo:" + this.useVbo);
            printwriter.println("hideServerAddress:" + this.hideServerAddress);
            printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
            printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
            printwriter.println("touchscreen:" + this.touchscreen);
            printwriter.println("overrideWidth:" + this.overrideWidth);
            printwriter.println("overrideHeight:" + this.overrideHeight);
            printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
            printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
            printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
            printwriter.println("chatScale:" + this.chatScale);
            printwriter.println("chatWidth:" + this.chatWidth);
            printwriter.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
            printwriter.println("mipmapLevels:" + this.mipmapLevels);
            printwriter.println("streamBytesPerPixel:" + this.streamBytesPerPixel);
            printwriter.println("streamMicVolume:" + this.streamMicVolume);
            printwriter.println("streamSystemVolume:" + this.streamGameVolume);
            printwriter.println("streamKbps:" + this.streamKbps);
            printwriter.println("streamFps:" + this.streamFps);
            printwriter.println("streamCompression:" + this.streamCompression);
            printwriter.println("streamSendMetadata:" + this.streamSendMetadata);
            printwriter.println("streamPreferredServer:" + this.streamPreferredServer);
            printwriter.println("streamChatEnabled:" + this.streamChatEnabled);
            printwriter.println("streamChatUserFilter:" + this.streamChatUserFilter);
            printwriter.println("streamMicToggleBehavior:" + this.streamMicToggleBehavior);
            printwriter.println("forceUnicodeFont:" + this.forceUnicodeFont);
            printwriter.println("allowBlockAlternatives:" + this.allowBlockAlternatives);
            printwriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
            printwriter.println("useNativeTransport:" + this.field_181150_U);
            printwriter.println("entityShadows:" + this.field_181151_V);
            printwriter.println("realmsNotifications:" + this.field_183509_X);

            for (final KeyBinding keybinding : this.keyBindings) {
                printwriter.println("key_" + keybinding.getKeyDescription() + ":" + keybinding.getKeyCode());
            }

            for (final SoundCategory soundcategory : SoundCategory.values()) {
                printwriter.println("soundCategory_" + soundcategory.getCategoryName() + ":" + this.getSoundLevel(soundcategory));
            }

            for (final EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values()) {
                printwriter.println("modelPart_" + enumplayermodelparts.getPartName() + ":" + this.setModelParts.contains(enumplayermodelparts));
            }

            printwriter.close();
        } catch (final Exception exception) {
            logger.error("Failed to save options", exception);
        }

        this.saveOfOptions();
        this.sendSettingsToServer();
    }

    public float getSoundLevel(final SoundCategory p_151438_1_) {
        return this.mapSoundLevels.getOrDefault(p_151438_1_, 1.0F);
    }

    public void setSoundLevel(final SoundCategory p_151439_1_, final float p_151439_2_) {
        this.mc.getSoundHandler().setSoundLevel(p_151439_1_, p_151439_2_);
        this.mapSoundLevels.put(p_151439_1_, p_151439_2_);
    }

    /**
     * Send a client info packet with settings information to the server
     */
    public void sendSettingsToServer() {
        if (this.mc.thePlayer != null) {
            int i = 0;

            for (final EnumPlayerModelParts enumplayermodelparts : this.setModelParts) {
                i |= enumplayermodelparts.getPartMask();
            }

            this.mc.thePlayer.sendQueue.addToSendQueue(new C15PacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, i));
        }
    }

    public Set<EnumPlayerModelParts> getModelParts() {
        return ImmutableSet.copyOf(this.setModelParts);
    }

    public void setModelPartEnabled(final EnumPlayerModelParts p_178878_1_, final boolean p_178878_2_) {
        if (p_178878_2_) {
            this.setModelParts.add(p_178878_1_);
        } else {
            this.setModelParts.remove(p_178878_1_);
        }

        this.sendSettingsToServer();
    }

    public void switchModelPartEnabled(final EnumPlayerModelParts p_178877_1_) {
        if (!this.getModelParts().contains(p_178877_1_)) {
            this.setModelParts.add(p_178877_1_);
        } else {
            this.setModelParts.remove(p_178877_1_);
        }

        this.sendSettingsToServer();
    }

    public int func_181147_e() {
        return this.renderDistanceChunks >= 4 ? this.clouds : 0;
    }

    public boolean func_181148_f() {
        return this.field_181150_U;
    }

    private void setOptionFloatValueOF(final GameSettings.Options p_setOptionFloatValueOF_1_, final float p_setOptionFloatValueOF_2_) {
        switch (p_setOptionFloatValueOF_1_) {
            case CLOUD_HEIGHT:
                this.ofCloudsHeight = p_setOptionFloatValueOF_2_;
                this.mc.renderGlobal.resetClouds();
                break;

            case AO_LEVEL:
                this.ofAoLevel = p_setOptionFloatValueOF_2_;
                this.mc.renderGlobal.loadRenderers();
                break;

            case AA_LEVEL:
                final int i = (int) p_setOptionFloatValueOF_2_;

                if (i > 0 && Config.isShaders()) {
                    Config.showGuiMessage(Lang.get("of.message.aa.shaders1"), Lang.get("of.message.aa.shaders2"));
                    return;
                }

                final int[] aint = new int[]{0, 2, 4, 6, 8, 12, 16};
                this.ofAaLevel = 0;

                for (final int k : aint) {
                    if (i >= k) {
                        this.ofAaLevel = k;
                    }
                }

                this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
                break;

            case AF_LEVEL:
                final int k = (int) p_setOptionFloatValueOF_2_;

                if (k > 1 && Config.isShaders()) {
                    Config.showGuiMessage(Lang.get("of.message.af.shaders1"), Lang.get("of.message.af.shaders2"));
                    return;
                }

                this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
                this.mc.refreshResources();
                break;

            case MIPMAP_TYPE:
                final int l = (int) p_setOptionFloatValueOF_2_;
                this.ofMipmapType = Config.limit(l, 0, 3);
                this.mc.refreshResources();
                break;

            case FULLSCREEN_MODE:
                final int i1 = (int) p_setOptionFloatValueOF_2_ - 1;
                final String[] astring = Config.getDisplayModeNames();

                if (i1 < 0 || i1 >= astring.length) {
                    this.ofFullscreenMode = "Default";
                    return;
                }

                this.ofFullscreenMode = astring[i1];
                break;
        }
    }

    private float getOptionFloatValueOF(final GameSettings.Options p_getOptionFloatValueOF_1_) {
        switch (p_getOptionFloatValueOF_1_) {
            case CLOUD_HEIGHT:
                return this.ofCloudsHeight;

            case AO_LEVEL:
                return this.ofAoLevel;

            case AA_LEVEL:
                return (float) this.ofAaLevel;

            case AF_LEVEL:
                return (float) this.ofAfLevel;

            case MIPMAP_TYPE:
                return (float) this.ofMipmapType;

            case FRAMERATE_LIMIT:
                return (float) this.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() && this.enableVsync ? 0.0F : (float) this.limitFramerate;

            case FULLSCREEN_MODE:
                if (this.ofFullscreenMode.equals("Default")) {
                    return 0.0F;
                } else {
                    final List<String> list = Arrays.asList(Config.getDisplayModeNames());
                    final int i = list.indexOf(this.ofFullscreenMode);
                    return i < 0 ? 0.0F : (float) (i + 1);
                }

            default:
                return Float.MAX_VALUE;
        }
    }

    private void setOptionValueOF(final GameSettings.Options p_setOptionValueOF_1_) {
        switch (p_setOptionValueOF_1_) {
            case FOG_FANCY:
                switch (this.ofFogType) {
                    case 1:
                        this.ofFogType = 2;

                        if (!Config.isFancyFogAvailable()) {
                            this.ofFogType = 3;
                        }

                        break;

                    case 2:
                        this.ofFogType = 3;
                        break;

                    default:
                        this.ofFogType = 1;
                }
                break;

            case FOG_START:
                this.ofFogStart += 0.2F;

                if (this.ofFogStart > 0.81F) {
                    this.ofFogStart = 0.2F;
                }
                break;

            case SMOOTH_FPS:
                this.ofSmoothFps = !this.ofSmoothFps;
                break;

            case SMOOTH_WORLD:
                this.ofSmoothWorld = !this.ofSmoothWorld;
                Config.updateThreadPriorities();
                break;

            case CLOUDS:
                ++this.ofClouds;

                if (this.ofClouds > 3) {
                    this.ofClouds = 0;
                }

                this.updateRenderClouds();
                this.mc.renderGlobal.resetClouds();
                break;

            case TREES:
                this.ofTrees = nextValue(this.ofTrees, OF_TREES_VALUES);
                this.mc.renderGlobal.loadRenderers();
                break;

            case DROPPED_ITEMS:
                ++this.ofDroppedItems;

                if (this.ofDroppedItems > 2) {
                    this.ofDroppedItems = 0;
                }
                break;

            case RAIN:
                ++this.ofRain;

                if (this.ofRain > 3) {
                    this.ofRain = 0;
                }
                break;

            case ANIMATED_WATER:
                ++this.ofAnimatedWater;

                if (this.ofAnimatedWater == 1) {
                    ++this.ofAnimatedWater;
                }

                if (this.ofAnimatedWater > 2) {
                    this.ofAnimatedWater = 0;
                }
                break;

            case ANIMATED_LAVA:
                ++this.ofAnimatedLava;

                if (this.ofAnimatedLava == 1) {
                    ++this.ofAnimatedLava;
                }

                if (this.ofAnimatedLava > 2) {
                    this.ofAnimatedLava = 0;
                }
                break;

            case ANIMATED_FIRE:
                this.ofAnimatedFire = !this.ofAnimatedFire;
                break;

            case ANIMATED_PORTAL:
                this.ofAnimatedPortal = !this.ofAnimatedPortal;
                break;

            case ANIMATED_REDSTONE:
                this.ofAnimatedRedstone = !this.ofAnimatedRedstone;
                break;

            case ANIMATED_EXPLOSION:
                this.ofAnimatedExplosion = !this.ofAnimatedExplosion;
                break;

            case ANIMATED_FLAME:
                this.ofAnimatedFlame = !this.ofAnimatedFlame;
                break;

            case ANIMATED_SMOKE:
                this.ofAnimatedSmoke = !this.ofAnimatedSmoke;
                break;

            case VOID_PARTICLES:
                this.ofVoidParticles = !this.ofVoidParticles;
                break;

            case WATER_PARTICLES:
                this.ofWaterParticles = !this.ofWaterParticles;
                break;

            case PORTAL_PARTICLES:
                this.ofPortalParticles = !this.ofPortalParticles;
                break;

            case POTION_PARTICLES:
                this.ofPotionParticles = !this.ofPotionParticles;
                break;

            case FIREWORK_PARTICLES:
                this.ofFireworkParticles = !this.ofFireworkParticles;
                break;

            case DRIPPING_WATER_LAVA:
                this.ofDrippingWaterLava = !this.ofDrippingWaterLava;
                break;

            case ANIMATED_TERRAIN:
                this.ofAnimatedTerrain = !this.ofAnimatedTerrain;
                break;

            case ANIMATED_TEXTURES:
                this.ofAnimatedTextures = !this.ofAnimatedTextures;
                break;

            case RAIN_SPLASH:
                this.ofRainSplash = !this.ofRainSplash;
                break;

            case LAGOMETER:
                this.ofLagometer = !this.ofLagometer;
                break;

            case SHOW_FPS:
                this.ofShowFps = !this.ofShowFps;
                break;

            case AUTOSAVE_TICKS:
                final int i = 900;
                this.ofAutoSaveTicks = Math.max(this.ofAutoSaveTicks / i * i, i);
                this.ofAutoSaveTicks *= 2;

                if (this.ofAutoSaveTicks > 32 * i) {
                    this.ofAutoSaveTicks = i;
                }
                break;

            case BETTER_GRASS:
                ++this.ofBetterGrass;

                if (this.ofBetterGrass > 3) {
                    this.ofBetterGrass = 1;
                }

                this.mc.renderGlobal.loadRenderers();
                break;

            case CONNECTED_TEXTURES:
                ++this.ofConnectedTextures;

                if (this.ofConnectedTextures > 3) {
                    this.ofConnectedTextures = 1;
                }

                if (this.ofConnectedTextures == 2) {
                    this.mc.renderGlobal.loadRenderers();
                } else {
                    this.mc.refreshResources();
                }
                break;

            case WEATHER:
                this.ofWeather = !this.ofWeather;
                break;

            case SKY:
                this.ofSky = !this.ofSky;
                break;

            case STARS:
                this.ofStars = !this.ofStars;
                break;

            case SUN_MOON:
                this.ofSunMoon = !this.ofSunMoon;
                break;

            case VIGNETTE:
                ++this.ofVignette;

                if (this.ofVignette > 2) {
                    this.ofVignette = 0;
                }
                break;

            case CHUNK_UPDATES:
                ++this.ofChunkUpdates;

                if (this.ofChunkUpdates > 5) {
                    this.ofChunkUpdates = 1;
                }
                break;

            case CHUNK_UPDATES_DYNAMIC:
                this.ofChunkUpdatesDynamic = !this.ofChunkUpdatesDynamic;
                break;

            case TIME:
                ++this.ofTime;

                if (this.ofTime > 2) {
                    this.ofTime = 0;
                }
                break;

            case CLEAR_WATER:
                this.ofClearWater = !this.ofClearWater;
                this.updateWaterOpacity();
                break;

            case PROFILER:
                this.ofProfiler = !this.ofProfiler;
                break;

            case BETTER_SNOW:
                this.ofBetterSnow = !this.ofBetterSnow;
                this.mc.renderGlobal.loadRenderers();
                break;

            case SWAMP_COLORS:
                this.ofSwampColors = !this.ofSwampColors;
                CustomColors.updateUseDefaultGrassFoliageColors();
                this.mc.renderGlobal.loadRenderers();
                break;

            case RANDOM_ENTITIES:
                this.ofRandomEntities = !this.ofRandomEntities;
                RandomEntities.update();
                break;

            case SMOOTH_BIOMES:
                this.ofSmoothBiomes = !this.ofSmoothBiomes;
                CustomColors.updateUseDefaultGrassFoliageColors();
                this.mc.renderGlobal.loadRenderers();
                break;

            case CUSTOM_FONTS:
                this.ofCustomFonts = !this.ofCustomFonts;
                this.mc.fontRendererObj.onResourceManagerReload(Config.getResourceManager());
                this.mc.standardGalacticFontRenderer.onResourceManagerReload(Config.getResourceManager());
                break;

            case CUSTOM_COLORS:
                this.ofCustomColors = !this.ofCustomColors;
                CustomColors.update();
                this.mc.renderGlobal.loadRenderers();
                break;

            case CUSTOM_ITEMS:
                this.ofCustomItems = !this.ofCustomItems;
                this.mc.refreshResources();
                break;

            case CUSTOM_SKY:
                this.ofCustomSky = !this.ofCustomSky;
                CustomSky.update();
                break;

            case SHOW_CAPES:
                this.ofShowCapes = !this.ofShowCapes;
                break;

            case NATURAL_TEXTURES:
                this.ofNaturalTextures = !this.ofNaturalTextures;
                NaturalTextures.update();
                this.mc.renderGlobal.loadRenderers();
                break;

            case EMISSIVE_TEXTURES:
                this.ofEmissiveTextures = !this.ofEmissiveTextures;
                this.mc.refreshResources();
                break;

            case FAST_MATH:
                this.ofFastMath = !this.ofFastMath;
                MathHelper.fastMath = this.ofFastMath;
                break;

            case FAST_RENDER:
                if (!this.ofFastRender && Config.isShaders()) {
                    Config.showGuiMessage(Lang.get("of.message.fr.shaders1"), Lang.get("of.message.fr.shaders2"));
                    return;
                }

                this.ofFastRender = !this.ofFastRender;

                if (this.ofFastRender) {
                    this.mc.entityRenderer.func_181022_b();
                }

                Config.updateFramebufferSize();
                break;

            case TRANSLUCENT_BLOCKS:
                switch (ofTranslucentBlocks) {
                    case 0:
                        this.ofTranslucentBlocks = 1;
                        break;

                    case 1:
                        this.ofTranslucentBlocks = 2;
                        break;

                    default:
                        this.ofTranslucentBlocks = 0;
                        break;
                }

                this.mc.renderGlobal.loadRenderers();
                break;

            case LAZY_CHUNK_LOADING:
                this.ofLazyChunkLoading = !this.ofLazyChunkLoading;
                break;

            case RENDER_REGIONS:
                this.ofRenderRegions = !this.ofRenderRegions;
                this.mc.renderGlobal.loadRenderers();
                break;

            case SMART_ANIMATIONS:
                this.ofSmartAnimations = !this.ofSmartAnimations;
                this.mc.renderGlobal.loadRenderers();
                break;

            case DYNAMIC_FOV:
                this.ofDynamicFov = !this.ofDynamicFov;
                break;

            case ALTERNATE_BLOCKS:
                this.ofAlternateBlocks = !this.ofAlternateBlocks;
                this.mc.refreshResources();
                break;

            case DYNAMIC_LIGHTS:
                this.ofDynamicLights = nextValue(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
                DynamicLights.removeLights(this.mc.renderGlobal);
                break;

            case SCREENSHOT_SIZE:
                ++this.ofScreenshotSize;

                if (this.ofScreenshotSize > 4) {
                    this.ofScreenshotSize = 1;
                }

                if (!OpenGlHelper.isFramebufferEnabled()) {
                    this.ofScreenshotSize = 1;
                }
                break;

            case CUSTOM_ENTITY_MODELS:
                this.ofCustomEntityModels = !this.ofCustomEntityModels;
                this.mc.refreshResources();
                break;

            case CUSTOM_GUIS:
                this.ofCustomGuis = !this.ofCustomGuis;
                CustomGuis.update();
                break;

            case SHOW_GL_ERRORS:
                this.ofShowGlErrors = !this.ofShowGlErrors;
                break;

            case HELD_ITEM_TOOLTIPS:
                this.heldItemTooltips = !this.heldItemTooltips;
                break;

            case ADVANCED_TOOLTIPS:
                this.advancedItemTooltips = !this.advancedItemTooltips;
                break;
        }
    }

    private String getKeyBindingOF(final GameSettings.Options p_getKeyBindingOF_1_) {
        final String s = I18n.format(p_getKeyBindingOF_1_.getEnumString()) + ": ";

        switch (p_getKeyBindingOF_1_) {
            case RENDER_DISTANCE:
                final int i1 = (int) this.getOptionFloatValue(p_getKeyBindingOF_1_);
                String s1 = I18n.format("options.renderDistance.tiny");
                int i = 2;

                if (i1 >= 4) {
                    s1 = I18n.format("options.renderDistance.short");
                    i = 4;
                }

                if (i1 >= 8) {
                    s1 = I18n.format("options.renderDistance.normal");
                    i = 8;
                }

                if (i1 >= 16) {
                    s1 = I18n.format("options.renderDistance.far");
                    i = 16;
                }

                if (i1 >= 32) {
                    s1 = Lang.get("of.options.renderDistance.extreme");
                    i = 32;
                }

                if (i1 >= 48) {
                    s1 = Lang.get("of.options.renderDistance.insane");
                    i = 48;
                }

                if (i1 >= 64) {
                    s1 = Lang.get("of.options.renderDistance.ludicrous");
                    i = 64;
                }

                final int j = this.renderDistanceChunks - i;
                String s2 = s1;

                if (j > 0) {
                    s2 = s1 + "+";
                }

                return s + i1 + " " + s2 + "";

            case FOG_FANCY:
                switch (this.ofFogType) {
                    case 1:
                        return s + Lang.getFast();

                    case 2:
                        return s + Lang.getFancy();

                    default:
                        return s + Lang.getOff();
                }

            case FOG_START:
                return s + this.ofFogStart;

            case MIPMAP_TYPE:
                switch (this.ofMipmapType) {
                    case 0:
                        return s + Lang.get("of.options.mipmap.nearest");

                    case 1:
                        return s + Lang.get("of.options.mipmap.linear");

                    case 2:
                        return s + Lang.get("of.options.mipmap.bilinear");

                    case 3:
                        return s + Lang.get("of.options.mipmap.trilinear");

                    default:
                        return s + "of.options.mipmap.nearest";
                }

            case SMOOTH_FPS:
                return this.ofSmoothFps ? s + Lang.getOn() : s + Lang.getOff();

            case SMOOTH_WORLD:
                return this.ofSmoothWorld ? s + Lang.getOn() : s + Lang.getOff();

            case CLOUDS:
                switch (this.ofClouds) {
                    case 1:
                        return s + Lang.getFast();

                    case 2:
                        return s + Lang.getFancy();

                    case 3:
                        return s + Lang.getOff();

                    default:
                        return s + Lang.getDefault();
                }

            case TREES:
                switch (this.ofTrees) {
                    case 1:
                        return s + Lang.getFast();

                    case 2:
                        return s + Lang.getFancy();

                    case 3:
                    default:
                        return s + Lang.getDefault();

                    case 4:
                        return s + Lang.get("of.general.smart");
                }

            case DROPPED_ITEMS:
                switch (this.ofDroppedItems) {
                    case 1:
                        return s + Lang.getFast();

                    case 2:
                        return s + Lang.getFancy();

                    default:
                        return s + Lang.getDefault();
                }

            case RAIN:
                switch (this.ofRain) {
                    case 1:
                        return s + Lang.getFast();

                    case 2:
                        return s + Lang.getFancy();

                    case 3:
                        return s + Lang.getOff();

                    default:
                        return s + Lang.getDefault();
                }

            case ANIMATED_WATER:
                switch (this.ofAnimatedWater) {
                    case 1:
                        return s + Lang.get("of.options.animation.dynamic");

                    case 2:
                        return s + Lang.getOff();

                    default:
                        return s + Lang.getOn();
                }

            case ANIMATED_LAVA:
                switch (this.ofAnimatedLava) {
                    case 1:
                        return s + Lang.get("of.options.animation.dynamic");

                    case 2:
                        return s + Lang.getOff();

                    default:
                        return s + Lang.getOn();
                }

            case ANIMATED_FIRE:
                return this.ofAnimatedFire ? s + Lang.getOn() : s + Lang.getOff();

            case ANIMATED_PORTAL:
                return this.ofAnimatedPortal ? s + Lang.getOn() : s + Lang.getOff();

            case ANIMATED_REDSTONE:
                return this.ofAnimatedRedstone ? s + Lang.getOn() : s + Lang.getOff();

            case ANIMATED_EXPLOSION:
                return this.ofAnimatedExplosion ? s + Lang.getOn() : s + Lang.getOff();

            case ANIMATED_FLAME:
                return this.ofAnimatedFlame ? s + Lang.getOn() : s + Lang.getOff();

            case ANIMATED_SMOKE:
                return this.ofAnimatedSmoke ? s + Lang.getOn() : s + Lang.getOff();

            case VOID_PARTICLES:
                return this.ofVoidParticles ? s + Lang.getOn() : s + Lang.getOff();

            case WATER_PARTICLES:
                return this.ofWaterParticles ? s + Lang.getOn() : s + Lang.getOff();

            case PORTAL_PARTICLES:
                return this.ofPortalParticles ? s + Lang.getOn() : s + Lang.getOff();

            case POTION_PARTICLES:
                return this.ofPotionParticles ? s + Lang.getOn() : s + Lang.getOff();

            case FIREWORK_PARTICLES:
                return this.ofFireworkParticles ? s + Lang.getOn() : s + Lang.getOff();

            case DRIPPING_WATER_LAVA:
                return this.ofDrippingWaterLava ? s + Lang.getOn() : s + Lang.getOff();

            case ANIMATED_TERRAIN:
                return this.ofAnimatedTerrain ? s + Lang.getOn() : s + Lang.getOff();

            case ANIMATED_TEXTURES:
                return this.ofAnimatedTextures ? s + Lang.getOn() : s + Lang.getOff();

            case RAIN_SPLASH:
                return this.ofRainSplash ? s + Lang.getOn() : s + Lang.getOff();

            case LAGOMETER:
                return this.ofLagometer ? s + Lang.getOn() : s + Lang.getOff();

            case SHOW_FPS:
                return this.ofShowFps ? s + Lang.getOn() : s + Lang.getOff();

            case AUTOSAVE_TICKS:
                final int l = 900;
                return this.ofAutoSaveTicks <= l ? s + Lang.get("of.options.save.45s") : (this.ofAutoSaveTicks <= 2 * l ? s + Lang.get("of.options.save.90s") : (this.ofAutoSaveTicks <= 4 * l ? s + Lang.get("of.options.save.3min") : (this.ofAutoSaveTicks <= 8 * l ? s + Lang.get("of.options.save.6min") : (this.ofAutoSaveTicks <= 16 * l ? s + Lang.get("of.options.save.12min") : s + Lang.get("of.options.save.24min")))));

            case BETTER_GRASS:
                switch (this.ofBetterGrass) {
                    case 1:
                        return s + Lang.getFast();

                    case 2:
                        return s + Lang.getFancy();

                    default:
                        return s + Lang.getOff();
                }

            case CONNECTED_TEXTURES:
                switch (this.ofConnectedTextures) {
                    case 1:
                        return s + Lang.getFast();

                    case 2:
                        return s + Lang.getFancy();

                    default:
                        return s + Lang.getOff();
                }

            case WEATHER:
                return this.ofWeather ? s + Lang.getOn() : s + Lang.getOff();

            case SKY:
                return this.ofSky ? s + Lang.getOn() : s + Lang.getOff();

            case STARS:
                return this.ofStars ? s + Lang.getOn() : s + Lang.getOff();

            case SUN_MOON:
                return this.ofSunMoon ? s + Lang.getOn() : s + Lang.getOff();

            case VIGNETTE:
                switch (this.ofVignette) {
                    case 1:
                        return s + Lang.getFast();

                    case 2:
                        return s + Lang.getFancy();

                    default:
                        return s + Lang.getDefault();
                }

            case CHUNK_UPDATES:
                return s + this.ofChunkUpdates;

            case CHUNK_UPDATES_DYNAMIC:
                return this.ofChunkUpdatesDynamic ? s + Lang.getOn() : s + Lang.getOff();

            case TIME:
                return this.ofTime == 1 ? s + Lang.get("of.options.time.dayOnly") : (this.ofTime == 2 ? s + Lang.get("of.options.time.nightOnly") : s + Lang.getDefault());

            case CLEAR_WATER:
                return this.ofClearWater ? s + Lang.getOn() : s + Lang.getOff();

            case AA_LEVEL:
                String s3 = "";

                if (this.ofAaLevel != Config.getAntialiasingLevel()) {
                    s3 = " (" + Lang.get("of.general.restart") + ")";
                }

                return this.ofAaLevel == 0 ? s + Lang.getOff() + s3 : s + this.ofAaLevel + s3;

            case AF_LEVEL:
                return this.ofAfLevel == 1 ? s + Lang.getOff() : s + this.ofAfLevel;

            case PROFILER:
                return this.ofProfiler ? s + Lang.getOn() : s + Lang.getOff();

            case BETTER_SNOW:
                return this.ofBetterSnow ? s + Lang.getOn() : s + Lang.getOff();

            case SWAMP_COLORS:
                return this.ofSwampColors ? s + Lang.getOn() : s + Lang.getOff();

            case RANDOM_ENTITIES:
                return this.ofRandomEntities ? s + Lang.getOn() : s + Lang.getOff();

            case SMOOTH_BIOMES:
                return this.ofSmoothBiomes ? s + Lang.getOn() : s + Lang.getOff();

            case CUSTOM_FONTS:
                return this.ofCustomFonts ? s + Lang.getOn() : s + Lang.getOff();

            case CUSTOM_COLORS:
                return this.ofCustomColors ? s + Lang.getOn() : s + Lang.getOff();

            case CUSTOM_SKY:
                return this.ofCustomSky ? s + Lang.getOn() : s + Lang.getOff();

            case SHOW_CAPES:
                return this.ofShowCapes ? s + Lang.getOn() : s + Lang.getOff();

            case CUSTOM_ITEMS:
                return this.ofCustomItems ? s + Lang.getOn() : s + Lang.getOff();

            case NATURAL_TEXTURES:
                return this.ofNaturalTextures ? s + Lang.getOn() : s + Lang.getOff();

            case EMISSIVE_TEXTURES:
                return this.ofEmissiveTextures ? s + Lang.getOn() : s + Lang.getOff();

            case FAST_MATH:
                return this.ofFastMath ? s + Lang.getOn() : s + Lang.getOff();

            case FAST_RENDER:
                return this.ofFastRender ? s + Lang.getOn() : s + Lang.getOff();

            case TRANSLUCENT_BLOCKS:
                return this.ofTranslucentBlocks == 1 ? s + Lang.getFast() : (this.ofTranslucentBlocks == 2 ? s + Lang.getFancy() : s + Lang.getDefault());

            case LAZY_CHUNK_LOADING:
                return this.ofLazyChunkLoading ? s + Lang.getOn() : s + Lang.getOff();

            case RENDER_REGIONS:
                return this.ofRenderRegions ? s + Lang.getOn() : s + Lang.getOff();

            case SMART_ANIMATIONS:
                return this.ofSmartAnimations ? s + Lang.getOn() : s + Lang.getOff();

            case DYNAMIC_FOV:
                return this.ofDynamicFov ? s + Lang.getOn() : s + Lang.getOff();

            case ALTERNATE_BLOCKS:
                return this.ofAlternateBlocks ? s + Lang.getOn() : s + Lang.getOff();

            case DYNAMIC_LIGHTS:
                final int k = indexOf(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
                return s + getTranslation(KEYS_DYNAMIC_LIGHTS, k);

            case SCREENSHOT_SIZE:
                return this.ofScreenshotSize <= 1 ? s + Lang.getDefault() : s + this.ofScreenshotSize + "x";

            case CUSTOM_ENTITY_MODELS:
                return this.ofCustomEntityModels ? s + Lang.getOn() : s + Lang.getOff();

            case CUSTOM_GUIS:
                return this.ofCustomGuis ? s + Lang.getOn() : s + Lang.getOff();

            case SHOW_GL_ERRORS:
                return this.ofShowGlErrors ? s + Lang.getOn() : s + Lang.getOff();

            case FULLSCREEN_MODE:
                return this.ofFullscreenMode.equals("Default") ? s + Lang.getDefault() : s + this.ofFullscreenMode;

            case HELD_ITEM_TOOLTIPS:
                return this.heldItemTooltips ? s + Lang.getOn() : s + Lang.getOff();

            case ADVANCED_TOOLTIPS:
                return this.advancedItemTooltips ? s + Lang.getOn() : s + Lang.getOff();

            case FRAMERATE_LIMIT:
                final float f = this.getOptionFloatValue(p_getKeyBindingOF_1_);
                return f == 0.0F ? s + Lang.get("of.options.framerateLimit.vsync") : (f == p_getKeyBindingOF_1_.valueMax ? s + I18n.format("options.framerateLimit.max") : s + (int) f + " fps");

            default:
                return null;
        }
    }

    public void loadOfOptions() {
        try {
            File file1 = this.optionsFileOF;

            if (!file1.exists()) {
                file1 = this.optionsFile;
            }

            if (!file1.exists()) {
                return;
            }

            final BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new FileInputStream(file1), StandardCharsets.UTF_8));
            String s;

            while ((s = bufferedreader.readLine()) != null) {
                try {
                    final String[] astring = s.split(":");

                    if (astring.length >= 2) {
                        switch (astring[0]) {
                            case "ofRenderDistanceChunks":
                                this.renderDistanceChunks = Integer.parseInt(astring[1]);
                                this.renderDistanceChunks = Config.limit(this.renderDistanceChunks, 2, 1024);
                                break;

                            case "ofFogType":
                                this.ofFogType = Integer.parseInt(astring[1]);
                                this.ofFogType = Config.limit(this.ofFogType, 1, 3);
                                break;

                            case "ofFogStart":
                                this.ofFogStart = Float.parseFloat(astring[1]);

                                if (this.ofFogStart < 0.2F) {
                                    this.ofFogStart = 0.2F;
                                }

                                if (this.ofFogStart > 0.81F) {
                                    this.ofFogStart = 0.8F;
                                }
                                break;

                            case "ofMipmapType":
                                this.ofMipmapType = Integer.parseInt(astring[1]);
                                this.ofMipmapType = Config.limit(this.ofMipmapType, 0, 3);
                                break;

                            case "ofOcclusionFancy":
                                this.ofOcclusionFancy = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofSmoothFps":
                                this.ofSmoothFps = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofSmoothWorld":
                                this.ofSmoothWorld = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofAoLevel":
                                this.ofAoLevel = Float.parseFloat(astring[1]);
                                this.ofAoLevel = Config.limit(this.ofAoLevel, 0.0F, 1.0F);
                                break;

                            case "ofClouds":
                                this.ofClouds = Integer.parseInt(astring[1]);
                                this.ofClouds = Config.limit(this.ofClouds, 0, 3);
                                this.updateRenderClouds();
                                break;

                            case "ofCloudsHeight":
                                this.ofCloudsHeight = Float.parseFloat(astring[1]);
                                this.ofCloudsHeight = Config.limit(this.ofCloudsHeight, 0.0F, 1.0F);
                                break;

                            case "ofTrees":
                                this.ofTrees = Integer.parseInt(astring[1]);
                                this.ofTrees = limit(this.ofTrees, OF_TREES_VALUES);
                                break;

                            case "ofDroppedItems":
                                this.ofDroppedItems = Integer.parseInt(astring[1]);
                                this.ofDroppedItems = Config.limit(this.ofDroppedItems, 0, 2);
                                break;

                            case "ofRain":
                                this.ofRain = Integer.parseInt(astring[1]);
                                this.ofRain = Config.limit(this.ofRain, 0, 3);
                                break;

                            case "ofAnimatedWater":
                                this.ofAnimatedWater = Integer.parseInt(astring[1]);
                                this.ofAnimatedWater = Config.limit(this.ofAnimatedWater, 0, 2);
                                break;

                            case "ofAnimatedLava":
                                this.ofAnimatedLava = Integer.parseInt(astring[1]);
                                this.ofAnimatedLava = Config.limit(this.ofAnimatedLava, 0, 2);
                                break;

                            case "ofAnimatedFire":
                                this.ofAnimatedFire = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofAnimatedPortal":
                                this.ofAnimatedPortal = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofAnimatedRedstone":
                                this.ofAnimatedRedstone = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofAnimatedExplosion":
                                this.ofAnimatedExplosion = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofAnimatedFlame":
                                this.ofAnimatedFlame = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofAnimatedSmoke":
                                this.ofAnimatedSmoke = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofVoidParticles":
                                this.ofVoidParticles = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofWaterParticles":
                                this.ofWaterParticles = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofPortalParticles":
                                this.ofPortalParticles = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofPotionParticles":
                                this.ofPotionParticles = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofFireworkParticles":
                                this.ofFireworkParticles = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofDrippingWaterLava":
                                this.ofDrippingWaterLava = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofAnimatedTerrain":
                                this.ofAnimatedTerrain = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofAnimatedTextures":
                                this.ofAnimatedTextures = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofRainSplash":
                                this.ofRainSplash = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofLagometer":
                                this.ofLagometer = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofShowFps":
                                this.ofShowFps = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofAutoSaveTicks":
                                this.ofAutoSaveTicks = Integer.parseInt(astring[1]);
                                this.ofAutoSaveTicks = Config.limit(this.ofAutoSaveTicks, 40, 40000);
                                break;

                            case "ofBetterGrass":
                                this.ofBetterGrass = Integer.parseInt(astring[1]);
                                this.ofBetterGrass = Config.limit(this.ofBetterGrass, 1, 3);
                                break;

                            case "ofConnectedTextures":
                                this.ofConnectedTextures = Integer.parseInt(astring[1]);
                                this.ofConnectedTextures = Config.limit(this.ofConnectedTextures, 1, 3);
                                break;

                            case "ofWeather":
                                this.ofWeather = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofSky":
                                this.ofSky = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofStars":
                                this.ofStars = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofSunMoon":
                                this.ofSunMoon = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofVignette":
                                this.ofVignette = Integer.parseInt(astring[1]);
                                this.ofVignette = Config.limit(this.ofVignette, 0, 2);
                                break;

                            case "ofChunkUpdates":
                                this.ofChunkUpdates = Integer.parseInt(astring[1]);
                                this.ofChunkUpdates = Config.limit(this.ofChunkUpdates, 1, 5);
                                break;

                            case "ofChunkUpdatesDynamic":
                                this.ofChunkUpdatesDynamic = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofTime":
                                this.ofTime = Integer.parseInt(astring[1]);
                                this.ofTime = Config.limit(this.ofTime, 0, 2);
                                break;

                            case "ofClearWater":
                                this.ofClearWater = Boolean.parseBoolean(astring[1]);
                                this.updateWaterOpacity();
                                break;

                            case "ofAaLevel":
                                this.ofAaLevel = Integer.parseInt(astring[1]);
                                this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
                                break;

                            case "ofAfLevel":
                                this.ofAfLevel = Integer.parseInt(astring[1]);
                                this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
                                break;

                            case "ofProfiler":
                                this.ofProfiler = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofBetterSnow":
                                this.ofBetterSnow = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofSwampColors":
                                this.ofSwampColors = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofRandomEntities":
                                this.ofRandomEntities = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofSmoothBiomes":
                                this.ofSmoothBiomes = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofCustomFonts":
                                this.ofCustomFonts = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofCustomColors":
                                this.ofCustomColors = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofCustomItems":
                                this.ofCustomItems = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofCustomSky":
                                this.ofCustomSky = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofShowCapes":
                                this.ofShowCapes = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofNaturalTextures":
                                this.ofNaturalTextures = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofEmissiveTextures":
                                this.ofEmissiveTextures = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofLazyChunkLoading":
                                this.ofLazyChunkLoading = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofRenderRegions":
                                this.ofRenderRegions = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofSmartAnimations":
                                this.ofSmartAnimations = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofDynamicFov":
                                this.ofDynamicFov = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofAlternateBlocks":
                                this.ofAlternateBlocks = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofDynamicLights":
                                this.ofDynamicLights = Integer.parseInt(astring[1]);
                                this.ofDynamicLights = limit(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
                                break;

                            case "ofScreenshotSize":
                                this.ofScreenshotSize = Integer.parseInt(astring[1]);
                                this.ofScreenshotSize = Config.limit(this.ofScreenshotSize, 1, 4);
                                break;

                            case "ofCustomEntityModels":
                                this.ofCustomEntityModels = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofCustomGuis":
                                this.ofCustomGuis = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofShowGlErrors":
                                this.ofShowGlErrors = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofFullscreenMode":
                                this.ofFullscreenMode = astring[1];
                                break;

                            case "ofFastMath":
                                this.ofFastMath = Boolean.parseBoolean(astring[1]);
                                MathHelper.fastMath = this.ofFastMath;
                                break;

                            case "ofFastRender":
                                this.ofFastRender = Boolean.parseBoolean(astring[1]);
                                break;

                            case "ofTranslucentBlocks":
                                this.ofTranslucentBlocks = Integer.parseInt(astring[1]);
                                this.ofTranslucentBlocks = Config.limit(this.ofTranslucentBlocks, 0, 2);
                                break;
                        }
                    }

                    if (astring[0].equals("key_" + this.ofKeyBindZoom.getKeyDescription())) {
                        this.ofKeyBindZoom.setKeyCode(Integer.parseInt(astring[1]));
                    }
                } catch (final Exception exception) {
                    Config.dbg("Skipping bad option: " + s);
                    exception.printStackTrace();
                }
            }

            KeyUtils.fixKeyConflicts(this.keyBindings, new KeyBinding[]{this.ofKeyBindZoom});
            KeyBinding.resetKeyBindingArrayAndHash();
            bufferedreader.close();
        } catch (final Exception exception1) {
            Config.warn("Failed to load options");
            exception1.printStackTrace();
        }
    }

    public void saveOfOptions() {
        try {
            final PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFileOF), StandardCharsets.UTF_8));
            printwriter.println("ofFogType:" + this.ofFogType);
            printwriter.println("ofFogStart:" + this.ofFogStart);
            printwriter.println("ofMipmapType:" + this.ofMipmapType);
            printwriter.println("ofOcclusionFancy:" + this.ofOcclusionFancy);
            printwriter.println("ofSmoothFps:" + this.ofSmoothFps);
            printwriter.println("ofSmoothWorld:" + this.ofSmoothWorld);
            printwriter.println("ofAoLevel:" + this.ofAoLevel);
            printwriter.println("ofClouds:" + this.ofClouds);
            printwriter.println("ofCloudsHeight:" + this.ofCloudsHeight);
            printwriter.println("ofTrees:" + this.ofTrees);
            printwriter.println("ofDroppedItems:" + this.ofDroppedItems);
            printwriter.println("ofRain:" + this.ofRain);
            printwriter.println("ofAnimatedWater:" + this.ofAnimatedWater);
            printwriter.println("ofAnimatedLava:" + this.ofAnimatedLava);
            printwriter.println("ofAnimatedFire:" + this.ofAnimatedFire);
            printwriter.println("ofAnimatedPortal:" + this.ofAnimatedPortal);
            printwriter.println("ofAnimatedRedstone:" + this.ofAnimatedRedstone);
            printwriter.println("ofAnimatedExplosion:" + this.ofAnimatedExplosion);
            printwriter.println("ofAnimatedFlame:" + this.ofAnimatedFlame);
            printwriter.println("ofAnimatedSmoke:" + this.ofAnimatedSmoke);
            printwriter.println("ofVoidParticles:" + this.ofVoidParticles);
            printwriter.println("ofWaterParticles:" + this.ofWaterParticles);
            printwriter.println("ofPortalParticles:" + this.ofPortalParticles);
            printwriter.println("ofPotionParticles:" + this.ofPotionParticles);
            printwriter.println("ofFireworkParticles:" + this.ofFireworkParticles);
            printwriter.println("ofDrippingWaterLava:" + this.ofDrippingWaterLava);
            printwriter.println("ofAnimatedTerrain:" + this.ofAnimatedTerrain);
            printwriter.println("ofAnimatedTextures:" + this.ofAnimatedTextures);
            printwriter.println("ofRainSplash:" + this.ofRainSplash);
            printwriter.println("ofLagometer:" + this.ofLagometer);
            printwriter.println("ofShowFps:" + this.ofShowFps);
            printwriter.println("ofAutoSaveTicks:" + this.ofAutoSaveTicks);
            printwriter.println("ofBetterGrass:" + this.ofBetterGrass);
            printwriter.println("ofConnectedTextures:" + this.ofConnectedTextures);
            printwriter.println("ofWeather:" + this.ofWeather);
            printwriter.println("ofSky:" + this.ofSky);
            printwriter.println("ofStars:" + this.ofStars);
            printwriter.println("ofSunMoon:" + this.ofSunMoon);
            printwriter.println("ofVignette:" + this.ofVignette);
            printwriter.println("ofChunkUpdates:" + this.ofChunkUpdates);
            printwriter.println("ofChunkUpdatesDynamic:" + this.ofChunkUpdatesDynamic);
            printwriter.println("ofTime:" + this.ofTime);
            printwriter.println("ofClearWater:" + this.ofClearWater);
            printwriter.println("ofAaLevel:" + this.ofAaLevel);
            printwriter.println("ofAfLevel:" + this.ofAfLevel);
            printwriter.println("ofProfiler:" + this.ofProfiler);
            printwriter.println("ofBetterSnow:" + this.ofBetterSnow);
            printwriter.println("ofSwampColors:" + this.ofSwampColors);
            printwriter.println("ofRandomEntities:" + this.ofRandomEntities);
            printwriter.println("ofSmoothBiomes:" + this.ofSmoothBiomes);
            printwriter.println("ofCustomFonts:" + this.ofCustomFonts);
            printwriter.println("ofCustomColors:" + this.ofCustomColors);
            printwriter.println("ofCustomItems:" + this.ofCustomItems);
            printwriter.println("ofCustomSky:" + this.ofCustomSky);
            printwriter.println("ofShowCapes:" + this.ofShowCapes);
            printwriter.println("ofNaturalTextures:" + this.ofNaturalTextures);
            printwriter.println("ofEmissiveTextures:" + this.ofEmissiveTextures);
            printwriter.println("ofLazyChunkLoading:" + this.ofLazyChunkLoading);
            printwriter.println("ofRenderRegions:" + this.ofRenderRegions);
            printwriter.println("ofSmartAnimations:" + this.ofSmartAnimations);
            printwriter.println("ofDynamicFov:" + this.ofDynamicFov);
            printwriter.println("ofAlternateBlocks:" + this.ofAlternateBlocks);
            printwriter.println("ofDynamicLights:" + this.ofDynamicLights);
            printwriter.println("ofScreenshotSize:" + this.ofScreenshotSize);
            printwriter.println("ofCustomEntityModels:" + this.ofCustomEntityModels);
            printwriter.println("ofCustomGuis:" + this.ofCustomGuis);
            printwriter.println("ofShowGlErrors:" + this.ofShowGlErrors);
            printwriter.println("ofFullscreenMode:" + this.ofFullscreenMode);
            printwriter.println("ofFastMath:" + this.ofFastMath);
            printwriter.println("ofFastRender:" + this.ofFastRender);
            printwriter.println("ofTranslucentBlocks:" + this.ofTranslucentBlocks);
            printwriter.println("key_" + this.ofKeyBindZoom.getKeyDescription() + ":" + this.ofKeyBindZoom.getKeyCode());
            printwriter.close();
        } catch (final Exception exception) {
            Config.warn("Failed to save options");
            exception.printStackTrace();
        }
    }

    private void updateRenderClouds() {
        switch (this.ofClouds) {
            case 1:
                this.clouds = 1;
                break;

            case 2:
                this.clouds = 2;
                break;

            case 3:
                this.clouds = 0;
                break;

            default:
                if (this.fancyGraphics) {
                    this.clouds = 2;
                } else {
                    this.clouds = 1;
                }
        }
    }

    public void resetSettings() {
        this.renderDistanceChunks = 8;
        this.viewBobbing = true;
        this.anaglyph = false;
        this.limitFramerate = (int) GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
        this.enableVsync = false;
        this.updateVSync();
        this.mipmapLevels = 4;
        this.fancyGraphics = true;
        this.ambientOcclusion = 2;
        this.clouds = 2;
        this.fovSetting = 70.0F;
        this.gammaSetting = 0.0F;
        this.guiScale = 2;
        this.particleSetting = 0;
        this.heldItemTooltips = true;
        this.useVbo = false;
        this.forceUnicodeFont = false;
        this.ofFogType = 1;
        this.ofFogStart = 0.8F;
        this.ofMipmapType = 0;
        this.ofOcclusionFancy = false;
        this.ofSmartAnimations = false;
        this.ofSmoothFps = false;
        Config.updateAvailableProcessors();
        this.ofSmoothWorld = Config.isSingleProcessor();
        this.ofLazyChunkLoading = false;
        this.ofRenderRegions = false;
        this.ofFastMath = false;
        this.ofFastRender = false;
        this.ofTranslucentBlocks = 0;
        this.ofDynamicFov = true;
        this.ofAlternateBlocks = true;
        this.ofDynamicLights = 3;
        this.ofScreenshotSize = 1;
        this.ofCustomEntityModels = true;
        this.ofCustomGuis = true;
        this.ofShowGlErrors = true;
        this.ofAoLevel = 1.0F;
        this.ofAaLevel = 0;
        this.ofAfLevel = 1;
        this.ofClouds = 0;
        this.ofCloudsHeight = 0.0F;
        this.ofTrees = 0;
        this.ofRain = 0;
        this.ofBetterGrass = 3;
        this.ofAutoSaveTicks = 4000;
        this.ofLagometer = false;
        this.ofShowFps = false;
        this.ofProfiler = false;
        this.ofWeather = true;
        this.ofSky = true;
        this.ofStars = true;
        this.ofSunMoon = true;
        this.ofVignette = 0;
        this.ofChunkUpdates = 1;
        this.ofChunkUpdatesDynamic = false;
        this.ofTime = 0;
        this.ofClearWater = false;
        this.ofBetterSnow = false;
        this.ofFullscreenMode = "Default";
        this.ofSwampColors = true;
        this.ofRandomEntities = true;
        this.ofSmoothBiomes = true;
        this.ofCustomFonts = true;
        this.ofCustomColors = true;
        this.ofCustomItems = true;
        this.ofCustomSky = true;
        this.ofShowCapes = true;
        this.ofConnectedTextures = 2;
        this.ofNaturalTextures = false;
        this.ofEmissiveTextures = true;
        this.ofAnimatedWater = 0;
        this.ofAnimatedLava = 0;
        this.ofAnimatedFire = true;
        this.ofAnimatedPortal = true;
        this.ofAnimatedRedstone = true;
        this.ofAnimatedExplosion = true;
        this.ofAnimatedFlame = true;
        this.ofAnimatedSmoke = true;
        this.ofVoidParticles = true;
        this.ofWaterParticles = true;
        this.ofRainSplash = true;
        this.ofPortalParticles = true;
        this.ofPotionParticles = true;
        this.ofFireworkParticles = true;
        this.ofDrippingWaterLava = true;
        this.ofAnimatedTerrain = true;
        this.ofAnimatedTextures = true;
        Shaders.setShaderPack("OFF");
        Shaders.configAntialiasingLevel = 0;
        Shaders.uninit();
        Shaders.storeConfig();
        this.updateWaterOpacity();
        this.mc.refreshResources();
        this.saveOptions();
    }

    public void updateVSync() {
        try {
            Display.setVSyncEnabled(this.enableVsync);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void updateWaterOpacity() {
        if (Config.isIntegratedServerRunning()) {
            Config.waterOpacityChanged = true;
        }

        ClearWater.updateWaterOpacity(this, this.mc.theWorld);
    }

    public void setAllAnimations(final boolean p_setAllAnimations_1_) {
        final int i = p_setAllAnimations_1_ ? 0 : 2;
        this.ofAnimatedWater = i;
        this.ofAnimatedLava = i;
        this.ofAnimatedFire = p_setAllAnimations_1_;
        this.ofAnimatedPortal = p_setAllAnimations_1_;
        this.ofAnimatedRedstone = p_setAllAnimations_1_;
        this.ofAnimatedExplosion = p_setAllAnimations_1_;
        this.ofAnimatedFlame = p_setAllAnimations_1_;
        this.ofAnimatedSmoke = p_setAllAnimations_1_;
        this.ofVoidParticles = p_setAllAnimations_1_;
        this.ofWaterParticles = p_setAllAnimations_1_;
        this.ofRainSplash = p_setAllAnimations_1_;
        this.ofPortalParticles = p_setAllAnimations_1_;
        this.ofPotionParticles = p_setAllAnimations_1_;
        this.ofFireworkParticles = p_setAllAnimations_1_;
        this.particleSetting = p_setAllAnimations_1_ ? 0 : 2;
        this.ofDrippingWaterLava = p_setAllAnimations_1_;
        this.ofAnimatedTerrain = p_setAllAnimations_1_;
        this.ofAnimatedTextures = p_setAllAnimations_1_;
    }

    private static int nextValue(final int p_nextValue_0_, final int[] p_nextValue_1_) {
        int i = indexOf(p_nextValue_0_, p_nextValue_1_);

        if (i < 0) {
            return p_nextValue_1_[0];
        } else {
            ++i;

            if (i >= p_nextValue_1_.length) {
                i = 0;
            }

            return p_nextValue_1_[i];
        }
    }

    private static int limit(final int p_limit_0_, final int[] p_limit_1_) {
        final int i = indexOf(p_limit_0_, p_limit_1_);
        return i < 0 ? p_limit_1_[0] : p_limit_0_;
    }

    private static int indexOf(final int p_indexOf_0_, final int[] p_indexOf_1_) {
        for (int i = 0; i < p_indexOf_1_.length; ++i) {
            if (p_indexOf_1_[i] == p_indexOf_0_) {
                return i;
            }
        }

        return -1;
    }

    public enum Options {
        INVERT_MOUSE("options.invertMouse", false, true),
        SENSITIVITY("options.sensitivity", true, false),
        FOV("options.fov", true, false, 30.0F, 110.0F, 1.0F),
        GAMMA("options.gamma", true, false),
        SATURATION("options.saturation", true, false),
        RENDER_DISTANCE("options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
        VIEW_BOBBING("options.viewBobbing", false, true),
        ANAGLYPH("options.anaglyph", false, true),
        FRAMERATE_LIMIT("options.framerateLimit", true, false, 0.0F, 260.0F, 5.0F),
        FBO_ENABLE("options.fboEnable", false, true),
        RENDER_CLOUDS("options.renderClouds", false, false),
        GRAPHICS("options.graphics", false, false),
        AMBIENT_OCCLUSION("options.ao", false, false),
        GUI_SCALE("options.guiScale", false, false),
        PARTICLES("options.particles", false, false),
        CHAT_VISIBILITY("options.chat.visibility", false, false),
        CHAT_COLOR("options.chat.color", false, true),
        CHAT_LINKS("options.chat.links", false, true),
        CHAT_OPACITY("options.chat.opacity", true, false),
        CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
        SNOOPER_ENABLED("options.snooper", false, true),
        USE_FULLSCREEN("options.fullscreen", false, true),
        ENABLE_VSYNC("options.vsync", false, true),
        USE_VBO("options.vbo", false, true),
        TOUCHSCREEN("options.touchscreen", false, true),
        CHAT_SCALE("options.chat.scale", true, false),
        CHAT_WIDTH("options.chat.width", true, false),
        CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
        CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
        MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
        FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
        BLOCK_ALTERNATIVES("options.blockAlternatives", false, true),
        REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
        ENTITY_SHADOWS("options.entityShadows", false, true),
        REALMS_NOTIFICATIONS("options.realmsNotifications", false, true),
        FOG_FANCY("of.options.FOG_FANCY", false, false),
        FOG_START("of.options.FOG_START", false, false),
        MIPMAP_TYPE("of.options.MIPMAP_TYPE", true, false, 0.0F, 3.0F, 1.0F),
        SMOOTH_FPS("of.options.SMOOTH_FPS", false, false),
        CLOUDS("of.options.CLOUDS", false, false),
        CLOUD_HEIGHT("of.options.CLOUD_HEIGHT", true, false),
        TREES("of.options.TREES", false, false),
        RAIN("of.options.RAIN", false, false),
        ANIMATED_WATER("of.options.ANIMATED_WATER", false, false),
        ANIMATED_LAVA("of.options.ANIMATED_LAVA", false, false),
        ANIMATED_FIRE("of.options.ANIMATED_FIRE", false, false),
        ANIMATED_PORTAL("of.options.ANIMATED_PORTAL", false, false),
        AO_LEVEL("of.options.AO_LEVEL", true, false),
        LAGOMETER("of.options.LAGOMETER", false, false),
        SHOW_FPS("of.options.SHOW_FPS", false, false),
        AUTOSAVE_TICKS("of.options.AUTOSAVE_TICKS", false, false),
        BETTER_GRASS("of.options.BETTER_GRASS", false, false),
        ANIMATED_REDSTONE("of.options.ANIMATED_REDSTONE", false, false),
        ANIMATED_EXPLOSION("of.options.ANIMATED_EXPLOSION", false, false),
        ANIMATED_FLAME("of.options.ANIMATED_FLAME", false, false),
        ANIMATED_SMOKE("of.options.ANIMATED_SMOKE", false, false),
        WEATHER("of.options.WEATHER", false, false),
        SKY("of.options.SKY", false, false),
        STARS("of.options.STARS", false, false),
        SUN_MOON("of.options.SUN_MOON", false, false),
        VIGNETTE("of.options.VIGNETTE", false, false),
        CHUNK_UPDATES("of.options.CHUNK_UPDATES", false, false),
        CHUNK_UPDATES_DYNAMIC("of.options.CHUNK_UPDATES_DYNAMIC", false, false),
        TIME("of.options.TIME", false, false),
        CLEAR_WATER("of.options.CLEAR_WATER", false, false),
        SMOOTH_WORLD("of.options.SMOOTH_WORLD", false, false),
        VOID_PARTICLES("of.options.VOID_PARTICLES", false, false),
        WATER_PARTICLES("of.options.WATER_PARTICLES", false, false),
        RAIN_SPLASH("of.options.RAIN_SPLASH", false, false),
        PORTAL_PARTICLES("of.options.PORTAL_PARTICLES", false, false),
        POTION_PARTICLES("of.options.POTION_PARTICLES", false, false),
        FIREWORK_PARTICLES("of.options.FIREWORK_PARTICLES", false, false),
        PROFILER("of.options.PROFILER", false, false),
        DRIPPING_WATER_LAVA("of.options.DRIPPING_WATER_LAVA", false, false),
        BETTER_SNOW("of.options.BETTER_SNOW", false, false),
        FULLSCREEN_MODE("of.options.FULLSCREEN_MODE", true, false, 0.0F, (float) Config.getDisplayModes().length, 1.0F),
        ANIMATED_TERRAIN("of.options.ANIMATED_TERRAIN", false, false),
        SWAMP_COLORS("of.options.SWAMP_COLORS", false, false),
        RANDOM_ENTITIES("of.options.RANDOM_ENTITIES", false, false),
        SMOOTH_BIOMES("of.options.SMOOTH_BIOMES", false, false),
        CUSTOM_FONTS("of.options.CUSTOM_FONTS", false, false),
        CUSTOM_COLORS("of.options.CUSTOM_COLORS", false, false),
        SHOW_CAPES("of.options.SHOW_CAPES", false, false),
        CONNECTED_TEXTURES("of.options.CONNECTED_TEXTURES", false, false),
        CUSTOM_ITEMS("of.options.CUSTOM_ITEMS", false, false),
        AA_LEVEL("of.options.AA_LEVEL", true, false, 0.0F, 16.0F, 1.0F),
        AF_LEVEL("of.options.AF_LEVEL", true, false, 1.0F, 16.0F, 1.0F),
        ANIMATED_TEXTURES("of.options.ANIMATED_TEXTURES", false, false),
        NATURAL_TEXTURES("of.options.NATURAL_TEXTURES", false, false),
        EMISSIVE_TEXTURES("of.options.EMISSIVE_TEXTURES", false, false),
        HELD_ITEM_TOOLTIPS("of.options.HELD_ITEM_TOOLTIPS", false, false),
        DROPPED_ITEMS("of.options.DROPPED_ITEMS", false, false),
        LAZY_CHUNK_LOADING("of.options.LAZY_CHUNK_LOADING", false, false),
        CUSTOM_SKY("of.options.CUSTOM_SKY", false, false),
        FAST_MATH("of.options.FAST_MATH", false, false),
        FAST_RENDER("of.options.FAST_RENDER", false, false),
        TRANSLUCENT_BLOCKS("of.options.TRANSLUCENT_BLOCKS", false, false),
        DYNAMIC_FOV("of.options.DYNAMIC_FOV", false, false),
        DYNAMIC_LIGHTS("of.options.DYNAMIC_LIGHTS", false, false),
        ALTERNATE_BLOCKS("of.options.ALTERNATE_BLOCKS", false, false),
        CUSTOM_ENTITY_MODELS("of.options.CUSTOM_ENTITY_MODELS", false, false),
        ADVANCED_TOOLTIPS("of.options.ADVANCED_TOOLTIPS", false, false),
        SCREENSHOT_SIZE("of.options.SCREENSHOT_SIZE", false, false),
        CUSTOM_GUIS("of.options.CUSTOM_GUIS", false, false),
        RENDER_REGIONS("of.options.RENDER_REGIONS", false, false),
        SHOW_GL_ERRORS("of.options.SHOW_GL_ERRORS", false, false),
        SMART_ANIMATIONS("of.options.SMART_ANIMATIONS", false, false);

        private final boolean enumFloat;
        private final boolean enumBoolean;
        private final String enumString;
        private final float valueStep;
        private final float valueMin;
        private float valueMax;

        public static GameSettings.Options getEnumOptions(final int p_74379_0_) {
            for (final GameSettings.Options gamesettings$options : values()) {
                if (gamesettings$options.returnEnumOrdinal() == p_74379_0_) {
                    return gamesettings$options;
                }
            }

            return null;
        }

        Options(final String p_i1015_3_, final boolean p_i1015_4_, final boolean p_i1015_5_) {
            this(p_i1015_3_, p_i1015_4_, p_i1015_5_, 0.0F, 1.0F, 0.0F);
        }

        Options(final String p_i45004_3_, final boolean p_i45004_4_, final boolean p_i45004_5_, final float p_i45004_6_, final float p_i45004_7_, final float p_i45004_8_) {
            this.enumString = p_i45004_3_;
            this.enumFloat = p_i45004_4_;
            this.enumBoolean = p_i45004_5_;
            this.valueMin = p_i45004_6_;
            this.valueMax = p_i45004_7_;
            this.valueStep = p_i45004_8_;
        }

        public boolean getEnumFloat() {
            return this.enumFloat;
        }

        public boolean getEnumBoolean() {
            return this.enumBoolean;
        }

        public int returnEnumOrdinal() {
            return this.ordinal();
        }

        public String getEnumString() {
            return this.enumString;
        }

        public float getValueMax() {
            return this.valueMax;
        }

        public void setValueMax(final float p_148263_1_) {
            this.valueMax = p_148263_1_;
        }

        public float normalizeValue(final float p_148266_1_) {
            return MathHelper.clamp_float((this.snapToStepClamp(p_148266_1_) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
        }

        public float denormalizeValue(final float p_148262_1_) {
            return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(p_148262_1_, 0.0F, 1.0F));
        }

        public float snapToStepClamp(float p_148268_1_) {
            p_148268_1_ = this.snapToStep(p_148268_1_);
            return MathHelper.clamp_float(p_148268_1_, this.valueMin, this.valueMax);
        }

        private float snapToStep(float p_148264_1_) {
            if (this.valueStep > 0.0F) {
                p_148264_1_ = this.valueStep * (float) Math.round(p_148264_1_ / this.valueStep);
            }

            return p_148264_1_;
        }
    }
}