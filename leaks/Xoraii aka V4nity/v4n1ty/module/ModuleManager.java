package v4n1ty.module;

import java.util.ArrayList;

import v4n1ty.events.Event;
import v4n1ty.module.combat.KillAura;
import v4n1ty.module.combat.Velocity;
import v4n1ty.module.movement.Speed;
import v4n1ty.module.movement.Sprint;
import v4n1ty.module.player.*;
import v4n1ty.module.render.*;

public class ModuleManager {

    private static ArrayList<Module> mods;

    public ModuleManager() {
        mods = new ArrayList<Module>();
        //COMBAT
        newMod(new KillAura());
        newMod(new Velocity());

        //MOVEMENT
        newMod(new Sprint());
        newMod(new Speed());

        //PLAYER
        newMod(new ChestStealer());
        newMod(new AutoArmor());
        newMod(new Scaffold());
        newMod(new Breaker());

        //RENDER
        newMod(new ChinaHat());
        newMod(new ClickGui());
        newMod(new FullBright());
        newMod(new TargetHud());

        //MISC
    }

    public Module getModule(Class<? extends Module> clazz) {
        for (Module module : this.mods) {
            if (module.getClass() == clazz)
                return module;
        }
        return null;
    }

    public static void newMod(Module m) {
        mods.add(m);
    }

    public static ArrayList<Module> getModules(){
        return mods;
    }

    public static void onUpdate() {
        for(Module m : mods) {
            m.onUpdate();
        }
    }

    public static void onRender() {
        for(Module m : mods) {
            m.onRender();
        }
    }

    public static void onEvent(Event e){
        for(Module m : mods){
            if(!m.isToggled())
                continue;

            m.onEvent(e);
        }
    }

    public static void onKey(int k) {
        for(Module m : mods) {
            if(m.getKey() == k) {
                m.toggle();
            }
        }
    }
}