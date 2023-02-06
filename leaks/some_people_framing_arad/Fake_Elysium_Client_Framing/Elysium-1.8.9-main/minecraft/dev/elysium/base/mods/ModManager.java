package dev.elysium.base.mods;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dev.elysium.base.events.EventBus;
import dev.elysium.base.events.types.EventTarget;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventKeyTyped;
import org.lwjgl.input.Keyboard;


public abstract class ModManager {

	protected List<Mod> mods = new ArrayList<Mod>();
	
	public abstract void register();

	public void clear() {
		mods.clear();
	}

	public ModManager() {
		EventBus.register(this);
		register();
	}
	
	public Mod getModByName(String name) {
		
		for(Mod mod : mods)
			if(mod.name.equalsIgnoreCase(name))
				return mod;
		
		return null;
	}

	public List<Mod> getModsByCategory(Category cat) {
		return mods.stream().filter(mod -> mod.category == cat).collect(Collectors.toList());
	}

	public Mod getModByClass(Class<?> cl) {
		
		for(Mod mod : mods)
			if(mod.getClass() == cl)
				return mod;
		
		return null;
	}
	
	public List<Mod> getMods(){
		return mods;
	}

	public void HandleKeypress(EventKeyTyped e) {
		for(Mod m : Elysium.getInstance().getModManager().getMods()) {
			if(m.keyCode.getKeyCode() == e.getKeyCode()) {
				if(m.keyCode.combo == 0) {
					m.toggle();
				} else {
					if(Keyboard.isKeyDown(m.keyCode.combo)) {
						m.toggle();
					}
				}
			}
		}
	}
}
