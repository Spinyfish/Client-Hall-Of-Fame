package de.fanta.module.impl.movement;

import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventTick;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;

public class Step extends Module {
	public Step() {
		super("Step", 0, Type.Movement, Color.GREEN);

		this.settings.add(new Setting("Modes", new DropdownBox("Vanilla",
				new String[] { "Vanilla", "Intave", "NCP", "AAC 3.3.13", "Legit", "Spartan" })));
		this.settings.add(new Setting("StepHight", new Slider(1, 90, 0.1, 4)));

	}

	public static double StepHight;
	private boolean notGround = false;

	public void onDisable() {
		mc.thePlayer.stepHeight = 0.5F;
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof EventTick) {
			if (mc.thePlayer.isCollidedHorizontally) {
				double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
				switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {
				case "Vanilla":
					StepHight = ((Slider) this.getSetting("StepHight").getSetting()).curValue;
					mc.thePlayer.stepHeight = (float) StepHight;
					break;
				case "Intave":
					if (mc.thePlayer.onGround) {
						mc.thePlayer.jump();
					} else {
						if (mc.thePlayer.motionY < 0.03) {
							mc.thePlayer.motionY += 0.08;
						}
					}
					if (mc.thePlayer.motionY > 0.03) {

						mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.001, mc.thePlayer.posZ);
						mc.thePlayer.onGround = true;
						mc.thePlayer.motionX = -Math.sin(yaw) * 0.14;
						mc.thePlayer.motionZ = Math.cos(yaw) * 0.14;

					}
					break;
				case "Spartan":
					mc.timer.timerSpeed = 1F;
					mc.thePlayer.stepHeight = 0.1f;

					if (!mc.thePlayer.isCollidedHorizontally) {
						mc.timer.timerSpeed = 1F;
					} else {
						mc.timer.timerSpeed = 0.2F;
					}
					if (mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround) {
						mc.thePlayer.jump();
					} else if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.onGround
							&& mc.thePlayer.ticksExisted % 2 == 1) {
						mc.thePlayer.onGround = true;
						mc.thePlayer.jump();
					}
//                    	   mc.thePlayer.stepHeight = 0.6f;
//                           if (mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround) {
//                               mc.thePlayer.jump();
//                           } else if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.onGround
//                                   && mc.thePlayer.ticksExisted % 10 == 0) {
//                               mc.thePlayer.onGround = true;
//                               mc.thePlayer.jump();
//                           }
					break;
				case "AAC 3.3.13":
					if (!Client.INSTANCE.moduleManager.getModule("Speed").isState()) {
						if (mc.thePlayer.onGround) {
							mc.thePlayer.jump();
						} else {
							if (mc.thePlayer.motionY < 0.03) {
								mc.thePlayer.motionY += 0.08;
							}
						}
						if (mc.thePlayer.motionY > 0.03) {

							mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.001, mc.thePlayer.posZ);
							mc.thePlayer.onGround = true;
							mc.thePlayer.motionX = -Math.sin(yaw) * -0.05;
							mc.thePlayer.motionZ = Math.cos(yaw) * -0.05;
						}
					}
					break;
				case "Legit":
					mc.thePlayer.jump();
					// mc.thePlayer.stepHeight = .5F;
					break;
				case "NCP":
					if (mc.thePlayer.isCollidedHorizontally && mc.thePlayer.isMoving()) {
						if (mc.thePlayer.onGround) {
							mc.thePlayer.motionY += .45;
							notGround = false;
						}
					} else {
						mc.thePlayer.stepHeight = .6F;
						if (notGround == false) {
							if (mc.thePlayer.fallDistance >= .5) {
								notGround = false;
								return;
							}
							mc.thePlayer.motionY += -10F;
							notGround = true;
						}
					}
					mc.thePlayer.stepHeight = .5F;
					Speed.setSpeed(.4);
					break;
				}
			}
		}
	}
}
