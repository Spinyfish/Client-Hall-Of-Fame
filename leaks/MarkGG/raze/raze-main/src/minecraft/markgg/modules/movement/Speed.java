package markgg.modules.movement;

import org.lwjgl.input.Keyboard;

import markgg.Client;
import markgg.events.Event;
import markgg.events.listeners.EventMotion;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;
import markgg.settings.BooleanSetting;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import markgg.util.MoveUtil;
import markgg.util.Timer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.potion.Potion;

public class Speed extends Module{

	public ModeSetting speedMode = new ModeSetting("Mode", this, "AAC4", "Motion", "AAC4", "Lowhop", "Lowhop2", "NCP", "NCP2", "NCP Low", "NCP Legit", "NCP Latest", "Bhop", "Bhop2", "Vulcan", "Vulcan2", "Spartan", "YPort", "Matrix", "Verus", "Strafe", "BlocksMC", "On Ground", "On Ground2", "Pika Low", "Pika Hop", "Spoof Hop", "Hypixel", "Blockdrop");
	public NumberSetting moveSpeed = new NumberSetting("Speed", this, 0.4, 0.1, 5, 0.1);
	public BooleanSetting dmgBoost = new BooleanSetting("Damage Boost", this, false);
	public BooleanSetting timer1 = new BooleanSetting("Timer", this, false);;
	public BooleanSetting debugMode = new BooleanSetting("Debug Mode", this, false);
	private Timer timer = new Timer();
	private int stage = 0;

