package ooo.cpacket.ruby.module.move;

import net.minecraft.network.play.client.C03PacketPlayer;
import ooo.cpacket.ruby.api.event.EventImpl;
import ooo.cpacket.ruby.api.event.events.player.EventMotionUpdate;
import ooo.cpacket.ruby.api.event.events.player.EventStep;
import ooo.cpacket.ruby.module.Module;

public class Step extends Module {

	public Step(String name, int key, Category category) {
		super(name, key, category);
	}

	private boolean resetNextTick;
	private double preY;

	@Override
	public void onEnable() {

	}

	@Override
	public void onDisable() {
		mc.thePlayer.stepHeight = 0.5f;
	}

	@EventImpl
	public void onUpdate(EventStep event) {
		if (event.getEntity() != mc.thePlayer)
			return;
		if (event.getType() == EventStep.Type.PRE) {
			if (canStep())
				event.setStepHeight(1.5F);
		} else {
			if (event.getStepHeight() != 1.5F)
				return;
			double offset = mc.thePlayer.boundingBox.minY - preY;
			if (offset > .6D) {
				double[] offsets = { .42D, (offset < 1D && offset > .8D) ? .73D : .75D, 1D, 1.16D, 1.23D, 1.2D };
				for (int i = 0; i < (offset > 1D ? offsets.length : 2); i++)
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
							mc.thePlayer.posY + offsets[i], mc.thePlayer.posZ, false));
				mc.timer.timerSpeed = (offset > 1D) ? .15F : .37F;
				resetNextTick = true;
			}
		}
	}

	@EventImpl
	public void onUpdate(EventMotionUpdate event) {
		if (resetNextTick) {
			mc.timer.timerSpeed = 1F;
			resetNextTick = false;
		}
	}

	private boolean canStep() {
		return mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround && mc.thePlayer.motionY < 0
				&& !mc.thePlayer.movementInput.jump;
	}

}
