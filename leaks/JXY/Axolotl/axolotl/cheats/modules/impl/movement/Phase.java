package axolotl.cheats.modules.impl.movement;

import axolotl.cheats.events.EventType;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.settings.ModeSetting;
import axolotl.cheats.settings.NumberSetting;

import axolotl.Axolotl;
import axolotl.cheats.events.Event;
import axolotl.cheats.modules.Module;

public class Phase extends Module {

	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla");
	public NumberSetting amount = new NumberSetting("Amount", -3, -5, 5, 0.5f);

	public Phase() {
		super("Phase", Category.MOVEMENT, false);
		this.addSettings(mode, amount);
		this.setSpecialSetting(mode);
	}

	public int value0 = 0;

	public void onDisable() {
		value0 = 0;
	}

	
	public void onEvent(Event e) {
		if(!(e instanceof EventUpdate) && e.eventType == EventType.PRE)return;
		switch(mode.getMode()) {
		
			case "Vanilla":
				Axolotl.INSTANCE.sendMessage("Phased!");

				phase();
				
				this.toggle();
				
				break;
		
			default:
				break;
				
		}

	}

	public void phase() {
		vClip(amount.getNumberValue());
	}
	
}
