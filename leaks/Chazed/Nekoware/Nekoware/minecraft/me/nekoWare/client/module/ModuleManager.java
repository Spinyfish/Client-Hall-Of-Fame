
package me.nekoWare.client.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.nekoWare.client.module.Module.Category;
import me.nekoWare.client.module.combat.*;
import me.nekoWare.client.module.exploit.Disabler;
import me.nekoWare.client.module.misc.*;
import me.nekoWare.client.module.movement.*;
import me.nekoWare.client.module.player.*;
import me.nekoWare.client.module.render.*;
import me.nekoWare.client.util.misc.Timer;

public class ModuleManager {
	private ArrayList<Module> modules = new ArrayList();

	public ModuleManager() {
		modules.add(new Aura());
		modules.add(new Stealer());
		modules.add(new NoHurtCam());
		modules.add(new AutoArmor());
		modules.add(new Criticals());
		modules.add(new Velocity("Velocity", 0, Module.Category.COMBAT));
		modules.add(new TargetStrafe("Target Strafe", 0, Module.Category.COMBAT));
		modules.add(new FastUse("Fast Use", 0, Module.Category.PLAYER));
		modules.add(new Fly());
		modules.add(new Speed());
		modules.add(new ESP());
		modules.add(new Step());
		modules.add(new Sprint());
		modules.add(new ClickGui());
		modules.add(new Disabler("Disabler", 0, Module.Category.EXPLOIT));
		modules.add(new Scaffold());
		modules.add(new me.nekoWare.client.module.misc.Timer());
		modules.add(new NoFall());
		modules.add(new Fullbright());
		modules.add(new Animations());
		modules.add(new Commands());
		modules.add(new FastPlace());
		modules.add(new Reach());
		this.getModule("Commands").toggle();

		try {
			KeyLoader.load(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Timer threadTimer = new Timer();
		new Thread(() -> {
			while (true) {
				if (threadTimer.delay(2500)) {
					KeyLoader.save(modules);
					threadTimer.reset();
				}
			}
		}).start();
	}

	public List<Module> getModules() {
		return modules;
	}

	public Module get(final Class moduleClass) {
		for (final Module m : modules) {
			if (m.getClass() != moduleClass) {
				continue;
			}
			return m;
		}
		return null;
	}

	public Module getModule(final String name) {
		for (final Module m : modules) {
			if (!m.getName().equalsIgnoreCase(name)) {
				continue;
			}
			return m;
		}
		return null;
	}

	public List<Module> getModules(Category category) {
		List<Module> l = new ArrayList();
		for (Module m : modules) {
			if (m.getCategory() == category) {
				l.add(m);
			}
		}
		return l;
	}
}
