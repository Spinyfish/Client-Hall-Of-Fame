package de.fanta.module.impl.movement;

import java.awt.Color;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventReceivedPacket;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.PlayerMoveEvent;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.PlayerUtil;
import de.fanta.utils.TimeUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;

public class Longjump extends Module {
	public Longjump() {
		super("Longjump", Keyboard.KEY_NONE, Type.Movement, Color.orange);
		this.settings.add(new Setting("Boost", new CheckBox(false)));
		this.settings.add(new Setting("LongjumpMode",
				new DropdownBox("Verus", new String[] { "Verus", "Watchdog", "WatchdogFloat", "Cubecraft" })));

	}

	public static int disable = 0;
	boolean cubedmg = false;

	public void onEnable() {
		cubedmg = false;
		switch (((DropdownBox) this.getSetting("LongjumpMode").getSetting()).curOption) {
		case "Verus":

			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, true));
			// mc.timer.timerSpeed = 0.1F;
			break;

		case "Cubecraft":
			for (int i = 0; i < 51; i++) {
				mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + 0.06, mc.thePlayer.posZ, false));
				mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY, mc.thePlayer.posZ, false));
			}
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
			break;

		}
		if (((CheckBox) Client.INSTANCE.moduleManager.getModule("Longjump").getSetting("Boost").getSetting()).state) {
			ChatUtil.sendChatInfo("Risk to Use");
		}
		// Client.INSTANCE.moduleManager.getModule("Speed").setState(true);
		setSpeed(0);
		disable = 0;
		super.onEnable();
	}

	public void onDisable() {
		// Client.INSTANCE.moduleManager.getModule("Speed").setState(true);
		disable = 0;
		mc.timer.timerSpeed = 1F;
		cubedmg = false;
		super.onDisable();
	}

	private int stage = 1;
	TimeUtil time = new TimeUtil();

	@Override
	public void onEvent(Event event) {
		switch (((DropdownBox) this.getSetting("LongjumpMode").getSetting()).curOption) {

		case "Verus":
			if (!Client.INSTANCE.moduleManager.getModule("Speed").isState()) {
				if (mc.thePlayer.onGround) {
					mc.thePlayer.motionY = 1F;
					mc.thePlayer.onGround = true;
					// mc.timer.timerSpeed = 25F;
				} else {
					mc.timer.timerSpeed = 1F;
					this.setSpeed(4);
					if (mc.thePlayer.ticksExisted % 5 == 0) {
						// if(mc.thePlayer.fallDistance > 0 ) {

						// mc.thePlayer.motionY = 0.08F;
						// }
						// mc.thePlayer.motionY = -0.05F;
					}
				}

			}

//			if(!Client.INSTANCE.moduleManager.getModule("Speed").isState()) {
//				if (mc.thePlayer.onGround) {
//					mc.thePlayer.motionY = 0.8F;
//					mc.thePlayer.onGround = true;
//					//mc.timer.timerSpeed = 25F;
//				} else {
//					mc.timer.timerSpeed = 1F;
//					this.setSpeed(1.1);
////					if(mc.thePlayer.fallDistance > 1.6 ) {
////						mc.thePlayer.motionY = -0.05F;
////					}
//				}
//					
//				
//					
//				}
			break;

		case "Cubecraft":
			if (mc.thePlayer.hurtTime > 0) {
				cubedmg = true;
			}

			if (!cubedmg) {
				mc.timer.timerSpeed = 1F;
				setSpeed(-0.12);
				mc.gameSettings.keyBindForward.pressed = false;
				mc.gameSettings.keyBindBack.pressed = false;
				mc.gameSettings.keyBindLeft.pressed = false;
				mc.gameSettings.keyBindRight.pressed = false;
			} else {
				mc.gameSettings.keyBindForward.pressed = true;
			}
			// if (mc.thePlayer.fallDistance > 0F) {
			if (cubedmg) {

				if (mc.thePlayer.onGround) {
					mc.timer.timerSpeed = 0.2F;
					mc.gameSettings.keyBindJump.pressed = true;
					// for (int i = 0; i < 4; i++) {

					mc.thePlayer.motionY += 0.66F;
					if (mc.thePlayer.hurtTime != 0) {
						Speed.setSpeed(Speed.getSpeed() + 0.6);
					}

					// }
				} else {
					mc.timer.timerSpeed = 1F;
					Speed.setSpeed(Speed.getSpeed());
					mc.thePlayer.speedInAir = 0.09F;
					mc.gameSettings.keyBindJump.pressed = false;
				}
			}

			break;
		case "Watchdog":
			if (event instanceof EventTick) {
				// mc.getNetHandler().addToSendQueue(new
				// C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
				// mc.thePlayer.posY, mc.thePlayer.posZ, false));
				if (mc.thePlayer.onGround) {

					final NetHandlerPlayClient netHandler = mc.getMinecraft().getNetHandler();
					final EntityPlayerSP player = mc.getMinecraft().thePlayer;
					final double x1 = player.posX;
					final double y1 = player.posY;
					final double z1 = player.posZ;
					for (int i = 0; i < getMaxFallDist() / 0.05510000046342611; ++i) {
						netHandler.addToSendQueue(
								new C03PacketPlayer.C04PacketPlayerPosition(x1, y1 + 0.060100000351667404, z1, false));
						netHandler.addToSendQueue(
								new C03PacketPlayer.C04PacketPlayerPosition(x1, y1 + 5.000000237487257E-4, z1, false));
						netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x1,
								y1 + 0.004999999888241291 + 6.01000003516674E-8, z1, false));
					}
					netHandler.addToSendQueue(new C03PacketPlayer(true));
//					for (double distance = 0.06; distance < 3.075; distance += 0.06) {
//						new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY * 0.06,
//								mc.thePlayer.posZ, false);
//						distance += 0.1E-8D;
//
//						mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//								mc.thePlayer.posY + 0.1E-8D, mc.thePlayer.posZ, false));
//					}
//					mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//							mc.thePlayer.posY, mc.thePlayer.posZ, false));
//					mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
//							mc.thePlayer.posY, mc.thePlayer.posZ, true));

				}

				// mc.timer.timerSpeed = 1.1F;

				// setSpeedTest(0.3);

				mc.thePlayer.jumpMovementFactor = 0.025F;

				boolean boost = (Math.abs(mc.thePlayer.rotationYawHead - mc.thePlayer.rotationYaw) < 90.0F);
				if ((mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindLeft.pressed
						|| mc.gameSettings.keyBindRight.pressed || mc.gameSettings.keyBindBack.pressed)
						&& mc.thePlayer.onGround) {
					mc.thePlayer.motionY = 0.42F;
					mc.timer.timerSpeed = (float) 0.1;
					mc.thePlayer.setSprinting(true);
				} else {
					mc.thePlayer.setSprinting(true);
					mc.timer.timerSpeed = (float) 1;
					if (mc.thePlayer.hurtTime != 0) {
						if (((CheckBox) Client.INSTANCE.moduleManager.getModule("Longjump").getSetting("Boost")
								.getSetting()).state) {
							mc.timer.timerSpeed = (float) 4;
						} else {
							mc.timer.timerSpeed = (float) 1;
						}
						mc.thePlayer.motionY += 0.02F;

//						if (mc.thePlayer.fallDistance > 0) {
//							mc.thePlayer.onGround = true;
//							mc.gameSettings.keyBindJump.pressed = true;
//						}

						if (mc.thePlayer.fallDistance > 0.1) {
							mc.thePlayer.motionY = 0.42F;
						} else {
							// mc.thePlayer.motionY = 0F;
						}
						if (mc.thePlayer.fallDistance > 0) {
							mc.thePlayer.motionY = 0.25F;
						} else {
							// mc.thePlayer.motionY = 0F;
						}

						Speed.setSpeed5(0.45);

					}
				}
				if (mc.thePlayer.onGround)
					Longjump.disable += 1;

				if (Longjump.disable > 1) {
					Client.INSTANCE.moduleManager.getModule("Longjump").setState(false);
					mc.timer.timerSpeed = 1F;
				}
			}
			break;
		case "WatchdogFloat":
			if (event instanceof EventTick) {
				// mc.getNetHandler().addToSendQueue(new
				// C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
				// mc.thePlayer.posY, mc.thePlayer.posZ, false));
				if (mc.thePlayer.onGround) {
					final NetHandlerPlayClient netHandler = mc.getMinecraft().getNetHandler();
					final EntityPlayerSP player = mc.getMinecraft().thePlayer;
					final double x1 = player.posX;
					final double y1 = player.posY;
					final double z1 = player.posZ;
					for (int i = 0; i < getMaxFallDist() / 0.05510000046342611; ++i) {
						netHandler.addToSendQueue(
								new C03PacketPlayer.C04PacketPlayerPosition(x1, y1 + 0.060100000351667404, z1, false));
						netHandler.addToSendQueue(
								new C03PacketPlayer.C04PacketPlayerPosition(x1, y1 + 5.000000237487257E-4, z1, false));
						netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x1,
								y1 + 0.004999999888241291 + 6.01000003516674E-8, z1, false));
					}
					netHandler.addToSendQueue(new C03PacketPlayer(true));
