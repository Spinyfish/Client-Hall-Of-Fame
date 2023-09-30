package de.fanta.module.impl.combat;

import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventPacket;
import de.fanta.events.listeners.EventReceivedPacket;
import de.fanta.events.listeners.PlayerMoveEvent;
import de.fanta.module.Module;
import de.fanta.module.impl.movement.Speed;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.utils.PlayerUtil;
import de.fanta.utils.TimeUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.MathHelper;

public class Velocity extends Module {

	TimeUtil time = new TimeUtil();
	TimeUtil time2 = new TimeUtil();
	private boolean flag;

	public Velocity() {
		super("Velocity", 0, Type.Combat, Color.orange);

		this.settings
				.add(new Setting("Modes", new DropdownBox("GommeQSG", new String[] { "Null", "OldGomme", "GommeQSG",
						"GommeBW|SW", "GommeNull", "OldAAC", "Legit", "AAC 3.3.13", "Reverse++", "AntiGamingChair" })));

	}

	public void onEnable() {

		flag = false;
	}

	public void onDisable() {
		flag = false;
	}

	@Override
	public void onEvent(Event event) {
		double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
		switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {
		case "Null":
			// try {

			if (event instanceof EventReceivedPacket) {
				Packet p = EventReceivedPacket.INSTANCE.getPacket();
				if (p instanceof S12PacketEntityVelocity) {
					S12PacketEntityVelocity packet = (S12PacketEntityVelocity) p;
					if (packet.getEntityID() == mc.thePlayer.getEntityId())
						EventReceivedPacket.INSTANCE.setCancelled(true);
				}
				if (p instanceof net.minecraft.network.play.server.S27PacketExplosion)
					EventReceivedPacket.INSTANCE.setCancelled(true);
			}

			break;
		// if (mc.thePlayer.hurtTime != 0) {
		case "Legit":
			if (mc.thePlayer.hurtTime != 0 && mc.thePlayer.isCollidedHorizontally) {
				setSpeed(0.1);
			}
//				if (mc.thePlayer.hurtTime == 10 && mc.thePlayer.onGround) {
//					mc.gameSettings.keyBindJump.pressed = true;
//				} else {
//					mc.gameSettings.keyBindJump.pressed = false;
//				}
			break;

		case "OldGomme":
			if (mc.thePlayer.hurtTime != 0) {
				mc.thePlayer.motionX = 0F;
				mc.thePlayer.motionZ = 0F;
				if(mc.thePlayer.onGround) {
					//mc.thePlayer.motionY = 0.42F;
				}
			//	mc.thePlayer.motionY = 0.42F;
			} else {
				if (mc.thePlayer.hurtTime != 0) {
					// setSpeed(0);
				}
			}

			break;
		case "AntiGamingChair":
			if (mc.thePlayer.hurtTime != 0) {
				setSpeed(0.1);
				if (mc.thePlayer.onGround) {
					mc.thePlayer.motionY = 0.42F;
					mc.thePlayer.motionX *= -1F;
					mc.thePlayer.motionZ *= -1F;
				}
			}

			break;
		case "AAC 3.3.13":
			if (mc.thePlayer.hurtTime != 0) {
				setSpeed(Speed.getSpeed());
//			if(Client.INSTANCE.moduleManager.getModule("Speed").isState()) {
//				if (mc.thePlayer.hurtTime != 0) {
//					Speed.setSpeed(Speed.getSpeed());
//				}
//			if (event instanceof EventReceivedPacket) {
//				Packet p = EventReceivedPacket.INSTANCE.getPacket();
//				//if (p instanceof S12PacketEntityVelocity) {
//				//	S12PacketEntityVelocity packet = (S12PacketEntityVelocity) p;
//					//if (packet.getEntityID() == mc.thePlayer.getEntityId())
//					//	EventReceivedPacket.INSTANCE.setCancelled(true);
//				//}
//				if (p instanceof net.minecraft.network.play.server.S27PacketExplosion)
//					EventReceivedPacket.INSTANCE.setCancelled(true);
//
//			}
//			}else {
//				if (mc.thePlayer.hurtTime != 0) {
//					Speed.setSpeed(Speed.getSpeed());
//				}
			}

			break;
		case "Reverse++":
			if (mc.thePlayer.hurtTime != 0) {
				setSpeed(0.35);
				if (mc.thePlayer.onGround) {
					mc.thePlayer.motionY = 0.22F;
				}
			}
			break;
		case "GommeQSG":
			if (!mc.thePlayer.isPotionActive(1)) {
				if (mc.thePlayer.onGround) {
					mc.gameSettings.keyBindSneak.pressed = false;
					if (mc.thePlayer.ticksExisted % 25 == 0) {
						mc.thePlayer.motionX *= 0.56;
						mc.thePlayer.motionZ *= 0.56;
						if (mc.thePlayer.ticksExisted % 6 == 0) {
							mc.thePlayer.jump();
						}
					}

				}
			}

			break;
		case "GommeBW|SW":
			float xZ = (float) ((float) -Math.sin(yaw) * 0.00031);
			float zZ = (float) ((float) Math.cos(yaw) * 0.00031);
			if (!mc.thePlayer.isPotionActive(1)) {
				if (mc.thePlayer.onGround) {
					mc.gameSettings.keyBindSneak.pressed = false;
					if (mc.thePlayer.ticksExisted % 6 == 0) {
						mc.gameSettings.keyBindJump.isPressed();
					}
				} else {
					if (time.hasReached(65)) {
						mc.gameSettings.keyBindSneak.pressed = true;
						if (mc.thePlayer.ticksExisted % 4 == 0) {
							mc.thePlayer.motionX = -Math.sin(yaw) * -0.05;
							mc.thePlayer.motionZ = Math.cos(yaw) * -0.05;
						}
						time.reset();
					}

				}

			}

			break;
		case "OldAAC":
			if (!mc.thePlayer.movementInput.jump) {
				if (mc.thePlayer.hurtTime < 4 && mc.thePlayer.fallDistance < 1.5) {
					if (time2.hasReached(Killaura.randomNumber(260, 170))) {
						mc.thePlayer.motionY -= 0.01;
						time2.reset();

					}
				}
			}
			break;

		}

		//
		switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {
		case "OldGomme":
			if (event instanceof PlayerMoveEvent) {
				if (mc.thePlayer.hurtTime != 0) {
					if (mc.thePlayer.onGround) {
						setSpeed1(PlayerMoveEvent.INSTANCE, 0);
						// mc.thePlayer.jump();
					}

				}
			}
		}
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
	/*
	 * if (event instanceof EventPacket && event.isPre()) { EventPacket eventPacket
	 * = (EventPacket) event; S12PacketEntityVelocity velocity =
	 * (S12PacketEntityVelocity) eventPacket.getPacket(); if (((EventPacket)
	 * event).getPacket() instanceof S12PacketEntityVelocity && !flag) {
	 * velocity.setMotionX(0); velocity.setMotionZ(0); } if (((EventPacket)
	 * event).getPacket() instanceof S08PacketPlayerPosLook) { flag = true; } if
	 * (time.hasReached(1000L) && flag) { flag = false; time.reset(); } }
	 * 
	 */

}
