package de.verschwiegener.atero.module.modules.movement;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.events.callables.EventReceivedPacket;
import com.darkmagician6.eventapi.events.callables.EventSendPacket;
import com.darkmagician6.eventapi.events.callables.PlayerMoveEvent;
import de.verschwiegener.atero.module.modules.world.Scaffold;
import de.verschwiegener.atero.util.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.module.modules.combat.Killaura;
import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.settings.SettingsItem;
import de.verschwiegener.atero.util.Util;
import god.buddy.aot.BCompiler;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Speed extends Module {

	public static Setting setting;
	private int stage = 1;

	public Speed() {
		super("Speed", "Speed", Keyboard.KEY_NONE, Category.Movement);
	}

	public void onEnable() {

		super.onEnable();
		setting = Management.instance.settingsmgr.getSettingByName(getName());
	}

	public void onDisable() {
		try{
		if(Scaffold.setting.getItemByName("SameY").isState()){
		//	Management.instance.modulemgr.getModuleByName("Scaffold").setEnabled(false);
		}
		}catch (NullPointerException e) {
		}
		Minecraft.getMinecraft().gameSettings.keyBindJump.pressed = false;
		Minecraft.getMinecraft().timer.timerSpeed = 1F;

		super.onDisable();
	}

	public static double boostCC = 0;


	@Override
	public void setup() {
		super.setup();
		final ArrayList<SettingsItem> items = new ArrayList<>();
		final ArrayList<String> modes = new ArrayList<>();
		modes.add("Watchdog");
		modes.add("Vanilla");
		modes.add("Vanilla2");
		modes.add("VerusYPort");
		modes.add("Mineplex");
		//TODO Child Mode setting fixen im Speed
		//So das wenn man Cubecraft auswählt, der CCBoost slider kommt
		items.add(new SettingsItem("DamageBoost", true, ""));
		items.add(new SettingsItem("Modes", modes, "Watchdog", "CCBoost", "Watchdog"));
		items.add(new SettingsItem("WatchdogBoost", 1.0F, 1.4F, 1.0F, ""));
		items.add(new SettingsItem("High", 0.12F, 0.50F, 0.42F, ""));
		Management.instance.settingsmgr.addSetting(new Setting(this, items));
	}

	@BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
	public void onUpdate() {
		if (this.isEnabled()) {
			super.onUpdate();
			String mode = setting.getItemByName("Modes").getCurrent();
			setExtraTag(mode);
			switch (mode) {
				case "HypixelOnGround":
					hypixelOnGround();
					break;

				case "Watchdog":
					mc.thePlayer.setSprinting(true);
					boostCC = setting.getItemByName("WatchdogBoost").getCurrentValue();
					boolean boost = (Math.abs(mc.thePlayer.rotationYawHead - mc.thePlayer.rotationYaw) < 90.0F);
					if ((mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed || mc.gameSettings.keyBindBack.pressed) && mc.thePlayer.onGround) {
						mc.gameSettings.keyBindJump.pressed = true;
						//mc.timer.timerSpeed = (float) 1.1;
					} else {
						mc.gameSettings.keyBindJump.pressed = false;
						//	mc.timer.timerSpeed = (float) 1.1;
						double currentSpeed = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
						double direction = getDirection();
						final float Hypixel = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.999, 1);
						mc.thePlayer.motionX = -Math.sin(direction) * 1 * currentSpeed;
						mc.thePlayer.motionZ = Math.cos(direction) * 1 * currentSpeed;
					}
					break;
				case "Cubecraft1vs1":
					boolean boost2 = (Math.abs(mc.thePlayer.rotationYawHead - mc.thePlayer.rotationYaw) < 90.0F);
					if (mc.thePlayer.onGround) {
						mc.timer.timerSpeed = 2F;
						//	Minecraft.getMinecraft().gameSettings.keyBindJump.pressed = true;
					} else {
						mc.timer.timerSpeed = 1F;
						double currentSpeed = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
						double speed = boost2 ? 1 : 1D;
						double direction = getDirection();
						mc.thePlayer.motionX = -Math.sin(direction) * speed * currentSpeed;
						mc.thePlayer.motionZ = Math.cos(direction) * speed * currentSpeed;

						//if(Management.instance.modulemgr.getModuleByName("TargetStrafe").)

					}

					break;
				case "Vanilla":
					mc.thePlayer.setSprinting(true);
					//	mc.timer.timerSpeed =2.5F;
					if(mc.thePlayer.onGround) {
						mc.thePlayer.motionY = 0.42F;
					}
							Util.setSpeed(0.3);


					break;
				case "VerusYPort":
					//	if (mc.thePlayer.ticksExisted % 2 == 1) {
					//	mc.thePlayer.motionY = 0.069F;
					//		}
					mc.timer.timerSpeed = 0.8F;
				//	mc.gameSettings.keyBindJump.pressed = true;
					Util.setSpeed(0.6);
					if (mc.thePlayer.onGround) {
						mc.thePlayer.motionY = 0.11F;

					} else {
						mc.thePlayer.motionY = -0.1F;
					}


					//Minecraft.thePlayer.sendQueue.addToSendQueue((Packet) new C03PacketPlayer(true));

					//HighJump.setSpeed(1);

					//mc.timer.timerSpeed = 1.2F;

					break;
				case "Hypixel":
					if (Minecraft.thePlayer.onGround) {
						Minecraft.getMinecraft().gameSettings.keyBindJump.pressed = false;
						// Minecraft.thePlayer.jump();
						float TIMMER1 = (float) MathHelper.getRandomDoubleInRange(new Random(), 4.0, 1.0);
						if (Minecraft.getMinecraft().thePlayer.onGround) {

							if (Minecraft.thePlayer.ticksExisted % 5 == 0) {
								Minecraft.getMinecraft().timer.timerSpeed = 20;
							} else {
								//	Minecraft.getMinecraft().timer.timerSpeed = 1;
							}
							if (Minecraft.thePlayer.ticksExisted % 3 == 0) {
								Minecraft.getMinecraft().timer.timerSpeed = 0.2F;
							}


						}
					}


					break;


				case "Vanilla2":
					if(mc.gameSettings.keyBindSprint.pressed) {
					float Y = (float) MathHelper.getRandomDoubleInRange(new Random(), -0.0, -0.1);
					float Y2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.040, 0.040);
					float Y3 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.038, 0.032);
					float slowdown1 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.007, 0.006);
					double speed = 0;
					mc.timer.timerSpeed = 1f;
					stage++;
					if (mc.thePlayer.isCollidedHorizontally) {
						stage = 50;
					}
					if (mc.thePlayer.onGround && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
						mc.gameSettings.keyBindJump.pressed = true;
						stage = 0;
						speed = 0;
					}

					if (!mc.thePlayer.onGround) {
						if (mc.thePlayer.motionY > Y) {
							mc.thePlayer.motionY += 0.002;
						} else {
							mc.thePlayer.motionY += 0.003;
						}
						double slowdown = slowdown1;
						speed = 0.8 - (stage * slowdown);
						if (speed < 0) speed = 0;
					}

						Util.setSpeed(1.2);
					}else{
						if(mc.thePlayer.onGround){
							if(mc.thePlayer.ticksExisted % 3 == 0) {
								mc.thePlayer.motionY = 0.25F;
							}else{
								mc.thePlayer.motionY = 0.26F;
							}
						}
						Util.setSpeed(0.55);
					}


					break;
			}
		}

	}


	private void hypixelOnGround() {
		boolean boost = (Math.abs(mc.thePlayer.rotationYawHead - mc.thePlayer.rotationYaw) < 90.0F);
		if (Minecraft.getMinecraft().gameSettings.keyBindForward.pressed
				|| Minecraft.getMinecraft().gameSettings.keyBindLeft.pressed
				|| Minecraft.getMinecraft().gameSettings.keyBindRight.pressed
				|| Minecraft.getMinecraft().gameSettings.keyBindBack.pressed) {
			Minecraft.getMinecraft().gameSettings.keyBindSprint.pressed = true;

			if (Minecraft.thePlayer.onGround) {
				Minecraft.getMinecraft().gameSettings.keyBindJump.pressed = false;
				// Minecraft.thePlayer.jump();
				float TIMMER1 = (float) MathHelper.getRandomDoubleInRange(new Random(), 4.0, 1.0);
				if (Minecraft.getMinecraft().thePlayer.onGround) {

					if (Minecraft.thePlayer.ticksExisted % 5 == 0) {
						Minecraft.getMinecraft().timer.timerSpeed = 15;
					}
					if (Minecraft.thePlayer.ticksExisted % 8 == 0) {
						Minecraft.getMinecraft().timer.timerSpeed = 1;
					}
					if (Killaura.instance.getTarget() != null) {

						if (mc.thePlayer.onGround) {
							Minecraft.getMinecraft().gameSettings.keyBindJump.pressed = true;
							mc.timer.timerSpeed = 1F;
						}
					}
				}
			}
		}

	}

	public static void setSpeed(PlayerMoveEvent moveEvent, double moveSpeed, float rotationYaw, double speed, float yaw) {
		Minecraft.thePlayer.motionX = -Math.sin(Math.toRadians(getDirection(yaw))) * speed;
		Minecraft.thePlayer.motionZ = Math.cos(Math.toRadians(getDirection(yaw))) * speed;
	}

	public static float getDirection(float rotationYaw) {
		float left = Minecraft.getMinecraft().gameSettings.keyBindLeft.pressed ? Minecraft.getMinecraft().gameSettings.keyBindBack.pressed ? 45 : Minecraft.getMinecraft().gameSettings.keyBindForward.pressed ? -45 : -90 : 0;
		float right = Minecraft.getMinecraft().gameSettings.keyBindRight.pressed ? Minecraft.getMinecraft().gameSettings.keyBindBack.pressed ? -45 : Minecraft.getMinecraft().gameSettings.keyBindForward.pressed ? 45 : 90 : 0;
		float back = Minecraft.getMinecraft().gameSettings.keyBindBack.pressed ? +180 : 0;
		float yaw = left + right + back;
		return rotationYaw + yaw;
	}

	public static float getDirection() {
		Minecraft mc = Minecraft.getMinecraft();
		float var1 = mc.thePlayer.rotationYaw;

		if (mc.thePlayer.moveForward < 0.0F) {
			var1 += 180.0F;
		}

		float forward = 1.0F;

		if (mc.thePlayer.moveForward < 0.0F) {
			final float strafe = (float) MathHelper.getRandomDoubleInRange(new Random(), -0.50, -0.55);
			forward = -strafe;
		} else if (mc.thePlayer.moveForward > 0.0F) {
			final float strafe2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.50, 0.55);
			forward = strafe2;
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

	@EventTarget
	private void onUpdate(PlayerMoveEvent emP) {

		String mode = setting.getItemByName("Modes").getCurrent();
		switch (mode) {
			case "Mineplex":
				float Y = (float) MathHelper.getRandomDoubleInRange(new Random(), -0.0, -0.0);
				float Y2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.040, 0.040);
				float slowdown1 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.007, 0.007);
				mc.thePlayer.setSprinting(true);
				if (mc.gameSettings.keyBindForward.pressed) {

					double speed = 0;
					mc.timer.timerSpeed = 1f;
					stage++;
					if (mc.thePlayer.isCollidedHorizontally) {
						stage = 50;
					}
					if (mc.thePlayer.onGround && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
						mc.thePlayer.jump();
						emP.setY(mc.thePlayer.motionY = 0.50);
						stage = 0;
						speed = 0;
					}

					if (!mc.thePlayer.onGround) {
						if (mc.thePlayer.motionY > Y) {
							mc.thePlayer.motionY += Y2;
						} else {
							mc.thePlayer.motionY += 0.01;
						}
						double slowdown = slowdown1;
						speed = 0.8 - (stage * slowdown);
						if (speed < 0) speed = 0;
					}
					Util.setSpeed1(emP, speed);
				}
				break;

		}
	}

	@EventTarget
	public void onUpdate(EventReceivedPacket ppe) {
		Packet p = ppe.getPacket();
		//mc.getNetHandler().addToSendQueue(new C13PacketPlayerAbilities());
	//	if (p instanceof S08PacketPlayerPosLook) {

				//	Management.instance.modulemgr.getModuleByName("Speed").setEnabled(false);
					mc.timer.timerSpeed = 1F;
					if (p instanceof C13PacketPlayerAbilities && !mc.thePlayer.isUsingItem()) {
						ppe.setCancelled(true);


		//	}
		}

	}

	}


