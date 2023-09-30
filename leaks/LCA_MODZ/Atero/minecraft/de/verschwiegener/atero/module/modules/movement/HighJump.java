package de.verschwiegener.atero.module.modules.movement;

import net.minecraft.client.Minecraft;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import de.verschwiegener.atero.util.Util;
import god.buddy.aot.BCompiler;

public class HighJump extends Module {
	TimeUtils timeUtils;
	private double posY;
	boolean Dmg = false;
	boolean Jump = false;
	private float speed;

	public HighJump() {
		super("HighJump", "HighJump", Keyboard.KEY_NONE, Category.Movement);
	}

	public void onEnable() {
		// posY = mc.thePlayer.posY;


		super.onEnable();
	}

	public void onDisable() {
		Minecraft.thePlayer.speedInAir = 0.02F;
		Minecraft.getMinecraft().timer.timerSpeed = 1F;
		Dmg = false;
		Jump = false;
		super.onDisable();
	}

	@BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
	public void onUpdate() {

		if (this.isEnabled()) {
			super.onUpdate();
			// mc.thePlayer.posY = mc.thePlayer.prevPosY;

			if (this.isEnabled()) {
				super.onUpdate();
				// mc.thePlayer.posY = mc.thePlayer.prevPosY;

			}

			// }
			// mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
			// mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction());
			// }

			// mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
			// mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction());
			// }
		}
	}
}
