package com.srt.module.movement;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.srt.SRT;
import com.srt.events.Event;
import com.srt.events.listeners.EventMotion;
import com.srt.events.listeners.EventMove;
import com.srt.module.ModuleBase;
import com.srt.module.combat.Velocity;
import com.srt.settings.settings.BooleanSetting;
import com.srt.settings.settings.ModeSetting;
import com.srt.settings.settings.NumberSetting;
import com.thunderware.utils.MovementUtils;

import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;

public class Speed extends ModuleBase {

	public NumberSetting timer = new NumberSetting("Timer", 1.0, 0.05, 0.05, 10.0);
	public NumberSetting speed = new NumberSetting("Speed", 0.5, 0.05, 0.15, 5);
	public BooleanSetting hypixelLow = new BooleanSetting("HypixelLow",true);
	public ModeSetting mode;
	public static int ticks = 0;
	
	public Speed() {
		super("Speed", Keyboard.KEY_X, Category.MOVEMENT);
		ArrayList<String> modes = new ArrayList<>();
		modes.add("Vanilla");
		modes.add("BlocksMC");
		modes.add("Verus");
		modes.add("Matrix");
		modes.add("Hypixel OffGround");
		modes.add("Hypixel");
		modes.add("TimerBMC");
		modes.add("AGC");
		modes.add("Legacy Multi-AntiCheat");
		modes.add("DEV");
		modes.add("Verus Flag");
		modes.add("SRT");
		modes.add("Dragpak");
		this.mode = new ModeSetting("Mode", modes);
		addSettings(timer, speed, mode);
	}
	
	public void onEnable() {
		ticks = 0;
	}
	
