package juul.module.combat;

import juul.module.settings.BooleanSetting;
import juul.util.value.Random;

import juul.event.Event;
import juul.event.EventReach;
import juul.module.Module;
import juul.module.settings.RangeSetting;

public class Reach extends Module {

	public BooleanSetting sprinting, clicking;

	public RangeSetting reach;
	
	public Reach() {
		super("Reach", "Allows you to reach further than vanilla.", 0, Category.COMBAT);
		addSetting(sprinting = new BooleanSetting("While Sprinting", "only activates while player is sprinting", false));
		addSetting(clicking = new BooleanSetting("While Clicking", "only activates while player is clicking", false));
		addSetting(reach = new RangeSetting("Distance", "Distance in blocks you can reach", 3, 3.3, 3, 6, 0.05));
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventReach) {

			if (sprinting.enabled && !thePlayerSP.isSprinting()) return;

			if (clicking.enabled && !mc.getGameSettings().keyBindAttack.getIsKeyPressed()) return;

			EventReach event = (EventReach)e;

			event.setDistance((float)Random.dbRandom(reach.getStart(), reach.getEnd()));
		}
	}
	
}
