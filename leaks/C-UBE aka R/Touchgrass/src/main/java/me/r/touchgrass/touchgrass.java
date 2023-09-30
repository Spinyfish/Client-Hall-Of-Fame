package me.r.touchgrass;

import com.thealtening.auth.AltService;
import com.vdurmont.semver4j.Semver;
import me.r.touchgrass.antidump.AntiDump;
import me.r.touchgrass.events.EventWorldListener;
import me.r.touchgrass.file.files.ClickGuiConfig;
import me.r.touchgrass.file.files.ModuleConfig;
import me.r.touchgrass.file.files.SettingsConfig;
import me.r.touchgrass.module.ModuleManager;
import me.r.touchgrass.ui.ingame.HUD;
import me.r.touchgrass.module.modules.gui.MainMenuModule;
import me.r.touchgrass.ui.clickgui.ClickGui;
import me.r.touchgrass.ui.ingame.components.ArrayList;
import me.r.touchgrass.ui.ingame.components.Watermark;
import me.r.touchgrass.ui.ingame.style.styles.Classic;
import me.r.touchgrass.ui.ingame.style.styles.New;
import me.r.touchgrass.ui.mainmenu.MainMenu;
import me.r.touchgrass.font.FontHelper;
import me.r.touchgrass.utils.HTTPUtil;
import me.r.touchgrass.utils.KeybindUtil;
import me.r.touchgrass.utils.Utils;
import me.r.touchgrass.altmanager.account.AccountManager;
import me.r.touchgrass.command.CommandManager;
import me.r.touchgrass.settings.SettingsManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.swing.*;
import java.io.*;

public class touchgrass {

    public static final String name = "touchgrass";
    public static final String devs[] = {"r", "you!"};
    public static final String prefix = "§7[§9" + name + "§7]";

    public static String version = "1.12.5";
    public static final String semantic_version = "1.12.5";

    public static final String github = "https://github.com/h/touchgrass/";
    public static final String release = github + "releases/";
    public static final String tags = release + "tag/" + semantic_version + "/";
    public static final String currentCommitURL = github + "commit/" + HTTPUtil.getCurrentCommitHash();

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        AntiDump.check();
    }

    private static touchgrass instance;

    public ModuleManager moduleManager;
    public SettingsManager settingsManager;
    public KeybindUtil keybindManager;
    public CommandManager commandManager;
    public AccountManager accountManager;
    public AltService altService;
    public ClickGui clickgui;
    public File directory;
    public HUD hud;

    public boolean outdated;
    public boolean panic = false;
    public boolean firstStart;
    public boolean isStableBuild = false;
    public String newversion;

    public boolean hasNewFiles;

    public touchgrass() {
        instance = this;
    }

    public void startClient() {
        directory = new File(Minecraft.getMinecraft().mcDataDir, name);
        if (!this.directory.exists()) {
            this.firstStart = true;
            directory.mkdir();
        }

        if(new File(directory, "modules.json").exists() || new File(directory, "settings.json").exists() || new File(directory, "clickgui.json").exists()) {
            hasNewFiles = true;
        } else {
            Utils.log("Old Files detected! Will be deleted after game shutdown.");
        }

        if(!isStableBuild) {
            // get commit dates
            HTTPUtil.getCurrentCommitDate();
            // add git commit hash to version
            version += String.format(" §7| %s", "hi");
        }

        moduleManager = new ModuleManager();
        settingsManager = new SettingsManager();
        keybindManager = new KeybindUtil();
        commandManager = new CommandManager();
        accountManager = new AccountManager(new File(this.directory.toString()));
        altService = new AltService();
        clickgui = new ClickGui();
        FontHelper.loadFonts();
        moduleManager.addModules();
        hud = new HUD();

        this.isOutdated();
        new MainMenu();
        Classic.classicArrayThread();
        New.newArrayThread();
        if (this.firstStart) {
            moduleManager.getModule(MainMenuModule.class).setEnabled();
            moduleManager.getModule(HUD.class).setEnabled();
            moduleManager.getModule(ArrayList.class).setEnabled();
            moduleManager.getModule(Watermark.class).setEnabled();
        }
    }

    public static touchgrass getClient() {
        return instance;
    }

    // removing in 1.12.1 or 1.13
    public void stopClient() {
        ModuleConfig moduleConfig = new ModuleConfig();
        moduleConfig.saveConfig();
        SettingsConfig settingsConfig = new SettingsConfig();
        settingsConfig.saveConfig();
        ClickGuiConfig clickGuiConfig = new ClickGuiConfig();
        clickGuiConfig.saveConfig();
        if(!hasNewFiles) {
            File oldVisibleFile = new File(directory, "visible.txt");
            if(oldVisibleFile.delete()) {
                Utils.log("Deleted old Visible Settings!");
            }
            File oldModuleFile = new File(directory, "modules.txt");
            if(oldModuleFile.delete()) {
                Utils.log("Deleted old Modules Settings!");
            }
            File oldKeybindFile = new File(directory, "binds.txt");
            if(oldKeybindFile.delete()) {
                Utils.log("Deleted old Binds Settings!");
            }
            File oldSliderFile = new File(directory, "slider.txt");
            if(oldSliderFile.delete()) {
                Utils.log("Deleted old Slider Settings!");
            }
            File oldButtonFile = new File(directory, "button.txt");
            if(oldButtonFile.delete()) {
                Utils.log("Deleted old Button Settings!");
            }
            File oldComboBoxFile = new File(directory, "combobox.txt");
            if(oldComboBoxFile.delete()) {
                Utils.log("Deleted old Combobox Settings!");
            }
            File oldTextFile = new File(directory, "text.txt");
            if(oldTextFile.delete()) {
                Utils.log("Deleted old Text Settings!");
            }
            File oldClickGuiFile = new File(directory, "clickgui.txt");
            if(oldClickGuiFile.delete()) {
                Utils.log("Deleted old ClickGui Settings!");
            }
        }
        panic = false;
    }

    public void isOutdated() {
    }
}

