package com.alan.clients;

import com.alan.clients.anticheat.CheatDetector;
import com.alan.clients.api.Clover;
import com.alan.clients.api.Rise;
import com.alan.clients.command.Command;
import com.alan.clients.command.CommandManager;
import com.alan.clients.component.Component;
import com.alan.clients.component.ComponentManager;
import com.alan.clients.creative.RiseTab;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.DevelopmentFeature;
import com.alan.clients.module.api.manager.ModuleManager;
import com.alan.clients.network.NetworkManager;
import com.alan.clients.newevent.bus.impl.EventBus;
import com.alan.clients.notification.NotificationManager;
import com.alan.clients.packetlog.Check;
import com.alan.clients.packetlog.api.manager.PacketLogManager;
import com.alan.clients.protection.check.api.McqBFVbnWB;
import com.alan.clients.security.SecurityFeatureManager;
import com.alan.clients.script.ScriptManager;
import com.alan.clients.protection.manager.TargetManager;
import com.alan.clients.ui.click.clover.CloverClickGUI;
import com.alan.clients.ui.click.dropdown.DropdownClickGUI;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.menu.impl.alt.AltManagerMenu;
import com.alan.clients.ui.theme.ThemeManager;
import com.alan.clients.util.file.FileManager;
import com.alan.clients.util.file.FileType;
import com.alan.clients.util.file.alt.AltManager;
import com.alan.clients.util.file.config.ConfigFile;
import com.alan.clients.util.file.config.ConfigManager;
import com.alan.clients.util.file.insult.InsultFile;
import com.alan.clients.util.file.insult.InsultManager;
import com.alan.clients.util.file.data.DataManager;
import com.alan.clients.util.localization.Locale;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.math.MathConst;
import com.alan.clients.bots.BotManager;
import com.alan.clients.util.value.ConstantManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.viamcp.ViaMCP;
import org.atteo.classindex.ClassIndex;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The main class where the client is loaded up.
 * Anything related to the client will start from here and managers etc instances will be stored in this class.
 *
 * @author Tecnio
 * @since 29/08/2021
 */
@Getter
public enum Client {

    /**
     * Simple enum instance for our client as enum instances
     * are immutable and are very easy to create and use.
     */
    INSTANCE;

    public static String NAME = "Rise";
    public static String VERSION = "6.0 Beta";
    public static String VERSION_FULL = "6.0 Pre-release 2"; // Used to give more detailed build info on beta builds
    public static String VERSION_DATE = "November 2, 2022";

    public static boolean DEVELOPMENT_SWITCH = true;
    public static boolean BETA_SWITCH = true;
    public static boolean FIRST_LAUNCH = true;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    
    @Setter
    private Locale locale = Locale.EN_US; // The language of the client

    private EventBus eventBus;
    private McqBFVbnWB McqAFVeaWB;
    private ModuleManager moduleManager;
    private ComponentManager componentManager;
    private CommandManager commandManager;
    private SecurityFeatureManager securityManager;
    private BotManager botManager;
    private ThemeManager themeManager;
    private NotificationManager notificationManager;
    @Setter
    private NetworkManager networkManager;
    private ScriptManager scriptManager;
    private DataManager dataManager;
    private CheatDetector cheatDetector;

    private FileManager fileManager;

    private ConfigManager configManager;
    private AltManager altManager;
    private InsultManager insultManager;
    private TargetManager targetManager;
    private ConstantManager constantManager;
    private PacketLogManager packetLogManager;

    private ConfigFile configFile;

    private CloverClickGUI cloverClickGUI;
    private RiseClickGUI standardClickGUI;
    private DropdownClickGUI dropdownClickGUI;
    private AltManagerMenu altManagerMenu;

    private RiseTab creativeTab;

    @Setter
    private boolean validated;