//					final NetHandlerPlayClient netHandler = mc.getMinecraft().getNetHandler();
//					final EntityPlayerSP player = mc.getMinecraft().thePlayer;
//					final double x1 = player.posX;
//					final double y1 = player.posY;
//					final double z1 = player.posZ;
//					for (int i = 0; i < getMaxFallDist() / 0.05510000046342611 + 0.2; ++i) {
//						netHandler.addToSendQueue(
//								new C03PacketPlayer.C04PacketPlayerPosition(x1, y1 + 0.060100000351667404, z1, false));
//						netHandler.addToSendQueue(
//								new C03PacketPlayer.C04PacketPlayerPosition(x1, y1 + 5.000000237487257E-4, z1, false));
//						netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x1,
//								y1 + 0.004999999888241291 + 6.01000003516674E-8, z1, false));
//					}
//					netHandler.addToSendQueue(new C03PacketPlayer(true));

				}

				// mc.timer.timerSpeed = 1.1F;

				// setSpeedTest(0.3);

				mc.thePlayer.jumpMovementFactor = 0.025F;

				boolean boost = (Math.abs(mc.thePlayer.rotationYawHead - mc.thePlayer.rotationYaw) < 90.0F);
				if ((mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindLeft.pressed
						|| mc.gameSettings.keyBindRight.pressed || mc.gameSettings.keyBindBack.pressed)
						&& mc.thePlayer.onGround) {
					mc.thePlayer.motionY = 0.42F;
					mc.timer.timerSpeed = (float) 0.05;
					mc.thePlayer.setSprinting(true);
				} else {
					mc.thePlayer.setSprinting(true);
					mc.timer.timerSpeed = (float) 1;
					if (mc.thePlayer.hurtTime != 0) {
						if (((CheckBox) Client.INSTANCE.moduleManager.getModule("Longjump").getSetting("Boost")
								.getSetting()).state) {
							mc.timer.timerSpeed = (float) 2;
						} else {
							mc.timer.timerSpeed = (float) 1;
						}
						mc.thePlayer.motionY += 0.02F;

//						if (mc.thePlayer.fallDistance > 0) {
//							mc.thePlayer.onGround = true;
//							mc.gameSettings.keyBindJump.pressed = true;
//						}

						if (mc.thePlayer.fallDistance > 0) {
							mc.thePlayer.motionY = 0F;

						}

						Speed.setSpeed5(0.47);

					}
				}
				if (mc.thePlayer.onGround)
					Longjump.disable += 1;

				if (Longjump.disable > 1) {
					Client.INSTANCE.moduleManager.getModule("Longjump").setState(false);
					mc.timer.timerSpeed = 1F;
				}
			}
			break;
		}

		if (event instanceof EventReceivedPacket) {
			Packet p = EventReceivedPacket.INSTANCE.getPacket();
			if (p instanceof S08PacketPlayerPosLook) {
				ChatUtil.sendChatInfo("its FakeDamage");
			}
		}

	}

	public static void setSpeed1(PlayerMoveEvent moveEvent, double moveSpeed) {
		setSpeed1(moveEvent, moveSpeed, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe,
				mc.thePlayer.movementInput.moveForward);
	}

	public static void setSpeed1(PlayerMoveEvent moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe,
			double pseudoForward) {
		double forward = pseudoForward;
		double strafe = pseudoStrafe;
		float yaw = pseudoYaw;
		if (forward != 0.0D) {
			if (strafe > 0.0D) {
				yaw += ((forward > 0.0D) ? -45 : 45);
			} else if (strafe < 0.0D) {
				yaw += ((forward > 0.0D) ? 45 : -45);
			}
			strafe = 0.0D;
			if (forward > 0.0D) {
				forward = 1.0D;
			} else if (forward < 0.0D) {
				forward = -1.0D;
			}
		}
		if (strafe > 0.0D) {
			strafe = 1.0D;
		} else if (strafe < 0.0D) {
			strafe = -1.0D;
		}
		double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
		double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
		moveEvent.setX((forward * moveSpeed * mx + strafe * moveSpeed * mz));
		moveEvent.setZ((forward * moveSpeed * mz - strafe * moveSpeed * mx));
	}

	public static void setSpeed(double speed) {
		EntityPlayerSP player = mc.getMinecraft().thePlayer;
		double yaw = (double) player.rotationYaw;
		boolean isMoving = player.moveForward != 0.0F || player.moveStrafing != 0.0F;
		boolean isMovingForward = player.moveForward > 0.0F;
		boolean isMovingBackward = player.moveForward < 0.0F;
		boolean isMovingRight = player.moveStrafing > 0.0F;
		boolean isMovingLeft = player.moveStrafing < 0.0F;
		boolean isMovingSideways = isMovingLeft || isMovingRight;
		boolean isMovingStraight = isMovingForward || isMovingBackward;
		if (isMoving) {
			if (isMovingForward && !isMovingSideways) {
				yaw += 0.0D;
			} else if (isMovingBackward && !isMovingSideways) {
				yaw += 180.0D;
			} else if (isMovingForward && isMovingLeft) {
				yaw += 45.0D;
			} else if (isMovingForward) {
				yaw -= 45.0D;
			} else if (!isMovingStraight && isMovingLeft) {
				yaw += 90.0D;
			} else if (!isMovingStraight && isMovingRight) {
				yaw -= 90.0D;
			} else if (isMovingBackward && isMovingLeft) {
				yaw += 135.0D;
			} else if (isMovingBackward) {
				yaw -= 135.0D;
			}
			yaw = Math.toRadians(yaw);
			player.motionX = -Math.sin(yaw) * speed;
			player.motionZ = Math.cos(yaw) * speed;
		}
	}

	public static float getDirection() {

		float var1 = mc.thePlayer.rotationYaw;

		if (mc.thePlayer.moveForward < 0.0F) {
			var1 += 180.0F;
		}

		float forward = 1.0F;

		if (mc.thePlayer.moveForward < 0.0F) {
			forward = -0.5F;
		} else if (mc.thePlayer.moveForward > 0.0F) {
			forward = 0.5F;
		}

		if (mc.thePlayer.moveStrafing > 0.0F) {
			var1 -= 90.0F * forward;
		}

		if (mc.thePlayer.moveStrafing < 0.0F) {
			var1 += 90.0F * forward;
		}

		var1 *= 0.017453292F;
		return var1;
	}

	public static float getDirection2() {

		float var1 = mc.thePlayer.rotationYaw;

		if (mc.thePlayer.moveForward < 0.0F) {
			var1 += 180.0F;
		}

		float forward = 1.0F;

		if (mc.thePlayer.moveForward < 0.0F) {
			// forward = -0.5F;
		} else if (mc.thePlayer.moveForward > 0.0F) {
			// forward = 0.5F;
		}

		if (mc.thePlayer.moveStrafing > 0.0F) {
			// var1 -= 90.0F * forward;
		}

		if (mc.thePlayer.moveStrafing < 0.0F) {
			// var1 += 90.0F * forward;
		}

		var1 *= 0.017453292F;
		return var1;
	}

	public static float getMaxFallDist() {
		final PotionEffect potioneffect = mc.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump);
		final int f = (potioneffect != null) ? (potioneffect.getAmplifier() + 1) : 0;
		return (float) (mc.getMinecraft().thePlayer.getMaxFallHeight() + f);
	}

	/*
	 * //OLDHYPIXELPAPERCHALLANGE case "OldNCP": if (mc.thePlayer.hurtTime > 0) {
	 * verusdmg = true; }
	 * 
	 * if (!verusdmg) { mc.timer.timerSpeed = 1F; setSpeed(-0.12);
	 * mc.gameSettings.keyBindForward.pressed = false;
	 * mc.gameSettings.keyBindBack.pressed = false;
	 * mc.gameSettings.keyBindLeft.pressed = false;
	 * mc.gameSettings.keyBindRight.pressed = false; } else {
	 * mc.gameSettings.keyBindForward.pressed = true; }
	 * 
	 * 
	 * 
	 * 
	 * if (mc.thePlayer.hurtTime != 0) { setSpeed(Speed.getSpeed()+0.01);
	 * mc.thePlayer.speedInAir = 0.08F; // mc.thePlayer.motionY = -0.05F;
	 * //mc.thePlayer.speedInAir = 0.06F; mc.timer.timerSpeed = 0.4F; }else {
	 * mc.timer.timerSpeed = 1F; }
	 * 
	 * if (verusdmg) { if(mc.thePlayer.onGround) { mc.thePlayer.jump(); }
	 * mc.gameSettings.keyBindForward.pressed = true;
	 * 
	 * // if (mc.gameSettings.keyBindForward.pressed) { // // double x =
	 * mc.thePlayer.posX; // double y = mc.thePlayer.posY; // double z =
	 * mc.thePlayer.posZ; //// double speed1 = 0.8D; //// double xm = -Math.sin(yaw)
	 * * speed1; //// double zm = Math.cos(yaw) * speed1; // double Y =
	 * mc.thePlayer.motionY = 0; // mc.thePlayer.setPosition(x, y + Y, z); // // //
	 * MineplexSpeed = ((Slider) // //
	 * this.getSetting("VerusSpeed").getSetting()).curValue; // mc.timer.timerSpeed
	 * = (float) 0.4; // // // mc.thePlayer.onGround = true; // } // if
	 * (mc.thePlayer.isMoving()) { // this.setSpeed(3); // } // if
	 * (mc.gameSettings.keyBindJump.pressed) { // double x = mc.thePlayer.posX; //
	 * double y = mc.thePlayer.posY; // double z = mc.thePlayer.posZ; // double Y =
	 * mc.thePlayer.motionY = 0.2; // mc.thePlayer.setPosition(x, y + Y, z); // // }
	 * // if (mc.gameSettings.keyBindSneak.pressed) { // double x =
	 * mc.thePlayer.posX; // double y = mc.thePlayer.posY; // double z =
	 * mc.thePlayer.posZ; // double Y = mc.thePlayer.motionY = -0.2; //
	 * mc.thePlayer.setPosition(x, y + Y, z); // // }
	 * 
	 * } break;
	 */

}
