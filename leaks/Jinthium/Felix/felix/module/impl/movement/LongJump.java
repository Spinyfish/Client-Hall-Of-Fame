package felix.module.impl.movement;

import java.util.LinkedList;

import felix.api.annotations.Handler;
import felix.events.player.EventMove;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.util.player.MovementUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class LongJump extends Module {

    public int stage, groundTicks;
    public double lastDistance;
    public double movementSpeed;

    public LongJump() {
        super("LongJump", 0, ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
        lastDistance = movementSpeed = 0.0D;
        stage = groundTicks = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.timer.timerSpeed = 1.0f;
    }

    @Handler
    public void onMotionUpdate(final EventMotionUpdate event) {
        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDistance = Math.sqrt(xDist * xDist + zDist * zDist);

        if (stage < 2) {
            event.setPosY(mc.thePlayer.posY);
        }
        if (stage > 3) {
        	groundTicks = 0;
        	mc.thePlayer.motionY = 0.0;
        	mc.timer.timerSpeed = 1.0f;
        }
    }

    @Handler
    public void onMove(final EventMove event) {
        if (stage == 1) {
            mc.thePlayer.cameraYaw = 0.03f;
            movementSpeed = 0;
            mc.timer.timerSpeed = 0.402371238f;
            
        } else if (stage == 2) {
            event.setY(mc.thePlayer.motionY = MovementUtils.getJumpBoostModifier(mc.thePlayer.getJumpUpwardsMotion()));
            
            movementSpeed = 1.9 * MovementUtils.getSpeed();
        } else if (stage == 3) {
            movementSpeed = 3.188 * MovementUtils.getSpeed();
        } else if (stage == 4) {
            movementSpeed *= 1.22;
        } else {
            if (stage < 15) {
                if (mc.thePlayer.motionY < 0) {
                    event.setY(mc.thePlayer.motionY *= .7225f);
                }
                movementSpeed = lastDistance - lastDistance / 159;
            } else {
            	movementSpeed = lastDistance - lastDistance / 159;
            }
        }
        MovementUtils.setSpeed(event, Math.max(movementSpeed, MovementUtils.getSpeed()));
        stage++;
    }
}
