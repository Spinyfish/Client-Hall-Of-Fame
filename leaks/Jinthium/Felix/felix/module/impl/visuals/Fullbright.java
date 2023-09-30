package felix.module.impl.visuals;

import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.value.impl.EnumValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Fullbright extends Module {
	
	private EnumValue<Mode> mode = new EnumValue<>("Fullbright Mode", Mode.Gamma);
	
	public Fullbright() {
		super("Fullbright", 0, ModuleCategory.VISUALS);
        setHidden(true);
        addValues(mode);
	}
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
		switch (mode.getValue()) {
			case Gamma: {
				mc.gameSettings.gammaSetting = 1f;
				break;
			}
			case Potion: {
				if (mc.thePlayer.isPotionActive(Potion.nightVision) ) {
					mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
				}
				break;
			}
		}
	}
	
	@Handler
	public void onMotionUpdate(final EventMotionUpdate event) {
		switch (mode.getValue()) {
			case Gamma: {
				if (mc.gameSettings.gammaSetting == 1f || mc.gameSettings.gammaSetting < 1f) {
					mc.gameSettings.gammaSetting = 100f;
				}
				if (mc.thePlayer.isPotionActive(Potion.nightVision)) {
					mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
				}
				break;
			}
			case Potion: {
				if (mc.gameSettings.gammaSetting > 1f) {
					mc.gameSettings.gammaSetting = 1f;
				}
		        mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 5200, 1));
				break;
			}
		}
	}
	
	private enum Mode {
		Gamma, Potion;
	}
}
