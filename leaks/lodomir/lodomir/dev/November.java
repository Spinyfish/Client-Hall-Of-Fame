/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 *  com.google.common.eventbus.Subscribe
 *  org.lwjgl.opengl.Display
 */
package lodomir.dev;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lodomir.dev.alt.AltManager;
import lodomir.dev.commands.CommandManager;
import lodomir.dev.event.impl.game.EventChat;
import lodomir.dev.event.impl.game.EventKey;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.ModuleManager;
import lodomir.dev.ui.HUD;
import lodomir.dev.ui.clickgui.Clickgui;
import lodomir.dev.ui.font.FontManager;
import lodomir.dev.utils.discord.DiscordRP;
import lodomir.dev.utils.file.FileHandler;
import lodomir.dev.utils.file.FileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.opengl.Display;
import viamcp.ViaMCP;

public enum November {
    INSTANCE;

    public Minecraft mc = Minecraft.getMinecraft();
    public String name = "November";
    public String release = "251022";
    public String build = "Development";
    public String version = "0.2";
    public String user;
    public EventBus bus;
    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public FileUtils fileUtils;
    public lodomir.dev.ui.click.Clickgui clickgui;
    public AltManager altManager;
    public Clickgui clickui;
    public DiscordRP discordRP = new DiscordRP();
    public HUD hud;
    private FileHandler file;
    public FontManager fm;

    public void init() {
        November.ConsoleLog("Loading November Client...");
        this.discordRP.start();
        November.ConsoleLog("Loaded DiscordRP [1/8]");
        this.bus = new EventBus();
        November.ConsoleLog("Loaded EventBus [2/8]");
        this.altManager = new AltManager();
        November.ConsoleLog("Loaded Alt Manager [3/8]");
        this.fileUtils = new FileUtils();
        try {
            if (!this.fileUtils.dirExists()) {
                this.fileUtils.createDir();
            }
            if (!this.fileUtils.exists("Config" + File.separator)) {
                this.fileUtils.createDirectory("Config" + File.separator);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        November.ConsoleLog("Created Client Directory [4/8]");
        this.moduleManager = new ModuleManager();
        November.ConsoleLog("Loaded Module Manager [5/8]");
        this.commandManager = new CommandManager();
        November.ConsoleLog("Loaded Command Manager [6/8]");
        this.file = new FileHandler();
        try {
            this.file.load();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        November.ConsoleLog("Loaded Client Config [7/8]");
        this.fm = new FontManager();
        November.ConsoleLog("Loaded Font Manager [8/8]");
        try {
            ViaMCP.getInstance().start();
            ViaMCP.getInstance().initAsyncSlider();
            ViaMCP.getInstance().initAsyncSlider(3, 3, 110, 20);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.bus.register((Object)this);
        this.hud = new HUD();
        this.clickui = new Clickgui();
        this.clickgui = new lodomir.dev.ui.click.Clickgui();
        Display.setTitle((String)(this.name + " Client "));
        November.ConsoleLog("Loaded November");
        if (this.user == "null" || this.user == null) {
            this.setUser("user");
        }
    }

    public void stop() {
        this.file.save();
        this.bus.unregister((Object)this);
    }

    public EventBus getBus() {
        return this.bus;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    @Subscribe
    public void setUser(String user) {
        this.user = user;
    }

    @Subscribe
    public void onKey(EventKey e) {
        for (Module m : this.moduleManager.getModules()) {
            if (m.getKey() != e.getKey()) continue;
            if (m.isEnabled()) {
                m.setEnabled(false);
                continue;
            }
            m.setEnabled(true);
        }
    }

    @Subscribe
    public void onChat(EventChat e) {
        this.commandManager.handleChat(e);
        for (Module m : November.INSTANCE.moduleManager.modules) {
            if (m.toggled) continue;
        }
    }

    public static void ConsoleLog(String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        message = "[" + dtf.format(now) + "] [" + November.INSTANCE.name + " Client]: " + message;
        System.out.println(message);
    }

    public static void Log(String message) {
        message = "\u00a7c" + November.INSTANCE.name + "\u00a77 > " + message;
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }
}

