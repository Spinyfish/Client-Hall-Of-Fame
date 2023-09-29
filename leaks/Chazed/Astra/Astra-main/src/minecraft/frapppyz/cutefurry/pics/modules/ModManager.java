package frapppyz.cutefurry.pics.modules;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.modules.impl.combat.*;
import frapppyz.cutefurry.pics.modules.impl.exploit.*;
import frapppyz.cutefurry.pics.modules.impl.move.*;
import frapppyz.cutefurry.pics.modules.impl.player.*;
import frapppyz.cutefurry.pics.modules.impl.render.*;
import frapppyz.cutefurry.pics.modules.impl.world.*;
import java.util.ArrayList;

public class ModManager {
    public ArrayList<Mod> mods = new ArrayList<>();

    public void addMods(){

        //COMBAT
        mods.add(new TargetStrafe());
        mods.add(new Backtrack());
        mods.add(new Killaura());
        mods.add(new Velocity());
        mods.add(new TPAura());
        mods.add(new Antibot());
        mods.add(new Crits());
        mods.add(new WTap());
        mods.add(new HvH());

        //MOVE
        mods.add(new Fly());
        mods.add(new Speed());
        mods.add(new Sprint());
        mods.add(new Longjump());

        //PLAYER
        mods.add(new NoSlow());
        mods.add(new AntiAim());
        mods.add(new ChestStealer());
        mods.add(new InvManager());
        mods.add(new Replica());
        mods.add(new NoFall());
        mods.add(new AntiVoid());

        //WORLD
        mods.add(new Scaffold());
        mods.add(new Phase());
        mods.add(new LagbackDetector());

        //RENDER
        mods.add(new ESP());
        mods.add(new HUD());
        mods.add(new Animations());
        mods.add(new ClickGUI());
        mods.add(new Debugger());
        mods.add(new Cape());
        mods.add(new FullBright());

        //EXPLOIT
        mods.add(new Disabler());


        Wrapper.getLogger().info("Loaded modules.");
        Wrapper.getLogger().info("List: " + mods.toString());
    }


    public Mod getModByName(String name){
        for(Mod m : mods){
            if(m.getName().equals(name))
                return m;
        }
        return null;
    }

    public void onKey(int key){
        for(Mod m : mods){
            if(m.getKey() == key){
                m.toggle();
            }
        }
    }

    public void onEvent(Event e){
        for(Mod m : mods){
            if(m.isToggled()) m.onEvent(e);
        }
    }

    public void onRender(){
        mods.forEach(Mod::onRender);
    }
}
