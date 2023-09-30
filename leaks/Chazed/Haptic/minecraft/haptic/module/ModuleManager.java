package haptic.module;

import java.util.ArrayList;

import haptic.module.impl.combat.*;
import haptic.module.impl.exploit.*;
import haptic.module.impl.ghost.*;
import haptic.module.impl.misc.*;
import haptic.module.impl.movement.*;
import haptic.module.impl.player.*;
import haptic.module.impl.render.*;
import haptic.module.impl.world.*;

public class ModuleManager {
	
	private final ArrayList<Module> modules = new ArrayList<>();
	
	public ModuleManager() {
		//Combat
		modules.add(new Killaura());
		modules.add(new AutoArmor());
		modules.add(new Velocity());
		modules.add(new Criticals());
		modules.add(new Antibot());
		
		//Movement
		modules.add(new Sprint());
		modules.add(new Speed());
		modules.add(new Fly());
		modules.add(new Longjump());
		modules.add(new InvMove());
		modules.add(new Noslow());
		
		//Player
		modules.add(new Nofall());
		modules.add(new Antivoid());
		modules.add(new FastEat());
		modules.add(new InvManager());
		
		//World
		modules.add(new Cheststealer());
		modules.add(new Scaffold());
		modules.add(new Breaker());
		
		//Render
		modules.add(new ClickGuiModule());
		modules.add(new Fullbright());
		modules.add(new HUD());
		modules.add(new ESP());
		modules.add(new Chams());
		
		//Exploit
		modules.add(new Disabler());
		modules.add(new Phase());
		modules.add(new Regen());
		
		//Ghost
		modules.add(new AutoClicker());
		modules.add(new Reach());
		modules.add(new AimAssist());
		modules.add(new AutoBridge());
		
		//Misc
		modules.add(new Anticheat());
		modules.add(new NoRotate());
	}

	public ArrayList<Module> getModules() {
		return modules;
	}
	
	public Module getModuleByName(String name) {
		for(Module m : modules) {
			if(m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}
	
}