	public void onDisable() {
		mc.timer.timerSpeed = 1.0f;
		mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
		mc.thePlayer.jumpMovementFactor = 0.02f;
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventMove) {
			EventMove event = (EventMove)e;
			switch(mode.getCurrentValue()) {
				case "Hypixel":
					if(mc.thePlayer.isMoveKeysDown() && mc.thePlayer.onGround && !mc.thePlayer.isCollidedHorizontally) {
			            double[] smart = MovementUtils.getMotion(MovementUtils.getSpeed());
			            for (int i = 1; i < 3; i++) {
			                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + (smart[0] * i), mc.thePlayer.posY, mc.thePlayer.posZ + (smart[1] * i), mc.thePlayer.onGround));
			            }
			            event.setX(smart[0] * 3);
			            event.setZ(smart[1] * 3);
					}
					break;
				case "AGC":
					if(mc.thePlayer.isMoveKeysDown() && mc.thePlayer.hurtTime != 0 && Velocity.hits % 3 != 0) {
						double[] gg = MovementUtils.getMotion(speed.getValue().floatValue());
						event.setX(gg[0]);
						event.setZ(gg[1]);
					}
					break;
				case "DEV":
					break;
			}
		}
		setSuffix(mode.getCurrentValue());
		if(e instanceof EventMotion) {
			switch(mode.getCurrentValue()) {
				case "Vanilla":
					if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
						mc.gameSettings.keyBindJump.pressed = false;
						if(mc.thePlayer.onGround)
							mc.gameSettings.keyBindJump.pressed = true;
						mc.thePlayer.setSpeed(speed.getValue());
					}else {
						mc.thePlayer.motionX = 0;
						mc.thePlayer.motionZ = 0;
					}
					
					break;
					
				case "DEV":
					if(!e.isPre())
						break;
					if(mc.thePlayer.onGround) {
						mc.thePlayer.motionY = 0.22;
						ticks = 0;
					}else {
						ticks++;
						if(ticks == 1) {
							mc.thePlayer.motionY = 0.02;
						}
					}
					break;
					
				case "Verus":
					if(!e.isPre())
						break;
					if (mc.thePlayer.onGround) {
	                    if (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
		                    mc.thePlayer.setSpeed(0.36F);
	                    	mc.thePlayer.jump();
							mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY+mc.thePlayer.motionY, mc.thePlayer.posZ);
							mc.thePlayer.motionY = 0.0;
	                    }
	                    mc.timer.timerSpeed = 1.0f;
	                    ticks = 0;
	                } else {
	                	ticks++;
	                    MovementUtils.setMotion();
	                }
					if(mc.thePlayer.hurtTime != 0) {
						mc.thePlayer.setSpeed(0.45f);
					}
					
					break;
					
				case "BlocksMC":
					if (mc.thePlayer.onGround) {
	                    if (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
	                        mc.thePlayer.jump();
	                    }
	                    mc.timer.timerSpeed = 1.0f;
	                    mc.thePlayer.setSpeed(0.61F);
	                } else {
	                    mc.thePlayer.setSpeed(0.45F);
	                    if(mc.thePlayer.ticksExisted % 2 != 0) {
	                    	mc.thePlayer.setSpeed(speed.getValue());
	                    }
	                }
					if(mc.thePlayer.hurtTime != 0) {
						mc.thePlayer.setSpeed(0.9f);
					}
					
					break;
				
				case "Matrix":
					if(ticks != 0) {
						ticks++;
					}
                    if(mc.thePlayer.hurtTime == 9) {
                    	ticks = 1;
                    }
					if (mc.thePlayer.onGround) {
	                    if(ticks != 0 && ticks <= 40) {
	                    	mc.thePlayer.setSpeed(0.975f);
	                    }
	                }
					break;
					
				case "AGC":
					if(!e.isPre() || !MovementUtils.isMoveKeysDown())
						return;
					if (mc.thePlayer.onGround) {
						ticks = 0;
	                    mc.thePlayer.jump();
	                }else {
	                	
	                }
                	mc.thePlayer.setSpeed(mc.thePlayer.getSpeed());
					break;
					
				case "Legacy Multi-AntiCheat":
                    if (mc.thePlayer.onGround) {
                        if (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
                            mc.thePlayer.jump();
                        }
                        mc.timer.timerSpeed = 1.07f;
                        mc.thePlayer.setSpeed(0.44f);
                        mc.thePlayer.motionZ *= 1.09424f;
                        mc.thePlayer.motionX *= 1.09424f;
                        mc.thePlayer.moveStrafing *= 1.92f;
                        
                    }else {
                    	mc.thePlayer.setSpeed(mc.thePlayer.getSpeed());
                        mc.thePlayer.jumpMovementFactor = 0.0277f;
                        if(mc.thePlayer.hurtTime != 0) {
                            mc.thePlayer.setSpeed(0.47);
                        }
                    } 
                    break;
					
				case "Hypixel OffGround":
					if(!e.isPre())
						return;
					
					AxisAlignedBB gg = mc.thePlayer.boundingBox.offset(0, -1.0, 0);
					if(mc.theWorld.isMaterialInBB(gg, Material.ice) || mc.theWorld.isMaterialInBB(gg, Material.packedIce)) {
						MovementUtils.setMotion(1.02f);
					}
					AxisAlignedBB gg2 = mc.thePlayer.boundingBox.offset(0, -0.005, 0);
					if(mc.theWorld.isMaterialInBB(gg2, Material.ice) || mc.theWorld.isMaterialInBB(gg2, Material.packedIce)) {
						mc.thePlayer.jump();
						MovementUtils.setMotion(1.9f);
					}
					break;
					
				case "Verus Flag":
					if(mc.thePlayer.onGround) {
						mc.thePlayer.setSpeed(speed.getValue());
						if(mc.thePlayer.ticksExisted % 19 == 0) {
							ticks++;
							((EventMotion) e).setY(((EventMotion) e).getY()-14.36D);
						}
					}
					break;
					
				case "TimerBMC":
					if (mc.thePlayer.onGround) {
	                    if (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
	                        mc.thePlayer.jump();
	                    }
	                    mc.timer.timerSpeed = 1.33f;
	                    mc.thePlayer.setSpeed(0.61F);
	                } else {
	                    mc.thePlayer.setSpeed(0.45F);
	                    if(mc.thePlayer.ticksExisted % 2 != 0) {
	                    	mc.thePlayer.setSpeed(speed.getValue());
	                    }
	                }
					if(mc.thePlayer.hurtTime != 0) {
						mc.thePlayer.setSpeed(0.9f);
					}
					
					break;
					
				case "Dragpak":
					if(mc.thePlayer.onGround) {
						if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
							mc.thePlayer.jump();
						}
						mc.thePlayer.setSpeed(3.5F);
					}else {
						break;
					
					}
						
				case "SRT":
					if(mc.thePlayer.onGround) {
						if (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
	                        mc.thePlayer.jump();
						}
						mc.timer.timerSpeed = 1.02F;
						mc.thePlayer.setSpeed(2.1F);
					}else {
						if(mc.thePlayer.hurtTime != 0) {
							mc.thePlayer.setSpeed(2.2F);
							
							break;
							
						}
						
					}
			}
		}
	}

}
