package dev.elysium.client.mods;

import dev.elysium.base.mods.ModManager;
import dev.elysium.client.mods.impl.combat.*;
import dev.elysium.client.mods.impl.movement.*;
import dev.elysium.client.mods.impl.player.*;
import dev.elysium.client.mods.impl.render.*;
import dev.elysium.client.mods.impl.settings.*;

public class ModManagerImpl extends ModManager{

	@Override
	public void register() {
		//Combat
		mods.add(new WTap());
		mods.add(new Reach());
		mods.add(new Hitbox());
		mods.add(new Velocity());
		mods.add(new AutoPot());
		mods.add(new Killaura());
		mods.add(new AimAssist());
		mods.add(new AutoClicker());
		mods.add(new NoClickDelay());

		//Movement
		mods.add(new Fly());
		mods.add(new Speed());
		mods.add(new Sprint());
		mods.add(new Sneak());
		mods.add(new NoSlowDown());
		mods.add(new TargetStrafe());
		mods.add(new InventoryMove());
		mods.add(new BowLongJump());

		//Player
		mods.add(new Blink());
		mods.add(new MLG());
		mods.add(new Eagle());
		mods.add(new Refill());
		mods.add(new NoFall());
		mods.add(new Lag());
		mods.add(new Disabler());
		mods.add(new Scaffold());
		mods.add(new AntiFall());
		mods.add(new SumoWalls());
		mods.add(new PingSpoof());
		mods.add(new ChatBypass());
		mods.add(new KineticSneak());
		mods.add(new ChestStealer());
		mods.add(new AntiBlockDesync());
		mods.add(new InventoryManager());

		//Render
		mods.add(new ESP());
		mods.add(new TabGui());
		mods.add(new Widgets());
		mods.add(new NameTags());
		mods.add(new BanStats());
		mods.add(new Freelook());
		mods.add(new Animations());
		mods.add(new FullBright());
		mods.add(new StaffDetector());

		//Settings
		mods.add(new Colors());
		mods.add(new Targets());
		mods.add(new ClickGui());
		mods.add(new TargetHUD());
		mods.add(new Optimization());
	}

}
