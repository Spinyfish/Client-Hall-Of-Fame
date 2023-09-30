package axolotl.cheats.modules;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import axolotl.Axolotl;
import axolotl.cheats.config.Config;
import axolotl.cheats.events.Event;
import axolotl.cheats.commands.impl.*;
import axolotl.cheats.events.EventPacket;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.modules.impl.chat.*;
import axolotl.cheats.modules.impl.combat.*;
import axolotl.cheats.modules.impl.movement.*;
import axolotl.cheats.modules.impl.player.*;
import axolotl.cheats.modules.impl.render.*;
import axolotl.cheats.modules.impl.world.*;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class ModuleManager {

	public String obfuscationVar1 = "CL_000182";

	// Make it harder to find this class!
	public int obfuscationVar2;
	public int obfuscationVar3;
	public int obfuscationVar4;
	public int obfuscationVar5;
	public int obfuscationVar6;
	public int obfuscationVar7;
	public int obfuscationVar8;
	public int obfuscationVar9;

	public CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();
	public void registerCheats() throws InterruptedException {

		// MODULES

		// MOVEMENT
		Flight a = new Flight();
		modules.add(a);
		CopyOnWriteArrayList<Flight> c = new CopyOnWriteArrayList<Flight>();
		c.add(a);
		modules.add(new Speed());
		boolean obfuscationVar0 = Minecraft.getMinecraft().field_175613_B;
		modules.add(new Sprint());
		int obfuscationVard;
		modules.add(new Phase());
		int obfuscationVarc;
		Object q = new Phase();
		modules.add(new Step());
		try {
			q.wait(6);
		} catch (IllegalMonitorStateException e) {
			;
		}
		int obfuscationVarb;
		modules.add(new Longjump());

		int obfuscationVara;


		boolean obfuscationVar1 = Minecraft.getMinecraft().field_175619_R;
		if(Axolotl.INSTANCE != null && obfuscationVar1)
			Axolotl.INSTANCE.sendMessage("logger.start");
		
		// PLAYER
		modules.add(new NoFall());
		modules.add(new NoSlow());
		modules.add(new AutoArmor());
		modules.add(new InvManager());
		modules.add(new Timer());
		modules.add(new Jesus());
		modules.add(new InvMove());
		
		// COMBAT
		modules.add(new Aura());
		modules.add(new Velocity());
		modules.add(new Aimbot());
		modules.add(new AimAssist());
		modules.add(new Reach());
		
		// RENDER
		modules.add(new Fullbright());
		modules.add(new axolotl.cheats.modules.impl.render.HUD());
		modules.add(new ESP());
		modules.add(new ClickGui());
		modules.add(new TabGUI());
		modules.add(new Animations());

		int obfuscationVar2;
		int obfuscationVar3;

		// WORLD
		modules.add(new ChestStealer());

		int obfuscationVar4;
		int obfuscationVar5;
		modules.add(new Disabler());

		int obfuscationVar7;
		modules.add(new Crasher());
		modules.add(new AntiVoid());

		int obfuscationVar8;
		int obfuscationVar9;
		modules.add(new Scaffold());
		modules.add(new BedNuker());
		int obfuscationVar10;
		modules.add(new AntiCrash());

		try {
			Minecraft.getMinecraft().theWorld.getEntityByID(521);
		} catch (NullPointerException e) {
			;
		}

		// CHAT
		modules.add(new Spammer());
		modules.add(new AutoGroomer());
		modules.add(new Killsults());

		// COMMANDS

		Axolotl.INSTANCE.cmdManager.loadCommand(new ConfigCmd());
		Axolotl.INSTANCE.cmdManager.loadCommand(new HelpCmd());
		Axolotl.INSTANCE.cmdManager.loadCommand(new BindCmd());

	}

	boolean debug, debug2;
	
	public void keyPress(int key) {
		Axolotl.INSTANCE.tabGUI.keyPress(key);
		if(key == Keyboard.KEY_F9){
			debug = true;
		} else if (key == Keyboard.KEY_F10 && debug) {
			debug2 = !debug2;
		} else {
			debug = false;
		}
		for(Module m : modules) {
			if(System.currentTimeMillis() - m.keybindSetting.timeKeychanged < 50)return;
			if(m.keybindSetting.getCode() == key) {
				m.toggle();
			} else {
				m.onKeyPress(key);
			}
		}
	}

	public int timer;

	public void onEvent(Event event) {

		if(event instanceof EventUpdate) {
			timer++;
			if(timer >= 60) {
				timer = 0;
				Config c = Axolotl.INSTANCE.configManager.createConfig(Axolotl.INSTANCE.moduleManager, "currentConfig");
				c.location = System.getProperty("user.dir") + "\\Axosense\\" + c.name + ".json";
				try {
					Axolotl.INSTANCE.configManager.saveConfig(c);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		if(debug2) {
			if(event instanceof EventPacket) {
				Axolotl.INSTANCE.sendMessage(((EventPacket) event).getPacket());
			}
		}

		for(Module m : modules) {
			if(m.toggled) {
				m.onEvent(event);
			}
		}
	}

	public Module getModule(String string) {
		for(Module m : modules) {
			if(m.name.equalsIgnoreCase(string)) {
				return m;
			}
		}
		return null;
	}

	public void sortModules() {
		  Collections.sort(modules, Comparator.comparing(object -> object.name + Axolotl.INSTANCE.hud.getMainModeString(object)));
	}
	
}
