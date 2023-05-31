// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module;

import java.util.Iterator;
import today.getbypass.module.player.ChestAura;
import today.getbypass.module.misc.Eagle;
import today.getbypass.module.movement.NoFall;
import today.getbypass.module.player.AntiAFK;
import today.getbypass.module.player.AntiCobweb;
import today.getbypass.module.player.AutoRespawn;
import today.getbypass.module.player.FastLadder;
import today.getbypass.module.combat.AutoClicker;
import today.getbypass.module.render.ImageESP;
import today.getbypass.module.player.FastPlace;
import today.getbypass.module.combat.NoHitDelay;
import today.getbypass.module.player.Timer;
import today.getbypass.module.movement.Spider;
import today.getbypass.module.movement.Glide;
import today.getbypass.module.combat.Aura;
import today.getbypass.module.movement.Step;
import today.getbypass.module.movement.Speed;
import today.getbypass.module.movement.Fly;
import today.getbypass.module.player.ChestStealer;
import today.getbypass.module.movement.Sprint;
import java.util.ArrayList;

public class ModuleManager
{
    public static ArrayList<Module> mods;
    
    public ModuleManager() {
        ModuleManager.mods = new ArrayList<Module>();
        newMod(new Sprint());
        newMod(new ChestStealer());
        newMod(new Fly());
        newMod(new Speed());
        newMod(new Step());
        newMod(new Aura());
        newMod(new Glide());
        newMod(new Spider());
        newMod(new Timer());
        newMod(new NoHitDelay());
        newMod(new FastPlace());
        newMod(new ImageESP());
        newMod(new AutoClicker());
        newMod(new FastLadder());
        newMod(new AutoRespawn());
        newMod(new AntiCobweb());
        newMod(new AntiAFK());
        newMod(new NoFall());
        newMod(new Eagle());
        newMod(new ChestAura());
    }
    
    public static void newMod(final Module m) {
        ModuleManager.mods.add(m);
    }
    
    public static ArrayList<Module> getModules() {
        return ModuleManager.mods;
    }
    
    public static void onUpdate() {
        for (final Module m : ModuleManager.mods) {
            m.onUpdate();
        }
    }
    
    public static void onRender() {
        for (final Module m : ModuleManager.mods) {
            m.onRender();
        }
    }
    
    public static void onKey(final int k) {
        for (final Module m : ModuleManager.mods) {
            if (m.getKey() == k) {
                m.toggle();
            }
        }
    }
}
