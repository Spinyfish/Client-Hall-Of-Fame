package cafe.corrosion;

import cafe.corrosion.event.impl.EventPostInitialize;
import cafe.corrosion.keybind.KeyStorage;
import cafe.corrosion.menu.GuiAltManager;
import cafe.corrosion.module.Module;
import cafe.corrosion.viamcp.ViaMCP;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import dev.gardeningtool.helper.annotation.Native;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.OpenGLException;

@Native
public class Bootstrap {
    public static void initializeMinecraft(Minecraft minecraft, GameConfiguration gameConfig) {
        minecraft.mcDataDir = gameConfig.folderInfo.mcDataDir;
        minecraft.fileAssets = gameConfig.folderInfo.assetsDir;
        minecraft.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
        minecraft.launchedVersion = gameConfig.gameInfo.version;
        minecraft.twitchDetails = gameConfig.userInfo.userProperties;
        minecraft.field_181038_N = gameConfig.userInfo.field_181172_c;
        minecraft.mcDefaultResourcePack = new DefaultResourcePack((new ResourceIndex(gameConfig.folderInfo.assetsDir, gameConfig.folderInfo.assetIndex)).getResourceMap());
        loadSession(minecraft, gameConfig);
    }

    public static void loadSession(Minecraft minecraft, GameConfiguration gameConfig) {
        minecraft.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
        minecraft.sessionService = (new YggdrasilAuthenticationService(gameConfig.userInfo.proxy, UUID.randomUUID().toString())).createMinecraftSessionService();
        minecraft.session = gameConfig.userInfo.session;
        Minecraft.logger.info("Setting user: " + minecraft.session.getUsername());
        Minecraft.logger.info("(Session ID is " + minecraft.session.getSessionID() + ")");
        loadAdditionalInfo(minecraft, gameConfig);
    }

    public static void loadAdditionalInfo(Minecraft minecraft, GameConfiguration gameConfig) {
        minecraft.isDemo = gameConfig.gameInfo.isDemo;
        minecraft.displayWidth = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
        minecraft.displayHeight = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;
        minecraft.tempDisplayWidth = gameConfig.displayInfo.width;
        minecraft.tempDisplayHeight = gameConfig.displayInfo.height;
        minecraft.fullscreen = gameConfig.displayInfo.fullscreen;
        minecraft.jvm64bit = minecraft.isJava64bit();
        minecraft.theIntegratedServer = new IntegratedServer(minecraft);
        if (gameConfig.serverInfo.serverName != null) {
            minecraft.serverName = gameConfig.serverInfo.serverName;
            minecraft.serverPort = gameConfig.serverInfo.serverPort;
        }

        ImageIO.setUseCache(false);
        net.minecraft.init.Bootstrap.register();
    }

