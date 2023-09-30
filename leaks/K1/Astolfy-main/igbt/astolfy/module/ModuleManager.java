package igbt.astolfy.module;

import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import igbt.astolfy.Astolfy;
import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventKey;
import igbt.astolfy.events.listeners.EventRender2D;
import igbt.astolfy.module.ModuleBase.Category;
import igbt.astolfy.module.combat.AntiBot;
import igbt.astolfy.module.combat.Killaura;
import igbt.astolfy.module.combat.Velocity;
import igbt.astolfy.module.exploit.Disabler;
import igbt.astolfy.module.movement.Flight;
import igbt.astolfy.module.movement.LongJump;
import igbt.astolfy.module.movement.Speed;
import igbt.astolfy.module.player.*;
import igbt.astolfy.module.visuals.*;

public class ModuleManager {

	private CopyOnWriteArrayList<ModuleBase> modules = new CopyOnWriteArrayList<>();
	
	public void addModule(ModuleBase mod) {
		System.out.println("Registed Module : " + mod.getName());
		this.modules.add(mod);
	}

	
	public void setupModules() {
		addModule(new AntiBot());
		addModule(new InvMove());
		addModule(new TabGUI());
		addModule(new SessionStats());
		addModule(new AutoHypixel());
		addModule(new Keystrokes());
		addModule(new Disabler()); // This line will needed to be deleted <3
		addModule(new ESP());
		addModule(new Hud());
		addModule(new Sprint());
		addModule(new Flight());
		addModule(new LongJump());
		addModule(new Velocity());
		addModule(new ChestStealer());
		addModule(new ViewBobbing());
		addModule(new Scaffold());
		addModule(new FastMine());
		addModule(new NoFall());
		addModule(new Killaura());
		addModule(new InvManager());
		addModule(new Speed());
		addModule(new Animations());
		addModule(new MiniMap());
		addModule(new ClickGUI());
		sortModules();
	}
	
	public void sortModules() {
		  Collections.sort(modules, new Comparator<ModuleBase>() {
		      @Override
		      public int compare(final ModuleBase object1, final ModuleBase object2) {
		          return object1.getName().compareTo(object2.getName());
		      }
		  });
	}
	
	public CopyOnWriteArrayList<ModuleBase> getModules() {
		return modules;
	}
	public CopyOnWriteArrayList<ModuleBase> getModulesByCategory(Category c) {
		CopyOnWriteArrayList<ModuleBase> mods = new CopyOnWriteArrayList<>();
		for(ModuleBase mod : modules) {
			if(mod.getCategory().equals(c))
				mods.add(mod);
		}
		return mods;
	}
	
	public ModuleBase getModuleByName(String name) {
		for (ModuleBase module : modules)
			if(module.getName().equalsIgnoreCase(name))
				return module;
		return null;
	}
	
    public void onEvent(Event event) {
		if(event instanceof EventKey) {
			EventKey e = (EventKey)event;
	        for (ModuleBase module : modules) {
				if(module.getKey() == e.getCode())
					module.toggle();
	        }
		}
		if(event instanceof EventRender2D) {
			Astolfy.notificationManager.update();
		}
		for(ModuleBase mod : modules) {
			if(mod.isToggled())
				mod.onEvent(event);
			mod.onSkipEvent(event);
		}
    }
}
