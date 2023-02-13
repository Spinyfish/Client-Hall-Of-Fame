package juul.module.world;

import juul.Juul;
import juul.event.Event;
import juul.event.EventMotion;
import juul.event.EventUpdate;
import juul.module.Module;
import juul.module.settings.NumberSetting;

public class VClip extends Module {

	public NumberSetting dist;

	private boolean bClipped;

	public VClip() {
		super("VClip", "vertical clip", 0, Category.WORLD);
		addSetting(dist = new NumberSetting("Distance", "", 5, -10, 10, 0.5));
	}

	public void onEvent(Event e) {

		if (e instanceof EventMotion && !bClipped)
		{
			// setPositionAndUpdate
			bClipped = true;
		}
		this.disable();
	}

	@Override
	public void onEnable()
	{
		bClipped = false;
	}
	
}
