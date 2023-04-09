package tech.dort.dortware;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.lwjgl.opengl.Display;
import tech.dort.dortware.api.config.ConfigManager;
import tech.dort.dortware.api.file.FileManager;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.impl.events.KeyboardEvent;
import tech.dort.dortware.impl.managers.*;

import java.net.Proxy;

/**
 * @author Dort
 */
public enum Client {
    INSTANCE();

    private String user = "";
    private String uid = "";
    private ValueManager valueManager;
    private ModuleManager moduleManager;
    private EventBus eventBus;
    private FileManager fileManager;
    private CommandManager commandManager;
    private Proxy proxy = Proxy.NO_PROXY;
    private ConfigManager configManager;
    private FriendManager friendManager;
    private FontManager fontManager;
    private NotificationManager notificationManager;
    private static String alt;


    /**
     * Initializes this {@code Client}
     */
    public final void initialize() {
        fontManager = new FontManager();
        String clientName = "Meme";
        eventBus = new EventBus(clientName);
        valueManager = new ValueManager();
        moduleManager = new ModuleManager();
        fileManager = new FileManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        notificationManager = new NotificationManager();
        configManager = new ConfigManager();
        eventBus.register(this);

        Display.setTitle("Meme");
    }

    @Subscribe
    public void onKeyboardClick(KeyboardEvent event) {
        for (Module module : moduleManager.getObjects()) {
            if (module.getKeyBind() != 0 && event.getKey() == module.getKeyBind())
                module.toggle();
        }
    }

    public String getClientVersion() {
        return "1.5.2";
    }

    public String getClientEdition() {
        return "Funny Packet Edition";
    }

    public ValueManager getValueManager() {
        return valueManager;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public FontManager getFontManager() {
        return fontManager;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public static String[] getSpectatorAlt() {
        return alt == null ? null : alt.split(":");
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUID(String uid) {
        this.uid = uid;
    }

    public static void setAlt(String alt) {
        Client.alt = alt;
    }
}
