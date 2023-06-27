package rip.athena.client;

import rip.athena.client.utils.*;
import rip.athena.client.gui.notifications.*;
import rip.athena.client.cosmetics.*;
import rip.athena.client.account.*;
import rip.athena.client.config.save.*;
import rip.athena.client.modules.*;
import rip.athena.client.theme.*;
import rip.athena.client.macros.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.utils.discord.*;
import rip.athena.client.socket.*;
import javax.swing.*;
import java.awt.*;
import rip.athena.client.utils.input.*;
import rip.athena.client.events.types.client.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import rip.athena.client.events.*;
import java.io.*;
import java.nio.file.*;

public class Athena
{
    public static final Athena INSTANCE;
    public static final File MAIN_DIR;
    public static final File CONFIGS_DIR;
    public static final File ACCOUNTS_DIR;
    private final PrefixedLogger log;
    private final String clientName = "Athena";
    private final String clientVersion = "0.0.3";
    private final String clientBuild = "062623";
    private NotificationManager notificationManager;
    private CosmeticsController cosmeticsController;
    private AccountManager accountManager;
    private ConfigManager configManager;
    private ModuleManager moduleManager;
    private ThemeManager themeManager;
    private MacroManager macroManager;
    private HUDManager hudManager;
    private DiscordRPC discordRPC;
    private EventBus eventBus;
    public static boolean hasSent;
    private boolean isGameRunningForeground;
    
    public Athena() {
        this.log = new PrefixedLogger("Athena");
        this.isGameRunningForeground = true;
    }
    
    public void initClient() {
        final String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("win")) {
            this.discordRPC = new DiscordRPC();
        }
        this.createDirectoryIfNotExists(Athena.MAIN_DIR);
        this.createFileIfNotExists(Athena.ACCOUNTS_DIR);
        if (SocketClient.isClientRunning()) {
            JOptionPane.showMessageDialog(null, "Port 45376 already in use.");
            System.exit(0);
        }
        this.configManager = new ConfigManager(Athena.CONFIGS_DIR);
        this.accountManager = new AccountManager();
        this.moduleManager = new ModuleManager();
        this.themeManager = new ThemeManager();
        this.macroManager = new MacroManager();
        this.hudManager = new HUDManager();
        this.eventBus = new EventBus();
        this.notificationManager = new NotificationManager();
        this.cosmeticsController = new CosmeticsController();
        this.registerEvents();
        this.configManager.postInit();
        if (this.cosmeticsController.getCapeManager().getSelectedCape() == null) {
            this.cosmeticsController.getCapeManager().setSelectedCape(this.cosmeticsController.getCapeManager().getCape("None"));
        }
    }
    
    public void registerEvents() {
        this.eventBus.register(new KeybindManager());
        this.eventBus.register(this.macroManager);
        this.eventBus.register(this.hudManager);
        this.eventBus.register(this);
    }
    
    @SubscribeEvent
    public void onClientTick(final ClientTickEvent event) {
        if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
            final String currentUsername = Minecraft.getMinecraft().thePlayer.getGameProfile().getName();
            if (!Athena.hasSent || !currentUsername.equals(SocketClient.getCurrentUsername())) {
                if (Athena.hasSent && !currentUsername.equals(SocketClient.getCurrentUsername())) {
                    Athena.hasSent = false;
                }
                final String uuid = Minecraft.getMinecraft().thePlayer.getUniqueID().toString();
                System.out.println(SocketClient.sendRequest("start", currentUsername + ":" + uuid + ":true"));
                SocketClient.setCurrentUsername(currentUsername);
                Athena.hasSent = true;
            }
        }
        else {
            if (Athena.hasSent && this.isGameRunningForeground) {
                Athena.hasSent = false;
            }
            this.isGameRunningForeground = false;
        }
        if (Minecraft.getMinecraft().isFullScreen() || Display.isActive()) {
            this.isGameRunningForeground = true;
        }
    }
    
    private void createDirectoryIfNotExists(final File directory) {
        if (!directory.exists()) {
            directory.mkdir();
        }
    }
    
    private void createFileIfNotExists(final File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void shutdownClient() {
        this.log.info("Shutting down client");
    }
    
    public String getClientBuild() {
        return "062623";
    }
    
    public String getClientName() {
        return "Athena";
    }
    
    public String getClientVersion() {
        return "0.0.3";
    }
    
    public PrefixedLogger getLog() {
        return this.log;
    }
    
    public NotificationManager getNotificationManager() {
        return this.notificationManager;
    }
    
    public CosmeticsController getCosmeticsController() {
        return this.cosmeticsController;
    }
    
    public AccountManager getAccountManager() {
        return this.accountManager;
    }
    
    public ConfigManager getConfigManager() {
        return this.configManager;
    }
    
    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }
    
    public ThemeManager getThemeManager() {
        return this.themeManager;
    }
    
    public MacroManager getMacroManager() {
        return this.macroManager;
    }
    
    public HUDManager getHudManager() {
        return this.hudManager;
    }
    
    public DiscordRPC getDiscordRPC() {
        return this.discordRPC;
    }
    
    public EventBus getEventBus() {
        return this.eventBus;
    }
    
    public boolean isGameRunningForeground() {
        return this.isGameRunningForeground;
    }
    
    static {
        INSTANCE = new Athena();
        MAIN_DIR = Paths.get(Minecraft.getMinecraft().mcDataDir.getAbsolutePath(), "settings").toFile();
        CONFIGS_DIR = Paths.get(Athena.MAIN_DIR.getAbsolutePath(), "configs").toFile();
        ACCOUNTS_DIR = new File(Athena.MAIN_DIR.getAbsolutePath(), "accounts.json");
        Athena.hasSent = false;
    }
}
