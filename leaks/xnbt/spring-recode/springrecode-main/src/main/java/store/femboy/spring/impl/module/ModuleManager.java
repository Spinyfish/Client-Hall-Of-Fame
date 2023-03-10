package store.femboy.spring.impl.module;

import net.minecraft.client.Minecraft;
import store.femboy.spring.Spring;
import store.femboy.spring.impl.module.impl.client.DiscordRPCModule;
import store.femboy.spring.impl.module.impl.client.MemFix;
import store.femboy.spring.impl.module.impl.combat.AntiBot;
import store.femboy.spring.impl.module.impl.combat.Criticals;
import store.femboy.spring.impl.module.impl.combat.KillAura;
import store.femboy.spring.impl.module.impl.exploit.Disabler;
import store.femboy.spring.impl.module.impl.exploit.Regen;
import store.femboy.spring.impl.module.impl.movement.*;
import store.femboy.spring.impl.module.impl.player.NoFall;
import store.femboy.spring.impl.module.impl.player.Velocity;
import store.femboy.spring.impl.module.impl.visual.HUD;
import store.femboy.spring.impl.module.impl.visual.clickgui.ClickGui;
import store.femboy.spring.impl.module.impl.player.Fastplace;
import store.femboy.spring.impl.module.impl.visual.Blur;
import store.femboy.spring.impl.module.impl.visual.Fullbright;
import store.femboy.spring.impl.module.impl.visual.TargetHud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    public ArrayList<Module> modules = new ArrayList<>();

    public ModuleManager(){
        init();
    }

    private void init() {
        addModule(new DiscordRPCModule());
        addModule(new Fullbright());
        addModule(new Fastplace());
        addModule(new Criticals());
        addModule(new TargetHud());
        addModule(new Velocity());
        addModule(new KillAura());
        addModule(new Disabler());
        addModule(new Scaffold());
        addModule(new NewScaffold());
        addModule(new ClickGui());
        addModule(new AntiBot());
        addModule(new NoFall());
        addModule(new Sprint());
        addModule(new MemFix());
        addModule(new Flight());
        addModule(new Speed());
        addModule(new Regen());
        addModule(new Blur());
        addModule(new HUD());

        if(Spring.INSTANCE.releaseType == ReleaseType.BETA || Spring.INSTANCE.releaseType == ReleaseType.DEV){

        }

        if(Spring.INSTANCE.releaseType == ReleaseType.DEV){
        }
    }

    public void addModule(Module module){
        modules.add(module);
    }
    public ArrayList<Module> getModules(){
        return modules;
    }
    public Module getModule(String name){
        for(Module module : modules){
            if(module.getName().equalsIgnoreCase(name)){
                return module;
            }
        }
        return null;
    }

    public static List<Module> getModulesByCategory(Category c) {
        List<Module> modules = new ArrayList<Module>();

        for(Module m : Spring.INSTANCE.getManager().modules) {
            if(m.category == c)
                modules.add(m);


        }
        return modules;
    }

    public List<Module> getSortedModules() {
        return this.getModules().stream().sorted(Comparator.comparing(module -> Minecraft.getMinecraft().fontRendererObj.getStringWidth(((Module) module).getName())).reversed()).collect(Collectors.toList());
    }

}

