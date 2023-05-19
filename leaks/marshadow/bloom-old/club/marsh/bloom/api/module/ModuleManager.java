package club.marsh.bloom.api.module;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import club.marsh.bloom.impl.utils.other.UrMomGayUtil;
import club.marsh.bloom.impl.mods.combat.*;
import club.marsh.bloom.impl.mods.exploit.*;
import club.marsh.bloom.impl.mods.movement.*;
import club.marsh.bloom.impl.mods.player.*;
import club.marsh.bloom.impl.mods.render.*;
import club.marsh.bloom.Bloom;
public class ModuleManager {
	private CopyOnWriteArrayList<Module> array = new CopyOnWriteArrayList<Module>();
	public ModuleManager() {
		this.addModules();
	}
	public void addModules() {
		Hud hud = new Hud();
		if (Bloom.INSTANCE.firstLoad)
			hud.setToggled(true);
		try {
			if (!UrMomGayUtil.ILLIILLLASIOJCNLINULLBBBCCCC()) {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		addModules(new Flight(), new BetterFPS(), new AutoPlace(), new Kick(), new TickBase(), new Blink(), new PVPBot(), new Phase(), new InventoryMove(), new Sprint(), new KeepSprint(), new MiddleClickFriends(), new FullBright(), new AutoHead(), new FastPlace(), new SafeWalk(), new NoClickDelay(), new AutoClicker(), new Timer(), new AntiResourceExploit(), new BalanceExploit(), new HudDesigner(), new TargetStrafe(), new ESP(), new AimAssist(), new Reach(), new Animations(), new NoSlow(),new LunarSpoofer(), new NoFall(), new NoBob(), new NoHurtCam(), new BuzzyBeeExploit(),new ClickGUI(),new AntiBot(),new KillAura(),new Speed(),hud,new Scaffold(),new Velocity(),new ChestStealer(),new BlockDeleter(),new GhostBlockCreator(),new InventoryManager(),new Teleport(),new Spider(),new FastLadder(), new Criticals(), new MurdererFinder(), new WTap(), new Strafe());
		//do move. has to be first.
		addModules(new Disabler());
	}
	public void addModules(Module... mods) {
		for (Module m : mods) {
			this.array.add(m);
			Bloom.INSTANCE.valueManager.register(m.getName(),m);
			m.addModesToModule();
		}
	}
	public CopyOnWriteArrayList<Module> getModules() {
		return array;
	}
	public List<Module> getModulesByCategory(Category category) {
		List<Module> mods = new ArrayList<>();
		for (Module mod : getModules()) {
			if (mod.category == category) {
				mods.add(mod);
			}
		}
		return mods;
	}
	public Module getByName(String name) {
		return array.stream().filter(mod -> mod.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
}
