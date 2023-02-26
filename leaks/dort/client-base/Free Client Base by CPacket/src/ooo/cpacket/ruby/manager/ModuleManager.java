package ooo.cpacket.ruby.manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import ooo.cpacket.lemongui.module.GuiModule;
import ooo.cpacket.ruby.module.Module;
import ooo.cpacket.ruby.module.Module.Category;
import ooo.cpacket.ruby.module.attack.AntiBot;
import ooo.cpacket.ruby.module.attack.Criticals;
import ooo.cpacket.ruby.module.attack.KillAura;
import ooo.cpacket.ruby.module.attack.Velocity;
import ooo.cpacket.ruby.module.misc.NoFall;
import ooo.cpacket.ruby.module.misc.Spammer;
import ooo.cpacket.ruby.module.move.Fly;
import ooo.cpacket.ruby.module.move.Speed;
import ooo.cpacket.ruby.module.move.Step;
import ooo.cpacket.ruby.module.render.Hud;
import ooo.cpacket.ruby.module.render.Nametags;
import ooo.cpacket.ruby.module.render.NoBob;

public class ModuleManager {

	public CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();

	public void setup() {
		this.modules.add(new Nametags("Nametags", Keyboard.KEY_NONE, Category.VISUAL));
		this.modules.add(new KillAura("KillAura", Keyboard.KEY_R, Category.ATTACK));
		this.modules.add(new Velocity("Velocity", 0, Category.ATTACK));
		this.modules.add(new AntiBot("AntiBot", Keyboard.KEY_NONE, Category.WORLD));
		this.modules.add(new Speed("Speed", Keyboard.KEY_F, Category.MOVE));
		this.modules.add(new Fly("Fly", Keyboard.KEY_G, Category.MOVE));
		this.modules.add(new Step("Step", 0, Category.MOVE));
		this.modules.add(new NoFall("NoFall", Keyboard.KEY_NONE, Category.MISC));
		this.modules.add(new Spammer("Spammer", Keyboard.KEY_NONE, Category.MISC));
		this.modules.add(new Criticals());
		this.modules.add(new GuiModule());
		this.modules.add(new Hud("Hud", Keyboard.KEY_NONE, Category.VISUAL));
		this.modules.add(new NoBob());
		this.sort();
	}

	public void sort() {

		modules.sort(new Comparator<Module>() {
			@Override
			public int compare(Module arg0, Module arg1) {
				int l1 = (int) Minecraft.getMinecraft().fontRendererObj.getStringWidth(arg0.getSuffixedName());
				int l2 = (int) Minecraft.getMinecraft().fontRendererObj.getStringWidth(arg1.getSuffixedName());
				return l2 - l1;
			}

		});
	}

	public ArrayList<Module> getModulesInCategory(Category categoryIn) {
		ArrayList<Module> mods = new ArrayList<Module>();
		for (Module m : this.modules) {
			if (m.getCategory() == categoryIn)
				mods.add(m);
		}
		return mods;
	}
	
	public Module getModule(Class clazz) {
		for (Module m : this.modules) {
			if (m.getClass() == clazz)
				return m;
		}
		return null;
	}

	public Module getModule(String moduleName) {
		for (Module module : this.modules) {
			if (module.getName().equals(moduleName)) {
				return module;
			}
		}
		return null;
	}
	
}