    /**
     * The main method when the Minecraft#startGame method is about
     * finish executing our client gets called and that's where we
     * can start loading our own classes and modules.
     */
    public void initRise() {
        System.out.println("Starting Rise");

        // Crack Protection
//        if (!this.validated && !DEVELOPMENT_SWITCH) {
//            return;
//        }

        // Init
        Minecraft mc = Minecraft.getMinecraft();
        MathConst.calculate();

        // Compatibility
        mc.gameSettings.guiScale = 2;
        mc.gameSettings.ofFastRender = false;
        mc.gameSettings.ofShowGlErrors = DEVELOPMENT_SWITCH;

        // Performance
        mc.gameSettings.ofSmartAnimations = true;
        mc.gameSettings.ofSmoothFps = false;
        mc.gameSettings.ofFastMath = false;

        this.McqAFVeaWB = new McqBFVbnWB();
        this.moduleManager = new ModuleManager();
        this.componentManager = new ComponentManager();
        this.commandManager = new CommandManager();
        this.fileManager = new FileManager();
        this.configManager = new ConfigManager();
        this.altManager = new AltManager();
        this.insultManager = new InsultManager();
        this.dataManager = new DataManager();
        this.securityManager = new SecurityFeatureManager();
        this.botManager = new BotManager();
        this.themeManager = new ThemeManager();
        this.notificationManager = new NotificationManager();
        this.networkManager = new NetworkManager();
        this.scriptManager = new ScriptManager();
        this.targetManager = new TargetManager();
        this.cheatDetector = new CheatDetector();
        this.constantManager = new ConstantManager();
        this.eventBus = new EventBus();
        this.packetLogManager = new PacketLogManager();

        // Register Components
        ClassIndex.getSubclasses(Component.class, Component.class.getClassLoader()).forEach(clazz -> {
            try {
                if (!clazz.isAnnotationPresent(Clover.class) && !Modifier.isAbstract(clazz.getModifiers())) {
                    this.componentManager.add(clazz.newInstance());
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });

        // Register Modules
        ClassIndex.getSubclasses(Module.class, Module.class.getClassLoader()).forEach(clazz -> {
            try {
                if (!clazz.isAnnotationPresent(Clover.class) && !Modifier.isAbstract(clazz.getModifiers()) &&
                        (!clazz.isAnnotationPresent(DevelopmentFeature.class) || Client.DEVELOPMENT_SWITCH)) {
                    this.moduleManager.add(clazz.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Register Commands
        ClassIndex.getSubclasses(Command.class, Command.class.getClassLoader()).forEach(clazz -> {
            try {
                if (!clazz.isAnnotationPresent(Clover.class)) {
                    this.commandManager.add(clazz.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Anti Packet Log
        ClassIndex.getSubclasses(Check.class, Check.class.getClassLoader()).forEach(clazz -> {
            try {
                this.packetLogManager.add(clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Minecraft.riseLaunchStage++;
        System.out.println("Init Features");


        // Init Managers
        this.notificationManager.init();
        this.targetManager.init();
        this.dataManager.init();
        this.McqAFVeaWB.init();
        this.moduleManager.init();
        this.securityManager.init();
        this.botManager.init();
        this.componentManager.init();
        this.commandManager.init();
        this.fileManager.init();
        this.configManager.init();
        this.altManager.init();
        this.insultManager.init();
        this.scriptManager.init();
        this.packetLogManager.init();

        Minecraft.riseLaunchStage++;
        System.out.println("Init Managers");

        final File file = new File(ConfigManager.CONFIG_DIRECTORY, "latest.json");
        this.configFile = new ConfigFile(file, FileType.CONFIG);
        this.configFile.allowKeyCodeLoading();
        this.configFile.read();

        this.insultManager.update();
        this.insultManager.forEach(InsultFile::read);

        this.standardClickGUI = new RiseClickGUI();
        this.dropdownClickGUI = new DropdownClickGUI();
        this.altManagerMenu = new AltManagerMenu();

        this.creativeTab = new RiseTab();

        ViaMCP.staticInit();

        Minecraft.riseLaunchStage++;
        System.out.println("Finished");

        Display.setTitle(NAME + " " + VERSION_FULL);
    }

    /**
     * The main method when the Minecraft#startGame method is about
     * finish executing our client gets called and that's where we
     * can start loading our own classes and modules.
     */
    public void initClover() {
        // Set Client and Version name
        NAME = "Clover";
        VERSION = "1.0";

        // Init
        MathConst.calculate();

        this.moduleManager = new ModuleManager();
        this.componentManager = new ComponentManager();
        this.fileManager = new FileManager();
        this.configManager = new ConfigManager();
        this.altManager = new AltManager();
        this.dataManager = new DataManager();
        this.themeManager = new ThemeManager();
        this.networkManager = new NetworkManager();

        // For devs
        this.commandManager = new CommandManager();

        // Register components
        ClassIndex.getSubclasses(Component.class, Component.class.getClassLoader()).forEach(clazz -> {
            try {
                if (!clazz.isAnnotationPresent(Rise.class) && !Modifier.isAbstract(clazz.getModifiers())) {
                    this.componentManager.add(clazz.newInstance());
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });

        // Register Modules
        AtomicInteger index = new AtomicInteger();
        ClassIndex.getSubclasses(Module.class, Module.class.getClassLoader()).forEach(clazz -> {
            try {
                index.getAndIncrement();
                if ((!clazz.isAnnotationPresent(Rise.class)) && !Modifier.isAbstract(clazz.getModifiers()) &&
                        (clazz.isAnnotationPresent(DevelopmentFeature.class) || Client.DEVELOPMENT_SWITCH)) {
                    this.moduleManager.add(clazz.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Register Commands
        ClassIndex.getSubclasses(Command.class, Command.class.getClassLoader()).forEach(clazz -> {
            try {
                if (!clazz.isAnnotationPresent(Clover.class) && (clazz.isAnnotationPresent(DevelopmentFeature.class) || Client.DEVELOPMENT_SWITCH)) {
                    this.commandManager.add(clazz.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Init Managers
        this.dataManager.init();
        this.moduleManager.init();
        this.componentManager.init();
        this.fileManager.init();
        this.configManager.init();
        this.altManager.init();

        //Temp for devs
        this.commandManager.init();

        final File file = new File(ConfigManager.CONFIG_DIRECTORY, "latest.json");
        this.configFile = new ConfigFile(file, FileType.CONFIG);
        this.configFile.allowKeyCodeLoading();
        this.configFile.read();

        this.standardClickGUI = new RiseClickGUI();
        this.dropdownClickGUI = new DropdownClickGUI();
        this.altManagerMenu = new AltManagerMenu();

        ViaMCP.staticInit();
        ViaMCP.setHidden(true);

        Display.setTitle(NAME + " " + VERSION_FULL);
    }

    /**
     * The terminate method is called when the Minecraft client is shutting
     * down, so we can cleanup our stuff and ready ourselves for the client quitting.
     */
    public void terminate() {
        this.configFile.write();
    }
}
