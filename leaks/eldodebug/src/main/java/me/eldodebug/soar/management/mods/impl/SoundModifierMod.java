package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventPlaySound;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;

public class SoundModifierMod extends Mod {

	public SoundModifierMod() {
		super("Sound Modifier", "Modify sound volume", ModCategory.OTHER);
	}

	@Override
	public void setup() {
		this.addSliderSetting("Note", this, 100, 0, 100, true);
		this.addSliderSetting("Mobs", this, 100, 0, 100, true);
		this.addSliderSetting("Portal", this, 100, 0, 100, true);
		this.addSliderSetting("Records", this, 100, 0, 100, true);
		this.addSliderSetting("Step", this, 100, 0, 100, true);
		this.addSliderSetting("TNT", this, 100, 0, 100, true);
	}
	
	@EventTarget
	public void onPlaySound(EventPlaySound event) {
		
		float noteVolume = Soar.instance.settingsManager.getSettingByName(this, "Note").getValFloat();
		float mobsVolume = Soar.instance.settingsManager.getSettingByName(this, "Mobs").getValFloat();
		float recordsVolume = Soar.instance.settingsManager.getSettingByName(this, "Records").getValFloat();
		float portalVolume = Soar.instance.settingsManager.getSettingByName(this, "Portal").getValFloat();
		float stepVolume = Soar.instance.settingsManager.getSettingByName(this, "Step").getValFloat();
		float tntVolume = Soar.instance.settingsManager.getSettingByName(this, "TNT").getValFloat();
		
		if(event.getSoundName().startsWith("note")) {
			event.setVolume(noteVolume / 100F);
		}
		
		if(event.getSoundName().equals("game.tnt.primed") || event.getSoundName().equals("random.explode") || event.getSoundName().equals("creeper.primed")) {
			event.setVolume(tntVolume / 100F);
		}
		
		if(event.getSoundName().contains("mob")) {
			event.setVolume(mobsVolume / 100F);
		}
		
		if(event.getSoundName().startsWith("records")) {
			event.setVolume(recordsVolume / 100F);
		}
		
		if(event.getSoundName().startsWith("step")) {
			event.setVolume(stepVolume / 100F);
		}
		
		if(event.getSoundName().startsWith("portal")) {
			event.setVolume(portalVolume / 100F);
		}
	}
}
