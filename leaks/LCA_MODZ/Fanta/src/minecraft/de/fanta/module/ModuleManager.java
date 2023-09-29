package de.fanta.module;

import java.util.concurrent.CopyOnWriteArrayList;

import de.fanta.Client;
import de.fanta.events.listeners.EventRegisterModule;
import de.fanta.module.impl.combat.*;
import de.fanta.module.impl.miscellaneous.*;
import de.fanta.module.impl.movement.*;
import de.fanta.module.impl.player.*;
import de.fanta.module.impl.visual.*;
import de.fanta.module.impl.world.*;
import de.hero.example.GUI;


public class ModuleManager {
	public CopyOnWriteArrayList<Module> modules;

	public ModuleManager() {
		this.modules = new CopyOnWriteArrayList<>();
		// Combat
		this.addModule(new Killaura());
		this.addModule(new Velocity());
		this.addModule(new ChestAura());
		this.addModule(new Criticals());
		this.addModule(new TpAura());
		this.addModule(new TestAura());
		// Misc
		this.addModule(new MemoryCleaner());
		this.addModule(new NoRotateSet());
		this.addModule(new MiddleClickFriends());
		this.addModule(new FullAntiVerus());
		this.addModule(new Sound());
		// Movement
		this.addModule(new Sprint());
		this.addModule(new InvMove());
		this.addModule(new Jesus());
		this.addModule(new NoWeb());
		this.addModule(new Speed());
		this.addModule(new Flight());
		this.addModule(new Step());
		this.addModule(new NoSlowDown());
		this.addModule(new AntiVoid());
		this.addModule(new Longjump());
		this.addModule(new TargetStrafe());
		this.addModule(new Teleport());
		// Player
		this.addModule(new InvCleaner());
		this.addModule(new AutoArmor());
		this.addModule(new Spammer());
		this.addModule(new Nofall());
		this.addModule(new Spider());
		this.addModule(new FastUse());
		this.addModule(new Regen());
		this.addModule(new InvManager());
		// World
		this.addModule(new ChestStealer());
		this.addModule(new Scaffold());
		this.addModule(new Disabler());
		this.addModule(new Fucker());
		this.addModule(new TP());
		this.addModule(new FastBreak());
		this.addModule(new Vclip());
		this.addModule(new Phase());
		// Visual
		this.addModule(new ArrayList());
		this.addModule(new Themes());
		this.addModule(new Crosshair());
		this.addModule(new ESP());
		this.addModule(new NameTags());
		this.addModule(new NoFov());
		this.addModule(new ChestESP());
		this.addModule(new MotionGraph());
		this.addModule(new BlockHit());
		this.addModule(new FakeBlock());
		this.addModule(new NoBob());
		this.addModule(new Notification());
		this.addModule(new BlockESP());
		this.addModule(new AntiChat());
		this.addModule(new GUI());
		this.addModule(new Tabgui());
		this.addModule(new Tracers2D());
		this.addModule(new CustomHotbar());
		this.addModule(new GuiBlur());
		this.addModule(new ChinaHat());
		this.addModule(new Radar());
		this.addModule(new TrailESP());
		this.addModule(new HitSlow());
		this.addModule(new InventoryRenderer());

	}
	
	public void addModule(Module module) {
		this.modules.add(module);
		try {
			module.onEvent(new EventRegisterModule());			
		} catch (Exception e) {}
	}
	
	public Module getModule(String name) {
		for (Module mod : modules) {
			if (mod.name.equalsIgnoreCase(name)) {
				return mod;
			}
		}

		return null;
	}
	
    public final <T extends Module> T getModule(final Class<T> tClass) {
        return (T) modules.stream().filter(module -> module.getClass().equals(tClass)).findFirst().orElse(null);
    }

    public final boolean isToggled(final Class tClass) {
        return getModule(tClass).state;
    }
}
