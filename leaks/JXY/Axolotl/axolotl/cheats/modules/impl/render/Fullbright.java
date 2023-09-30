package axolotl.cheats.modules.impl.render;

import axolotl.cheats.events.EventType;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.modules.Module;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Fullbright extends Module {

	public Fullbright() {
		super("Fullbright", Category.RENDER, true);
	}
	
	private PotionEffect night = new PotionEffect(Potion.nightVision.id, 999, 1);
	
	public void onDisable() {
		mc.thePlayer.activePotionsMap.remove(Potion.nightVision.id, night);
	}
	
	@SuppressWarnings("unchecked")
	public void onEvent(Event e) {
		
		if(!(e instanceof EventUpdate) || e.eventType != EventType.PRE)return;
		
		night = new PotionEffect(Potion.nightVision.id, 999, 1);
		mc.thePlayer.activePotionsMap.put(Potion.nightVision.id, night);
	}
	
}
