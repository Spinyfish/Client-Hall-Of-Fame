/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.modules;

import java.util.ArrayList;
import lodomir.dev.November;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.combat.AntiBot;
import lodomir.dev.modules.impl.combat.AutoClicker;
import lodomir.dev.modules.impl.combat.AutoPot;
import lodomir.dev.modules.impl.combat.Criticals;
import lodomir.dev.modules.impl.combat.KillAura;
import lodomir.dev.modules.impl.combat.Regen;
import lodomir.dev.modules.impl.combat.TargetStrafe;
import lodomir.dev.modules.impl.combat.Velocity;
import lodomir.dev.modules.impl.movement.Fly;
import lodomir.dev.modules.impl.movement.HighJump;
import lodomir.dev.modules.impl.movement.InventoryMove;
import lodomir.dev.modules.impl.movement.KeepSprint;
import lodomir.dev.modules.impl.movement.LongJump;
import lodomir.dev.modules.impl.movement.NoSlow;
import lodomir.dev.modules.impl.movement.Speed;
import lodomir.dev.modules.impl.movement.Sprint;
import lodomir.dev.modules.impl.movement.Step;
import lodomir.dev.modules.impl.movement.Strafe;
import lodomir.dev.modules.impl.other.AutoHypixel;
import lodomir.dev.modules.impl.other.Disabler;
import lodomir.dev.modules.impl.other.LightningDetector;
import lodomir.dev.modules.impl.other.LunarSpoofer;
import lodomir.dev.modules.impl.other.NoGuiClose;
import lodomir.dev.modules.impl.other.NoRotate;
import lodomir.dev.modules.impl.other.Notifications;
import lodomir.dev.modules.impl.other.PingSpoof;
import lodomir.dev.modules.impl.other.ResetVL;
import lodomir.dev.modules.impl.other.SelfDestruct;
import lodomir.dev.modules.impl.other.Spammer;
import lodomir.dev.modules.impl.other.Timer;
import lodomir.dev.modules.impl.player.AutoRespawn;
import lodomir.dev.modules.impl.player.AutoTool;
import lodomir.dev.modules.impl.player.Blink;
import lodomir.dev.modules.impl.player.ChestStealer;
import lodomir.dev.modules.impl.player.FastEat;
import lodomir.dev.modules.impl.player.FastMine;
import lodomir.dev.modules.impl.player.FastPlace;
import lodomir.dev.modules.impl.player.InvManager;
import lodomir.dev.modules.impl.player.NewScaffold;
import lodomir.dev.modules.impl.player.NoFall;
import lodomir.dev.modules.impl.player.NoVoid;
import lodomir.dev.modules.impl.player.Phase;
import lodomir.dev.modules.impl.player.Scaffold;
import lodomir.dev.modules.impl.render.Ambience;
import lodomir.dev.modules.impl.render.Animations;
import lodomir.dev.modules.impl.render.Cape;
import lodomir.dev.modules.impl.render.Chams;
import lodomir.dev.modules.impl.render.ChinaHat;
import lodomir.dev.modules.impl.render.ClickGUI;
import lodomir.dev.modules.impl.render.Crosshair;
import lodomir.dev.modules.impl.render.ESP;
import lodomir.dev.modules.impl.render.FullBright;
import lodomir.dev.modules.impl.render.ImageESP;
import lodomir.dev.modules.impl.render.Interface;
import lodomir.dev.modules.impl.render.NameProtect;
import lodomir.dev.modules.impl.render.Nametags;
import lodomir.dev.modules.impl.render.Radar;
import lodomir.dev.modules.impl.render.Scoreboard;
import lodomir.dev.modules.impl.render.SessionInfo;
import lodomir.dev.modules.impl.render.TabGUI;
import lodomir.dev.modules.impl.render.TargetHUD;
import lodomir.dev.modules.impl.render.Test;
import lodomir.dev.settings.Setting;

