package haptic.module.impl.ghost;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventMotionUpdate;
import haptic.event.impl.EventUpdate;
import haptic.module.Category;
import haptic.module.ListeningType;
import haptic.module.Module;
import haptic.util.network.PacketUtils;
import haptic.util.world.BlockUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class AutoBridge extends Module {
	
	private boolean finishedRotatingYaw, finishedRotatingPitch;
	private boolean spoofingSneak;
	
	private float startingYaw, spoofedYaw, spoofedPitch;
	
	public AutoBridge() {
		super("AutoBridge", Category.GHOST, ListeningType.ALWAYS);
	}
	
	public void onEnable() {
		finishedRotatingYaw = finishedRotatingPitch = false;
		startingYaw = mc.thePlayer.rotationYaw;
		spoofedPitch = mc.thePlayer.rotationPitch;
		
		spoofedYaw = (float) MathHelper.wrapAngleTo180_double(startingYaw);
	}
	
	public void onDisable() {
		mc.leftClickCounter = 6;
		
		mc.thePlayer.rotationYaw = startingYaw;
		mc.thePlayer.rotationPitch = 10;
		
		if(spoofingSneak) {
			PacketUtils.stopSneaking();
			spoofingSneak = false;
		}
		
		mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
		mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
		mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
		mc.gameSettings.keyBindUseItem.pressed = false;
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			rotate(e);
		}
		
		if(this.isEnabled()) {
			if(event instanceof EventUpdate) {
				boolean foundSlot = false;
				int block = 0;
				
				for(int i = 0; i < 9; i++) {
					if (mc.thePlayer.inventory.getStackInSlot(i) == null)
						continue;
					if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !BlockUtil.blockBlacklist.contains(((ItemBlock) mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock())) {
						block = i;
						foundSlot = true;
						break;
					}
				}
				if(foundSlot) {
					mc.thePlayer.inventory.currentItem = block;
				}
				
				BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
				if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
					mc.gameSettings.keyBindSneak.pressed = false;
					mc.gameSettings.keyBindUseItem.pressed = true;
					if(!spoofingSneak) {
						PacketUtils.startSneaking();
						spoofingSneak = true;
					}
					move.motionMult(0.7);
				} else {
					if(spoofingSneak) {
						PacketUtils.stopSneaking();
						spoofingSneak = false;
					}
					mc.gameSettings.keyBindSneak.pressed = false;
					mc.gameSettings.keyBindUseItem.pressed = false;
				}

				mc.rightClickDelayTimer = 0;
				
				mc.thePlayer.rotationYaw = startingYaw - 180;
				mc.thePlayer.rotationPitch = move.isGoingDiagonally() ? 78F : 81.5F;
				mc.gameSettings.keyBindForward.pressed = false;
				mc.gameSettings.keyBindBack.pressed = true;
			}
		}
	}
	
	private void rotate(EventMotionUpdate e) {
		float yaw = this.isEnabled() ? (float) MathHelper.wrapAngleTo180_double(startingYaw - 135) : startingYaw;
		
		double turnSpeed = 30;
		
		if(this.isEnabled()) {
			if(Math.abs(yaw - spoofedYaw) < turnSpeed || (Math.abs(yaw - spoofedYaw) > 360 - turnSpeed)) {
				spoofedYaw = yaw;
			} else {
				if(Math.abs(yaw - spoofedYaw) < 180) {
					if(spoofedYaw > yaw) {
						spoofedYaw -= turnSpeed;
					} else {
						spoofedYaw += turnSpeed;
					}
				} else {
					if(spoofedYaw > yaw) {
						spoofedYaw += turnSpeed;
					} else {
						spoofedYaw -= turnSpeed;
					}
				}
			}
		} else if(!finishedRotatingYaw) {
			if(Math.abs(yaw - spoofedYaw) < turnSpeed || (Math.abs(yaw - spoofedYaw) > 360 - turnSpeed)) {
				spoofedYaw = yaw;
				finishedRotatingYaw = true;
			} else {
				if(Math.abs(yaw - spoofedYaw) < 180) {
					if(spoofedYaw > yaw) {
						spoofedYaw -= turnSpeed;
					} else {
						spoofedYaw += turnSpeed;
					}
				} else {
					if(spoofedYaw > yaw) {
						spoofedYaw += turnSpeed;
					} else {
						spoofedYaw -= turnSpeed;
					}
				}
			}
		}
		
		if(this.isEnabled()) {
			spoofedPitch += 20;
			if(spoofedPitch > (move.isGoingDiagonally() ? 78F : 81.5F)) {
				spoofedPitch = move.isGoingDiagonally() ? 78F : 81.5F;
			}
		} else if(!finishedRotatingPitch) {
			spoofedPitch -= 20;
			if(spoofedPitch > mc.thePlayer.rotationYaw) {
				spoofedPitch = mc.thePlayer.rotationYaw;
				finishedRotatingPitch = true;
			}
		}
		
		if(this.isEnabled() || !finishedRotatingYaw) {
			mc.thePlayer.rotationYawHead = spoofedYaw;
			mc.thePlayer.renderYawOffset = spoofedYaw;
			e.setYaw(spoofedYaw);
		}
	}

}