	public Speed() {
		super("Speed", "Makes you faster", 0, Category.MOVEMENT);
		addSettings(speedMode, moveSpeed, dmgBoost, timer1, debugMode);
	}

	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			if(e.isPre()) {
				switch (speedMode.getMode()) {
				case "Motion":
					if(mc.thePlayer.hurtTime > 1 && dmgBoost.isEnabled())
						MoveUtil.setSpeed(MoveUtil.getSpeed() + 0.04F); 
					MoveUtil.setSpeed(moveSpeed.getValue());
					break;
				case "AAC4":
					mc.timer.timerSpeed = 1.02F;
					if(mc.thePlayer.hurtTime > 1 && dmgBoost.isEnabled())
						MoveUtil.setSpeed(MoveUtil.getSpeed() + 0.04F); 
					if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
						mc.thePlayer.jump();
						mc.thePlayer.motionZ *= 1.05D;
						mc.thePlayer.motionX *= 1.05D;
						mc.thePlayer.motionY = MoveUtil.getJumpMotion(0.4F);
					}
					break;
				case "Lowhop":
					if(mc.thePlayer.hurtTime > 1 && dmgBoost.isEnabled())
						MoveUtil.setSpeed(MoveUtil.getSpeed() + 0.04F); 
					if (!mc.thePlayer.isUsingItem())
						mc.timer.timerSpeed = 1.3F; 
					if (mc.thePlayer.onGround && MoveUtil.isMoving() && !mc.thePlayer.isCollidedHorizontally)
						mc.thePlayer.motionY = 0.11999999731779099D; 
					if ((mc.thePlayer.isCollidedHorizontally || mc.gameSettings.keyBindJump.isPressed()) && mc.thePlayer.onGround && MoveUtil.isMoving())
						mc.thePlayer.jump(); 
					MoveUtil.setSpeed(moveSpeed.getValue());
					break;
				case "Lowhop2":
					int ticks = 0;
					if (MoveUtil.isMoving()) {
						mc.thePlayer.setSprinting(false);
						if (mc.thePlayer.onGround) {
							mc.thePlayer.motionY = 0.20F;
							if (mc.thePlayer.isPotionActive(Potion.moveSpeed.id)) {
								MoveUtil.setSpeed((0.37));
							} else {
								MoveUtil.setSpeed((0.29));
							}
							ticks = 0;
						} else {
							if (mc.thePlayer.isPotionActive(Potion.moveSpeed.id)) {
								if (ticks == 0) MoveUtil.setSpeed((0.46));
								ticks++;
							} else {
								if (ticks == 0) MoveUtil.setSpeed((0.24));
								ticks++;
							}
						}
					}
					break;
				case "NCP":
					if(mc.thePlayer.hurtTime > 1 && dmgBoost.isEnabled())
						MoveUtil.setSpeed(MoveUtil.getSpeed() + 0.05F); 
					if (MoveUtil.isMoving()) {
						if (mc.thePlayer.onGround) {
							mc.thePlayer.jump();
							MoveUtil.strafe((float) (MoveUtil.getSpeed() * moveSpeed.getValue() + Math.random() / 100.0D));
						} else {
							MoveUtil.strafe((float) (MoveUtil.getSpeed() * 1.0035D));
						} 
						mc.thePlayer.jumpMovementFactor = 0.02725F;
					}
					break;
				case "NCP2":
					if(MoveUtil.isMoving()) {
						MoveUtil.strafe(0.23F);
						if(mc.thePlayer.onGround) {
							mc.thePlayer.motionY = 0.4F;
						} else {
							stage++;
							if(timer1.isEnabled()) {
								if(stage > 9) {
									mc.timer.timerSpeed = 1.22F;
									MoveUtil.strafe(0.32F);
									if(stage > 12) {
										mc.timer.timerSpeed = 0.99F;
										stage = 0;
									}
								}
							}
						}
					}
					break;
				case "NCP Low":
					if(MoveUtil.isMoving()) {
						if(mc.thePlayer.fallDistance > 0.1 && mc.thePlayer.fallDistance < 0.3) {
							mc.thePlayer.motionX *= 1.015F;
							mc.thePlayer.motionY *= 0.99F;
							mc.thePlayer.motionZ *= 1.015F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 2");
						}
						if(mc.thePlayer.fallDistance > 0.6 && mc.thePlayer.fallDistance < 0.9) {
							mc.thePlayer.motionX *= 1.01F;
							mc.thePlayer.motionY *= 0.99F;
							mc.thePlayer.motionZ *= 1.01F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 3");
						}
						if(mc.thePlayer.fallDistance > 1 && mc.thePlayer.fallDistance < 1.3) {
							mc.thePlayer.motionX *= 1.013F;
							mc.thePlayer.motionY *= 0.8F;
							mc.thePlayer.motionZ *= 1.013F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 4");
						}
						MoveUtil.strafe(0.259F);
						if(mc.thePlayer.onGround) {
							mc.thePlayer.motionX *= 1.01F;
							mc.thePlayer.motionY *= 0.99F;
							mc.thePlayer.motionZ *= 1.01F;
							mc.thePlayer.motionY = 0.4F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 1");
						} else {
							stage++;
							if(stage > 9) {
								mc.timer.timerSpeed = 1.43F;
								if(stage > 12) {
									mc.timer.timerSpeed = 0.903F;
									stage = 0;
								}
							}
						}
					}
					break;
				case "NCP Legit":
					if(MoveUtil.isMoving()) {
						if(mc.thePlayer.onGround)
							mc.thePlayer.jump();
						if(mc.thePlayer.fallDistance > 0.45 || mc.thePlayer.onGround)
							MoveUtil.strafe(0.25F);
						if(mc.thePlayer.fallDistance > MoveUtil.jumpHeight() - 0.02) {
							mc.thePlayer.motionX *= 1.012F;
							mc.thePlayer.motionZ *= 1.012F;
						}
						if(mc.thePlayer.onGround) {
							mc.thePlayer.motionX *= 1.05F;
							mc.thePlayer.motionZ *= 1.05F;
						}
					}
					break;
				case "NCP Latest":
					if(debugMode.isEnabled())
						Client.addChatMessage(MoveUtil.getSpeed() + "");
					if(mc.thePlayer.fallDistance > 0.9)
						mc.timer.timerSpeed = 1.13F;
					else if(!mc.thePlayer.onGround)
						mc.timer.timerSpeed = 1F;
					if(MoveUtil.isMoving()) {
						float aaa = 1;
						if(MoveUtil.getSpeed() < 0.247)
							aaa = 1.014F;
						MoveUtil.strafe(MoveUtil.getSpeed() * aaa);
						mc.gameSettings.keyBindJump.pressed = true;
					} else {
						mc.gameSettings.keyBindJump.pressed = false;
						mc.thePlayer.motionX = 0;
						mc.thePlayer.motionZ = 0;
					}
					break;
				case "Bhop":
					if(mc.thePlayer.hurtTime > 1 && dmgBoost.isEnabled())
						MoveUtil.setSpeed(MoveUtil.getSpeed() + 0.05F); 
					if(mc.thePlayer.onGround) {
						mc.thePlayer.jump();
						MoveUtil.setSpeed(0.2D);
					}
					break;
				case "Bhop2":
					if(mc.thePlayer.hurtTime > 1 && dmgBoost.isEnabled())
						MoveUtil.setSpeed(MoveUtil.getSpeed() + 0.05F); 
					if(mc.thePlayer.onGround)
						mc.thePlayer.jump();
				case "Vulcan":
					if(e.isPre()){
						if(mc.thePlayer.hurtTime > 1 && dmgBoost.isEnabled())
							MoveUtil.setSpeed(MoveUtil.getSpeed() + 0.05F); 
						mc.thePlayer.jumpMovementFactor = 0.0244f;
					}
					mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);

