package cafe.corrosion;

import cafe.corrosion.command.manager.CommandManager;
import cafe.corrosion.config.ConfigManager;
import cafe.corrosion.event.bus.EventBus;
import cafe.corrosion.event.handler.IHandler;
import cafe.corrosion.event.impl.EventKeyPress;
import cafe.corrosion.font.FontManager;
import cafe.corrosion.menu.drag.GuiComponentManager;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.manager.ModuleManager;
import cafe.corrosion.notification.Notification;
import cafe.corrosion.property.registry.PropertyRegistry;
import cafe.corrosion.social.CorrosionSocket;
import cafe.corrosion.social.feature.impl.NotificationFeature;
import cafe.corrosion.util.render.Blurrer;
import cafe.corrosion.version.Version;
import java.nio.file.Path;
import java.nio.file.Paths;

public enum Corrosion implements IHandler {
    INSTANCE;

    private final Blurrer blurrer = new Blurrer(false);
    private final CommandManager commandManager = new CommandManager();
    private final EventBus eventBus = new EventBus();
    private final FontManager fontManager = new FontManager();
    private final ModuleManager moduleManager = new ModuleManager();
    private final GuiComponentManager guiComponentManager = new GuiComponentManager();
    private final ConfigManager configManager = new ConfigManager();
    private final PropertyRegistry propertyRegistry = new PropertyRegistry();
    private final CorrosionSocket corrosionSocket = new CorrosionSocket();
    private final Version version = (new Version()).setMajor("2").setMinor("0").setBuild("0").setDev(true);
    private final Path keybindPath = Paths.get("keybinds.json");
    private final Path altsPath = Paths.get("alts.json");

    private Corrosion() {
        this.eventBus.register(this, EventKeyPress.class, (event) -> {
            int keyCode = event.getKeyCode();
            this.moduleManager.getIf((module) -> {
                return module.getKeyCode() == keyCode;
            }).forEach(Module::toggle);
        });
    }

    public void end() {
        this.configManager.exportSettings();
    }

    public boolean isEnabled() {
        return true;
    }

    public void addNotification(Notification notification) {
        this.corrosionSocket.getFeatures().stream().filter((feature) -> {
            return feature instanceof NotificationFeature;
        }).forEach((feature) -> {
            ((NotificationFeature)feature).displayNotification(notification);
        });
    }

    public Blurrer getBlurrer() {
        return this.blurrer;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public EventBus getEventBus() {
        return this.eventBus;
    }

    public FontManager getFontManager() {
        return this.fontManager;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public GuiComponentManager getGuiComponentManager() {
        return this.guiComponentManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public PropertyRegistry getPropertyRegistry() {
        return this.propertyRegistry;
    }

    public CorrosionSocket getCorrosionSocket() {
        return this.corrosionSocket;
    }

    public Version getVersion() {
        return this.version;
    }

    public Path getKeybindPath() {
        return this.keybindPath;
    }

    public Path getAltsPath() {
        return this.altsPath;
    }
}
