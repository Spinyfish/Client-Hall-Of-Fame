package felix.module.impl.world;

import net.minecraft.util.*;
import felix.api.annotations.Handler;
import felix.events.player.EventBoundingBox;
import felix.events.player.EventMotionUpdate;
import felix.events.player.EventMove;
import felix.events.render.EventBlockRender;
import felix.module.Module;
import felix.util.other.PlayerUtil;
import felix.value.impl.EnumValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.material.*;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Phase extends Module {
	
	private EnumValue<Mode> phaseMode = new EnumValue<>("Phase Mode", Mode.NCP);
	
	private double distance;
	
	public Phase() {
		super("Phase", 0, ModuleCategory.WORLD);
	}
	
	public void onEnable() {
		super.onEnable();
		distance = 1.2;
	}
	
	public void onDisable() {
		super.onDisable();
	}
	
	@Handler
	public void onMove(final EventMove event) {
		switch (phaseMode.getValue()) {
		case NCP:
			if (mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
				if (mc.timer.timerSpeed == 0.2F) {
					final float var2 = getDirection();
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + (mc.thePlayer.motionX * 0.3925), mc.thePlayer.posY, mc.thePlayer.posZ + (mc.thePlayer.motionZ * 0.3925), mc.thePlayer.onGround));
					mc.thePlayer.setPosition(mc.thePlayer.posX + 0.8420 * Math.cos(Math.toRadians(var2 + 90.0f)), mc.thePlayer.posY, mc.thePlayer.posZ + 0.8420 * Math.sin(Math.toRadians(var2 + 90.0f)));
					for (int i = 0; i < 2; i++) {
	                	if (PlayerUtil.isInsideBlock()) {
	                		mc.timer.timerSpeed = 1F;
	                		mc.thePlayer.setPosition(mc.thePlayer.posX + 0.3520 * Math.cos(Math.toRadians(var2 + 90.0f)), mc.thePlayer.posY, mc.thePlayer.posZ + 0.3520 * Math.sin(Math.toRadians(var2 + 90.0f)));
	                	}
	                }
					event.setY(0);
				} else {
					mc.thePlayer.moveForward *= 0.2F;
					mc.thePlayer.moveStrafing *= 0.2F;
					mc.timer.timerSpeed = 0.2F;
					mc.thePlayer.cameraPitch += 18;
					mc.thePlayer.cameraYaw += 1;
				}
			} else {
				mc.timer.timerSpeed = 1F;
			}
			break;
		case Aris:
			break;
		}
	}
	
	@Handler
	public void onMotionUpdate(final EventMotionUpdate event) {
		switch (phaseMode.getValue()) {
		case Aris:
	        if (event.getType().equals(EventMotionUpdate.Type.PRE) && PlayerUtil.isInsideBlock() && mc.thePlayer.isSneaking()) {
	            final float yaw = event.getYaw();
	            mc.thePlayer.boundingBox.offsetAndUpdate(distance * Math.cos(Math.toRadians(yaw + 90.0f)), 0.0, distance * Math.sin(Math.toRadians(yaw + 90.0f)));
	        }
			break;
		case NCP:
			break;
		}
	}
	
	@Handler
	public void onBoundingBox(final EventBoundingBox event) {
		switch (phaseMode.getValue()) {
		case Aris:
	        break;
		case NCP:
			break;
		}
	}
    
	private float getDirection() {
        float direction = mc.thePlayer.rotationYaw;
        boolean back =mc.gameSettings.keyBindBack.isKeyDown() && !mc.gameSettings.keyBindForward.isKeyDown();
        boolean forward =!mc.gameSettings.keyBindBack.isKeyDown() && mc.gameSettings.keyBindForward.isKeyDown();
        if (mc.gameSettings.keyBindLeft.isKeyDown()) {
        	direction -= (back ? 135 : (forward ? 45 : 90));
        } if (mc.gameSettings.keyBindRight.isKeyDown()) {
        	direction += (back ? 135 : (forward ? 45 : 90));
        }
        if (back && direction == mc.thePlayer.rotationYaw) {
        	direction += 180F;
        }
        return direction;
    }

	private enum Mode {
    	NCP, Aris;
    }
}
