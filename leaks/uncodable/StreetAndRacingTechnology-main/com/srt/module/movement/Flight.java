package com.srt.module.movement;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.srt.events.Event;
import com.srt.events.listeners.EventBoundingBox;
import com.srt.events.listeners.EventMotion;
import com.srt.events.listeners.EventMove;
import com.srt.events.listeners.EventPacket;
import com.srt.events.listeners.EventStep;
import com.srt.module.ModuleBase;
import com.srt.module.combat.Velocity;
import com.srt.settings.settings.ModeSetting;
import com.srt.settings.settings.NumberSetting;
import com.thunderware.utils.MovementUtils;
import com.thunderware.utils.TimerUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class Flight extends ModuleBase {

	public ModeSetting mode;
	public NumberSetting speed = new NumberSetting("Speed", 2.5, 0.05, 0.15, 9.9);
	
	public Flight() {
		super("Flight", Keyboard.KEY_F, Category.MOVEMENT);
		ArrayList<String> modes = new ArrayList<>();
		modes.add("Smooth");
		modes.add("BlocksMC");
		modes.add("Vanilla");
		modes.add("MatrixRod");
		modes.add("Ghostly");
		modes.add("Hypixel");
		modes.add("Test");
		modes.add("AGC");
		mode = new ModeSetting("Mode", modes);
		addSettings(mode, speed);
	}
	public static boolean hurt = false;
	public static int ticks = 0;
	public static double y = 0;
	public static TimerUtils timer = new TimerUtils();
	public ArrayList<Packet> ghostlyTrans = new ArrayList<Packet>();
	
	/**
	 *
	 */
	public void onEvent(Event e) {
		if(e instanceof EventBoundingBox) {
			EventBoundingBox event = (EventBoundingBox)e;
			switch(mode.getCurrentValue()) {
				case "Test":
					//event.setBB(AxisAlignedBB.fromBounds(mc.thePlayer.posX,y-0.05,mc.thePlayer.posZ,mc.thePlayer.posX,y,mc.thePlayer.posZ));
					break;
				
			}
		}
		if(e instanceof EventMove) {
			EventMove event = (EventMove)e;
			switch(mode.getCurrentValue()) {
				case "Test":
					
					break;
				case "Hypixel":
					if(ticks != 2){
		                event.setX(0);
		                event.setZ(0);
		            }else if(ticks == 2 && MovementUtils.isMoveKeysDown()){
		                double[] smart = MovementUtils.getMotion((float) MovementUtils.getNCPBaseSpeed());
		                //mc.timer.timerSpeed = 1.0f;
		                if(!timer.hasReached(250)) {
		                    for (int i = 1; i < 4; i++) {
		                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + (smart[0] * i), mc.thePlayer.posY, mc.thePlayer.posZ + (smart[1] * i), mc.thePlayer.onGround));
		                    }
		                    event.setX(smart[0] * 4);
		                    event.setZ(smart[1] * 4);
		                }else if(!timer.hasReached(250+334)){
		                    for (int i = 1; i < 3; i++) {
		                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + (smart[0] * i), mc.thePlayer.posY, mc.thePlayer.posZ + (smart[1] * i), mc.thePlayer.onGround));
		                    }
		                    event.setX(smart[0] * 3);
		                    event.setZ(smart[1] * 3);
		                }else if(!timer.hasReached(250+334+500)) {
		                    for (int i = 1; i < 2; i++) {
		                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + (smart[0] * i), mc.thePlayer.posY, mc.thePlayer.posZ + (smart[1] * i), mc.thePlayer.onGround));
		                    }
		                    event.setX(smart[0] * 2);
		                    event.setZ(smart[1] * 2);
		                }else{
		                    event.setX(smart[0] * 1);
		                    event.setZ(smart[1] * 1);
		                }
		            }
					break;
			}
		}
		if(e instanceof EventMotion) {
			EventMotion event = (EventMotion)e;
			setSuffix(mode.getCurrentValue());
			switch(mode.getCurrentValue()) {
				case "Test":
					mc.timer.timerSpeed = 0.9f;
					if(mc.thePlayer.ticksExisted % 4 == 0) {
						double[] boost = MovementUtils.getMotion(0.221f);
						for(int i = 2; i > 0; i--) {
							mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX+(boost[0]/i), mc.thePlayer.posY, mc.thePlayer.posZ+(boost[1]/i), mc.thePlayer.onGround));
							mc.thePlayer.setPosition(mc.thePlayer.posX+(boost[0]/i), mc.thePlayer.posY, mc.thePlayer.posZ+(boost[1]/i));
						}
					}
					break;
				case "Smooth":
					if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
						mc.thePlayer.setSpeed(0.5f);
					}else {
						mc.thePlayer.motionX /= 2;
						mc.thePlayer.motionZ /= 2;
					}
					if(mc.gameSettings.keyBindJump.isKeyDown())
						mc.thePlayer.motionY = 0.4;
					else if(mc.gameSettings.keyBindSneak.isKeyDown()) {
						mc.thePlayer.motionY = -0.4;
					} else
						mc.thePlayer.motionY = 0;
					
					break;
					
				case "Vanilla":
					if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
						mc.thePlayer.setSpeed(speed.getValue());
						mc.thePlayer.cameraYaw = 0.1f/1.02f;
					}else {
						mc.thePlayer.motionX = 0;
						mc.thePlayer.motionZ = 0;
						mc.thePlayer.cameraYaw = 0.0f;
					}
					if(mc.gameSettings.keyBindJump.isKeyDown())
						mc.thePlayer.motionY = speed.getValue();
					else if(mc.gameSettings.keyBindSneak.isKeyDown()) {
						mc.thePlayer.motionY = -speed.getValue();
					} else
						mc.thePlayer.motionY = 0;
					
					break;
					
				case "BlocksMC":
					if(mc.thePlayer.hurtTime != 0) {
						hurt = true;
					}
					if(hurt) {
						mc.thePlayer.motionY = 0.0;
						mc.thePlayer.setSpeed(3.0f);
						mc.timer.timerSpeed = 0.7f;
						((EventMotion) e).setOnGround(true);
					}
					break;
				case "MatrixRod":
					if(ticks == 0) {
						mc.thePlayer.setSpeed(-0.1275f);
					}
					if(ticks != 0) {
						ticks++;
					}
					if(mc.thePlayer.hurtTime == 9) {
						ticks = 1;
					}
					if(ticks <= 50 && ticks != 0) {
						mc.thePlayer.setSpeed(0.975f);
						if(ticks <= 20) {
							mc.thePlayer.jump();
							mc.thePlayer.setSpeed(1.47325f);
						}
						if(ticks >= 30) {
							mc.thePlayer.setSpeed(0.415f);
						}
						mc.thePlayer.motionY = 0.0;
					}
					if(ticks <= 50 && ticks != 0) {
						mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
					}
					((EventMotion) e).setPitch(-89);
					break;
					
				case "AGC":
					if(mc.thePlayer.onGround) {
						mc.thePlayer.jump();
						mc.thePlayer.setSpeed(0.1);
					}else {
						if(mc.thePlayer.hurtTime != 0) {
							mc.thePlayer.setSpeed(9.0);
							mc.timer.timerSpeed = 0.1f;
							mc.thePlayer.motionY = -0.01 - Math.random()*0.025;
							hurt = true;
						}else if(hurt == true) {
							this.toggle();
						}
					}
					break;
					
				case "Hypixel":
					if(e.isPre()) {
						if(ticks == 1){
			                mc.thePlayer.posY = mc.thePlayer.lastTickPosY;
			                mc.timer.timerSpeed = 1.4f;
			            }
			            if(ticks == 0 && mc.thePlayer.onGround){
			                mc.thePlayer.jump();
			                ticks = 1;
			            }
			            if(ticks == 1 && mc.thePlayer.onGround){
			                ((EventMotion) e).y -= 0.0784;
			                ((EventMotion) e).onGround = true;
			            }
			            if(ticks == 2){
			                mc.timer.timerSpeed = 1.075f;
			                mc.thePlayer.cameraYaw = 0.1f;
			                mc.thePlayer.motionY = 0.0;
			            }
					}
					break;
					
				case "Ghostly":
					mc.thePlayer.setSpeed(speed.getValue());
					if(mc.thePlayer.motionY <= 0) {
						event.setOnGround(true);
						mc.thePlayer.motionY = 0.0;
						mc.thePlayer.cameraYaw = 0.1f/1.02f;
						mc.thePlayer.onGround = true;
					}
					if(mc.thePlayer.ticksExisted % 45 == 0) {
						for(Packet p : ghostlyTrans) {
							mc.getNetHandler().addToSendQueueNoEvent(p);
						}
						ghostlyTrans.clear();
						C13PacketPlayerAbilities packet = new C13PacketPlayerAbilities();
						packet.setAllowFlying(true);
						packet.setFlying(true);
						mc.getNetHandler().addToSendQueueNoEvent(packet);
					}
					break;
			}
		}
		
		////////////////////////////////////////////////////////
		
		if(e instanceof EventPacket) {
			if(mc.thePlayer == null)
				return;
			EventPacket event = (EventPacket)e;
			switch(mode.getCurrentValue()) {
			
				case "BlocksMC":
					
                    if (mc.thePlayer.ticksExisted % 3 != 0 && event.getPacket() instanceof C03PacketPlayer && hurt) {
                       event.setCancelled(true);
                    }
					
					break;
					
				case "Hypixel":
					if(event.getPacket() instanceof S08PacketPlayerPosLook && ticks == 1){
		                mc.thePlayer.performHurtAnimation();
		                ticks = 2;
		                timer.reset();
		            }
					break;
					
				case "Test":
					break;
					
				case "Ghostly":
					if(event.getPacket() instanceof C00PacketKeepAlive || event.getPacket() instanceof C0FPacketConfirmTransaction) {
						e.setCancelled(true);
						ghostlyTrans.add(event.getPacket());
					}
					break;
			}
		}
	}
	
	public void onEnable() {
		ticks = 0;
		timer.reset();
		switch(mode.getCurrentValue()) {
			case "BlocksMC":
				double[] jumpYPositions = {0.41999998688698, 0.7531999805212, 1.00133597911214, 1.16610926093821, 1.24918707874468, 1.24918707874468, 1.1707870772188, 1.0155550727022, 0.78502770378924, 0.4807108763317, 0.10408037809304, 0.0};
				
				if(mc.thePlayer.onGround) {
					for(int i = 0; i < 4; i++) {
						mc.timer.timerSpeed = 0.0875f;
						for(double y : jumpYPositions) {
							mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY+y, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
						}
					}
					mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,false));
				}
				
				break;
			case "Vanilla":
				hurt = true;
				break;
			case "MatrixRod":
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw,-89f,mc.thePlayer.onGround));
				mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
				break;
			case "AGC":
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY-3.0001,mc.thePlayer.posZ,false));
				break;
			case "Test":
				y = 0.48f;
				break;
		}
	}
	
	public void onDisable() {
		mc.timer.timerSpeed = 1.0f;
		hurt = false;
		//if(mode.getCurrentValue() != "Test") {
			mc.thePlayer.motionX = 0;
			mc.thePlayer.motionZ = 0;	
		//}
	}

}
