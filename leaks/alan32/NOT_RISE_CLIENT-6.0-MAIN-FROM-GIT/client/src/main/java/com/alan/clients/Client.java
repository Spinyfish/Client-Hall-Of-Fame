package com.alan.clients;

import by.radioegor146.nativeobfuscator.Native;
import com.alan.clients.anticheat.CheatDetector;
import com.alan.clients.api.Hidden;
import com.alan.clients.bots.BotManager;
import com.alan.clients.command.Command;
import com.alan.clients.command.CommandManager;
import com.alan.clients.component.Component;
import com.alan.clients.component.ComponentManager;
import com.alan.clients.creative.RiseTab;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.manager.ModuleManager;
import com.alan.clients.network.NetworkManager;
import com.alan.clients.newevent.bus.impl.EventBus;
import com.alan.clients.packetlog.Check;
import com.alan.clients.packetlog.api.manager.PacketLogManager;
import com.alan.clients.protection.check.api.McqBFVbnWB;
import com.alan.clients.protection.manager.TargetManager;
import com.alan.clients.script.ScriptManager;
import com.alan.clients.security.SecurityFeatureManager;
import com.alan.clients.ui.click.clover.CloverClickGUI;
import com.alan.clients.ui.click.dropdown.DropdownClickGUI;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.menu.impl.alt.AltManagerMenu;
import com.alan.clients.ui.theme.ThemeManager;
import com.alan.clients.util.ReflectionUtil;
import com.alan.clients.util.file.FileManager;
import com.alan.clients.util.file.FileType;
import com.alan.clients.util.file.alt.AltManager;
import com.alan.clients.util.file.config.ConfigFile;
import com.alan.clients.util.file.config.ConfigManager;
import com.alan.clients.util.file.data.DataManager;
import com.alan.clients.util.file.insult.InsultFile;
import com.alan.clients.util.file.insult.InsultManager;
import com.alan.clients.util.localization.Locale;
import com.alan.clients.util.math.MathConst;
import com.alan.clients.util.value.ConstantManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.viamcp.ViaMCP;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main class where the client is loaded up.
 * Anything related to the client will start from here and managers etc instances will be stored in this class.
 *
 * @author Tecnio
 * @since 29/08/2021
 */
@Getter
@Native
public enum Client {

    /**
     * Simple enum instance for our client as enum instances
     * are immutable and are very easy to create and use.
     */
    INSTANCE;

    public static String NAME = "Rise";
    public static String VERSION = "6.0";
    public static String VERSION_FULL = "6.0"; // Used to give more detailed build info on beta builds
    public static String VERSION_DATE = "February 12, 2023";

    public static boolean DEVELOPMENT_SWITCH = true;
    public static boolean BETA_SWITCH = true;
    public static boolean FIRST_LAUNCH = true;
    public static Type CLIENT_TYPE = Type.RISE;


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
    @Setter
    private NetworkManager networkManager;
    @Setter
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
        this.networkManager = new NetworkManager();
        this.scriptManager = new ScriptManager();
        this.targetManager = new TargetManager();
        this.cheatDetector = new CheatDetector();
        this.constantManager = new ConstantManager();
        this.eventBus = new EventBus();
        this.packetLogManager = new PacketLogManager();

        // Register
        String[] paths = {
                "com.alan.clients.",
                "hackclient."
        };

        for (String path : paths) {
            if (!ReflectionUtil.dirExist(path)) {
                continue;
            }

            Class<?>[] classes = ReflectionUtil.getClassesInPackage(path);

            for (Class<?> clazz : classes) {
                try {
                    if (clazz.isAnnotationPresent(Hidden.class)) continue;

                    if (Component.class.isAssignableFrom(clazz) && clazz != Component.class) {
                        this.componentManager.add((Component) clazz.getConstructor().newInstance());
                    } else if (Module.class.isAssignableFrom(clazz) && clazz != Module.class) {
                        this.moduleManager.add((Module) clazz.getConstructor().newInstance());
                    } else if (Command.class.isAssignableFrom(clazz) && clazz != Command.class) {
                        this.commandManager.add((Command) clazz.getConstructor().newInstance());
                    } else if (Check.class.isAssignableFrom(clazz) && clazz != Check.class) {
                        this.packetLogManager.add((Check) clazz.getConstructor().newInstance());
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
            }

            break;
        }

        // Init Managers
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

