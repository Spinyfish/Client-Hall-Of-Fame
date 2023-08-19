/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 19:17
 */

package cc.swift;

import cc.swift.config.ConfigManager;
import cc.swift.gui.clickgui.ClickGui;
import cc.swift.commands.CommandManager;
import cc.swift.module.ModuleManager;
import cc.swift.notification.NotificationManager;
import cc.swift.util.RotationHandler;
import cc.swift.util.player.Player;
import dev.codeman.eventbus.EventBus;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Session;
import viamcp.ViaMCP;

import java.io.File;

@Getter
public enum Swift {
    INSTANCE;

    public final String NAME = "Swift";
    public final String VERSION = "Developer Alpha";
    private EventBus eventBus;
    private ModuleManager moduleManager;
    private NotificationManager notificationManager;
    private ClickGui clickGui;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private RotationHandler rotationHandler;

    private final Player player = new Player();

    public ScaledResolution sr;


    public void init() {

        try
        {
            ViaMCP.getInstance().start();

            // Only use one of the following
            ViaMCP.getInstance().initAsyncSlider(); // For top left aligned slider
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        System.out.println("Initializing Swift");

        Minecraft.getMinecraft().session = new Session("swifthakar420", "", "", "mojang");

        sr = new ScaledResolution(Minecraft.getMinecraft());

        this.eventBus = new EventBus();
        this.configManager = new ConfigManager(new File(Minecraft.getMinecraft().mcDataDir, "swift"));
        this.configManager.loadConfigs();
        this.moduleManager = new ModuleManager();
        this.moduleManager.init();
        this.configManager.loadBinds();
        this.eventBus.subscribe(this.moduleManager);
        this.commandManager = new CommandManager();
        this.commandManager.setupCommands();
        this.eventBus.subscribe(this.commandManager);
        this.rotationHandler = new RotationHandler();
        this.eventBus.subscribe(this.rotationHandler);
        this.notificationManager = new NotificationManager();

        this.clickGui = new ClickGui();

        if (this.configManager.getConfigs().containsKey("default")) {
            this.configManager.getConfig("default").loadConfig();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.configManager.saveBinds();
            this.configManager.saveConfig("default");
        }));
    }
}