					if (MoveUtil.getSpeed() < 0.215f && !mc.thePlayer.onGround)
						MoveUtil.strafe(0.215f);
					if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
						mc.gameSettings.keyBindJump.pressed = false;
						mc.thePlayer.jump();

						if (!mc.thePlayer.isAirBorne) return;

						if(MoveUtil.getSpeed() < 0.5f)
							MoveUtil.strafe(0.4849f);
					}else if (!MoveUtil.isMoving()) {
						mc.timer.timerSpeed = 1.00f;
						mc.thePlayer.motionX = 0.0;
						mc.thePlayer.motionZ = 0.0;
					}
					break;
				case "Vulcan2":
					if(!timer1.isEnabled())
						mc.timer.timerSpeed = 1.00f;
					if(mc.thePlayer.hurtTime > 1 && MoveUtil.getSpeed() < 0.47 && dmgBoost.isEnabled()) {
						mc.thePlayer.motionX *= 1.0015F;
						mc.thePlayer.motionZ *= 1.0015F;
					}
					if(MoveUtil.getSpeed() < 0.29) {
						mc.thePlayer.motionX *= 1.0005F;
						mc.thePlayer.motionZ *= 1.0005F;
					}
					if(timer.hasTimeElapsed(1000, false) && timer1.isEnabled())
						mc.timer.timerSpeed = 1.21F;
					if(timer.hasTimeElapsed(2000, true) && timer1.isEnabled()) {
						mc.thePlayer.motionX *= 1.008F;
						mc.thePlayer.motionZ *= 1.008F;
						mc.timer.timerSpeed = 0.843F;
					}
					mc.thePlayer.jumpMovementFactor = 0.0244F;
					mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
					if (MoveUtil.getSpeed() < 0.215f && !mc.thePlayer.onGround)
						MoveUtil.strafe(0.215f);
					if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
						mc.gameSettings.keyBindJump.pressed = false;
						mc.thePlayer.jump();

						if (!mc.thePlayer.isAirBorne) return;

						if(MoveUtil.getSpeed() < 0.5f)
							MoveUtil.strafe(0.4849f);
					}else if (!MoveUtil.isMoving()) {
						mc.timer.timerSpeed = 1.00f;
						mc.thePlayer.motionX = 0.0;
						mc.thePlayer.motionZ = 0.0;
					}
					break;
				case "Spartan":
					if(mc.thePlayer.hurtTime > 1 && dmgBoost.isEnabled())
						MoveUtil.setSpeed(MoveUtil.getSpeed() + 0.05F); 
					mc.timer.timerSpeed = 1.05999999999F;
					if(mc.thePlayer.onGround)
						mc.thePlayer.jump();
					MoveUtil.strafe(0.23F);
					if(!MoveUtil.isMoving())
						MoveUtil.stop();
					break;
				case "YPort":
					MoveUtil.strafe(0.23F);
					mc.gameSettings.keyBindJump.pressed = false;
					if(mc.thePlayer.onGround) {
						mc.thePlayer.motionX *= 1.03F;
						mc.thePlayer.motionZ *= 1.03F;
						mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY + 0.7, mc.thePlayer.posZ);
					} else {
						if(mc.thePlayer.fallDistance < 0.3) {
							mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ);
						}
					}
					break;
				case "Matrix":
					if(!MoveUtil.isMoving()) {
						mc.timer.timerSpeed = 1F;
					}
					if(MoveUtil.isMoving()) {
						if(timer.hasTimeElapsed(1000, false) && timer1.isEnabled()) {
							mc.timer.timerSpeed = 1.38F;
						}
						if(timer.hasTimeElapsed(2000, true) && timer1.isEnabled()) {
							mc.timer.timerSpeed = 0.75F;
						}
						mc.gameSettings.keyBindJump.pressed = true;
						if(mc.thePlayer.fallDistance > 0.5) {
							MoveUtil.strafe(0.2F);
						}
						if(mc.thePlayer.onGround) {
							mc.thePlayer.motionX *= 1.01F;
							mc.thePlayer.motionZ *= 1.01F;
						}
					}
					break;
				case "Verus":
					if(mc.thePlayer.onGround) {
						mc.gameSettings.keyBindJump.pressed = true;
					}
					int hopStage = 1;
					boolean aa = false;	
					if(timer.hasTimeElapsed(500, false) && !aa) {
						hopStage = 1;
						aa = true;
					}
					if(timer.hasTimeElapsed(1000, true)) {
						hopStage = 2;
						aa = false;
					}
					if(hopStage == 1) {
						if(mc.thePlayer.fallDistance > 0.2) {
							mc.thePlayer.motionX *= 1.01F;
							mc.thePlayer.motionZ *= 1.01F;
							MoveUtil.strafe(0.16F);
						} else {
							MoveUtil.strafe(0.32F);
						}
					}
					break;
				case "Strafe":
					MoveUtil.strafe((float) Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ));
					if(mc.thePlayer.onGround && mc.gameSettings.keyBindJump.pressed == false) {
						mc.thePlayer.jump();
						if(mc.gameSettings.keyBindSprint.pressed == false)
							mc.thePlayer.setSprinting(true);
					}
					if(mc.thePlayer.hurtTime > 1 && dmgBoost.isEnabled()) {
						MoveUtil.setSpeed(MoveUtil.getSpeed() + 0.018F); 
					}
					break;
				case "BlocksMC":
					if(MoveUtil.isMoving()) {
						if(mc.thePlayer.fallDistance > 0.1 && mc.thePlayer.fallDistance < 0.3) {
							mc.thePlayer.motionX *= 1.015F;
							mc.thePlayer.motionY *= 1.015F;
							mc.thePlayer.motionZ *= 1.015F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 2");
						}
						if(mc.thePlayer.fallDistance > 0.6 && mc.thePlayer.fallDistance < 0.9) {
							mc.thePlayer.motionX *= 1.01F;
							mc.thePlayer.motionY *= 1.005F;
							mc.thePlayer.motionZ *= 1.01F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 3");
						}
						if(mc.thePlayer.fallDistance > 1 && mc.thePlayer.fallDistance < 1.3) {
							mc.thePlayer.motionX *= 1.013F;
							mc.thePlayer.motionY *= 1.2F;
							mc.thePlayer.motionZ *= 1.013F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 4");
						}
						MoveUtil.strafe(0.2659F);
						if(mc.thePlayer.onGround) {
							mc.thePlayer.motionX *= 1.01F;
							mc.thePlayer.motionY *= 1.01F;
							mc.thePlayer.motionZ *= 1.01F;
							mc.thePlayer.motionY = 0.424999;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 1");
						} else {
							stage++;
							if(stage > 9) {
								mc.timer.timerSpeed = 1.43F;
								if(stage > 12) {
									mc.timer.timerSpeed = 0.903F;
									stage = 0;
								}
							}
						}
					}
					break;

				case "On Ground":
					if(MoveUtil.isMoving()) {
						if(mc.thePlayer.fallDistance > 0.1 && mc.thePlayer.fallDistance < 0.3) {
							mc.thePlayer.motionX *= 1.015F;
							mc.thePlayer.motionZ *= 1.015F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 2");
						}
						if(mc.thePlayer.fallDistance > 0.6 && mc.thePlayer.fallDistance < 0.9) {
							mc.thePlayer.motionX *= 1.01F;
							mc.thePlayer.motionZ *= 1.01F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 3");
						}
						if(mc.thePlayer.fallDistance > 1 && mc.thePlayer.fallDistance < 1.3) {
							mc.thePlayer.motionX *= 1.013F;
							mc.thePlayer.motionZ *= 1.013F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 4");
						}
						MoveUtil.strafe(0.24F);
						if(mc.thePlayer.onGround) {
							mc.thePlayer.motionX *= 1.01F;
							mc.thePlayer.motionZ *= 1.01F;
							mc.thePlayer.motionY = 0.0000000000000001F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 1");
						} else {
							stage++;
							if(stage > 9) {
								if(stage > 11) {
									stage = 0;
								}
							}
						}
					}
					break;
				case "On Ground2":
					if(mc.thePlayer.onGround)
						MoveUtil.setSpeed(0.5F);
					break;
				case "Pika Low":
					if(MoveUtil.isMoving()) {
						if(mc.thePlayer.fallDistance > 0.1 && mc.thePlayer.fallDistance < 0.3) {
							mc.thePlayer.motionX *= 1.015F;
							mc.thePlayer.motionZ *= 1.015F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 2");
						}
						if(mc.thePlayer.fallDistance > 0.6 && mc.thePlayer.fallDistance < 0.9) {
							mc.thePlayer.motionX *= 1.01F;
							mc.thePlayer.motionZ *= 1.01F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 3");
						}
						if(mc.thePlayer.fallDistance > 1 && mc.thePlayer.fallDistance < 1.3) {
							mc.thePlayer.motionX *= 1.013F;
							mc.thePlayer.motionZ *= 1.013F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 4");
						}
						MoveUtil.strafe(0.24F);
						if(mc.thePlayer.onGround) {
							mc.thePlayer.motionX *= 1.01F;
							mc.thePlayer.motionZ *= 1.01F;
							mc.thePlayer.motionY = 0.36F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 1");
						} else {
							stage++;
							if(stage > 9) {
								if(stage > 11) {
									stage = 0;
								}
							}
						}
					}
					break;
				case "Pika Hop":
					double x1 = mc.thePlayer.posX, y1 = mc.thePlayer.posY, z1 = mc.thePlayer.posZ;
					mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(x1, y1, z1, true));
					int jumps = 0;

					if(MoveUtil.isMoving()) {
						MoveUtil.strafe(0.24F);
						if(mc.thePlayer.onGround) {
							jumps++;
							mc.thePlayer.motionX *= 1.01F;
							mc.thePlayer.motionZ *= 1.01F;
							mc.thePlayer.motionY = 0.42F;
							if(debugMode.isEnabled())
								Client.addChatMessage("Worked! 1, Jumped " + jumps);
						} else {
							stage++;
							if(stage > 9)
								if(stage > 11)
									stage = 0;
						}

					}
					break;
				case "Spoof Hop":
					double x2 = mc.thePlayer.posX, y2 = mc.thePlayer.posY, z2 = mc.thePlayer.posZ;
					mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(x2, y2, z2, true));
					mc.gameSettings.keyBindJump.pressed = true;
					MoveUtil.setSpeed(MoveUtil.getBaseMoveSpeed());
					break;
				case "Hypixel":
					if(timer.hasTimeElapsed(500, false))
						mc.timer.timerSpeed = 1.3F;
					if(timer.hasTimeElapsed(600, true))
						mc.timer.timerSpeed = 1F;
					mc.gameSettings.keyBindJump.pressed = true;
					if(mc.thePlayer.onGround) {
						mc.thePlayer.motionX *= 1.008F;
						mc.thePlayer.motionZ *= 1.008F;
					}
					break;
				case "Blockdrop":
					if(timer.hasTimeElapsed(1000, false))
						mc.timer.timerSpeed = 1.08F;

					if(timer.hasTimeElapsed(1200, true))
						mc.timer.timerSpeed = 1F;

					if(MoveUtil.isMoving()) {
						MoveUtil.strafe(MoveUtil.getSpeed());
						mc.gameSettings.keyBindJump.pressed = true;
					} else {
						mc.gameSettings.keyBindJump.pressed = false;
						mc.thePlayer.motionX = 0;
						mc.thePlayer.motionZ = 0;
					}	
					break;
				}
			}
		}
	}


	public void onEnable() {
		NotificationManager.show(new Notification(NotificationType.ENABLE, getName() + " was enabled!", 1));
		switch (speedMode.getMode()) {
		case "Lowhop":
			Client.addChatMessage("Recommended speed for lowhop is 0.2!");
			break;
		case "Matrix":
			if(timer1.isEnabled())
				Client.addChatMessage("Using timer is faster, but flags after ~30 seconds!");
			break;
		case "Vulcan2":
			if(timer1.isEnabled())
				Client.addChatMessage("Using timer is faster, but flags after ~30 seconds!");
			break;
		case "NCP2":
			stage = 0;
			Client.addChatMessage("Do not bridge while using this mode!");
			break;
		}
	}

	public void onDisable() {
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		mc.timer.timerSpeed = 1.0F;
		MoveUtil.setSpeed(MoveUtil.getBaseMoveSpeed());
		mc.thePlayer.speedInAir = 0.02F;
		mc.thePlayer.jumpMovementFactor = 0.02F;
		Blocks.ice.slipperiness = 0.89F;
		Blocks.packed_ice.slipperiness = 0.89F;
		mc.gameSettings.keyBindJump.pressed = false;
	}

}
