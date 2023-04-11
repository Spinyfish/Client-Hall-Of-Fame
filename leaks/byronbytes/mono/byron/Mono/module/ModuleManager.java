package byron.Mono.module;

import byron.Mono.module.impl.combat.*;
import byron.Mono.module.impl.hud.Arraylist;
import byron.Mono.module.impl.hud.ClickGui;
import byron.Mono.module.impl.hud.HUDS;
import byron.Mono.module.impl.movement.*;
import byron.Mono.module.impl.player.*;
import byron.Mono.module.impl.visual.Fullbright;
import byron.Mono.module.impl.visual.Nametags;
import byron.Mono.module.impl.visual.NoHurtCam;
import byron.Mono.module.impl.visual.TrueSight;
import byron.Mono.module.impl.visual.Tracers;
import byron.Mono.module.impl.visual.Animations;
import byron.Mono.module.impl.visual.Chams;
import byron.Mono.module.impl.visual.ChestESP;
import byron.Mono.module.impl.visual.ESP;

import java.util.ArrayList;

public class ModuleManager {



    public ArrayList<Module> modules = new ArrayList<>();

    public ModuleManager() {
    init();
    }

    public void init()
    {
        // Combat
        modules.add(new Disabler());
        modules.add(new Velocity());
        modules.add(new AutoArmor());
        modules.add(new Killaura());
        modules.add(new AntiBot());
        modules.add(new Aimbot());
        modules.add(new TargetStrafe());
        modules.add(new WTap());
        modules.add(new Criticals());
        modules.add(new OtherDisabler());
        // Movement
        modules.add(new Sprint());
        modules.add(new Flight());
        modules.add(new Speed());
        modules.add(new NoFall());
        modules.add(new InvWalk());
        modules.add(new SafeWalk());
        modules.add(new Jesus());
        modules.add(new Phase());
        modules.add(new NoSlowdown());
        modules.add(new Scaffold());
        modules.add(new AirHop());
        modules.add(new AntiVoid());
        modules.add(new Blink());

        // Player
        modules.add(new FastPlace());
        modules.add(new FastEat());
        modules.add(new Damage());
        modules.add(new ChestStealer());
        modules.add(new NoRotate());
        // Visual
        modules.add(new Fullbright());
        modules.add(new Tracers());
        modules.add(new Chams());
        modules.add(new TrueSight());
        modules.add(new NoHurtCam());
        modules.add(new Animations());
        modules.add(new Nametags());
        modules.add(new ESP());
        modules.add(new ChestESP());
        // HUD
        modules.add(new ClickGui());
        modules.add(new Arraylist());
        modules.add(new HUDS());
    }

    public ArrayList<Module> getModules()
    {
        return modules;
    }

    public ArrayList<Module> getModules(Category category) {
        ArrayList<Module> modules = new ArrayList<>();
        for(Module m : this.modules) {
            if (m.getCategory().equals(category))
                modules.add(m);
        }
        return modules;
    }

    public Module getModule(String name)
    {
        return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }




}
