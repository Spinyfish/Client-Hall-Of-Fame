package felix.module.impl.player;

import java.awt.Color;

import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Zoot extends Module {

	public Zoot() {
		super("Zoot", 0, ModuleCategory.PLAYER);
	}

	@Handler
	public void onMotionUpdate(final EventMotionUpdate event) {
		for (Potion potion : Potion.potionTypes) {
			PotionEffect effect;
			if (event.isPre() && potion != null && ((effect = mc.thePlayer.getActivePotionEffect(potion)) != null && potion.isBadEffect()
					|| mc.thePlayer.isBurning() && !mc.thePlayer.isInWater() && mc.thePlayer.onGround)) {
				for (int i = 0; mc.thePlayer.isBurning() ? i < 20 : i < effect.getDuration() / 20; i++) {
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
				}
			}
		}
	}
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
	}
}
