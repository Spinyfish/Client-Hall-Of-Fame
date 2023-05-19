package club.marsh.bloom.impl.mods.movement;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.UpdateEvent;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;


public class Teleport extends Module {
	public Teleport() {
		super("ClickTP",Keyboard.KEY_NONE,Category.MOVEMENT);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (mc.gameSettings.keyBindAttack.pressed) {
			mc.gameSettings.keyBindAttack.pressed = false;
			final MovingObjectPosition block = mc.theWorld.rayTraceBlocks(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + 1.62, mc.thePlayer.posZ), new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + 1.62, mc.thePlayer.posZ).add(new Vec3(mc.thePlayer.getLookVec().xCoord * 300, mc.thePlayer.getLookVec().yCoord * 300, mc.thePlayer.getLookVec().zCoord * 300)), false, true, false);
			if (block != null) {
				//block.getBlockPos();
				addMessage(String.format("Teleporting to: %d,%d,%d", block.getBlockPos().getX(),block.getBlockPos().getY(),block.getBlockPos().getZ()));
				double spoofedPosX,spoofedPosY,spoofedPosZ;
				spoofedPosX = mc.thePlayer.posX;
				spoofedPosY = mc.thePlayer.posY;
				spoofedPosZ = mc.thePlayer.posZ;
				int divisor = (int) (Math.round(block.getBlockPos().distanceSq(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ))+1);
				divisor *= 2;
				for (int i = divisor; i > 0; --i) {
					double xDist = spoofedPosX - block.getBlockPos().getX();
			        double yDist = spoofedPosY - block.getBlockPos().getY();
			        double zDist = spoofedPosZ - block.getBlockPos().getZ();
			        xDist /= divisor;
			        yDist /= divisor;
			        zDist /= divisor;
			        //addMessage(String.format("Distance: %f,%f,%f", xDist,yDist,zDist));
			        spoofedPosX -= xDist;
			        spoofedPosY -= yDist;
			        spoofedPosZ -= zDist;
			        mc.getNetHandler().addToSendQueueSilent(new C04PacketPlayerPosition(spoofedPosX,spoofedPosY,spoofedPosZ,false));
				}
				mc.thePlayer.setPosition(spoofedPosX, spoofedPosY, spoofedPosZ);
			}
		}
	}
}
