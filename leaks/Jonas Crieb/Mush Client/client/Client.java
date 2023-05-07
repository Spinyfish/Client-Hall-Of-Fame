package client;

;
import client.command.Command;
import client.command.CommandManager;
import client.event.bus.impl.EventBus;
import client.gayclicks.Clickgui;
import client.gui.HWID;
import client.module.Module;
import client.module.DevelopmentFeature;
import client.module.ModuleManager;
import client.util.file.FileManager;
import client.util.file.FileType;
import client.util.file.config.ConfigFile;
import client.util.file.config.ConfigManager;
import lombok.Getter;
import org.atteo.classindex.ClassIndex;
import org.lwjgl.opengl.Display;
import viamcp.ViaMCP;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.security.NoSuchAlgorithmException;

@Getter
public enum Client {

    INSTANCE;

    public static final String NAME = "Mush Client";
    public static final String VERSION = "1.4.0 Nigga Edition";

    public static final boolean DEVELOPMENT_SWITCH = true;
    public static String user;
    public static String rank;
    private EventBus eventBus;

    public static final boolean ball = false;


    private Clickgui clickGui;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private FileManager fileManager;
    private ConfigManager configManager;
    private ConfigFile configFile;

    public void init() {

        this.moduleManager = new ModuleManager();
        this.commandManager = new CommandManager();
        this.fileManager = new FileManager();
        this.configManager = new ConfigManager();
        this.eventBus = new EventBus();


        ClassIndex.getSubclasses(Module.class, Module.class.getClassLoader()).forEach(clazz -> {
            try {
                if (!Modifier.isAbstract(clazz.getModifiers()) &&
                        (!clazz.isAnnotationPresent(DevelopmentFeature.class) || Client.DEVELOPMENT_SWITCH)) {
                    this.moduleManager.add(clazz.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ClassIndex.getSubclasses(Command.class, Command.class.getClassLoader()).forEach(clazz -> {
            try {
                if (!Modifier.isAbstract(clazz.getModifiers()) &&
                        (!clazz.isAnnotationPresent(DevelopmentFeature.class) || Client.DEVELOPMENT_SWITCH)) {
                    this.commandManager.add(clazz.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        this.moduleManager.init();
        this.commandManager.init();
        this.fileManager.init();
        this.configManager.init();

        final File file = new File(ConfigManager.CONFIG_DIRECTORY, "latest.json");
        this.configFile = new ConfigFile(file, FileType.CONFIG);
        this.configFile.allowKeyCodeLoading();
        this.configFile.read();

        ViaMCP.getInstance().start();
        ViaMCP.getInstance().initAsyncSlider();

        Display.setTitle(NAME + " " + VERSION);
    }

    public void terminate() {
        this.configFile.write();
    }
}