    public static void startGame(final Minecraft minecraft) throws LWJGLException, IOException {
        minecraft.gameSettings = new GameSettings(minecraft, minecraft.mcDataDir);
        minecraft.defaultResourcePacks.add(minecraft.mcDefaultResourcePack);
        minecraft.startTimerHackThread();
        if (minecraft.gameSettings.overrideHeight > 0 && minecraft.gameSettings.overrideWidth > 0) {
            minecraft.displayWidth = minecraft.gameSettings.overrideWidth;
            minecraft.displayHeight = minecraft.gameSettings.overrideHeight;
        }

        Minecraft.logger.info("LWJGL Version: " + Sys.getVersion());
        minecraft.setWindowIcon();
        minecraft.setInitialDisplayMode();
        minecraft.createDisplay();
        OpenGlHelper.initializeTextures();
        minecraft.framebufferMc = new Framebuffer(minecraft.displayWidth, minecraft.displayHeight, true);
        minecraft.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        minecraft.registerMetadataSerializers();
        minecraft.mcResourcePackRepository = new ResourcePackRepository(minecraft.fileResourcepacks, new File(minecraft.mcDataDir, "server-resource-packs"), minecraft.mcDefaultResourcePack, minecraft.metadataSerializer_, minecraft.gameSettings);
        minecraft.mcResourceManager = new SimpleReloadableResourceManager(minecraft.metadataSerializer_);
        minecraft.mcLanguageManager = new LanguageManager(minecraft.metadataSerializer_, minecraft.gameSettings.language);
        minecraft.mcResourceManager.registerReloadListener(minecraft.mcLanguageManager);
        minecraft.refreshResources();
        minecraft.renderEngine = new TextureManager(minecraft.mcResourceManager);
        minecraft.mcResourceManager.registerReloadListener(minecraft.renderEngine);
        minecraft.drawSplashScreen(minecraft.renderEngine);
        minecraft.initStream();
        minecraft.skinManager = new SkinManager(minecraft.renderEngine, new File(minecraft.fileAssets, "skins"), minecraft.sessionService);
        minecraft.saveLoader = new AnvilSaveConverter(new File(minecraft.mcDataDir, "saves"));
        minecraft.mcSoundHandler = new SoundHandler(minecraft.mcResourceManager, minecraft.gameSettings);
        minecraft.mcResourceManager.registerReloadListener(minecraft.mcSoundHandler);
        minecraft.mcMusicTicker = new MusicTicker(minecraft);
        minecraft.fontRendererObj = new FontRenderer(minecraft.gameSettings, new ResourceLocation("textures/font/ascii.png"), minecraft.renderEngine, false);
        if (minecraft.gameSettings.language != null) {
            minecraft.fontRendererObj.setUnicodeFlag(minecraft.isUnicode());
            minecraft.fontRendererObj.setBidiFlag(minecraft.mcLanguageManager.isCurrentLanguageBidirectional());
        }

        minecraft.standardGalacticFontRenderer = new FontRenderer(minecraft.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), minecraft.renderEngine, false);
        minecraft.mcResourceManager.registerReloadListener(minecraft.fontRendererObj);
        minecraft.mcResourceManager.registerReloadListener(minecraft.standardGalacticFontRenderer);
        minecraft.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
        minecraft.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        AchievementList.openInventory.setStatStringFormatter(new IStatStringFormat() {
            public String formatString(String p_74535_1_) {
                try {
                    return String.format(p_74535_1_, GameSettings.getKeyDisplayString(minecraft.gameSettings.keyBindInventory.getKeyCode()));
                } catch (Exception var3) {
                    return "Error: " + var3.getLocalizedMessage();
                }
            }
        });
        minecraft.mouseHelper = new MouseHelper();
        minecraft.checkGLError("Pre startup");
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7425);
        GlStateManager.clearDepth(1.0D);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.cullFace(1029);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        minecraft.checkGLError("Startup");
        minecraft.textureMapBlocks = new TextureMap("textures");
        minecraft.textureMapBlocks.setMipmapLevels(minecraft.gameSettings.mipmapLevels);
        minecraft.renderEngine.loadTickableTexture(TextureMap.locationBlocksTexture, minecraft.textureMapBlocks);
        minecraft.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        minecraft.textureMapBlocks.setBlurMipmapDirect(false, minecraft.gameSettings.mipmapLevels > 0);
        minecraft.modelManager = new ModelManager(minecraft.textureMapBlocks);
        minecraft.mcResourceManager.registerReloadListener(minecraft.modelManager);
        minecraft.renderItem = new RenderItem(minecraft.renderEngine, minecraft.modelManager);
        minecraft.renderManager = new RenderManager(minecraft.renderEngine, minecraft.renderItem);
        minecraft.itemRenderer = new ItemRenderer(minecraft);
        minecraft.mcResourceManager.registerReloadListener(minecraft.renderItem);
        minecraft.entityRenderer = new EntityRenderer(minecraft, minecraft.mcResourceManager);
        minecraft.mcResourceManager.registerReloadListener(minecraft.entityRenderer);
        minecraft.blockRenderDispatcher = new BlockRendererDispatcher(minecraft.modelManager.getBlockModelShapes(), minecraft.gameSettings);
        minecraft.mcResourceManager.registerReloadListener(minecraft.blockRenderDispatcher);
        minecraft.renderGlobal = new RenderGlobal(minecraft);
        minecraft.mcResourceManager.registerReloadListener(minecraft.renderGlobal);
        minecraft.guiAchievement = new GuiAchievement(minecraft);
        GlStateManager.viewport(0, 0, minecraft.displayWidth, minecraft.displayHeight);
        minecraft.effectRenderer = new EffectRenderer(minecraft.theWorld, minecraft.renderEngine);
        minecraft.checkGLError("Post startup");
        minecraft.ingameGUI = new GuiIngame(minecraft);
        if (minecraft.serverName != null) {
            minecraft.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), minecraft, minecraft.serverName, minecraft.serverPort));
        } else {
            minecraft.displayGuiScreen(new GuiMainMenu());
        }

        minecraft.renderEngine.deleteTexture(minecraft.mojangLogo);
        minecraft.mojangLogo = null;
        minecraft.loadingScreen = new LoadingScreenRenderer(minecraft);
        if (minecraft.gameSettings.fullScreen && !minecraft.fullscreen) {
            minecraft.toggleFullscreen();
        }

        try {
            Display.setVSyncEnabled(minecraft.gameSettings.enableVsync);
        } catch (OpenGLException var2) {
            minecraft.gameSettings.enableVsync = false;
            minecraft.gameSettings.saveOptions();
        }

        minecraft.renderGlobal.makeEntityOutlineShader();
        initializeCorrosion();
    }

    public static void initializeCorrosion() {
        Corrosion corrosion = Corrosion.INSTANCE;
        KeyStorage.load(corrosion.getKeybindPath());
        corrosion.getModuleManager().init();
        corrosion.getGuiComponentManager().initialize();
        corrosion.getCommandManager().init();
        corrosion.getCorrosionSocket().init();
        corrosion.getModuleManager().getObjects().stream().filter((object) -> {
            return object.getAttributes().defaultModule();
        }).forEach(Module::toggle);
        corrosion.getConfigManager().loadConfigs();
        corrosion.getConfigManager().loadDefault();
        ViaMCP.getInstance().start();
        corrosion.getEventBus().handle(new EventPostInitialize());
        GuiAltManager.init();
    }

    public static void initializeGame(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("fullscreen");
        optionparser.accepts("checkGlErrors");
        OptionSpec<String> optionspec = optionparser.accepts("server").withRequiredArg();
        OptionSpec<Integer> optionspec1 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
        OptionSpec<File> optionspec2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
        OptionSpec<File> optionspec3 = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        OptionSpec<File> optionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        OptionSpec<String> optionspec5 = optionparser.accepts("proxyHost").withRequiredArg();
        OptionSpec<Integer> optionspec6 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
        OptionSpec<String> optionspec7 = optionparser.accepts("proxyUser").withRequiredArg();
        OptionSpec<String> optionspec8 = optionparser.accepts("proxyPass").withRequiredArg();
        OptionSpec<String> optionspec9 = optionparser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L);
        OptionSpec<String> optionspec10 = optionparser.accepts("uuid").withRequiredArg();
        OptionSpec<String> optionspec11 = optionparser.accepts("accessToken").withRequiredArg().required();
        OptionSpec<String> optionspec12 = optionparser.accepts("version").withRequiredArg().required();
        OptionSpec<Integer> optionspec13 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
        OptionSpec<Integer> optionspec14 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
        OptionSpec<String> optionspec15 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> optionspec16 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> optionspec17 = optionparser.accepts("assetIndex").withRequiredArg();
        OptionSpec<String> optionspec18 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy");
        OptionSpec<String> optionspec19 = optionparser.nonOptions();
        OptionSet optionset = optionparser.parse(args);
        List<String> list = optionset.valuesOf((OptionSpec)optionspec19);
        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list);
        }

        String s = (String)optionset.valueOf((OptionSpec)optionspec5);
        Proxy proxy = Proxy.NO_PROXY;
        if (s != null) {
            try {
                proxy = new Proxy(Type.SOCKS, new InetSocketAddress(s, (Integer)optionset.valueOf((OptionSpec)optionspec6)));
            } catch (Exception var46) {
            }
        }

        final String s1 = (String)optionset.valueOf((OptionSpec)optionspec7);
        final String s2 = (String)optionset.valueOf((OptionSpec)optionspec8);
        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(s1) && isNullOrEmpty(s2)) {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(s1, s2.toCharArray());
                }
            });
        }

        int i = (Integer)optionset.valueOf((OptionSpec)optionspec13);
        int j = (Integer)optionset.valueOf((OptionSpec)optionspec14);
        boolean flag = optionset.has("fullscreen");
        boolean flag1 = optionset.has("checkGlErrors");
        boolean flag2 = optionset.has("demo");
        String s3 = (String)optionset.valueOf((OptionSpec)optionspec12);
        Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
        PropertyMap propertymap = (PropertyMap)gson.fromJson((String)optionset.valueOf((OptionSpec)optionspec15), PropertyMap.class);
        PropertyMap propertymap1 = (PropertyMap)gson.fromJson((String)optionset.valueOf((OptionSpec)optionspec16), PropertyMap.class);
        File file1 = (File)optionset.valueOf((OptionSpec)optionspec2);
        File file2 = optionset.has((OptionSpec)optionspec3) ? (File)optionset.valueOf((OptionSpec)optionspec3) : new File(file1, "assets/");
        File file3 = optionset.has((OptionSpec)optionspec4) ? (File)optionset.valueOf((OptionSpec)optionspec4) : new File(file1, "resourcepacks/");
        String s4 = optionset.has((OptionSpec)optionspec10) ? (String)optionspec10.value(optionset) : (String)optionspec9.value(optionset);
        String s5 = optionset.has((OptionSpec)optionspec17) ? (String)optionspec17.value(optionset) : null;
        String s6 = (String)optionset.valueOf((OptionSpec)optionspec);
        Integer integer = (Integer)optionset.valueOf((OptionSpec)optionspec1);
        Session session = new Session((String)optionspec9.value(optionset), s4, (String)optionspec11.value(optionset), (String)optionspec18.value(optionset));
        GameConfiguration gameconfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, propertymap, propertymap1, proxy), new GameConfiguration.DisplayInformation(i, j, flag, flag1), new GameConfiguration.FolderInformation(file1, file3, file2, s5), new GameConfiguration.GameInformation(flag2, s3), new GameConfiguration.ServerInformation(s6, integer));
        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
            public void run() {
                Minecraft.stopIntegratedServer();
                Corrosion.INSTANCE.end();
            }
        });
        Thread.currentThread().setName("Client thread");
        (new Minecraft(gameconfiguration)).run();
    }

    private static boolean isNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
