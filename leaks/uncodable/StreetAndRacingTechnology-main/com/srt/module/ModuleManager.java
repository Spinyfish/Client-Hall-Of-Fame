package com.srt.module;

import java.util.Collections;

import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.srt.events.Event;
import com.srt.events.listeners.EventKey;
import com.srt.module.combat.*;
import com.srt.module.exploit.*;
import com.srt.module.movement.*;
import com.srt.module.player.*;
import com.srt.module.secrets.*;
import com.srt.module.visuals.*;

public class ModuleManager {

	private CopyOnWriteArrayList<ModuleBase> modules = new CopyOnWriteArrayList<>();
	
	public void addModule(ModuleBase mod) {
		System.out.println("Registed Module : " + mod.getName());
		this.modules.add(mod);
	}

	
	public void setupModules() {
		addModule(new AutoRespawn());
		addModule(new AntiBot());
		addModule(new Disabler());
		addModule(new Hud());
		addModule(new Sprint());
		addModule(new Flight());
		addModule(new ChestStealer());
		addModule(new Killaura());
		addModule(new Speed());
		addModule(new Velocity());
		addModule(new NoFall());
		addModule(new LongJump());
		addModule(new Phase());
		addModule(new AntiVoid());
		addModule(new Commands());
		addModule(new InvMove());
		addModule(new Timer());
		addModule(new Jetpack());
		addModule(new InvManager());
		addModule(new Scaffold());
		addModule(new Blink());
		addModule(new Criticals());
		addModule(new NoSlow());
		addModule(new Animations());
		addModule(new TargetHUD());
		addModule(new EATrax());
		addModule(new Radar());
		addModule(new Ambience());
		addModule(new ESP());


		//MODULES BEFORE CGUI
		addModule(new ClickGUI());
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
		for(ModuleBase mod : modules) {
			if(mod.isToggled())
				mod.onEvent(event);
			else
				mod.onSkipEvent(event);
		}
    }
}
