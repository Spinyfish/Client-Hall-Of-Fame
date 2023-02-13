package juul.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import juul.module.Module.Category;
import juul.module.combat.*;
import juul.module.movement.*;
import juul.module.player.*;
import juul.module.render.*;
import juul.module.world.*;

public class Manager {

	public List<Module> modules = new ArrayList<Module>();
	
	public boolean setup() {

		// movement
		modules.add(new Bhop());
		modules.add(new Sprint());
		modules.add(new Boost());
		modules.add(new EdgeJump());

		// combat
		modules.add(new AutoClicker());
		modules.add(new Velocity());
		modules.add(new Aimbot());
		modules.add(new Reach());

		// render
		modules.add(new SelfDestruct());
		modules.add(new FullBright());
		modules.add(new HUD());
		modules.add(new GUI());

		// player
		modules.add(new Refill());
		modules.add(new FastPlace());
		modules.add(new PingSpoof());

		// world
		modules.add(new Eagle());
		modules.add(new VClip());
		modules.add(new HClip());
		
		modules.sort(Comparator.comparingDouble(m -> ((Module)m).getNameLength()).reversed());
		return true;
	}

	public Map<Class, Module> moduleMap = new HashMap<>();
	
	public List<Module> getModules(Category category){
		List<Module> categoryModules = new ArrayList<Module>();
		for(Module module : modules)
			if(module.category == category)
				categoryModules.add(module);
		
		return categoryModules;
	}
	
	public <T> T getModule(Class<T> clazz){
		if(moduleMap.containsKey(clazz)) {
			return (T) moduleMap.get(clazz);
		}
		
		for(Module m : modules){
			if(m.getClass() == clazz){
				moduleMap.put(clazz, m);
				return (T)m;
			}
		}
		return null;
	}
}
