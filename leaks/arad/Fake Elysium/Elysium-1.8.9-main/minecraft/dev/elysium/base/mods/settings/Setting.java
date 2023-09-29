package dev.elysium.base.mods.settings;

import java.util.function.Predicate;
import dev.elysium.base.mods.Mod;

public class Setting {
	public String name;
	public Mod parent;
	public Predicate<Mod> visible;
	public int tTimer;

	public Setting(String name, Mod parent) {
		this.name = name;
		this.parent = parent;
		parent.settings.add(this);
	}
	
	public boolean isVisible() {
		if(visible != null) {
			return visible.test(parent);
		}
		
		return true;
	}
	
	public Setting setPredicate(Predicate<Mod> visible) {
		this.visible = visible;
        return null;
    }
	
}
