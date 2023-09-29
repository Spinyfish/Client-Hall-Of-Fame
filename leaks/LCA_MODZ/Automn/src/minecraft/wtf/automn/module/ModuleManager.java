package wtf.automn.module;

import wtf.automn.Automn;
import wtf.automn.module.impl.combat.ModuleKillaura;
import wtf.automn.module.impl.combat.ModuleTriggerbot;
import wtf.automn.module.impl.combat.ModuleVelocity;
import wtf.automn.module.impl.misc.ModuleAutoAnswer;
import wtf.automn.module.impl.movement.ModuleFlight;
import wtf.automn.module.impl.movement.ModuleNoSlowDown;
import wtf.automn.module.impl.movement.ModuleSpeed;
import wtf.automn.module.impl.movement.ModuleSprint;
import wtf.automn.module.impl.player.Fixes;
import wtf.automn.module.impl.player.ModuleFastPlace;
import wtf.automn.module.impl.visual.*;
import wtf.automn.module.impl.world.ModuleEagle;
import wtf.automn.module.impl.world.ModuleNofall;

import java.util.List;

public class ModuleManager {

    public ModuleManager() {
    }

    //Combat
    public final ModuleTriggerbot triggerbot = new ModuleTriggerbot();
    public final ModuleKillaura killaura = new ModuleKillaura();
    public final ModuleVelocity velocity = new ModuleVelocity();

    //Movement
    public final ModuleSprint sprint = new ModuleSprint();
    public final ModuleSpeed speed = new ModuleSpeed();
    public final ModuleFlight flight = new ModuleFlight();
    public final ModuleNoSlowDown noSlowDown = new ModuleNoSlowDown();

    //Visual
    public final ModuleHUD hud = new ModuleHUD();
    public final ModuleBlur blur = new ModuleBlur();
    public final ModuleRadar radar = new ModuleRadar();
    public final ModuleBluredItem bluredItem = new ModuleBluredItem();
    public final ModuleFullbright fullbright = new ModuleFullbright();
    public final ModuleAmbieance ambieance = new ModuleAmbieance();
    public final ModuleShadow shadow = new ModuleShadow();
    public final ModuleBloom glow = new ModuleBloom();
    public final ModuleShaderESP shaderESP = new ModuleShaderESP();
    public final ModuleGlowedItem glowedItem = new ModuleGlowedItem();
    public final ModuleCustomChat customChat = new ModuleCustomChat();

    //Misc
    public final ModuleVideoPlayer videoPlayer = new ModuleVideoPlayer();
    public final ModuleAutoAnswer autoAnswer = new ModuleAutoAnswer();

    //Player
    public final ModuleFastPlace fastPlace = new ModuleFastPlace();
    public final Fixes fixes = new Fixes();

    //World
    public final ModuleNofall nofall = new ModuleNofall();
    public final ModuleEagle eagle = new ModuleEagle();



    public List<Module> getModules() {
        return Automn.instance().manager().getModules();
    }

    public void save() {
        this.getModules().forEach(mods -> {
            mods.saveToFile(mods.id());
        });
    }

    public void addModule(final Module mod) {
        Automn.instance().manager().getModules().add(mod);
    }

    public void removeModule(final Module mod) {
        Automn.instance().manager().getModules().remove(mod);
    }

    public Module getModule(final String s) {
        return Automn.instance().manager().getModules().stream().filter(module -> module.id().equals(s)).findFirst().orElse(null);
    }

    public Module getModule(final Class<? extends Module> mod) {
        return Automn.instance().manager().getModules().stream().filter(module -> module.getClass() == mod).findFirst().orElse(null);
    }
    


}