public class ModuleManager {
    public ArrayList<Module> modules = new ArrayList();
    public ArrayList<Setting> settings = new ArrayList();
    private String getSettingName;
    private String getSettingSettingName;
    private Setting getSettingSetting;

    public ModuleManager() {
        this.init();
    }

    public void init() {
        this.add(new Velocity());
        this.add(new AutoClicker());
        this.add(new AntiBot());
        this.add(new TargetStrafe());
        this.add(new AutoPot());
        this.add(new KillAura());
        this.add(new Regen());
        this.add(new Criticals());
        this.add(new Sprint());
        this.add(new Strafe());
        this.add(new Fly());
        this.add(new Speed());
        this.add(new LongJump());
        this.add(new HighJump());
        this.add(new InventoryMove());
        this.add(new KeepSprint());
        this.add(new NoSlow());
        this.add(new Step());
        this.add(new ChestStealer());
        this.add(new InvManager());
        this.add(new AutoRespawn());
        this.add(new FastPlace());
        this.add(new FastMine());
        this.add(new FastEat());
        this.add(new NewScaffold());
        this.add(new Scaffold());
        this.add(new AutoTool());
        this.add(new NoFall());
        this.add(new NoVoid());
        this.add(new Blink());
        this.add(new Phase());
        this.add(new Ambience());
        this.add(new Animations());
        this.add(new FullBright());
        this.add(new Crosshair());
        this.add(new TargetHUD());
        this.add(new Nametags());
        this.add(new Scoreboard());
        this.add(new SessionInfo());
        this.add(new NameProtect());
        this.add(new Interface());
        this.add(new ImageESP());
        this.add(new ChinaHat());
        this.add(new ClickGUI());
        this.add(new Radar());
        this.add(new TabGUI());
        this.add(new Chams());
        this.add(new Cape());
        this.add(new ESP());
        this.add(new LightningDetector());
        this.add(new Notifications());
        this.add(new SelfDestruct());
        this.add(new NoGuiClose());
        this.add(new LunarSpoofer());
        this.add(new AutoHypixel());
        this.add(new ResetVL());
        this.add(new Disabler());
        this.add(new PingSpoof());
        this.add(new Spammer());
        this.add(new Timer());
        this.add(new NoRotate());
        this.add(new Test());
    }

    public ArrayList<Module> getModules() {
        return this.modules;
    }

    public Module getModule(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equals(name)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getModulesByCategory(Category category) {
        ArrayList<Module> modules = new ArrayList<Module>();
        for (Module m : this.modules) {
            if (!m.getCategory().equals((Object)category)) continue;
            modules.add(m);
        }
        return modules;
    }

    public Module getEnabledModules(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equals(name) || !module.isEnabled()) continue;
            return module;
        }
        return null;
    }

    public Module getModuleByName(String moduleName) {
        for (Module m : November.INSTANCE.getModuleManager().getModules()) {
            if (!m.getName().trim().equalsIgnoreCase(moduleName) && !m.toString().equalsIgnoreCase(moduleName.trim())) continue;
            return m;
        }
        return null;
    }

    public ArrayList<Setting> getSettings() {
        return this.settings;
    }

    public Setting getSetting(String moduleName, String settingName) {
        if (this.getSettingName != null && this.getSettingSettingName != null && this.getSettingSetting != null && this.getSettingName.equals(moduleName) && this.getSettingSettingName.equals(settingName)) {
            return this.getSettingSetting;
        }
        for (Module m : this.modules) {
            if (!m.getName().equalsIgnoreCase(moduleName)) continue;
            for (Setting s : m.getSettings()) {
                if (!s.getName().equalsIgnoreCase(settingName)) continue;
                this.getSettingName = moduleName;
                this.getSettingSettingName = settingName;
                this.getSettingSetting = s;
                return s;
            }
        }
        return null;
    }

    public void add(Module module) {
        this.modules.add(module);
    }
}

