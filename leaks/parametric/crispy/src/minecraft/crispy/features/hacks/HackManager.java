package crispy.features.hacks;


import crispy.Crispy;
import crispy.features.hacks.impl.combat.*;
import crispy.features.hacks.impl.misc.*;
import crispy.features.hacks.impl.movement.*;
import crispy.features.hacks.impl.movement.Speed;
import crispy.features.hacks.impl.player.*;
import crispy.features.hacks.impl.render.*;
import crispy.features.hacks.impl.render.BlockHit;
import crispy.features.script.ScriptModule;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HackManager {

    private final ArrayList<Hack> hacks = new ArrayList<>();

    public HackManager() {
        addHack(new Sprint());
        addHack(new Scaffold());
        addHack(new HUD());
        addHack(new Speed());
        addHack(new NoSlowDown());
        addHack(new Criticals());
        addHack(new AntiVoid());
        addHack(new Fly());
        addHack(new Nofall());
        addHack(new Breaker());
        addHack(new ClickGui());
        addHack(new Disabler());
        addHack(new Commands());
        addHack(new Timer());
        addHack(new AntiVanish());
        addHack(new FastEat());
        addHack(new Teleport());
        addHack(new DamageParticles());
        addHack(new Nametag());
        addHack(new OldHitting());
        addHack(new KillInsults());
        addHack(new InvManager());
        addHack(new LagSwitch());
        addHack(new LongJump());
        addHack(new AntiBot());
        addHack(new AutoPot());
        addHack(new Phase());
        addHack(new TimeChanger());
        addHack(new PvpBot());
        addHack(new BlockHit());
        addHack(new Velocity());
        addHack(new ChestStealer());
        addHack(new InventoryMove());
        addHack(new ChestESP());
        addHack(new AimAssist());
        addHack(new AutoClicker());
        addHack(new OutlineESP());
        addHack(new HackerDetector());
        addHack(new WTap());
        addHack(new Jesus());
        addHack(new TargetStrafe());
        addHack(new Crasher());
        addHack(new TabGui());
        addHack(new Plugin());
        addHack(new Blink());
        addHack(new TargetHud());
        addHack(new ESP());
        addHack(new Aura());
        addHack(new AntiCrash());
        addHack(new SelfDestruct());
        addHack(new FastPlace());
        addHack(new NoRotate());
        addHack(new InfiniteAura());
        addHack(new BackTrace());
        addHack(new SsmBotter());
        addHack(new ItemPhysics());
        addHack(new PirateSpeak());
        addHack(new AntiFreeze());
        if(!getHack(HUD.class).isEnabled()) getHack(HUD.class).setState(true);
        if(!getHack(Commands.class).isEnabled()) getHack(Commands.class).setState(true);

    }
    private void addHack(Hack module) {
        hacks.add(module);
        Crispy.INSTANCE.getValueManager().registerObject(module.getName(), module);
        System.out.println("Registered hack from " + module.getClass());
    }

    public <T extends Hack> T getHack(Class<T> clazz) {

        return (T) hacks.stream().filter(hack -> hack.getClass() == clazz).findFirst().orElse(null);
    }
    public List<Hack> getModules(Category category) {

        List<Hack> l = new ArrayList<>();
        for (Hack m : this.hacks) {
            if (m.getCategory() == category)
                l.add(m);
        }
        return l;
    }
    public Hack getModule(String name, boolean caseSensitive) {
        return hacks.stream().filter(mod -> !caseSensitive && name.equalsIgnoreCase(mod.getName()) || name.equals(mod.getName())).findFirst().orElse(null);
    }

    public void addScriptModule(ScriptModule module) {
        addHack(module);
    }

    public void addConfigModule(ConfigModule module) {
        addHack(module);
    }
}
