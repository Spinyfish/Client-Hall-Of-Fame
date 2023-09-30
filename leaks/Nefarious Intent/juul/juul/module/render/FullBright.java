package juul.module.render;

import juul.Juul;
import juul.event.Event;
import juul.event.EventUpdate;
import juul.inject.wrappers.MinecraftWrapper;
import juul.module.Module;

public class FullBright extends Module {

	public FullBright() {
		super("FullBright", "see stuff", 0, Category.RENDER);
	}

	protected float flPreGamma;

	@Override
	public void onEnable()
	{
		flPreGamma = mc.getGameSettings().getGammaSetting();
		mc.getGameSettings().setGammaSetting(100);
	}

	@Override
	public void onDisable()
	{
		mc.getGameSettings().setGammaSetting(flPreGamma);
	}

	
}
